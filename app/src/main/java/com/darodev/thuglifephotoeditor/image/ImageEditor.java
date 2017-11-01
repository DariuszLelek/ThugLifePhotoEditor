package com.darodev.thuglifephotoeditor.image;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.darodev.thuglifephotoeditor.R;
import com.darodev.thuglifephotoeditor.utility.BitmapUtility;
import com.darodev.thuglifephotoeditor.utility.ConfigUtility;
import com.darodev.thuglifephotoeditor.utility.DefaultConfig;

/**
 * Created by Dariusz Lelek on 10/31/2017.
 * dariusz.lelek@gmail.com
 */

public class ImageEditor {
    private Bitmap bitmap;
    private int orientation;
    private final ConfigUtility configUtility;

    public ImageEditor(BitmapHolder bitmapHolder, ConfigUtility configUtility) {
        this.configUtility = configUtility;

        this.bitmap = bitmapHolder.getBitmap();
        this.orientation = bitmapHolder.getOrientation();

        scaleBitmapToFitView();

        if (isBitmapRotated()) {
            rotateBitmapToDefaultOrientation();
        }
    }

    private void rotateBitmapToDefaultOrientation(){
        bitmap = BitmapUtility.rotate(bitmap, orientation);
        orientation = 0;
    }

    private void scaleBitmapToFitView(){
        float maxWidth = configUtility.get(R.string.key_edit_image_width, DefaultConfig.IMAGE_MAX_WIDTH.getIntValue());
        float scale =  maxWidth / BitmapUtility.getLongerSide(bitmap);
        bitmap = BitmapUtility.getScaledBitmap(bitmap, scale);
    }

    private boolean isBitmapRotated(){
        return orientation > 0;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void rotateBitmap() {
        Matrix matrix = new Matrix();
        int rotationAngle = 90;

        matrix.postRotate(rotationAngle);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        orientation = Math.abs((orientation - rotationAngle + 360) % 360);
    }

    // TODO public?
    public void recycleBitmap(){
        if(bitmap != null){
            bitmap.recycle();
        }

        bitmap = null;
    }
}
