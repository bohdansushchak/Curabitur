package sushchak.bohdan.curabitur.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sushchak.bohdan.curabitur.MainActivity;
import sushchak.bohdan.curabitur.R;
import sushchak.bohdan.curabitur.data.StaticVar;
import sushchak.bohdan.curabitur.data.UserDataSharedPreference;
import sushchak.bohdan.curabitur.model.User;

public class LoginActivity extends AppCompatActivity implements
        View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {

    private static String TAG = "LoginActivity";
    private final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private static final int REQUEST_CODE_SING_GOOGLE = 555;

    @BindView(R.id.etEmail_Login) TextInputEditText editTextUsername;
    @BindView(R.id.etPassword_Login) TextInputEditText editTextPassword;

    private GoogleApiClient mGoogleApiClient;

    private AuthLoginUtils authLoginUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(LoginActivity.this);

        initFireBase();
    }

    private void initFireBase() {

        authLoginUtils = new AuthLoginUtils();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(LoginActivity.this)
                .enableAutoManage(LoginActivity.this, LoginActivity.this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_SING_GOOGLE && resultCode == RESULT_OK){
           /* waitingDialog.setIcon(android.R.drawable.btn_radio)
                    .setTitle("Login....")
                    .setTopColorRes(R.color.colorPrimary)
                    .show();*/

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d(TAG, result.getStatus().toString());
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                authLoginUtils.fireBaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
                //TODO create alertDialog
            }
        }
    }

    private boolean validate(String emailStr, String password) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return (password.length() > 0 || password.equals(";")) && matcher.find();
    }

    @OnClick({R.id.sing_in_button, R.id.btnLogin, R.id.tvRegister_Login})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sing_in_button:{
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, REQUEST_CODE_SING_GOOGLE);
                break;
            }
            case R.id.btnLogin:{
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();
                if (validate(username, password)) {
                    authLoginUtils.singIn(username, password);
                } else {
                    Toast.makeText(this, "Invalid email or empty password", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.tvRegister_Login:{
                LoginActivity.this.finish();
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class AuthLoginUtils {

        private FirebaseAuth mAuth;

        private FirebaseUser firebaseUseruser;

        private LovelyProgressDialog waitingDialog;

        AuthLoginUtils(){
            mAuth = FirebaseAuth.getInstance();
            waitingDialog = new LovelyProgressDialog(LoginActivity.this).setCancelable(false);
        }

        public void singIn(String email, String password) {
            waitingDialog.setIcon(android.R.drawable.btn_radio)
                    .setTitle("Login....")
                    .setTopColorRes(R.color.colorPrimary)
                    .show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    waitingDialog.dismiss();
                    if (task.isSuccessful()) {
                        //saveUserData();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));

                    } else {
                        Log.w(TAG, "signInWithEmail:failed", task.getException());
                        new LovelyInfoDialog(LoginActivity.this) {
                            @Override
                            public LovelyInfoDialog setConfirmButtonText(String text) {
                                findView(com.yarolegovich.lovelydialog.R.id.ld_btn_confirm)
                                        .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dismiss();
                                    }
                                });
                                return super.setConfirmButtonText(text);
                            }
                        }
                                .setTopColorRes(R.color.colorAccent)
                                .setIcon(android.R.drawable.btn_radio)
                                .setTitle("Login false")
                                .setMessage("Email not exist or wrong password!")
                                .setCancelable(false)
                                .setConfirmButtonText("Ok")
                                .show();
                    }
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // waitingDialog.dismiss();
                        }
                    });
        }

        public void fireBaseAuthWithGoogle(GoogleSignInAccount acct) {
            waitingDialog.setIcon(android.R.drawable.btn_radio)
                    .setTitle("Login....")
                    .setTopColorRes(R.color.colorPrimary)
                    .show();

            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "signInWithCredential:success");
                                firebaseUseruser = mAuth.getCurrentUser();

                                FirebaseDatabase.getInstance().getReference().child("users/" + firebaseUseruser.getUid())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(!dataSnapshot.exists())
                                            initNewUserInfo(firebaseUseruser);
                                        //else saveUserData();
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                waitingDialog.dismiss();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                LoginActivity.this.finish();

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }

                            // ...
                        }
                    });
        }

        private void initNewUserInfo(FirebaseUser user) {
            User newUser = new User();
            newUser.setEmail(user.getEmail());
            if(user.getDisplayName() != null){
                newUser.setName(user.getDisplayName());
            }else {
                newUser.setName(user.getEmail().substring(0, user.getEmail().indexOf("@")));
            }
            newUser.setAvatar(StaticVar.STR_DEFAULT_AVATAR); //TODO image
            newUser.setPhone("none");
            FirebaseDatabase.getInstance().getReference().child("users/" + user.getUid()).setValue(newUser);

            UserDataSharedPreference.getInstance(LoginActivity.this).saveUserData(newUser);
        }

       /* private void saveUserData(){
            FirebaseDatabase.getInstance().getReference().child("users/" + mAuth.getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        HashMap mapContactData = (HashMap) dataSnapshot.getValue();
                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        String name = mapContactData.get("name").toString();
                        String email = mapContactData.get("email").toString();
                        String avatar = mapContactData.get("avatar").toString();
                        String phone = mapContactData.get("phone").toString();

                        User user = new User(userId, name, email, avatar, phone);

                        UserDataSharedPreference.getInstance(LoginActivity.this).saveUserData(user);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

*/

    }
}


