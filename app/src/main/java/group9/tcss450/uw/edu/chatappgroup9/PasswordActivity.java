package group9.tcss450.uw.edu.chatappgroup9;

import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import group9.tcss450.uw.edu.chatappgroup9.utils.SendPostAsyncTask;

public class PasswordActivity extends AppCompatActivity implements ForgotPasswordFragment.OnFragmentInteractionListener , ResetFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        ForgotPasswordFragment fragment = new ForgotPasswordFragment();
        android.support.v4.app.FragmentManager manager  = getSupportFragmentManager();
        manager.beginTransaction().add(R.id.fragment, fragment, "")
                .addToBackStack(null)
                .commit();
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
