package com.overtake.objectlist;

import java.util.ArrayList;

import com.overtake.base.OTConsts;
import com.overtake.base.OTJson;
import com.overtake.data.OTBaseData;
import com.overtake.data.OTDataManager;
import com.overtake.data.OTDataTask;
import com.overtake.data.OTRequestManager;
import com.overtake.data.OTRequestObserver;
import com.overtake.utils.OTLog;

/**
 * @author lhq
 */
public class ListDataProvider implements OTRequestObserver {

    // ===========================================================
    // public interface and enumerations
    // ===========================================================
    public enum RequestType {
        request_cache_then_latest, request_cache, request_latest, request_get_more, request_clear, modify,
    }

    public enum ListDataType {
        cache, latest, more, after_clear, after_modified
    }

    public interface RequestDataHandler {
        void prepareDataTask(RequestType requestType, OTDataTask dataTask);

        void onRequestSucceed(ListDataType dataType, ListDataWrapper listData, Boolean async);
    }

    protected long mDataId;
    protected String mDataCategory;
    protected int mNumPerPage;
    protected int mLastStart;
    protected Boolean mBusy;
    protected ListDataWrapper mListDataWrapper;
    protected int mLastLen;
    protected Long mLastRequestTime = 0L;
    protected int mRequestLatestDataTimeInterval = 0;

    protected RequestDataHandler mRequestDataHandler;

    public ListDataProvider(String dataCategory, long dataId, int numPerPage) {
        mLastStart = 0;
        mLastLen = 0;
        mBusy = false;

        mNumPerPage = numPerPage;
        mRequestLatestDataTimeInterval = OTConsts.REQUEST_LATESTDATA_TIME_INTERVAL;
        mDataCategory = dataCategory;
        mDataId = dataId;
        OTRequestManager.getInstance().registerRequestObserver(this, mDataCategory);
        mListDataWrapper = new ListDataWrapper();
        loadCacheData();
    }

    public ListDataProvider(String dataCategory, long dataId) {
        this(dataCategory, dataId, OTConsts.LIST_NUM_PER_PAGE);
    }

    public void clear() {
        OTRequestManager.getInstance().unregisterRequestObserver(this);
    }

    // ===========================================================
    // public methods
    // ===========================================================
    public void setNumPerPage(int numPerPage) {
        mNumPerPage = numPerPage;
    }

    public void setRequestLatestDataTimeInterval(int timeInterval) {
        mRequestLatestDataTimeInterval = timeInterval;
    }

    public String getDataCategory() {
        return mDataCategory;
    }

    public OTJson getRawDataFromDataModel() {
        return getDataModel().getListData(mDataId);
    }

    public void submitDataListChange() {
        getDataModel().saveCacheForDataId(mDataId);
    }

    public void setRequestDataHandler(RequestDataHandler handler) {
        mRequestDataHandler = handler;
    }

    public void requestLatestData(boolean withPageArguments) {
        final OTDataTask task = genDataTask(0, RequestType.request_latest, withPageArguments);
        if (!entryLock()) {
            OTRequestManager.getInstance().cancelTask(task, new OTDataTask.ITaskComparator() {
                @Override
                public boolean isEquals(OTDataTask taskA, OTDataTask taskB) {
                    return taskA.dataId == taskB.dataId &&
                            taskA.dataCategory.equals(taskB.dataCategory) &&
                            taskA.dataRequestType == taskB.dataRequestType;
                }
            });
            releaseLock();
        }

        OTRequestManager.getInstance().addTask(task);
    }

    public void clearData() {
        mListDataWrapper.clear();
        mRequestDataHandler.onRequestSucceed(ListDataType.after_clear, mListDataWrapper, false);
    }

    public void requestCacheData() {
        mRequestDataHandler.onRequestSucceed(ListDataType.cache, mListDataWrapper, true);
    }

    public void initByDataList(ArrayList<?> dataList, Boolean hasMore) {
        mListDataWrapper.initByData(dataList, hasMore);
        int len = dataList.size();
        mLastStart = 0;
        mLastLen = len;
        mRequestDataHandler.onRequestSucceed(ListDataType.latest, mListDataWrapper, true);
    }

    public void addData(int index, ArrayList<?> dataList, Boolean hasMore) {
        mListDataWrapper.addData(index, dataList, hasMore);
        mRequestDataHandler.onRequestSucceed(ListDataType.after_modified, mListDataWrapper, true);
    }

    public void addData(ArrayList<?> dataList, Boolean hasMore) {
        mListDataWrapper.addData(dataList, hasMore);
        mRequestDataHandler.onRequestSucceed(ListDataType.more, mListDataWrapper, true);
    }

    public void addData(Object item) {
        mListDataWrapper.dataList.add(item);
        mRequestDataHandler.onRequestSucceed(ListDataType.more, mListDataWrapper, true);
    }

    public void requestCacheDataOrLatestData(boolean withPageArguments) {
        Boolean needRequestLatestData = needRequestLatestData();
        if (!needRequestLatestData || mListDataWrapper.count() > 0) {
            requestCacheData();
        }
        if (needRequestLatestData) {
            requestLatestData(withPageArguments);
        }
    }

    public boolean needRequestLatestData() {
        Boolean needRequestLatestData = (System.currentTimeMillis() - mLastRequestTime) > mRequestLatestDataTimeInterval * 1000;
        return needRequestLatestData;
    }

    public void requestMore() {
        if (!entryLock())
            return;
        OTDataTask task = genDataTask(mLastStart + mNumPerPage, RequestType.request_get_more, true);
        OTRequestManager.getInstance().addTask(task);
    }

    public void remove(int index) {
        if (index >= mListDataWrapper.dataList.size() && index < 0) {

        } else {
            mListDataWrapper.dataList.remove(index);
            mRequestDataHandler.onRequestSucceed(ListDataType.after_modified, mListDataWrapper, true);
        }
    }

    public boolean hasMore() {
        return mListDataWrapper.hasMore;
    }

    public int getNumPerPage() {
        return mNumPerPage;
    }

    public int getStart() {
        return mLastStart;
    }

    public ListDataWrapper getListData() {
        return mListDataWrapper;
    }

    public int getTotal() {
        return mListDataWrapper.count();
    }

    // ===========================================================
    // private methods
    // ===========================================================
    protected void loadCacheData() {
        OTJson data = getRawDataFromDataModel();
        mListDataWrapper.updateFrom(data);
        mLastStart = data.getIntForKey("last_start");
        mLastLen = mListDataWrapper.count();
    }

    protected OTBaseData getDataModel() {
        OTBaseData dataModel = OTDataManager.getInstance().getDataForCategory(mDataCategory);
        return dataModel;
    }

    protected OTDataTask genDataTask(int start, RequestType requestType, Boolean withPageArguments) {
        int kxDataRequestType = requestType == RequestType.request_latest ? OTConsts.KXDATA_REQUEST_REFRESH : OTConsts.KXDATA_REQUEST_GETMORE;
        OTDataTask task = OTDataTask.createTask(mDataCategory, kxDataRequestType, mDataId);

        if (withPageArguments) {
            task.args.put("start", String.valueOf(start));
            task.args.put("num", String.valueOf(mNumPerPage));
        }
        if (null != mRequestDataHandler) {
            mRequestDataHandler.prepareDataTask(requestType, task);
        }
        return task;
    }

    protected Boolean entryLock() {
        if (mBusy)
            return false;
        mBusy = true;
        return true;
    }

    protected void releaseLock() {
        mBusy = false;
    }

    // ===========================================================
    // Implements interface KXRequestObserver
    // ===========================================================
    @Override
    public void requestSuccessForTask(OTDataTask task) {
        if (task.dataId != mDataId) {
            return;
        }
        releaseLock();

        mLastRequestTime = System.currentTimeMillis();

        mListDataWrapper.updateFrom(getRawDataFromDataModel());
        int total = mListDataWrapper.count();
        ListDataType dataType = null;
        if (task.dataRequestType == OTConsts.KXDATA_REQUEST_REFRESH) {
            dataType = ListDataType.latest;
            mLastStart = 0;
            mLastLen = total;
        } else {
            dataType = ListDataType.more;
            if (total > mLastLen) {
                mLastStart += mNumPerPage;
                mLastLen = total;
            }
        }

        mListDataWrapper.setLastStart(mLastStart);
        submitDataListChange();
        if (null != mRequestDataHandler) {
            mRequestDataHandler.onRequestSucceed(dataType, mListDataWrapper, true);
        }
    }

    @Override
    public void requestFailedForTask(OTDataTask task, Throwable error) {
        releaseLock();

        requestCacheData();
    }

    @Override
    public void requestDataModifyForTask(OTDataTask task) {
    }

    @Override
    public void taskAddedToRequestManager(OTDataTask task) {
    }
}
