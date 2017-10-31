package sushchak.bohdan.curabitur.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import sushchak.bohdan.curabitur.data.StaticVar;

public class ImageUtils {

    public static final int USER_AVATAR_WIDTH = 256;
    public static final int USER_AVATAR_HEIGH = 256;

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    /*
    public static Uri getImageFromFireBase(String path) throws IOException{
        final Uri[] imageUri = {Uri.EMPTY};

        final File localFile = File.createTempFile("avatar", "jpeg");

        FirebaseStorage.getInstance()
                .getReference()
                .child(path)
                .getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        File file = localFile;
                        imageUri[0] = Uri.fromFile(file);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

        return imageUri[0];
    }*/

    public static String saveImage(Context context, Uri imageUri, String imageName) throws IOException{

        String pathName = StaticVar.STR_APP_DIRECTORY + File.separator + "avatar" + File.separator + imageName + ".jpeg";

        FileOutputStream out = new FileOutputStream(pathName);
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out);

        return pathName;
    }

    public static void deleteImage(String avatarName){
        File imageFile = new File(StaticVar.STR_APP_DIRECTORY + File.separator + "avatar" + File.separator + avatarName + ".jpeg");
        if(imageFile.exists()) {
            imageFile.delete();

        }
    }


}
