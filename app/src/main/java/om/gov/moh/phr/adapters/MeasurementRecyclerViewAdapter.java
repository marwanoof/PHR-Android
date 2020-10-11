package om.gov.moh.phr.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiDemographicsHolder;
import om.gov.moh.phr.interfaces.AdapterToFragmentConnectorInterface;

import static om.gov.moh.phr.models.MyConstants.LANGUAGE_ARABIC;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_PREFS;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_SELECTED;

public class MeasurementRecyclerViewAdapter extends
        RecyclerView.Adapter<MeasurementRecyclerViewAdapter.MyViewHolder> {

    private ArrayList<ApiDemographicsHolder.ApiDemographicItem.RecentVitals> mItemsArrayList;
    private Context mContext;
    private AdapterToFragmentConnectorInterface mCallback;
    private int mColorIndex;

    //the constructor of the LastRecordsRecyclerViewAdapter
    public MeasurementRecyclerViewAdapter(AdapterToFragmentConnectorInterface fragment, Context context, ArrayList<ApiDemographicsHolder.ApiDemographicItem.RecentVitals> items) {
        this.mItemsArrayList = items;
        this.mContext = context;
        mCallback = fragment;
    }

    //onCreateViewHolder allows you to inflate the "List items view"
//also allows to make some action during once the list created
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_body_measurements_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    //onBindViewHolder , allows you to write the data into the fields
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final ApiDemographicsHolder.ApiDemographicItem.RecentVitals result = mItemsArrayList.get(position);

        //in order to get color correctly add 0xff at the beginning
        holder.vRoundRect.setBackground(new DrawableGradient(getColorsArray(position)));

        if (getStoredLanguage().equals(LANGUAGE_ARABIC)) {
            holder.tvTitle.setText(result.getVitalNameNls());
            holder.tvValue.setText(result.getValue());
            holder.tvUnit.setText(result.getUnitNls());
        } else {
            holder.tvTitle.setText(result.getName());
            holder.tvValue.setText(result.getValue());
            holder.tvUnit.setText(result.getUnit());
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onMyListItemClicked(result, result.getName(), position);
            }
        });

        switch (result.getName()){
            case "Heart rate":
                holder.imgIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_v_heart));
                break;
            case "Oxygen saturation":
                holder.imgIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_v_oxygen));
                break;
            case "Body height":
                holder.imgIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_v_height));
                break;
            case "Body temperature":
                holder.imgIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_v_temp));
                holder.tvUnit.setText("\u2103");
                break;
            case "Respiratory rate":
                holder.imgIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_v_respiratory));
                break;
            case "Diastolic blood pressure":
                holder.imgIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_v_blood_low));
                break;
            case "Systolic blood pressure":
                holder.imgIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_v_blood_up));
                break;
            case "Weight Measured":
                holder.imgIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_v_weight));
                break;
        }
    }


    @Override
    public int getItemCount() {
        return mItemsArrayList.size();
    }


    //the requirements is to have display each item with a specific color, if items are more than the colors repeat the colors\
    // item color is gradient from left to right.

    private int[] getColorsArray(int i) {
/**
 Body measurement
 Gradient direction - horizontal
 Hexadecimal Codes
 Height  -      #fe9a3a    #ffb953
 weight  -      #ff5581    #ff6d97
 BMI       -    #f749b8    #f767e3
 BP         -   #30cfb9    #37e6af
 RR         -   #52a9ff    #52c7ff
 Temp    -      #6054ff    #536bff
 */
        int[] leftColors = {0xff30cfb9,
                0xff52a9ff,
                0xffff5581,
                0xfffe9a3a,
                0xfff749b8,
                0xffaf39ee,
                0xff6054ff};
        int[] rightColors = {0xff37e6af,
                0xff52c7ff,
                0xffff6d97,
                0xffffb953,
                0xfff767e3,
                0xffdcd3ea,
                0xff536bff};

        if (mColorIndex > leftColors.length - 1) {
            mColorIndex = 0;
        }

        int[] result = {leftColors[mColorIndex], rightColors[mColorIndex]};
        mColorIndex++;

        return result;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final CardView vRoundRect;
        private final TextView tvTitle;
        private final TextView tvValue;
        private final TextView tvUnit;
        private final ImageView imgIcon;

        public MyViewHolder(View view) {
            super(view);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvValue = itemView.findViewById(R.id.tv_value);
            tvUnit = itemView.findViewById(R.id.tv_unit);
            vRoundRect = itemView.findViewById(R.id.bodyM_cardView);
            imgIcon = itemView.findViewById(R.id.img_vitel);
        }
    }

    public class DrawableGradient extends GradientDrawable {
        private static final float CORNER_RADIUS = 24;

        DrawableGradient(int[] colors) {
            super(Orientation.LEFT_RIGHT, colors);
            try {
                this.setShape(GradientDrawable.RECTANGLE);
                this.setGradientType(GradientDrawable.LINEAR_GRADIENT);
                this.setCornerRadius(CORNER_RADIUS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private String getStoredLanguage() {
        SharedPreferences sharedPref = mContext.getSharedPreferences(LANGUAGE_PREFS, Context.MODE_PRIVATE);
        return sharedPref.getString(LANGUAGE_SELECTED, LANGUAGE_ARABIC);
    }
}