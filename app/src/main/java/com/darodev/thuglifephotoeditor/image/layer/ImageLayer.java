package com.darodev.thuglifephotoeditor.image.layer;

import android.widget.ImageView;

/**
 * Created by Dariusz Lelek on 11/4/2017.
 * dariusz.lelek@gmail.com
 */

public class ImageLayer {
    private final int index;
    private final ImageView view;

    public ImageLayer(int index, ImageView view) {
        this.index = index;
        this.view = view;
    }

    public int getIndex() {
        return index;
    }

    public ImageView getView() {
        return view;
    }
}
