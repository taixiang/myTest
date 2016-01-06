package com.overtake.data;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONValue;

import android.annotation.SuppressLint;
import android.os.Handler;

import com.overtake.base.OTConsts;
import com.overtake.data.OTUploadQueueItem.UploadQueueItemListener;
import com.overtake.data.OTUploadQueueItem.UploadQueueItemStatus;
import com.overtake.utils.FileUtil;
import com.overtake.utils.OTLog;

import junit.framework.Assert;

@SuppressLint("DefaultLocale")
@SuppressWarnings("unchecked")
public class OTUploadQueueItemsManager implements OTRequestObserver {

    public interface KXUploadQueueItemsManagerDelegate {

        public void uploadQueueItemsManagerDidInitialize();

        public void uploadQueueItemsManagerDidItemChangeWithItemId(String itemId);
    }

    public enum UploadQueueStatus {

        UploadQueueStatusIdle, UploadQueueStatusRunning, UploadQueueStatusStop;
    }

    public static final String KEY_WAITING_UPLOAD_ITEMS = "WaitingUploadItems";
    public static final String KEY_FAILED_UPLOAD_ITEMS = "failedUploadItems";

    public static final int UPLOAD_ITEM_ID_INVALID = 0;

    public ArrayList<OTUploadQueueItem> mUploadQueueItems;
    public HashMap<String, Object> mUploadQueueItemsStatus;
    public boolean mIsInitialized;
    public KXUploadQueueItemsManagerDelegate mDelegate;
    public UploadQueueStatus mStatus;
    public String mCurrentItem;

    private static OTUploadQueueItemsManager mQueue;

    private static String mCachedDir;

    public UploadQueueItemListener itemListener;

    Runnable saveRunnable = new Runnable() {

        @Override
        public void run() {
            saveUploadQueueItemsStatus();
        }
    };

    Runnable loadRunnable = new Runnable() {

        @Override
        public void run() {
            loadUploadQueueItemsStatus();
        }
    };

    public OTUploadQueueItemsManager() {
        mUploadQueueItemsStatus = new HashMap<String, Object>();
        mUploadQueueItems = new ArrayList<OTUploadQueueItem>();
    }

    public synchronized static OTUploadQueueItemsManager getInstance() {
        if (mQueue == null) {
            mQueue = new OTUploadQueueItemsManager();
        }
        return mQueue;
    }

    public void start(String cachedDir) {
        mCachedDir = cachedDir;
        loadRunnable.run();
    }

    private void saveUploadQueueItemsStatus() {
        String dataCacheDir = String.format("%s%s/%s", getCacheDir(), "UploadItems", "UploadQueueItemsStatus.dat");
        String json = JSONValue.toJSONString(mUploadQueueItemsStatus);
        if (!FileUtil.writeStringToFile(json, dataCacheDir)) {
            OTLog.i(this, "write cache failed!!!");
        }
    }

    private void loadUploadQueueItemsStatus() {

        String dataCacheDir = String.format("%s%s/%s", getCacheDir(), "UploadItems", "UploadQueueItemsStatus.dat");
        try {

            mUploadQueueItemsStatus = (HashMap<String, Object>) JSONValue.parse(FileUtil.readStringFromFile(dataCacheDir));

        } catch (Exception e) {
            FileUtil.removeFile(dataCacheDir);

        }

        if (null == mUploadQueueItemsStatus.get(KEY_FAILED_UPLOAD_ITEMS)) {
            mUploadQueueItemsStatus.put(KEY_FAILED_UPLOAD_ITEMS, new ArrayList<String>());
        }
        if (null == mUploadQueueItemsStatus.get(KEY_WAITING_UPLOAD_ITEMS)) {
            mUploadQueueItemsStatus.put(KEY_WAITING_UPLOAD_ITEMS, new ArrayList<String>());
        }

        mUploadQueueItems.clear();
        mIsInitialized = true;
        continueUploadWaitingTask();
        if (mDelegate != null)
            mDelegate.uploadQueueItemsManagerDidInitialize();
    }

    public String getCacheDir() {
        Assert.assertNotNull("mCachedDir should be config", mCachedDir);
        return String.format("%s/%s/%s/", mCachedDir, "0000", "QueueManager");
    }

    private void continueUploadWaitingTask() {
        mStatus = UploadQueueStatus.UploadQueueStatusIdle;
        ArrayList<String> waitingList = (ArrayList<String>) mUploadQueueItemsStatus.get(KEY_WAITING_UPLOAD_ITEMS);
        if (waitingList != null) {
            try {
                String itemId = waitingList.get(0);
                String filePath = String.format("%sUploadItems/%s.dat", getCacheDir(), itemId);
                HashMap<String, Object> uploadItem = (HashMap<String, Object>) JSONValue.parse(FileUtil.readStringFromFile(filePath));
                if (uploadItem != null) {
                    OTDataTask task = OTDataTask.taskWithInfo(uploadItem);
                    mCurrentItem = itemId;
                    addItemToUploadQueueItems(itemId, task);
                    addTask(task);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @SuppressLint("DefaultLocale")
    private int generateUniqueUploadQueueItemId() {
        int itemId = 0;
        int i = 1;
        String filePath = "";
        do {
            filePath = String.format("%sUploadItems/%04d.dat", getCacheDir(), i++);
        } while (FileUtil.fileExist(filePath));

        itemId = i - 1;
        return itemId;
    }

    public void addTask(OTDataTask task) {
        if (mStatus == UploadQueueStatus.UploadQueueStatusIdle) {
            mStatus = UploadQueueStatus.UploadQueueStatusRunning;
            OTRequestManager.getInstance().setRequestTimeOut(60 * 1000);
            OTRequestManager.getInstance().addTask(task);
        } else {
            uploadQueueItemAddedToQueueWaited(task);
        }
    }

    @SuppressLint("DefaultLocale")
    private void uploadQueueItemAddedToQueueWaited(OTDataTask task) {
        ArrayList<String> waitings = (ArrayList<String>) mUploadQueueItemsStatus.get(KEY_WAITING_UPLOAD_ITEMS);
        if (null == waitings) {
            waitings = new ArrayList<String>();
            mUploadQueueItemsStatus.put(KEY_WAITING_UPLOAD_ITEMS, waitings);
        }
        int itemId = generateUniqueUploadQueueItemId();

        if (itemId != UPLOAD_ITEM_ID_INVALID) {
            String uploadId = String.format("%04d", itemId);
            task.args.put(OTConsts.DATA_REQUEST_KEY_UPLOADTIME, String.valueOf(System.currentTimeMillis()));
            task.args.put(OTConsts.DATA_REQUEST_KEY_UPLOADITEMID, uploadId);

            HashMap<String, Object> uploadItem = task.convert2Dictionary();

            String filePath = String.format("%sUploadItems/%s.dat", getCacheDir(), uploadId);
            String json = JSONValue.toJSONString(uploadItem);
            if (!FileUtil.writeStringToFile(json, filePath)) {

                OTLog.i(this, "write cache failed!!!");
            }
            waitings.add(uploadId);
            saveUploadQueueItemsStatus();
        }
    }

    private void uploadQueueItemAddedToQueue(OTDataTask task) {
        ArrayList<String> waitings = (ArrayList<String>) mUploadQueueItemsStatus.get(KEY_WAITING_UPLOAD_ITEMS);
        ArrayList<String> faileds = (ArrayList<String>) mUploadQueueItemsStatus.get(KEY_FAILED_UPLOAD_ITEMS);
        String itemId = task.args.get(OTConsts.DATA_REQUEST_KEY_UPLOADITEMID);

        task.args.put(OTConsts.DATA_REQUEST_KEY_UPLOADTIME, String.valueOf(System.currentTimeMillis()));

        if (itemId != null) {
            faileds.remove(itemId);
            if (!waitings.contains(itemId)) {
                waitings.add(itemId);
                saveUploadQueueItemsStatus();
            }
        } else {
            int tempId = generateUniqueUploadQueueItemId();
            if (tempId != UPLOAD_ITEM_ID_INVALID) {
                itemId = String.format("%04d", tempId);
                task.args.put(OTConsts.DATA_REQUEST_KEY_UPLOADITEMID, itemId);
                HashMap<String, Object> uploadItem = task.convert2Dictionary();
                String filePath = String.format("%sUploadItems/%s.dat", getCacheDir(), itemId);
                String json = JSONValue.toJSONString(uploadItem);
                if (!FileUtil.writeStringToFile(json, filePath)) {

                    OTLog.i(this, "write cache failed!!!");
                }
                waitings.add(itemId);
                saveUploadQueueItemsStatus();
            }
        }
        addItemToUploadQueueItems(itemId, task);
    }

    private OTUploadQueueItemDetailInfo getDetailInfoForTask(OTDataTask task) {
        if (task != null) {
            OTBaseData data = OTDataManager.getInstance().getDataForCategory(task.dataCategory);
            OTUploadQueueItemDetailInfo info = data.getUploadQueueItemDetailInfoForTask(task);
            return info;
        }
        return null;
    }

    public void addItemToUploadQueueItems(String itemId, OTDataTask task) {
        boolean find = false;
        for (OTUploadQueueItem item : mUploadQueueItems) {
            if (item.mItemId.equals(itemId)) {
                item.mStatus = UploadQueueItemStatus.UploadQueueItemStatusWaiting;
                task.uploadProgressHandler = item;
                find = true;
            }
        }

        if (!find) {
            OTUploadQueueItem item = new OTUploadQueueItem(itemId, UploadQueueItemStatus.UploadQueueItemStatusWaiting);
            item.itemListener = itemListener;
            item.mDetailInfo = getDetailInfoForTask(task);
            task.uploadProgressHandler = item;

            mUploadQueueItems.add(item);
        }
        if (null != mDelegate)
            mDelegate.uploadQueueItemsManagerDidItemChangeWithItemId(itemId);
    }

    public OTUploadQueueItemDetailInfo getDetailInfoWithUploadQueueItemId(String itemId) {
        OTDataTask task = getTaskWithUploadQueueItemId(itemId);
        return getDetailInfoForTask(task);
    }

    public int getUploadItemCount(){
        ArrayList<String> waitings = (ArrayList<String>) mUploadQueueItemsStatus.get(KEY_WAITING_UPLOAD_ITEMS);
        ArrayList<String> faileds = (ArrayList<String>) mUploadQueueItemsStatus.get(KEY_FAILED_UPLOAD_ITEMS);
        return waitings.size() + faileds.size();
    }

    public ArrayList<String> getUploadItemIds(){
        ArrayList<String> waitings = (ArrayList<String>) mUploadQueueItemsStatus.get(KEY_WAITING_UPLOAD_ITEMS);
        ArrayList<String> faileds = (ArrayList<String>) mUploadQueueItemsStatus.get(KEY_FAILED_UPLOAD_ITEMS);
        ArrayList<String> tmp = new ArrayList<String>();
        tmp.addAll(waitings);
        tmp.addAll(faileds);
        return tmp;
    }

    public OTDataTask getTaskWithUploadQueueItemId(String itemId) {
        OTDataTask task = OTRequestManager.getInstance().getTaskForItemId(itemId);
        if (task == null) {
            String filePath = String.format("%sUploadItems/%s.dat", getCacheDir(), itemId);
            HashMap<String, Object> uploadItem = (HashMap<String, Object>) JSONValue.parse(FileUtil.readStringFromFile(filePath));
            if (uploadItem != null) {
                task = OTDataTask.taskWithInfo(uploadItem);
            }
        }

        return task;
    }

    public void clearUploadQueueItemWithItemId(String itemId) {

        OTDataTask task = getTaskWithUploadQueueItemId(itemId);
        removeUploadQueueItemStatus(KEY_WAITING_UPLOAD_ITEMS, itemId);
        removeUploadQueueItemStatus(KEY_FAILED_UPLOAD_ITEMS, itemId);

        if (cancelRequestForTask(task)) {
            continueUploadWaitingTask();
        }
    }

    private boolean cancelRequestForTask(OTDataTask task) {

        return true;
    }

    private void removeUploadQueueItemStatus(String status, String itemId) {

        String filePath = String.format("%sUploadItems/%s.dat", getCacheDir(), itemId);
        FileUtil.removeFile(filePath);

        ArrayList<String> data = (ArrayList<String>) mUploadQueueItemsStatus.get(status);
        if (data != null) {
            if (data.contains(itemId)) {
                data.remove(itemId);
                saveUploadQueueItemsStatus();
                if (mDelegate != null)
                    mDelegate.uploadQueueItemsManagerDidItemChangeWithItemId(itemId);
            }
            removeUploadQueueItemWithItemId(itemId);
        }
    }

    private void removeUploadQueueItemWithItemId(String itemId) {
        for (OTUploadQueueItem item : mUploadQueueItems) {
            if (item.mItemId.equals(itemId)) {
                mUploadQueueItems.remove(item);
                if (mDelegate != null)
                    mDelegate.uploadQueueItemsManagerDidItemChangeWithItemId(itemId);
                break;
            }
        }
    }

    public void clearProgressDelegateWithItemId(String itemId) {
        OTDataTask task = getTaskWithUploadQueueItemId(itemId);
        task.uploadProgressHandler = null;
    }

    private OTUploadQueueItem getUploadQueueItemWithItemId(String itemId) {
        for (OTUploadQueueItem item : mUploadQueueItems) {
            if (item.mItemId.equals(itemId))
                return item;
        }
        return null;
    }

    public void retryFailedUploadItemWithItemId(String itemId) {
        ArrayList<String> faileds = (ArrayList<String>) mUploadQueueItemsStatus.get(KEY_FAILED_UPLOAD_ITEMS);
        if(faileds.contains(itemId)){
            uploadQueueItemAddedToQueue(getTaskWithUploadQueueItemId(itemId));
            if(mStatus == UploadQueueStatus.UploadQueueStatusIdle)
                continueUploadWaitingTask();
        }
    }

    // pragma mark - KXRequestObserver
    public void taskAddedToRequestManager(OTDataTask task) {
        uploadQueueItemAddedToQueue(task);
    }

    public void requestSuccessForTask(OTDataTask task) {
        OTRequestManager.getInstance().setRequestTimeOut(10 * 1000);
        String itemId = task.args.get(OTConsts.DATA_REQUEST_KEY_UPLOADITEMID);
        if (itemId != null) {

            OTUploadQueueItem item = getUploadQueueItemWithItemId(itemId);

            mStatus = UploadQueueStatus.UploadQueueStatusIdle;
            removeUploadQueueItemStatus(KEY_WAITING_UPLOAD_ITEMS, itemId);
            continueUploadWaitingTask();

            item.onSucceed();
        }
    }

    private void moveUploadQueueItemToFailedStatus(OTDataTask task) {

        String itemId = task.args.get(OTConsts.DATA_REQUEST_KEY_UPLOADITEMID);
        if (itemId != null) {
            ArrayList<String> faileds = (ArrayList<String>) mUploadQueueItemsStatus.get(KEY_FAILED_UPLOAD_ITEMS);

            if (faileds != null) {
                ArrayList<String> waiting = (ArrayList<String>) mUploadQueueItemsStatus.get(KEY_WAITING_UPLOAD_ITEMS);
                faileds.remove(itemId);
                faileds.add(itemId);
                waiting.remove(itemId);
                saveUploadQueueItemsStatus();

                for (OTUploadQueueItem item : mUploadQueueItems) {
                    if (item.mItemId.equals(itemId)) {
                        item.mStatus = UploadQueueItemStatus.UploadQueueItemStatusFailed;
                        task.uploadProgressHandler = null;
                        break;
                    }
                }
                if (mDelegate != null)
                    mDelegate.uploadQueueItemsManagerDidItemChangeWithItemId(itemId);

            }
        }
    }

    @Override
    public void requestFailedForTask(OTDataTask task, Throwable error) {
        OTRequestManager.getInstance().setRequestTimeOut(10 * 1000);
        OTBaseData data = OTDataManager.getInstance().getDataForCategory(task.dataCategory);
        if (data.shouldAddToFailedUploadQueueForTask(task, error)) {
            mStatus = UploadQueueStatus.UploadQueueStatusIdle;

            String itemId = task.args.get(OTConsts.DATA_REQUEST_KEY_UPLOADITEMID);
            OTUploadQueueItem item = getUploadQueueItemWithItemId(itemId);
            item.onFailed();

            moveUploadQueueItemToFailedStatus(task);
            continueUploadWaitingTask();
        }
    }

    @Override
    public void requestDataModifyForTask(OTDataTask task) {

    }

}
