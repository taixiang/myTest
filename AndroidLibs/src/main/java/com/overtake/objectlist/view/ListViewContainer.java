package com.overtake.objectlist.view;

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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.overtake.objectlist.ListAbstractCustomizer;
import com.overtake.objectlist.AdaptableViewManager;
import com.overtake.objectlist.IAdaptableContainer;
import com.overtake.objectlist.OnLoadMoreListener;
import com.overtake.objectlist.view.PinnedHeaderListView.PinnedHeaderListViewController;
import com.overtake.objectlist.view.PinnedHeaderListView.PinnedHeaderStatus;

import cn.ikinder.androidlibs.R;

public class ListViewContainer extends LinearLayout implements IAdaptableContainer {

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

    private OnListScrollListener mOnListScrollListener;
    private ListView mListView;

    private boolean mAutoLoadMore = true;
    private boolean mHasMore = true;

    private boolean mIsEnd = true;
    private boolean mIsPinnedHeader = false;
    private boolean mNoFooterView = false;

    protected int mOriginWidth;
    protected int mOriginHeight;
    protected boolean mScrollToBottomWhenSizeChange = false;

    private PinnedHeaderListViewController mPinnedHeaderListViewController;

    public ListViewContainer(Context context) {
        this(context, false);
    }

    public ListViewContainer(Context context, Boolean isPinnedHeader) {
        super(context);
        mIsPinnedHeader = isPinnedHeader;
        doLayout();
    }

    public ListViewContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray arr = getContext().obtainStyledAttributes(attrs, R.styleable.KBListView, 0, 0);
        if (arr != null) {
            if (arr.hasValue(R.styleable.KBListView_pinned_header)) {
                mIsPinnedHeader = arr.getBoolean(R.styleable.KBListView_pinned_header, false);
            }

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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (getContext() instanceof Activity) {
            if (((Activity) getContext()).getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
                if (mScrollToBottomWhenSizeChange && oldh > h && oldh == mOriginHeight) {
                    mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                }
            }
        }
    }

    public void setScrollToBottomWhenSizeChange(boolean scrollToBottomWhenSizeChange) {
        mScrollToBottomWhenSizeChange = scrollToBottomWhenSizeChange;
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

        mListView.addFooterView(footView, null, false);
        if (mNoFooterView) {
            mFooterContentView.setVisibility(View.GONE);
        }

        mListView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                if (mOnListScrollListener != null) {
                    mOnListScrollListener.onScrollStateChanged(view, scrollState);
                }

                if (scrollState == SCROLL_STATE_IDLE) {
                    //TODO prevent scroll lag
                    //ImageDownLoaderManager.getInstance().setCanLoad();
                } else {
                    //ImageDownLoaderManager.getInstance().setNotCanLoad();
                }
                if (mIsEnd && scrollState == SCROLL_STATE_IDLE) {
                    onReachBottom();
                }
            }

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (mOnListScrollListener != null) {
                    mOnListScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                }
                if (firstVisibleItem + visibleItemCount >= totalItemCount - 1)
                    mIsEnd = true;
                else
                    mIsEnd = false;

                // pinned header list view
                if (mIsPinnedHeader && view instanceof PinnedHeaderListView) {
                    processPinnedHeaderListView(((PinnedHeaderListView) view), firstVisibleItem);
                }
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        // try to layout the pinned header
        if (mIsPinnedHeader) {
            int firstVisibleItem = mListView.getFirstVisiblePosition();
            processPinnedHeaderListView(((PinnedHeaderListView) mListView), firstVisibleItem);
        }
    }

    public void setAutoLoadMore(boolean isAutoLoadMore) {
        mAutoLoadMore = isAutoLoadMore;
    }

    public void setListScrollListener(OnListScrollListener listener) {
        mOnListScrollListener = listener;
    }

    public void setPinnedHeaderListViewController(PinnedHeaderListViewController controller) {
        mPinnedHeaderListViewController = controller;
    }

    public void setListViewPinnedHeader(View header) {
        if (!mIsPinnedHeader)
            return;
        if (mListView instanceof PinnedHeaderListView) {
            ((PinnedHeaderListView) mListView).setPinnedHeader(header);
        }
    }

    public void setCustomNoMoreView(View view, RelativeLayout.LayoutParams lyp) {
        mCustomNoMoreView = view;
        view.setLayoutParams(lyp);
        mFooterContentView.addView(view);
    }

    public ListView getListView() {
        return mListView;
    }

    public Boolean isPinnedHeader() {
        return mIsPinnedHeader;
    }

    private void processPinnedHeaderListView(PinnedHeaderListView view, int position) {
        if (null != mPinnedHeaderListViewController) {
            PinnedHeaderStatus state = mPinnedHeaderListViewController.getPinnedHeaderState(position);
            view.controlPinnedHeader(state);
            if (state == PinnedHeaderStatus.Visible || state == PinnedHeaderStatus.PushedUp) {
                mPinnedHeaderListViewController.showPinnedHeader(view.getPinnedHander(), position);
            }
        }
    }

    private class PTROnGlobalLayoutListener implements OnGlobalLayoutListener {
        @Override
        public void onGlobalLayout() {
            mOriginWidth = getWidth();
            mOriginHeight = getHeight();
            getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
    }

    private void onReachBottom() {
        if (mOnLoadMoreListener != null && mAutoLoadMore && mHasMore) {
            mOnLoadMoreListener.onLoadMore();
        }
    }

    public void showLoadingData() {
        if (mNoFooterView)
            return;
        mFooterContentView.setVisibility(View.VISIBLE);
        hideNoMoreView();
        mImageLoadingMore.setVisibility(VISIBLE);
        mLoadingMoreTitle.setVisibility(VISIBLE);
    }

    public void hideFooterView() {
        mFooterContentView.setVisibility(View.GONE);
    }

    public void setNoFootView() {
        mFooterContentView.setVisibility(View.GONE);
        mNoFooterView = true;
    }

    public void showNoMore() {
        if (mNoFooterView)
            return;
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
        mHasMore = hasMore;

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

    protected ListView createContentView() {
        ListView listView;
        if (mIsPinnedHeader) {
            listView = new PinnedHeaderListView(getContext());
        } else {
            listView = new ListView(getContext());
        }
        listView.setSelector(R.drawable.ptr_selector_no_pressed);
        listView.setFooterDividersEnabled(false);
        listView.setDivider(null);
        return listView;
    }

    @Override
    public void setAdapter(AdaptableViewManager adapter) {
        mListView.setAdapter(adapter);
    }

    @Override
    public void setLoadMoreHandler(
            com.overtake.objectlist.OnLoadMoreListener handler) {
        mOnLoadMoreListener = handler;
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
}