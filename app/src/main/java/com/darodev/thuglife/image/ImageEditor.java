package com.darodev.thuglife.image;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.darodev.thuglife.R;
import com.darodev.thuglife.utility.BitmapUtility;
import com.darodev.thuglife.utility.ConfigUtility;
import com.darodev.thuglife.utility.DefaultConfig;

/**
 * Created by Dariusz Lelek on 10/31/2017.
 * dariusz.lelek@gmail.com
 */

public class ImageEditor {
    private Bitmap image;
    private int rotationDegrees;
    private boolean imageEdited;

    private final ConfigUtility configUtility;

    public ImageEditor(@NonNull final ConfigUtility configUtility) {
        this.configUtility = configUtility;
    }

    public ImageEditor(@NonNull final BitmapHolder bitmapHolder, @NonNull final ConfigUtility configUtility) {
        this.configUtility = configUtility;

        this.image = bitmapHolder.getBitmap();
        this.rotationDegrees = bitmapHolder.getRotationDegrees();
        this.imageEdited = false;

        scaleBitmapToFitView();

        if (isImageRotated()) {
            rotateImageToDefaultOrientation();
            resetRotation();
        }
    }

    private void scaleBitmapToFitView(){
        float maxWidth = configUtility.getInt(R.string.key_edit_image_width, DefaultConfig.IMAGE_MAX_WIDTH.getIntValue());
        float scale =  maxWidth / BitmapUtility.getLongerSide(image);
        image = BitmapUtility.getScaledBitmap(image, scale);
    }

    public boolean isImageRotated(){
        return rotationDegrees > 0;
    }

    private void rotateImageToDefaultOrientation(){
        image = BitmapUtility.rotate(image, rotationDegrees);
    }

    private void resetRotation(){
        this.rotationDegrees = 0;
    }

    public boolean isImageEdited() {
        return imageEdited;
    }

    public void setImageIsEdited() {
        this.imageEdited = true;
    }

    public Bitmap getImage() {
        return image;
    }

    public boolean hasImage(){return image != null; }

    public void rotateImage() {
        if(image != null){
            image = BitmapUtility.rotate(image, rotationDegrees > 0 ? -90 : 90);
            rotationDegrees = rotationDegrees > 0 ? 0 : 90;
        }
    }

    public void recycle(){
        if(image != null){
            image.recycle();
        }
        image = null;
        System.gc();
    }
}
