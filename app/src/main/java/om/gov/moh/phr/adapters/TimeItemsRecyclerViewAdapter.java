package om.gov.moh.phr.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import om.gov.moh.phr.R;
import om.gov.moh.phr.interfaces.AdapterToFragmentConnectorInterface;
import om.gov.moh.phr.models.CustomSlot;

public class
TimeItemsRecyclerViewAdapter extends
        RecyclerView.Adapter<TimeItemsRecyclerViewAdapter.MyViewHolder> {


    private Context mContext;
    private AdapterToFragmentConnectorInterface mCallback;
    private ArrayList<String> mItemsArrayList = new ArrayList<>();
    private CustomSlot mCustomItem;
    int mSelectedPosition = -1;

    //the constructor of the LastRecordsRecyclerViewAdapter
    public TimeItemsRecyclerViewAdapter(AdapterToFragmentConnectorInterface fragment, Context context) {
        this.mContext = context;
        mCallback = fragment;
    }

    //onCreateViewHolder allows you to inflate the "List items view"
//also allows to make some action during once the list created
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_appointment_time_item, parent, false);
        return new MyViewHolder(itemView);
    }

    //onBindViewHolder , allows you to write the data into the fields
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final String result = mItemsArrayList.get(position);

        //highlight
        if (mSelectedPosition == position) {
            holder.vOutline.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.colorRed));
            holder.vContainer.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.colorRed));
            holder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
        } else {
            holder.vOutline.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.colorTimeItemGray));
            holder.vContainer.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.colorWhite));
            holder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.colorTextViewFontNormal));
        }

        holder.tvTitle.setText(result);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             //   Log.d("re-runId", " onBindViewHolder:" + result + " / " + mCustomItem.getRunIdArrayList());

                mSelectedPosition = position;
                notifyDataSetChanged();
                mCustomItem.setSelectedRunId(mCustomItem.getRunIdArrayList().get(position));
                mCustomItem.setSelectedTime(mCustomItem.getTimeBlock().get(position));
                mCallback.onMyListItemClicked(mCustomItem, result);
            }
        });

    }


    public void updateList(CustomSlot item) {
        mCustomItem = item;
        mItemsArrayList = item.getTimeBlock();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mItemsArrayList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final View vOutline;
        private final View vContainer;
        private final TextView tvTitle;


        public MyViewHolder(View view) {
            super(view);
            vOutline = itemView.findViewById(R.id.v_outline);
            vContainer = itemView.findViewById(R.id.v_container);
            tvTitle = itemView.findViewById(R.id.tv_title);

        }
    }
}