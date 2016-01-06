package com.overtake.objectlist.view;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.overtake.objectlist.AdaptableViewManager;
import com.overtake.objectlist.IAdaptableContainer;
import com.overtake.objectlist.ListAbstractCustomizer;
import com.overtake.objectlist.OnLoadMoreListener;

import cn.ikinder.androidlibs.R;

public class PullToRefreshListViewContainer extends PullToRefreshListView implements IAdaptableContainer {

    public static interface OnListScrollListener {
        public void onScrollStateChanged(AbsListView view, int scrollState);

        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);
    }

    // ===========================================================
    // Fields
    // ===========================================================
    private RelativeLayout mFooterContentView;
    private View mCustomNoMoreView;
    private View mNoMoreView;
    private ProgressBar mImageLoadingMore;
    private TextView mLoadingMoreTitle;
    private OnLoadMoreListener mOnLoadMoreListener;
    private ListView mListView;
    private View mFootView;
    private boolean mAutoLoadMore = true;
    private boolean mHasMore = true;
    private boolean mNoFooterView = true;

    protected int mOriginWidth;
    protected int mOriginHeight;
    protected boolean mScrollToBottomWhenSizeChange = false;


    public PullToRefreshListViewContainer(Context context) {
        super(context, Mode.PULL_FROM_START, AnimationStyle.ROTATE);
        doLayout();
    }

    public PullToRefreshListViewContainer(Context context, Boolean hasFooter) {
        super(context, Mode.PULL_FROM_START, AnimationStyle.ROTATE);
        mNoFooterView = hasFooter;
        doLayout();
    }

    public PullToRefreshListViewContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        doLayout();
    }

    public void setScrollToBottomWhenSizeChange(boolean scrollToBottomWhenSizeChange) {
        mScrollToBottomWhenSizeChange = scrollToBottomWhenSizeChange;
    }

    private void doLayout() {
        ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new PTROnGlobalLayoutListener());
        mListView = this.getRefreshableView();
        mListView.setSelector(R.drawable.ptr_selector_no_pressed);
        mListView.setFooterDividersEnabled(false);
        mListView.setDivider(null);
        mListView.setFadingEdgeLength(0);
        mListView.setCacheColorHint(getContext().getResources().getColor(R.color.window_background));
        setFadingEdgeLength(0);
        mListView.setBackgroundColor(getContext().getResources().getColor(R.color.window_background));
        mFootView = LayoutInflater.from(getContext()).inflate(ListAbstractCustomizer.CONTAINER_FOOTER_LAYOUT_ID, null);
        mFootView.setLayoutParams(new AbsListView.LayoutParams(-1, -2));

        mFooterContentView = (RelativeLayout) mFootView.findViewById(R.id.ptr_id_listview_footer);

        mNoMoreView = mFootView.findViewById(R.id.ptr_id_listview_footer_nomore);
        mNoMoreView.setVisibility(View.GONE);

        mImageLoadingMore = (ProgressBar) mFootView.findViewById(R.id.ptr_id_listview_footer_loading_more);
        mImageLoadingMore.setVisibility(GONE);

        mLoadingMoreTitle = (TextView) mFootView.findViewById(R.id.ptr_id_listview_footer_loading_more_title);
        mLoadingMoreTitle.setVisibility(GONE);

        mListView.addFooterView(mFootView, null, false);
        if (mNoFooterView)
            mFootView.setVisibility(View.GONE);

        setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

            @Override
            public void onLastItemVisible() {
                if (mOnLoadMoreListener != null && mAutoLoadMore && mHasMore) {
                    mOnLoadMoreListener.onLoadMore();
                }
            }
        });

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (getContext() instanceof Activity) {
            if (((Activity) getContext()).getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
                if (true && oldh > h && oldh == mOriginHeight) {
                    mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                }
            }
        }
    }

    public void setAutoLoadMore(boolean isAutoLoadMore) {
        mAutoLoadMore = isAutoLoadMore;
    }

    public void setCustomNoMoreView(View view, RelativeLayout.LayoutParams lyp) {
        mCustomNoMoreView = view;
        view.setLayoutParams(lyp);
        mFooterContentView.addView(view);
    }

    public ListView getListView() {
        return this.getRefreshableView();
    }

    public void showLoadingData() {
        if (mNoFooterView)
            return;
        mFootView.setVisibility(VISIBLE);
        mFooterContentView.setVisibility(View.VISIBLE);
        hideNoMoreView();
        mImageLoadingMore.setVisibility(VISIBLE);
        mLoadingMoreTitle.setVisibility(VISIBLE);
    }

    public void hideFooterView() {
        mFootView.setVisibility(GONE);
        mFooterContentView.setVisibility(View.GONE);
    }

    public void setNoFootView() {
        mFootView.setVisibility(GONE);
        mFooterContentView.setVisibility(View.GONE);
        mNoFooterView = true;
    }

    public void showNoMore() {
        if (mNoFooterView)
            return;
        mFootView.setVisibility(GONE);
        mFooterContentView.setVisibility(View.VISIBLE);
        mImageLoadingMore.setVisibility(GONE);
        mLoadingMoreTitle.setVisibility(GONE);

        if (null != mCustomNoMoreView) {
            mCustomNoMoreView.setVisibility(View.VISIBLE);
            mNoMoreView.setVisibility(GONE);
        } else {
            mNoMoreView.setVisibility(VISIBLE);
        }
    }

    private void hideNoMoreView() {
        mNoMoreView.setVisibility(View.GONE);
        if (null != mCustomNoMoreView)
            mCustomNoMoreView.setVisibility(View.GONE);
    }

    @Override
    public void onLoadingDataComplete(boolean hasMore) {
        this.onRefreshComplete();
        this.mHasMore = hasMore;

        if (mNoFooterView)
            return;

        mFootView.setVisibility(GONE);
        mImageLoadingMore.setVisibility(GONE);
        mLoadingMoreTitle.setVisibility(GONE);
        if (hasMore) {
            hideNoMoreView();
        } else {
            showNoMore();
        }
    }

    @Override
    public void setAdapter(AdaptableViewManager adapter) {
        super.setAdapter(adapter);
    }

    @Override
    public void setLoadMoreHandler(
            OnLoadMoreListener handler) {
        mOnLoadMoreListener = handler;

    }

    private class PTROnGlobalLayoutListener implements OnGlobalLayoutListener {
        @Override
        public void onGlobalLayout() {
            mOriginWidth = getWidth();
            mOriginHeight = getHeight();
            getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
    }

    public Boolean contentIsEmptyOrFirstChildInView() {
        Boolean flag = false;
        if (getListView().getCount() == 0) {
            flag = true;
        } else {
            View c = mListView.getChildAt(0);
            if (mListView.getFirstVisiblePosition() == 0 && c != null && c.getTop() == 0) {
                flag = true;
            }
        }
        return flag;
    }

    public void startRefresh() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new RefreshTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new RefreshTask().execute();
        }
    }

    private class RefreshTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            // Simulates a background job.
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            // Do some stuff here

            // Call onRefreshComplete when the list has been refreshed.
            PullToRefreshListViewContainer.this.demo();
            PullToRefreshListViewContainer.this.setRefreshing(false);

            super.onPostExecute(result);
        }
    }
}