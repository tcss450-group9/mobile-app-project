package group9.tcss450.uw.edu.chatappgroup9;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Switch;

import org.json.JSONArray;

import java.util.ArrayList;

import group9.tcss450.uw.edu.chatappgroup9.model.RecyclerViewAdapterSearchResult;
import group9.tcss450.uw.edu.chatappgroup9.utils.InputVerificationTool;

/**
 * This class display all contacts and pending requests. you can delete a contact and
 * accept or reject a pending request.
 */
public class ContactsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private OnFragmentInteractionListener mListener;
    private TextView myTitle;
    private RecyclerView myContactsRecyclerView;
    private RecyclerView myRequestsRecyclerView;
    private Switch mySwitch;
    private SharedPreferences myPrefs;
    private SearchView mySearchView;


    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myPrefs = getContext().getSharedPreferences(
                getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);

        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        myContactsRecyclerView = view.findViewById(R.id.contactRecycleViewAllContacts);
        myRequestsRecyclerView = view.findViewById(R.id.contactsRecyclerViewRequests);
        myTitle = view.findViewById(R.id.contactTextViewTitle);
        mySwitch = view.findViewById(R.id.contactsSwitchExisting);
        mySwitch.setOnCheckedChangeListener(this);

        myContactsRecyclerView.setHasFixedSize(true);
        myContactsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        myRequestsRecyclerView.setHasFixedSize(true);
        myRequestsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (mListener != null) {
            String username = myPrefs.getString(getString(R.string.keys_shared_prefs_username), null);
            mListener.getAllContacts(getString(R.string.ep_base_url),
                    getString(R.string.ep_view_connections), username);
            mListener.getPendingRequests(getString(R.string.ep_base_url),
                    getString(R.string.ep_view_requests), username);
        }

        FloatingActionButton fab = view.findViewById(R.id.contactsFloatingButtonAddNewContact);
        fab.setOnClickListener(this::floatingButtonOnClick);
        return view;
    }

    /**
     * Click floating button go to search fragment.
     * @param view
     */
    private void floatingButtonOnClick(View view) {
        Fragment searchFrag = new SearchFragment();
        FragmentTransaction ft = getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, searchFrag, getString(R.string.keys_search_fragment_tag))
                .addToBackStack(null);
        ft.commit();
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
     * change views between contacts and pending requests.
     * @param theSwitch
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton theSwitch, boolean isChecked) {
        if (isChecked) {
            //Client chose to view pending requests
            Log.d("Contact Fragment", "onCheckedChanged: isChecked; contacts invisible");
            myTitle.setText(getString(R.string.contacts_edittext_title2));
            myContactsRecyclerView.setVisibility(View.GONE);
            myRequestsRecyclerView.setVisibility(View.VISIBLE);
        } else {
            //Client chose to view existing connections
            Log.d("Contact Fragment", "onCheckedChanged: not checked; requests invisible");
            myTitle.setText(getString(R.string.contacts_edittext_title1));
            myContactsRecyclerView.setVisibility(View.VISIBLE);
            myRequestsRecyclerView.setVisibility(View.GONE);
        }
        String username = myPrefs.getString(getString(R.string.keys_shared_prefs_username), null);
        mListener.getAllContacts(getString(R.string.ep_base_url),
                getString(R.string.ep_view_connections), username);
        mListener.getPendingRequests(getString(R.string.ep_base_url),
                getString(R.string.ep_view_requests), username);
    }

    public interface OnFragmentInteractionListener {
        /**
         * Sends a async task to the server to get all the contacts.
         * @param baseURL
         * @param endPoint
         * @param username my username
         */
        void getAllContacts(String baseURL, String endPoint, String username);

        /**
         * Sends a async task to the server to get all the pending requests..
         * @param baseURL
         * @param endpoint
         * @param username
         */
        void getPendingRequests(String baseURL, String endpoint, String username);
    }
}
