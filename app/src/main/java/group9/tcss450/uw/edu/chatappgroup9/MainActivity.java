package group9.tcss450.uw.edu.chatappgroup9;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private final String INVALID_LOGIN_INFO = "Invalid username or password";
    private final int MIN_LENGTH_USERNAME_PASSWORD = 6;
    /** Regular expression**/
    private final Pattern REG_EX_USERNAME = Pattern.compile("[^a-zA-Z_0-9]");
    private final Pattern REG_EX_PASSWORD =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*(_|[^\\w])).+$");
    private EditText myUsername;
    private EditText myPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myUsername = findViewById(R.id.mainEditTextUserName);
        myPassword = findViewById(R.id.mainEditTextPassword);


    }

    public void loginOnClicked(View view){
        Intent intent = new Intent(this, NavigationActivity.class);
        if (!isLoginInfoGood()) {
            myUsername.setError(INVALID_LOGIN_INFO);
            myPassword.setError(INVALID_LOGIN_INFO);
        } else {
            //TODO attempt to login
            startActivity(intent);
        }

    }

    /**
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
        } else if (REG_EX_USERNAME.matcher(username).find()) {
            result = false;
            Log.e("Main", "Username contains only A-Z, a-z, 0-9 and _ :" + username);
        }
        else if (TextUtils.isEmpty(password)) {
            result = false;
        } else if (password.length() < MIN_LENGTH_USERNAME_PASSWORD) {
            result = false;
        } else if (!REG_EX_PASSWORD.matcher(password).matches()) {
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
