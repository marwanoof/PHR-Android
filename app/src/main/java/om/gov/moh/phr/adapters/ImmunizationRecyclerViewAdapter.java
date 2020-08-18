package om.gov.moh.phr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
            if(medicineObj.getGivenOn()!=null) {
                Date GivenDate = new Date(medicineObj.getGivenOn());
                String GivenDateText = df2.format(GivenDate);
                holder.tvStatus.setText(context.getResources().getString(R.string.given_msg) + GivenDateText);
            }else {
                holder.tvStatus.setText(context.getResources().getString(R.string.not_given_msg));
            }

            if (medicineObj.getGivenOn()!= null)
                holder.ivGivenImage.setVisibility(View.VISIBLE);
            else
                holder.ivGivenImage.setVisibility(View.INVISIBLE);
            Date ScheduledDate = new Date(medicineObj.getScheduledOn());
            String ScheduledDateText = df2.format(ScheduledDate);
            holder.tvDateWritten.setText(ScheduledDateText);
        } else {
            holder.tvStatus.setText(medicineObj.getStatus());
            Date date = new Date(medicineObj.getImmunizationDate());
            SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
            String dateText = df2.format(date);
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

        public MyViewHolder(View view) {
            super(view);
            tvVaccineName = view.findViewById(R.id.tv_vaccineName);
            tvStatus = view.findViewById(R.id.tv_estName);
            tvDateWritten = view.findViewById(R.id.tv_ScheduledDate);
            ivGivenImage = view.findViewById(R.id.iv_MidGiven);
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
