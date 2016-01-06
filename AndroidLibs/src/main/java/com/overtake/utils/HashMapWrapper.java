package com.overtake.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.json.simple.JSONValue;

public class HashMapWrapper {

    private HashMap<String, Object> mData;

    public HashMapWrapper(HashMap<String, Object> data) {
        mData = data;
    }

    public HashMapWrapper() {
        mData = new HashMap<String, Object>();
    }

    public HashMapWrapper put(String key, Object value) {
        mData.put(key, value);
        return this;
    }

    public Object get(String key) {
        return mData.get(key);
    }

    @SuppressWarnings("unchecked")
    public HashMapWrapper putAll(Object map) {
        mData.putAll((HashMap<String, Object>) map);
        return this;
    }

    @SuppressWarnings("unchecked")
    public HashMapWrapper getChild(String key) {
        if (mData.containsKey(key)) {
            Object childData = mData.get(key);
            if (childData instanceof HashMapWrapper) {
                return (HashMapWrapper) childData;
            } else if (childData instanceof HashMap) {
                HashMapWrapper child = new HashMapWrapper((HashMap<String, Object>) childData);
                return child;
            } else {
                throw new IllegalAccessError("this child elment can not be accessed");
            }
        } else {
            HashMapWrapper child = new HashMapWrapper();
            mData.put(key, child);
            return child;
        }
    }

    @SuppressWarnings("rawtypes")
    public HashMap<String, Object> getData() {
        Iterator iter = mData.entrySet().iterator();
        HashMap<String, Object> data = new HashMap<String, Object>();
        while (iter.hasNext()) {
            Entry entry = (Entry) iter.next();
            String key = (String) entry.getKey();
            Object val = entry.getValue();
            if (val instanceof HashMapWrapper) {
                val = ((HashMapWrapper) val).getData();
            }
            data.put(key, val);
        }
        return data;
    }

    public String toJsonString() {
        return JSONValue.toJSONString(getData());
    }
}
