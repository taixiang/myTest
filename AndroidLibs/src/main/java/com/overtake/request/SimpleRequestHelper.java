package com.overtake.request;

import com.overtake.base.OTConsts;
import com.overtake.data.OTDataRequest;
import com.overtake.data.OTDataTask;
import com.overtake.data.OTRequestManager;
import com.overtake.base.OTJson;

public class SimpleRequestHelper {

    private String mDataCategory = "CommonRequestData";
    private PrepareRequestHandler mPrepareRequestHandler;
    private AfterRequestHandler mAfterRequestHandler;

    private boolean mBusy = false;

    public SimpleRequestHelper(PrepareRequestHandler prepareRequestHandler) {
        mPrepareRequestHandler = prepareRequestHandler;
    }

    public SimpleRequestHelper(PrepareRequestHandler prepareRequestHandler, AfterRequestHandler afterRequestHandler) {
        mPrepareRequestHandler = prepareRequestHandler;
        mAfterRequestHandler = afterRequestHandler;
    }

    public void query() {
        if (!entryLock()) {
            return;
        }
        OTDataTask task = OTDataTask.createTask(mDataCategory, OTConsts.KXDATA_REQUEST_COMMON_QUERY, OTConsts.KXDATA_ID_DEFAULT);
        task.senders.add(this);
        OTRequestManager.getInstance().addTask(task);
    }

    private Boolean entryLock() {
        if (mBusy)
            return false;
        mBusy = true;
        return true;
    }

    private void releaseLock() {
        mBusy = false;
    }

    public void getDataRequestForTask(OTDataRequest request) {
        if (null != mPrepareRequestHandler) {
            mPrepareRequestHandler.prepareRequest(request);
        }
        mPrepareRequestHandler = null;
    }

    public void afterProcessJson(boolean isSuccess, OTJson retRawData) {
        releaseLock();
        if (null != mAfterRequestHandler) {
            mAfterRequestHandler.afterRequest(isSuccess, retRawData);
        }
        mAfterRequestHandler = null;
    }
}
