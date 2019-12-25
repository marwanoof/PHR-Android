package om.gov.moh.phr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiVitalsPivotHolder;
import om.gov.moh.phr.interfaces.AdapterToFragmentConnectorInterface;

public class VitalsGraphRecyclerViewAdapter extends
        RecyclerView.Adapter<VitalsGraphRecyclerViewAdapter.MyViewHolder> {

    private ArrayList<ApiVitalsPivotHolder.Pivot> mPivotArrayList; // contain the JSON array call "items" which is available in Youtube response
    // mCallback variable will allow us to communicate with fragment
    private AdapterToFragmentConnectorInterface mCallback;
    private Context mContext;
    private int mActiveItem;

    //the constructor of the LastRecordsRecyclerViewAdapter
    public VitalsGraphRecyclerViewAdapter(AdapterToFragmentConnectorInterface fragment, Context context, ArrayList<ApiVitalsPivotHolder.Pivot> items, int activatedItem) {
        this.mPivotArrayList = items;
        this.mContext = context;
        mCallback = fragment;
        this.mActiveItem = activatedItem;

    }

    //onCreateViewHolder allows you to inflate the "List items view"
//also allows to make some action during once the list created
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_vitals_graph_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    //onBindViewHolder , allows you to write the data into the fields
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final ApiVitalsPivotHolder.Pivot item = mPivotArrayList.get(position);

        holder.tvTitle.setText(item.getVitalSign());
       if (mActiveItem == position) {
            holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.colorWhite));
        } else {
            holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.colorLiteGray));
        }

        holder.tvRow1i1.setText(String.valueOf(item.getValue1()));
        holder.tvRow1i2.setText(String.valueOf(item.getValue2()));
        holder.tvRow1i3.setText(String.valueOf(item.getValue3()));

        holder.tvRow1i4.setText(String.valueOf(item.getValue5()));
        holder.tvRow1i5.setText(String.valueOf(item.getValue4()));


        holder.tvRow2i1.setText(String.valueOf(item.getVitalDate1()));
        holder.tvRow2i2.setText(String.valueOf(item.getVitalDate2()));
        holder.tvRow2i3.setText(String.valueOf(item.getVitalDate3()));

        holder.tvRow2i4.setText(String.valueOf(item.getVitalDate4()));
        holder.tvRow2i5.setText(String.valueOf(item.getVitalDate5()));


        /**
         * on listItemClicked
         */
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveItem(position);
                mCallback.onMyListItemClicked(item, null);
            }
        });


    }


    public void setActiveItem(int newActiveItem) {
        if (mActiveItem != newActiveItem) {
            mActiveItem = newActiveItem;
            notifyDataSetChanged();
        }
    }


    @Override
    public int getItemCount() {
        return mPivotArrayList.size();
    }

//MyViewHolder extends RecyclerView.ViewHolder
//allows you to declare and initialize views which you need to use in ...
//... onBindViewHolder function

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public final TextView tvTitle;
        //col 1
        public final TextView tvRow1i1;
        public final TextView tvRow1i2;
        public final TextView tvRow1i3;

        public final TextView tvRow1i4;
        public final TextView tvRow1i5;
        //col 2
        public final TextView tvRow2i1;
        public final TextView tvRow2i2;
        public final TextView tvRow2i3;

        public final TextView tvRow2i4;
        public final TextView tvRow2i5;


        public MyViewHolder(View view) {
            super(view);

            tvTitle = itemView.findViewById(R.id.tv_title);

            tvRow1i1 = itemView.findViewById(R.id.tv_row1_i1);
            tvRow1i2 = itemView.findViewById(R.id.tv_row1_i2);
            tvRow1i3 = itemView.findViewById(R.id.tv_row1_i3);
            tvRow1i4 = itemView.findViewById(R.id.tv_row1_i4);
            tvRow1i5 = itemView.findViewById(R.id.tv_row1_i5);

            tvRow2i1 = itemView.findViewById(R.id.tv_row2_i1);
            tvRow2i2 = itemView.findViewById(R.id.tv_row2_i2);
            tvRow2i3 = itemView.findViewById(R.id.tv_row2_i3);
            tvRow2i4 = itemView.findViewById(R.id.tv_row2_i4);
            tvRow2i5 = itemView.findViewById(R.id.tv_row2_i5);

        }
    }
}