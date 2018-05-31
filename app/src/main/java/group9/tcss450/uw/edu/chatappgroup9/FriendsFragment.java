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

import group9.tcss450.uw.edu.chatappgroup9.model.RecyclerViewAdapterFriends;
import group9.tcss450.uw.edu.chatappgroup9.utils.SendPostAsyncTask;


/**
 * This class represents all your contacts.
 * you can starts chatting with your friends.
 */
public class FriendsFragment extends Fragment implements RecyclerViewAdapterFriends.FriendItemListener {

    private OnFragmentInteractionListener myListener;
    private RecyclerView myFriendsRecyclerView;
    private final String TAG = "FriendsFragment";
    private String myChatId;
    private String myUsername;
    private String myFriendUsername;
    private String myMemberId;
    private String myFriendMemberId;
    private ArrayList<String> myContactsList;

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        Bundle bunbdle = getArguments();
        Log.e(TAG, "RecyclerViewAdapterFriends bunbdle = " + bunbdle);
        if (bunbdle != null) {
            myContactsList = bunbdle.getStringArrayList("CONTACTS_ID_USERNAME");
        }
        myFriendsRecyclerView = view.findViewById(R.id.friendsRecyclerViewContacts);
        myFriendsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecyclerViewAdapterFriends adapter = new RecyclerViewAdapterFriends(myContactsList);
        myFriendsRecyclerView.setAdapter(adapter);
        adapter.setItemClickedListener(this);
        myChatId = null;
        myFriendUsername = null;
        myUsername = getActivity().getSharedPreferences(getString(R.string.keys_shared_prefs),
                Context.MODE_PRIVATE).getString(getString(R.string.keys_shared_prefs_username),
                "unknown username");
        myMemberId = getActivity().getSharedPreferences(getString(R.string.keys_shared_prefs),
                Context.MODE_PRIVATE).getString(getString(R.string.keys_shared_prefs_memberid),
                null);
        return view;
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

            myFriendMemberId = strings[0];
            myFriendUsername =strings[1];

            Log.e(TAG, "friendItemLayoutOnClicked: myFriendMemberId = " + myFriendMemberId +
                     " myFriendUsername: "+ myFriendUsername );
            getNewChatId(myUsername, myFriendUsername);
        }
    }

    /**
     * Send a asyncTask to the server to get a new chat id..
     * @param chatMemberA
     */
    private void getNewChatId(String chatMemberA, String chatMemberB) {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_new_chat_id))
                .build();
        JSONObject chatNameJson = new JSONObject();
        try {
            chatNameJson.put(getString(R.string.keys_json_chat_member_a), chatMemberA);
            chatNameJson.put(getString(R.string.keys_json_chat_member_b), chatMemberB);
        } catch (JSONException e) {
            Log.e(TAG, "JSON Parse Error" + e.getMessage());
        }

        new SendPostAsyncTask.Builder(uri.toString(), chatNameJson)
                .onPostExecute(this::endOfGetNewChatId)
                .build().execute();
    }

    /**
     *
     * @param result the returned chat id.
     */
    private void endOfGetNewChatId(String result) {
        try {
            JSONObject resultJson = new JSONObject(result);
            boolean success = resultJson.getBoolean(getString(R.string.keys_json_success));

            if (success) {
                myChatId = resultJson.getString(getString(R.string.keys_json_chatid));
                createNewChatSession(myChatId, myMemberId, myFriendMemberId);
            } else {
                Log.e(TAG, "Get new chat id fail");
            }

        } catch (JSONException e) {
            Log.e(TAG, "JSON Parse Error" + e.getMessage());
        }
    }

    /**
     * send a async task to the server to create a new chat session.
     * @param chatId
     * @param memberIdA
     * @param memberIdB
     */
    private void createNewChatSession(String chatId, String memberIdA, String memberIdB) {
        Log.e(TAG, "createNewChatSession start");
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_create_chat_session))
                .build();
        JSONObject magsJson = new JSONObject();
        Log.e(TAG, "createNewChatSession " + chatId + " URL: " + uri.toString());
        try {
            magsJson.put(getString(R.string.keys_json_chatid), chatId);
            magsJson.put(getString(R.string.keys_json_chat_member_a), memberIdA);
            magsJson.put(getString(R.string.keys_json_chat_member_b), memberIdB);
        } catch (JSONException e) {
            Log.e(TAG, "JSON Parse Error" + e.getMessage());
        }

        Log.e(TAG, "createNewChatSession " + chatId + " magsJson: " + magsJson.toString());
        new SendPostAsyncTask.Builder(uri.toString(), magsJson)
                .onPostExecute(this::startChatting)
                .build().execute();
    }

    /**
     * user the returned chat id to start a chat if success.
     * @param result chat id.
     */
    private void startChatting(String result) {
        Log.e(TAG, "createNewChatSession end");
        try {
            JSONObject jsonObject = new JSONObject(result);
            boolean success = jsonObject.getBoolean(getString(R.string.keys_json_success));
            if (success) {
                String chatid = jsonObject.getString(getString(R.string.keys_json_chatid));
                loadChatFragment(chatid);
                Log.e(TAG, "start chatting with chat id " + chatid);
            } else {
                Log.e(TAG, "start chatting fail " + jsonObject.toString());
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON parse error " + e.getMessage());
        }
    }

    /**
     * load a new chat fragment using the targetChatId.
     * @param targetChatId
     */
    private void loadChatFragment(String targetChatId) {
        Fragment chatFrag = new ChatFragmentV2();
        Bundle arg = new Bundle();
        arg.putString("TARGET_CHAT_ID", targetChatId);
        arg.putString("TARGET_USERNAME", myFriendUsername);
        arg.putStringArrayList("CONTACTS_ID_USERNAME", myContactsList);
        chatFrag.setArguments(arg);

        Log.e(TAG, "loadChatFragment");
        FragmentTransaction ft = getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, chatFrag, getString(R.string.keys_chat_fragment_tag))
                .addToBackStack(null);
        ft.commit();

    }


    public interface OnFragmentInteractionListener {
        /**
         * sends a async task to the server to get all contacts.
         * @param baseURL
         * @param endPoint
         * @param username
         */
        void getFriendList(String baseURL, String endPoint, String username);
    }
}
