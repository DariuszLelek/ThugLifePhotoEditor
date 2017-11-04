package com.darodev.thuglifephotoeditor.image.layer;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Created by Dariusz Lelek on 11/4/2017.
 * dariusz.lelek@gmail.com
 */

public class ImageLayerController {
    private final Context context;
    private final ImageView defaultLayer;
    private final FrameLayout imageLayerLayout;
    private final Queue<ImageView> imageLayers = new ArrayDeque<>();
    private int freeIndex = 0;

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

    public void processImageMove(float x, float y){

    }

    public void processImageResizeRotate(float x, float y){

    }

    /**
     * Add new Layer over image with selected Bitmap
     */
    public void addLayer(Bitmap bitmap){
        final ImageView view = new ImageView(context);
        view.setLayoutParams(defaultLayer.getLayoutParams());
        view.setImageBitmap(bitmap);

        imageLayers.add(view);
        imageLayerLayout.addView(view, freeIndex++);
    }


    /**
     * Removes most top layer from the layout
     */
    public void removeTopLayer(){
        if(!imageLayers.isEmpty()){
            imageLayerLayout.removeViewAt(--freeIndex);
            imageLayers.remove();
        }
    }


}
