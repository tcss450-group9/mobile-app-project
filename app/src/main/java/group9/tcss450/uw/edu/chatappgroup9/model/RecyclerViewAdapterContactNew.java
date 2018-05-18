package group9.tcss450.uw.edu.chatappgroup9.model;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import group9.tcss450.uw.edu.chatappgroup9.R;

public class RecyclerViewAdapterContactNew extends RecyclerView.Adapter<RecyclerViewAdapterContactNew.ViewHolder> {

    private List<String> myContactMembers;
    private ContactItemListener contactItemListener;
    private String myTargetContactMember;
    private final String TAG = "RecyclerViewAdapterContactNew";



    public RecyclerViewAdapterContactNew(List<String> theChatIds){
         this.myContactMembers = theChatIds;

    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView contactUsername;
        ConstraintLayout contactItemLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            contactUsername = itemView.findViewById(R.id.recycleViewItemContactUsername);
            contactItemLayout = itemView.findViewById(R.id.recyclerViewItemContactLayout);
            contactItemLayout.setOnClickListener(this::contactItemLayoutOnClicked);
        }

        private void contactItemLayoutOnClicked(View view) {
            contactItemListener.contactItemLayoutOnClicked(myContactMembers.get(getAdapterPosition()));
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       LayoutInflater inflater = LayoutInflater.from(parent.getContext());
       View view = inflater.inflate(R.layout.recycler_view_item_a_chat,parent , false);
       ViewHolder viewHolder  = new ViewHolder(view);
       return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        myTargetContactMember = myContactMembers.get(position);
        String[] idUsername = myContactMembers.get(position).split(":");
        holder.contactUsername.setText(idUsername[1]);
    }

    public void addData(String newMessage) {
        myContactMembers.add(newMessage);
        notifyDataSetChanged();
    }

    public void setAdapterDataSet(List<String> newDataSet) {
        if (newDataSet != null) {
            myContactMembers = newDataSet;
            notifyDataSetChanged();
        } else {
            myContactMembers = new ArrayList<>();
            notifyDataSetChanged();
        }

    }

    public void setItemClickedListener(ContactItemListener listener) {
        contactItemListener = listener;
    }


    @Override
    public int getItemCount() {
        return myContactMembers.size();
    }


    public interface ContactItemListener {
        void contactItemLayoutOnClicked(String targetMemberId);
    }

}
