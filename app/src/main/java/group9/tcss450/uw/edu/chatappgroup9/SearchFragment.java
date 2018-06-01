package group9.tcss450.uw.edu.chatappgroup9;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import java.util.ArrayList;

import group9.tcss450.uw.edu.chatappgroup9.model.RecyclerViewAdapterSearchResult;
import group9.tcss450.uw.edu.chatappgroup9.utils.InputVerificationTool;
/**
 * This class relates to search a user by username, email or fist name and last name
 * @author Garrett Engle, Jenzel Villanueva, Cory Davis,Minqing Chen
 */
public class SearchFragment extends Fragment implements SearchView.OnQueryTextListener {

    private OnFragmentInteractionListener myListener;
    private SearchView mySearchView;


    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        mySearchView = view.findViewById(R.id.searchSearchView);
        mySearchView.setOnQueryTextListener(this);
        mySearchView.setOnClickListener(this::SearchViewOnClicked);
        View noneSearchView = view.findViewById(R.id.searchNoneSearchArea);
        noneSearchView.setOnClickListener(this::noneSearchViewAreaClick);

        RecyclerView recyclerView = view.findViewById(R.id.searchRecycleViewUserFound);
        recyclerView.setOnClickListener(this::noneSearchViewAreaClick);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        RecyclerViewAdapterSearchResult mAdapter = new RecyclerViewAdapterSearchResult(new ArrayList<String>());
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    /**
     * starts a searching by clicking the whole search view.
     * @param view
     */
    private void SearchViewOnClicked(View view) {
        mySearchView.setIconified(false);
    }


    private void noneSearchViewAreaClick(View view) {
        Log.e("SearchFragment", "noneSearchViewAreaClick");
        mySearchView.setQuery("", false);
        mySearchView.setIconified(true);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        //detects user input and search by username, email or first name and last name.
        if (!TextUtils.isEmpty(newText)) {
//            Log.e("searchFragment", "change text: " + newText);
            if (InputVerificationTool.isEmail(newText)) {
                myListener.onSearchByEmailAttempt(newText);
            } else if (InputVerificationTool.isUsername(newText)) {
                myListener.onSearchByUsernameAttempt(newText);
            } else {
                String[] strings = newText.split("\\s+");
                if (strings.length > 1) {
                    myListener.onSearchByNameAttempt(strings[0], strings[1]);
                }
            }
        }
        return true;
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

    public interface OnFragmentInteractionListener {
        /**
         * search by email
         * @param email
         */
        void onSearchByEmailAttempt(String email);

        /**
         * search by username
         * @param username
         */
        void onSearchByUsernameAttempt(String username);

        /**
         * search by first name and last name
         * @param firstname
         * @param lastname
         */
        void onSearchByNameAttempt(String firstname, String lastname);
    }
}
