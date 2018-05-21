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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ResetFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ResetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResetFragment extends Fragment {
    private int MIN_LENGTH_USERNAME_PASSWORD = 6;

    private final String PASSWORD_NOT_MATCH = "Passwords are not match";
    private final String PASSWORD_TOO_SHORT = "Password is too short";

    private final String PASSWORD_TOO_SIMPLE = "Password is too simple";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ResetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResetFragment newInstance(String param1, String param2) {
        ResetFragment fragment = new ResetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_reset , container, false);
        Button b = v.findViewById(R.id.Password_Reset_Submit);
        b.setOnClickListener(this::onSubmitClickForgot);
        Log.d("gwrwrw", "onCreateView: here1");
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    public void onSubmitClickForgot(View view) {

        Log.d("gerer", "onSubmitClickForgot: here");
        SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
        String user = prefs.getString(getString(R.string.keys_shared_prefs_username), "");
        Uri uri = new Uri.Builder().scheme("https").appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_change_password1)).build();
        EditText password3 = getActivity().findViewById(R.id.Reset_Password_NewPassword1);
        EditText password2 = getActivity().findViewById(R.id.Reset_Password_NewPassword2);
        String password = password3.getText().toString();
        String confirmPassword = password2.getText().toString();
        Boolean result = true;
        if (!password.equals(confirmPassword)) {
            result = false;
            password2.setError(PASSWORD_NOT_MATCH);
            password3.setError(PASSWORD_NOT_MATCH);
        } else if (password.length() < MIN_LENGTH_USERNAME_PASSWORD) {
            result = false;
            password2.setError(PASSWORD_TOO_SHORT);
            password3.setError(PASSWORD_TOO_SHORT);
        } else if (!InputVerificationTool.isPassword(password)) {
            result = false;
            password2.setError(PASSWORD_TOO_SIMPLE);
            password3.setError(PASSWORD_TOO_SIMPLE);
        }
        //build the JSON object
        if(result) {
            JSONObject msg = new JSONObject();
            try {
                msg.put("password", ((EditText) getActivity().findViewById(R.id.Reset_Password_NewPassword1)).getText().toString());
                msg.put("verification", "-" + ((EditText) getActivity().findViewById(R.id.Reset_Password_Verification)).getText().toString());
                Log.d("JSON", "onSubmitClickForgot: "+msg.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("rew", "onSubmitClickForgot: sending async");
            new SendPostAsyncTask.Builder(uri.toString(), msg)
                    .onPostExecute(this::handleResetOnPost)
                    .build().execute();
        }
    }

    private void handleResetOnPost(String s) {
        ResetFragment frag  = new ResetFragment();
        Log.d("finished", "handleResetOnPost: finished async" + s);

        ((EditText) getView().findViewById(R.id.Reset_Password_NewPassword1))
                .setText("");
        ((EditText) getView().findViewById(R.id.Reset_Password_NewPassword2))
                .setText("");
        ((EditText) getView().findViewById(R.id.Reset_Password_Verification))
                .setText("");
        Toast.makeText(getContext(), "Password successfully changed.", Toast.LENGTH_LONG).show();
        startActivity(new Intent(getContext(), MainActivity.class));
        getActivity().finish();


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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
