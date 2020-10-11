package om.gov.moh.phr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiOtherDocsHolder;
import om.gov.moh.phr.fragments.HealthRecordDetailsFragment;
import om.gov.moh.phr.fragments.OtherDocsDetailsFragment;
import om.gov.moh.phr.interfaces.MediatorInterface;

public class OtherDocsRecyclerViewAdapter extends RecyclerView.Adapter<OtherDocsRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<ApiOtherDocsHolder.ApiDocInfo> othersDocsArrayList;
    private Context context;
    private ArrayList<ApiOtherDocsHolder.ApiDocInfo> arraylist;
    private MediatorInterface mediatorInterface;
    // private int row_index = -1;

    public OtherDocsRecyclerViewAdapter(MediatorInterface mMediatorCallback, ArrayList<ApiOtherDocsHolder.ApiDocInfo> othersDocsList, Context context) {
        this.othersDocsArrayList = othersDocsList;
        this.context = context;
        this.arraylist = new ArrayList<ApiOtherDocsHolder.ApiDocInfo>();
        this.arraylist.addAll(othersDocsList);
        this.mediatorInterface = mMediatorCallback;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_documents, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final ApiOtherDocsHolder.ApiDocInfo docObj = othersDocsArrayList.get(position);
        holder.tvDocType.setText(docObj.getTitle());
        Date date = new Date(docObj.getIndexed());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY" + "\n" + " HH:mm:ss", Locale.ENGLISH);
        String dateText = dateFormat.format(date);
        holder.tvDateWritten.setText(dateText);
        long encounterDate = docObj.getEncounterDate();

        SimpleDateFormat dateFormatGroupedDate = new SimpleDateFormat("dd-MM-YYYY", Locale.ENGLISH);
        if (position != 0) {
            int prev = position - 1;
            long prevEncounterDate = othersDocsArrayList.get(prev).getEncounterDate();
            if (dateFormatGroupedDate.format(new Date(encounterDate)).equals(dateFormatGroupedDate.format(new Date(prevEncounterDate)))) {
                holder.tvDate.setVisibility(View.GONE);
                holder.ivMoreArrow.setVisibility(View.GONE);
            } else {
                holder.tvDate.setVisibility(View.VISIBLE);
                holder.ivMoreArrow.setVisibility(View.VISIBLE);
                holder.tvDate.setText(context.getResources().getString(R.string.visit_date) + ": " + dateFormatGroupedDate.format(new Date(encounterDate)));
            }
        } else {
            holder.tvDate.setVisibility(View.VISIBLE);
            holder.ivMoreArrow.setVisibility(View.VISIBLE);
            holder.tvDate.setText(context.getResources().getString(R.string.visit_date) + ": " + dateFormatGroupedDate.format(new Date(encounterDate)));
        }
        holder.tvDosage.setText(docObj.getEstName());
   /*   if (row_index == position) {
            // holder.imageButton.setBackgroundTintList(context.getResources().getColorStateList(R.color.colorPeach));
            holder.clOrderItem.setBackgroundColor(context.getResources().getColor(R.color.colorPeach));
        } else {
            //holder.imageButton.setBackgroundTintList(context.getResources().getColorStateList(R.color.colorWhite));
            holder.clOrderItem.setBackgroundColor(context.getResources().getColor(android.R.color.white));
        }*/
        if (docObj.getType().contains("Pregnancy visit summary"))
            holder.ivDoctype.setImageDrawable(context.getResources().getDrawable(R.drawable.pregnancy_note));
        else if (docObj.getType().contains("Medical Report"))
            holder.ivDoctype.setImageDrawable(context.getResources().getDrawable(R.drawable.medical_report));
        else if (docObj.getType().contains("Diabetology Consult Note"))
            holder.ivDoctype.setImageDrawable(context.getResources().getDrawable(R.drawable.diabetology));
        else if (docObj.getType().contains("Addendum Document"))
            holder.ivDoctype.setImageDrawable(context.getResources().getDrawable(R.drawable.addendum));
        else if (docObj.getType().contains("Admission evaluation note"))
            holder.ivDoctype.setImageDrawable(context.getResources().getDrawable(R.drawable.admission_evaluation));
        else if (docObj.getType().contains("Birth certificate"))
            holder.ivDoctype.setImageDrawable(context.getResources().getDrawable(R.drawable.birth_certificate));
        else if (docObj.getType().contains("Hospital Consultation"))
            holder.ivDoctype.setImageDrawable(context.getResources().getDrawable(R.drawable.hospital_consultation));
        else if (docObj.getType().contains("Patient Consent"))
            holder.ivDoctype.setImageDrawable(context.getResources().getDrawable(R.drawable.consent));
        else if (docObj.getType().contains("Consult note"))
            holder.ivDoctype.setImageDrawable(context.getResources().getDrawable(R.drawable.consult_note));
        else if (docObj.getType().contains("Hospital Discharge"))
            holder.ivDoctype.setImageDrawable(context.getResources().getDrawable(R.drawable.discharge));
        else if (docObj.getType().contains("Letter"))
            holder.ivDoctype.setImageDrawable(context.getResources().getDrawable(R.drawable.letter));
        else if (docObj.getType().contains("Diagnostic"))
            holder.ivDoctype.setImageDrawable(context.getResources().getDrawable(R.drawable.diagnostic_study));
        else if (docObj.getType().contains("Laboratory"))
            holder.ivDoctype.setImageDrawable(context.getResources().getDrawable(R.drawable.lab));
        else
            holder.ivDoctype.setImageDrawable(context.getResources().getDrawable(R.drawable.other_doc));
        holder.clOrderItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //   row_index = position;
                // notifyDataSetChanged();
                mediatorInterface.changeFragmentTo(OtherDocsDetailsFragment.newInstance(docObj), docObj.getEstFullname());
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediatorInterface.changeFragmentTo(HealthRecordDetailsFragment.newInstance(docObj), docObj.getEstFullname());
            }
        });
    }

    @Override
    public int getItemCount() {
        return othersDocsArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvDocType, tvDosage, tvDateWritten, tvEstName, tvDate;
        CardView clOrderItem;
        ImageButton imageButton;
        ImageView ivMoreArrow, ivDoctype;

        public MyViewHolder(View view) {
            super(view);
            tvDocType = view.findViewById(R.id.tv_title_docs);
            tvDosage = view.findViewById(R.id.tv_hospital_docs);
            tvDateWritten = view.findViewById(R.id.tv_date_docs);
            tvDate = view.findViewById(R.id.tvDate);
            clOrderItem = view.findViewById(R.id.constraintLayout_documents);
            ivMoreArrow = view.findViewById(R.id.iv_moreArrow);
            ivDoctype = view.findViewById(R.id.ivDocType);
            //imageButton = view.findViewById(R.id.imageButton);
        }
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        othersDocsArrayList.clear();
        if (charText.length() == 0) {
            othersDocsArrayList.addAll(arraylist);
        } else {
            for (ApiOtherDocsHolder.ApiDocInfo wp : arraylist) {
                if (wp.getTypeD().toLowerCase(Locale.getDefault()).contains(charText)) {
                    othersDocsArrayList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}
