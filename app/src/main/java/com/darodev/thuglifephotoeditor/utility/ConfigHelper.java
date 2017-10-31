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

public class ConfigHelper {
    private final Resources resources;
    private final SharedPreferences preferences;
    private final Map<Integer, String> cachedKeys = new ConcurrentHashMap<>();

    public ConfigHelper(Resources resources, SharedPreferences preferences) {
        this.resources = resources;
        this.preferences = preferences;
    }

    public boolean adsEnabled(){
        return preferences.getBoolean(getKey(R.string.key_ads_enabled), false);
    }

    private String getKey(int keyId) {
        if (!cachedKeys.containsKey(keyId)) {
            cachedKeys.put(keyId, resources.getString(keyId));
        }
        return cachedKeys.get(keyId);
    }
}
