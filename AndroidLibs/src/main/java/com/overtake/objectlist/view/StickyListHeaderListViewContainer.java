package com.overtake.objectlist.view;

import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.overtake.objectlist.ListAbstractCustomizer;
import com.overtake.objectlist.AdaptableViewManager;
import com.overtake.objectlist.IAdaptableContainer;
import com.overtake.objectlist.OnLoadMoreListener;
import com.overtake.objectlist.stickylistview.StickyListHeadersListView;
import com.overtake.objectlist.stickylistview.WrapperViewLis;
import com.overtake.objectlist.stickylistview.WrapperViewList;
import com.overtake.utils.OTLog;

import cn.ikinder.androidlibs.R;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StickyListHeaderListViewContainer extends LinearLayout implements IAdaptableContainer {

    public static interface OnListScrollListener {
        public void onScrollStateChanged(AbsListView view, int scrollState);

        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);
    }

    /*--- Fields ---*/
    private RelativeLayout mFooterContentView;
    private View mCustomNoMoreView;
    private View mNoMoreView;
    private ProgressBar mImageLoadingMore;
    private TextView mLoadingMoreTitle;

    private boolean mAutoLoadMore = true;
    private boolean mHasMore = true;
    private boolean mIsEnd = true;
    private boolean mNoFooterView = false;
    private StickyListHeadersListView mListView;
    private OnListScrollListener mOnListScrollListener;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean mScrollToBottomWhenSizeChange = false;

    protected int mOriginWidth;
    protected int mOriginHeight;
    public int lastPos;

    public StickyListHeaderListViewContainer(Context context) {

        this(context, null);
    }

    public StickyListHeaderListViewContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (attrs != null) {
            TypedArray arr = getContext().obtainStyledAttributes(attrs, R.styleable.KBListView, 0, 0);
            if (arr.hasValue(R.styleable.KBListView_auto_load_more)) {
                mAutoLoadMore = arr.getBoolean(R.styleable.KBListView_auto_load_more, false);
            }

            if (arr.hasValue(R.styleable.KBListView_no_footer)) {
                mNoFooterView = arr.getBoolean(R.styleable.KBListView_no_footer, false);
            }
            arr.recycle();
        }
        doLayout();
    }

    private void doLayout() {
        ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new PTROnGlobalLayoutListener());

        mListView = createContentView();
        LayoutParams lyp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mListView.setLayoutParams(lyp);
        this.addView(mListView);

        View footView = LayoutInflater.from(getContext()).inflate(ListAbstractCustomizer.CONTAINER_FOOTER_LAYOUT_ID, null);
        footView.setLayoutParams(new AbsListView.LayoutParams(-1, -2));

        mFooterContentView = (RelativeLayout) footView.findViewById(R.id.ptr_id_listview_footer);

        mNoMoreView = footView.findViewById(R.id.ptr_id_listview_footer_nomore);
        mNoMoreView.setVisibility(View.INVISIBLE);

        mImageLoadingMore = (ProgressBar) footView.findViewById(R.id.ptr_id_listview_footer_loading_more);
        mImageLoadingMore.setVisibility(INVISIBLE);

        mLoadingMoreTitle = (TextView) footView.findViewById(R.id.ptr_id_listview_footer_loading_more_title);
        mLoadingMoreTitle.setVisibility(INVISIBLE);

        mListView.addFooterView(footView);
        if (mNoFooterView) {
            mFooterContentView.setVisibility(View.GONE);
        }

        mListView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (mOnListScrollListener != null) {
                    mOnListScrollListener.onScrollStateChanged(view, scrollState);
                }

                if (scrollState == SCROLL_STATE_IDLE) {
                    lastPos = mListView.getFirstVisiblePosition();
                } else {
                    //ImageDownLoaderManager.getInstance().setNotCanLoad();
                }
                if (mIsEnd && scrollState == SCROLL_STATE_IDLE) {
                    onReachBottom();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (mOnListScrollListener != null) {
                    mOnListScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                }

                if (firstVisibleItem + visibleItemCount >= totalItemCount - 1)
                    mIsEnd = true;
                else
                    mIsEnd = false;
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (getContext() instanceof Activity) {
            Activity activity = (Activity) getContext();
            if (activity.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                if (mScrollToBottomWhenSizeChange && oldh > h && oldh == mOriginHeight) {
                    mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                }
            }
        }
    }

    public void setOnRefreshListener(OnRefreshListener<WrapperViewLis> listener) {
        getListView().setOnRefreshListener(listener);
    }

    public void setScrollToBottomWhenSizeChange(boolean scrollToBottomWhenSizeChange) {
        mScrollToBottomWhenSizeChange = scrollToBottomWhenSizeChange;
    }

    private void onReachBottom() {
        OTLog.i("list_view", String.format("onReachButtom ----+|+------+|+----> It is important auto_load_more: %s has_more: %s", mAutoLoadMore, mHasMore));
        if (mOnLoadMoreListener != null && mAutoLoadMore && mHasMore) {
            mOnLoadMoreListener.onLoadMore();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    public void serAutoLoadMore(boolean isAutoLoadMore) {
        mAutoLoadMore = isAutoLoadMore;
    }

    public void setLoadMoreHandler(OnLoadMoreListener listener) {
        mOnLoadMoreListener = listener;
    }

    public void setListScrollListener(OnListScrollListener listener) {
        mOnListScrollListener = listener;
    }

    public void setCustomNoMoreView(View view, RelativeLayout.LayoutParams lyp) {

    }

    public WrapperViewList getListView() {
        return mListView.getWrappedList();
    }


    private class PTROnGlobalLayoutListener implements OnGlobalLayoutListener {
        @SuppressWarnings("deprecation")
        @Override
        public void onGlobalLayout() {
            mOriginWidth = getWidth();
            mOriginHeight = getHeight();
            getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
    }

    public void showLoadingData() {
        if (mNoFooterView)
            return;
        OTLog.i("list_view", String.format("showLoadingData"));
        mFooterContentView.setVisibility(View.VISIBLE);
        hideNoMoreView();
        mImageLoadingMore.setVisibility(VISIBLE);
        mLoadingMoreTitle.setVisibility(VISIBLE);
    }

    public void hideFooterView() {
        OTLog.i("list_view", String.format("hideFooterView"));
        mFooterContentView.setVisibility(View.GONE);
    }

    public void setNoFootView() {
        mFooterContentView.setVisibility(View.GONE);
        mNoFooterView = true;
    }

    public void showNoMore() {
        if (mNoFooterView)
            return;
        OTLog.i("list_view", String.format("showNoMore"));
        mFooterContentView.setVisibility(View.VISIBLE);
        mImageLoadingMore.setVisibility(INVISIBLE);
        mLoadingMoreTitle.setVisibility(INVISIBLE);

        if (null != mCustomNoMoreView) {
            mCustomNoMoreView.setVisibility(View.VISIBLE);
            mNoMoreView.setVisibility(INVISIBLE);
        } else {
            mNoMoreView.setVisibility(VISIBLE);
        }
    }

    private void hideNoMoreView() {
        mNoMoreView.setVisibility(View.INVISIBLE);
        if (null != mCustomNoMoreView)
            mCustomNoMoreView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLoadingDataComplete(boolean hasMore) {
        getListView().onRefreshComplete();
        OTLog.i("list_view", String.format("onLoadDataComplete, has_more: %s", hasMore));
        this.mHasMore = hasMore;

        if (mNoFooterView)
            return;
        mImageLoadingMore.setVisibility(INVISIBLE);
        mLoadingMoreTitle.setVisibility(INVISIBLE);
        if (hasMore) {
            hideNoMoreView();
        } else {
            showNoMore();
        }
    }

    protected StickyListHeadersListView createContentView() {
        StickyListHeadersListView listView = null;
        listView = new StickyListHeadersListView(getContext());
        listView.setSelector(R.drawable.ptr_selector_no_pressed);
        listView.setFooterDividersEnabled(false);
        listView.setDivider(null);
        listView.setCacheColorHint(getContext().getResources().getColor(R.color.window_background));
        return listView;
    }

    @Override
    public void setAdapter(AdaptableViewManager adapter) {
        mListView.setAdapter(adapter);
    }

    @Override
    public Boolean contentIsEmptyOrFirstChildInView() {
        Boolean flag = false;
        if (mListView.getCount() == 0) {
            flag = true;
        } else {
            View c = mListView.getChildAt(0);
            if (mListView.getFirstVisiblePosition() == 0 && c != null && c.getTop() == 0) {
                flag = true;
            }
        }
        return flag;
    }
}
