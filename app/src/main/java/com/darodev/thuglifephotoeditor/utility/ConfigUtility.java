package com.darodev.thuglifephotoeditor.utility;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.NonNull;

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

    public static final float DISABLED_BUTTON_ALPHA = 0.2F;

    public ConfigUtility(@NonNull final Resources resources, @NonNull final SharedPreferences preferences) {
        this.resources = resources;
        this.preferences = preferences;
    }

    public boolean adsEnabled(){
        return preferences.getBoolean(getKey(R.string.key_ads_enabled), DefaultConfig.ADS_ENABLED.getBooleanValue());
    }

    public int getInt(int keyId, int defaultValue){
        return preferences.getInt(getKey(keyId), defaultValue);
    }

    public boolean getBoolean(int keyId, boolean defaultValue){
        return preferences.getBoolean(getKey(keyId), defaultValue);
    }

    public void saveInt(int keyId, int value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(getKey(keyId), value);
        editor.apply();
    }

    public void saveBoolean(int keyId, boolean value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(getKey(keyId), value);
        editor.apply();
    }

    private String getKey(int keyId) {
        if (!cachedKeys.containsKey(keyId)) {
            cachedKeys.put(keyId, resources.getString(keyId));
        }
        return cachedKeys.get(keyId);
    }
}
