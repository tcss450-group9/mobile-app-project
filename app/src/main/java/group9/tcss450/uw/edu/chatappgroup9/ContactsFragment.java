package group9.tcss450.uw.edu.chatappgroup9;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import group9.tcss450.uw.edu.chatappgroup9.model.RecycleViewAdapterContact;
import group9.tcss450.uw.edu.chatappgroup9.model.RecyclerViewAdapterContactNew;
import group9.tcss450.uw.edu.chatappgroup9.utils.SendPostAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContactsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ContactsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener,
        RecyclerViewAdapterContactNew.ContactItemListener {

    private OnFragmentInteractionListener mListener;
//    private String[] myDummyValue = {"Little_dog", "little_cat", "big_turtle", "myDummyValue", "African buffalo", "Meles meles"};
    private TextView myTitle;
    private RecyclerView myContactsRecyclerView;
    private RecyclerView myRequestsRecyclerView;
    private Switch mySwitch;
    private boolean mySwitchPosition;
    private SharedPreferences myPrefs;
    private final String TAG = "Contact Fragment";
    private String myTargetMemberId;

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
            mListener.bindContactItemListener(this);
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

    @Override
    public void contactItemLayoutOnClicked(String targetChatMember) {
        myTargetMemberId = getChatID(targetChatMember);
        String chatName = getChatName(targetChatMember);
        Uri uri = new Uri.Builder().scheme("https").appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_send_new_chat_id)).build();
        JSONObject chatNameJSON = new JSONObject();

        try {
            chatNameJSON.put(getString(R.string.keys_json_chat_name), chatName);
            Log.e("NavigationActivity", "Put email to json" );
        } catch (JSONException theException) {
            Log.e("NavigationActivity", "Error creating JSON" + theException.getMessage());
        }

        new SendPostAsyncTask.Builder(uri.toString(), chatNameJSON)
                .onPostExecute(this::handleEndOfContactItemClicked).build().execute();
    }


    private void handleEndOfContactItemClicked(String result) {
        try{
            JSONObject resultJson = new JSONObject(result);
            boolean success = resultJson.getBoolean(getString(R.string.keys_json_success));

            if (success) {
                String returnedChatId = resultJson.getString(getString(R.string.keys_json_chat_id));
                Log.e(TAG, "The returned Chat ID: " + returnedChatId);
            } else {
                Log.e(TAG, "Get Chat ID fail" );
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON paser error" + e.getMessage());
        }

    }

    /**
     * Helper method, get chat name from the targetChatMember.
     *
     * @param targetChatMember
     * @return a chat name.
     * @throws IllegalArgumentException when targetChatMember is in wrong format.
     */
    private String getChatName(String targetChatMember) {
        if (targetChatMember != null) {
            String[] strings = targetChatMember.split(":");
            return strings[1];
        }
        throw new IllegalArgumentException("Missing Chat name");
    }

    /**
     * Helper method, get chat ID from the targetChatMember.
     *
     * @param targetChatMember
     * @return a chat name.
     * @throws IllegalArgumentException when targetChatMember is in wrong format.
     */
    private String getChatID(String targetChatMember) {
        if (targetChatMember != null) {
            String[] strings = targetChatMember.split(":");
            return strings[0];
        }
        throw new IllegalArgumentException("Missing Chat ID");
    }


    private void loadChatFragment(Fragment frag, String theFragmentTag) {
        Log.e(TAG, "loadChatFragment");
        FragmentTransaction ft = getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, frag, theFragmentTag)
                .addToBackStack(null);
        ft.commit();

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void getAllContacts(String baseURL, String endPoint, String username);
        void getPendingRequests(String baseURL, String endpoint, String username);
        void bindContactItemListener(RecyclerViewAdapterContactNew.ContactItemListener listener);
    }
}
