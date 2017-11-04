package com.darodev.thuglifephotoeditor.image;

/**
 * Created by Dariusz Lelek on 11/4/2017.
 * dariusz.lelek@gmail.com
 */

public enum ImageEditMode {
    NONE(0), MOVE(1), ROTATE_RESIZE(2);

    private int modeLevel;

    ImageEditMode(int modeLevel) {
        this.modeLevel = modeLevel;
    }

    public boolean isLowerThan(ImageEditMode mode){
        return this.modeLevel < mode.modeLevel;
    }

    public static ImageEditMode getHigherPriorityMode(ImageEditMode currentMode){
        if(currentMode == NONE){
            return MOVE;
        }else if(currentMode == MOVE){
            return ROTATE_RESIZE;
        }
        return NONE;
    }
}
