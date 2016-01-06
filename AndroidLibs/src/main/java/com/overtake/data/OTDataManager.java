package com.overtake.data;

import java.util.Hashtable;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.overtake.data.OTBaseData;
import com.overtake.utils.Utils;

public final class OTDataManager {

    private static OTDataManager _manager;
    private final Hashtable<String, OTBaseData> _dataModule;
    private ThreadPoolExecutor _threadPool;
    private String _packageName;

    private OTDataManager() {

        _dataModule = new Hashtable<String, OTBaseData>();
    }

    public synchronized static OTDataManager getInstance() {

        if (_manager == null) {

            _manager = new OTDataManager();
        }

        return _manager;
    }

    public void initialize(String packageName) {
        _packageName = packageName;
    }

    public ThreadPoolExecutor getThreadPool() {

        if (_threadPool == null) {

            // 用于执行BaseData缓存写入操作的线程池
            _threadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        }

        return _threadPool;
    }

    public OTBaseData getDataForCategory(String dataCategory) {

        if (Utils.isNullOrEmpty(dataCategory))
            return null;
        OTBaseData data = _dataModule.get(dataCategory);

        if (data == null) {

            Class<?> clazz = null;
            try {

                clazz = Class.forName(_packageName + "." + dataCategory);

            } catch (ClassNotFoundException e) {

                e.printStackTrace();
            }

            try {

                data = (OTBaseData) clazz.newInstance();

            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            if (data != null) {

                _dataModule.put(dataCategory, data);
            }
        }

        return data;
    }

    public void removeDataForCategory(String dataCategory) {

        if (Utils.isNullOrEmpty(dataCategory))
            return;

        _dataModule.remove(dataCategory);
    }

    public void removeAll() {
        _dataModule.clear();
    }
}
