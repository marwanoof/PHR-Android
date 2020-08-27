package om.gov.moh.phr.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiLabOrdersListHolder;
import om.gov.moh.phr.fragments.LabResultDetailsFragment;
import om.gov.moh.phr.fragments.LabResultsFragment;
import om.gov.moh.phr.interfaces.AdapterToFragmentConnectorInterface;
import om.gov.moh.phr.interfaces.MediatorInterface;

public class OrderLabRecyclerViewAdapter extends RecyclerView.Adapter<OrderLabRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<ApiLabOrdersListHolder.ApiOredresList> labOrdersArrayList = new ArrayList<>();
    ;
    private Context context;
    private ArrayList<ApiLabOrdersListHolder.ApiOredresList> arraylist = new ArrayList<>();
    private MediatorInterface mediatorInterface;
    private int row_index = -1;
    private int clickCount = 0;

    public OrderLabRecyclerViewAdapter(MediatorInterface mMediatorCallback, Context context) {
        this.context = context;
        this.mediatorInterface = mMediatorCallback;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_lab_result, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final ApiLabOrdersListHolder.ApiOredresList orderObj = labOrdersArrayList.get(position);
//        if (row_index == position) {
//            holder.imageButton.setBackgroundTintList(context.getResources().getColorStateList(R.color.colorPeach));
//            holder.clOrderItem.setBackgroundColor(context.getResources().getColor(R.color.colorPeach));
//        } else {
//            holder.imageButton.setBackgroundTintList(context.getResources().getColorStateList(R.color.colorWhite));
//            holder.clOrderItem.setBackgroundColor(context.getResources().getColor(R.color.colorWhite));
//        }

        holder.tvProcName.setText(orderObj.getProcName());
        String status = orderObj.getStatus();
        switch (status){
            case "completed":
                holder.imageButton.setBackground(context.getResources().getDrawable(R.drawable.lab_complate));
                break;
            default:
                holder.imageButton.setBackground(null);
                break;
        }
        //holder.tvStatus.setText(orderObj.getStatus());
        LabResultDetailsFragment.newInstance(orderObj);
        Date date = new Date(orderObj.getOrderDate());
        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        String dateText = df2.format(date);
        holder.tvOrderDate.setText(dateText);
        holder.tvEstName.setText(orderObj.getEstName());
        holder.clOrderItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                row_index = position;
                notifyDataSetChanged();
                mediatorInterface.changeFragmentTo(LabResultDetailsFragment.newInstance(orderObj), orderObj.getProcName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return labOrdersArrayList.size();
    }

    public void updateItemsList(ArrayList<ApiLabOrdersListHolder.ApiOredresList> items) {
        labOrdersArrayList = items;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvProcName, tvStatus, tvOrderDate, tvEstName;
        ConstraintLayout clOrderItem;
        ImageButton imageButton;
        CardView labDetails_card;

        public MyViewHolder(View view) {
            super(view);
            tvProcName = view.findViewById(R.id.tv_lab_title);
            //tvStatus = view.findViewById(R.id.tv_lab_desc);
            tvOrderDate = view.findViewById(R.id.tv_section_title_lab);
            tvEstName = view.findViewById(R.id.tv_lab_desc);
            clOrderItem = view.findViewById(R.id.labResult_constraint);
            imageButton=view.findViewById(R.id.btn_lab_status);

        }
    }

    // Filter Class
    public void filter(String charText) {

        this.arraylist.addAll(labOrdersArrayList);
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
