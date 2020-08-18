package om.gov.moh.phr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import om.gov.moh.phr.R;

public class NotificationHomeAdapter extends RecyclerView.Adapter<NotificationHomeAdapter.MyViewHolder> {


    private ArrayList<String> mItemsArrayList = new ArrayList<>();
    private Context mContext;

    //the constructor of the LastRecordsRecyclerViewAdapter
    public NotificationHomeAdapter(ArrayList<String> mItemsArrayList, Context context) {
        this.mContext = context;
        this.mItemsArrayList = mItemsArrayList;
    }

    //onCreateViewHolder allows you to inflate the "List items view"
//also allows to make some action during once the list created
    @Override
    public NotificationHomeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_notification_home, parent, false);
        return new NotificationHomeAdapter.MyViewHolder(itemView);
    }

    //onBindViewHolder , allows you to write the data into the fields
    @Override
    public void onBindViewHolder(@NonNull final NotificationHomeAdapter.MyViewHolder holder, int position) {


        holder.tvTitle.setText(mItemsArrayList.get(position));



    }


    @Override
    public int getItemCount() {
        return mItemsArrayList.size();
    }






    public class MyViewHolder extends RecyclerView.ViewHolder {


        private TextView tvTitle;


        public MyViewHolder(View view) {
            super(view);

            tvTitle = itemView.findViewById(R.id.tv_notification_title);


        }
    }
}