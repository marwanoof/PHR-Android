package om.gov.moh.phr.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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

import static om.gov.moh.phr.models.MyConstants.LANGUAGE_ARABIC;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_PREFS;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_SELECTED;

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

        Date date = new Date(docObj.getIndexed());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy  HH:mm", Locale.ENGLISH);
        String dateText = dateFormat.format(date);
        holder.tvDateWritten.setText(dateText);

        if (getStoredLanguage().equals(LANGUAGE_ARABIC)){
            if (docObj.getType().contains("Hospital Discharge summary")){
                holder.tvDocType.setText(docObj.getTypeNls());
            } else{
                holder.tvDocType.setText(docObj.getTitleNls());
            }


            holder.tvDosage.setText(docObj.getEstFullnameNls());
            holder.moreDetails.setImageBitmap(flipImage());
        }else {
            holder.tvDocType.setText(docObj.getTitle());
            holder.tvDosage.setText(docObj.getEstFullname());
            holder.moreDetails.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_right));
        }
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
            holder.ivDoctype.setImageDrawable(context.getResources().getDrawable(R.drawable.other_doc));
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
        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediatorInterface.changeFragmentTo(HealthRecordDetailsFragment.newInstance(docObj), docObj.getEstFullname());
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return othersDocsArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvDocType, tvDosage, tvDateWritten, tvEstName;
        CardView clOrderItem;
        ImageButton imageButton;
        ImageView  ivDoctype, moreDetails;

        public MyViewHolder(View view) {
            super(view);
            tvDocType = view.findViewById(R.id.tv_title_docs);
            tvDosage = view.findViewById(R.id.tv_hospital_docs);
            tvDateWritten = view.findViewById(R.id.tv_date_docs);
            clOrderItem = view.findViewById(R.id.constraintLayout_documents);
            ivDoctype = view.findViewById(R.id.ivDocType);

            moreDetails = view.findViewById(R.id.imgArrowDetails);
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_arrow_right);
            moreDetails.setImageBitmap(bitmap);
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
                if (wp.getTitle().toLowerCase(Locale.getDefault()).contains(charText)
                || wp.getTypeNls().toLowerCase().contains(charText)
                || wp.getTitleNls().toLowerCase().contains(charText)
                || wp.getType().toLowerCase(Locale.getDefault()).contains(charText)
                || wp.getEstFullname().toLowerCase(Locale.getDefault()).contains(charText)
                || wp.getEstFullnameNls().toLowerCase().contains(charText)) {
                    othersDocsArrayList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
    private String getStoredLanguage() {
        SharedPreferences sharedPref = context.getSharedPreferences(LANGUAGE_PREFS, Context.MODE_PRIVATE);
        return sharedPref.getString(LANGUAGE_SELECTED, getDeviceLanguage());
    }
    private String getDeviceLanguage() {
        return Locale.getDefault().getLanguage();
    }

    private Bitmap flipImage() {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_arrow_right);
// create new matrix for transformation
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);

        Bitmap flipped_bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        return flipped_bitmap;

    }
}
