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

import group9.tcss450.uw.edu.chatappgroup9.model.RecyclerViewAdapterSearchResult;
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

        mySearchView = view.findViewById(R.id.searchSearchView);
        mySearchView.setOnQueryTextListener(this);
        mySearchView.setOnClickListener(this::SearchViewOnClicked);
        View noneSearchView = view.findViewById(R.id.searchNoneSearchArea);
        noneSearchView.setOnClickListener(this::noneSearchViewAreaClick);


        RecyclerView recyclerView = view.findViewById(R.id.searchRecycleViewUserFound);
        recyclerView.setOnClickListener(this::noneSearchViewAreaClick);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter (see also next example)
        String[] s = {};
        RecyclerViewAdapterSearchResult mAdapter = new RecyclerViewAdapterSearchResult(s);
        recyclerView.setAdapter(mAdapter);

         return view;
    }

    private void SearchViewOnClicked(View view) {
        mySearchView.setIconified(false);
    }


    private void onSendRequestClicked(View view) {
//        myListener.onSendRequestAttempt();
    }

    private void noneSearchViewAreaClick(View view) {
        Log.e("SearchFragment","noneSearchViewAreaClick");
        mySearchView.setQuery("", false);
        mySearchView.setIconified(true);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (!TextUtils.isEmpty(newText)) {
//            Log.e("searchFragment", "change text: " + newText);
            if (InputVerificationTool.isEmail(newText)) {
                myListener.onSearchByEmailAttempt(newText);
            } else if (InputVerificationTool.isUsername(newText)) {
                myListener.onSearchByUsernameAttempt(newText);
            } else  {
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
    }
}
