package com.darodev.thuglifephotoeditor.image.layer;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.darodev.thuglifephotoeditor.image.ImageEditMode;
import com.darodev.thuglifephotoeditor.touch.PointerPairCompareResult;
import com.darodev.thuglifephotoeditor.utility.BitmapUtility;
import com.darodev.thuglifephotoeditor.touch.PointPair;

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
    private Bitmap currentBitmap;
    private float lastX, lastY;
    private PointPair lastPointPair;
    private ImageEditMode currentEditMode;
    private float currentRotation;

    private static final int EDIT_TIME_DELAY_MS = 50;

    public ImageLayerEditor(Context context, ImageView defaultLayer, FrameLayout imageLayerLayout) {
        this.context = context;
        this.defaultLayer = defaultLayer;
        this.imageLayerLayout = imageLayerLayout;

        reset();
    }

    public void reset(){
        resetLastCoordinates();
        resetPointerPair();

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

    public float getCurrentRotation() {
        return currentRotation;
    }

    public void setCurrentRotation(float currentRotation) {
        this.currentRotation = currentRotation;
    }

    public boolean hasTopLayer(){
        return !imageLayers.isEmpty();
    }

    public void processTopLayerMove(float x, float y){
        if(canEditTopLayer() && lastCoordinatesValid()){
            Bitmap bitmap = getCurrentTopLayerBitmap();
            peekTopLayerView().setImageBitmap(BitmapUtility.move(bitmap, x , y, imageLayers.peek().getImageLocation()));
            imageLayers.peek().setImageLocation(new ImageLocation(x, y));
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

    private ImageView peekTopLayerView() {
        return imageLayers.peek().getImageView();
    }

    private Bitmap getCurrentTopLayerBitmap(){
        if(currentBitmap == null && hasTopLayer()){
            //peekTopLayerView().invalidate();
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
        resetPointerPair();
    }

    private void updateLastCoordinates(float x, float y){
        lastX = x;
        lastY = y;
    }

    private void resetPointerPair(){
        lastPointPair = PointPair.INVALID_PAIR;
    }

    public void processTopLayerResizeRotate(PointPair pointPair){
        if(canEditTopLayer() && lastPointPair.isValid()){
            peekTopLayerView().setImageBitmap(
                    BitmapUtility.rotateBitmap(
                            getCurrentTopLayerBitmap(),
                            -currentRotation,
                            imageLayers.peek().getImageLocation()));
            updateEditTime();
            resetPointerPair();
        }
        updatePointerPair(pointPair);
    }

    private boolean isRotation(PointPair pointPair){

        return false;
    }

    private void updatePointerPair(PointPair pointPair){
        lastPointPair = pointPair;
    }

    /**
     * Add new Layer over image with selected Bitmap
     */
    public void addLayer(Bitmap bitmap){
        final ImageView view = new ImageView(context);
        view.setLayoutParams(defaultLayer.getLayoutParams());
        view.setImageBitmap(bitmap);
        view.setDrawingCacheEnabled(true);
        imageLayerLayout.addView(view, freeIndex++);

        view.post(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = view.getDrawingCache();
                ImageLocation location = new ImageLocation(bitmap.getWidth() / 2,bitmap.getHeight() / 2);
                imageLayers.push(new ImageLayer(view, location));
            }
        });
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
