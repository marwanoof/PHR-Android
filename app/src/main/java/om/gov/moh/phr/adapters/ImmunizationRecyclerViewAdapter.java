package om.gov.moh.phr.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiImmunizationHolder;
import om.gov.moh.phr.apimodels.ApiScheduleImmunizationHolder;
import om.gov.moh.phr.models.UserEmailFetcher;

import static om.gov.moh.phr.models.MyConstants.LANGUAGE_ARABIC;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_PREFS;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_SELECTED;


public class ImmunizationRecyclerViewAdapter extends RecyclerView.Adapter<ImmunizationRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<ApiImmunizationHolder.ApiImmunizationInfo> immunizationArrayList;
    private ArrayList<ApiScheduleImmunizationHolder.ApiScheduleImmunizationInfo> immunizationScheduleArrayList;
    private Context context;
    private ArrayList<ApiImmunizationHolder.ApiImmunizationInfo> arraylist;
    private ArrayList<ApiScheduleImmunizationHolder.ApiScheduleImmunizationInfo> ScheduleArraylist;
    private boolean isSchedule;

    public ImmunizationRecyclerViewAdapter(ArrayList<ApiImmunizationHolder.ApiImmunizationInfo> immunizationArrayList, Context context, boolean isSchedule) {
        this.immunizationArrayList = immunizationArrayList;
        this.context = context;
        this.arraylist = new ArrayList<ApiImmunizationHolder.ApiImmunizationInfo>();
        this.arraylist.addAll(immunizationArrayList);
        this.isSchedule = isSchedule;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.immunization_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ApiImmunizationHolder.ApiImmunizationInfo medicineObj = immunizationArrayList.get(position);
        holder.tvVaccineName.setText(medicineObj.getVaccineName());
        if (isSchedule) {
            SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
            if (medicineObj.getGivenOn() != null) {
                Date GivenDate = new Date(medicineObj.getGivenOn());
                String GivenDateText = df2.format(GivenDate);
                holder.tvStatus.setText(context.getResources().getString(R.string.given_msg) + GivenDateText);

                holder.ivGivenImage.setVisibility(View.VISIBLE);
            } else {
                holder.tvStatus.setText(context.getResources().getString(R.string.not_given_msg));
                holder.ivGivenImage.setVisibility(View.INVISIBLE);
            }

            if ((medicineObj.getScheduledOn() != null && medicineObj.getGivenOn() == null)) {
                //setup event reminder
                final String title = context.getResources().getString(R.string.time_to_immunization) + " (" + medicineObj.getVaccineName() + ")";
                final String location = context.getResources().getString(R.string.location);
                final String userEmail = UserEmailFetcher.getEmail(context);
                final long eventDate = medicineObj.getScheduledOn();
                final String description = context.getResources().getString(R.string.go_for_vaccination);
                holder.scheduleBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent insertCalendarIntent = new Intent(Intent.ACTION_INSERT)
                                .setData(CalendarContract.Events.CONTENT_URI)
                                .putExtra(CalendarContract.Events.TITLE, title) // Simple title
                                .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true)
                                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, eventDate) // Only date part is considered when ALL_DAY is true; Same as DTSTART
                                //.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,endTimeInMillis) // Only date part is considered when ALL_DAY is true
                                .putExtra(CalendarContract.Events.EVENT_LOCATION, location)
                                .putExtra(CalendarContract.Events.DESCRIPTION, description) // Description
                                .putExtra(Intent.EXTRA_EMAIL, userEmail)
                                // .putExtra(CalendarContract.Events.RRULE, getRRule()) // Recurrence rule
                                .putExtra(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE)
                                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_FREE);
                        context.startActivity(insertCalendarIntent);

                    }
                });
            }
            if (medicineObj.getScheduledOn() != null) {
                Date ScheduledDate = new Date(medicineObj.getScheduledOn());
                String ScheduledDateText = df2.format(ScheduledDate);
                holder.tvDateWritten.setText(ScheduledDateText);
            }
        } else {
            holder.scheduleBtn.setVisibility(View.GONE);
            holder.tvStatus.setText(medicineObj.getStatus());
            Date date = new Date(medicineObj.getImmunizationDate());
            SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            final String dateText = df2.format(date);
            holder.tvDateWritten.setText(dateText);
        }
    }

    @Override
    public int getItemCount() {
        return immunizationArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvVaccineName, tvStatus, tvDateWritten;
        ImageView ivGivenImage;
        ImageButton scheduleBtn;

        public MyViewHolder(View view) {
            super(view);
            tvVaccineName = view.findViewById(R.id.tv_vaccineName);
            tvStatus = view.findViewById(R.id.tv_estName);
            tvDateWritten = view.findViewById(R.id.tv_ScheduledDate);
            ivGivenImage = view.findViewById(R.id.iv_MidGiven);
            scheduleBtn = view.findViewById(R.id.btn_schedual_imm);
        }
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        immunizationArrayList.clear();
        if (charText.length() == 0) {
            immunizationArrayList.addAll(arraylist);
        } else {
            for (ApiImmunizationHolder.ApiImmunizationInfo wp : arraylist) {
                if (wp.getVaccineName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    immunizationArrayList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}
