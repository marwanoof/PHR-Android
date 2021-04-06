package om.gov.moh.phr.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiHomeHolder;
import om.gov.moh.phr.models.DummyVitalSigns;
import om.gov.moh.phr.models.GlobalMethods;
import om.gov.moh.phr.models.GlobalMethodsKotlin;

import static om.gov.moh.phr.models.MyConstants.LANGUAGE_ARABIC;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_PREFS;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_SELECTED;

public class MyVitalListAdapter extends RecyclerView.Adapter<MyVitalListAdapter.MyViewHolder> {

    private ArrayList<ApiHomeHolder.ApiRecentVitals> models;
    private Context context;
    private ArrayList<DummyVitalSigns> dummyVitalSigns;
    private boolean isDefault = false;

    public MyVitalListAdapter(Context context, ArrayList<DummyVitalSigns> dummyVitalSigns, boolean isDefault) {
        this.context = context;
        this.dummyVitalSigns = dummyVitalSigns;
        this.isDefault = isDefault;
    }

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
    public void onBindViewHolder(@NonNull final MyVitalListAdapter.MyViewHolder holder, int position) {
        if (isDefault){
            final DummyVitalSigns dummyVital = dummyVitalSigns.get(position);
            String title = "";
            String unit = "";
            if (getStoredLanguage().equals(LANGUAGE_ARABIC)){
                title = dummyVital.getNameNls();
            }else{
                title = dummyVital.getName();
            }
            holder.title.setText(title);
            holder.value.setText(dummyVital.getValue());
            holder.sign.setText(unit);
        }else{
            final ApiHomeHolder.ApiRecentVitals apiRecentVitals = models.get(position);
            String title = "";
            String unit = "";
            if (getStoredLanguage().equals(LANGUAGE_ARABIC)){
                if (apiRecentVitals.getVitalNameNls() == null)
                    title = apiRecentVitals.getName();
                else
                    title = apiRecentVitals.getVitalNameNls();

                if (apiRecentVitals.getUnitNls() == null)
                    unit = apiRecentVitals.getUnit();
                else
                    unit = apiRecentVitals.getUnitNls();

            }else{
                title = apiRecentVitals.getName();
                if (apiRecentVitals.getUnit().equals("degrees C")){
                    unit = "\u2103";
                }else {
                    unit = apiRecentVitals.getUnit();
                }

            }

            holder.title.setText(title);
            holder.value.setText(models.get(position).getValue());
            holder.sign.setText(unit);
        }



    }

    @Override
    public int getItemCount() {
        if (isDefault){
            return dummyVitalSigns.size();
        }else {
            return models.size();
        }

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, value, sign;
        CardView cardView;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_myvital_title);
            value = itemView.findViewById(R.id.tv_myvital_value);
            sign = itemView.findViewById(R.id.tv_myvital_sign);
            cardView = itemView.findViewById(R.id.recentVitalCard);


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