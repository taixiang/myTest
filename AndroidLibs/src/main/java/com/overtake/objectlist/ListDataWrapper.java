package com.overtake.objectlist;

import java.util.ArrayList;
import java.util.HashMap;

import com.overtake.base.OTJson;
import com.overtake.objectlist.ListDataProvider.ListDataType;
import com.overtake.utils.OTLog;

/**
 * A wrapper of the list data which contains a dataList. It provides some
 * methods to access the datalist easily.
 *
 * @author lhq
 */
public class ListDataWrapper {

    @SuppressWarnings("rawtypes")
    public ArrayList dataList;
    public Boolean hasMore = false;

    private HashMap<String, Object> mRawHashMapData;
    public ListDataType dataType;

    @SuppressWarnings("rawtypes")
    public ListDataWrapper() {
        this.dataList = new ArrayList();
    }

    public int count() {
        return dataList.size();
    }

    @SuppressWarnings("unchecked")
    public void updateFrom(OTJson json) {
        mRawHashMapData = (HashMap<String, Object>) json.json;

        dataList = (ArrayList<?>) json.getJsonForKey("list").json;

        hasMore = json.getBoolForKey("has_more");
    }

    public void setLastStart(int lastStart) {
        mRawHashMapData.put("last_start", lastStart);
    }

    @SuppressWarnings("unchecked")
    public void initByData(ArrayList<?> dataList, Boolean hasMore) {
        this.dataList.clear();
        this.dataList.addAll(dataList);

        mRawHashMapData.put("has_more", hasMore);
        mRawHashMapData.put("last_start", 0);

        this.hasMore = hasMore;
    }

    @SuppressWarnings("unchecked")
    public void addData(int index, ArrayList<?> dataList, Boolean hasMore) {
        this.dataList.addAll(index, dataList);
        this.hasMore = hasMore;
    }

    @SuppressWarnings("unchecked")
    public void addData(ArrayList<?> dataList, Boolean hasMore) {
        this.dataList.addAll(dataList);
        this.hasMore = hasMore;
    }

    public OTJson getItem(int index) {
        if (index >= dataList.size()) {
            return null;
        }
        return OTJson.createJson(dataList.get(index));
    }

    public HashMap<String, Object> getRawHashMapData(){
        return mRawHashMapData;
    }

    public void clear() {
        dataList.clear();
    }
}