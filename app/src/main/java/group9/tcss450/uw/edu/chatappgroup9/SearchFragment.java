package group9.tcss450.uw.edu.chatappgroup9;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

import group9.tcss450.uw.edu.chatappgroup9.utils.InputVerificationTool;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * I added this comment just so I could push a change. <3 Cory
 */
public class SearchFragment extends Fragment implements SearchView.OnQueryTextListener {

    private OnFragmentInteractionListener myListener;
    private EditText myEmail;
    private EditText myUsername;
    private EditText myFirstName;
    private EditText myLastName;
    private Button mySendRequest;
    private Button mySearch;
    private TextView mySearchResult;
    private SearchView mySearchView;


    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("NavigationActivity", "" + "SearchFragmentTag");
        // Inflate the layout for this fragment
         View view = inflater.inflate(R.layout.fragment_search, container, false);
         mySendRequest = view.findViewById(R.id.searchButtonSendRequest);
         mySendRequest.setEnabled(false);
         mySendRequest.setOnClickListener(this::onSendRequestClicked);
         mySearch = view.findViewById(R.id.searchButtonSearch);
         myEmail = view.findViewById(R.id.searchEditTextEmail);
         myUsername = view.findViewById(R.id.searchEditTextUsername);
         myLastName = view.findViewById(R.id.searchEditTextLastName);
         myFirstName = view.findViewById(R.id.searchEditTextFirstname);
         mySearch.setOnClickListener(this::onSearchClicked);
         mySearchResult = view.findViewById(R.id.searchTextViewSearchResult);
         mySearchResult.setOnClickListener(this::onSearchResultClicked);

        mySearchView = view.findViewById(R.id.searchSearchView);
        mySearchView.setOnQueryTextListener(this);
        mySearchView.setOnClickListener(this::SearchViewOnClicked);

         return view;
    }

    private void SearchViewOnClicked(View view) {
        mySearchView.setIconified(false);
    }


    private void onSendRequestClicked(View view) {
        mySendRequest.setEnabled(false);
        myListener.onSendRequestAttempt();
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (!TextUtils.isEmpty(newText)) {
            Log.e("searchFragment", "change text: " + newText);
            if (InputVerificationTool.isEmail(newText)) {
                myListener.onSearchByEmailAttempt(newText);
            } else if (InputVerificationTool.isUsername(newText)) {
                myListener.onSearchByUsernameAttempt(newText);
            } else  {
                String[] strings = newText.split("\\s+");
                myListener.onSearchByNameAttempt(strings[0], strings[1]);
            }
        }

        return true;
    }


    /**
     * attempts to search by the email, username, first name and last name provided in order.
     * only search the first valid field and execute the searching only once.
     * @param theSearchButton
     */
    public void onSearchClicked(View theSearchButton) {
        if (myListener != null) {
            if (InputVerificationTool.isEmail(myEmail.getText().toString())) {
                myListener.onSearchByEmailAttempt(myEmail.getText().toString());
                Log.e("SearchFragment: Email ", myEmail.getText().toString());

            } else if (InputVerificationTool.isUsername(myUsername.getText().toString())) {
                myListener.onSearchByUsernameAttempt(myUsername.getText().toString());
                Log.e("SearchFragment: Username ", myUsername.getText().toString());

            } else if (InputVerificationTool.isName(myFirstName.getText().toString()) &&
                    InputVerificationTool.isName(myLastName.getText().toString())) {
                myListener.onSearchByNameAttempt(myFirstName.getText().toString(),
                        myLastName.getText().toString());
                Log.e("SearchFragment: name ", myFirstName.getText().toString() +
                        ", " + myLastName.getText().toString());

            } else {
                Log.e("SearchFragment", "Search fail: " + myEmail.getText().toString());
            }
        }
    }

    public void onSearchResultClicked(View theSearchResultTextView) {
        if (!mySearchResult.getText().toString().equals(getString(R.string.search_textview_user_not_found))) {
            mySendRequest.setEnabled(true);
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
        void onSearchByEmailAttempt(String searchInfo);
        void onSearchByUsernameAttempt(String searchInfo);
        void onSearchByNameAttempt(String firstname, String lastname);
        void onSendRequestAttempt();
    }
}
