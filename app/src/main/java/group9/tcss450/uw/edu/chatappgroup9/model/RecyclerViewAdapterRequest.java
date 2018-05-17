package group9.tcss450.uw.edu.chatappgroup9.model;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import group9.tcss450.uw.edu.chatappgroup9.R;

public class RecyclerViewAdapterRequest extends RecyclerView.Adapter<RecyclerViewAdapterRequest.ViewHolder>{
    private String[][] myDataset;

    public RecyclerViewAdapterRequest(String[][] dataset) {
        myDataset = dataset;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView myUsername;
        TextView myFullName;
        Button myAcceptButton;
        Button myRejectButton;

        public ViewHolder(View v) {
            super(v);
            myUsername = v.findViewById(R.id.recyclerViewItemRequestTextViewUsername);
            myFullName = v.findViewById(R.id.recyclerViewItemRequestTextViewFullName);
            myAcceptButton = v.findViewById(R.id.recyclerViewItemRequestButtonAccept);
            myRejectButton = v.findViewById(R.id.recyclerViewItemRequestButtonReject);
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
        holder.myUsername.setText(myDataset[position][0]);
        holder.myFullName.setText(myDataset[position][1]);
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