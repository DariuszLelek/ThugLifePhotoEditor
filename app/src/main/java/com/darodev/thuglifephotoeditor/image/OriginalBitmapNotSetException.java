package com.darodev.thuglifephotoeditor.image;

/**
 * Created by Dariusz Lelek on 10/31/2017.
 * dariusz.lelek@gmail.com
 */

public class OriginalBitmapNotSetException extends Exception {

    @Override
    public String getMessage() {
        return "Original Bitmap is not set.";
    }
}
