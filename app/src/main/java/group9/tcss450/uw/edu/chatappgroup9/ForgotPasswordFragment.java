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
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import group9.tcss450.uw.edu.chatappgroup9.utils.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ForgotPasswordFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ForgotPasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForgotPasswordFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final String TAG = "ForgotPasswordFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ForgotPasswordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ForgotPasswordFragment newInstance(String param1, String param2) {
        ForgotPasswordFragment fragment = new ForgotPasswordFragment();
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
        View v = inflater.inflate(R.layout.fragment_forgot_password , container, false);
        Button b = v.findViewById(R.id.Password_Reset_Submit);
        b.setOnClickListener(this::onSubmitClickForgot);
        b = v.findViewById(R.id.Forgot_password_Button_already_submit);
        b.setOnClickListener(this::onAlreadySubmit);
        return v;
    }
    public void onAlreadySubmit(View view){
        ResetFragment frag  = new ResetFragment();

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, frag, "frag")
                .addToBackStack(null)
                .commit();
        ((EditText) getView().findViewById(R.id.Forgot_password_Editext))
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
        int veri = verificationPinGenerator();
        Log.d("gerer", "onSubmitClickForgot: here");
        SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
        String user = prefs.getString(getString(R.string.keys_shared_prefs_username), "");
        Uri uri = new Uri.Builder().scheme("https").appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_change_pass_initiate)).build();

        //build the JSON object
        JSONObject msg = new JSONObject();
        try {
            msg.put("email",((EditText)getActivity().findViewById( R.id.Forgot_password_Editext)).getText().toString());
            msg.put("verification",veri);

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

    private void handleResetOnPost(String s) {
        ResetFragment frag  = new ResetFragment();
        Log.d("finished", "handleResetOnPost: finished async" + s);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, frag, "frag")
                .addToBackStack(null)
                .commit();
        ((EditText) getView().findViewById(R.id.Forgot_password_Editext))
                .setText("");


    }
    public int verificationPinGenerator() {
        Random random = new Random();

        int value = random.nextInt(10000);

        return value * -1;
    }


    public void onClick(View view) {
        if (mListener != null) {
            switch (view.getId()) {
                case R.id.Password_Reset_Submit:
                    Log.d("here", "onClick: here");
                    onSubmitClickForgot(view);

                    break;
                default:
                    Log.wtf("TAG", "kill me");
            }
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
