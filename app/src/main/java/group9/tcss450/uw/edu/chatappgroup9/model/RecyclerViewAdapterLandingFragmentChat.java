package group9.tcss450.uw.edu.chatappgroup9.model;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import group9.tcss450.uw.edu.chatappgroup9.R;

/**
 * This class is a recycler view adapter for recycler view in Landing Fragment.
 * This adapter use a list to hold all data.
 * @author Garrett Engle, Jenzel Villanueva, Cory Davis,Minqing Chen
 */
public class RecyclerViewAdapterLandingFragmentChat extends RecyclerView.Adapter<RecyclerViewAdapterLandingFragmentChat.ViewHolder> {

    private List<String> myChatIds;
    private ChatItemListener chatItemListener;
    private String targetChatId;

    public RecyclerViewAdapterLandingFragmentChat(List<String> theChatIds){
         this.myChatIds = theChatIds;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        Button oneChatItem;
        public ViewHolder(View itemView) {
            super(itemView);
            oneChatItem = itemView.findViewById(R.id.recycleViewItemChat);
            oneChatItem.setOnClickListener(this::chatItemOnClicked);
        }

        /**
         * Fires a chat id when a item clicked.
         * @param view
         */
        private void chatItemOnClicked(View view) {
            chatItemListener.chatItemOnClicked(myChatIds.get(getAdapterPosition()));
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
    }

    public void addData(String newMessage) {
        myChatIds.add(newMessage);
        notifyDataSetChanged();
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
