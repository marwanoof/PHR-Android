package om.gov.moh.phr.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiFriendChatListHolder;
import om.gov.moh.phr.fragments.ChatMessagesFragment;
import om.gov.moh.phr.interfaces.MediatorInterface;

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<ApiFriendChatListHolder.ApiFriendListInfo> friendChatArrayList;
    private Context context;
    private MediatorInterface mediatorInterface;

    public ChatRecyclerViewAdapter(ArrayList<ApiFriendChatListHolder.ApiFriendListInfo> friendChatArrayList, Context context, MediatorInterface mMediatorCallback) {
        this.friendChatArrayList = friendChatArrayList;
        this.context = context;
        this.mediatorInterface = mMediatorCallback;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final ApiFriendChatListHolder.ApiFriendListInfo messageObj = friendChatArrayList.get(position);
        if(messageObj.isNew()){
            holder.tvUnreadCount.setVisibility(View.VISIBLE);
        }else {
            if (messageObj.getUnreadCount() != 0)
                holder.tvUnreadCount.setVisibility(View.VISIBLE);
            else
                holder.tvUnreadCount.setVisibility(View.INVISIBLE);
        }
     /*   if(isNewRecieved) {
            SharedPreferences sharedPref = context.getSharedPreferences("CHAT-BODY", Context.MODE_PRIVATE);
            String messageSender = sharedPref.getString("MESSAGE-SENDER", null);
            if (messageObj.getCreatedBy().trim().equals(messageSender.trim()))
                holder.tvUnreadCount.setVisibility(View.VISIBLE);
            else
                holder.tvUnreadCount.setVisibility(View.INVISIBLE);
        }else {
            if (messageObj.getUnreadCount() != 0)
                holder.tvUnreadCount.setVisibility(View.VISIBLE);
            else
                holder.tvUnreadCount.setVisibility(View.INVISIBLE);
        }*/
        holder.tvCreatedName.setText(messageObj.getCreatedName());
        holder.tvCreatedDate.setText(messageObj.getCreatedDate());
        holder.clFreindItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.tvUnreadCount.setVisibility(View.INVISIBLE);
                messageObj.setNew(false);
                notifyDataSetChanged();
                mediatorInterface.changeFragmentTo(ChatMessagesFragment.newInstance(messageObj), "");
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendChatArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvCreatedName, tvCreatedDate, tvUnreadCount;
        ConstraintLayout clFreindItem;

        public MyViewHolder(View view) {
            super(view);
            tvCreatedName = view.findViewById(R.id.tv_CreatedName);
            tvCreatedDate = view.findViewById(R.id.tv_CreatedDate);
            tvUnreadCount = view.findViewById(R.id.tv_unReadCount);
            clFreindItem = view.findViewById(R.id.cl_friendItem);
        }
    }
}
