package om.gov.moh.phr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiOtherDocsHolder;
import om.gov.moh.phr.fragments.OtherDocsDetailsFragment;
import om.gov.moh.phr.interfaces.MediatorInterface;

public class OtherDocsRecyclerViewAdapter extends RecyclerView.Adapter<OtherDocsRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<ApiOtherDocsHolder.ApiDocInfo> othersDocsArrayList;
    private Context context;
    private ArrayList<ApiOtherDocsHolder.ApiDocInfo> arraylist;
    private MediatorInterface mediatorInterface;
    private int row_index = -1;

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
        SimpleDateFormat df2 = new SimpleDateFormat("MM /dd /yyyy " + "\n" + " HH:mm:ss");
        String dateText = df2.format(date);
        holder.tvDateWritten.setText(dateText);
        holder.tvDosage.setText(docObj.getEstName());
        if (row_index == position) {
           // holder.imageButton.setBackgroundTintList(context.getResources().getColorStateList(R.color.colorPeach));
            holder.clOrderItem.setBackgroundColor(context.getResources().getColor(R.color.colorPeach));
        } else {
            //holder.imageButton.setBackgroundTintList(context.getResources().getColorStateList(R.color.colorWhite));
            holder.clOrderItem.setBackgroundColor(context.getResources().getColor(R.color.colorWhite));
        }
        holder.clOrderItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                row_index = position;
                notifyDataSetChanged();
                mediatorInterface.changeFragmentTo(OtherDocsDetailsFragment.newInstance(docObj), docObj.getEstFullname());
            }
        });
    }

    @Override
    public int getItemCount() {
        return othersDocsArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvDocType, tvDosage, tvDateWritten, tvEstName;
        ConstraintLayout clOrderItem;
        ImageButton imageButton;

        public MyViewHolder(View view) {
            super(view);
            tvDocType = view.findViewById(R.id.tv_title_docs);
            tvDosage = view.findViewById(R.id.tv_hospital_docs);
            tvDateWritten = view.findViewById(R.id.tv_date_docs);

            clOrderItem = view.findViewById(R.id.constraintLayout_documents);
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
