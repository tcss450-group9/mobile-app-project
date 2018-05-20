package group9.tcss450.uw.edu.chatappgroup9;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import group9.tcss450.uw.edu.chatappgroup9.model.RecyclerViewAdapterContactNew;
import group9.tcss450.uw.edu.chatappgroup9.utils.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FriendsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class FriendsFragment extends Fragment implements RecyclerViewAdapterContactNew.FriendItemListener {

    private OnFragmentInteractionListener myListener;
    private RecyclerView myFriendsRecyclerView;
    private final String TAG = "FriendsFragment";
    private String myChatId;
    private String myUsername;
    private String myFriendUsername;
    private String myMemberId;
    private String myFriendMemberId;

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        ArrayList<String> contactsList = null;
        Bundle bunbdle = getArguments();
        Log.e(TAG, "RecyclerViewAdapterContactNew bunbdle = " + bunbdle);
        if (bunbdle != null) {
            contactsList = bunbdle.getStringArrayList("CONTACTS_ID_USERNAME");
        }
        myFriendsRecyclerView = view.findViewById(R.id.friendsRecyclerViewContacts);
        myFriendsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        Log.e(TAG, "RecyclerViewAdapterContactNew contactsList = " + contactsList);
        RecyclerViewAdapterContactNew adapter = new RecyclerViewAdapterContactNew(contactsList);
//        Log.e(TAG, "RecyclerViewAdapterContactNew = " + adapter);
        myFriendsRecyclerView.setAdapter(adapter);
        adapter.setItemClickedListener(this);
        myChatId = null;
        myFriendUsername = null;
        myUsername = getActivity().getSharedPreferences(getString(R.string.keys_shared_prefs),
                Context.MODE_PRIVATE).getString(getString(R.string.keys_shared_prefs_username),
                "unknown username");
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (myListener != null) {
            myListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            myListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        myListener = null;
    }

    @Override
    public void friendItemLayoutOnClicked(String friendMemberIdUsername) {

        if (!TextUtils.isEmpty(friendMemberIdUsername)) {
            String[] strings = friendMemberIdUsername.split(":");
            String usernameAsChatName = strings[1];
            myFriendMemberId = strings[0];
            myFriendUsername =strings[1];

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
                myChatId = resultJson.getString(getString(R.string.keys_json_chatid));
//                createNewChatSession(myChatId, myMemberId, myFriendMemberId);
                //TODO start a new chat with new chat id/ what about if a chat id alread exist and associate with us?
                //
                loadChatFragment(myChatId);

                Log.e(TAG, "Got new chat id " + myChatId);
            } else {
                Log.e(TAG, "Get new chat id fail");
            }


        } catch (JSONException e) {
            Log.e(TAG, "JSON Parse Error" + e.getMessage());
        }
    }

    /**
     * send a asyntask to the server to create a new chat session with the new chat id, my member id
     * and my friend member id.
     * @param chatId
     * @param myMemberId
     * @param friendMemberId
     */
    private void createNewChatSession(String chatId, String myMemberId, String friendMemberId) {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_new_one_to_one_chat))
                .build();
        JSONObject magsJson = new JSONObject();
        Log.e(TAG, "createNewChatSession " + uri.toString());
        try {
            magsJson.put(getString(R.string.keys_json_chat_id), chatId);
            magsJson.put(getString(R.string.keys_json_chat_member_a), myMemberId);
            magsJson.put(getString(R.string.keys_json_chat_member_b), friendMemberId);
        } catch (JSONException e) {
            Log.e(TAG, "JSON Parse Error" + e.getMessage());
        }

        new SendPostAsyncTask.Builder(uri.toString(), magsJson)
                .onPostExecute(this::startChatting)
                .build().execute();
    }

    private void startChatting(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            boolean success = jsonObject.getBoolean(getString(R.string.keys_json_success));

            if (success) {
                String chatid = jsonObject.getString(getString(R.string.keys_json_chatid));
                loadChatFragment(chatid);
                Log.e(TAG, "start chatting with chat id" + chatid);
            } else {
                Log.e(TAG, "start chatting fail");
            }

        } catch (JSONException e) {
            Log.e(TAG, "JSON parse error " + e.getMessage());
        }
    }

    private void loadChatFragment(String targetChatId) {
        Fragment chatFrag = new ChatFragment();
        Bundle arg = new Bundle();
        arg.putString("TARGET_CHAT_ID", targetChatId);
        arg.putString("TARGET_USERNAME", myFriendUsername);
        chatFrag.setArguments(arg);

        Log.e(TAG, "loadChatFragment");
        FragmentTransaction ft = getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, chatFrag, getString(R.string.keys_chat_fragment_tag))
                .addToBackStack(null);
        ft.commit();

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
        void getFriendList(String baseURL, String endPoint, String username);
    }
}
