package com.overtake.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;

import org.json.simple.JSONValue;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings.Secure;

import com.overtake.base.OTConsts;
import com.overtake.base.OTError;
import com.overtake.base.OTJson;
import com.overtake.utils.OTLog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpRequest;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public final class OTRequestManager {

    public enum NetworkStatus {
        NotReachable, ReachableViaWiFi, ReachableViaMobile,
    }

    private static OTRequestManager _manager;

    // 存储每个数据分类的观察者列表
    private final HashMap<String, List<OTRequestObserver>> _dataObservers;

    // 全局的json数据处理勾子
    private final ArrayList<OTGlobalJsonHook> _globalJsonHooks;
    private final ArrayList<OTGlobalRequestHook> _globalRequestHooks;

    private final AsyncHttpClient _client;
    private String _apiUrlPrefix;
    private Context _context;

    public String identify = "";
    public String accessToken = "";

    private OTRequestManager() {
        _dataObservers = new HashMap<String, List<OTRequestObserver>>();
        _globalJsonHooks = new ArrayList<OTGlobalJsonHook>();
        _globalRequestHooks = new ArrayList<OTGlobalRequestHook>();
        _client = new AsyncHttpClient();
    }

    public void setRequestTimeOut(int milliseconds) {
        _client.setTimeout(milliseconds);
    }

    public synchronized static OTRequestManager getInstance() {
        if (_manager == null) {
            _manager = new OTRequestManager();
        }

        return _manager;
    }

    public void initialize(String apiPrefix, Context context) {
        _apiUrlPrefix = apiPrefix;
        _context = context;
    }

    public String getApiUrlPrefix() {
        return _apiUrlPrefix;
    }

    public AsyncHttpRequest getRequestForTask(OTDataTask task) {
        for (AsyncHttpRequest request : _client.httpRequests) {
            OTDataRequest dataRequest = (OTDataRequest) request.userInfo.get(OTConsts.DATA_REQUEST);
            if (dataRequest.task == task &&
                    dataRequest.task.dataCategory.equals(task.dataCategory) &&
                    dataRequest.task.dataRequestType == task.dataRequestType) {
                return request;
            }
        }
        return null;
    }

    public OTDataTask getTaskForItemId(String itemId) {
        for (AsyncHttpRequest request : _client.httpRequests) {
            OTDataRequest dataRequest = (OTDataRequest) request.userInfo.get(OTConsts.DATA_REQUEST);
            String requestItemIdString = dataRequest.task.args.get(OTConsts.DATA_REQUEST_KEY_UPLOADITEMID);
            if (requestItemIdString.equals(itemId)) {
                return dataRequest.task;
            }
        }
        return null;
    }

    public boolean addTask(OTDataTask task) {
        OTBaseData dataProvider = OTDataManager.getInstance().getDataForCategory(task.dataCategory);
        OTDataRequest dataRequest = dataProvider.getDataRequestForTask(task);
        if (dataRequest.task == null)
            dataRequest.task = task;

        if (!dataProvider.needRequestDataForTask(task)) {
            notifyRequestSuccessForTask(task);
            return false;
        }

        if (!dataRequest.url.startsWith("http://") && !dataRequest.url.startsWith("https://")) {
            dataRequest.url = _apiUrlPrefix + dataRequest.url;
        }

        ArrayList<AsyncHttpRequest> removedList = new ArrayList<AsyncHttpRequest>();
        for (AsyncHttpRequest request : _client.httpRequests) {
            if (dataRequest.task.dataRequestType == OTConsts.KXDATA_REQUEST_REFRESH) {
                OTDataRequest requestInQueue = (OTDataRequest) request.userInfo.get(OTConsts.DATA_REQUEST);
                if (requestInQueue.equals(dataRequest)) {
                    Future<?> future = request.futureRef.get();
                    if (future != null) {
                        future.cancel(true);
                        removedList.add(request);
                        OTLog.i(this, "remove : " + dataRequest.task.dataCategory + "" + dataRequest.task.dataId);
                    }
                }
            }
        }

        _client.httpRequests.removeAll(removedList);

        if (dataRequest.url.startsWith(OTConsts.DATA_REQUEST_KEY_LOCALHOST)) {

        } else {
            addRequestToQueue(dataRequest);
        }

        return false;
    }

    void addRequestToQueue(OTDataRequest dataRequest) {
        HashMap<String, String> params = new HashMap<String, String>();

        AsyncHttpRequest request = createRequest(dataRequest, params);
        if (request != null) {
            _client.httpRequests.add(request);
        }
        OTBaseData data = OTDataManager.getInstance().getDataForCategory(dataRequest.task.dataCategory);
        if (data.isUploadQueueTask(dataRequest.task))
            registerRequestObserver(OTUploadQueueItemsManager.getInstance(), dataRequest.task.dataCategory);
        notifyRequestAdded(dataRequest.task);
    }

    private AsyncHttpRequest createRequest(OTDataRequest request, HashMap<String, String> paramsMap) {
        RequestParams params = new RequestParams(paramsMap);
        AsyncHttpRequest httpRequest = request.httpMethod == HttpMethod.Get ? createGetRequest(request, params) : createPostRequest(request, params);
        httpRequest.userInfo = new Hashtable<String, Object>();
        httpRequest.userInfo.put(OTConsts.DATA_REQUEST, request);
        httpRequest.userInfo.put(OTConsts.DATA_REQUEST_KEY_TIME, String.valueOf(System.currentTimeMillis()));

        return httpRequest;
    }

    private AsyncHttpRequest createGetRequest(OTDataRequest request, RequestParams params) {
        if (request.requestParams != null) {
            for (String key : request.requestParams.keySet()) {
                params.put(key, String.valueOf(request.requestParams.get(key)));
            }
        }

        OTLog.i(this, request.url + "?" + params.toString());

        return _client.get(_context, request.url, params, createHandler());
    }

    private AsyncHttpRequest createPostRequest(OTDataRequest request, RequestParams params) {
        if (request.requestParams != null) {
            for (String key : request.requestParams.keySet()) {
                params.put(key, String.valueOf(request.requestParams.get(key)));
            }
        }

        if (request.dataParams != null) {
            for (String key : request.dataParams.keySet()) {
                Object value = request.dataParams.get(key);
                if (value instanceof File) {
                    try {
                        params.put(key, (File) value);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else if (value instanceof InputStream) {
                    params.put(key, (InputStream) value);
                }
            }
        }

        if (request.imageParams != null) {
            for (String key : request.imageParams.keySet()) {
                Object value = request.imageParams.get(key);
                if (value instanceof File) {
                    try {
                        params.put(key, (File) value);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else if (value instanceof String) {
                    try {
                        params.put(key, new File((String) value));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                } else if (value instanceof InputStream) {
                    params.put(key, (InputStream) value, key, "image/jpeg");
                }

            }
        }

        OTLog.i(this, request.url + "?" + params.toString());

        return _client.post(_context, request.url, params, createHandler());
    }

    private AsyncHttpResponseHandler createHandler() {
        return new AsyncHttpResponseHandler() {
            public void onSuccess(String content, AsyncHttpRequest request) {
                OTRequestManager.this.onSuccess(content, request);
            }

            public void onFailure(Throwable error, String content, AsyncHttpRequest request) {
                OTRequestManager.this.onFailure(error, content, request);
            }

            public void onProgress(long position, long total, AsyncHttpRequest request) {
                OTRequestManager.this.onProgress(position, total, request);
            }
        };
    }

    public void notifyRequestSuccessForTask(OTDataTask task) {
        List<OTRequestObserver> observers = _dataObservers.get(task.dataCategory);
        if (observers == null || observers.size() == 0)
            return;

        for (int i = 0; i < observers.size(); i++) {
            observers.get(i).requestSuccessForTask(task);
        }
    }

    public void notifyRequestFailedForTask(OTDataTask task, OTError error, String content, OTJson rawData) {
        OTBaseData dataProvider = OTDataManager.getInstance().getDataForCategory(task.dataCategory);
        dataProvider.onError(task, error, content, rawData);

        List<OTRequestObserver> observers = _dataObservers.get(task.dataCategory);
        if (observers == null || observers.size() == 0)
            return;

        for (int i = 0; i < observers.size(); i++) {
            observers.get(i).requestFailedForTask(task, error);
        }
    }

    public void notifyRequestDataModify(OTDataTask task) {
        List<OTRequestObserver> observers = _dataObservers.get(task.dataCategory);
        if (observers == null || observers.size() == 0)
            return;

        for (int i = 0; i < observers.size(); i++) {
            observers.get(i).requestDataModifyForTask(task);
        }
    }

    public void notifyRequestAdded(OTDataTask task) {
        List<OTRequestObserver> observers = _dataObservers.get(task.dataCategory);
        if (observers == null || observers.size() == 0)
            return;

        for (int i = 0; i < observers.size(); i++) {
            observers.get(i).taskAddedToRequestManager(task);
        }
    }

    public void registerRequestObserver(OTRequestObserver observer, String dataCategory) {
        List<OTRequestObserver> observers = _dataObservers.get(dataCategory);

        if (observers == null) {
            observers = new ArrayList<OTRequestObserver>();
            _dataObservers.put(dataCategory, observers);
        }

        boolean needAdd = true;
        for (OTRequestObserver observerInArray : observers) {
            if (observerInArray.equals(observer)) {
                needAdd = false;
                break;
            }
        }

        if (needAdd) {
            observers.add(observer);
        }
    }

    public void unregisterRequestObserver(OTRequestObserver observer, String dataCategory) {
        List<OTRequestObserver> observers = _dataObservers.get(dataCategory);

        if (observers != null) {
            observers.remove(observer);
        }
    }

    public void unregisterRequestObserver(OTRequestObserver observer) {
        Collection<List<OTRequestObserver>> values = _dataObservers.values();

        for (Iterator iterator = values.iterator(); iterator.hasNext(); ) {
            List<OTRequestObserver> list = (List<OTRequestObserver>) iterator.next();
            for (Iterator iterator2 = list.iterator(); iterator2.hasNext(); ) {
                OTRequestObserver kxRequestObserver = (OTRequestObserver) iterator2.next();
                if (observer.equals(kxRequestObserver)) {
                    iterator2.remove();
                }
            }
        }
    }

    /*
     * 注册及反注册全局json处理勾子
     */
    public void registerGlobalJsonHook(OTGlobalJsonHook jsonHook) {
        if (_globalJsonHooks.indexOf(jsonHook) == -1)
            _globalJsonHooks.add(jsonHook);
    }

    public void unregisterGlobalJsonHook(OTGlobalJsonHook jsonHook) {
        if (_globalJsonHooks.indexOf(jsonHook) != -1)
            _globalJsonHooks.remove(jsonHook);
    }

    public void clearAllGlobalJsonHooks() {
        _globalJsonHooks.clear();
    }

    public void registerGlobalRequestHook(OTGlobalRequestHook jsonHook) {
        if (_globalRequestHooks.indexOf(jsonHook) == -1)
            _globalRequestHooks.add(jsonHook);
    }

    public void unregisterGlobalRequestHook(OTGlobalRequestHook jsonHook) {
        if (_globalRequestHooks.indexOf(jsonHook) != -1)
            _globalRequestHooks.remove(jsonHook);
    }

    public void clearAllGlobalRequestHooks() {
        _globalRequestHooks.clear();
    }

    public void cancelAllTasksBySender(Object sender) {
        ArrayList<AsyncHttpRequest> allRequests = _client.httpRequests;
        ArrayList<AsyncHttpRequest> removedList = new ArrayList<AsyncHttpRequest>();
        for (AsyncHttpRequest request : allRequests) {
            OTDataRequest dataRequest = (OTDataRequest) request.userInfo.get(OTConsts.DATA_REQUEST);
            if (dataRequest.task.senders.contains(sender)) {
                dataRequest.task.senders.remove(sender);
                if (dataRequest.task.senders.size() == 0) {
                    Future<?> future = request.futureRef.get();
                    if (future != null) {
                        future.cancel(true);
                    }
                    removedList.add(request);
                }
            }
        }

        _client.httpRequests.removeAll(removedList);
    }

    public void cancelAllRequest() {
        ArrayList<AsyncHttpRequest> allRequests = _client.httpRequests;
        ArrayList<AsyncHttpRequest> removedList = new ArrayList<AsyncHttpRequest>();

        for (AsyncHttpRequest request : allRequests) {
            Future<?> future = request.futureRef.get();
            if (future != null) {
                future.cancel(true);
                removedList.add(request);
            }
        }
        for (AsyncHttpRequest request : removedList) {
            _client.httpRequests.remove(request);
            OTDataRequest dataRequest = (OTDataRequest) request.userInfo.get(OTConsts.DATA_REQUEST);
            notifyRequestFailedForTask(dataRequest.task, new OTError("Error", "请求已取消", 0), "", new OTJson());
        }
    }

    public void cancelTask(OTDataTask task, OTDataTask.ITaskComparator comparator) {
        ArrayList<AsyncHttpRequest> removedList = new ArrayList<AsyncHttpRequest>();
        for (AsyncHttpRequest request : _client.httpRequests) {
            OTDataRequest requestInQueue = (OTDataRequest) request.userInfo.get(OTConsts.DATA_REQUEST);
            if (comparator != null && comparator.isEquals(task, requestInQueue.task)) {
                Future<?> future = request.futureRef.get();
                if (future != null) {
                    future.cancel(true);
                    removedList.add(request);
                }
            }
        }

        _client.httpRequests.removeAll(removedList);
    }

    public void onProgress(long position, long total, AsyncHttpRequest request) {
        OTDataRequest dataRequest = (OTDataRequest) request.userInfo.get(OTConsts.DATA_REQUEST);
        OTDataTask task = dataRequest.task;
        if (task.uploadProgressHandler != null) {
            task.uploadProgressHandler.updateProgress(position, total);
        }
    }

    public void onSuccess(String content, AsyncHttpRequest request) {
        OTDataRequest dataRequest = (OTDataRequest) request.userInfo.get(OTConsts.DATA_REQUEST);
        OTBaseData dataProvider = OTDataManager.getInstance().getDataForCategory(dataRequest.task.dataCategory);
        if (dataProvider == null) {
            _client.httpRequests.remove(request);
        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new DataPreProcessor(dataProvider, content, request).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                new DataPreProcessor(dataProvider, content, request).execute();
            }

        }
    }

    public void onFailure(Throwable error, String content, AsyncHttpRequest request) {
        OTDataRequest dataRequest = (OTDataRequest) request.userInfo.get(OTConsts.DATA_REQUEST);
        notifyRequestFailedForTask(dataRequest.task, new OTError("Error", "网络链接错误", 0), content, new OTJson());
        _client.httpRequests.remove(request);
    }

    public NetworkStatus getNetworkReachableType() {

        ConnectivityManager manager = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {

            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                return NetworkStatus.ReachableViaMobile;
            } else {
                return NetworkStatus.ReachableViaWiFi;
            }
        }

        return NetworkStatus.NotReachable;
    }

    private class DataPreProcessor extends AsyncTask<Void, Void, Boolean> {

        private String mContent;
        private AsyncHttpRequest mRequest;
        private OTBaseData mDataProvider;
        private Object jsonObject;
        private OTJson rawData;

        public DataPreProcessor(OTBaseData dataProvider, String content, AsyncHttpRequest request) {
            mDataProvider = dataProvider;
            mContent = content;
            mRequest = request;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (mContent != null && mContent.length() > 0) {
                jsonObject = JSONValue.parse(mContent);
            }

            rawData = OTJson.createJson(jsonObject);

            OTDataRequest dataRequest = (OTDataRequest) mRequest.userInfo.get(OTConsts.DATA_REQUEST);
            OTBaseData dataProvider = OTDataManager.getInstance().getDataForCategory(dataRequest.task.dataCategory);

            if (rawData != null) {
                OTLog.i("request finished:", dataProvider + rawData.toString());
            }

            if (dataProvider == null) {
                _client.httpRequests.remove(mRequest);
                return false;
            }
            boolean processed = dataProvider.processHttpRequest(rawData, mRequest);
            return processed;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            OTDataRequest dataRequest = (OTDataRequest) mRequest.userInfo.get(OTConsts.DATA_REQUEST);

            OTJson json = rawData.getJsonForKey(OTConsts.DATA_REQUEST_KEY_RESULT);

            boolean isGoOn = true;
            for (Iterator<OTGlobalRequestHook> iterator = _globalRequestHooks.iterator(); iterator.hasNext(); ) {
                OTGlobalRequestHook hook = iterator.next();
                if (!hook.globalRequestHook(json, dataRequest.task)) {
                    isGoOn = false;
                }
            }

            if (!isGoOn) return;

            if (result) {
                for (Iterator<OTGlobalJsonHook> iterator = _globalJsonHooks.iterator(); iterator.hasNext(); ) {
                    OTGlobalJsonHook hook = iterator.next();
                    hook.globalJsonHook(json, dataRequest.task);
                }

                notifyRequestSuccessForTask(dataRequest.task);
                mDataProvider.onSucceed(json, dataRequest.task);

            } else {
                final String errorDomain = "Error";
                OTError error;
                if (jsonObject == null) {
                    error = new OTError(errorDomain, "网络链接错误", 0);
                } else {
                    int ret = json.getJsonForKey(OTConsts.DATA_REQUEST_KEY_DATA).getIntForKey(OTConsts.DATA_REQUEST_KEY_ERRNO);
                    if (ret == 0) {
                        error = new OTError(errorDomain, "json数据不合法，json:" + json.toString(), 0);
                    } else {
                        error = new OTError(errorDomain, json.getJsonForKey(OTConsts.DATA_REQUEST_KEY_DATA).getStringForKey(OTConsts.DATA_REQUEST_KEY_MSG), ret);
                    }
                }
                notifyRequestFailedForTask(dataRequest.task, error, mContent, json);
                mDataProvider.onError(dataRequest.task, error, mContent, json);
            }
            _client.httpRequests.remove(mRequest);
        }
    }
}
