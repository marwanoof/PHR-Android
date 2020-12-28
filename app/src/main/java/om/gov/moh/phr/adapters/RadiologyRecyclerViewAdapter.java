package om.gov.moh.phr.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiRadiologyHolder;
import om.gov.moh.phr.fragments.ProceduresReportsDetailsFragment;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.models.GlobalMethods;

import static om.gov.moh.phr.models.MyConstants.LANGUAGE_ARABIC;

public class RadiologyRecyclerViewAdapter extends RecyclerView.Adapter<RadiologyRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<ApiRadiologyHolder.Radiology> radiologyArrayList = new ArrayList<>();
    private Context context;
    private MediatorInterface mediatorInterface;

    public RadiologyRecyclerViewAdapter(Context context, ArrayList<ApiRadiologyHolder.Radiology> radiologyArrayList) {
        this.radiologyArrayList = radiologyArrayList;
        this.context = context;
        mediatorInterface = (MediatorInterface) context;
    }

    @NonNull
    @Override
    public RadiologyRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_procedures, parent, false);
        return new RadiologyRecyclerViewAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RadiologyRecyclerViewAdapter.MyViewHolder holder, int position) {
        final ApiRadiologyHolder.Radiology radiology = radiologyArrayList.get(position);

        holder.tvProcedureName.setText(radiology.getProcName());

        Date date = new Date(radiology.getReportDoneDate());
        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
        String dateText = df2.format(date);
        holder.tvDateWritten.setText(dateText);




        if (GlobalMethods.getStoredLanguage(context).equals(LANGUAGE_ARABIC)){
            holder.tvEstName.setText(radiology.getEstFullnameNls());
            holder.moreDetailArrow.setImageBitmap(GlobalMethods.flipImage(context));
        }else {
            holder.tvEstName.setText(radiology.getEstFullname());
            holder.moreDetailArrow.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_right));
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  row_index = position;
                // notifyDataSetChanged();
                if (radiology.getReportId() == null){
                    GlobalMethods.displayDialog(context.getResources().getString(R.string.no_recodrs_dialog),context);
                }else
                    mediatorInterface.changeFragmentTo(ProceduresReportsDetailsFragment.newInstance(radiology, "RAD"), radiology.getProcName());

            }
        });
    }

    @Override
    public int getItemCount() {
        return radiologyArrayList.size();
    }

    public void updateItemsList(ArrayList<ApiRadiologyHolder.Radiology> items) {
        radiologyArrayList = items;
        notifyDataSetChanged();
    }

    public void clear(){
        int size = radiologyArrayList.size();
        radiologyArrayList.clear();
        notifyItemRangeRemoved(0, size);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvProcedureName, tvDosage, tvDateWritten, tvEstName, tvContentDetails, tvDateDetails, tvDate;
        ConstraintLayout clOrderItem;
        ImageView moreDetailArrow;
        CardView cardView;


        public MyViewHolder(View view) {
            super(view);
            tvProcedureName = view.findViewById(R.id.tv_name_proc);
            tvDateWritten = view.findViewById(R.id.tv_date_proc);
            tvEstName = view.findViewById(R.id.tv_hospital_proc);
            moreDetailArrow = view.findViewById(R.id.img_arrowDetails);
            cardView = view.findViewById(R.id.cardView_procContent);
            tvContentDetails = view.findViewById(R.id.tv_content_proc_details);
            tvDateDetails = view.findViewById(R.id.tv_date_proc_details);

            tvDate = view.findViewById(R.id.tvDate);
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_arrow_right);
            moreDetailArrow.setImageBitmap(bitmap);
        }
    }
}