package sushchak.bohdan.curabitur.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sushchak.bohdan.curabitur.MainActivity;
import sushchak.bohdan.curabitur.R;
import sushchak.bohdan.curabitur.data.StaticVar;
import sushchak.bohdan.curabitur.model.User;

public class RegisterActivity extends AppCompatActivity {

    private final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @BindView(R.id.etEmail) TextInputEditText etUserEmail;
    @BindView(R.id.etPassword_Login) TextInputEditText etPassword;
    @BindView(R.id.etRePassword) TextInputEditText etRepeatPassword;

    private AuthRegisterUtils authRegisterUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(RegisterActivity.this);

        authRegisterUtils = new AuthRegisterUtils();

    }

    @OnClick(R.id.btnRegister)
    public void clickRegister(View view) {
        String email = etUserEmail.getText().toString();
        String password = etPassword.getText().toString();
        String repeatPassword = etRepeatPassword.getText().toString();

        if (validate(email, password, repeatPassword)) {
            authRegisterUtils.createUser(email, password);
        } else {
            Toast.makeText(this, "Invalid email or not match password", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        RegisterActivity.this.finish();
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }

    private boolean validate(String emailStr, String password, String repeatPassword) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return password.length() > 0 && repeatPassword.equals(password) && matcher.find();
    }

    private class AuthRegisterUtils {

        private FirebaseAuth mAuth;
        private LovelyProgressDialog waitingDialog;

        AuthRegisterUtils() {
            mAuth = FirebaseAuth.getInstance();
            waitingDialog = new LovelyProgressDialog(RegisterActivity.this).setCancelable(false);
        }

        public void createUser(final String email, String password) {

                waitingDialog.setIcon(android.R.drawable.btn_radio)
                        .setTitle("Registering....")
                        .setTopColorRes(R.color.colorPrimary)
                        .show();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                waitingDialog.dismiss();
                                if (task.isSuccessful()) {

                                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                    initNewUserInfo(email, userId);
                                    Toast.makeText(RegisterActivity.this, "Register and Login success", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                    RegisterActivity.this.finish();

                                } /*else {
                            new LovelyInfoDialog(LoginActivity.this) {
                                @Override
                                public LovelyInfoDialog setConfirmButtonText(String text) {
                                    findView(com.yarolegovich.lovelydialog.R.id.ld_btn_confirm).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dismiss();
                                        }
                                    });
                                    return super.setConfirmButtonText(text);
                                }
                            }
                        }
                                    .setTopColorRes(R.color.colorAccent)
                                    .setIcon(android.R.drawable.btn_radio)
                                    .setTitle("Register false")
                                    .setMessage("Email exist or weak password!")
                                    .setConfirmButtonText("ok")
                                    .setCancelable(false)
                                    .show();

                        })*/
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //waitingDialog.dismiss();}
                            }
                        });
        }

        public void initNewUserInfo(String email, String userId) {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(email.substring(0, email.indexOf("@")));
            newUser.setAvatar(StaticVar.STR_DEFAULT_AVATAR);
            newUser.setPhone("none");
            FirebaseDatabase.getInstance().getReference().child("users/" + userId).setValue(newUser);

        }


    }
}




