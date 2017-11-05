package com.darodev.thuglifephotoeditor.image.layer;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by Dariusz Lelek on 11/5/2017.
 * dariusz.lelek@gmail.com
 */

public class ImageLayer {
    public static final ImageLayer EMPTY = new ImageLayer(null, new ImageCenter(0,0), null);

    private final ImageView imageView;
    private final Bitmap originalBitmap;
    private ImageCenter imageCenter;
    private float rotation, prevRotation = 0F, scale = 1.0F, prevScale = 1.0F;
    private static final float MAX_SCALE = 10.0f;
    private static final float MIN_SCALE = 0.05f;

    public ImageLayer(ImageView imageView, ImageCenter center, Bitmap originalBitmap) {
        this.imageView = imageView;
        this.imageCenter = center;
        this.originalBitmap = originalBitmap;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public Bitmap getOriginalBitmap() {
        return originalBitmap;
    }

    public ImageCenter getImageCenter() {
        return imageCenter;
    }

    public void setImageCenter(ImageCenter imageCenter) {
        this.imageCenter = imageCenter;
    }

    public float getScale() {
        return Math.max(Math.min(scale * prevScale, MAX_SCALE), MIN_SCALE);
    }

    public void updatePreviousScale(){
        prevScale = getScale();
        scale = 1.0F;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void setBitmapToView(Bitmap bitmap){
        this.imageView.setImageBitmap(bitmap);
    }

    public float getRotation() {
        return rotation;
    }

    public void updatePreviousRotation(){
        this.prevRotation = this.rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = this.prevRotation + rotation;
    }
}
