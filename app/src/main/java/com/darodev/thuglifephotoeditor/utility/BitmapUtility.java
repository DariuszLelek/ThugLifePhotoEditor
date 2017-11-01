package com.darodev.thuglifephotoeditor.utility;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

import java.io.IOException;

/**
 * Created by Dariusz Lelek on 10/31/2017.
 * dariusz.lelek@gmail.com
 */

public class BitmapUtility {

    @Nullable
    public static Bitmap getFromIntentData(Intent intent, Context context) {
        if (intent.getData() == null && intent.getExtras() != null) {
            return (Bitmap) intent.getExtras().get("data");
        } else {
            try {
                return MediaStore.Images.Media.getBitmap(context.getContentResolver(), intent.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Bitmap getScaledBitmap(Bitmap original, int width){
        return Bitmap.createScaledBitmap(original, width,
                Math.round(original.getHeight() * width / original.getWidth()), false);
    }

    public static Bitmap rotateBitmapRight(Bitmap bitmap){
        return null;
    }
}
