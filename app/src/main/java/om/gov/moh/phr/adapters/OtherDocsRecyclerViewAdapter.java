package om.gov.moh.phr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiOtherDocsHolder;
import om.gov.moh.phr.fragments.OtherDocsDetailsFragment;
import om.gov.moh.phr.interfaces.MediatorInterface;

public class OtherDocsRecyclerViewAdapter extends RecyclerView.Adapter<OtherDocsRecyclerViewAdapter.MyViewHolder>{
    private ArrayList<ApiOtherDocsHolder.ApiDocInfo> othersDocsArrayList;
    private Context context;
    private ArrayList<ApiOtherDocsHolder.ApiDocInfo> arraylist;
    private MediatorInterface mediatorInterface;
    public OtherDocsRecyclerViewAdapter(MediatorInterface mMediatorCallback, ArrayList<ApiOtherDocsHolder.ApiDocInfo> othersDocsList, Context context) {
        this.othersDocsArrayList = othersDocsList;
        this.context = context;
        this.arraylist = new ArrayList<ApiOtherDocsHolder.ApiDocInfo>();
        this.arraylist.addAll(othersDocsList);
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
        final ApiOtherDocsHolder.ApiDocInfo docObj = othersDocsArrayList.get(position);
        holder.tvDocType.setText(docObj.getTypeD());
        Date date = new Date(docObj.getIndexed());
        SimpleDateFormat df2 = new SimpleDateFormat("MM /dd /yyyy "+"\n"+" HH:mm:ss");
        String dateText = df2.format(date);
        holder.tvDateWritten.setText(dateText);
        holder.tvDosage.setText(docObj.getEstName());
        holder.clOrderItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediatorInterface.changeFragmentTo(OtherDocsDetailsFragment.newInstance(docObj), docObj.getEstFullname());
            }
        });
    }

    @Override
    public int getItemCount() {
          return othersDocsArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvDocType, tvDosage, tvDateWritten, tvEstName;
        ConstraintLayout clOrderItem;

        public MyViewHolder(View view) {
            super(view);
            tvDocType = view.findViewById(R.id.tv_procName);
            tvDosage = view.findViewById(R.id.tv_status);
            tvDateWritten = view.findViewById(R.id.tv_orderDate);
            tvEstName = view.findViewById(R.id.tv_estName);
            tvEstName.setVisibility(View.INVISIBLE);
            clOrderItem = view.findViewById(R.id.cl_orderItem);
        }
    }
    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        othersDocsArrayList.clear();
        if (charText.length() == 0) {
            othersDocsArrayList.addAll(arraylist);
        } else {
            for (ApiOtherDocsHolder.ApiDocInfo wp : arraylist) {
                if (wp.getTypeD().toLowerCase(Locale.getDefault()).contains(charText)) {
                    othersDocsArrayList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}
