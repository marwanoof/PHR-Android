package om.gov.moh.phr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiDemographicsHolder;
import om.gov.moh.phr.interfaces.AdapterToFragmentConnectorInterface;

public class LinkedInstitutesRecyclerViewAdapter extends
        RecyclerView.Adapter<LinkedInstitutesRecyclerViewAdapter.MyViewHolder> {

    private ArrayList<ApiDemographicsHolder.ApiDemographicItem.Patients> mItemsArrayList;
    private Context mContext;
    private AdapterToFragmentConnectorInterface mCallback;

    //the constructor of the LastRecordsRecyclerViewAdapter
    public LinkedInstitutesRecyclerViewAdapter(AdapterToFragmentConnectorInterface fragment, Context context, ArrayList<ApiDemographicsHolder.ApiDemographicItem.Patients> items) {
        this.mItemsArrayList = items;
        this.mContext = context;
        mCallback = fragment;
    }

    //onCreateViewHolder allows you to inflate the "List items view"
//also allows to make some action during once the list created
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_dependent_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    //onBindViewHolder , allows you to write the data into the fields
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final ApiDemographicsHolder.ApiDemographicItem.Patients result = mItemsArrayList.get(position);

        holder.tvName.setText(result.getEstName());
        if (result.getIsPending()) {
            holder.tvCivilId.setVisibility(View.VISIBLE);
            holder.tvCivilId.setText(mContext.getString(R.string.title_pending));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onMyListItemClicked(result, null);
            }
        });
    }

    public void update(ArrayList<ApiDemographicsHolder.ApiDemographicItem.Patients> items) {
        mItemsArrayList.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mItemsArrayList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvRelation;
        private final TextView tvCivilId;


        public MyViewHolder(View view) {
            super(view);
            tvName = itemView.findViewById(R.id.tv_name);
            tvRelation = itemView.findViewById(R.id.tv_relation);
            tvCivilId = itemView.findViewById(R.id.tv_civil_id);
            tvRelation.setVisibility(View.GONE);
            tvCivilId.setVisibility(View.INVISIBLE);
        }
    }
}