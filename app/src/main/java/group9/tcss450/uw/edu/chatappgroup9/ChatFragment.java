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
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import group9.tcss450.uw.edu.chatappgroup9.model.RecyclerViewAdapterChat;
import group9.tcss450.uw.edu.chatappgroup9.utils.ListenManager;
import group9.tcss450.uw.edu.chatappgroup9.utils.SendPostAsyncTask;


///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link ChatFragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// */
public class ChatFragment extends Fragment {
    private String myUsername;
    private String mySendUrl;
    private ListenManager myListenManager;
    private RecyclerView myRecyclerView;
    private boolean myNewMessage;
    private int myPreMessageLength;
    private RecyclerViewAdapterChat myAdapterChat;

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
        myRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        myRecyclerView.setLayoutManager(linearLayoutManager);
        myAdapterChat = new RecyclerViewAdapterChat(new ArrayList<String>());
        myRecyclerView.setAdapter(myAdapterChat);
        myNewMessage = false;
        myPreMessageLength= 0;

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
                .appendQueryParameter("chatId", "1")
                .build();

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


//    @Override
//    public void onDetach() {
//        super.onDetach();
//        myListener = null;
//    }

    private void publishProgress(JSONObject messages) {
        final String[] msgs;
        int currentMsgsLength;
        if(messages.has(getString(R.string.keys_json_messages))) {
            try {

                JSONArray jMessages = messages.getJSONArray(getString(R.string.keys_json_messages));

                msgs = new String[jMessages.length()];
                for (int i = 0; i < jMessages.length(); i++) {

                    JSONObject msg = jMessages.getJSONObject(i);
                    String username = msg.get(getString(R.string.keys_json_username)).toString();
                    String userMessage = msg.get(getString(R.string.keys_json_message)).toString();
                    msgs[i] = myUsername + ":" + username + ":" + userMessage;
                }
                currentMsgsLength = msgs.length;
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }

            getActivity().runOnUiThread(() -> {
                for (String s: msgs) {
                    myAdapterChat.addData(s);
//                    Log.e("ChatFragemnt", "ui run");
                    myRecyclerView.scrollToPosition(myAdapterChat.getItemCount() - 1);
                }
                Log.e("ChatFragemnt", myAdapterChat.getItemCount() + "");
//                myRecyclerView.scrollToPosition(myAdapterChat.getItemCount() - 1);

            });
        }
    }

    private void sendMessage(final View theButton) {
        JSONObject messageJson = new JSONObject();
        String msg = ((EditText) getView().findViewById(R.id.chatInputEditText))
                .getText().toString();

        try {
            messageJson.put(getString(R.string.keys_json_username), myUsername);
            messageJson.put(getString(R.string.keys_json_message), msg);
            messageJson.put(getString(R.string.keys_json_chat_id), 1);
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

            if(res.get(getString(R.string.keys_json_success)).toString()
                    .equals(getString(R.string.keys_json_success_value_true))) {

                ((EditText) getView().findViewById(R.id.chatInputEditText))
                        .setText("");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

}
