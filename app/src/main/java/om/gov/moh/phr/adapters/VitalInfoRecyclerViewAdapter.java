package om.gov.moh.phr.adapters;
//test comment..    sdsd
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import om.gov.moh.phr.R;

public class VitalInfoRecyclerViewAdapter extends RecyclerView.Adapter<VitalInfoRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<String> stringArrayList;
    private Context context;

    public VitalInfoRecyclerViewAdapter(ArrayList<String> stringArrayList, Context context) {
        this.stringArrayList = stringArrayList;
        this.context = context;
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
         String vitalInfo = stringArrayList.get(position);
        holder.tvDetails.setText(vitalInfo);
    }

    @Override
    public int getItemCount() {
        return stringArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
          TextView tvDetails;

        public MyViewHolder(View view) {
            super(view);
            tvDetails = view.findViewById(R.id.tv_vitalInfoDetails);
        }
    }
}
