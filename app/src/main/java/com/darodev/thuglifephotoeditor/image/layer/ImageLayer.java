package com.darodev.thuglifephotoeditor.image.layer;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by Dariusz Lelek on 11/5/2017.
 * dariusz.lelek@gmail.com
 */

public class ImageLayer {
    private final ImageView imageView;
    private ImageLocation imageLocation;

    public ImageLayer(ImageView imageView, ImageLocation imageLocation) {
        this.imageView = imageView;
        this.imageLocation = imageLocation;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public ImageLocation getImageLocation() {
        return imageLocation;
    }

    public void setImageLocation(ImageLocation imageLocation) {
        this.imageLocation = imageLocation;
    }
}
