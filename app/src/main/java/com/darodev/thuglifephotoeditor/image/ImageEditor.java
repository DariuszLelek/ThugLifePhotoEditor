package com.darodev.thuglifephotoeditor.image;

import android.graphics.Bitmap;

import com.darodev.thuglifephotoeditor.utility.BitmapUtility;

/**
 * Created by Dariusz Lelek on 10/31/2017.
 * dariusz.lelek@gmail.com
 */

public class ImageEditor {
    private Bitmap originalBitmap, scaledBitmap;

    public ImageEditor() {
        clearBitmaps();
    }

    public void setOriginalBitmap(Bitmap originalBitmap) {
        this.originalBitmap = originalBitmap;
    }

    public boolean isOriginalBitmapSet(){
        return originalBitmap != null;
    }

    public boolean isScaledBitmapSet(){
        return scaledBitmap != null;
    }

    public Bitmap getBitmapScaledToDimensions(int width, int height, boolean keepAspect) {
        if(isOriginalBitmapSet()){
            return BitmapUtility.getScaledBitmap(originalBitmap, width, height, keepAspect);
        }
        return null;
    }

    // TODO public?
    public void clearBitmaps(){
        if(isOriginalBitmapSet()){
            originalBitmap.recycle();
        }

        if(isScaledBitmapSet()){
            scaledBitmap.recycle();
        }

        originalBitmap = null;
        scaledBitmap = null;
    }
}
