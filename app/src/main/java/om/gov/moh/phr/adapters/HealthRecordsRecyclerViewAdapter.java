package om.gov.moh.phr.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.Locale;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiEncountersHolder;
import om.gov.moh.phr.apimodels.ApiLabOrdersListHolder;
import om.gov.moh.phr.apimodels.ApiMedicationHolder;
import om.gov.moh.phr.fragments.HealthRecordDetailsFragment;
import om.gov.moh.phr.fragments.HealthRecordListFragment;
import om.gov.moh.phr.interfaces.AdapterToFragmentConnectorInterface;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.models.GlobalMethods;

import static om.gov.moh.phr.models.MyConstants.LANGUAGE_ARABIC;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_ENGLISH;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_PREFS;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_SELECTED;

public class HealthRecordsRecyclerViewAdapter extends ListAdapter<ApiEncountersHolder.Encounter, HealthRecordsRecyclerViewAdapter.MyViewHolder> {
    private static final DiffUtil.ItemCallback<ApiEncountersHolder.Encounter> DIFF_CALLBACK = new DiffUtil.ItemCallback<ApiEncountersHolder.Encounter>() {
        @Override
        public boolean areItemsTheSame(@NonNull ApiEncountersHolder.Encounter oldItem, @NonNull ApiEncountersHolder.Encounter newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull ApiEncountersHolder.Encounter oldItem, @NonNull ApiEncountersHolder.Encounter newItem) {
            return oldItem.equals(newItem);
        }
    };
    private AdapterToFragmentConnectorInterface mListener;
    private Context mContxt;
    private AdapterToFragmentConnectorInterface mCallback;
    private MediatorInterface mediatorInterface;
    public HealthRecordsRecyclerViewAdapter(HealthRecordListFragment healthRecordListFragment, Context context, MediatorInterface mMediatorCallback) {
        super(DIFF_CALLBACK);
        this.mContxt = context;
        this.mediatorInterface = mMediatorCallback;
        mCallback = healthRecordListFragment;
    }


    //onCreateViewHolder allows you to inflate the "List items view"
//also allows to make some action during once the list created
    @Override
    public HealthRecordsRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.health_record_item, parent, false);
        return new HealthRecordsRecyclerViewAdapter.MyViewHolder(itemView);
    }

    //onBindViewHolder , allows you to write the data into the fields
    @Override
    public void onBindViewHolder(@NonNull HealthRecordsRecyclerViewAdapter.MyViewHolder holder, final int position) {
        final ApiEncountersHolder.Encounter result = getItem(position);

        String title = "";
        if (result.getDepartmentArrayList().size() == 0 || result.getDepartmentArrayList().get(0) == null) {
            title = "Not provided";
        } else {
            if (getStoredLanguage().equals(LANGUAGE_ARABIC)){
                holder.tvDay.setText(GlobalMethods.convertToEnglishNumber(result.getEncounterDay()));
                if (result.getDepartmentArrayList().get(0).getValueNls() == null){
                    title = result.getDepartmentArrayList().get(0).getValue().trim();
                }else {
                    title = result.getDepartmentArrayList().get(0).getValueNls().trim();
                }
            }else {
                title = result.getDepartmentArrayList().get(0).getValue().trim();
                holder.tvDay.setText(result.getEncounterDay());
            }

        }

        holder.tvTitle.setText(title);

        String patientClass = result.getPatientClass();
        switch (result.getPatientClass()){
            case "outpatient":
                if (getStoredLanguage().equals(LANGUAGE_ARABIC)){patientClass = "العيادات الخارجية";}
                //holder.patientClass.setImageDrawable(mContxt.getResources().getDrawable(R.drawable.ic_outpatient));
                break;
            case "emergency":
                if (getStoredLanguage().equals(LANGUAGE_ARABIC)){patientClass = "الطوارئ";}
                //holder.patientClass.setImageDrawable(mContxt.getResources().getDrawable(R.drawable.ic_emergency));
                break;
            case "inpatient":
                if (getStoredLanguage().equals(LANGUAGE_ARABIC)){patientClass = "العيادات الداخلية";}
                //holder.patientClass.setImageDrawable(mContxt.getResources().getDrawable(R.drawable.ic_inpatient));
                break;
            default:
                holder.patientClass.setImageDrawable(null);
                break;
        }




        holder.tvMonth.setText(result.getEncounterMonth());
        //holder.tvDay.setText(result.getEncounterDay());

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int recordYear = Integer.valueOf(result.getEncounterYear());

        int yearDiff = currentYear - recordYear;

        if (holder.isOdd(yearDiff)){
            holder.dateView.setBackgroundColor(holder.grayColor);
        }else {
            holder.dateView.setBackgroundColor(holder.redColor);
        }

        //Translation
        if (getStoredLanguage().equals(LANGUAGE_ENGLISH)){
            String sub2;
            if (result.getDiagnosisArrayList() == null || result.getDiagnosisArrayList().size() == 0 || result.getDiagnosisArrayList().get(0) == null) {
                sub2 = "";
            } else {
                sub2 = result.getDiagnosisArrayList().get(0).getValue().trim();
            }
            holder.tvSub2.setText(sub2);
            holder.detailsArrow.setImageDrawable(mContxt.getResources().getDrawable(R.drawable.ic_arrow_right));
            String sub1 = patientClass + " | " + result.getEstShortName();
            holder.tvSub1.setText(sub1);
        }else {
            String sub1 = patientClass + " | " + result.getEstFullnameNls();
            holder.tvSub1.setText(sub1);
            holder.detailsArrow.setImageBitmap(flipImage());
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediatorInterface.changeFragmentTo(HealthRecordDetailsFragment.newInstance(result),"HeathRecordsDetails");
                //mCallback.onMyListItemClicked(result, result.getEstShortName(), position);
            }
        });
    }

    public ApiEncountersHolder.Encounter getEncounterAt(int position) {
        return getItem(position);
    }

    public void setOnItemClickListener(AdapterToFragmentConnectorInterface listener) {
        mListener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final TextView tvSub1;
        private final TextView tvSub2;
        private final TextView tvMonth;
        private final TextView tvDay;
        private final View dateView;
        private final ImageView patientClass;
        private int grayColor = mContxt.getResources().getColor(R.color.colorSecendary);
        private int redColor = mContxt.getResources().getColor(R.color.colorPrimary);
        private final ImageView detailsArrow;

        public boolean isOdd(int value){
            return value % 2 != 0;
        }


        public MyViewHolder(View view) {
            super(view);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvSub1 = itemView.findViewById(R.id.tv_sub1);
            tvSub2 = itemView.findViewById(R.id.tv_sub2);
            tvMonth = itemView.findViewById(R.id.tv_month);
            tvDay = itemView.findViewById(R.id.tv_day);
            dateView = itemView.findViewById(R.id.v_date_holder);
            patientClass = itemView.findViewById(R.id.img_patientClassIcon);
            detailsArrow = itemView.findViewById(R.id.img_detail_arrow);

            Bitmap bitmap = BitmapFactory.decodeResource(mContxt.getResources(),R.drawable.ic_arrow_right);
            detailsArrow.setImageBitmap(bitmap);
            //detailsArrow.setImageDrawable(mContxt.getResources().getDrawable(R.drawable.ic_keyboard_arrow_next));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (mListener != null && position != RecyclerView.NO_POSITION) {
                        mListener.onMyListItemClicked(getEncounterAt(position), null);
                    }
                }
            });


        }
    }
    private String getStoredLanguage() {
        SharedPreferences sharedPref = mContxt.getSharedPreferences(LANGUAGE_PREFS, Context.MODE_PRIVATE);
        return sharedPref.getString(LANGUAGE_SELECTED, LANGUAGE_ARABIC);
    }

    private Bitmap flipImage() {
        Bitmap bitmap = BitmapFactory.decodeResource(mContxt.getResources(),R.drawable.ic_arrow_right);
// create new matrix for transformation
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);

        Bitmap flipped_bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        return flipped_bitmap;

    }
   /* // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        mListener.clear();
        if (charText.length() == 0) {
            mListener.addAll(arraylist);
        } else {
            for (ApiMedicationHolder.ApiMedicationInfo wp : arraylist) {
                if (wp.getMedicineName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    medicineArrayList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }*/
}