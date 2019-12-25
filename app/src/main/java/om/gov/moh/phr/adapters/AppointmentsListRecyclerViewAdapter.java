package om.gov.moh.phr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiAppointmentsListHolder;
import om.gov.moh.phr.interfaces.AdapterToFragmentConnectorInterface;

import static om.gov.moh.phr.models.MyConstants.ACTION_CANCEL;
import static om.gov.moh.phr.models.MyConstants.ACTION_RESCHEDULE;

public class AppointmentsListRecyclerViewAdapter extends
        RecyclerView.Adapter<AppointmentsListRecyclerViewAdapter.MyViewHolder> {


    private ArrayList<ApiAppointmentsListHolder.Appointments> mItemsArrayList = new ArrayList<>();
    private Context mContext;
    private AdapterToFragmentConnectorInterface mCallback;

    //the constructor of the LastRecordsRecyclerViewAdapter
    public AppointmentsListRecyclerViewAdapter(AdapterToFragmentConnectorInterface fragment, Context context) {
        this.mContext = context;
        mCallback = fragment;
    }

    //onCreateViewHolder allows you to inflate the "List items view"
//also allows to make some action during once the list created
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.swipe_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    //onBindViewHolder , allows you to write the data into the fields
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final ApiAppointmentsListHolder.Appointments result = mItemsArrayList.get(position);
        String title = result.getDescription() + result.getEstName();
        holder.tvTitle.setText(title);


        holder.btnReschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onMyListItemClicked(result, ACTION_RESCHEDULE, holder.getAdapterPosition());
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onMyListItemClicked(result, ACTION_CANCEL, holder.getAdapterPosition());
            }
        });

    /*    holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onMyListItemClicked(result, "");
            }
        });*/
    }


    @Override
    public int getItemCount() {
        return mItemsArrayList.size();
    }

    public void updateItemsList(ArrayList<ApiAppointmentsListHolder.Appointments> items) {
        mItemsArrayList = items;
        notifyDataSetChanged();
    }

    public void deleteItemAt(int position) {
        mItemsArrayList.remove(position);
        notifyItemRemoved(position);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private Button btnReschedule;
        private Button btnDelete;
        private TextView tvTitle;


        public MyViewHolder(View view) {
            super(view);
            btnReschedule = itemView.findViewById(R.id.btn_reschedule);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            tvTitle = itemView.findViewById(R.id.tv_title);
        }
    }
}