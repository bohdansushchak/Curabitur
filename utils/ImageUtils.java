package sushchak.bohdan.curabitur.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtils {

    public static final int USER_AVATAR_WIDTH = 256;
    public static final int USER_AVATAR_HEIGH = 256;

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    public static void saveImage(Context context, Uri imageUri){

        try (FileOutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory() + "/Download/avatar.jpeg")){
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out);

        } catch (IOException e) {
            e.printStackTrace();}
    }



}
