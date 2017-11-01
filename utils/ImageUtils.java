package sushchak.bohdan.curabitur.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import sushchak.bohdan.curabitur.R;
import sushchak.bohdan.curabitur.data.StaticVar;
import sushchak.bohdan.curabitur.data.UserDataSharedPreference;
import sushchak.bohdan.curabitur.model.User;

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

    public static void setUserAvatar(final WeakReference<CircleImageView> referenceToImageView,
                                     final WeakReference<TextView> referenceTextName,
                                     final User user,
                                     final int resourceDefault) {

        if(referenceToImageView != null){
            final CircleImageView imageView = (CircleImageView) referenceToImageView.get();
            String avatarPath = "users" + File.separator + user.getUserId() + File.separator + user.getAvatar();
            try {
                final File localFile = File.createTempFile("avatar", "jpeg");
                FirebaseStorage.getInstance()
                        .getReference()
                        .child(avatarPath)
                        .getFile(localFile)
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                File file = localFile;
                                Uri image = Uri.fromFile(file);

                                imageView.setImageURI(image);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        imageView.setImageResource(resourceDefault);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(referenceTextName != null) {
            final TextView tvUserName = (TextView) referenceTextName.get();
            String userName = user.getName();
            if (userName != null)
                tvUserName.setText(user.getName());
            else tvUserName.setText("");
        }


    }

    public static void setUserAvatar(final WeakReference<CircleImageView> referenceToView,
                                     final WeakReference<TextView> referenceTextName,
                                     final String userId,
                                     final int resourceDefault){
        FirebaseDatabase.getInstance().getReference().child("users/" + userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    if(dataSnapshot.getValue() != null) {
                        HashMap mapContactData = (HashMap) dataSnapshot.getValue();
                        String name = mapContactData.get("name").toString();
                        String email = mapContactData.get("email").toString();
                        String avatar = mapContactData.get("avatar").toString();
                        String phone = mapContactData.get("phone").toString();

                        User user = new User(userId, name, email, avatar, phone);
                        setUserAvatar(referenceToView,referenceTextName, user, resourceDefault);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

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
