package com.darodev.thuglifephotoeditor.image;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

/**
 * Created by Dariusz Lelek on 11/1/2017.
 * dariusz.lelek@gmail.com
 */

public class BitmapHolder {
    private final Bitmap bitmap;
    private final int rotationDegrees;

    public BitmapHolder(@NonNull final Bitmap bitmap, int rotationDegrees) {
        this.bitmap = bitmap;
        this.rotationDegrees = rotationDegrees;
    }

    @NonNull
    Bitmap getBitmap() {
        return bitmap;
    }

    int getRotationDegrees() {
        return rotationDegrees;
    }
}
