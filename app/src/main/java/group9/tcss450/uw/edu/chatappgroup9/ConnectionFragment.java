package group9.tcss450.uw.edu.chatappgroup9;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ConnectionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ConnectionFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {

    private OnFragmentInteractionListener mListener;
    private EditText mySearchBar;
    private ImageButton mySearchButton;
    private ImageButton myDropdownMenuButton;
    private ScrollView myConnectionsScrollView;

    public ConnectionFragment() {
        // Required empty public constructor
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("ConnectionFragment", "show up");
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_connection, container, false);
        ImageButton ib = v.findViewById(R.id.connections_imagebutton_dropdown);
        ib.setOnClickListener(this::toggleSearchOptionsMenu);

        EditText searchBar = v.findViewById(R.id.connections_edittext_searchbar);

        ImageButton searchButton = v.findViewById(R.id.connections_imagebutton_search);

        ImageButton menuButton = v.findViewById(R.id.connections_imagebutton_dropdown);

        ScrollView connections = v.findViewById(R.id.connections_scrollview_connections_table);

        return v;
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
     *
     * @param v
     */
    public void onClick(View v) {
        if(mListener != null) {

        }
    }

    /**
     * Opens the menu containing search criteria for existing connections.
     * @param v The Button object which opens this menu.
     */
    public void toggleSearchOptionsMenu(View v) {
        PopupMenu pm = new PopupMenu(getContext(), v);
        pm.setOnMenuItemClickListener(this);
        pm.inflate(R.menu.connections_menu_search_criteria);
        pm.show();
    }

    /**
     * Performs the action associated with the MenuItem selected by the user.
     * @param item The MenuItem selected by the user.
     * @return
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.connections_menu_searchByName:
                Toast.makeText(getContext(), "Search by Name", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.connections_menu_searchByUN:
                Toast.makeText(getContext(), "Search by Username", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.connections_menu_searchByNickname:
                Toast.makeText(getContext(), "Search by Nickname", Toast.LENGTH_SHORT).show();
                return true;
            default:
                Toast.makeText(getContext(), "Error choosing item", Toast.LENGTH_SHORT).show();
                return false;
        }
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
