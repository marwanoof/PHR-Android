package om.gov.moh.phr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import om.gov.moh.phr.R;
import om.gov.moh.phr.models.MyVital;

public class MyVitalListAdapter extends RecyclerView.Adapter<MyVitalListAdapter.MyViewHolder> {

    private ArrayList<MyVital> models;

    private Context context;

    public MyVitalListAdapter(ArrayList<MyVital> models, Context context) {
        this.models = models;

        this.context = context;

    }




    @NonNull
    @Override
    public MyVitalListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_my_vital_home, parent, false);
        return new MyVitalListAdapter.MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyVitalListAdapter.MyViewHolder holder, int position) {


        holder.title.setText(models.get(position).getTitle());
        holder.value.setText(models.get(position).getValue());
        holder.sign.setText(models.get(position).getSign());

    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title,value, sign;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_myvital_title);
            value = itemView.findViewById(R.id.tv_myvital_value);
            sign = itemView.findViewById(R.id.tv_myvital_sign);



        }

    }
}