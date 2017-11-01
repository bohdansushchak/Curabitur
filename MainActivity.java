package sushchak.bohdan.curabitur;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import sushchak.bohdan.curabitur.data.StaticVar;
import sushchak.bohdan.curabitur.data.UserDataSharedPreference;
import sushchak.bohdan.curabitur.model.ThreadData;
import sushchak.bohdan.curabitur.model.User;
import sushchak.bohdan.curabitur.ui.ChatActivity;
import sushchak.bohdan.curabitur.ui.ChatsFragment;
import sushchak.bohdan.curabitur.ui.ContactsFragment;
import sushchak.bohdan.curabitur.ui.LoginActivity;
import sushchak.bohdan.curabitur.ui.SettingsActivity;
import sushchak.bohdan.curabitur.utils.ChatUtils;
import sushchak.bohdan.curabitur.utils.ImageUtils;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        ChatsFragment.ThreadFragmentInteractionListener,
        ContactsFragment.ContactsFragmentInteractionListener
{
    public static final String TAG = "MainActivity";

    private final int REQUEST_SETTINGS_CHANGE_DATA = 33;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ChatsFragment chatsFragment;
    private ContactsFragment contactsFragment;

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

        this.navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        this.civUserAvatar = (CircleImageView) header.findViewById(R.id.civUserAvatar_navHeader);
        this.tvEmailUser = (TextView) header.findViewById(R.id.tvEmailUser_navHeader);
        this.tvUserName = (TextView) header.findViewById(R.id.tvUserName_navHeader);
        this.backgroundUserLayout = (LinearLayout) header.findViewById(R.id.backgroundUserLayout);

        initFireBase();
    }


    private void initFragments(){
        chatsFragment = new ChatsFragment();
        contactsFragment = new ContactsFragment();

        if(getSupportFragmentManager().getBackStackEntryCount() == 0)
            replaceFragment(chatsFragment);
        else {
            getSupportFragmentManager().popBackStack();
            replaceFragment(chatsFragment);
        }
    }

    private void replaceFragment (Fragment fragment){
        String backStateName = fragment.getClass().getName();

        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);

        if (!fragmentPopped) {
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.frameLayout, fragment);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    private void initFireBase(){
        this.mAuth = FirebaseAuth.getInstance();

        this.mAuthListener = new FirebaseAuth.AuthStateListener() {
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

                                        MainActivity.this.currentUser = new User(userId, name, email, avatar, phone);

                                        UserDataSharedPreference.getInstance(MainActivity.this).saveUserData(currentUser);
                                        setUserData();
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                    initFragments();

                } else {
                    MainActivity.this.finish();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }

            }
        };
    }

    private void setUserData(){

        this.tvEmailUser.setText(currentUser.getEmail());
        this.tvUserName.setText(currentUser.getName());

        WeakReference<CircleImageView> reference = new WeakReference<>(civUserAvatar);
        ImageUtils.setUserAvatar(reference, null,currentUser, R.drawable.user_avatar_default);

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
                replaceFragment(contactsFragment);

                break;
            }

            case R.id.nav_out: {
                mAuth.signOut();
                break;
            }
            case R.id.nav_share:{

                break;
            }

            case R.id.nav_setting:{
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(intent, REQUEST_SETTINGS_CHANGE_DATA);
                break;
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_SETTINGS_CHANGE_DATA && resultCode == RESULT_OK){
            currentUser = UserDataSharedPreference.getInstance(MainActivity.this).getUserData();
            setUserData();
        }
    }

    @Override
    public void threadFragmentInteractionClick(final ThreadData item, int clickType) {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1)
            getSupportFragmentManager().popBackStack();

        switch (clickType){
            case ChatsFragment.ThreadFragmentInteractionListener.CLICK:
            {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                intent.putExtra(StaticVar.STR_EXTRA_CHAT_ID, item.getThread_id());
                startActivity(intent);
                break;
            }
        }
    }

    @Override
    public void onBackPressed(){
        if (getSupportFragmentManager().getBackStackEntryCount() == 1){
            finish();
        }
        else {
            super.onBackPressed();
        }
    }


    @Override
    public void contactsFragmentInteractionClick(User item) {
        Intent intent = new Intent(MainActivity.this, ChatActivity.class);

        intent.putExtra(StaticVar.STR_EXTRA_CONTACT_ID, item.getUserId());
        intent.putExtra(StaticVar.STR_EXTRA_CONTACT_NAME, item.getName());
        startActivity(intent);

    }
}
