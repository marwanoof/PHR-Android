package om.gov.moh.phr.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiUploadsDocsHolder;
import om.gov.moh.phr.fragments.UploadedDocDetailsFragment;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.models.GlobalMethods;

import static om.gov.moh.phr.models.MyConstants.LANGUAGE_ARABIC;

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
        if (GlobalMethods.getStoredLanguage(context).equals(LANGUAGE_ARABIC))
            holder.tvDocType.setText(docObj.getDocTypeNameNls());
        else
            holder.tvDocType.setText(docObj.getDocTypeName());
        holder.tvDocInfo.setText(docObj.getSource());
        holder.tvDocDate.setText(docObj.getCreatedDate());

        if (GlobalMethods.getStoredLanguage(context).equals(LANGUAGE_ARABIC)) {

            holder.moreDetails.setImageBitmap(GlobalMethods.flipImage(context));
        } else {

            holder.moreDetails.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_right));
        }
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
        ImageButton moreDetails;


        public MyViewHolder(View view) {
            super(view);
            tvDocType = view.findViewById(R.id.tv_DocType);
            tvDocInfo = view.findViewById(R.id.tv_DocInfo);
            tvDocDate = view.findViewById(R.id.tvDate);
            cardView = view.findViewById(R.id.constraintLayout_documents);
            ivDocStatus = view.findViewById(R.id.iv_docStatus);
            moreDetails = view.findViewById(R.id.imgArrowDetailsSelf);
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_arrow_right);
            moreDetails.setImageBitmap(bitmap);

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
