package com.darodev.thuglifephotoeditor.utility;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.NonNull;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Dariusz Lelek on 11/8/2017.
 * dariusz.lelek@gmail.com
 */

public class ImageSaver {
    private static final int QUALITY = 100;
    private final ContextWrapper contextWrapper;

    public ImageSaver(@NonNull Context context) {
        this.contextWrapper = new ContextWrapper(context);
    }

    public boolean saveImage(Bitmap bitmap) {
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File filePath = new File(directory, "profile.jpg");

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStream(fileOutputStream);
        }
        return !directory.getAbsolutePath().isEmpty();
    }

    private void closeStream(Closeable stream){
        if(stream != null){
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
