package om.gov.moh.phr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiHomeHolder;
import om.gov.moh.phr.fragments.ChatMessagesFragment;
import om.gov.moh.phr.interfaces.MediatorInterface;

public class MessageChatsAdapter extends RecyclerView.Adapter<MessageChatsAdapter.MyViewHolder> {


    private ArrayList<ApiHomeHolder.ApiChatMessages> mItemsArrayList;
    private Context mContext;
    private MediatorInterface mediatorInterface;

    //the constructor of the LastRecordsRecyclerViewAdapter
    public MessageChatsAdapter(MediatorInterface mMediatorCallback, ArrayList<ApiHomeHolder.ApiChatMessages> mItemsArrayList, Context context) {
        this.mContext = context;
        this.mItemsArrayList = mItemsArrayList;
        this.mediatorInterface = mMediatorCallback;
    }

    //onCreateViewHolder allows you to inflate the "List items view"
//also allows to make some action during once the list created
    @Override
    public MessageChatsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_message_home, parent, false);
        return new MessageChatsAdapter.MyViewHolder(itemView);
    }

    //onBindViewHolder , allows you to write the data into the fields
    @Override
    public void onBindViewHolder(@NonNull final MessageChatsAdapter.MyViewHolder holder, final int position) {


        holder.tvTitle.setText(mItemsArrayList.get(position).getCreatedName());
        holder.tvDate.setText(mItemsArrayList.get(position).getCreatedDate());
    }


    @Override
    public int getItemCount() {
        return mItemsArrayList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {


        private TextView tvTitle, tvDate;


        public MyViewHolder(View view) {
            super(view);

            tvTitle = itemView.findViewById(R.id.tv_chat_title);
            tvDate = itemView.findViewById(R.id.tv_chat_date);

        }
    }
}