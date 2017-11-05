package com.darodev.thuglifephotoeditor.touch;

import org.joda.time.DateTime;

/**
 * Created by Dariusz Lelek on 11/5/2017.
 * dariusz.lelek@gmail.com
 */

public class TouchMoveHelper {

    /**
     * Delay after initial touch to wait for multitouch (rotate or resize)
     */
    private static final int TOUCH_THRESHOLD_MS = 75;
    private static DateTime lastTouchTime;

    /**
     * @return true if no multitouch event after {@link #TOUCH_THRESHOLD_MS} threshold.
     */
    public static boolean isMove(){
        return getLastTouchTime().plusMillis(TOUCH_THRESHOLD_MS).isBeforeNow();
    }

    private static DateTime getLastTouchTime(){
        if(lastTouchTime == null){
            lastTouchTime = DateTime.now();
        }

        return lastTouchTime;
    }

    public static void reset(){
        lastTouchTime = null;
    }
}
