package de.ui.statistics;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Preferences;

/**
 * Simple in-memory mock for libGDX Preferences.
 * Allows checking which values were set and retrieved.
 */
public class MockPreferences implements Preferences {
    private final Map<String, Object> values = new HashMap<>();

    MockPreferences() {}

    @Override
    public Preferences putInteger(String key, int val) {
        values.put(key, val);
        return this;
    }

    @Override
    public int getInteger(String key, int defValue) {
        return values.containsKey(key) ? (Integer) values.get(key) : defValue;
    }

    @Override
    public int getInteger(String key) {
        return getInteger(key, 0);
    }

    @Override
    public Preferences putLong(String key, long val) {
        values.put(key, val);
        return this;
    }

    @Override
    public long getLong(String key, long defValue) {
        return values.containsKey(key) ? (Long) values.get(key) : defValue;
    }

    @Override
    public long getLong(String key) {
        return getLong(key, 0L);
    }

    @Override
    public Preferences putFloat(String key, float val) {
        values.put(key, val);
        return this;
    }

    @Override
    public float getFloat(String key, float defValue) {
        return values.containsKey(key) ? (Float) values.get(key) : defValue;
    }

    @Override
    public float getFloat(String key) {
        return getFloat(key, 0f);
    }

    @Override
    public Preferences putBoolean(String key, boolean val) {
        values.put(key, val);
        return this;
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return values.containsKey(key) ? (Boolean) values.get(key) : defValue;
    }

    @Override
    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    @Override
    public Preferences putString(String key, String val) {
        values.put(key, val);
        return this;
    }

    @Override
    public String getString(String key, String defValue) {
        return values.containsKey(key) ? (String) values.get(key) : defValue;
    }

    @Override
    public String getString(String key) {
        return getString(key, "");
    }

    @Override
    public Preferences put(Map<String, ?> vals) {
        values.putAll(vals);
        return this;
    }

    @Override
    public boolean contains(String key) {
        return values.containsKey(key);
    }

    @Override
    public void remove(String key) {
        values.remove(key);
    }

    @Override
    public void clear() {
        values.clear();
    }

    @Override
    public void flush() {
        // No-op for in-memory mock
    }

    @Override
    public Map<String, ?> get() {
        return new HashMap<>(values);
    }
}