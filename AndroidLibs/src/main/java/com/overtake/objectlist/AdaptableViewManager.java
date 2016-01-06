package com.overtake.objectlist;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.overtake.base.OTJson;
import com.overtake.data.OTDataTask;
import com.overtake.data.OTRequestManager;
import com.overtake.objectlist.ListDataProvider.ListDataType;
import com.overtake.objectlist.ListDataProvider.RequestDataHandler;
import com.overtake.objectlist.ListDataProvider.RequestType;
import com.overtake.objectlist.stickylistview.StickyListHeadersAdapter;
import com.overtake.utils.OTLog;

public class AdaptableViewManager extends BaseAdapter implements RequestDataHandler, OnLoadMoreListener, StickyListHeadersAdapter {
    // ===========================================================
    // Interface
    // ===========================================================
    public interface PrepareListRequestHandler {
        void prepareDataTask(RequestType requestType, OTDataTask dataTask);
    }

    public interface StickyListHeaderHandler {

        public View getHeaderView(int position, View convertView,
                                  ViewGroup parent, OTJson json);

        public long getHeaderId(int position, OTJson json);

    }

    protected PrepareListRequestHandler mPrepareRequestHandler;
    protected StickyListHeaderHandler mStickyListHeaderHandler;
    @SuppressWarnings("rawtypes")
    protected ObjectListViewController mObjectListViewController;

    protected ListDataProvider mDataProvider;
    @SuppressWarnings("rawtypes")
    protected ArrayList<ListViewItemDataWrapperBase> mItemWrapperList;
    protected Boolean mReUseConvertView = true;

    protected IAdaptableContainer mListViewContainer;

    @SuppressWarnings("rawtypes")
    public AdaptableViewManager(IAdaptableContainer listViewContainer, String dataCategory, long dataId, ObjectListViewController viewController) {
        mItemWrapperList = new ArrayList<ListViewItemDataWrapperBase>();
        mDataProvider = createDataProvider(dataCategory, dataId);

        mDataProvider.setRequestDataHandler(this);

        mListViewContainer = listViewContainer;
        mListViewContainer.setLoadMoreHandler(this);
        mListViewContainer.setAdapter(this);

        mObjectListViewController = viewController;
        if (viewController instanceof PrepareListRequestHandler) {
            setPrepareRequestHandler((PrepareListRequestHandler) viewController);
        }
    }

    protected ListDataProvider createDataProvider(String dataCategory, long dataId) {
        return new ListDataProvider(dataCategory, dataId);
    }

    // ===========================================================
    // Implements interface OnLoadMoreListener
    // ===========================================================
    @Override
    public void onLoadMore() {
        requestMoreData();
    }

    // ===========================================================
    // Setter & Getter
    // ===========================================================
    public void setReUseConvertView(Boolean reUse) {
        mReUseConvertView = reUse;
    }

    public void setPrepareRequestHandler(PrepareListRequestHandler handler) {
        mPrepareRequestHandler = handler;
    }

    public ListDataProvider getDataProvider() {
        return mDataProvider;
    }

    public void setStickyListHeaderHandler(StickyListHeaderHandler handler) {
        mStickyListHeaderHandler = handler;
    }

    // ===========================================================
    // Public methods
    // ===========================================================
    public void requestCacheOrLatestData(boolean withPageArguments) {
        notifyOnloadingData(RequestType.request_cache_then_latest);
        this.mDataProvider.requestCacheDataOrLatestData(withPageArguments);
    }

    public void requestCache() {
        this.mDataProvider.requestCacheData();
    }

    public void requestCacheOrLatestData() {
        requestCacheOrLatestData(true);
    }

    public void requestLatestData() {
        requestLatestData(true);
    }

    public void requestLatestData(boolean withPageArguments) {
        notifyOnloadingData(RequestType.request_latest);
        mDataProvider.requestLatestData(withPageArguments);
    }

    public void requestMoreData() {
        notifyOnloadingData(RequestType.request_get_more);
        mDataProvider.requestMore();
    }

    public void clearData() {
        mDataProvider.clearData();
    }

    public void showData(ArrayList<?> dataList, Boolean hasMore) {
        mDataProvider.initByDataList(dataList, hasMore);
    }

    public void clearObserver() {
        OTRequestManager.getInstance().unregisterRequestObserver(mDataProvider);
    }

    @SuppressWarnings("rawtypes")
    public void removeItem(ListViewItemDataWrapperBase itemWrapperBase, Boolean notifyDataChange) {
        int index = mItemWrapperList.indexOf(itemWrapperBase);
        if (-1 != index) {
            if (mDataProvider.getListData().dataList.size() > index) {
                mDataProvider.getListData().dataList.remove(index);
            }
            mItemWrapperList.remove(index);
            if (notifyDataChange)
                notifyRequestDataSucc(mDataProvider.getListData());
        }
    }

    @SuppressWarnings("rawtypes")
    public void removeItem(ListViewItemDataWrapperBase itemWrapperBase) {
        removeItem(itemWrapperBase, true);
    }

    @SuppressWarnings("rawtypes")
    public void addItem(ListViewItemDataWrapperBase itemWrapperBase, Boolean notifyDataChange) {
        int index = mItemWrapperList.indexOf(itemWrapperBase);
        if (-1 == index) {
            mItemWrapperList.add(itemWrapperBase);
            if (notifyDataChange)
                notifyRequestDataSucc(mDataProvider.getListData());
        }
    }

    @SuppressWarnings("rawtypes")
    public void addItem(ListViewItemDataWrapperBase itemWrapperBase) {
        addItem(itemWrapperBase, true);
    }

    public void addRawData(Object itemData) {
        mDataProvider.addData(itemData);
    }

    @SuppressWarnings("rawtypes")
    public ArrayList<ListViewItemDataWrapperBase> getItemDataWrapperList() {
        return mItemWrapperList;
    }

    protected void notifyOnloadingData(RequestType requestType) {
        if (mDataProvider.getListData().count() == 0 || requestType == RequestType.request_get_more) {
            if (null != mListViewContainer)
                mListViewContainer.showLoadingData();
        }

        if (null != mObjectListViewController) {
            mObjectListViewController.onRequestData(requestType);
        }
    }

    protected void notifyRequestDataSucc(ListDataWrapper listData) {
        if (null != mListViewContainer) {
            mListViewContainer.onLoadingDataComplete(listData.hasMore);
        }

        notifyDataSetChanged();

        if (null != mObjectListViewController) {
            mObjectListViewController.onRequestDataSucceed();
        }

    }

    // ===========================================================
    // Implements interface: BaseAdapter
    // ===========================================================

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ListViewItemDataWrapperBase wrapper = mItemWrapperList.get(position);
        if (convertView == null || !mReUseConvertView) {

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            ListViewItemViewHolderBase holderBase = mObjectListViewController.createItemHolder();
            holderBase.setHoldingItemWrapper(wrapper);
            convertView = holderBase.createView(inflater, wrapper);

            holderBase.setHoldingItemWrapper(wrapper);
            holderBase.showHoldingItemWrapper();
            if (mReUseConvertView) {
                convertView.setTag(holderBase);
            }
        } else {
            ListViewItemViewHolderBase holderBase = (ListViewItemViewHolderBase) convertView.getTag();
            holderBase.setHoldingItemWrapper(wrapper);
            holderBase.showHoldingItemWrapper();
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return mItemWrapperList.size();
    }

    @Override
    public Object getItem(int position) {
        if (mItemWrapperList.size() <= position || position < 0) {
            return null;
        }
        return mItemWrapperList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // ===========================================================
    // Implements interface: RequestDataHandler
    // ===========================================================

    @Override
    public void prepareDataTask(RequestType requestType, OTDataTask dataTask) {
        if (null != mPrepareRequestHandler) {
            mPrepareRequestHandler.prepareDataTask(requestType, dataTask);
        }
    }

    @Override
    public void onRequestSucceed(ListDataType dataType, ListDataWrapper listData, Boolean async) {
        OTLog.i("list_view", String.format("onRequestSucc %s", dataType));
        if (null == dataType)
            OTLog.i("list_view", "dataid should not be null");

        listData.dataType = dataType;
        if (async) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new DataPreProcessor().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, listData);
            } else {
                new DataPreProcessor().execute(listData);
            }
        } else {
            processListData(listData);
            afterPreProcess(listData, tmpList);
        }
    }

    protected void afterPreProcess(ListDataWrapper listData, ArrayList<ListViewItemDataWrapperBase> tmpList) {
        if (listData.dataType == ListDataType.more) {

        } else {
            mItemWrapperList.clear();
        }
        mItemWrapperList.addAll(tmpList);
        notifyRequestDataSucc(listData);
    }

    private ArrayList<ListViewItemDataWrapperBase> tmpList;

    private void processListData(ListDataWrapper listData) {
        int start = 0;
        if (listData.dataType == ListDataType.more) {
            start = mItemWrapperList.size();
        }
        int currentDataLength = listData.dataList.size();
        tmpList = new ArrayList<ListViewItemDataWrapperBase>();
        for (int i = start; i < currentDataLength; i++) {
            ListViewItemDataWrapperBase wrapper = null;
            Object itemData = listData.dataList.get(i);
            if (itemData instanceof ArrayList || itemData instanceof HashMap) {
                wrapper = mObjectListViewController.createItemWrapper(i, OTJson.createJson(itemData));
            } else {

                wrapper = mObjectListViewController.createItemWrapper(i, itemData);
            }
            wrapper.total = currentDataLength;
            wrapper.preProcess();
            tmpList.add(wrapper);
        }
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    private class DataPreProcessor extends AsyncTask<ListDataWrapper, Void, Integer> {

        private ListDataWrapper listData;

        @Override
        protected Integer doInBackground(ListDataWrapper... params) {
            listData = params[0];
            processListData(listData);
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            afterPreProcess(listData, tmpList);
        }
    }

    @Override
    public View getHeaderView(int position, View convertView,
                              ViewGroup parent) {
        if (mStickyListHeaderHandler == null) {
            return null;
        } else {
            return mStickyListHeaderHandler.getHeaderView(position, convertView, parent, (OTJson) mItemWrapperList.get(position).getRawData());
        }
    }

    @Override
    public long getHeaderId(int position) {
        if (mStickyListHeaderHandler != null)
            return mStickyListHeaderHandler.getHeaderId(position, (OTJson) mItemWrapperList.get(position).getRawData());
        return 0;
    }
}
