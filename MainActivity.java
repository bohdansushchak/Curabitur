package sushchak.bohdan.curabitur;



import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import sushchak.bohdan.curabitur.data.StaticVar;
import sushchak.bohdan.curabitur.model.Contact;
import sushchak.bohdan.curabitur.model.Thread;
import sushchak.bohdan.curabitur.model.User;
import sushchak.bohdan.curabitur.ui.ChatActivity;
import sushchak.bohdan.curabitur.ui.ContactsFragment;
import sushchak.bohdan.curabitur.ui.LoginActivity;
import sushchak.bohdan.curabitur.ui.ThreadsFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        ThreadsFragment.OnListFragmentInteractionListener,
        ContactsFragment.OnListFragmentInteractionListener
{
    private static String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private FragmentTransaction fragmentTransaction;
    private Fragment threadsFragment;
    private Fragment contactsFragment;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.nav_view) NavigationView navigationView;

    private CircleImageView civUserAvatar;
    private TextView tvEmailUser;
    private TextView tvUserName;
    private LinearLayout backgroundUserLayout;

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(MainActivity.this);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        civUserAvatar = (CircleImageView) header.findViewById(R.id.civUserAvatar);
        tvEmailUser = (TextView) header.findViewById(R.id.tvEmailUser);
        tvUserName = (TextView) header.findViewById(R.id.tvUserName);
        backgroundUserLayout = (LinearLayout) header.findViewById(R.id.backgroundUserLayout);

        initFireBase();


    }

    private void initFireBase(){
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    FirebaseDatabase.getInstance().getReference()
                            .child("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.getValue() != null){
                                        HashMap mapContactData = (HashMap) dataSnapshot.getValue();
                                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        String name = mapContactData.get("name").toString();
                                        String email = mapContactData.get("email").toString();
                                        String avatar = mapContactData.get("avatar").toString();
                                        String phone = mapContactData.get("phone").toString();

                                        currentUser = new User(userId, name, email, avatar, "online", phone);
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                    threadsFragment = new ThreadsFragment();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.add(R.id.frameLayout, threadsFragment);
                    fragmentTransaction.commit();
                } else {
                    MainActivity.this.finish();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }

            }
        };
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_friends:{

                toolbar.setNavigationIcon(R.drawable.ic_menu_send);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        threadsFragment = new ThreadsFragment();
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.detach(contactsFragment);
                        fragmentTransaction.add(R.id.frameLayout, threadsFragment);
                        fragmentTransaction.commit();
                    }
                });

                toolbar.setTitle(getResources().getString(R.string.nav_friends));
                contactsFragment = new ContactsFragment();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.detach(threadsFragment);
                fragmentTransaction.add(R.id.frameLayout, contactsFragment);
                fragmentTransaction.commit();
                break;
            }

            case R.id.nav_out: {
                mAuth.signOut();
                break;
            }
            /*case R.id.nav_share:{
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1994);
                break;
            }*/

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       /* if (requestCode == 1994 && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                Bitmap imgBitmap = BitmapFactory.decodeStream(inputStream);
                imgBitmap = ImageUtils.cropToSquare(imgBitmap);
                InputStream is = ImageUtils.convertBitmapToInputStream(imgBitmap);
                final Bitmap liteImage = ImageUtils.makeImageLite(is,
                        imgBitmap.getWidth(), imgBitmap.getHeight(),
                        ImageUtils.AVATAR_WIDTH, ImageUtils.AVATAR_HEIGHT);

                String imageBase64 = ImageUtils.encodeBase64(liteImage);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }*/
    }

    @Override
    public void onListFragmentInteraction(Thread item) {
        Log.d(TAG, item.getThread_id());
        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        intent.putExtra(StaticVar.STR_EXTRA_CHAT_ID, item.getThread_id());
        startActivity(intent);
    }

    @Override
    public void onListFragmentInteraction(Contact item) {
        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        intent.putExtra(StaticVar.STR_EXTRA_CONTACT_ID, item.contactId);
        intent.putExtra(StaticVar.STR_EXTRA_CONTACT_NAME, item.name);
        startActivity(intent);

    }
}
