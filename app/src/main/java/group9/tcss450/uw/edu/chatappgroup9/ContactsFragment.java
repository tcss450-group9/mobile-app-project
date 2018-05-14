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
public class ContactsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private String[] myDummyValue = {"Little_dog", "little_cat", "big_turtle", "myDummyValue", "African buffalo", "Meles meles"};
    private JSONArray myContacts;



    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        Log.e("NavigationActivity", "" + "Contact FragmentTag");
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contacts, container, false);
        RecyclerView recyclerView = v.findViewById(R.id.contactRecycleViewAllContacts);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        SharedPreferences prefs = getContext().getSharedPreferences(
                getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
        if (mListener != null) {
            String username = prefs.getString(getString(R.string.keys_shared_prefs_username),null);
            mListener.getAllContacts(getString(R.string.ep_base_url),
                    getString(R.string.ep_view_connections),
                    username);
        }
        //recyclerView.setAdapter(new RecycleViewAdapterContact(myDummyValue));



        return v;
    }

   /* @Override
    public void onStart() {
        super.onStart();
        //Populate the recyclerView with all existing connections
        SharedPreferences prefs = getContext().getSharedPreferences(
                getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
        if (mListener != null) {
            String username = prefs.getString(getString(R.string.keys_shared_prefs_username),null);
            myContacts = mListener.getAllContacts(getString(R.string.ep_base_url),
                    getString(R.string.ep_view_connections),
                    username);
        }
    }*/

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
        void getAllContacts(String baseURL, String endPoint, String username);
    }
}
