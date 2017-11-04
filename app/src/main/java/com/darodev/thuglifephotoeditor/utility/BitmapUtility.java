package com.darodev.thuglifephotoeditor.utility;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Debug;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.darodev.thuglifephotoeditor.image.BitmapHolder;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Dariusz Lelek on 10/31/2017.
 * dariusz.lelek@gmail.com
 */

public class BitmapUtility {
    private static final Paint bitmapPaint = new Paint();

    @NonNull
    public static BitmapHolder getFromIntentData(Intent intent, Context context) {
        if (intent.getData() == null && intent.getExtras() != null) {
            return new BitmapHolder((Bitmap) intent.getExtras().get("data"), 0);
        } else {
            try {

                String[] projection = {MediaStore.Images.ImageColumns.ORIENTATION};
                Cursor cursor = context.getContentResolver().query(intent.getData(), projection, null, null, null);

                int rotationDegrees = -1;
                if (cursor != null && cursor.moveToFirst()) {
                    rotationDegrees = cursor.getInt(0);
                    cursor.close();
                }

                return new BitmapHolder(MediaStore.Images.Media.getBitmap(context.getContentResolver(), intent.getData()),rotationDegrees);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return BitmapHolder.EMPTY;
    }

    public static Bitmap getScaledBitmap(Bitmap original, float scale) {
        return Bitmap.createScaledBitmap(
                original,
                Math.round(original.getWidth() * scale),
                Math.round(original.getHeight() * scale),
                true);
    }

    public static float getLongerSide(Bitmap bitmap){
        return Math.max(bitmap.getHeight(), bitmap.getWidth());
    }

    public static Bitmap rotate(Bitmap bitmap, int degrees){
        if(degrees != 0){
            Matrix matrix = new Matrix();
            matrix.postRotate(degrees);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        return bitmap;
    }

    public static Bitmap move(Bitmap bitmap, float x, float y){
        if(x != 0 && y != 0){
            Bitmap clean = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_4444);
            Canvas canvas = new Canvas(clean);
            canvas.drawBitmap(bitmap, -30, -30, bitmapPaint);
            return clean;
        }
        return bitmap;
    }

    public static boolean canFitInMemory(Bitmap bitmap) {
        long size = bitmap.getRowBytes() * bitmap.getHeight();
        long allocNativeHeap = Debug.getNativeHeapAllocatedSize();

        final long heapPad = (long) Math.max(4 * 1024 * 1024, Runtime.getRuntime().maxMemory() * 0.1);

        return (size + allocNativeHeap + heapPad) < Runtime.getRuntime().freeMemory();
    }
}
