package com.darodev.thuglifephotoeditor.image.layer;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.darodev.thuglifephotoeditor.image.ImageEditMode;
import com.darodev.thuglifephotoeditor.touch.PointPair;
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
    private final ImageLayer emptyLayer;
    private final Stack<ImageLayer> imageLayers = new Stack<>();
    private int freeIndex;
    private DateTime lastEditTime, lestEditModeSwitchTime;
    private PointPair scaleStartPointPair = PointPair.INVALID_PAIR;
    private ImageEditMode currentEditMode;

    private static final int EDIT_TIME_DELAY_MS = 40;
    private static final int EDIT_MODE_TIME_DELAY_MS = 75;


    public ImageLayerEditor(Context context, ImageView defaultLayer, FrameLayout imageLayerLayout) {
        this.context = context;
        this.defaultLayer = defaultLayer;
        this.imageLayerLayout = imageLayerLayout;
        this.emptyLayer = getEmptyImageLayer();

        reset();
    }

    private ImageLayer getEmptyImageLayer(){
        return new ImageLayer(
                new ImageView(context),
                new ImageCenter(0,0),
                Bitmap.createBitmap(1,1, Bitmap.Config.ARGB_4444));
    }

    public void reset(){
        imageLayers.clear();
        freeIndex = getFirstFreeIndex(imageLayerLayout);
        lastEditTime = DateTime.now();
        currentEditMode = ImageEditMode.NONE;
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

    public void changeCurrentEditMode(ImageEditMode editMode) {
        if (this.currentEditMode != editMode) {
            this.currentEditMode = editMode;
            lestEditModeSwitchTime = DateTime.now();
        }
    }

    public void updatePreviousRotation() {
        peekTopLayer().updatePreviousScale();
        peekTopLayer().updatePreviousRotation();
    }

    public void setCurrentEditModeByTouchCount(int touchCount) {
        if(touchCount == 1){
            changeCurrentEditMode(ImageEditMode.MOVE);
        }else if (touchCount == 2){
            changeCurrentEditMode(ImageEditMode.ROTATE_RESIZE);
        }
    }

    public void setCurrentRotation(float currentRotation) {
        peekTopLayer().setRotation(currentRotation);
    }

    public void resetScaleStartPointPair(){
        scaleStartPointPair = PointPair.INVALID_PAIR;
    }

    public boolean hasTopLayer(){
        return !imageLayers.isEmpty();
    }

    public void processTopLayerMove(ImageCenter imageCenter){
        processManipulation(imageCenter);
    }

    public void setScaleStartPointPair(PointPair pointPair){
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

    private boolean canEditTopLayer(){
        return hasTopLayer() && lastEditTime.plusMillis(EDIT_TIME_DELAY_MS).isBeforeNow() && currentEditMode != ImageEditMode.NONE;
    }

    private float getScale(PointPair pointPair){
        return canGetScale(pointPair) ? scaleStartPointPair.getScaleResult(pointPair) : 1.0F;
    }

    private boolean canGetScale(PointPair pointPair){
        return scaleStartPointPair.isValid() && pointPair.isValid() && !scaleStartPointPair.equals(pointPair);
    }

    private ImageLayer peekTopLayer() {
        return hasTopLayer() ? imageLayers.peek() : emptyLayer;
    }

    private void updateEditTime(){
        lastEditTime = DateTime.now();
    }

    private void processManipulation(ImageCenter imageCenter) {
        if (canEditTopLayer()) {
            peekTopLayer().setBitmapToView(getManipulatedBitmap(peekTopLayer(), imageCenter));
            updateEditTime();
        }
    }

    private Bitmap getManipulatedBitmap(ImageLayer topLayer, ImageCenter imageCenter) {
        return BitmapUtility.manipulate(topLayer.getOriginalBitmap(), -topLayer.getRotation(), topLayer.getScale(), imageCenter);
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
                ImageCenter location = new ImageCenter(bitmap.getWidth() / 2,bitmap.getHeight() / 2);
                imageLayers.push(new ImageLayer(view, location, bitmap));
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
