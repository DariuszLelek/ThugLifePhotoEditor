package com.darodev.thuglifephotoeditor.image.layer;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by Dariusz Lelek on 11/5/2017.
 * dariusz.lelek@gmail.com
 */

public class ImageLayer {
    private final ImageView imageView;
    private final Bitmap originalBitmap;

    public ImageLayer(ImageView imageView, Bitmap originalBitmap) {
        this.imageView = imageView;
        this.originalBitmap = originalBitmap;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public Bitmap getOriginalBitmap() {
        return originalBitmap;
    }
}
