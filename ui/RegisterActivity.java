package sushchak.bohdan.curabitur.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sushchak.bohdan.curabitur.R;
import sushchak.bohdan.curabitur.data.StaticVar;

public class RegisterActivity extends AppCompatActivity {

    private final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    EditText editTextUsername;
    EditText editTextPassword;
    EditText editTextRepeatPassword;

    public static String STR_EXTRA_ACTION_REGISTER = "register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void clickRegister(View view) {

        editTextUsername = (EditText) findViewById(R.id.etEmail);
        editTextPassword = (EditText) findViewById(R.id.etPassword);
        editTextRepeatPassword = (EditText) findViewById(R.id.etRePassword);

        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();
        String repeatPassword = editTextRepeatPassword.getText().toString();
        if (validate(username, password, repeatPassword)) {
            Intent data = new Intent();
            data.putExtra(StaticVar.STR_EXTRA_USERNAME, username);
            data.putExtra(StaticVar.STR_EXTRA_PASSWORD, password);
            data.putExtra(StaticVar.STR_EXTRA_ACTION, STR_EXTRA_ACTION_REGISTER);
            setResult(RESULT_OK, data);
            finish();
        } else {
            Toast.makeText(this, "Invalid email or not match password", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validate(String emailStr, String password, String repeatPassword) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return password.length() > 0 && repeatPassword.equals(password) && matcher.find();
    }
}
