package com.darodev.thuglifephotoeditor.image;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;

import com.darodev.thuglifephotoeditor.utility.BitmapUtility;
import com.darodev.thuglifephotoeditor.utility.DefaultConfig;

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

        if(originalBitmap.getWidth() > DefaultConfig.IMAGE_MAX_WIDTH.getIntValue()){
            this.originalBitmap = getBitmapScaledToDimensions(DefaultConfig.IMAGE_MAX_WIDTH.getIntValue());
        }
    }

    public boolean isOriginalBitmapSet(){
        return originalBitmap != null;
    }

    public boolean isScaledBitmapSet(){
        return scaledBitmap != null;
    }

    public Bitmap getBitmapScaledToDimensions(int width) {
        if(isOriginalBitmapSet()){
            return BitmapUtility.getScaledBitmap(originalBitmap, width);
        }
        return null;
    }

    public void rotateOriginalBitmap(){
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        originalBitmap = Bitmap.createBitmap(originalBitmap, 0,0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, false);
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
