package com.darodev.thuglifephotoeditor.utility.permission;

import android.Manifest;

/**
 * Created by Dariusz Lelek on 11/8/2017.
 * dariusz.lelek@gmail.com
 */

public enum Permission {
    UNKNOWN("", -1),
    CAMERA(Manifest.permission.CAMERA, 6500),
    WRITE_STORAGE(Manifest.permission.WRITE_EXTERNAL_STORAGE, 6501);

    private final String name;
    private final int requestCode;

    Permission(String name, int requestCode) {
        this.name = name;
        this.requestCode = requestCode;
    }

    public String getName() {
        return name;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public static Permission getByRequestCode(int requestCode){
        for(Permission permission : values()){
            if(permission.getRequestCode() == requestCode){
                return permission;
            }
        }
        return UNKNOWN;
    }
}
