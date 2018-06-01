package group9.tcss450.uw.edu.chatappgroup9.model;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import group9.tcss450.uw.edu.chatappgroup9.R;

/**
 * This class is a recycler view adapter for chat message. uses a list to hold all the data.
 */
public class RecyclerViewAdapterMessages extends RecyclerView.Adapter<RecyclerViewAdapterMessages.ViewHolder> {

    private List<String> myDataSet;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView myMessage;
        private TextView myOtherUserMessage;
        private LinearLayout myLeftLayout;
        private LinearLayout myRightLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            myMessage = itemView.findViewById(R.id.recyclyViewItemUserMessages);
            myOtherUserMessage = itemView.findViewById(R.id.recyCleViewItemOtherUserMessage);
            myLeftLayout = itemView.findViewById(R.id.recycleViewItemChatLeftLayout);
            myRightLayout = itemView.findViewById(R.id.recycleViewItemChatRightLayout);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerViewAdapterMessages(List<String> theDataSet) {
        myDataSet = theDataSet;
    }

    /**
     * insert a new message to the data set
     * @param newMessage
     */
    public void addData(String newMessage) {
        myDataSet.add(newMessage);
        notifyItemInserted(getItemCount() - 1);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerViewAdapterMessages.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                     int viewType) {
        // create a new view
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_item_a_message, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    /**
     * @since
     * The layouts of the messages is invisible and must set visible.
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String[] data = myDataSet.get(position).split(":");

        if (data.length > 0) {

            if (data[0].equals(data[1])) {
                holder.myLeftLayout.setVisibility(View.VISIBLE);
                holder.myMessage.setText(data[2]);
            } else {
                holder.myRightLayout.setVisibility(View.VISIBLE);
                holder.myOtherUserMessage.setText(data[2]);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return myDataSet.size();
    }
}
