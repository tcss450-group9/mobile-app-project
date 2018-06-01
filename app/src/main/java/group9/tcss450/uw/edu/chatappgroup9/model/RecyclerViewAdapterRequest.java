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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import group9.tcss450.uw.edu.chatappgroup9.R;
import group9.tcss450.uw.edu.chatappgroup9.utils.SendPostAsyncTask;

/**
 * This class is a recycler view adapter for pending request in ContactsFragment. It uses a list to hold
 * all the data.
 * @author Garrett Engle, Jenzel Villanueva, Cory Davis,Minqing Chen
 */
public class RecyclerViewAdapterRequest extends RecyclerView.Adapter<RecyclerViewAdapterRequest.ViewHolder> {
    private List<String> myDataSet;
    private String TAG = "RecyclerViewAdapterRequest";


    public RecyclerViewAdapterRequest(ArrayList<String> dataset) {
        myDataSet = dataset;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mySenderUsername;
        private TextView mySenderFullName;
        private Button myAcceptButton;
        private Button myRejectButton;
        private Context myContext;
        private SharedPreferences myPrefs;
        private String myUsername;

        public ViewHolder(View v) {
            super(v);
            mySenderUsername = v.findViewById(R.id.recyclerViewItemRequestTextViewUsername);
            mySenderFullName = v.findViewById(R.id.recyclerViewItemRequestTextViewFullName);

            myAcceptButton = v.findViewById(R.id.recyclerViewItemRequestButtonAccept);
            myAcceptButton.setOnClickListener(this::onAcceptClicked);
            myRejectButton = v.findViewById(R.id.recyclerViewItemRequestButtonReject);
            myRejectButton.setOnClickListener(this::onRejectClicked);

            myContext = v.getContext();
            myPrefs = myContext.getSharedPreferences(
                    myContext.getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
            myUsername = myPrefs.getString(
                    myContext.getString(R.string.keys_shared_prefs_username), null);
        }

        /**
         * sends a async task to the server to accept the request.
         *
         * @param view the button.
         */
        private void onAcceptClicked(View view) {
            Log.d(TAG, "Top of onAcceptClicked");
            JSONObject request = new JSONObject();
            try {
                request.put("username", myUsername);
                request.put("sender", mySenderUsername.getText());
            } catch (JSONException e) {
                Log.e(TAG, "Error creating JSON: " + e.getMessage());
            }
            Uri uri = new Uri.Builder().scheme("https")
                    .appendPath(myContext.getString(R.string.ep_base_url))
                    .appendPath(myContext.getString(R.string.ep_accept_request))
                    .appendQueryParameter("username", myUsername)
                    .appendQueryParameter("sender", mySenderUsername.getText().toString())
                    .build();
            Log.d(TAG, uri.toString());

            new SendPostAsyncTask.Builder(uri.toString(), request)
                    .onPostExecute(this::handleOnPostAccept)
                    .build().execute();
        }

        /**
         * Handles errors if the sendPostAsyncTask wasn't successful. Sends a toast if it was successful.
         *
         * @param theResponse The JSON received from the web service.
         */
        private void handleOnPostAccept(String theResponse) {
            deleteAdapterItem(getAdapterPosition());
            try {
                JSONObject jsonResponse = new JSONObject(theResponse);
                if (jsonResponse.getBoolean("success")) {
                    Toast.makeText(myContext, "Added " + mySenderUsername.getText().toString(),
                            Toast.LENGTH_SHORT).show();

                } else {
                    Log.wtf("RecyclerViewAdapterRequest/handleOnPostAccept",
                            "Unable to accept connection request");
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error building JSON: " + e.getMessage());
            }
        }

        private void onRejectClicked(View v) {
            JSONObject request = new JSONObject();
            try {
                request.put("username", myUsername);
                request.put("sender", mySenderUsername.getText());
            } catch (JSONException e) {
                Log.e(TAG, "Error creating JSON: " + e.getMessage());
            }
            Uri uri = new Uri.Builder().scheme("https")
                    .appendPath(myContext.getString(R.string.ep_base_url))
                    .appendPath(myContext.getString(R.string.ep_reject_request))
                    .appendQueryParameter("username", myUsername)
                    .appendQueryParameter("sender", mySenderUsername.getText().toString())
                    .build();
            Log.d(TAG, "handleOnPostAccept " + uri.toString());

            new SendPostAsyncTask.Builder(uri.toString(), request)
                    .onPostExecute(this::handleOnPostReject)
                    .build().execute();
        }

        /**
         * Handles errors if the sendPostAsyncTask wasn't successful. Sends a toast if it was successful.
         *
         * @param theResponse The JSON received from the web service.
         */
        private void handleOnPostReject(String theResponse) {
            deleteAdapterItem(getAdapterPosition());
            try {
                JSONObject jsonResponse = new JSONObject(theResponse);
                if (jsonResponse.getBoolean("success")) {
                    Toast.makeText(myContext, "Rejected " + mySenderUsername.getText().toString()
                            + " ._.", Toast.LENGTH_SHORT).show();

                } else {
                    Log.wtf("RecyclerViewAdapterRequest/handleOnPostReject",
                            "Unable to reject connection request");
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error building JSON: " + e.getMessage());
            }
        }
    }

    /**
     * only delete the data in the specified position of the data set.
     *
     * @param adapterPosition
     */
    private void deleteAdapterItem(int adapterPosition) {
        myDataSet.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerViewAdapterRequest.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_item_request, parent, false);
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapterRequest.ViewHolder holder, int position) {
        String[] data = myDataSet.get(position).split(":");
        Log.e("myDataSet", myDataSet.get(position).toString());
        if (data.length > 0) {
            holder.mySenderUsername.setText(data[0]);
            holder.mySenderFullName.setText(data[1]);
        }
    }

    @Override
    public int getItemCount() {
        return myDataSet.size();
    }

    /**
     * sets this adapter to the specified data set.
     *
     * @param theDataSet
     */
    public void setAdapterDataSet(List<String> theDataSet) {
        if (myDataSet != null) {
            myDataSet = theDataSet;
        } else {
            myDataSet = new ArrayList<String>();
        }
        notifyDataSetChanged();
    }
}