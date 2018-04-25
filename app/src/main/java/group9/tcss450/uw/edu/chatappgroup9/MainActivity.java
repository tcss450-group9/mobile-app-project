package group9.tcss450.uw.edu.chatappgroup9;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private final String EMPTY_USERNAME = "Username cannot be empty";
    private final String EMPTY_PASSWORD = "Password cannot be empty";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void loginOnClicked(View view){
        Intent intent = new Intent(this, NavigationActivity.class);
        EditText name = (EditText) findViewById(R.id.userNameEditText);
        EditText password = (EditText) findViewById(R.id.passwordEditText);

        if (TextUtils.isEmpty(name.getText().toString())) {
            name.setError(EMPTY_USERNAME);
        }
        else if (TextUtils.isEmpty(password.getText().toString())) {
            password.setError(EMPTY_PASSWORD);
        } else {
            //TODO login success
            startActivity(intent);
        }
    }

    public void registerOnClicked(View view) {
        Intent intent = new Intent(this, registerActivity.class);
            startActivity(intent);

    }
}
