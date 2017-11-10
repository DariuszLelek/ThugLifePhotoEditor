package com.darodev.thuglife.touch;

import android.support.annotation.NonNull;
import android.view.MotionEvent;

/**
 * Created by Dariusz Lelek on 11/5/2017.
 * dariusz.lelek@gmail.com
 */

public class PointPair {
    public static final PointPair INVALID_PAIR = new PointPair();

    private final float x1, y1, x2, y2;
    private final boolean valid;

    private PointPair(){
        this.x1 = this.y1 = this.x2 = this.y2  = 0;
        this.valid = false;
    }

    public PointPair(@NonNull final MotionEvent event){
        if(event.getPointerCount() == 2){
            this.x1 = event.getX(0);
            this.y1 = event.getY(0);
            this.x2 = event.getX(1);
            this.y2 = event.getY(1);
        }else{
            this.x1 = 0;
            this.y1 = 0;
            this.x2 = 0;
            this.y2 = 0;
        }

        this.valid = validate();
    }

    private boolean validate(){
        return x1 > 0 && x2 > 0 && y1 > 0 && y2 > 0;
    }

    public float getCenterX(){
        return Math.min(x1, x2) + Math.abs((x1 - x2) / 2);
    }

    public float getCenterY(){
        return Math.min(y1, y2) + Math.abs((y1 - y2) / 2);
    }

    public float getScaleResult(@NonNull final PointPair o){
        double firstLength = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
        double secondLength = Math.sqrt(Math.pow(o.x1 - o.x2, 2) + Math.pow(o.y1 - o.y2, 2));
        return (float) (secondLength/firstLength);
    }

    public boolean isValid(){
        return valid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PointPair pointPair = (PointPair) o;

        return Float.compare(pointPair.x1, x1) == 0
                && Float.compare(pointPair.y1, y1) == 0
                && Float.compare(pointPair.x2, x2) == 0
                && Float.compare(pointPair.y2, y2) == 0
                && valid == pointPair.valid;
    }

    @Override
    public int hashCode() {
        int result = (x1 != +0.0f ? Float.floatToIntBits(x1) : 0);
        result = 31 * result + (y1 != +0.0f ? Float.floatToIntBits(y1) : 0);
        result = 31 * result + (x2 != +0.0f ? Float.floatToIntBits(x2) : 0);
        result = 31 * result + (y2 != +0.0f ? Float.floatToIntBits(y2) : 0);
        result = 31 * result + (valid ? 1 : 0);
        return result;
    }
}
