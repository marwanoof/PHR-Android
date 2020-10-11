package om.gov.moh.phr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiAppointmentsListHolder;
import om.gov.moh.phr.apimodels.ApiHomeHolder;
import om.gov.moh.phr.interfaces.AdapterToFragmentConnectorInterface;

import static om.gov.moh.phr.models.MyConstants.ACTION_CANCEL;
import static om.gov.moh.phr.models.MyConstants.ACTION_RESCHEDULE;

public class ComingAppointmentListAdapter extends RecyclerView.Adapter<ComingAppointmentListAdapter.MyViewHolder> {


    private ArrayList<ApiHomeHolder.ApiAppointments> mItemsArrayList = new ArrayList<>();
    private Context mContext;

    //the constructor of the LastRecordsRecyclerViewAdapter
    public ComingAppointmentListAdapter(ArrayList<ApiHomeHolder.ApiAppointments> mItemsArrayList, Context context) {
        this.mContext = context;
        this.mItemsArrayList = mItemsArrayList;
    }

    //onCreateViewHolder allows you to inflate the "List items view"
//also allows to make some action during once the list created
    @Override
    public ComingAppointmentListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_coming_appointment, parent, false);
        return new ComingAppointmentListAdapter.MyViewHolder(itemView);
    }

    //onBindViewHolder , allows you to write the data into the fields
    @Override
    public void onBindViewHolder(@NonNull final ComingAppointmentListAdapter.MyViewHolder holder, int position) {


        holder.tvTitle.setText(mItemsArrayList.get(position).getDescription()+" "+ mItemsArrayList.get(position).getEstName());



    }


    @Override
    public int getItemCount() {
        return mItemsArrayList.size();
    }






    public class MyViewHolder extends RecyclerView.ViewHolder {


        private TextView tvTitle;
        private ImageView ivType;

        public MyViewHolder(View view) {
            super(view);

            tvTitle = itemView.findViewById(R.id.tv_comingApp_title);
            ivType = itemView.findViewById(R.id.img_appType);
            ivType.setVisibility(View.GONE);

        }
    }
}