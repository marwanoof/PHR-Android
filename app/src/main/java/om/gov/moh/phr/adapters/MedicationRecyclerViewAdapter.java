package om.gov.moh.phr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiMedicationHolder;

public class MedicationRecyclerViewAdapter extends RecyclerView.Adapter<MedicationRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<ApiMedicationHolder.ApiMedicationInfo> medicineArrayList;
    private Context context;
    private ArrayList<ApiMedicationHolder.ApiMedicationInfo> arraylist;
    private String visitDate = "";

    public MedicationRecyclerViewAdapter(ArrayList<ApiMedicationHolder.ApiMedicationInfo> medicineArrayList, Context context) {
        this.medicineArrayList = medicineArrayList;
        this.context = context;
        this.arraylist = new ArrayList<ApiMedicationHolder.ApiMedicationInfo>();
        this.arraylist.addAll(medicineArrayList);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_medication, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ApiMedicationHolder.ApiMedicationInfo medicineObj = medicineArrayList.get(position);
        holder.tvMedicineName.setText(medicineObj.getMedicineName());
        holder.tvDosage.setText(medicineObj.getDosage());

        SimpleDateFormat dateFormatGroupedDate = new SimpleDateFormat("dd-MM-YYYY", Locale.ENGLISH);
        if (position != 0) {
            int prev = position - 1;
            long prevEncounterDate = medicineArrayList.get(prev).getDateWrittern();
            if (dateFormatGroupedDate.format(new Date(medicineObj.getDateWrittern())).equals(dateFormatGroupedDate.format(new Date(prevEncounterDate)))) {
                holder.tvDateWritten.setVisibility(View.GONE);
            } else {
                holder.tvDateWritten.setVisibility(View.VISIBLE);
                holder.tvDateWritten.setText(context.getResources().getString(R.string.date_label)+ " " + dateFormatGroupedDate.format(new Date(medicineObj.getDateWrittern())));
            }
        } else {
            holder.tvDateWritten.setVisibility(View.VISIBLE);
            holder.tvDateWritten.setText(context.getResources().getString(R.string.date_label) + " " +dateFormatGroupedDate.format(new Date(medicineObj.getDateWrittern())));
        }
        holder.tvEstName.setText(medicineObj.getEstName());

    }

    @Override
    public int getItemCount() {
        return medicineArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvMedicineName, tvDosage, tvDateWritten, tvEstName;
        ImageButton ibImageButton;

        public MyViewHolder(View view) {
            super(view);
            tvMedicineName = view.findViewById(R.id.tv_dose_title);
            tvDosage = view.findViewById(R.id.tv_dose_desc);
            tvDateWritten = view.findViewById(R.id.tv_section_title);
            tvEstName = view.findViewById(R.id.tv_est_name);
            ibImageButton = view.findViewById(R.id.btn_medication_notify);
            //ibImageButton.setVisibility(View.GONE);
        }
    }

    // Filter Class
    public void filter(String charText) {
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
    }
}
