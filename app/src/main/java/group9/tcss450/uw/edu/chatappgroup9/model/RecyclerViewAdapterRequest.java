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

import group9.tcss450.uw.edu.chatappgroup9.R;
import group9.tcss450.uw.edu.chatappgroup9.utils.SendPostAsyncTask;

public class RecyclerViewAdapterRequest extends RecyclerView.Adapter<RecyclerViewAdapterRequest.ViewHolder>{
    private String[][] myDataset;

    public RecyclerViewAdapterRequest(String[][] dataset) {
        myDataset = dataset;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mySender;
        TextView myFullName;
        Button myAcceptButton;
        Button myRejectButton;
        Context myContext;
        SharedPreferences myPrefs;
        String myUsername;

        public ViewHolder(View v) {
            super(v);
            mySender = v.findViewById(R.id.recyclerViewItemRequestTextViewUsername);
            myFullName = v.findViewById(R.id.recyclerViewItemRequestTextViewFullName);

            myAcceptButton = v.findViewById(R.id.recyclerViewItemRequestButtonAccept);
            myAcceptButton.setOnClickListener(this::onAcceptClicked);

            myRejectButton = v.findViewById(R.id.recyclerViewItemRequestButtonReject);
            myContext = v.getContext();
            myPrefs = myContext.getSharedPreferences(
                    myContext.getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
            myUsername = myPrefs.getString(
                    myContext.getString(R.string.keys_shared_prefs_username), null);
        }

        private void onAcceptClicked(View v) {
            Log.d("RecyclerViewAdapterRequest", "Top of onAcceptClicked");
            JSONObject request = new JSONObject();
            try {
                request.put("username", myUsername);
                request.put("sender", mySender.getText());
            }
            catch(JSONException e) {
                Log.e("RecyclerViewAdapterRequest", "Error creating JSON: " + e.getMessage());
            }
            Uri uri = new Uri.Builder().scheme("https")
                    .appendPath(myContext.getString(R.string.ep_base_url))
                    .appendPath(myContext.getString(R.string.ep_accept_request))
                    .appendQueryParameter("username", myUsername)
                    .appendQueryParameter("sender", mySender.getText().toString())
                    .build();
            Log.d("RecyclerViewAdapterRequest/handleOnPostExecute", uri.toString());

            new SendPostAsyncTask.Builder(uri.toString(), request)
                    .onPostExecute(this::handleOnPostExecute)
                    .build().execute();


        }

        private void handleOnPostExecute(String theResponse) {
            try {
                JSONObject jsonResponse = new JSONObject(theResponse);
                if(jsonResponse.getBoolean("success")) {
                    Toast.makeText(myContext,"Added " + mySender.getText().toString(), Toast.LENGTH_SHORT);
                }
                else {
                    Log.wtf("RecyclerViewAdapterRequest/handleOnPostExecute",
                            "Unable to accept connection request");
                }
            }
            catch(JSONException e) {
                Log.e("RecyclerViewAdapterRequest/handleOnPostExecute",
                        "Error building JSON: " + e.getMessage());
            }
        }
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
        holder.mySender.setText(myDataset[position][0]);
        holder.myFullName.setText(myDataset[position][1]);

        /*holder.myAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(holder.myContext,"You accepted!", Toast.LENGTH_SHORT).show();

            }
        });*/
        holder.myRejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(holder.myContext,"You rejected ._.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return myDataset.length;
    }

    public void setAdapterDataSet(String[][] dataset) {
        if (myDataset != null) {
            myDataset = dataset;
            notifyDataSetChanged();
        } else {
            myDataset = new String[0][0];
            notifyDataSetChanged();
        }
    }
}