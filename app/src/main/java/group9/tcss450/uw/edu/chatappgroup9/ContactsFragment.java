package group9.tcss450.uw.edu.chatappgroup9;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Switch;

import org.json.JSONArray;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContactsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ContactsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private OnFragmentInteractionListener mListener;
    private String[] myDummyValue = {"Little_dog", "little_cat", "big_turtle", "myDummyValue", "African buffalo", "Meles meles"};
    private TextView myTitle;
    private RecyclerView myContactsRecyclerView;
    private RecyclerView myRequestsRecyclerView;
    private Switch mySwitch;
    private boolean mySwitchPosition;
    private SharedPreferences myPrefs;



    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myPrefs = getContext().getSharedPreferences(
                getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);

        View v = inflater.inflate(R.layout.fragment_contacts, container, false);

        myContactsRecyclerView = v.findViewById(R.id.contactRecycleViewAllContacts);
        myRequestsRecyclerView = v.findViewById(R.id.contactsRecyclerViewRequests);
        myTitle = v.findViewById(R.id.contactTextViewTitle);
        mySwitch = v.findViewById(R.id.contactsSwitchExisting);
        mySwitch.setOnCheckedChangeListener(this);

        myContactsRecyclerView.setHasFixedSize(true);
        myContactsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        myRequestsRecyclerView.setHasFixedSize(true);
        myRequestsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (mListener != null) {
            String username = myPrefs.getString(getString(R.string.keys_shared_prefs_username),null);
            mListener.getAllContacts(getString(R.string.ep_base_url),
                    getString(R.string.ep_view_connections), username);
            mListener.getPendingRequests(getString(R.string.ep_base_url),
                    getString(R.string.ep_view_requests), username);
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
            Log.d("Contact Fragment", "onCheckedChanged: isChecked; contacts invisible");
            myTitle.setText(getString(R.string.contacts_edittext_title2));
            myContactsRecyclerView.setVisibility(View.GONE);
            myRequestsRecyclerView.setVisibility(View.VISIBLE);
        }
        else {
            //Client chose to view existing connections
            Log.d("Contact Fragment", "onCheckedChanged: not checked; requests invisible");
            myTitle.setText(getString(R.string.contacts_edittext_title1));
            myContactsRecyclerView.setVisibility(View.VISIBLE);
            myRequestsRecyclerView.setVisibility(View.GONE);
        }
        String username = myPrefs.getString(getString(R.string.keys_shared_prefs_username),null);
        mListener.getAllContacts(getString(R.string.ep_base_url),
                getString(R.string.ep_view_connections), username);
        mListener.getPendingRequests(getString(R.string.ep_base_url),
                getString(R.string.ep_view_requests), username);
    }

    public boolean getSwitchPosition() {
        return mySwitchPosition;
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
        void getAllContacts(String baseURL, String endPoint, String username);
        void getPendingRequests(String baseURL, String endpoint, String username);
    }
}
