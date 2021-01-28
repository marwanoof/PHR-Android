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
import om.gov.moh.phr.models.NameDate;

public class VitalInfoRecyclerViewAdapter extends RecyclerView.Adapter<VitalInfoRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<NameDate> stringArrayList;

    private Context context;
    private String type;

    public VitalInfoRecyclerViewAdapter(ArrayList<NameDate> stringArrayList, Context context,String type) {
        this.stringArrayList = stringArrayList;
        this.context = context;
        this.type = type;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vital_info_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
         String vitalInfo = stringArrayList.get(position).getName();
        String vitalDate = stringArrayList.get(position).getDate();
        holder.tvDetails.setText(vitalInfo);
        if (type.equals("final") || type.equals("history")){
            holder.tvDate.setVisibility(View.VISIBLE);
            holder.tvDate.setText(vitalDate);
        }
    }

    @Override
    public int getItemCount() {
        return stringArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
          TextView tvDetails,tvDate;

        public MyViewHolder(View view) {
            super(view);
            tvDetails = view.findViewById(R.id.tv_vitalInfoDetails);
            tvDate = view.findViewById(R.id.tv_vitalInfoDate);
            tvDate.setVisibility(View.GONE);
        }
    }
}
