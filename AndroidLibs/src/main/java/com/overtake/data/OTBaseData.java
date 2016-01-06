package com.overtake.data;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

import junit.framework.Assert;

import org.json.simple.JSONValue;

import android.annotation.SuppressLint;

import com.overtake.base.OTConsts;
import com.overtake.base.OTError;
import com.overtake.base.OTJson;
import com.overtake.utils.FileUtil;
import com.overtake.utils.OTLog;
import com.overtake.utils.Utils;
import com.loopj.android.http.AsyncHttpRequest;

@SuppressLint("DefaultLocale")
public abstract class OTBaseData {

    private final HashMap<String, Object> _data;
    private final HashMap<String, OTDataStatus> _dataStatus;
    private final String _dataCategory;

    public Hashtable<String, Object> args;

    public OTBaseData() {

        _data = new HashMap<String, Object>();
        _dataStatus = new HashMap<String, OTDataStatus>();
        _dataCategory = this.getClass().getSimpleName();
        loadAllCache();
    }

    public static OTBaseData getInstance(Class clz) {
        OTBaseData data = OTDataManager.getInstance().getDataForCategory(clz.getSimpleName());
        return data;
    }

    public String getDataCategory() {

        return _dataCategory;
    }

    public String getDataId(long dataId) {
        return String.format("%d", dataId);
    }

    /*
     * 如果是数据是数组就返回ArrayList.class, 如果是字典则返回HashMap.class
     */
    public abstract Class<?> getDataObjectClassForDataId(long dataId);


    /**
     * 处理列表数据，则应该重载这个方法
     *
     * @param dataId
     * @return
     */
    public OTJson getListData(long dataId) {
        Assert.assertEquals("this should be overrided", true, false);
        return null;
    }

    public Class<?> getDataStatusClassForDataId(long dataId) {

        // 暂时只支持KXDataStatus
        return OTDataStatus.class;
    }

    public OTDataStatus getDataStatusForDataId(long dataId) {

        OTDataStatus dataStatus = _dataStatus.get(this.getDataId(dataId));

        if (dataStatus == null) {

            Class<?> clazz = this.getDataStatusClassForDataId(dataId);

            if (clazz.isAssignableFrom(OTDataStatus.class)) {

                dataStatus = new OTDataStatus();

            } else {

                try {
                    dataStatus = (OTDataStatus) clazz.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            _dataStatus.put(this.getDataId(dataId), dataStatus);
        }

        return dataStatus;
    }

    public OTDataStatus getDataStatus() {

        return this.getDataStatusForDataId(OTConsts.KXDATA_ID_DEFAULT);
    }

    public String getUserId() {

        return null;
    }

    public boolean isAutoCache() {

        return false;
    }

    public boolean isAutoCacheForTask(OTDataTask task) {

        if (task.dataRequestType == OTConsts.KXDATA_REQUEST_REFRESH) {

            return true;
        }

        return false;
    }

    public String getBaseDir() {

        Assert.assertNotNull("this should be config according to project", null);

        return FileUtil.getDocumentDirectory("/ikinder");
    }

    public String getCacheDir() {

        String userId = this.getUserId();

        if (Utils.isNullOrEmpty(userId))
            return null;

        return String.format("%s/%s/%s/", this.getBaseDir(), userId, _dataCategory);
    }

    @SuppressLint("DefaultLocale")
    public void clearCacheForDataId(long dataId) {

        _dataStatus.remove(this.getDataId(dataId));
        _data.remove(this.getDataId(dataId));

        String cacheDir = this.getCacheDir();
        if (!Utils.isNullOrEmpty(cacheDir)) {

            cacheDir = String.format("%s%d.dat", cacheDir, dataId);
            FileUtil.removeFile(cacheDir);
        }
    }

    public void clearAllCache() {

        String cacheDir = this.getCacheDir();

        if (!Utils.isNullOrEmpty(cacheDir)) {

            FileUtil.removeFolder(cacheDir);
        }

        _dataStatus.clear();
        _data.clear();
    }

    public void saveCacheForDataId(long dataId) {

        String cacheDir = this.getCacheDir();

        if (Utils.isNullOrEmpty(cacheDir))
            return;

        FileUtil.createFolder(cacheDir);
        final String filePath = String.format("%s%d.dat", cacheDir, dataId);
        OTLog.i(this, filePath);

        final Object data = _data.get(this.getDataId(dataId));
        OTDataManager.getInstance().getThreadPool().submit(new Runnable() {

            @Override
            public void run() {

                String json = JSONValue.toJSONString(data);
                if (!FileUtil.writeStringToFile(json, filePath)) {
                    OTLog.i(this, "write cache failed!!!");
                }
            }
        });
    }

    public void saveCache() {

        this.saveCacheForDataId(OTConsts.KXDATA_ID_DEFAULT);
    }

    public void loadAllCache() {
        _data.clear();
        String userId = this.getUserId();

        if (Utils.isNullOrEmpty(userId))
            return;

        String cacheDir = String.format("%s/%s/%s/", this.getBaseDir(), userId, _dataCategory);
        File[] files = new File(cacheDir).listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File arg0, String arg1) {

                return arg1.toLowerCase().endsWith(".dat");
            }
        });

        if (files == null || files.length == 0)
            return;

        for (File file : files) {

            long dataId = Long.parseLong(FileUtil.getFileNameWithoutExtension(file));

            String dataCacheDir = String.format("%s/%d.dat", cacheDir, dataId);
            Object data = JSONValue.parse(FileUtil.readStringFromFile(dataCacheDir));

            if (data != null) {

                _data.put(getDataId(dataId), data);
            }
        }
    }

    public OTJson getOTJsonObjectForDataId(long dataId) {

        Object data = _data.get(this.getDataId(dataId));

        if (data == null) {

            try {

                data = this.getDataObjectClassForDataId(dataId).newInstance();
                _data.put(this.getDataId(dataId), data);

            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return OTJson.createJson(data);
    }

    public OTJson getOTJsonObject() {

        return this.getOTJsonObjectForDataId(OTConsts.KXDATA_ID_DEFAULT);
    }

    public int getDataCountForDataId(long dataId) {

        return this.getOTJsonObjectForDataId(dataId).count();
    }

    public int getDataCount() {

        return this.getDataCountForDataId(OTConsts.KXDATA_ID_DEFAULT);
    }

    public boolean canGetMoreForDataId(long dataId) {

        return this.getDataStatusForDataId(dataId).hasMore;
    }

    public boolean canGetMore() {

        return this.canGetMoreForDataId(OTConsts.KXDATA_ID_DEFAULT);
    }

    public int getPageSize() {

        return OTConsts.KXDATA_PAGESIZE_DEFAULT;
    }

    /*
     * 生成网络请求的 KXDataRequest, 子类需重写返回符合需求的 KXDataRequest
     */
    public OTDataRequest getDataRequestForTask(OTDataTask task) {

        OTDataRequest request = new OTDataRequest();
        request.task = task;
        request.httpMethod = HttpMethod.Get;

        return request;
    }

    /*
     * 针对任务设置缓存时间(秒)，两次请求的时候超过这个时间，则发请求
     */
    public int cacheIntervalForTask(OTDataTask task) {

        return 0;
    }

    public boolean needRequestDataForTask(OTDataTask task) {

        if (!isAutoCache() || !isAutoCacheForTask(task) || getDataCountForDataId(task.dataId) == 0) {
            return true;
        }

        OTDataStatus status = getDataStatusForDataId(task.dataId);
        long now = System.currentTimeMillis();

        if (now - status.timestamp < cacheIntervalForTask(task) * 1000) {

            status.onlineStatus = OTDataOnlineStatus.Cached;
            return false;
        }

        return true;
    }

    public boolean processHttpRequest(OTJson rawData, AsyncHttpRequest request) {

        boolean processed = true;
        OTDataTask task = ((OTDataRequest) request.userInfo.get(OTConsts.DATA_REQUEST)).task;

        if (rawData == null || rawData.json == null) {
            return false;
        } else {
            processed = processRawDataJson(rawData, task);
        }

        if (processed) {

            OTDataStatus dataStatus = getDataStatusForDataId(task.dataId);
            dataStatus.onlineStatus = OTDataOnlineStatus.Gained;
            dataStatus.timestamp = System.currentTimeMillis();
            _dataStatus.put(getDataId(task.dataId), dataStatus);

            if (isAutoCache() && isAutoCacheForTask(task)) {

                saveCacheForDataId(task.dataId);
            }
        }

        return processed;
    }

    public boolean processRawDataJson(OTJson rawData, OTDataTask task) {

        if (rawData == null || !(rawData.json instanceof HashMap))
            return false;

        OTJson json = rawData.getJsonForKey(OTConsts.DATA_REQUEST_KEY_RESULT);
        if (json.getIntForKey(OTConsts.DATA_REQUEST_KEY_RET) != OTConsts.DATA_REQUEST_RET_CODE_SUCCESS) {

            return false;
        }

        boolean processed = processJson(json, task);
        if (processed && (task.dataRequestType == OTConsts.KXDATA_REQUEST_REFRESH || task.dataRequestType == OTConsts.KXDATA_REQUEST_GETMORE)) {
            fillDataStatus(json, task.dataId);
        }

        return processed;
    }

    protected void fillDataStatus(OTJson json, long dataID) {

        if (json.haveIntValueForKey(OTConsts.DATA_REQUEST_KEY_TOTAL)) {
            this.getDataStatus().total = json.getIntForKey(OTConsts.DATA_REQUEST_KEY_TOTAL);
        }

        if (json.haveIntValueForKey(OTConsts.DATA_REQUEST_KEY_HASMORE)) {
            this.getDataStatus().hasMore = json.getBoolForKey(OTConsts.DATA_REQUEST_KEY_HASMORE);
        } else {
            this.getDataStatus().hasMore = getDataStatus().total > getDataCountForDataId(dataID);
        }
    }

    public boolean processJson(OTJson json, OTDataTask task) {
        if (json.haveStringValueForKey(OTConsts.DATA_REQUEST_KEY_TOTAL)) {

            OTDataStatus status = getDataStatusForDataId(task.dataId);
            status.total = json.getIntForKey(OTConsts.DATA_REQUEST_KEY_TOTAL);
        }
        return true;
    }

    public void onError(OTDataTask task, OTError error, String content, OTJson rawData) {

    }

    public void onSucceed(OTJson rawData, OTDataTask task) {

    }

    public OTUploadQueueItemDetailInfo getUploadQueueItemDetailInfoForTask(OTDataTask task) {
        OTUploadQueueItemDetailInfo info = new OTUploadQueueItemDetailInfo();
        info.mDescription = this.getClass().getName();
        info.mUploadTime = Long.parseLong(task.args.get(OTConsts.DATA_REQUEST_KEY_UPLOADTIME));
        info.mDataRequestType = task.dataRequestType;
        info.mItemId = task.args.get(OTConsts.DATA_REQUEST_KEY_UPLOADITEMID);
        return info;
    }

    public boolean shouldAddToFailedUploadQueueForTask(OTDataTask task, Throwable error) {
        return true;
    }

    public boolean isUploadQueueTask(OTDataTask task) {
        return false;
    }
}