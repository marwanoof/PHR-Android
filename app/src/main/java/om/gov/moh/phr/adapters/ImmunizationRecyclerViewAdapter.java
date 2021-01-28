package om.gov.moh.phr.adapters;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EventListener;
import java.util.Locale;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiImmunizationHolder;
import om.gov.moh.phr.models.UserEmailFetcher;

import static om.gov.moh.phr.models.MyConstants.LANGUAGE_ARABIC;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_ENGLISH;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_PREFS;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_SELECTED;


public class ImmunizationRecyclerViewAdapter extends RecyclerView.Adapter<ImmunizationRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<ApiImmunizationHolder.ApiImmunizationInfo> immunizationArrayList;
    private Context context;
    private ArrayList<ApiImmunizationHolder.ApiImmunizationInfo> arraylist;
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
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final ApiImmunizationHolder.ApiImmunizationInfo medicineObj = immunizationArrayList.get(position);
        holder.tvVaccineName.setText(medicineObj.getVaccineName());
        if (getStoredLanguage().equals(LANGUAGE_ENGLISH))
            holder.tvStatus.setText(medicineObj.getEstFullName());
        else
            holder.tvStatus.setText(medicineObj.getEstFullNameNls());
        if (isSchedule) {
            SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");

            if ((medicineObj.getScheduledOn() != null && medicineObj.getGivenOn() == null)) {
                //setup event reminder
                final String title = /*context.getResources().getString(R.string.time_to_immunization) +*/ " (" + medicineObj.getVaccineName() + ")";
                final String location;
                if (getStoredLanguage().equals(LANGUAGE_ENGLISH))
                    location = medicineObj.getEstFullName();
                else
                    location = medicineObj.getEstFullNameNls();
                final String userEmail = UserEmailFetcher.getEmail(context);
                final long eventDate = medicineObj.getScheduledOn();
                final String description = context.getResources().getString(R.string.go_for_vaccination);
                holder.scheduleBtn.setTag(false);
                if (readeCalender(medicineObj.getVaccineName())) {
                    holder.scheduleBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_reminder_true));
                    // holder.scheduleBtn.setBackgroundTintList(null);
                    holder.scheduleBtn.setTag(true);
                }

                holder.scheduleBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (holder.scheduleBtn.getTag().equals(true)) {
                            deleteCalendar(title.trim());
                            holder.scheduleBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_reminder));
                            holder.scheduleBtn.setTag(false);
                        } else {
                            Intent insertCalendarIntent = new Intent(Intent.ACTION_INSERT)
                                    .setData(CalendarContract.Events.CONTENT_URI)
                                    .putExtra(CalendarContract.Events.CALENDAR_ID, position)
                                    .putExtra(CalendarContract.Events.TITLE, title.trim()) // Simple title
                                    .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true)
                                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, eventDate) // Only date part is considered when ALL_DAY is true; Same as DTSTART
                                    //.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,endTimeInMillis) // Only date part is considered when ALL_DAY is true
                                    .putExtra(CalendarContract.Events.EVENT_LOCATION, location)
                                    .putExtra(CalendarContract.Events.DESCRIPTION, description.trim()) // Description
                                    // .putExtra(CalendarContract.Events.STATUS, position+"")
                                    .putExtra(Intent.EXTRA_EMAIL, userEmail)
                                    // .putExtra(CalendarContract.Events.RRULE, getRRule()) // Recurrence rule
                                    .putExtra(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE)
                                    .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_FREE);
                            context.startActivity(insertCalendarIntent);
                        }
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
            Date date = new Date(medicineObj.getGivenOn());
            SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            final String dateText = df2.format(date);
            holder.tvDateWritten.setText(dateText);
        }

    }

    private String getStoredLanguage() {
        SharedPreferences sharedPref = context.getSharedPreferences(LANGUAGE_PREFS, Context.MODE_PRIVATE);
        return sharedPref.getString(LANGUAGE_SELECTED, getDeviceLanguage());
    }

    private String getDeviceLanguage() {
        return Locale.getDefault().getLanguage();
    }

    @Override
    public int getItemCount() {
        return immunizationArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvVaccineName, tvStatus, tvDateWritten;
        ImageButton scheduleBtn;

        public MyViewHolder(View view) {
            super(view);
            tvVaccineName = view.findViewById(R.id.tv_vaccineName);
            tvStatus = view.findViewById(R.id.tv_estName);
            tvDateWritten = view.findViewById(R.id.tv_ScheduledDate);
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

    private Boolean readeCalender(String vaccineName) {
/*        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(CalendarContract.Events.CONTENT_URI, new String[]{"calendar_id", "title", "description", "dtstart", "dtend", "eventLocation"}, null, null, null);
        cursor.moveToFirst();
        String[] CalNames = new String[cursor.getCount()];
        int[] CalIds = new int[cursor.getCount()];
        for (int i = 0; i < CalNames.length; i++) {
            CalIds[i] = cursor.getInt(0);
            if (medId != null && cursor != null) {
                if (cursor.getString(cursor.getColumnIndex("title")) != null) {
                    if (cursor.getString(cursor.getColumnIndex("title")).equals(vaccineName)) {
                        Log.d("vaccineName", cursor.getString(cursor.getColumnIndex("title")));
                        return true;
                    }
                }
            }
            cursor.moveToNext();
        }
        cursor.close();
        return false;*/
    /*    ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(CalendarContract.Events.CONTENT_URI, new String[]{"calendar_id", "title", "description", "dtstart", "dtend", "eventLocation"}, null, null, null);
        cursor.moveToFirst();
        String vName = cursor.getString(cursor.getColumnIndex("title"));
        int calendarId = cursor.getInt(cursor.getColumnIndex("calendar_id"));
        Log.d("isThere", "fromCalender: "+vName+"  "+ calendarId + " "+"From Api"+ vaccineName+"  "+ 2);
        return vName.contains(vaccineName)&&calendarId==(2);*/
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(Uri.parse("content://com.android.calendar/events"), new String[]{"calendar_id", "title", "description", "dtstart", "dtend", "eventLocation"}, null, null, null);
        //Cursor cursor = cr.query(Uri.parse("content://calendar/calendars"), new String[]{ "_id", "name" }, null, null, null);
        String add = null;
        cursor.moveToFirst();
        String[] CalNames = new String[cursor.getCount()];
        int[] CalIds = new int[cursor.getCount()];
        for (int i = 0; i < CalNames.length; i++) {
            CalIds[i] = cursor.getInt(0);
            CalNames[i] = "Event" + cursor.getInt(0) + ": \nTitle: " + cursor.getString(1) + "\nDescription: " + cursor.getString(2) + "\nStart Date: " + new Date(cursor.getLong(3)) + "\nEnd Date : " + new Date(cursor.getLong(4)) + "\nLocation : " + cursor.getString(5);
            if (add == null)
                add = CalNames[i];
            else {
                add += CalNames[i];
            }
            // ((TextView)findViewById(R.id.calendars)).setText(add);
            Log.d("addLog", add);
            cursor.moveToNext();
        }
        cursor.close();
        return false;
    }

    private void deleteCalendar(String title) {
        ContentResolver resolver = context.getContentResolver();
        resolver.delete(CalendarContract.Events.CONTENT_URI, "title=?", new String[]{title});
    }

    public void updateItemsList() {
        notifyDataSetChanged();
    }
}
