package group9.tcss450.uw.edu.chatappgroup9;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import group9.tcss450.uw.edu.chatappgroup9.model.RecyclerViewAdapterMessages;
import group9.tcss450.uw.edu.chatappgroup9.utils.ListenManager;
import group9.tcss450.uw.edu.chatappgroup9.utils.SendPostAsyncTask;


///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link ChatFragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// */
public class ChatFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private String myUsername;
    private String mySendUrl;
    private ListenManager myListenManager;
    private RecyclerView myRecyclerView;
    private RecyclerViewAdapterMessages myAdapterChat;
    private SharedPreferences prefs;
    private List<String> contacts = new ArrayList<>();
    Spinner spinner;
    private int check =0;
    /**
     *
     */
    private String myTargetChatId;
    private String myTargetUsername;
    private final String TAG = "Chat Fragment";

//    private OnFragmentInteractionListener myListener;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);

        v.findViewById(R.id.chatSendButton).setOnClickListener(this::sendMessage);
        myRecyclerView = v.findViewById(R.id.chatRecyclerViewAllMessages);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        myRecyclerView.setLayoutManager(linearLayoutManager);
        myAdapterChat = new RecyclerViewAdapterMessages(new ArrayList<String>());
        myRecyclerView.setAdapter(myAdapterChat);
        TextView chattingWith = v.findViewById(R.id.chatTextViewChattingWith);

        if (getArguments() == null) {
            myTargetChatId = "1";
            chattingWith.setText("Chat ID " + myTargetChatId + " global ");
        } else {
            myTargetChatId = getArguments().getString("TARGET_CHAT_ID");
            myTargetUsername = getArguments().getString("TARGET_USERNAME");
            chattingWith.setText("Chatting with " + myTargetUsername + " Chat ID " + myTargetChatId);
            Log.e(TAG, "current TARGET_CHAT_ID : " + myTargetChatId);
        }
        SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
        getAllContacts(getString(R.string.ep_base_url), getString(R.string.ep_view_connections), prefs.getString(getString(R.string.keys_shared_prefs_username), "") );
        Log.d("Contacts", "onCreateView: " + contacts.size());

       spinner = (Spinner)v.findViewById(R.id.chat_Fragment_Contacts_spinner);


        return v;
    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (myListener != null) {
//            myListener.onFragmentInteraction(uri);
//        }
//    }

    //    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            myListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
    private void handleError(final Exception e) {
        Log.e("LISTEN ERROR!!!", e.getMessage());
    }


    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        if (!prefs.contains(getString(R.string.keys_shared_prefs_username))) {
            throw new IllegalStateException("No username in prefs!");
        }
        myUsername = prefs.getString(getString(R.string.keys_shared_prefs_username), "");

        mySendUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_send_message))
                .build()
                .toString();
        Uri retrieve = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_get_message))
                .appendQueryParameter("chatId", myTargetChatId)
                .build();
        Log.e(TAG, "listen message from " + myTargetChatId);
        if (prefs.contains(getString(R.string.keys_prefs_time_stamp))) {
            //ignore all of the seen messages. You may want to store these messages locally
            myListenManager = new ListenManager.Builder(retrieve.toString(),
                    this::publishProgress)
                    .setTimeStamp(prefs.getString(getString(R.string.keys_prefs_time_stamp), "0"))
                    .setExceptionHandler(this::handleError)
                    .setDelay(1000)
                    .build();
        } else {
            //no record of a saved timestamp. must be a first time login
            myListenManager = new ListenManager.Builder(retrieve.toString(),
                    this::publishProgress)
                    .setExceptionHandler(this::handleError)
                    .setDelay(1000)
                    .build();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        myListenManager.startListening();
    }


    @Override
    public void onStop() {
        super.onStop();
        String latestMessage = myListenManager.stopListening();
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        // Save the most recent message timestamp
        prefs.edit().putString(
                getString(R.string.keys_prefs_time_stamp),
                latestMessage)
                .apply();
    }


    private void publishProgress(JSONObject messages) {
        final String[] msgs;
        int currentMsgsLength;
        if (messages.has(getString(R.string.keys_json_messages))) {
            try {

                JSONArray jMessages = messages.getJSONArray(getString(R.string.keys_json_messages));

                msgs = new String[jMessages.length()];
                for (int i = 0; i < jMessages.length(); i++) {

                    JSONObject msg = jMessages.getJSONObject(i);
                    String username = msg.get(getString(R.string.keys_json_username)).toString();
                    String userMessage = msg.get(getString(R.string.keys_json_message)).toString();
                    msgs[i] = myUsername + ":" + username + ":" + userMessage;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }

            getActivity().runOnUiThread(() -> {
                for (String s : msgs) {
                    myAdapterChat.addData(s);
                    myRecyclerView.scrollToPosition(myAdapterChat.getItemCount() - 1);
                }
//                Log.e("ChatFragemnt", myAdapterChat.getItemCount() + "");
//                myRecyclerView.scrollToPosition(myAdapterChat.getItemCount() - 1);

            });
        }
    }

    private void sendMessage(final View theButton) {
        JSONObject messageJson = new JSONObject();
        String msg = ((EditText) getView().findViewById(R.id.chatInputEditText))
                .getText().toString();
        prefs = getActivity().getSharedPreferences(getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
        try {
            messageJson.put(getString(R.string.keys_json_username), myUsername);
            messageJson.put(getString(R.string.keys_json_message), msg);
            messageJson.put(getString(R.string.keys_json_send_message_chat_id), myTargetChatId);

            Log.e(TAG, "message sent " + msg.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendPostAsyncTask.Builder(mySendUrl, messageJson)
                .onPostExecute(this::endOfSendMsgTask)
                .onCancelled(this::handleError)
                .build().execute();
    }

    private void handleError(final String msg) {
        Log.e("CHAT ERROR!!!", msg.toString());
    }

    private void endOfSendMsgTask(final String result) {
        try {
            JSONObject res = new JSONObject(result);

            if (res.get(getString(R.string.keys_json_success)).toString()
                    .equals(getString(R.string.keys_json_success_value_true))) {

                ((EditText) getView().findViewById(R.id.chatInputEditText))
                        .setText("");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(check++  <1) {
            return;
        }
        Log.d("gerer", "onSubmitClickForgot: here");
        SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
        String user = prefs.getString(getString(R.string.keys_shared_prefs_username), "");
        Uri uri = new Uri.Builder().scheme("https").appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_addToChat)).build();

        //build the JSON object
        JSONObject msg = new JSONObject();
        try {
            msg.put("chatID",(prefs.getString(getString(R.string.keys_json_chat_id),"" )));
            msg.put("username", ((TextView)view.findViewById(R.id.recycleview_item_textview_chat)).getText().toString());

        } catch (JSONException e) {
            Log.d("hello", "hello");
            e.printStackTrace();
        }
        Log.d("whathehellarewesending", "onSubmitClickForgot: sending async"+ msg.toString() + uri.toString());
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleResetOnPost)
                .onCancelled(this::handleError)
                .build().execute();

    }

    private void handleResetOnPost(String s) {
        Toast.makeText(getActivity(), "we Added a user", Toast.LENGTH_SHORT);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
public void getAllContacts(String baseURL, String endPoint, String username) {
    Log.d("Load Contact Fragment","Top of getAllContacts");
    JSONArray contacts = new JSONArray(); //This is never populated and is returned empty. Perhaps change this function to void?
    JSONObject unObject = new JSONObject();
    try {
        unObject.put("username", username);
    }
    catch(JSONException e) {
        Log.e("GETALLCONTACTS", "Error building username JSONObject: " + e.getMessage());
    }

    Uri uri = new Uri.Builder()
            .scheme("https")
            .appendPath(baseURL)
            .appendPath(endPoint)
            .appendQueryParameter("username",username)
            .build();
    Log.d("Load Contact Fragment", uri.toString());
    new SendPostAsyncTask.Builder(uri.toString(), unObject)
            .onPostExecute(this::handleGetAllContactsOnPost)
            .build().execute();
    Log.d("Load Contact Fragment","Bottom of getAllContacts");
}

    private void handleGetAllContactsOnPost(String s) {
        JSONObject n = new JSONObject();
        String temp = "here";
        JSONArray g = new JSONArray();

        try {
             n = new JSONObject(s);
           g = n.getJSONArray("contacts");
            Log.d("JSON return", "handleGetAllContactsOnPost: " + g.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i =0; i < g.length(); i++){
            try {
                temp = ((JSONObject)g.get(i)).getString("username");
                Log.d("adding", "handleGetAllContactsOnPost: " + ((JSONObject)g.get(i)).getString("username")+ temp);
            } catch (JSONException e) {
                e.printStackTrace();
            }
                    contacts.add(temp);
        }
        Log.d("Contacts", "onCreateView: " + contacts.size());


        ArrayAdapter<String> adapter = new ArrayAdapter(getActivity(),R.layout.recycler_view_item_chat_contacts,R.id.recycleview_item_textview_chat, contacts);
        adapter.setDropDownViewResource(R.layout.recycler_view_item_chat_contacts);

        spinner.setOnItemSelectedListener(this);
        spinner.setAdapter(adapter);
        Log.d("hello", "onCreateView: " + adapter.getCount());


    }


}
