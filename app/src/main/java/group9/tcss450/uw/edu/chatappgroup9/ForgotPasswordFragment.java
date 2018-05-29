package group9.tcss450.uw.edu.chatappgroup9;

import android.content.Context;
import android.content.Intent;
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


public class ForgotPasswordFragment extends Fragment {


    private OnFragmentInteractionListener mListener;
    private final String TAG = "ForgotPasswordFragment";
    private EditText myEmail;

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }


    @Override
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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public void onSubmitClickForgot(View view) {
        String email = myEmail.getText().toString();
        if (!InputVerificationTool.isEmail(email)) {
            myEmail.setError("Please enter an email");
            return;
        }

        int veri = verificationPinGenerator();
        Log.d(TAG, "onSubmitClickForgot: here");
        Uri uri = new Uri.Builder().scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_change_pass_initiate))
                .build();

        //build the JSON object
        JSONObject msg = new JSONObject();
        try {
            msg.put("email",((EditText)getActivity().findViewById( R.id.forgetPasswordEditTextEmail)).getText().toString());
            msg.put("verification",veri);
            Log.d(TAG, "JSON: " + msg.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("rew", "onSubmitClickForgot: sending async");
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleResetOnPost)
                .onCancelled(this::handleError)
                .build().execute();
    }

    public void onBackPressed() {
        startActivity(new Intent(getContext(), MainActivity.class));
        getActivity().finish();
    }

    private void handleError(final String msg) {
        Log.e("CHAT ERROR!!!", msg.toString());
    }

    private void handleResetOnPost(String result) {
        ResetFragment frag  = new ResetFragment();
        Log.d("finished", "handleResetOnPost: finished async" + result);
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
