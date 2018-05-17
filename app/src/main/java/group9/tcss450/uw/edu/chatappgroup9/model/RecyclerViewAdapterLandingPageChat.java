package group9.tcss450.uw.edu.chatappgroup9.model;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import group9.tcss450.uw.edu.chatappgroup9.R;

public class RecyclerViewAdapterLandingPageChat extends RecyclerView.Adapter<RecyclerViewAdapterLandingPageChat.ViewHolder> {

    private List<String> myChatIds;
    private ChatItemListener chatItemListener;
    private String targetChatId;



    public RecyclerViewAdapterLandingPageChat(List<String> theChatIds){
         this.myChatIds = theChatIds;

    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        Button oneChatItem;
        public ViewHolder(View itemView) {
            super(itemView);
            oneChatItem = itemView.findViewById(R.id.recycleViewItemChat);
            oneChatItem.setOnClickListener(this::chatItemOnClicked);
        }

        private void chatItemOnClicked(View view) {
            chatItemListener.chatItemOnClicked(myChatIds.get(getItemCount()-1));
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
        targetChatId = myChatIds.get(position);
        holder.oneChatItem.setText("Chat ID " + targetChatId);
    }

    public void addData(String newMessage) {
        myChatIds.add(newMessage);
        notifyItemInserted(getItemCount() - 1);
    }

    public void setItemClickedListener(ChatItemListener listener) {
        chatItemListener = listener;
    }


    @Override
    public int getItemCount() {
        return myChatIds.size();
    }


    public interface ChatItemListener {
        void chatItemOnClicked(String targetChatId);
    }

}
