package com.overtake.objectlist.view;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.overtake.objectlist.ListAbstractCustomizer;
import com.overtake.objectlist.AdaptableViewManager;
import com.overtake.objectlist.IAdaptableContainer;
import com.overtake.objectlist.OnLoadMoreListener;

import cn.ikinder.androidlibs.R;

public class GridViewContainer extends LinearLayout implements IAdaptableContainer {
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
    private ProgressBar mLoadingMoreProgress;
    private OnLoadMoreListener mOnLoadMoreListener;
    private OnListScrollListener mOnListScrollListener;
    private GridView mListView;
    private TextView mLoadingMoreTitle;

    private boolean mAutoLoadMore = true;
    private boolean mHasMore = true;
    private boolean mIsEnd = true;
    private boolean mIsPinnedHeader = false;
    private boolean mNoFooterView = false;

    protected int mOriginWidth;
    protected int mOriginHeight;
    protected boolean mScrollToBottomWhenSizeChange = false;
    private int mColumn = 3;

    public GridViewContainer(Context context, int numCol) {
        super(context);
        mColumn = numCol;
        doLayout();
    }

    public GridViewContainer(Context context, AttributeSet attrs) {
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
        }
        arr.recycle();
        doLayout();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (getContext() instanceof Activity) {
            if (((Activity) getContext()).getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
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
        setOrientation(LinearLayout.VERTICAL);
        ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new PTROnGlobalLayoutListener());

        mListView = createContentView();
        LayoutParams lyp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mListView.setLayoutParams(lyp);
        this.addView(mListView);

        LinearLayout footView = (LinearLayout) LayoutInflater.from(getContext()).inflate(ListAbstractCustomizer.CONTAINER_FOOTER_LAYOUT_ID, null);
        footView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);

        mFooterContentView = (RelativeLayout) footView.findViewById(R.id.ptr_id_listview_footer);

        mNoMoreView = footView.findViewById(R.id.ptr_id_listview_footer_nomore);
        mNoMoreView.setVisibility(View.INVISIBLE);

        mLoadingMoreProgress = (ProgressBar) footView.findViewById(R.id.ptr_id_listview_footer_loading_more);
        mLoadingMoreProgress.setVisibility(INVISIBLE);

        mLoadingMoreTitle = (TextView) footView.findViewById(R.id.ptr_id_listview_footer_loading_more_title);
        mLoadingMoreTitle.setVisibility(INVISIBLE);

        this.addView(footView);
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
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    // ===========================================================
    // Setter & Getter
    // ===========================================================
    public void setAutoLoadMore(boolean isAutoLoadMore) {
        mAutoLoadMore = isAutoLoadMore;
    }

    public void setListScrollListener(OnListScrollListener listener) {
        mOnListScrollListener = listener;
    }

    public void setCustomNoMoreView(View view, RelativeLayout.LayoutParams lyp) {
        mCustomNoMoreView = view;
        view.setLayoutParams(lyp);
        mFooterContentView.addView(view);
    }

    public GridView getListView() {
        return mListView;
    }

    public Boolean isPinnedHeader() {
        return mIsPinnedHeader;
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
        mLoadingMoreProgress.setVisibility(VISIBLE);
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
        mLoadingMoreProgress.setVisibility(INVISIBLE);
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
        this.mHasMore = hasMore;

        if (mNoFooterView)
            return;
        mLoadingMoreProgress.setVisibility(INVISIBLE);
        mLoadingMoreTitle.setVisibility(INVISIBLE);
        if (hasMore) {
            hideNoMoreView();
        } else {
            showNoMore();
        }
    }

    public void setNumColumns(int columns) {
        mColumn = columns;
    }

    protected GridView createContentView() {
        GridView gridView = new GridView(getContext());
        gridView.setNumColumns(mColumn);
        gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        gridView.setHorizontalSpacing(10);
        gridView.setVerticalSpacing(10);
        gridView.setPadding(10, 10, 10, 10);
        gridView.setSelector(R.drawable.ptr_selector_no_pressed);
        return gridView;
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
            if (mListView.getFirstVisiblePosition() == 0 && c != null && c.getTop() - mListView.getPaddingTop() == 0) {
                flag = true;
            }
        }
        return flag;
    }
}
