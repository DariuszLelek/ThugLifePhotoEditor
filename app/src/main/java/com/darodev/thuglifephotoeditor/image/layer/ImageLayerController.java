package com.darodev.thuglifephotoeditor.image.layer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.darodev.thuglifephotoeditor.utility.BitmapUtility;

import org.joda.time.DateTime;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Stack;

/**
 * Created by Dariusz Lelek on 11/4/2017.
 * dariusz.lelek@gmail.com
 */

public class ImageLayerController {
    private final Context context;
    private final ImageView defaultLayer;
    private final FrameLayout imageLayerLayout;
    private final Stack<ImageView> imageLayers = new Stack<>();
    private int freeIndex = 0;
    private DateTime lastEditTime = DateTime.now();

    private static final int EDIT_TIME_DELAY_MS = 50;

    public ImageLayerController(Context context, ImageView defaultLayer, FrameLayout imageLayerLayout) {
        this.context = context;
        this.defaultLayer = defaultLayer;
        this.imageLayerLayout = imageLayerLayout;
        this.freeIndex = getFirstFreeIndex(imageLayerLayout);
    }

    private int getFirstFreeIndex(final FrameLayout imageLayerLayout){
        int index = 0;
        while(imageLayerLayout.getChildAt(index) != null){
            index ++;
        }
        return index;
    }

    public boolean hasTopLayer(){
        return !imageLayers.isEmpty();
    }

    public void processTopLayerMove(float x, float y){
        if(canEditTopLayer()){
            Bitmap bitmap = imageLayers.peek().getDrawingCache();

            imageLayers.peek().setImageBitmap(BitmapUtility.move(bitmap, x, y));
            imageLayers.peek().invalidate();

            lastEditTime = DateTime.now();
        }
    }

    public void processTopLayerResizeRotate(float x, float y){
        if(canEditTopLayer()){

            lastEditTime = DateTime.now();
        }
    }

    private boolean canEditTopLayer(){
        return !imageLayers.isEmpty() && lastEditTime.withMillis(EDIT_TIME_DELAY_MS).isBefore(DateTime.now());
    }

    /**
     * Add new Layer over image with selected Bitmap
     */
    public void addLayer(Bitmap bitmap){
        final ImageView view = new ImageView(context);
        view.setLayoutParams(defaultLayer.getLayoutParams());
        view.setImageBitmap(bitmap);
        view.setDrawingCacheEnabled(true);

        imageLayers.push(view);
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
