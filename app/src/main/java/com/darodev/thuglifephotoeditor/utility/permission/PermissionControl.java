package com.darodev.thuglifephotoeditor.utility.permission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by Dariusz Lelek on 11/8/2017.
 * dariusz.lelek@gmail.com
 */

public class PermissionControl {
    private final Context context;
    private final Activity activity;

    public PermissionControl(final @NonNull Context context, final @NonNull Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public boolean isPermissionGranted(@NonNull Permission permission){
        return ContextCompat.checkSelfPermission(context, permission.getName()) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission(@NonNull Permission permission){
        ActivityCompat.requestPermissions(activity, new String[]{permission.getName()}, permission.getRequestCode());
    }
}
