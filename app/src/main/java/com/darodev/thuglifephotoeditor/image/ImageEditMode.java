package com.darodev.thuglifephotoeditor.image;

/**
 * Created by Dariusz Lelek on 11/4/2017.
 * dariusz.lelek@gmail.com
 */

public enum ImageEditMode {
    NONE(""),
    MOVE("move"),
    MOVE_ROTATE_RESIZE("move, rotate, resize");

    private String name;

    ImageEditMode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
