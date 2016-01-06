package com.overtake.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONValue;

import com.overtake.utils.OTLog;
import com.overtake.utils.Utils;

public class OTJson {

    public Object json;

    public static OTJson createJson(Object o) {

        OTJson json = new OTJson();

        if (o instanceof Map || o instanceof List) {

            json.json = o;
        }

        return json;
    }

    public OTJson getJsonForKey(String key) {

        Object ret = null;

        if (this.json instanceof Map) {

            ret = ((Map) this.json).get(key);
        }

        if (ret == null) {

            ret = new HashMap();
        }

        return OTJson.createJson(ret);
    }

    public OTJson getJsonForIndex(int index) {

        Object ret = null;

        if (this.json instanceof List) {

            List list = (List) this.json;

            if (list != null && list.size() > index) {

                ret = list.get(index);
            }
        }

        if (ret == null) {

            ret = new ArrayList();
        }

        return OTJson.createJson(ret);
    }

    public boolean haveStringValueForKey(String key) {

        if (this.json instanceof Map) {

            Map jsonObject = (Map) this.json;
            Object ret = jsonObject.get(key);

            if (ret == null)
                return false;
            return ret instanceof String;
        }

        return false;
    }

    public boolean haveIntValueForKey(String key) {

        if (this.json instanceof Map) {

            Map jsonObject = (Map) this.json;
            Object ret = jsonObject.get(key);

            if (ret == null)
                return false;
            return ret instanceof Integer || ret instanceof String;
        }

        return false;
    }

    public boolean haveBoolValueForKey(String key) {

        if (this.json instanceof Map) {

            Map jsonObject = (Map) this.json;
            Object ret = jsonObject.get(key);

            if (ret == null)
                return false;
            return ret instanceof Boolean || ret instanceof String;
        }

        return false;
    }

    public int getIntForKey(String key) {
        String value = getStringForKey(key);
        if (Utils.isNullOrEmpty(value))
            return 0;

        int val = 0;

        try {
            val = Integer.parseInt(value);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return val;
    }

    public boolean getBoolForKey(String key) {

        String value = getStringForKey(key);

        if (Utils.isNullOrEmpty(value))
            return false;
        return Boolean.valueOf(value);
    }

    public long getLongForKey(String key) {

        String value = getStringForKey(key);

        if (Utils.isNullOrEmpty(value))
            return 0;

        return Long.parseLong(value);
    }

    public String getStringForKey(String key) {

        if (this.json instanceof Map) {

            Map jsonObject = (Map) this.json;
            Object value = jsonObject.get(key);

            if (value == null)
                return "";
            return String.valueOf(value);
        }

        return "";
    }

    public void putForKey(String key, Object value) {
        if (this.json instanceof Map) {
            Map jsonObject = (Map) this.json;
            jsonObject.put(key, value);
        }
    }

    public void putForIndex(int index, Object value) {
        if (this.json instanceof List) {
            List jsonObject = (List) this.json;
            jsonObject.add(index, value);
        }
    }

    public String getStringForIndex(int index) {

        String ret = "";
        if (this.json instanceof List) {

            List array = (List) this.json;
            if (array.size() > index) {

                Object value = array.get(index);
                if (value != null)
                    ret = String.valueOf(value);
            }
        }

        return ret;
    }

    public int getIntForIndex(int index) {
        String string = this.getStringForIndex(index);
        if (Utils.isNullOrEmpty(string))
            return 0;
        return Integer.parseInt(string);
    }

    public boolean getBoolForIndex(int index) {

        return Boolean.parseBoolean(getStringForIndex(index));
    }

    public long getLongForIndex(int index) {

        return Long.parseLong(getStringForIndex(index));
    }

    public int count() {

        if (this.json == null)
            return 0;
        if (this.json instanceof Map)
            return ((Map) this.json).size();
        if (this.json instanceof ArrayList)
            return ((List) this.json).size();
        return 0;
    }

    @Override
    public String toString() {
        return JSONValue.toJSONString(this.json);
    }

    public void printJson() {

        OTLog.i(this, this.toString());
    }

    public long hash() {

        return this.json.hashCode();
    }
}
