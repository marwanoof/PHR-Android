package om.gov.moh.phr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiLabResultCultureHolder;


public class CultureDataRecyclerViewAdapter extends RecyclerView.Adapter<CultureDataRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<ApiLabResultCultureHolder.Culture> cultureArrayList = new ArrayList<>();
    private Context context;

    public CultureDataRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public CultureDataRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_culture, parent, false);
        return new CultureDataRecyclerViewAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CultureDataRecyclerViewAdapter.MyViewHolder holder, int position) {
        final ApiLabResultCultureHolder.Culture textualDataObj = cultureArrayList.get(position);

        holder.name.setText(textualDataObj.getBacteriaName());
        holder.value.setText(textualDataObj.getResult());
    }

    @Override
    public int getItemCount() {
        return cultureArrayList.size();
    }

    public void updateItemsList(ArrayList<ApiLabResultCultureHolder.Culture> items) {
        cultureArrayList = items;
        notifyDataSetChanged();
    }
    public void clear(){
        int size = cultureArrayList.size();
        cultureArrayList.clear();
        notifyItemRangeRemoved(0, size);
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, value;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.tvName);
            value = view.findViewById(R.id.tvValue);
        }
    }
}