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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import group9.tcss450.uw.edu.chatappgroup9.utils.InputVerificationTool;
import group9.tcss450.uw.edu.chatappgroup9.utils.SendPostAsyncTask;



public class ResetFragment extends Fragment {
    private int MIN_LENGTH_USERNAME_PASSWORD = 6;

    private final String PASSWORD_NOT_MATCH = "Passwords are not match";
    private final String PASSWORD_TOO_SHORT = "Password is too short";

    private final String PASSWORD_TOO_SIMPLE = "Password is too simple";
    private final String TAG = "ResetFragment";

    private OnFragmentInteractionListener mListener;

    public ResetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_reset , container, false);
        Button b = v.findViewById(R.id.Password_Reset_Submit);
        b.setOnClickListener(this::onSubmitClickForgot);
        Log.d(TAG, "onCreateView: here1");
        return v;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void onSubmitClickForgot(View view) {

        Log.d(TAG, "onSubmitClickForgot: here");
        SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
        String user = prefs.getString(getString(R.string.keys_shared_prefs_username), "");
        Uri uri = new Uri.Builder().scheme("https").appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_change_password1)).build();
        EditText password1 = getActivity().findViewById(R.id.Reset_Password_NewPassword1);
        EditText password2 = getActivity().findViewById(R.id.Reset_Password_NewPassword2);
        String password = password1.getText().toString();
        String confirmPassword = password2.getText().toString();
        Boolean result = true;


        if (!password.equals(confirmPassword)) {
            result = false;
            password2.setError(PASSWORD_NOT_MATCH);
            password1.setError(PASSWORD_NOT_MATCH);
        } else if (password.length() < MIN_LENGTH_USERNAME_PASSWORD) {
            result = false;
            password2.setError(PASSWORD_TOO_SHORT);
            password1.setError(PASSWORD_TOO_SHORT);
        } else if (!InputVerificationTool.isPassword(password)) {
            result = false;
            password2.setError(PASSWORD_TOO_SIMPLE);
            password1.setError(PASSWORD_TOO_SIMPLE);
        }
        //build the JSON object
        if(result) {
            JSONObject msg = new JSONObject();
            try {
                msg.put(getString(R.string.keys_json_password),
                        ((EditText) getActivity().findViewById(R.id.Reset_Password_NewPassword1)).getText().toString());
                msg.put(getString(R.string.keys_json_verification),
                        "-" + ((EditText) getActivity().findViewById(R.id.Reset_Password_Verification)).getText().toString());
                Log.d("JSON", "onSubmitClickForgot: "+msg.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "onSubmitClickForgot: sending async");
            new SendPostAsyncTask.Builder(uri.toString(), msg)
                    .onPostExecute(this::handleResetOnPost)
                    .build().execute();
        }
    }

    private void handleResetOnPost(String result) {
        SharedPreferences preferences = getActivity().getSharedPreferences(getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
        int verificationPin = preferences.getInt(getString(R.string.keys_verification_pin), -99999);
        EditText pin = getActivity().findViewById(R.id.Reset_Password_Verification);
        try {
            JSONObject resultJson = new JSONObject(result);
            boolean success = resultJson.getBoolean(getString(R.string.keys_json_success));
            if (success) {
                int userInputVerificationPin = Integer.valueOf(pin.getText().toString());
                Log.e(TAG, "success - shared preference verification PIN " + verificationPin +
                        " userInputVerificationPin "+ userInputVerificationPin);

                if (userInputVerificationPin == verificationPin) {
                    ResetFragment frag  = new ResetFragment();
                    Log.d(TAG, "handleResetOnPost: finished async" + result);

                    ((EditText) getView().findViewById(R.id.Reset_Password_NewPassword1))
                            .setText("");
                    ((EditText) getView().findViewById(R.id.Reset_Password_NewPassword2))
                            .setText("");
                    ((EditText) getView().findViewById(R.id.Reset_Password_Verification))
                            .setText("");
                    Toast.makeText(getContext(), "Password successfully changed.", Toast.LENGTH_LONG).show();
                    SharedPreferences prefs =
                            getActivity().getSharedPreferences(
                                    getString(R.string.keys_shared_prefs),
                                    Context.MODE_PRIVATE);

                    prefs.edit().remove(getString(R.string.keys_shared_prefs_username));

                    prefs.edit().putBoolean(
                            getString(R.string.keys_prefs_stay_login),
                            false)
                            .apply();
                    startActivity(new Intent(getContext(), MainActivity.class));
                    getActivity().finish();
                } else {

                    pin.setError("Wrong verification PIN!");
                }

            } else {
                Log.e(TAG, "handleResetOnPost fail: " + resultJson.toString());
            }
        } catch (JSONException e) {

        }



//        ResetFragment frag  = new ResetFragment();
//        Log.d(TAG, "handleResetOnPost: finished async" + result);
//
//        ((EditText) getView().findViewById(R.id.Reset_Password_NewPassword1))
//                .setText("");
//        ((EditText) getView().findViewById(R.id.Reset_Password_NewPassword2))
//                .setText("");
//        ((EditText) getView().findViewById(R.id.Reset_Password_Verification))
//                .setText("");
//        Toast.makeText(getContext(), "Password successfully changed.", Toast.LENGTH_LONG).show();
//        SharedPreferences prefs =
//                getActivity().getSharedPreferences(
//                        getString(R.string.keys_shared_prefs),
//                        Context.MODE_PRIVATE);
//
//        prefs.edit().remove(getString(R.string.keys_shared_prefs_username));
//
//        prefs.edit().putBoolean(
//                getString(R.string.keys_prefs_stay_login),
//                false)
//                .apply();
//        startActivity(new Intent(getContext(), MainActivity.class));
//        getActivity().finish();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
