package com.darodev.thuglifephotoeditor.utility;

import android.content.SharedPreferences;
import android.content.res.Resources;

import com.darodev.thuglifephotoeditor.R;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Dariusz Lelek on 10/31/2017.
 * dariusz.lelek@gmail.com
 */

public class ConfigUtility {
    private final Resources resources;
    private final SharedPreferences preferences;
    private final Map<Integer, String> cachedKeys = new ConcurrentHashMap<>();

    public ConfigUtility(Resources resources, SharedPreferences preferences) {
        this.resources = resources;
        this.preferences = preferences;
    }

    public boolean adsEnabled(){
        return preferences.getBoolean(getKey(R.string.key_ads_enabled), DefaultConfig.ADS_ENABLED.getBooleanValue());
    }

    public int get(int keyId, int defaultValue){
        return preferences.getInt(getKey(keyId), defaultValue);
    }

    public void save(int keyId, int value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(getKey(keyId), value);
        editor.apply();
    }

    private String getKey(int keyId) {
        if (!cachedKeys.containsKey(keyId)) {
            cachedKeys.put(keyId, resources.getString(keyId));
        }
        return cachedKeys.get(keyId);
    }
}
