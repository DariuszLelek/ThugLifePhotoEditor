package com.darodev.thuglife.image.layer;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.widget.ImageView;

/**
 * Created by Dariusz Lelek on 11/5/2017.
 * dariusz.lelek@gmail.com
 */

class ImageLayer {
    private final ImageView imageView;
    private Bitmap originalBitmap;
    private float rotation = 0F,
            prevRotation = 0F,
            scale = 1.0F,
            prevScale = 1.0F;

    private static final float MAX_SCALE = 10.0f;
    private static final float MIN_SCALE = 0.05f;

    ImageLayer(@NonNull final ImageView imageView) {
        this.imageView = imageView;

        imageView.post(new Runnable() {
            @Override
            public void run() {
                originalBitmap = imageView.getDrawingCache();
            }
        });
    }

    @NonNull
    Bitmap getOriginalBitmap() {
        return originalBitmap;
    }

    float getScale() {
        return Math.max(Math.min(scale * prevScale, MAX_SCALE), MIN_SCALE);
    }

    void updatePreviousScale(){
        prevScale = getScale();
        scale = 1.0F;
    }

    void setScale(float scale) {
        this.scale = scale;
    }

    void setBitmapToView(Bitmap bitmap){
        this.imageView.setImageBitmap(bitmap);
    }

    float getRotation() {
        return rotation;
    }

    void updatePreviousRotation(){
        this.prevRotation = this.rotation;
    }

    void setRotation(float rotation) {
        this.rotation = this.prevRotation + rotation;
    }
}
