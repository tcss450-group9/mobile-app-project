package group9.tcss450.uw.edu.chatappgroup9.model;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import group9.tcss450.uw.edu.chatappgroup9.R;

public class RecylerViewAdapterSearchResult extends RecyclerView.Adapter<RecylerViewAdapterSearchResult.ViewHolder> {
    private String[] mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView myUsername;
        public TextView myName;
        public ViewHolder(View itemView) {
            super(itemView);
            myUsername = itemView.findViewById(R.id.recycleViewItemUsername);
            myName = itemView.findViewById(R.id.recycleViewItemFirstLastName);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecylerViewAdapterSearchResult(String[] theDataset) {
        mDataset = theDataset;
    }

    public void setAdapterDataSet(String[] myDataset) {
        if (myDataset != null) {
            mDataset = myDataset;
            notifyDataSetChanged();
        } else {
            mDataset = new String[0];
            notifyDataSetChanged();
        }


    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecylerViewAdapterSearchResult.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                        int viewType) {
        // create a new view
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_view_item_search, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        String[] data = mDataset[position].split(":");
        if (data.length > 0) {
            holder.myUsername.setText(data[0]);
            holder.myName.setText(data[1] + " " +data[2]);
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}
