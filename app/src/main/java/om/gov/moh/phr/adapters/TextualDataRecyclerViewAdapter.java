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
import om.gov.moh.phr.apimodels.ApiTextualDataHolder;
import om.gov.moh.phr.apimodels.DLabResultsHolder;

public class TextualDataRecyclerViewAdapter extends RecyclerView.Adapter<TextualDataRecyclerViewAdapter.MyViewHolder>{
    private ArrayList<DLabResultsHolder.TextualData> textualDataArrayList=new ArrayList<>();
    private Context context;

    public TextualDataRecyclerViewAdapter(Context context) {
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
        final DLabResultsHolder.TextualData textualDataObj = textualDataArrayList.get(position);
        holder.tvResult.setText(textualDataObj.getParamName()+"\n"+textualDataObj.getResult());
    }

    @Override
    public int getItemCount() {
        return textualDataArrayList.size();
    }
    public void updateItemsList(ArrayList<DLabResultsHolder.TextualData> items) {
        textualDataArrayList = items;
        notifyDataSetChanged();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView  tvResult;

        public MyViewHolder(View view) {
            super(view);
            tvResult = view.findViewById(R.id.tv_vitalInfoDetails);
        }
    }
}
