package om.gov.moh.phr.adapters;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.CalendarContract;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiMedicationHolder;
import om.gov.moh.phr.models.UserEmailFetcher;

import static om.gov.moh.phr.models.MyConstants.LANGUAGE_ARABIC;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_PREFS;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_SELECTED;

public class MedicineItemAdapter extends RecyclerView.Adapter<MedicineItemAdapter.MyViewHolder> {
    private ArrayList<ApiMedicationHolder.Medication> medicineArrayList;
    private Context context;
    private ArrayList<ApiMedicationHolder.Medication> arraylist;
    private String visitDate = "";


    public MedicineItemAdapter(ArrayList<ApiMedicationHolder.Medication> medicineArrayList, Context context) {
        this.medicineArrayList = medicineArrayList;
        this.context = context;
        this.arraylist = new ArrayList<ApiMedicationHolder.Medication>();
        this.arraylist.addAll(medicineArrayList);
    }

    @NonNull
    @Override
    public MedicineItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_medication, parent, false);
        return new MedicineItemAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MedicineItemAdapter.MyViewHolder holder, int position) {
        final ApiMedicationHolder.Medication medicineEncounter = medicineArrayList.get(position);

        final String userEmail = UserEmailFetcher.getEmail(context);
        boolean reminderIsSet = false;
        holder.medicineName.setText(medicineEncounter.getMedicineName());
        int timingDur = medicineEncounter.getTimingDuration();
        holder.progressBar.setMax(timingDur);
        Date startDateM = medicineEncounter.getStartDate();
        final Date currentDate = new Date();
        long daysDiff = getDateDiff(startDateM,currentDate,TimeUnit.DAYS);

        if (daysDiff >= timingDur){
            holder.progressBar.setProgress(medicineEncounter.getTimingDuration());
            holder.setCompletedStyle();
        }else {
            holder.progressBar.setProgress((int) daysDiff);
            holder.daysLeft.setText(setDaysLeft(timingDur-daysDiff));
        }

        //Arabic Translation
        String medicineAction = context.getResources().getString(R.string.take_medicine);
        String dosageUnit = "";
        String dosageDesc = "";

        if (getStoredLanguage().equals(LANGUAGE_ARABIC)){
            holder.dose.setText(medicineEncounter.getDosageNls());
            dosageUnit = medicineEncounter.getUnitNameNls();
            dosageDesc = medicineEncounter.getDoseDescNls();
        }else {
            holder.dose.setText(medicineEncounter.getDosage());
            dosageUnit = medicineEncounter.getDoseValueUnitName();
            dosageDesc = medicineEncounter.getDoseDesc();
        }
        final String eventTitle = medicineAction + " " + medicineEncounter.getDoseQty() + " " + dosageUnit + " " + dosageDesc;
        final int dosageDays = (int) (timingDur-daysDiff);

        holder.reminderBtn.setTag(false);
        if (readeCalender(eventTitle,medicineEncounter.getMedicationOrderId())){
            holder.reminderBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_reminder_true));
            holder.reminderBtn.setTag(true);
        }

        holder.reminderBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                 if (holder.reminderBtn.getTag().equals(true)){
                     deleteCalendar(medicineEncounter.getMedicationOrderId());
                     holder.reminderBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_reminder));
                     holder.reminderBtn.setTag(false);
                 }else {
                     SimpleDateFormat yyyymmdd = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
                     Calendar dt = Calendar.getInstance();
                     dt.setTime(new Date());
                     dt.add(Calendar.DAY_OF_MONTH, dosageDays);
                     String dtUntill = yyyymmdd.format(dt.getTime());
                     Date endDate = dt.getTime();

                     insertIntoCalender(eventTitle,medicineEncounter.getMedicationOrderId(),dtUntill,currentDate,endDate);
                     //holder.reminderBtn.setTag(true);
                 }


                //readeCalender();
            }
        });





    }

    @Override
    public int getItemCount() {
        return medicineArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView medicineName,dose,hospital,daysLeft;
        ProgressBar progressBar;
        ImageButton reminderBtn;



        public MyViewHolder(View view) {
            super(view);
            medicineName = view.findViewById(R.id.tv_dose_title);
            dose = view.findViewById(R.id.tv_dose_desc);
            hospital = view.findViewById(R.id.tv_est_name);
            daysLeft = view.findViewById(R.id.tv_days_left);
            progressBar = view.findViewById(R.id.progressBar);
            reminderBtn = view.findViewById(R.id.btn_medication_notify);

        }
        private void setCompletedStyle(){
            daysLeft.setText(context.getResources().getString(R.string.completed));
            daysLeft.setTextColor(context.getResources().getColor(R.color.colorGreen));
            reminderBtn.setEnabled(false);
            reminderBtn.setAlpha((float) 0.3);

            progressBar.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
            progressBar.setAlpha((float) 0.3);
            dose.setAlpha((float) 0.3);
            medicineName.setAlpha((float) 0.4);
            hospital.setAlpha((float) 0.3);
        }
    }
    public static long getDateDiff(Date start, Date current, TimeUnit timeUnit) {
        long diffInMillies = current.getTime() - start.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }
    private String setDaysLeft(long days){
        String result = "";
        int daysInt = (int) days;
        if (getStoredLanguage().equals(LANGUAGE_ARABIC)){
            if (daysInt == 1){
                result = "يوم واحد متبقي";
            }else if (daysInt == 2){
                result = "يومان متبقيان";
            }else if (daysInt >= 3 && daysInt <= 10){
                result = daysInt+" أيام متبقية";
            }else {
                result = daysInt+" يوم متبقي";
            }

        }else {
            if (daysInt == 1){
                result = daysInt+" day left";
            }else {
                result = daysInt+" days left";
            }
        }

        return result;
    }

    private String getStoredLanguage() {
        SharedPreferences sharedPref = context.getSharedPreferences(LANGUAGE_PREFS, Context.MODE_PRIVATE);
        return sharedPref.getString(LANGUAGE_SELECTED, getDeviceLanguage());
    }
    private String getDeviceLanguage() {
        return Locale.getDefault().getLanguage();
    }

    private void insertIntoCalender(String title, String desc, String untilDate, Date currentDate, Date endDate){
        Intent insertCalendarIntent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.DTSTART,currentDate)
                // .putExtra(CalendarContract.Events.DTEND,dt)
                .putExtra(CalendarContract.Events.TITLE, title) // Simple title
                .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true)
                .putExtra(CalendarContract.Events.CALENDAR_ID, 1)
                //.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,endDate.getTime()) // Only date part is considered when ALL_DAY is true; Same as DTSTART
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME,endDate) // Only date part is considered when ALL_DAY is true

                .putExtra(CalendarContract.Events.DESCRIPTION, desc) // Description
                //.putExtra(CalendarContract.Events.EVENT_LOCATION,"@23.6061459,58.4076075")
                .putExtra(CalendarContract.Events.RRULE, "FREQ=DAILY;UNTIL=" + untilDate) // Recurrence rule
                .putExtra(CalendarContract.Events.DURATION, "+P1H")
                .putExtra(CalendarContract.Events.HAS_ALARM, 1)
                .putExtra(CalendarContract.Events.CUSTOM_APP_PACKAGE, "om.gov.moh.phr")
                .putExtra(CalendarContract.Events.DISPLAY_COLOR,context.getResources().getColor(R.color.colorPrimary))
                .putExtra(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
                .putExtra(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE)
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_FREE);

        context.startActivity(insertCalendarIntent);

    }
    private Boolean readeCalender(String title, String medId){
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(CalendarContract.Events.CONTENT_URI, new String[]{ "calendar_id", "title", "description", "dtstart", "dtend", "eventLocation" }, null, null, null);
        //Cursor cursor = cr.query(Uri.parse("content://calendar/calendars"), new String[]{ "_id", "name" }, null, null, null);
        String add = null;
        cursor.moveToFirst();
        String[] CalNames = new String[cursor.getCount()];
        int[] CalIds = new int[cursor.getCount()];
        for (int i = 0; i < CalNames.length; i++) {
            CalIds[i] = cursor.getInt(0);
            if (cursor.getString(2).equals(medId)){
                if (cursor.getString(1).equals(title)){
                    return true;
                }
               // CalNames[i] = "Event"+cursor.getInt(0)+": \nTitle: "+ cursor.getString(1)+"\nDescription: "+cursor.getString(2)+"\nStart Date: "+new Date(cursor.getLong(3))+"\nEnd Date : "+new Date(cursor.getLong(4))+"\nLocation : "+cursor.getString(5);

            }
           /* if(add == null)
                add = CalNames[i];
            else{
                add += CalNames[i];
            }
            System.out.println(add);*/
            //((TextView)findViewById(R.id.calendars)).setText(add);

            cursor.moveToNext();
        }
        cursor.close();
        return false;
    }
    private void deleteCalendar(String desc) {
        ContentResolver resolver = context.getContentResolver();
        //Cursor cursor = resolver.query(CalendarContract.Events.CONTENT_URI, new String[]{"description"}, "description='" + desc+"'", null, null);
        resolver.delete(CalendarContract.Events.CONTENT_URI,"description=?",new String[]{desc});
        /*while (cursor.moveToNext()) {
            long eventId = cursor.getLong(cursor.getColumnIndex("calendar_id"));

            resolver.delete(ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId), null, null);
        }
        cursor.close();*/
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