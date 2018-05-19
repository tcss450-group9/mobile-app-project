package group9.tcss450.uw.edu.chatappgroup9;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import group9.tcss450.uw.edu.chatappgroup9.model.RecyclerViewAdapterContactNew;
import group9.tcss450.uw.edu.chatappgroup9.utils.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContactsFragmentNew.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ContactsFragmentNew extends Fragment implements RecyclerViewAdapterContactNew.ContactItemListener{

    private OnFragmentInteractionListener mListener;
    private RecyclerView myContactRecyclerView;
    private final String TAG = "ContactsFragmentNew";
    private String myNewChatId;
    private String myMemberId;
    private String myFriendMemberId;

    public ContactsFragmentNew() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contacts_fragment_new, container, false);

        ArrayList<String> contactsList = null;
        Bundle bunbdle = getArguments();
        Log.e(TAG, "RecyclerViewAdapterContactNew bunbdle = " + bunbdle);
        if (bunbdle != null) {
            contactsList = bunbdle.getStringArrayList("CONTACTS_ID_USERNAME");
        }
        myContactRecyclerView = view.findViewById(R.id.newContactsRecyclerViewContacts);
        myContactRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        Log.e(TAG, "RecyclerViewAdapterContactNew contactsList = " + contactsList);
        RecyclerViewAdapterContactNew adapter = new RecyclerViewAdapterContactNew(contactsList);
//        Log.e(TAG, "RecyclerViewAdapterContactNew = " + adapter);
        myContactRecyclerView.setAdapter(adapter);
        adapter.setItemClickedListener(this);

        myNewChatId = null;

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
    public void contactItemLayoutOnClicked(String friendMemberIdUsername) {

        if (!TextUtils.isEmpty(friendMemberIdUsername)) {
            String[] strings = friendMemberIdUsername.split(":");
            String usernameAsChatName = strings[1];
            myFriendMemberId = strings[0];
            //TODO open a new chat when tap
            getNewChatId(usernameAsChatName);
        }
    }

    /**
     * Send a asyncTask to the server to get a new chat id..
     * @param usernameAsChatName
     */
    private void getNewChatId(String usernameAsChatName) {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_new_chat_id))
                .build();
        JSONObject chatName = new JSONObject();
        try {
            chatName.put(getString(R.string.keys_json_chat_name), usernameAsChatName);
        } catch (JSONException e) {
            Log.e(TAG, "JSON Parse Error" + e.getMessage());
        }

        new SendPostAsyncTask.Builder(uri.toString(), chatName)
                .onPostExecute(this::endOfGetNewChatId)
                .build().execute();
    }

    private void endOfGetNewChatId(String result) {
        try {
            JSONObject resultJson = new JSONObject(result);
            boolean success = resultJson.getBoolean(getString(R.string.keys_json_success));

            if (success) {
                myNewChatId = resultJson.getString(getString(R.string.keys_json_chat_id));
                createNewChatSession(myNewChatId, myMemberId, myFriendMemberId);
                //TODO start a new chat with new chat id/ what about if there is a chat id exist and associate with us?
                //

                Log.e(TAG, "Got new chat id");
            } else {
                Log.e(TAG, "Get new chat id fail");
            }


        } catch (JSONException e) {
            Log.e(TAG, "JSON Parse Error" + e.getMessage());
        }
    }

    private void createNewChatSession(String myNewChatId, String myMemberId, String myFriendMemberId) {

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
