package com.darodev.thuglifephotoeditor.image.save;

import android.graphics.Bitmap;
import android.os.Environment;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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
    private static final String IMAGE_PREFIX = "Thug_";
    private static final String IMAGE_EXTENSION = ".jpg";
    private static final Bitmap.CompressFormat IMAGE_FORMAT = Bitmap.CompressFormat.JPEG;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd_HH:mm:ss");

    public boolean saveImage(Bitmap bitmap) {
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File filePath = new File(directory, IMAGE_PREFIX + DATE_TIME_FORMATTER.print(DateTime.now()) + IMAGE_EXTENSION);

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(filePath);
            bitmap.compress(IMAGE_FORMAT, QUALITY, fileOutputStream);
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
