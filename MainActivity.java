package sushchak.bohdan.curabitur;



import android.content.Intent;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.HashMap;

import sushchak.bohdan.curabitur.data.StaticVar;
import sushchak.bohdan.curabitur.model.Contact;
import sushchak.bohdan.curabitur.model.Thread;
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
    private FirebaseUser user;

    private FragmentTransaction fragmentTransaction;
    private Fragment threadsFragment;
    private Fragment contactsFragment;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        initFireBase();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void initFireBase(){
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user != null){
                    FirebaseDatabase.getInstance().getReference()
                            .child("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.getValue() != null){
                                        HashMap mapContactData = (HashMap) dataSnapshot.getValue();
                                        StaticVar.currentUser = new Contact();
                                        StaticVar.currentUser.contactId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        StaticVar.currentUser.name =  mapContactData.get("name").toString();
                                        StaticVar.currentUser.email = mapContactData.get("email").toString();
                                        StaticVar.currentUser.avatar = mapContactData.get("avatar").toString();
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
                    Log.d(TAG, "onAuthStateChanged:signed_out");
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

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onListFragmentInteraction(Thread item) {
        Log.d(TAG, item.thread_id);
        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        intent.putExtra(StaticVar.STR_EXTRA_CHAT_ID, item.thread_id);
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
