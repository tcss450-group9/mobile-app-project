package group9.tcss450.uw.edu.chatappgroup9;

import android.app.FragmentManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PasswordActivity extends AppCompatActivity implements ForgotPasswordFragment.OnFragmentInteractionListener {

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
