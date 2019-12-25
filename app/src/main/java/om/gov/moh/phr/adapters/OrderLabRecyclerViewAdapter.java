package om.gov.moh.phr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiLabOrdersListHolder;
import om.gov.moh.phr.fragments.LabResultDetailsFragment;
import om.gov.moh.phr.interfaces.AdapterToFragmentConnectorInterface;
import om.gov.moh.phr.interfaces.MediatorInterface;

public class OrderLabRecyclerViewAdapter extends RecyclerView.Adapter<OrderLabRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<ApiLabOrdersListHolder.ApiOredresList> labOrdersArrayList;
    private Context context;
    private AdapterToFragmentConnectorInterface mCallback;
    private MediatorInterface mediatorInterface;
    private ArrayList<ApiLabOrdersListHolder.ApiOredresList> arraylist;
    public OrderLabRecyclerViewAdapter(MediatorInterface mMediatorCallback, ArrayList<ApiLabOrdersListHolder.ApiOredresList> labOrdersArrayList, Context context) {
        this.labOrdersArrayList = labOrdersArrayList;
        this.context = context;
        this.arraylist = new ArrayList<ApiLabOrdersListHolder.ApiOredresList>();
        this.arraylist.addAll(labOrdersArrayList);
        this.mediatorInterface = mMediatorCallback;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ApiLabOrdersListHolder.ApiOredresList orderObj = labOrdersArrayList.get(position);
        holder.tvProcName.setText(orderObj.getProcName());
        holder.tvStatus.setText(orderObj.getStatus());
        Date date = new Date(orderObj.getOrderDate());
        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
        String dateText = df2.format(date);
        holder.tvOrderDate.setText(dateText);
        holder.tvEstName.setText(orderObj.getEstName());
        holder.clOrderItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediatorInterface.changeFragmentTo(LabResultDetailsFragment.newInstance(orderObj), orderObj.getProcName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return labOrdersArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvProcName, tvStatus, tvOrderDate, tvEstName;
        ConstraintLayout clOrderItem;

        public MyViewHolder(View view) {
            super(view);
            tvProcName = view.findViewById(R.id.tv_procName);
            tvStatus = view.findViewById(R.id.tv_status);
            tvOrderDate = view.findViewById(R.id.tv_orderDate);
            tvEstName = view.findViewById(R.id.tv_estName);
            clOrderItem = view.findViewById(R.id.cl_orderItem);
        }
    }
    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        labOrdersArrayList.clear();
        if (charText.length() == 0) {
            labOrdersArrayList.addAll(arraylist);
        } else {
            for (ApiLabOrdersListHolder.ApiOredresList wp : arraylist) {
                if (wp.getProcName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    labOrdersArrayList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}
