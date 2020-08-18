package om.gov.moh.phr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiFriendChatListHolder;
import om.gov.moh.phr.interfaces.MediatorInterface;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.MyViewHolder> {
    private ArrayList<ApiFriendChatListHolder.ApiFriendListInfo> messagesArrayList;
    private Context context;
    private MediatorInterface mediatorInterface;

    public ChatRoomAdapter(ArrayList<ApiFriendChatListHolder.ApiFriendListInfo> friendChatArrayList, Context context, MediatorInterface mMediatorCallback) {
        this.messagesArrayList = friendChatArrayList;
        this.context = context;
        this.mediatorInterface = mMediatorCallback;
    }

    @Override
    public int getItemViewType(int position) {
        if (messagesArrayList.get(position).getCreatedBy() != null) {
            final ApiFriendChatListHolder.ApiFriendListInfo messageObj = messagesArrayList.get(position);

            if (messageObj.getCreatedBy().equals(mediatorInterface.getCurrentUser().getCivilId())) {
                return 0;
            } else {
                return 1;
            }
        }
        return 0;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_self, parent, false);
            return new MyViewHolder(view);
        }else if(viewType == 1){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_other, parent, false);
            return new MyViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ApiFriendChatListHolder.ApiFriendListInfo messageObj = messagesArrayList.get(position);
        holder.message.setText(messageObj.getMessageBody());
        holder.timestamp.setText(messageObj.getCreatedDate());
    }

    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView message, timestamp;

        public MyViewHolder(View view) {
            super(view);
            message = (TextView) itemView.findViewById(R.id.message);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
        }
    }
}
