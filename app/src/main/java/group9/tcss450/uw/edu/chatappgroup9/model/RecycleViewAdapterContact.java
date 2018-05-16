package group9.tcss450.uw.edu.chatappgroup9.model;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import group9.tcss450.uw.edu.chatappgroup9.R;

public class RecycleViewAdapterContact extends RecyclerView.Adapter<RecycleViewAdapterContact.ViewHolder> {
    private String[][] mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView myUsername;
        public TextView myFullName;
        public Button myAddDeleteButton;
        public ViewHolder(View itemView) {
            super(itemView);
            myUsername = itemView.findViewById(R.id.recycleViewContactItemUsername);
            myFullName = itemView.findViewById(R.id.recycleview_item_textview_fullname);
            myAddDeleteButton = itemView.findViewById(R.id.recycleview_item_button_add_remove);
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
        holder.myUsername.setText(mDataset[position][0]);
        holder.myFullName.setText(mDataset[position][1]);

        //boolean viewPendingSwitchValue =
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}
