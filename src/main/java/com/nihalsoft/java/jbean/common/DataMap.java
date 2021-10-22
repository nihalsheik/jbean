package com.nihalsoft.java.jbean.common;

import java.util.HashMap;
import java.util.Map;

public class DataMap extends HashMap<String, Object> {

    private static final long serialVersionUID = 1L;

    public static DataMap create() {
        return new DataMap();
    }

    public static DataMap create(String key, Object value) {
        return DataMap.create().put(key, value);
    }
    
    public static DataMap from(Map<String, Object> data) {
        DataMap d = new DataMap();
        d.putAll(data);
        return d;
    }

    public DataMap put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public String getString(String key) {
        try {
            return this.get(key).toString();
        } catch (Exception ex) {
            return "";
        }
    }

    public Long getLong(String key) {
        try {
            return Long.valueOf(this.get(key).toString());
        } catch (Exception ex) {
            return 0L;
        }
    }

    public int getInt(String key) {
        try {
            return Integer.valueOf(this.get(key).toString());
        } catch (Exception ex) {
            return 0;
        }
    }

    public float getFloat(String key) {
        try {
            return Float.valueOf(this.get(key).toString());
        } catch (Exception ex) {
            return 0;
        }
    }

    public Object get(String key) {
        return super.get(key);
    }

    public Object get(String key, Object defaultValue) {
        if (this.containsKey(key)) {
            return super.get(key);
        } else {
            return defaultValue;
        }
    }

    public void remove(String... keys) {
        for (String key : keys) {
            this.remove(key);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        return (T) super.get(key);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        this.entrySet().forEach(a -> {
            sb.append(a.getKey()).append("=").append(a.getValue()).append(" ");
        });
        return sb.toString();
    }

    public Map<String, Object> toMap() {
        return this;
    }

}
