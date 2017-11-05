package com.darodev.thuglifephotoeditor.touch;

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

    public PointPair(float x1, float y1, float x2, float y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;

        this.valid = validate();
    }

    public float getX1() {
        return x1;
    }

    public float getY1() {
        return y1;
    }

    public float getX2() {
        return x2;
    }

    public float getY2() {
        return y2;
    }

    public float getCenterX(){
        return Math.min(x1, x2) + Math.abs((x1 - x2) / 2);
    }

    public float getCenterY(){
        return Math.min(y1, y2) + Math.abs((y1 - y2) / 2);
    }

    public PointerPairCompareResult getCompareResult(PointPair o){
        double firstLength = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
        double secondLength = Math.sqrt(Math.pow(o.x1 - o.x2, 2) + Math.pow(o.y1 - o.y2, 2));

        double resizeLength = Math.abs(firstLength - secondLength);

        firstLength = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
        secondLength = Math.sqrt(Math.pow(o.x1 - o.x2, 2) + Math.pow(o.y1 - o.y2, 2));


        return PointerPairCompareResult.UNKNOWN;
    }

    private boolean validate(){
        return x1 > 0 && x2 > 0 && y1 > 0 && y2 > 0;
    }

    public boolean isValid(){
        return valid;
    }
}
