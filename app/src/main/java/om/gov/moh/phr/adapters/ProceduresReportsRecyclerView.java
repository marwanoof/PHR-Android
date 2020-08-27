package om.gov.moh.phr.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiProceduresReportsHolder;
import om.gov.moh.phr.fragments.ProceduresReportsDetailsFragment;
import om.gov.moh.phr.interfaces.MediatorInterface;

public class ProceduresReportsRecyclerView extends RecyclerView.Adapter<ProceduresReportsRecyclerView.MyViewHolder> {
    private ArrayList<ApiProceduresReportsHolder> proceduresReportArrayList;
    private Context context;
    private ArrayList<ApiProceduresReportsHolder> arraylist;
    private MediatorInterface mediatorInterface;
    private ArrayList<ProceduresReportsDetailsFragment.ReportData> textReport;
    private boolean isTrue;
    private boolean isRad;
    private int row_index = -1;
    public ProceduresReportsRecyclerView(ArrayList<ProceduresReportsDetailsFragment.ReportData> arrayList, Context mContext) {
        this.textReport = arrayList;
        this.context = mContext;
        isTrue = true;
    }

    public ProceduresReportsRecyclerView(MediatorInterface mMediatorCallback, ArrayList<ApiProceduresReportsHolder> reportsList, Context context, boolean isRad) {
        this.proceduresReportArrayList = reportsList;
        this.context = context;
        this.arraylist = new ArrayList<ApiProceduresReportsHolder>();
        this.arraylist.addAll(reportsList);
        this.mediatorInterface = mMediatorCallback;
        this.isTrue = false;
        this.isRad = isRad;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_procedures, parent, false);
        if (isTrue){
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_procedures_details, parent, false);
        }else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_procedures, parent, false);
        }

        return new MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        if (isTrue) {

            ProceduresReportsDetailsFragment.ReportData reportData = textReport.get(position);
            holder.tvContentDetails.setTypeface(null,Typeface.NORMAL);
            holder.tvContentDetails.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
            holder.tvContentDetails.setText(reportData.getReportText());

            SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy \n hh:mm:ss", Locale.US);
            if(reportData.getReportTime()==null)
                holder.tvDateDetails.setVisibility(View.INVISIBLE);
            else {
                String dateReport = df2.format(reportData.getReportTime());
                holder.tvDateDetails.setText(dateReport);
            }
//            holder.tvEstName.setVisibility(View.INVISIBLE);
//            holder.moreDetailArrow.setVisibility(View.GONE);
//            holder.tvEstName.setVisibility(View.INVISIBLE);

        } else {
            holder.moreDetailArrow.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams = holder.cardView.getLayoutParams();
            layoutParams.height = 150;
            //holder.cardView.setLayoutParams(layoutParams);
            final ApiProceduresReportsHolder procedureObj = proceduresReportArrayList.get(position);
            if (isRad) {
                holder.tvProcedureName.setText(procedureObj.getProcName());
                Date date = new Date(procedureObj.getProcedureDoneDate());
                SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
                String dateText = df2.format(date);
                holder.tvDateWritten.setText(dateText);
                holder.tvEstName.setText(procedureObj.getEstName());
            } else {
                holder.tvProcedureName.setText(procedureObj.getName());
                Date date = new Date(procedureObj.getStartTime());
                SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
                String dateText = df2.format(date);
                holder.tvDateWritten.setText(dateText);
                holder.tvEstName.setText(procedureObj.getEstName());
            }
            holder.clOrderItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    row_index = position;
                    notifyDataSetChanged();
                    if (isRad)
                        mediatorInterface.changeFragmentTo(ProceduresReportsDetailsFragment.newInstance(procedureObj, "RAD"), procedureObj.getName());
                    else
                        mediatorInterface.changeFragmentTo(ProceduresReportsDetailsFragment.newInstance(procedureObj), procedureObj.getName());
                }
            });
//            if (row_index == position) {
//
//                holder.clOrderItem.setBackgroundColor(context.getResources().getColor(R.color.colorPeach));
//            } else {
//
//                holder.clOrderItem.setBackgroundColor(context.getResources().getColor(R.color.colorWhite));
//            }
        }
    }

    @Override
    public int getItemCount() {
        if (isTrue)
            return textReport.size();
        else
            return proceduresReportArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvProcedureName, tvDosage, tvDateWritten, tvEstName, tvContentDetails, tvDateDetails;
        ConstraintLayout clOrderItem;
        ImageView moreDetailArrow;
        CardView cardView;
        //ImageButton imageButton;

        public MyViewHolder(View view) {
            super(view);
            tvProcedureName = view.findViewById(R.id.tv_name_proc);

            tvDateWritten = view.findViewById(R.id.tv_date_proc);
            tvEstName = view.findViewById(R.id.tv_hospital_proc);
            clOrderItem = view.findViewById(R.id.constraintLayout_proc);
            moreDetailArrow = view.findViewById(R.id.img_arrowDetails);
            cardView = view.findViewById(R.id.cardView_procContent);
            tvContentDetails = view.findViewById(R.id.tv_content_proc_details);
            tvDateDetails = view.findViewById(R.id.tv_date_proc_details);
        }
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        proceduresReportArrayList.clear();
        if (charText.length() == 0) {
            proceduresReportArrayList.addAll(arraylist);
        } else {
            for (ApiProceduresReportsHolder wp : arraylist) {
                if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    proceduresReportArrayList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}
