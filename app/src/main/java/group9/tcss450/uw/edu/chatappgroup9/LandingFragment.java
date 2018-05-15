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
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import group9.tcss450.uw.edu.chatappgroup9.model.RecyclerViewAdapterLandingPage;
import group9.tcss450.uw.edu.chatappgroup9.utils.ListenManager;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LandingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class LandingFragment extends Fragment {
    private  int counter = 0;
    private ListenManager chatsmanager;
    private ListenManager contentmanager;
    private String mSendUrl  = "";
    private RecyclerView recyclerview;
    private String myUsername;
    private final String [] array = new String [255];
    private OnFragmentInteractionListener mListener;
    private String mSendUrl2;

    public LandingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("error", "onCreateView: we in this bitch ");
        JSONObject user = new JSONObject();
        View view = inflater.inflate(R.layout.fragment_landing, container, false);
        Log.e("NavigationActivity", "" + "LandingFragmentTag");
        // Inflate the layout for this fragment
        recyclerview = (RecyclerView) view.findViewById(R.id.Chats);
        //       Log.d("", "onCreateView: " + recyclerview.toString());
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerview.setAdapter(new RecyclerViewAdapterLandingPage(getActivity(), array));

        Button logout = view.findViewById(R.id.landingButtonLogout);
        logout.setOnClickListener(this::onLogoutPressed);
        return view;
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
        Uri retrieve = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_get_Chats))
                .appendQueryParameter("username", prefs.getString(getString(R.string.keys_shared_prefs_username), ""))
                .build();
        mSendUrl2 = new Uri.Builder().scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_get_chatInfo)).appendQueryParameter("chatId","1" )
                .build()
                .toString();

        //no record of a saved timestamp. must be a first time login
        chatsmanager = new ListenManager.Builder(retrieve.toString(),
                this::endOfSendMsgTask)
                .setExceptionHandler(this::handleError)
                .setDelay(1000)
                .build();
//            contentmanager = new ListenManager.Builder(mSendUrl2,
//                    this::getchats)
//                    .setExceptionHandler(this::handleError2)
//                    .setDelay(1000)
//                    .build();


    }
    @Override
    public void onResume() {
        super.onResume();
        chatsmanager.startListening2();
        //  contentmanager.startListening2();
    }
    @Override
    public void onStop() {
        super.onStop();
         String latestMessage = chatsmanager.stopListening();
        //  contentmanager.stopListening();
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        // Save the most recent message timestamp
//        prefs.edit().putString(
//                getString(R.string.keys_prefs_time_stamp),
//                latestMessage)
//                .apply();
    }
    private void handleError(final Exception e) {
        Log.e("LISTEN ERROR!!!", e.getMessage());
    }
    private void handleError2(final Exception e) {
        Log.e("LISTEN ERROR!!!76", e.getMessage());
    }

    private void endOfSendMsgTask(JSONObject g) {
        Log.d("here", "endOfSendMsgTask: "+ g.toString());

        try {
            JSONObject chat;
            JSONObject res = g;
            JSONArray n = res.getJSONArray("Chats");
            for(counter = 0; counter < n.length() ; counter++) {
                try{
                    chat =  n.getJSONObject(counter);
                    array[counter] = chat.get("chatid").toString();
                    recyclerview.setAdapter(new RecyclerViewAdapterLandingPage(getActivity(), array));
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                // Log.d("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "endOfSendMsgTask: "+ array.toString());
            }
            counter= 0;

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    //    public void getchats(JSONObject result){
//       // Log.d("hereererer", "getchats: "+ result.toString());
//        try {
//            JSONObject chat = result;
//            Log.d("", "getchats: " +array.toString());
//            JSONArray n = chat.getJSONArray("Chats");
//            array[counter][0] = n.getJSONObject(0).getString("message");
//
//            recyclerview.setAdapter(new RecyclerViewAdapterLandingPage(getActivity(), array));
//        }catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
    private void onLogoutPressed(View view) {
        if (mListener != null) {
            mListener.onLogout();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
        getActivity().finishAndRemoveTask();
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
        void onLogout();
    }
}