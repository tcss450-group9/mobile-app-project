package group9.tcss450.uw.edu.chatappgroup9;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class VerificationActivity extends AppCompatActivity {

    private EditText myVerificationPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        myVerificationPin = findViewById(R.id.verificationEditTextPin);

        Button submit = findViewById(R.id.verificationButtonVerifyAccount);
        submit.setOnClickListener(this::submitOnClicked);
    }

    public void submitOnClicked(View theSubmitButton){
        onVerifyAttempt();
    }

    /**
     * Attempts to check if verification pin matches user. It will fail when incorrect pin is used.
     */
    private void onVerifyAttempt() {
        //TODO: possible server interaction to verify code
        handleVerificationOnPost();
    }

    /**
     * handles the registration response returned from the server.
     * the response is in JSON format.
     *
     */
    private void handleVerificationOnPost() {
        Toast.makeText(getApplicationContext(),
                "Registration success", Toast.LENGTH_LONG).show();
        backToLogin();
    }

    private void backToLogin() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
