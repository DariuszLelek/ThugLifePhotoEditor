package com.darodev.thuglifephotoeditor.utility;

/**
 * Created by Dariusz Lelek on 10/31/2017.
 * dariusz.lelek@gmail.com
 */

public enum DefaultConfig {
    ADS_ENABLED(1, false),
    IMAGE_MAX_WIDTH(1800, false),
    INSTRUCTIONS_VISIBLE(1, true);

    private final int intValue;
    private final boolean booleanValue;

    DefaultConfig(int intValue, boolean booleanValue) {
        this.intValue = intValue;
        this.booleanValue = booleanValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public boolean getBooleanValue() {
        return booleanValue;
    }
}
