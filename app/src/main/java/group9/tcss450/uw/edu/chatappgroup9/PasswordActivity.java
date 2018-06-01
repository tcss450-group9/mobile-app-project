package group9.tcss450.uw.edu.chatappgroup9;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import group9.tcss450.uw.edu.chatappgroup9.utils.SendPostAsyncTask;

/**
 * This is the activity that loads the password reset  fragments and allows the user to reset the password.
 *
 *@author Garrett Engle, Jenzel Villanueva, Cory Davis,Minqing Chen
 */
public class PasswordActivity extends AppCompatActivity implements ForgotPasswordFragment.OnFragmentInteractionListener , ResetFragment.OnFragmentInteractionListener{

    @Override
    /**
     * This creates the activity and the layout.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        ForgotPasswordFragment fragment = new ForgotPasswordFragment();
        android.support.v4.app.FragmentManager manager  = getSupportFragmentManager();
        manager.beginTransaction().add(R.id.fragmentContainer, fragment, "")
                .addToBackStack(null)
                .commit();

    }

    /**
     * Loads the main activity on back press.
     */
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        this.finish();
    }

    @Override
    /**
     *
     */
    public void onFragmentInteraction(Uri uri) {

    }
}
