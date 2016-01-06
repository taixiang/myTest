package com.overtake.objectlist.view;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.overtake.objectlist.AdaptableViewManager;
import com.overtake.objectlist.IAdaptableContainer;
import com.overtake.objectlist.ListAbstractCustomizer;
import com.overtake.objectlist.OnLoadMoreListener;

import cn.ikinder.androidlibs.R;

public class PullToRefreshGridViewContainer extends PullToRefreshGridView implements IAdaptableContainer {
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
	private GridView mGridView;
	private TextView mLoadingMoreTitle;

	private boolean mAutoLoadMore = true;
	private boolean mHasMore = true;
	private boolean mNoFooterView = false;

	protected boolean mScrollToBottomWhenSizeChange = false;
	private int mColumn  = 3;

	public PullToRefreshGridViewContainer(Context context, int numCol) {
		super(context, Mode.PULL_FROM_START, AnimationStyle.FLIP);
		mColumn = numCol;
		doLayout();
	}

	public PullToRefreshGridViewContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		doLayout();
	}

	public void setScrollToBottomWhenSizeChange(boolean scrollToBottomWhenSizeChange) {
		mScrollToBottomWhenSizeChange = scrollToBottomWhenSizeChange;
	}

	private void doLayout() {
		setOrientation(LinearLayout.VERTICAL);
		
		mGridView = getRefreshableView();
		mGridView.setNumColumns(mColumn);
		mGridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		mGridView.setHorizontalSpacing(10);
		mGridView.setVerticalSpacing(10);
		mGridView.setPadding(10, 10, 10, 10);
		mGridView.setSelector(R.drawable.ptr_selector_no_pressed);
		mGridView.setBackgroundColor(getContext().getResources().getColor(R.color.window_background));
		LinearLayout footView =(LinearLayout) LayoutInflater.from(getContext()).inflate(ListAbstractCustomizer.CONTAINER_FOOTER_LAYOUT_ID, null);
		footView.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.TOP);

		mFooterContentView = (RelativeLayout) footView.findViewById(R.id.ptr_id_listview_footer);

		mNoMoreView = footView.findViewById(R.id.ptr_id_listview_footer_nomore);
		mNoMoreView.setVisibility(View.INVISIBLE);

		mLoadingMoreProgress = (ProgressBar) footView.findViewById(R.id.ptr_id_listview_footer_loading_more);
		mLoadingMoreProgress.setVisibility(INVISIBLE);
		
		mLoadingMoreTitle = (TextView) footView.findViewById(R.id.ptr_id_listview_footer_loading_more_title);
		mLoadingMoreTitle.setVisibility(INVISIBLE);
		
		if (mNoFooterView) {
			mFooterContentView.setVisibility(View.GONE);
		}

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

	public void setAutoLoadMore(boolean isAutoLoadMore) {
		mAutoLoadMore = isAutoLoadMore;
	}

	public void setCustomNoMoreView(View view, RelativeLayout.LayoutParams lyp) {
		mCustomNoMoreView = view;
		view.setLayoutParams(lyp);
		mFooterContentView.addView(view);
	}

	public GridView getListView() {
		return mGridView;
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
		onRefreshComplete();
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
	
	public void setNumColumns(int columns){
		mColumn = columns; 
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

	@Override
	public Boolean contentIsEmptyOrFirstChildInView() {
		return null;
	}
	
	public void startRefresh(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new RefreshTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new RefreshTask().execute();
        }
	}
	
	private class RefreshTask extends AsyncTask<Void, Void, String[]>{
		
		@Override
		protected String[] doInBackground(Void... params){
			
			try{
				Thread.sleep(100);
			} catch (InterruptedException e){}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String [] result){
			PullToRefreshGridViewContainer.this.setRefreshing(false);
			PullToRefreshGridViewContainer.this.demo();
		}
	}
}
