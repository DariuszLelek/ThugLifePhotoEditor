package com.darodev.thuglifephotoeditor.image;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Dariusz Lelek on 11/1/2017.
 * dariusz.lelek@gmail.com
 */

public class BitmapHolder {
    public static final BitmapHolder EMPTY = new BitmapHolder(null, 0);

    private final Bitmap bitmap;
    private final int rotationDegrees;

    public BitmapHolder(@Nullable Bitmap bitmap, int rotationDegrees) {
        this.bitmap = bitmap != null ? bitmap : getEmptyBitmap();
        this.rotationDegrees = rotationDegrees;
    }

    private Bitmap getEmptyBitmap(){
        return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
    }

    @NonNull
    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getRotationDegrees() {
        return rotationDegrees;
    }

    public boolean hasBitmap(){
        return bitmap != null;
    }
}
