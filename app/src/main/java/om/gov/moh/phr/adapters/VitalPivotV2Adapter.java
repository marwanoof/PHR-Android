package om.gov.moh.phr.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiVitalPivotV2;
import om.gov.moh.phr.interfaces.AdapterToFragmentConnectorInterface;

import static om.gov.moh.phr.models.MyConstants.LANGUAGE_ARABIC;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_PREFS;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_SELECTED;

public class VitalPivotV2Adapter extends RecyclerView.Adapter<VitalPivotV2Adapter.MyViewHolder> {

    private ArrayList<ApiVitalPivotV2.VitalRecord> mItemsArrayList;



    private Context mContext;
    private AdapterToFragmentConnectorInterface mCallback;
    private int mColorIndex;


    //the constructor of the LastRecordsRecyclerViewAdapter
    public VitalPivotV2Adapter( Context context, ArrayList<ApiVitalPivotV2.VitalRecord> items) {
        this.mItemsArrayList = items;
        this.mContext = context;


    }

    //onCreateViewHolder allows you to inflate the "List items view"
//also allows to make some action during once the list created
    @Override
    public VitalPivotV2Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_vital_v2, parent, false);
        return new VitalPivotV2Adapter.MyViewHolder(itemView);
    }

    //onBindViewHolder , allows you to write the data into the fields
    @Override
    public void onBindViewHolder(@NonNull final VitalPivotV2Adapter.MyViewHolder holder, final int position) {
        final ApiVitalPivotV2.VitalRecord result = mItemsArrayList.get(position);

        holder.tvDate.setText(result.getVitalDate());

        holder.tvValue.setText(result.getValue());
        String vitalUnit = result.getUnit();
        if (getStoredLanguage().equals(LANGUAGE_ARABIC)) {

                holder.tvUnit.setText(result.getUnitNls());


        } else {
            if (vitalUnit.equals("degrees C")){
                holder.tvUnit.setText("\u2103");
            }else {
                holder.tvUnit.setText(result.getUnit());
            }
        }
    }


    @Override
    public int getItemCount() {
        return mItemsArrayList.size();
    }


    //the requirements is to have display each item with a specific color, if items are more than the colors repeat the colors\
    // item color is gradient from left to right.




    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvDate;
        private final TextView tvValue;
        private final TextView tvUnit;


        public MyViewHolder(View view) {
            super(view);
            tvDate = itemView.findViewById(R.id.tv_date_vital_v2);
            tvValue = itemView.findViewById(R.id.tv_value_vital_v2);
            tvUnit = itemView.findViewById(R.id.tv_unit_vital_v2);

        }
    }

    private String getStoredLanguage() {
        SharedPreferences sharedPref = mContext.getSharedPreferences(LANGUAGE_PREFS, Context.MODE_PRIVATE);
        return sharedPref.getString(LANGUAGE_SELECTED, getDeviceLanguage());
    }
    private String getDeviceLanguage() {
        return Locale.getDefault().getLanguage();
    }
}