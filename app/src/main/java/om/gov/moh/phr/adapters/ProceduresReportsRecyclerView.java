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
import om.gov.moh.phr.apimodels.ApiProceduresReportsHolder;
import om.gov.moh.phr.fragments.ProceduresReportsDetailsFragment;
import om.gov.moh.phr.interfaces.MediatorInterface;

public class ProceduresReportsRecyclerView extends RecyclerView.Adapter<ProceduresReportsRecyclerView.MyViewHolder> {
    private ArrayList<ApiProceduresReportsHolder> proceduresReportArrayList;
    private Context context;
    private ArrayList<ApiProceduresReportsHolder> arraylist;
    private MediatorInterface mediatorInterface;
    private ArrayList<String> textReport;
    private boolean isTrue;
    private boolean isRad;

    public ProceduresReportsRecyclerView(ArrayList<String> arrayList, Context mContext) {
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
                .inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (isTrue) {
            String reportText = textReport.get(position);
            holder.tvProcedureName.setText(reportText);
            holder.tvDosage.setVisibility(View.INVISIBLE);
            holder.tvDateWritten.setVisibility(View.INVISIBLE);
            holder.tvEstName.setVisibility(View.INVISIBLE);
            holder.tvEstName.setVisibility(View.INVISIBLE);
            holder.imageButton.setVisibility(View.GONE);
        } else {
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
                    if (isRad)
                        mediatorInterface.changeFragmentTo(ProceduresReportsDetailsFragment.newInstance(procedureObj, "RAD"), procedureObj.getName());
                    else
                        mediatorInterface.changeFragmentTo(ProceduresReportsDetailsFragment.newInstance(procedureObj), procedureObj.getName());
                }
            });
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
        TextView tvProcedureName, tvDosage, tvDateWritten, tvEstName;
        ConstraintLayout clOrderItem;
        ImageButton imageButton;

        public MyViewHolder(View view) {
            super(view);
            tvProcedureName = view.findViewById(R.id.tv_procName);
            tvDosage = view.findViewById(R.id.tv_status);
            tvDosage.setVisibility(View.INVISIBLE);
            tvDateWritten = view.findViewById(R.id.tv_orderDate);
            tvEstName = view.findViewById(R.id.tv_estName);
            clOrderItem = view.findViewById(R.id.cl_orderItem);
            imageButton = view.findViewById(R.id.imageButton);
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
