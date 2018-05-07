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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import group9.tcss450.uw.edu.chatappgroup9.model.Credentials;
import group9.tcss450.uw.edu.chatappgroup9.utils.InputVerificationTool;
import group9.tcss450.uw.edu.chatappgroup9.utils.SendPostAsyncTask;

public class MainActivity extends AppCompatActivity {

    private final String INVALID_LOGIN_INFO = "Invalid username or password";
    private final int MIN_LENGTH_USERNAME_PASSWORD = 6;
    private EditText myUsername;
    private EditText myPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myUsername = findViewById(R.id.mainEditTextUserName);
        myPassword = findViewById(R.id.mainEditTextPassword);
        Button login = findViewById(R.id.mainButtonLogin);
        login.setOnClickListener(this::loginOnClicked);

    }

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
                //Login was successful. Switch to the chat page.
                Intent intent = new Intent(this, NavigationActivity.class);
                startActivity(intent);
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

    }

}
