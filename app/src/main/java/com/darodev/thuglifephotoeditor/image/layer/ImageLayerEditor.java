package com.darodev.thuglifephotoeditor.image.layer;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.darodev.thuglifephotoeditor.image.ImageEditMode;
import com.darodev.thuglifephotoeditor.utility.BitmapUtility;

import org.joda.time.DateTime;

import java.util.Stack;

/**
 * Created by Dariusz Lelek on 11/4/2017.
 * dariusz.lelek@gmail.com
 */

public class ImageLayerEditor {
    private final Context context;
    private final ImageView defaultLayer;
    private final FrameLayout imageLayerLayout;
    private final Stack<ImageLayer> imageLayers = new Stack<>();
    private int freeIndex;
    private DateTime lastEditTime;
    private float lastX, lastY;
    private ImageEditMode currentEditMode;

    private static final int EDIT_TIME_DELAY_MS = 50;

    public ImageLayerEditor(Context context, ImageView defaultLayer, FrameLayout imageLayerLayout) {
        this.context = context;
        this.defaultLayer = defaultLayer;
        this.imageLayerLayout = imageLayerLayout;

        reset();
    }

    public void reset(){
        resetLastCoordinates();
        removeAllLayers();

        imageLayers.clear();
        freeIndex = getFirstFreeIndex(imageLayerLayout);
        lastEditTime = DateTime.now();
        currentBitmap = null;
        currentEditMode = ImageEditMode.NONE;
    }

    private void resetLastCoordinates(){
        lastX = -1F;
        lastY = -1F;
    }

    private int getFirstFreeIndex(final FrameLayout imageLayerLayout){
        int index = 0;
        while(imageLayerLayout.getChildAt(index) != null){
            index ++;
        }
        return index;
    }

    // TODO this method is redundant because after image is added new ImageLayerEditor is created.
    @Deprecated
    private void removeAllLayers(){
        while (freeIndex > 0){
            if(imageLayerLayout.getChildAt(freeIndex) != null){
                imageLayerLayout.removeViewAt(freeIndex);
            }
            freeIndex--;
        }
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

    public boolean hasTopLayer(){
        return !imageLayers.isEmpty();
    }

    public void processTopLayerMove(float x, float y){
        if(canEditTopLayer() && lastCoordinatesValid()){
            peekTopLayerView().setImageBitmap(BitmapUtility.move(getCurrentTopLayerBitmap(), x , y));
            updateEditTime();
            resetLastCoordinates();
        }
        updateLastCoordinates(x, y);
    }

    private boolean canEditTopLayer(){
        return !imageLayers.isEmpty() && lastEditTime.withMillis(EDIT_TIME_DELAY_MS).isBefore(DateTime.now());
    }

    private boolean lastCoordinatesValid(){
        return lastX > 0 && lastY > 0;
    }

    private ImageView peekTopLayerView(){
        synchronized (imageLayers){
            return imageLayers.peek().getImageView();
        }
    }

    private Bitmap getCurrentTopLayerBitmap(){
        if(currentBitmap == null && hasTopLayer()){
            currentBitmap = peekTopLayerView().getDrawingCache();
        }

        return currentBitmap;
    }

    private void updateEditTime(){
        lastEditTime = DateTime.now();
    }

    public void processEditFinished(){
        currentBitmap = null;
        resetLastCoordinates();
    }

    private void updateLastCoordinates(float x, float y){
        lastX = x;
        lastY = y;
    }

    public void processTopLayerResizeRotate(float x, float y){
        if(canEditTopLayer()){
            // TODO

            updateEditTime();
        }
    }

    /**
     * Add new Layer over image with selected Bitmap
     */
    public void addLayer(Bitmap bitmap){
        final ImageView view = new ImageView(context);
        view.setLayoutParams(defaultLayer.getLayoutParams());
        view.setImageBitmap(bitmap);
        view.setDrawingCacheEnabled(true);

        imageLayers.push(new ImageLayer(view));
        imageLayerLayout.addView(view, freeIndex++);
    }


    /**
     * Removes most top layer from the layout
     */
    public void removeTopLayer(){
        if(!imageLayers.isEmpty()){
            imageLayerLayout.removeViewAt(--freeIndex);
            imageLayers.pop();
        }
    }

}
