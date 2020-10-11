package om.gov.moh.phr.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiHomeHolder;

import static om.gov.moh.phr.models.MyConstants.LANGUAGE_ARABIC;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_PREFS;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_SELECTED;

public class MyVitalListAdapter extends RecyclerView.Adapter<MyVitalListAdapter.MyViewHolder> {

    private ArrayList<ApiHomeHolder.ApiRecentVitals> models;
    private Context context;

    public MyVitalListAdapter(ArrayList<ApiHomeHolder.ApiRecentVitals> models, Context context) {
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
        if (getStoredLanguage().equals(LANGUAGE_ARABIC) && models.get(position).getVitalNameNls() != null)
            holder.title.setText(models.get(position).getVitalNameNls());
        else
            holder.title.setText(models.get(position).getName());
        holder.value.setText(models.get(position).getValue());
        holder.sign.setText(models.get(position).getUnit());
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, value, sign;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_myvital_title);
            value = itemView.findViewById(R.id.tv_myvital_value);
            sign = itemView.findViewById(R.id.tv_myvital_sign);
        }

    }

    private String getStoredLanguage() {
        SharedPreferences sharedPref = context.getSharedPreferences(LANGUAGE_PREFS, Context.MODE_PRIVATE);
        return sharedPref.getString(LANGUAGE_SELECTED, getDeviceLanguage());
    }

    private String getDeviceLanguage() {
        return Locale.getDefault().getLanguage();
    }
}