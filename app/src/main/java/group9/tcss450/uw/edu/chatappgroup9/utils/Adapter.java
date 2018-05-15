package group9.tcss450.uw.edu.chatappgroup9.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import group9.tcss450.uw.edu.chatappgroup9.R;

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private  String[]items;
    public Adapter(Context context, String[] items){
         this.context = context;
         this.items = items;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       LayoutInflater inflater = LayoutInflater.from(context);
       View row = inflater.inflate(R.layout.custom_row,parent , false);
       Item item  = new Item(row);
       return item;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((Item)holder).textview.setText(items[position]);
        //((Item)holder).message.setText(items[position])
        ((Item)holder).textview.setOnClickListener(new View.OnClickListener() {
            Context activity = holder.itemView.getContext();
            SharedPreferences prefs =  holder.itemView.getContext().getSharedPreferences(holder.itemView.getContext().getString(R.string.keys_json_chatid),context.MODE_PRIVATE);

            @Override
            public void onClick(View view) {
                prefs.edit().putString(context.getString(R.string.keys_json_chatid), ((Item)holder).textview.getText().toString());

            }
        });

    }

    @Override
    public int getItemCount() {
        return items.length;
    }
    public class Item extends RecyclerView.ViewHolder{
        Button textview;
        TextView message;
        public Item(View itemView) {
            super(itemView);
            textview = itemView.findViewById(R.id.item);
          //  message = itemView.findViewById(R.id.Landing_Recycler_message);
        }

    }

}
