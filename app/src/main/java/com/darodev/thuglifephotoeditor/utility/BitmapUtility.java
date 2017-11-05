package com.darodev.thuglifephotoeditor.utility;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Debug;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.darodev.thuglifephotoeditor.image.BitmapHolder;
import com.darodev.thuglifephotoeditor.image.layer.ImageCenter;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Dariusz Lelek on 10/31/2017.
 * dariusz.lelek@gmail.com
 */

public class BitmapUtility {
    private static final Paint bitmapPaint = new Paint( Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG );

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

    public static Bitmap manipulate(Bitmap bitmap, float rotationAngleDegree, float scale, ImageCenter imageCenter){
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int newW = w, newH = h;
        if (rotationAngleDegree == 90 || rotationAngleDegree == 270) {
            newW = h;
            newH = w;
        }

        Bitmap rotatedBitmap = Bitmap.createBitmap(newW, newH, bitmap.getConfig());
        Canvas canvas = new Canvas(rotatedBitmap);

        Matrix matrix = new Matrix();
        matrix.postTranslate(-bitmap.getWidth() / 2, -bitmap.getHeight() / 2);
        matrix.postScale(scale,scale);
        matrix.postRotate(rotationAngleDegree);
        matrix.postTranslate(imageCenter.getX(), imageCenter.getY());
        canvas.drawBitmap(bitmap, matrix, bitmapPaint);
        matrix.reset();

        return rotatedBitmap;
    }

    public static Bitmap rotate(Bitmap bitmap, float degrees){
        if(degrees != 0){
            Matrix matrix = new Matrix();
            matrix.postRotate(degrees);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
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
