package om.gov.moh.phr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiAppointmentsListHolder;
import om.gov.moh.phr.interfaces.AdapterToFragmentConnectorInterface;

public class RefferalsListRecyclerViewAdapter extends
        RecyclerView.Adapter<RefferalsListRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<ApiAppointmentsListHolder.Referrals> mItemsArrayList = new ArrayList<>();
    private Context mContext;
    private AdapterToFragmentConnectorInterface mCallback;

    //the constructor of the LastRecordsRecyclerViewAdapter
    public RefferalsListRecyclerViewAdapter(AdapterToFragmentConnectorInterface fragment, Context context) {
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
        final ApiAppointmentsListHolder.Referrals result = mItemsArrayList.get(position);
        Date date = new Date(result.getSendDate());
        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
        String releasedTime = df2.format(date);
        String title = result.getReferralBy() + ", " + releasedTime + ", " + result.getRefInstitute() /*+ ", " + result.getDescription()*/;
        holder.tvTitle.setText(title);




        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onMyListItemClicked(result.getDescription(), "REFFERAL");
            }
        });
    }


    @Override
    public int getItemCount() {
        return mItemsArrayList.size();
    }

    public void updateItemsList(ArrayList<ApiAppointmentsListHolder.Referrals> items) {
        mItemsArrayList = items;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private Button btnReschedule;
        private Button btnDelete;
        private TextView tvTitle;
        private ImageButton ibArrow;

        public MyViewHolder(View view) {
            super(view);
            btnReschedule = itemView.findViewById(R.id.btn_reschedule);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            tvTitle = itemView.findViewById(R.id.tv_title);
            ibArrow = itemView.findViewById(R.id.imgArrowDetailsSelf);
            btnReschedule.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
            ibArrow.setVisibility(View.GONE);
        }
    }
}
