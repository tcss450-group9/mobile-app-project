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

import group9.tcss450.uw.edu.chatappgroup9.utils.SendPostAsyncTask;

public class VerificationActivity extends AppCompatActivity {

    private final int MIN_LENGTH_PIN = 4;
    private final String PIN_EMPTY = "Validation Pin cannot be empty";
    private final String PIN_TOO_SHORT = "Validation Pin is too short";
    private final String VERIFICATION_ERR = "That is the incorrect Verification Pin!";

    private String myUsername;
    private int myInputPin;
    private EditText myVerificationPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        //Get the bundle; pass in username so Verification knows how to verify pin
        Bundle bundle = getIntent().getExtras();

        //Extract the data…
        myUsername = bundle.getString("username");

        myVerificationPin = findViewById(R.id.verificationEditTextPin);

        Button validate = findViewById(R.id.verificationButtonValidateAccount);
        validate.setOnClickListener(this::verifyOnClicked);
    }

    public void verifyOnClicked(View theValidateButton){
        if (isVerificationPinGood(theValidateButton)) {
            String pinStr = myVerificationPin.getText().toString();
            myInputPin = Integer.parseInt(pinStr);
            onVerificationAttempt();
        }
    }

    private boolean isVerificationPinGood(View theValidateButton) {
        boolean result = true;
        String pin = myVerificationPin.getText().toString();

        if (TextUtils.isEmpty(pin)) {
            result = false;
            myVerificationPin.setError(PIN_EMPTY);
        } else if (pin.length() < MIN_LENGTH_PIN) {
            result = false;
            myVerificationPin.setError(PIN_TOO_SHORT);
        }

        return result;
    }

    /**
     * Attempts to check if verification pin matches user. It will fail when incorrect pin is used.
     */
    private void onVerificationAttempt() {

        //build the web server URL
        Uri uri = new Uri.Builder().scheme("https").appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_verification)).build();

        //build the JSON object
        JSONObject msg = new JSONObject();

        try {
            msg.put("username", myUsername);
        } catch (JSONException e) {
            Log.wtf("CREDENTIALS", "Error creating JSON: " + e.getMessage());
        }

        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleVerificationOnPost)
                .build().execute();
    }

    /**
     * handles the registration response returned from the server.
     * the response is in JSON format.
     * @param theResult the JSON formatted String response from the web service
     */
    private void handleVerificationOnPost(final String theResult) {
        try {
            JSONObject resultJSON = new JSONObject(theResult);
            boolean success = resultJSON.getBoolean(getString(R.string.keys_json_success));
            String failReason = null;

            if (!success) {
                failReason = resultJSON.getJSONObject(getString(R.string.keys_json_error))
                        .getString(getString(R.string.keys_json_detail));
            }

            if (success) {
                int retrievedPin = resultJSON.getJSONObject(getString(R.string.keys_json_messages))
                        .getInt(getString(R.string.keys_json_verification));

                if (myInputPin == retrievedPin) {
                    Toast.makeText(getApplicationContext(),
                            "Registration Success!", Toast.LENGTH_LONG).show();

                    backToLogin();
                } else {
                    Toast.makeText(getApplicationContext(),
                            VERIFICATION_ERR, Toast.LENGTH_LONG).show();
                }

            } else {
                Log.e("Verification Activity","Verification fail reason: " + failReason);
            }
        } catch (JSONException e) {
            Log.e("JSON parse error",theResult + System.lineSeparator()
                    + e.getMessage()+e.getLocalizedMessage());
        }
    }

    private void backToLogin() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }
}
