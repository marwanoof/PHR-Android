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
import om.gov.moh.phr.apimodels.DLabResultsHolder;

public class LabResultsDetailsRecyclerViewAdapter extends RecyclerView.Adapter<LabResultsDetailsRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<DLabResultsHolder.TabularData> labResultArrayList=new ArrayList<>();
    private Context context;


    public LabResultsDetailsRecyclerViewAdapter( Context mContext) {
        this.context = mContext;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.test_item_lab_results, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DLabResultsHolder.TabularData object = labResultArrayList.get(position);
        if (object.getInterpretation().equals("A")) {
            setTextColorItem(holder.tvTestName);
            setTextColorItem(holder.tvResult);
            //setTextColorItem(holder.tvUnit);
            setTextColorItem(holder.tvRange);
        }
        holder.tvTestName.setText(object.getTestName());
        holder.tvResult.setText(object.getResult()+ " " + object.getUnit());
       // holder.tvUnit.setText(object.getUnit());
        holder.tvRange.setText(object.getRangeHigh() + " - " + object.getRangeLow());
    }

    private void setTextColorItem(TextView textView) {
        textView.setTextColor(context.getResources().getColor(R.color.colorRed));
    }

    @Override
    public int getItemCount() {
        return labResultArrayList.size();
    }
    public void updateItemsList(ArrayList<DLabResultsHolder.TabularData> items) {
        labResultArrayList = items;
        notifyDataSetChanged();
    }
    public void clear(){
        int size = labResultArrayList.size();
        labResultArrayList.clear();
        notifyItemRangeRemoved(0, size);
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTestName, tvResult, tvUnit, tvRange;

        public MyViewHolder(View view) {
            super(view);
            tvTestName = view.findViewById(R.id.tv_testName);
            tvResult = view.findViewById(R.id.tv_testResult);
           // tvUnit = view.findViewById(R.id.tv_testUnit);
            tvRange = view.findViewById(R.id.tv_testRange);
        }
    }
}
