package group9.tcss450.uw.edu.chatappgroup9.model;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import group9.tcss450.uw.edu.chatappgroup9.R;
import group9.tcss450.uw.edu.chatappgroup9.utils.SendPostAsyncTask;

/**
 * This is a search view adapter. uses a list to holder the data set.
 * @author Garrett Engle, Jenzel Villanueva, Cory Davis,Minqing Chen
 */
public class RecyclerViewAdapterSearchResult extends RecyclerView.Adapter<RecyclerViewAdapterSearchResult.ViewHolder> {
    private List<String> myDataSet;
    private final String TAG = "RecyclerViewAdapterSearchResult";


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView othersUsername;
        private TextView othersName;
        private String othersMemberId;
        private String myMemberId;
        private Button mySendRequestButton;

        public ViewHolder(View itemView) {
            super(itemView);
            othersUsername = itemView.findViewById(R.id.recycleViewItemchatUsername);
            othersName = itemView.findViewById(R.id.recycleViewItemFirstLastName);
            mySendRequestButton = itemView.findViewById(R.id.recycleViewItemSendRequest);
            mySendRequestButton.setOnClickListener(this::onSendRequestClicked);
        }

        /**
         * send a async task to the server to send a request.
         * @param view
         */
        private void onSendRequestClicked(View view) {
            Uri uri = new Uri.Builder().scheme("https").appendPath(itemView.getContext().getString(R.string.ep_base_url))
                    .appendPath(itemView.getContext().getString(R.string.ep_send_friend_request)).build();
            JSONObject nameJSON = new JSONObject();

            try {
                nameJSON.put("memberA", myMemberId);
                nameJSON.put("memberB", othersMemberId);
            } catch (JSONException theException) {
                Log.e("Request Button Clicked", "Error creating JSON" + theException.getMessage());
            }

            new SendPostAsyncTask.Builder(uri.toString(), nameJSON)
                    .onPostExecute(this::handleEndOfSendRequest).build().execute();

        }

        /**
         * tell the user a request was sent.
         * @param result
         */
        private void handleEndOfSendRequest(String result) {

            try {
                JSONObject jsonObject = new JSONObject(result);
                boolean success = jsonObject.getBoolean(itemView.getContext().getString(R.string.keys_json_success));

                if (success) {
                    Toast.makeText(itemView.getContext(),
                            "Request Sent", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(itemView.getContext(),
                            jsonObject.getString(itemView.getContext().getString(R.string.keys_json_message)), Toast.LENGTH_LONG).show();
                }

            } catch (JSONException theException) {
                Log.e("Request Button Clicked", "Error creating JSON" + theException.getMessage());
            }
        }
    }

    /**
     * initialized the data set.
     * @param theDataset
     */
    public RecyclerViewAdapterSearchResult(List<String> theDataset) {
        myDataSet = theDataset;
    }

    /**
     * sets this adapter to the specified data set.
     *
     * @param theDataSet
     */
    public void setAdapterDataSet(List<String> theDataSet) {
        if (theDataSet != null) {
            myDataSet = theDataSet;

        } else {
            myDataSet = new ArrayList<>();
        }
        notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerViewAdapterSearchResult.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_item_search, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

//            Log.e(TAG, "item count: " + getItemCount() + "position: " + position + "myDataSet[position] " + myDataSet.get(position));
        String[] data = myDataSet.get(position).split(":");
        Log.e("myDataSet", myDataSet.get(position).toString());
        if (data.length > 0) {
            holder.othersName.setText(data[1] + " " + data[2]);
            holder.othersMemberId = data[3];
            holder.myMemberId = data[4];
            holder.othersUsername.setText(data[0]);
        }


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return myDataSet.size();
    }
}
