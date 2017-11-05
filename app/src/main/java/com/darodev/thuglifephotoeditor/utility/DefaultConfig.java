package com.darodev.thuglifephotoeditor.utility;

/**
 * Created by Dariusz Lelek on 10/31/2017.
 * dariusz.lelek@gmail.com
 */

public enum DefaultConfig {
    ADS_ENABLED(1, "", false),
    IMAGE_DEFAULT_WIDTH(800, "", false),
    IMAGE_DEFAULT_HEIGHT(600, "", false),
    IMAGE_MAX_WIDTH(1800, "", false),
    IMAGE_MAX_HEIGHT(1000, "", false);

    private final int intValue;
    private final String stringValue;
    private final boolean booleanValue;

    DefaultConfig(int intValue, String stringValue, boolean booleanValue) {
        this.intValue = intValue;
        this.stringValue = stringValue;
        this.booleanValue = booleanValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public boolean getBooleanValue() {
        return booleanValue;
    }
}
