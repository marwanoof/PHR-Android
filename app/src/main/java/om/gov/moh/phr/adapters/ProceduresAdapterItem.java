package om.gov.moh.phr.adapters;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiMedicationHolder;
import om.gov.moh.phr.apimodels.ApiProceduresReportsHolder;
import om.gov.moh.phr.fragments.ProceduresReportsDetailsFragment;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.models.UserEmailFetcher;

import static om.gov.moh.phr.models.MyConstants.LANGUAGE_ARABIC;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_PREFS;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_SELECTED;

public class ProceduresAdapterItem extends RecyclerView.Adapter<ProceduresAdapterItem.MyViewHolder> {
    private ArrayList<ApiProceduresReportsHolder.Procedures> proceduresArrayList;
    private Context context;
    private ArrayList<ApiProceduresReportsHolder.Procedures> arraylist;
    private String visitDate = "";
    private boolean isRad;
    private MediatorInterface mediatorInterface;

    public ProceduresAdapterItem(ArrayList<ApiProceduresReportsHolder.Procedures> proceduresArrayList, Context context, boolean isRad) {
        this.proceduresArrayList = proceduresArrayList;
        this.context = context;
        this.arraylist = new ArrayList<ApiProceduresReportsHolder.Procedures>();
        this.arraylist.addAll(proceduresArrayList);
        this.isRad = isRad;
        mediatorInterface = (MediatorInterface) context;
    }

    @NonNull
    @Override
    public ProceduresAdapterItem.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_procedures, parent, false);
        return new ProceduresAdapterItem.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProceduresAdapterItem.MyViewHolder holder, int position) {
        final ApiProceduresReportsHolder.Procedures proceduresEncounter = proceduresArrayList.get(position);

            holder.moreDetailArrow.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams = holder.cardView.getLayoutParams();
            layoutParams.height = 150;
            //holder.cardView.setLayoutParams(layoutParams);
            //final ApiProceduresReportsHolder procedureObj = proceduresReportArrayList.get(position);
            if (isRad) {
                holder.tvProcedureName.setText(proceduresEncounter.getProcedure().get(0).getName());
                Date date = new Date(proceduresEncounter.getProcedureDoneDate());
                SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH);
                String dateText = df2.format(date);
                holder.tvDateWritten.setText(dateText);


                holder.tvDate.setVisibility(View.GONE);

                if (getStoredLanguage().equals(LANGUAGE_ARABIC)){
                    holder.tvEstName.setText(proceduresEncounter.getEstFullnameNls());
                    holder.moreDetailArrow.setImageBitmap(flipImage());
                }else {
                    holder.tvEstName.setText(proceduresEncounter.getEstFullname());
                    holder.moreDetailArrow.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_right));
                }


            } else {

                holder.tvProcedureName.setText(proceduresEncounter.getProcedure().get(0).getName());

                Date date = new Date(proceduresEncounter.getStartTime());
                SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH);
                String dateText = df2.format(date);
                holder.tvDateWritten.setText(dateText);

                if (getStoredLanguage().equals(LANGUAGE_ARABIC)){
                    holder.tvEstName.setText(proceduresEncounter.getEstFullnameNls());
                    holder.moreDetailArrow.setImageBitmap(flipImage());
                }else {
                    holder.tvEstName.setText(proceduresEncounter.getEstFullname());
                    holder.moreDetailArrow.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_right));
                }

            }
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  //  row_index = position;
                   // notifyDataSetChanged();

                        mediatorInterface.changeFragmentTo(ProceduresReportsDetailsFragment.newInstance(proceduresEncounter), proceduresEncounter.getProcedure().get(0).getName());
                }
            });
//            if (row_index == position) {
//
//                holder.clOrderItem.setBackgroundColor(context.getResources().getColor(R.color.colorPeach));
//            } else {
//
//                holder.clOrderItem.setBackgroundColor(context.getResources().getColor(R.color.colorWhite));
//            }





    }

    @Override
    public int getItemCount() {
        return proceduresArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvProcedureName, tvDosage, tvDateWritten, tvEstName, tvContentDetails, tvDateDetails, tvDate;
        ConstraintLayout clOrderItem;
        ImageView moreDetailArrow;
        CardView cardView;


        public MyViewHolder(View view) {
            super(view);
            tvProcedureName = view.findViewById(R.id.tv_name_proc);
            tvDateWritten = view.findViewById(R.id.tv_date_proc);
            tvEstName = view.findViewById(R.id.tv_hospital_proc);
            moreDetailArrow = view.findViewById(R.id.img_arrowDetails);
            cardView = view.findViewById(R.id.cardView_procContent);
            tvContentDetails = view.findViewById(R.id.tv_content_proc_details);
            tvDateDetails = view.findViewById(R.id.tv_date_proc_details);

            tvDate = view.findViewById(R.id.tvDate);
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_arrow_right);
            moreDetailArrow.setImageBitmap(bitmap);
        }
    }

    private String getStoredLanguage() {
        SharedPreferences sharedPref = context.getSharedPreferences(LANGUAGE_PREFS, Context.MODE_PRIVATE);
        return sharedPref.getString(LANGUAGE_SELECTED, LANGUAGE_ARABIC);
    }


    private Bitmap flipImage() {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_arrow_right);
// create new matrix for transformation
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);

        Bitmap flipped_bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        return flipped_bitmap;

    }

 /*   private String getCalendarUriBase(MedicineItemAdapter act) {

        String calendarUriBase = null;
        Uri calendars = Uri.parse("content://calendar/calendars");
        Cursor managedCursor = null;
        try {
            managedCursor = context.getApplicationContext().managedQuery(calendars, null, null, null, null);
        } catch (Exception e) {
        }
        if (managedCursor != null) {
            calendarUriBase = "content://calendar/";
        } else {
            calendars = Uri.parse("content://com.android.calendar/calendars");
            try {
                managedCursor = act.managedQuery(calendars, null, null, null, null);
            } catch (Exception e) {
            }
            if (managedCursor != null) {
                calendarUriBase = "content://com.android.calendar/";
            }
        }
        return calendarUriBase;
    }*/
    // Filter Class
    /*public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        medicineArrayList.clear();
        if (charText.length() == 0) {
            medicineArrayList.addAll(arraylist);
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