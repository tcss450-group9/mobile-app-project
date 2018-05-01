package group9.tcss450.uw.edu.chatappgroup9;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import group9.tcss450.uw.edu.chatappgroup9.model.Credentials;
import group9.tcss450.uw.edu.chatappgroup9.utils.SendPostAsyncTask;

public class RegistrationActivity extends AppCompatActivity {

    private int MIN_PASSWORD_LENGTH = 6;
    private final String USERNAME_EMPTY = "Username cannot be empty";
    private final String USERNAME_TOO_SHORT = "Username is too short";
    private final String FIRST_NAME_EMPTY = "First name cannot be empty";
    private final String LAST_NAME_EMPTY = "Last name cannot be empty";
    private final String PASSWORD_NOT_MATCH = "Passwords are not match";
    private final String PASSWORD_TOO_SHORT = "Password is too short";
    private final String EMAIL_INVALID = "Email address is invalid";
    private final String INVALID_CHARACTER = "Invalid character contained";
    private final String PASSWORD_TOO_SIMPLE = "Password is too simple";
    private final String USERNAME_EXIST = "Username has already exist";
    private final String EMAIL_EXIST = "Email has already registered";
    /** Regular expression**/
    private final Pattern REG_EX_USERNAME = Pattern.compile("[^a-zA-Z_0-9]");
    private final Pattern REG_EX_NAME = Pattern.compile("[^a-zA-Z]");
    private final Pattern REG_EX_PASSWORD =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*(_|[^\\w])).+$");

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
        if (isRegistrationInfoGood(theSubmitButton)) {
            final Credentials info = new Credentials.Builder(myUsername.getText().toString(),
                    myPassword.getEditableText())
                    .addEmail(myEmail.getText().toString())
                    .addFirstName(myFirstName.getText().toString())
                    .addLastName(myLastName.getText().toString()).build();

            onRegisterAttempt(info);


        }
    }

    /**
     * This method must be called before submit onRegisterAttempt() to make sure all registration
     * information meet the requirement.
     * @return true if the registration information is ready to for the server; false otherwise.
     */
    private boolean isRegistrationInfoGood(View theSubmitButton) {
        boolean result = true;
        String email = myEmail.getText().toString();
        String firstname = myFirstName.getText().toString();
        String lastname = myLastName.getText().toString();
        String username = myUsername.getText().toString();
        String password = myPassword.getText().toString();
        String confirmPassword = myConfirmPassword.getText().toString();

        if (!isValidEmail(email)) {
            result = false;
            myEmail.setError(EMAIL_INVALID);
        }

        if (TextUtils.isEmpty(firstname)) {
            result = false;
            myFirstName.setError(FIRST_NAME_EMPTY);
        }else if (REG_EX_NAME.matcher(firstname).find()) {
            result = false;
            myFirstName.setError(INVALID_CHARACTER);
        }

        if (TextUtils.isEmpty(lastname)) {
            result = false;
            myLastName.setError(LAST_NAME_EMPTY);
        } else if (REG_EX_NAME.matcher(lastname).find()) {
            result = false;
            myLastName.setError(INVALID_CHARACTER);
        }

        if (TextUtils.isEmpty(username)) {
            result = false;
            myUsername.setError(USERNAME_EMPTY);
        } else if (REG_EX_USERNAME.matcher(username).find()) {
            result = false;
            myUsername.setError(INVALID_CHARACTER);
        } else if (username.length() < MIN_PASSWORD_LENGTH) {
            result = false;
            myUsername.setError(USERNAME_TOO_SHORT);
        }

        //password and confirm password check
        if (!password.equals(confirmPassword)) {
            result = false;
            myPassword.setError(PASSWORD_NOT_MATCH);
            myConfirmPassword.setError(PASSWORD_NOT_MATCH);
        } else if (password.length() < MIN_PASSWORD_LENGTH) {
            result = false;
            myPassword.setError(PASSWORD_TOO_SHORT);
            myConfirmPassword.setError(PASSWORD_TOO_SHORT);
        } else if (!REG_EX_PASSWORD.matcher(password).matches()) {
            result = false;
            myPassword.setError(PASSWORD_TOO_SIMPLE);
            myConfirmPassword.setError(PASSWORD_TOO_SIMPLE);
        }

        return result;
    }

    /**
     * Attempts to submit the registration to the server. It will fail when username or email has
     * already registered.
     * @param registrationInfo
     */
    private void onRegisterAttempt(final Credentials registrationInfo) {

        //build the web server URL
        Uri uri = new Uri.Builder().scheme("https").appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_register)).build();

        //build the JSON object
        JSONObject msg = registrationInfo.asJSONObject();

        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleRegistrationOnPost)
                .build().execute();
    }


    private void handleRegistrationOnPost(final String theResult) {
        try {
            JSONObject resultJSON = new JSONObject(theResult);
            boolean success = resultJSON.getBoolean("success");
            String failReason = null;
            if (!success) {
               failReason = resultJSON.getJSONObject("error").getString("detail");
            }

            if (success) {
                Toast.makeText(getApplicationContext(),
                        "Registration success", Toast.LENGTH_LONG).show();
                backToLogin();
            } else if (failReason.contains("username")) {
                myUsername.setError(USERNAME_EXIST);
                Toast.makeText(getApplicationContext(),
                        USERNAME_EXIST, Toast.LENGTH_LONG).show();
            } else if (failReason.contains("email")) {
                myEmail.setError(EMAIL_EXIST);
                Toast.makeText(getApplicationContext(),
                        EMAIL_EXIST, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This", Toast.LENGTH_LONG).show();
                Log.e("Registration Activity","Registration fail: " + failReason);
            }

        } catch (JSONException e) {
            Log.e("JSON parse error",theResult + System.lineSeparator()
                    + e.getMessage()+e.getLocalizedMessage());
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

