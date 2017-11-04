package com.darodev.thuglifephotoeditor.image;

import android.graphics.Bitmap;

import com.darodev.thuglifephotoeditor.R;
import com.darodev.thuglifephotoeditor.utility.BitmapUtility;
import com.darodev.thuglifephotoeditor.utility.ConfigUtility;
import com.darodev.thuglifephotoeditor.utility.DefaultConfig;

/**
 * Created by Dariusz Lelek on 10/31/2017.
 * dariusz.lelek@gmail.com
 */

public class ImageEditor {
    private Bitmap image;
    private int rotationDegrees;
    private ImageEditMode currentEditMode = ImageEditMode.NONE;

    private final ConfigUtility configUtility;

    public ImageEditor(BitmapHolder bitmapHolder, ConfigUtility configUtility) {
        this.configUtility = configUtility;

        this.image = bitmapHolder.getBitmap();
        this.rotationDegrees = bitmapHolder.getRotationDegrees();

        scaleBitmapToFitView();

        if (isImageRotated()) {
            rotateImageToDefaultOrientation();
            resetRotation();
        }
    }

    private void rotateImageToDefaultOrientation(){
        image = BitmapUtility.rotate(image, rotationDegrees);
    }

    private void resetRotation(){
        this.rotationDegrees = 0;
    }

    private void scaleBitmapToFitView(){
        float maxWidth = configUtility.get(R.string.key_edit_image_width, DefaultConfig.IMAGE_MAX_WIDTH.getIntValue());
        float scale =  maxWidth / BitmapUtility.getLongerSide(image);
        image = BitmapUtility.getScaledBitmap(image, scale);
    }

    public boolean isImageRotated(){
        return rotationDegrees > 0;
    }

    public ImageEditMode getCurrentEditMode() {
        return currentEditMode;
    }

    public void setCurrentEditMode(ImageEditMode currentEditMode) {
        this.currentEditMode = currentEditMode;
    }

    public void setCurrentEditModeByTouchCount(int touchCount) {
        if(touchCount == 1 && currentEditMode.isLowerThan(ImageEditMode.MOVE)){
            this.currentEditMode = ImageEditMode.MOVE;
        }else if (touchCount == 2){
            this.currentEditMode = ImageEditMode.ROTATE_RESIZE;
        }
    }

    public Bitmap getImage() {
        return image;
    }

    public void rotateImage() {
        image = BitmapUtility.rotate(image, rotationDegrees > 0 ? -90 : 90);
        rotationDegrees = rotationDegrees > 0 ? 0 : 90;
    }

    // TODO public?
    public void recycleBitmap(){
        if(image != null){
            image.recycle();
        }

        image = null;
    }
}
