package sushchak.bohdan.curabitur.ui;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import sushchak.bohdan.curabitur.R;
import sushchak.bohdan.curabitur.data.StaticVar;
import sushchak.bohdan.curabitur.data.UserDataSharedPreference;
import sushchak.bohdan.curabitur.model.User;
import sushchak.bohdan.curabitur.utils.ImageUtils;


public class SettingsActivity extends AppCompatActivity {

    private final String TAG = "SettingsActivity";
    private static final int REQUEST_PICK_IMAGE = 33;

    private User user;

    private Uri avatarUri;

    @BindView(R.id.circle_image_view) CircleImageView civAvatar;
    @BindView(R.id.tvTitleUserName) TextView tvUserName;
    @BindView(R.id.tvUserStatus) TextView tvUserStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ButterKnife.bind(SettingsActivity.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        SettingsListFragment fragment = new SettingsListFragment();
        FragmentTransaction fragmentTransaction;
        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();

        setUserData();
    }

    private void setUserData(){
        user = UserDataSharedPreference.getInstance(SettingsActivity.this).getUserData();

        WeakReference<CircleImageView> reference = new WeakReference<CircleImageView>(civAvatar);
        ImageUtils.setUserAvatar(reference, user, R.drawable.user_avatar_default);

        tvUserName.setText(user.getName());
        tvUserStatus.setText("online");
    }


    @OnClick(R.id.fabPickImage)
    public void pickImage(View view){

        //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PICK_IMAGE);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK) {
            beginCrop(data.getData());
        }

        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            final Uri resultUri = UCrop.getOutput(data);
            String avatarKey = UUID.randomUUID().toString();

            uploadImage(resultUri, avatarKey);

        }
    }

    private void beginCrop(Uri sourceUri) {
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "avatar"));
        UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(1, 1)
                .start(this);
    }

    private void uploadImage(final Uri imageUri, final String avatarKey) {

        FirebaseStorage
                .getInstance()
                .getReference()
                .child("users" + File.separator + user.getUserId() + File.separator + avatarKey)
                .putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String oldAvatarPath = "users" + File.separator + user.getUserId() + File.separator + user.getAvatar();
                deleteAvatarFromFireBase(oldAvatarPath);

                civAvatar.setImageURI(imageUri);

                FirebaseDatabase.getInstance().getReference().child("users/" + user.getUserId() + "/avatar").setValue(avatarKey);

                user.setAvatar(avatarKey);
                UserDataSharedPreference.getInstance(SettingsActivity.this).saveUserData(user);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //TODO:
                Toast.makeText(SettingsActivity.this, "Failed to upload", Toast.LENGTH_LONG).show();
            }
        });


    }

    private void deleteAvatarFromFireBase(String avatarPath){
        FirebaseStorage.getInstance().getReference().child(avatarPath).delete();
    }

    private void deleteAvatar(){
        String avatarKey = user.getAvatar();
        FirebaseDatabase.getInstance().getReference().child("users" + File.separator + user.getUserId() + "/avatar").setValue(StaticVar.STR_DEFAULT_AVATAR);
        ImageUtils.deleteImage(avatarKey);
    }


    @Override
    protected void onStop() {
        super.onStop();
        setResult(RESULT_OK);
    }
}
