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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiUploadsDocsHolder;
import om.gov.moh.phr.fragments.UploadedDocDetailsFragment;
import om.gov.moh.phr.interfaces.MediatorInterface;

public class UploadedDocsRecyclerViewAdapter extends RecyclerView.Adapter<UploadedDocsRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<ApiUploadsDocsHolder.ApiUploadDocInfo> uploadedDocsArrayList;
    private Context context;
    private ArrayList<ApiUploadsDocsHolder.ApiUploadDocInfo> arraylist;
    private MediatorInterface mediatorInterface;
    //private int row_index = -1;

    public UploadedDocsRecyclerViewAdapter(MediatorInterface mMediatorCallback, ArrayList<ApiUploadsDocsHolder.ApiUploadDocInfo> uploadedDocsList, Context context) {
        this.uploadedDocsArrayList = uploadedDocsList;
        this.context = context;
        this.arraylist = new ArrayList<ApiUploadsDocsHolder.ApiUploadDocInfo>();
        this.arraylist.addAll(uploadedDocsList);
        this.mediatorInterface = mMediatorCallback;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.uploaded_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final ApiUploadsDocsHolder.ApiUploadDocInfo docObj = uploadedDocsArrayList.get(position);
        if (docObj.getStatus().equals("P"))
            holder.ivDocStatus.setVisibility(View.VISIBLE);
        else holder.ivDocStatus.setVisibility(View.INVISIBLE);
        holder.tvDocType.setText(docObj.getDocTypeName());
        holder.tvDocInfo.setText(docObj.getSource());
        holder.tvDocDate.setText(docObj.getCreatedDate());
        if (position != 0) {
            int prev = position - 1;
            String prevEncounterDate = uploadedDocsArrayList.get(prev).getCreatedDate();
            if (docObj.getCreatedDate().equals(prevEncounterDate)) {
                holder.tvDocDate.setVisibility(View.GONE);
            } else
                holder.tvDocDate.setText(context.getString(R.string.date_label)+ " " + docObj.getCreatedDate());
        } else
            holder.tvDocDate.setText(context.getString(R.string.date_label)+ " " + docObj.getCreatedDate());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  row_index = position;
              //  notifyDataSetChanged();
                mediatorInterface.changeFragmentTo(UploadedDocDetailsFragment.newInstance(docObj), docObj.getDocTypeName());
            }
        });
      /*  if (row_index == position) {
            holder.imageButton.setBackgroundTintList(context.getResources().getColorStateList(R.color.colorPeach));
            holder.cl_uploadedItem.setBackgroundColor(context.getResources().getColor(R.color.colorPeach));
        } else {
            holder.imageButton.setBackgroundTintList(context.getResources().getColorStateList(R.color.colorWhite));
            holder.cl_uploadedItem.setBackgroundColor(context.getResources().getColor(R.color.colorWhite));
        }*/
    }

    @Override
    public int getItemCount() {
        return uploadedDocsArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvDocType, tvDocInfo, tvDocDate;
        CardView cardView;
        ImageView ivDocStatus;
        ImageButton imageButton;

        public MyViewHolder(View view) {
            super(view);
            tvDocType = view.findViewById(R.id.tv_DocType);
            tvDocInfo = view.findViewById(R.id.tv_DocInfo);
            tvDocDate = view.findViewById(R.id.tvDate);
            cardView = view.findViewById(R.id.constraintLayout_documents);
            ivDocStatus = view.findViewById(R.id.iv_docStatus);
            imageButton = view.findViewById(R.id.imageButton);
        }
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        uploadedDocsArrayList.clear();
        if (charText.length() == 0) {
            uploadedDocsArrayList.addAll(arraylist);
        } else {
            for (ApiUploadsDocsHolder.ApiUploadDocInfo wp : arraylist) {
                if (wp.getDocTypeName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    uploadedDocsArrayList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}
