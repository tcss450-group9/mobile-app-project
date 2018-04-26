package group9.tcss450.uw.edu.chatappgroup9;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegistrationActivity extends AppCompatActivity {

    private static int MIN_PASSWORD_LENGTH = 6;
    private static final String USERNAME_EMPTY = "Username cannot be empty";
    private static final String FIRST_NAME_EMPTY = "First name cannot be empty";
    private static final String LAST_NAME_EMPTY = "Last name cannot be empty";
    private static final String PASSWORD_NOT_MATCH = "Passwords are not match";
    private static final String PASSWORD_TOO_SHORT = "Password less than 6 characters";
    private  static final String EMAIL_INVALID = "Email address is invalid";
    private EditText myUsername;
    private EditText myPassword;
    private EditText myConfirmPassword;
    private EditText myEmail;
    private EditText myFirstName;
    private EditText myLastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        myUsername = findViewById(R.id.registrationEditTextUsername);
        myPassword = findViewById(R.id.registrationEditTextPassword);
        myConfirmPassword = findViewById(R.id.registrationEditTextConfirmPassword);
        myEmail = findViewById(R.id.registrationEditTextEmail);
        myFirstName = findViewById(R.id.registrationEditTextFirstName);
        myLastName = findViewById(R.id.registrationEditTextLastName);

        Button submit = findViewById(R.id.registrationButtonSubmit);
        submit.setOnClickListener(this::submitOnClicked);
    }

    public void submitOnClicked(View theSubmitButton){
        verifyRegistrationInfo(theSubmitButton);
        //TODO Register new account


    }

    private void verifyRegistrationInfo(View theSubmitButton) {
        if (!isValidEmail(myEmail.getText().toString())) {
            myEmail.setError(EMAIL_INVALID);
        }
        if (myFirstName.getText().toString().isEmpty()) {
            myFirstName.setError(FIRST_NAME_EMPTY);
        }
        if (myLastName.getText().toString().isEmpty()) {
            myLastName.setError(LAST_NAME_EMPTY);
        }
        if (myUsername.getText().toString().isEmpty()) {
            myUsername.setError(USERNAME_EMPTY);
        }
        if (!myPassword.getText().toString().equals(myConfirmPassword.getText().toString())) {
            myPassword.setError(PASSWORD_NOT_MATCH);
            return;
        }
        if (myPassword.getText().toString().length() < MIN_PASSWORD_LENGTH) {
            myPassword.setError(PASSWORD_TOO_SHORT);
        } else {
            Toast.makeText(getApplicationContext(),
                    "Registration success", Toast.LENGTH_LONG).show();
            backToLogin();

        }
    }

    private void backToLogin() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private boolean isValidEmail (final String target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


}
