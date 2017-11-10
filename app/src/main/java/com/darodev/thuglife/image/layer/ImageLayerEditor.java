package com.darodev.thuglife.image.layer;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.darodev.thuglife.image.ImageEditMode;
import com.darodev.thuglife.touch.PointPair;
import com.darodev.thuglife.utility.BitmapUtility;

import org.joda.time.DateTime;

import java.util.Stack;

/**
 * Created by Dariusz Lelek on 11/4/2017.
 * dariusz.lelek@gmail.com
 */

public class ImageLayerEditor {
    private final FrameLayout imageLayerLayout;
    private final ImageLayer emptyLayer;
    private final Stack<ImageLayer> imageLayers = new Stack<>();
    private int freeIndex;
    private DateTime lastEditTime, lestEditModeSwitchTime;
    private PointPair scaleStartPointPair = PointPair.INVALID_PAIR;
    private ImageEditMode currentEditMode;

    private static final int EDIT_TIME_DELAY_MS = 40;
    private static final int EDIT_MODE_TIME_DELAY_MS = 75;


    public ImageLayerEditor(Context context, FrameLayout imageLayerLayout) {
        this.imageLayerLayout = imageLayerLayout;
        this.emptyLayer = new ImageLayer(new ImageView(context));

        reset();
    }

    public void reset(){
        imageLayers.clear();
        clearImageLayerLayout();
        freeIndex = getFirstFreeIndex(imageLayerLayout);
        lastEditTime = DateTime.now();
        currentEditMode = ImageEditMode.NONE;
        scaleStartPointPair = PointPair.INVALID_PAIR;
    }

    private void clearImageLayerLayout(){
        for(int index = imageLayerLayout.getChildCount() - 1; index > 0; index --){
            imageLayerLayout.removeViewAt(index);
        }
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

    public void updatePrevScaleRotation() {
        peekTopLayer().updatePreviousScale();
        peekTopLayer().updatePreviousRotation();
    }

    public void setCurrentEditModeByTouchCount(int touchCount) {
        if(touchCount == 1){
            changeCurrentEditMode(ImageEditMode.MOVE);
        }else if (touchCount == 2){
            changeCurrentEditMode(ImageEditMode.MOVE_ROTATE_RESIZE);
        }
    }

    public void changeCurrentEditMode(ImageEditMode editMode) {
        if (this.currentEditMode != editMode) {
            this.currentEditMode = editMode;
            lestEditModeSwitchTime = DateTime.now();
        }
    }

    public void setCurrentRotation(float currentRotation) {
        peekTopLayer().setRotation(currentRotation);
    }

    public void resetScaleStartPointPair(){
        scaleStartPointPair = PointPair.INVALID_PAIR;
    }

    private void processManipulation(ImageCenter imageCenter) {
        if (canEditTopLayer()) {
            peekTopLayer().setBitmapToView(getManipulatedBitmap(peekTopLayer(), imageCenter));
            updateEditTime();
        }
    }

    private boolean canEditTopLayer(){
        return hasTopLayer() && lastEditTime.plusMillis(EDIT_TIME_DELAY_MS).isBeforeNow() && currentEditMode != ImageEditMode.NONE;
    }

    public boolean hasTopLayer(){
        return !imageLayers.isEmpty();
    }

    private void updateEditTime(){
        lastEditTime = DateTime.now();
    }

    public void processTopLayerMove(ImageCenter imageCenter){
        processManipulation(imageCenter);
    }

    private void setScaleStartPointPair(PointPair pointPair){
        if(!scaleStartPointPair.isValid()){
            scaleStartPointPair = pointPair;
        }
    }

    public void processTopLayerResizeRotate(PointPair pointPair){
        peekTopLayer().setScale(getScale(pointPair));
        processManipulation(new ImageCenter(pointPair.getCenterX(), pointPair.getCenterY()));
        setScaleStartPointPair(pointPair);
    }

    public boolean isNotBetweenEditModeSwitch() {
        return lestEditModeSwitchTime.plusMillis(EDIT_MODE_TIME_DELAY_MS).isBeforeNow();
    }

    private float getScale(PointPair pointPair){
        return canGetScale(pointPair) ? scaleStartPointPair.getScaleResult(pointPair) : 1.0F;
    }

    public int getFreeIndex() {
        return freeIndex;
    }

    private boolean canGetScale(PointPair pointPair){
        return scaleStartPointPair.isValid() && pointPair.isValid() && !scaleStartPointPair.equals(pointPair);
    }

    private ImageLayer peekTopLayer() {
        return hasTopLayer() ? imageLayers.peek() : emptyLayer;
    }

    private Bitmap getManipulatedBitmap(ImageLayer topLayer, ImageCenter imageCenter) {
        return BitmapUtility.manipulate(topLayer.getOriginalBitmap(), -topLayer.getRotation(), topLayer.getScale(), imageCenter);
    }

    public FrameLayout getImageLayerLayout() {
        return imageLayerLayout;
    }

    /**
     * Add new Layer over image with selected Bitmap
     */
    public void addLayer(ImageView view){
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
