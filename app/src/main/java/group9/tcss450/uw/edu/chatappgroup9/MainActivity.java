package group9.tcss450.uw.edu.chatappgroup9;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import group9.tcss450.uw.edu.chatappgroup9.model.Credentials;
import group9.tcss450.uw.edu.chatappgroup9.utils.InputVerificationTool;
import group9.tcss450.uw.edu.chatappgroup9.utils.SendPostAsyncTask;

/**
 *This is the main Activity  that begins the app and also serves as the login page.
 * This activity  can call the registration activity and the forgot password activity.
 * It implements  the forgot password  fragment
 *
 *
 */
public class MainActivity extends AppCompatActivity implements ForgotPasswordFragment.OnFragmentInteractionListener  {

    private final String INVALID_LOGIN_INFO = "Invalid username or password";
    private final int MIN_LENGTH_USERNAME_PASSWORD = 6;
    private final int PIN_VERIFIED = -1;
    private EditText myUsername;
    private EditText myPassword;
    private CheckBox myStayLogin;
    private SharedPreferences mySharePrefs;

    @Override
    /**
     * This Method creates the context of the activity and act like a constructor for the activity.
     *
     * @param savedInstanceState  the saved state of the activity.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myUsername = findViewById(R.id.mainEditTextUserName);
        myPassword = findViewById(R.id.mainEditTextPassword);
        Button login = findViewById(R.id.mainButtonLogin);
        login.setOnClickListener(this::loginOnClicked);
        Button register = findViewById(R.id.mainButtonRegister);
        register.setOnClickListener(this::toRegistrationActivity);
        myStayLogin = findViewById(R.id.mainCheckboxStayLogin);

        mySharePrefs = getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);

        if (mySharePrefs != null) {
            if (mySharePrefs.getBoolean(getString(R.string.keys_prefs_stay_login), false)) {
                startActivity(new Intent(getApplicationContext(), NavigationActivity.class));
            }
        }
    }

    /**
     * Sets the on  click listener for the login submit button.
     *
     * @param view  this is the button that will use the onclick listener
     */
    public void loginOnClicked(View view){
        if (!isLoginInfoGood()) {
            myUsername.setError(INVALID_LOGIN_INFO);
            myPassword.setError(INVALID_LOGIN_INFO);
        } else {

            Credentials loginInfo = new Credentials.Builder(myUsername.getText().toString(),
                    myPassword.getEditableText()).build();

            loginAttempt(loginInfo);
        }
    }

    /**
     * Sends login information to login end point to verify.
     * @param loginInfo
     * @return true if login success; false otherwise.
     */
    private void loginAttempt(final Credentials loginInfo) {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_login))
                .build();

        JSONObject msg = loginInfo.asJSONObject();

        //instantiate and execute the AsyncTask.
        //Feel free to add a handler for onPreExecution so that a progress bar
        //is displayed or maybe disable buttons. You would need a method in
        //LoginFragment to perform this.
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleLoginOnPost)
//                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    /**
     * Handle onPostExecute of the AsyncTask. The result from our webservice is
     * a JSON formatted String. Parse it for success or failure.
     * @param result the JSON formatted String response from the web service
     */
    private void handleLoginOnPost(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean(getString(R.string.keys_json_success));


            if (success) {
                String email = resultsJSON.getString(getString(R.string.keys_json_email));
                int pin = resultsJSON.getInt(getString(R.string.keys_json_verification));

                saveUserInfoToSharedPreference(resultsJSON);
                if(pin < PIN_VERIFIED){
                    Toast.makeText(this, "Please change password", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(this, PasswordActivity.class);
                    startActivity(intent);
                } else if (pin == PIN_VERIFIED) {
                    //Login was successful. Switch to the chat page.
                    Intent intent = new Intent(this, NavigationActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), email
                            + ", Please verify your pin", Toast.LENGTH_LONG).show();
                    //User not verified. Switch to the verification page.
                    Intent intent = new Intent(this, VerificationActivity.class);

                    String getUsername = myUsername.getText().toString();

                    //Create the bundle; pass in username so Verification knows how to verify pin
                    Bundle bundle = new Bundle();

                    //Add your data to bundle
                    bundle.putString("username", getUsername);

                    //Add the bundle to the intent
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            } else {
                //Login was unsuccessful.
                Log.e("Main activity","Log in unsuccessful");
                Toast.makeText(this ,"Login Unsuccessful incorrect Username or PassWord", Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            //It appears that the web service didn’t return a JSON formatted String
            //or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR",  result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }

    /**
     * Checks if the login information is good or not.
     * This method must be called before attempt to submit the login information to the server.
     * @return true if the username and password is ready for the server; false otherwise.
     */
    private boolean isLoginInfoGood() {
        boolean result = true;
        String username = myUsername.getText().toString();
        String password = myPassword.getText().toString();


        if (TextUtils.isEmpty(username)) {
            result = false;
        } else if (username.length() < MIN_LENGTH_USERNAME_PASSWORD) {
            result = false;
        } else if (!InputVerificationTool.isUsername(username)) {
            result = false;
            Log.e("Main", "Username contains only A-Z, a-z, 0-9 and _ :" + username);
        }
        else if (TextUtils.isEmpty(password)) {
            result = false;
        } else if (password.length() < MIN_LENGTH_USERNAME_PASSWORD) {
            result = false;
        } else if (!InputVerificationTool.isPassword(password)) {
            result = false;
            Log.e("Main",
                    "Password needs at least one number " +
                    "and one lower case letter and one " +
                    "upper case letter and one special character: " + password);
        }
        return result;
    }

    public void toRegistrationActivity(View view) {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
        this.finish();
    }


    /**
     * This method is called on login success;
     * it will save the user information in the shared preferences.
     * @param resultsJSON  this is the return for the webservice call with the login information.
     */
    private void saveUserInfoToSharedPreference(JSONObject resultsJSON) {
        SharedPreferences prefs =
                getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        if (myStayLogin.isChecked()) {

            //save the username for later usage
            prefs.edit().putString(
                    getString(R.string.keys_shared_prefs_username),
                    myUsername.getText().toString())
                    .apply();
            //save the users “want” to stay logged in
            prefs.edit().putBoolean(
                    getString(R.string.keys_prefs_stay_login),
                    true)
                    .apply();
        }
        try {
            String username = resultsJSON.getString(getString(R.string.keys_json_username));
            String firstname = resultsJSON.getString(getString(R.string.keys_json_firstname));
            String lastname = resultsJSON.getString(getString(R.string.keys_json_lastname));
            String email = resultsJSON.getString(getString(R.string.keys_json_email));
            String memberid = resultsJSON.getString(getString(R.string.keys_json_memberid));

            prefs.edit().putString(getString(R.string.keys_shared_prefs_username), username).apply();
            prefs.edit().putString(getString(R.string.keys_shared_prefs_firstname), firstname).apply();
            prefs.edit().putString(getString(R.string.keys_shared_prefs_lastname), lastname).apply();
            prefs.edit().putString(getString(R.string.keys_shared_prefs_email), email).apply();
            prefs.edit().putString(getString(R.string.keys_shared_prefs_memberid), memberid).apply();

        } catch (JSONException e) {
            Log.e("MainActivity", e.getMessage());
        }


    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
