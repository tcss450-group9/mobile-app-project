package group9.tcss450.uw.edu.chatappgroup9;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Switch;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;

import group9.tcss450.uw.edu.chatappgroup9.model.RecycleViewAdapterContact;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContactsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ContactsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private OnFragmentInteractionListener mListener;
    private String[] myDummyValue = {"Little_dog", "little_cat", "big_turtle", "myDummyValue", "African buffalo", "Meles meles"};
    private JSONArray myContacts;
    private TextView myTitle;
    private RecyclerView myRecyclerView;
    private Switch mySwitch;
    private SharedPreferences myPrefs;

    /**
     * The query parameter to select verified (existing) or unverified (pending request) connections.
     * 1 = existing; 0 = pending. Must be passed as a string.
     */
    private int myContactStatusVerified = 1;



    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myPrefs = getContext().getSharedPreferences(
                getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
        View v = inflater.inflate(R.layout.fragment_contacts, container, false);
        myRecyclerView = v.findViewById(R.id.contactRecycleViewAllContacts);
        myTitle = v.findViewById(R.id.contactTextViewTitle);
        mySwitch = v.findViewById(R.id.contactsSwitchExisting);
        mySwitch.setOnCheckedChangeListener(this);

        myRecyclerView.setHasFixedSize(true);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (mListener != null) {
            String username = myPrefs.getString(getString(R.string.keys_shared_prefs_username),null);
            mListener.getAllContacts(getString(R.string.ep_base_url),
                    getString(R.string.ep_view_connections), username, Integer.toString(myContactStatusVerified));
        }
        //recyclerView.setAdapter(new RecycleViewAdapterContact(myDummyValue));
        return v;
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

    @Override
    public void onCheckedChanged(CompoundButton theSwitch, boolean isChecked) {
        if(isChecked) {
            //Client chose to view pending requests
            myContactStatusVerified = 0;
            myTitle.setText(getString(R.string.contacts_edittext_title2));
        }
        else {
            //Client chose to view existing connections
            myContactStatusVerified = 1;
            myTitle.setText(getString(R.string.contacts_edittext_title1));
        }
        String username = myPrefs.getString(getString(R.string.keys_shared_prefs_username),null);
        mListener.getAllContacts(getString(R.string.ep_base_url),
                getString(R.string.ep_view_connections), username, Integer.toString(myContactStatusVerified));
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
        void getAllContacts(String baseURL, String endPoint, String username, String existing);
    }
}
