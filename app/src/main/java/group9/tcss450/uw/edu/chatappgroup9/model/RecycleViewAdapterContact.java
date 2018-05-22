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

import group9.tcss450.uw.edu.chatappgroup9.R;
import group9.tcss450.uw.edu.chatappgroup9.utils.SendPostAsyncTask;

public class RecycleViewAdapterContact extends RecyclerView.Adapter<RecycleViewAdapterContact.ViewHolder> {
    private String[][] mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView myFriendUsername;
        public TextView myFriendFullName;
        public String myUsername;
        public Button myDeleteButton;
        public Switch viewRequestSwitch;
        public Context myContext;
        public SharedPreferences myPrefs;
        public ViewHolder(View itemView) {
            super(itemView);
            viewRequestSwitch = itemView.findViewById(R.id.contactsSwitchExisting);
            myFriendUsername = itemView.findViewById(R.id.recycleViewItemContactUsername);
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
         * @param v The button clicked
         */
        public void onClick(View v) {
            Uri uri = new Uri.Builder().scheme("https")
                    .appendPath(myContext.getString(R.string.ep_base_url))
                    .appendPath(myContext.getString(R.string.ep_delete_connection))
                    .appendQueryParameter("myUsername", myUsername)
                    .appendQueryParameter("friendUsername", myFriendUsername.getText().toString())
                    .build();
            Log.d("RecyclerViewAdapterContact", uri.toString());

            JSONObject request = new JSONObject();
            try {
                request.put("myUsername", myUsername);
                request.put("friendUsername",myFriendUsername.toString());

            }
            catch(JSONException e) {
                Log.e("RecyclerViewAdapterContact", "Error building JSON: " + e.getMessage());
            }

            new SendPostAsyncTask.Builder(uri.toString(),request)
                    .onPostExecute(this::handleOnPostDeleteContact)
                    .build().execute();
        }

        /**
         * Handles errors if the sendPostAsyncTask wasn't successful. Sends a toast if it was successful.
         * @param theResponse The JSON received from the web service.
         */
        private void handleOnPostDeleteContact(String theResponse) {
            try {
                JSONObject jsonResponse = new JSONObject(theResponse);
                if(jsonResponse.getBoolean("success")) {
                    Toast.makeText(myContext,"Deleted " + myFriendUsername.getText().toString(),
                            Toast.LENGTH_SHORT).show();

                }
                else {
                    Log.wtf("RecyclerViewAdapterRequest/handleOnPostAccept",
                            "Unable to delete contact: " + jsonResponse.get("error"));
                }
            }
            catch(JSONException e) {
                Log.e("RecyclerViewAdapterRequest/handleOnPostAccept",
                        "Error building JSON: " + e.getMessage());
            }
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecycleViewAdapterContact(String[][] theDataset) {
        mDataset = theDataset;
    }

    public void setAdapterDataSet(String[][] myDataset) {
        if (myDataset != null) {
            mDataset = myDataset;
            notifyDataSetChanged();
        } else {
            mDataset = new String[0][0];
            notifyDataSetChanged();
        }


    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecycleViewAdapterContact.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                        int viewType) {
        // create a new view
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_item_contact, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        
            holder.myFriendUsername.setText(mDataset[position][0]);
            holder.myFriendFullName.setText(mDataset[position][1]);

    }



    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}
