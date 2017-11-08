package com.darodev.thuglifephotoeditor.image;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Dariusz Lelek on 11/1/2017.
 * dariusz.lelek@gmail.com
 */

public class BitmapHolder {
    private final Bitmap bitmap;
    private final int rotationDegrees;

    public BitmapHolder(@NonNull Bitmap bitmap, int rotationDegrees) {
        this.bitmap = bitmap;
        this.rotationDegrees = rotationDegrees;
    }

    private Bitmap getEmptyBitmap(){
        return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
    }

    @NonNull
    Bitmap getBitmap() {
        return bitmap;
    }

    int getRotationDegrees() {
        return rotationDegrees;
    }
}
