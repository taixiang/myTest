package com.overtake.data;

import java.util.HashMap;
import java.util.HashSet;

import com.overtake.base.OTConsts;
import com.loopj.android.http.RequestProgressHandler;

public final class OTDataTask {

    public static interface ITaskComparator {
        public boolean isEquals(OTDataTask taskA, OTDataTask taskB);
    }

    public String dataCategory;
    public int dataRequestType;
    public long dataId;
    public HashMap<String, String> args;
    public boolean ignoreAuth;
    public HashMap<String, Object> userInfo;
    public HashSet<Object> senders;
    public RequestProgressHandler uploadProgressHandler;

    public OTDataTask() {

        this.args = new HashMap<String, String>();
        this.userInfo = new HashMap<String, Object>();
        this.senders = new HashSet<Object>();
    }

    public static OTDataTask createTask(String dataCategory, int requestType, long dataId) {

        return createTask(null, dataCategory, requestType, dataId);
    }

    public static OTDataTask createTask(Object sender, String dataCategory, int requestType, long dataId) {

        OTDataTask task = new OTDataTask();
        if (sender != null) {

            task.senders.add(sender);
        }

        task.dataCategory = dataCategory;
        task.dataRequestType = requestType;
        task.dataId = dataId;

        return task;
    }

    public HashMap<String, Object> convert2Dictionary() {
        HashMap<String, Object> dict = new HashMap<String, Object>();

        dict.put(OTConsts.DATA_REQUEST_KEY_CATEGORY, this.dataCategory);
        dict.put(OTConsts.DATA_REQUEST_KEY_TYPE, this.dataRequestType);
        dict.put(OTConsts.DATA_REQUEST_KEY_DATAID, this.dataId);
        dict.put(OTConsts.DATA_REQUEST_KEY_ARGS, this.args);
        dict.put(OTConsts.DATA_REQUEST_KEY_USERINFO, this.userInfo);

        return dict;
    }

    public static OTDataTask taskWithInfo(HashMap<String, Object> info) {
        OTDataTask task = new OTDataTask();
        task.dataCategory = info.get(OTConsts.DATA_REQUEST_KEY_CATEGORY).toString();
        task.dataRequestType = Integer.parseInt(info.get(OTConsts.DATA_REQUEST_KEY_TYPE).toString());
        task.dataId = Long.parseLong(info.get(OTConsts.DATA_REQUEST_KEY_DATAID).toString());
        task.args = (HashMap<String, String>) info.get(OTConsts.DATA_REQUEST_KEY_ARGS);
        task.userInfo = (HashMap<String, Object>) info.get(OTConsts.DATA_REQUEST_KEY_USERINFO);

        return task;
    }


}
