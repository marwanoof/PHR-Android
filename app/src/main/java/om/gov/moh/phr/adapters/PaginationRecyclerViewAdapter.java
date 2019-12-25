package om.gov.moh.phr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import om.gov.moh.phr.R;
import om.gov.moh.phr.interfaces.AdapterToFragmentConnectorInterface;
import om.gov.moh.phr.models.Pagination;

public class PaginationRecyclerViewAdapter extends
        RecyclerView.Adapter<PaginationRecyclerViewAdapter.MyViewHolder> {

    private ArrayList<Pagination> mItemsArrayList = new ArrayList<>();
    private Context mContext;
    private AdapterToFragmentConnectorInterface mCallback;

    //the constructor of the LastRecordsRecyclerViewAdapter
    public PaginationRecyclerViewAdapter(AdapterToFragmentConnectorInterface fragment, Context context) {
        this.mContext = context;
        mCallback = fragment;
    }

    //onCreateViewHolder allows you to inflate the "List items view"
//also allows to make some action during once the list created
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_home_pagination_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    //onBindViewHolder , allows you to write the data into the fields
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final Pagination result = mItemsArrayList.get(position);

        holder.tvTitle.setText(result.getTitle());

        holder.ibIcon.setImageResource(result.getIconId());


        holder.ibIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onMyListItemClicked(result.getFragment(), result.getFragmentTag());
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCallback.onMyListItemClicked(result.getFragment(), result.getFragmentTag());

            }
        });
    }


    public void updateList(ArrayList<Pagination> items) {
        mItemsArrayList = items;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mItemsArrayList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final ImageView ibIcon;


        public MyViewHolder(View view) {
            super(view);
            tvTitle = itemView.findViewById(R.id.tv_title);
            ibIcon = itemView.findViewById(R.id.ib_icon);
        }
    }
}