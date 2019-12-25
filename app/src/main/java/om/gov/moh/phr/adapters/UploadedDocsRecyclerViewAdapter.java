package om.gov.moh.phr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
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
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ApiUploadsDocsHolder.ApiUploadDocInfo docObj = uploadedDocsArrayList.get(position);
        if(docObj.getStatus().equals("P"))
            holder.ivDocStatus.setVisibility(View.VISIBLE);
        holder.tvDocType.setText(docObj.getDocTypeName());
        holder.tvDocInfo.setText(docObj.getSource());
        holder.tvDocDate.setText(docObj.getCreatedDate());
        holder.cl_uploadedItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediatorInterface.changeFragmentTo(UploadedDocDetailsFragment.newInstance(docObj), docObj.getDocTypeName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return uploadedDocsArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvDocType, tvDocInfo, tvDocDate;
        ConstraintLayout cl_uploadedItem;
        ImageView ivDocStatus;

        public MyViewHolder(View view) {
            super(view);
            tvDocType = view.findViewById(R.id.tv_DocType);
            tvDocInfo = view.findViewById(R.id.tv_DocInfo);
            tvDocDate = view.findViewById(R.id.tv_estName);
            cl_uploadedItem = view.findViewById(R.id.cl_uploadedItem);
            ivDocStatus = view.findViewById(R.id.iv_docStatus);
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
