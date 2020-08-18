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

public class UpdatesListAdapter extends RecyclerView.Adapter<UpdatesListAdapter.MyViewHolder> {


    private ArrayList<String> mItemsArrayList = new ArrayList<>();
    private Context mContext;

    //the constructor of the LastRecordsRecyclerViewAdapter
    public UpdatesListAdapter(ArrayList<String> mItemsArrayList, Context context) {
        this.mContext = context;
        this.mItemsArrayList = mItemsArrayList;
    }

    //onCreateViewHolder allows you to inflate the "List items view"
//also allows to make some action during once the list created
    @Override
    public UpdatesListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_updates, parent, false);
        return new UpdatesListAdapter.MyViewHolder(itemView);
    }

    //onBindViewHolder , allows you to write the data into the fields
    @Override
    public void onBindViewHolder(@NonNull final UpdatesListAdapter.MyViewHolder holder, int position) {


        holder.tvTitle.setText(mItemsArrayList.get(position));



    }


    @Override
    public int getItemCount() {
        return mItemsArrayList.size();
    }






    public class MyViewHolder extends RecyclerView.ViewHolder {


        private TextView tvTitle;
        private ImageView imgUpdates;

        public MyViewHolder(View view) {
            super(view);

            tvTitle = itemView.findViewById(R.id.tv_updates_title);
            imgUpdates = itemView.findViewById(R.id.img_updates_icon);

        }
    }
}