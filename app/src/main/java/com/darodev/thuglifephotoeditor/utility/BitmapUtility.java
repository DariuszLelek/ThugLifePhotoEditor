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
import android.view.View;
import android.widget.ImageView;

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
    private static final Matrix defaultMatrix = new Matrix();
    private static final Bitmap defaultBitmap = Bitmap.createBitmap(1,1, Bitmap.Config.ARGB_4444);

    @NonNull
    public static BitmapHolder getFromIntentData(Intent intent, Context context) {
        if (intent.getData() == null && intent.getExtras() != null) {
            Bitmap bitmap = (Bitmap) intent.getExtras().get("data");
            return new BitmapHolder(bitmap != null ? bitmap : defaultBitmap, 0);
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
        return new BitmapHolder(defaultBitmap, 0);
    }

    public static Bitmap getScaledBitmap(Bitmap original, float scale) {
        return Bitmap.createScaledBitmap(
                original,
                Math.round(original.getWidth() * scale),
                Math.round(original.getHeight() * scale),
                true);
    }

    public static void drawViewBitmapOnCanvas(final Canvas canvas, View view){
        if(view != null){
            view.invalidate();
            Bitmap bitmap = view.getDrawingCache();
            canvas.drawBitmap(bitmap, defaultMatrix, bitmapPaint);
            recycle(bitmap);
        }
    }

    public static float getLongerSide(Bitmap bitmap){
        return Math.max(bitmap.getHeight(), bitmap.getWidth());
    }

    public static Bitmap manipulate(final Bitmap bitmap, float rotationAngleDegree, float scale, ImageCenter imageCenter){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap rotatedBitmap = Bitmap.createBitmap(width, height, bitmap.getConfig());
        Canvas canvas = new Canvas(rotatedBitmap);

        Matrix matrix = new Matrix();
        matrix.postTranslate(-width / 2, -height / 2);
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
            Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            recycle(bitmap);
            bitmap = rotated;
        }
        return bitmap;
    }

    public static void setImageBitmap(ImageView imageView, Bitmap bitmap) {
        if(imageView != null){
            imageView.setDrawingCacheEnabled(true);
            //recycle(imageView.getDrawingCache());
            imageView.setImageBitmap(bitmap);
        }
    }

    public static void recycle(Bitmap bitmap){
        if(bitmap != null){
            bitmap.recycle();
        }
    }

    @Deprecated
    public static boolean canFitInMemory(Bitmap bitmap) {
        long size = bitmap.getRowBytes() * bitmap.getHeight();
        long allocNativeHeap = Debug.getNativeHeapAllocatedSize();

        final long heapPad = (long) Math.max(4 * 1024 * 1024, Runtime.getRuntime().maxMemory() * 0.1);

        return (size + allocNativeHeap + heapPad) < Runtime.getRuntime().freeMemory();
    }
}
