package group9.tcss450.uw.edu.chatappgroup9.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import group9.tcss450.uw.edu.chatappgroup9.R;
import group9.tcss450.uw.edu.chatappgroup9.utils.SendPostAsyncTask;

/**
 * This adapter populates the recyclerview on the ContactsFragment with the users existing contacts.
 * Each contact consists of a username and full name which are replaced by the data returned by the
 * database. Each contact also has a delete button which will remove the connection from the
 * database and update the list. Their friendship has been nullified =(.
 * @author Garrett Engle, Jenzel Villanueva, Cory Davis,Minqing Chen
 * @version 5/31/18
 */
public class RecyclerViewAdapterContact extends RecyclerView.Adapter<RecyclerViewAdapterContact.ViewHolder> {
    private List<String> mDataset;

    /**
     *  Provide a reference to the views for each data item
     *  Complex data items may need more than one view per item, and
     *  you provide access to all the views for a data item in a view holder
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView myFriendUsername;
        private TextView myFriendFullName;
        private String myUsername;
        private Button myDeleteButton;
        private Context myContext;
        private SharedPreferences myPrefs;


        /**
         * Constructor for creating a ViewHolder object.
         * @param itemView
         */
        public ViewHolder(View itemView) {
            super(itemView);
            myFriendUsername = itemView.findViewById(R.id.recycleViewItemchatUsername);
            myFriendFullName = itemView.findViewById(R.id.recycleview_item_textview_fullname);
            myDeleteButton = itemView.findViewById(R.id.recycleview_item_button_add_remove);
            myContext = itemView.getContext();
            myPrefs = myContext.getSharedPreferences
                    (myContext.getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
            myUsername = myPrefs.getString(myContext.getString
                    (R.string.keys_shared_prefs_username), null);

            myDeleteButton.setOnClickListener(this::onClick);
        }

        /**
         * Executes a SendPostAsyncTask which removes the selected contact from the contacts table
         * in the database
         *
         * @param v The button clicked
         */
        public void onClick(View v) {
            deleteAdapterItem(getAdapterPosition());

            Uri uri = new Uri.Builder().scheme("https")
                    .appendPath(myContext.getString(R.string.ep_base_url))
                    .appendPath(myContext.getString(R.string.ep_delete_connection))
                    .appendQueryParameter("myUsername", myUsername)
                    .appendQueryParameter("friendUsername", myFriendUsername.getText().toString())
                    .build();
            Log.d("RVAdapterContact", uri.toString());

            JSONObject request = new JSONObject();
            try {
                request.put("myUsername", myUsername);
                request.put("friendUsername", myFriendUsername.toString());

            } catch (JSONException e) {
                Log.e("RVAdapterContact", "Error building JSON: " + e.getMessage());
            }

            new SendPostAsyncTask.Builder(uri.toString(), request)
                    .onPostExecute(this::handleOnPostDeleteContact)
                    .build().execute();
        }

        /**
         * only delete the data in the specified position of the data set.
         * @param adapterPosition
         */
        private void deleteAdapterItem(int adapterPosition) {
            mDataset.remove(adapterPosition);
            notifyItemRemoved(adapterPosition);
            notifyDataSetChanged();
        }

        /**
         * Handles errors if the sendPostAsyncTask wasn't successful. Sends a toast if it was successful.
         *
         * @param theResponse The JSON received from the web service.
         */
        private void handleOnPostDeleteContact(String theResponse) {
            try {
                JSONObject jsonResponse = new JSONObject(theResponse);
                if (jsonResponse.getBoolean("success")) {
                    Toast.makeText(myContext, "Deleted " + myFriendUsername.getText().toString(),
                            Toast.LENGTH_SHORT).show();
//                    mDataset = new String[0][0];
//                    notifyDataSetChanged();
                } else {
                    Log.wtf("RecyclerViewAdapterRequest/handleOnPostAccept",
                            "Unable to delete contact: " + jsonResponse.get("error"));
                }
            } catch (JSONException e) {
                Log.e("RVAdapterRequest",
                        "Error building JSON: " + e.getMessage());
            }
        }
    }

    public RecyclerViewAdapterContact(List<String> theDataset) {
        mDataset = theDataset;
    }

    /**
     * Sets the contact data to the given Array.
     * @param myDataset An arraylist of contacts and their corresponding data.
     */
    public void setAdapterDataSet(List<String> myDataset) {
        if (myDataset != null) {
            mDataset = myDataset;
            notifyDataSetChanged();
        } else {
            mDataset = new ArrayList<String>();
            notifyDataSetChanged();
        }
    }

    /**
     * Create new views (invoked by the layout manager)
     */
    @Override
    public RecyclerViewAdapterContact.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                    int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_item_contact, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    /**
     * Replace the contents of a view (invoked by the layout manager)
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String[] data = mDataset.get(position).split(":");
        Log.e("mDataset", mDataset.get(position).toString());
        if (data.length > 0) {
            holder.myFriendUsername.setText(data[0]);
            holder.myFriendFullName.setText(data[1]);
        }

    }


    /**
     * Return the size of your dataset (invoked by the layout manager)
     */
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
