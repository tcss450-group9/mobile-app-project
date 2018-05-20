package group9.tcss450.uw.edu.chatappgroup9.model;

import android.net.Uri;
import android.support.constraint.ConstraintLayout;
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

public class RecyclerViewAdapterSearchResult extends RecyclerView.Adapter<RecyclerViewAdapterSearchResult.ViewHolder> {
    private List<String> mDataset;
    private final String TAG = "RecyclerViewAdapterSearchResult";


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView othersUsername;
        private TextView othersName;
        private String othersMemberId;
        private String myMemberId;
        private Button mySendRequestButton;

        public ViewHolder(View itemView) {
            super(itemView);
            othersUsername = itemView.findViewById(R.id.recycleViewItemUsername);
            othersName = itemView.findViewById(R.id.recycleViewItemFirstLastName);
            mySendRequestButton = itemView.findViewById(R.id.recycleViewItemSendRequest);
            mySendRequestButton.setOnClickListener(this::onSendRequestClicked);
        }

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

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerViewAdapterSearchResult(List<String> theDataset) {
        mDataset = theDataset;
    }

    public void setAdapterDataSet(List<String> myDataset) {
        if (myDataset != null) {
            mDataset = myDataset;

        } else {
            mDataset = new ArrayList<>();
        }
        notifyDataSetChanged();


    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerViewAdapterSearchResult.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                         int viewType) {
        // create a new view
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_item_search, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

            Log.e(TAG, "item count: " + getItemCount() + "position: " + position + "mDataset[position] " + mDataset.get(position));
            String[] data = mDataset.get(position).split(":");
            Log.e("mDataset", mDataset.get(position).toString());
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
        return mDataset.size();
    }
}
