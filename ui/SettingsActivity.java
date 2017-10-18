package sushchak.bohdan.curabitur.ui;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.io.FileNotFoundException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import sushchak.bohdan.curabitur.R;
import sushchak.bohdan.curabitur.utils.ImageUtils;

public class SettingsActivity extends AppCompatActivity {

    private static final int REQUEST_PICK_IMAGE = 33;

    @BindView(R.id.circle_image_view) CircleImageView civAvatar;

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

        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK){
            if(data != null){
                try {

                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    Bitmap imgBitmap = ImageUtils.cropToSquare(BitmapFactory.decodeStream(inputStream));

                    civAvatar.setImageBitmap(imgBitmap);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
