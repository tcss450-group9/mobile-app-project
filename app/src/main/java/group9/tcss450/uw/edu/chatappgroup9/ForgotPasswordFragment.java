package group9.tcss450.uw.edu.chatappgroup9;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import group9.tcss450.uw.edu.chatappgroup9.utils.InputVerificationTool;
import group9.tcss450.uw.edu.chatappgroup9.utils.SendPostAsyncTask;

/**
 * This fragment  sends the  request to reset the password and adds a verification code to the DB to allow the user to change the password.
 *
 *@author Garrett Engle, Jenzel Villanueva, Cory Davis,Minqing Chen
 */
public class ForgotPasswordFragment extends Fragment {


    private OnFragmentInteractionListener mListener;
    private final String TAG = "ForgotPasswordFragment";
    private EditText myEmail;

    /**
     * Required empty public constructor.
     */
    public ForgotPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    /**
     * creates the view that the view the fragment will be contained in.
     *
     * @param inflater  creates the view
     * @param container  the activity that the fragement will be in
     * @param savedInstanceState set to a previous state if one exists
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_forgot_password , container, false);
        Button b = v.findViewById(R.id.Password_Reset_Submit);
        b.setOnClickListener(this::onSubmitClickForgot);
        b = v.findViewById(R.id.Forgot_password_Button_already_submit);
        b.setOnClickListener(this::onAlreadySubmit);
        myEmail = v.findViewById(R.id.forgetPasswordEditTextEmail);
        return v;
    }

    /**
     * Chages the fragment if the user has clicked that they have an active change password request.
     *
     * @param view this is the view  that the button is contained in
     */
    public void onAlreadySubmit(View view){
        ResetFragment frag  = new ResetFragment();

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, frag, null)
                .addToBackStack(null)
                .commit();
        ((EditText) getView().findViewById(R.id.forgetPasswordEditTextEmail))
                .setText("");
    }


    @Override
    /**
     * upon the fragment being attached to the activity.
     */
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     * This sends an async task to the DB that tells it that user needs to reset their password and add a verification code to do so.
     *
     * @param view This is the button that with initiate the click.
     */
    public void onSubmitClickForgot(View view) {
        String email = myEmail.getText().toString();
        if (!InputVerificationTool.isEmail(email)) {
            myEmail.setError("Please enter an email");
            return;
        }

        int verificationPin = verificationPinGenerator();
        SharedPreferences preferences = getActivity().getSharedPreferences(getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
        preferences.edit().putInt(getString(R.string.keys_verification_pin), -verificationPin).apply();
        Log.d(TAG, "onSubmitClickForgot: here");
        Uri uri = new Uri.Builder().scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_change_pass_initiate))
                .build();

        //build the JSON object
        JSONObject msg = new JSONObject();
        try {
            msg.put(getString(R.string.keys_json_email),((EditText)getActivity().findViewById( R.id.forgetPasswordEditTextEmail)).getText().toString());
            msg.put(getString(R.string.keys_json_verification), verificationPin);
            Log.d(TAG, "JSON: " + msg.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "onSubmitClickForgot: sending async");
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleResetOnPost)
                .onCancelled(this::handleError)
                .build().execute();
    }

    public void onBackPressed() {
        startActivity(new Intent(getContext(), MainActivity.class));
        getActivity().finish();
    }

    /**
     * prints and error message on failed request
     * @param msg this the the errror message
     */
    private void handleError(final String msg) {
        Log.e("CHAT ERROR!!!", msg.toString());
    }

    private void handleResetOnPost(String result) {
        ResetFragment frag  = new ResetFragment();
        Log.d(TAG, "handleResetOnPost: finished async" + result);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, frag, "frag")
                .addToBackStack(null)
                .commit();
        ((EditText) getView().findViewById(R.id.forgetPasswordEditTextEmail))
                .setText("");
    }

    public int verificationPinGenerator() {
        Random random = new Random();
        int value = random.nextInt(10000);
        return value * -1;
    }

    @Override
    /**
     * this is what the fragment will do on detach from the context.
     */
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * does nothing
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
