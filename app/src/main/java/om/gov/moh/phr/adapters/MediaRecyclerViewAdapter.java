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
import om.gov.moh.phr.apimodels.ApiMediaProcedureHolder;
import om.gov.moh.phr.apimodels.ApiProceduresNurseNoteHolder;
import om.gov.moh.phr.fragments.ProceduresReportsDetailsFragment;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.models.GlobalMethods;

import static om.gov.moh.phr.models.MyConstants.LANGUAGE_ARABIC;

public class MediaRecyclerViewAdapter extends RecyclerView.Adapter<MediaRecyclerViewAdapter.MyViewHolder> {


    private ArrayList<ApiMediaProcedureHolder.MediaProcedure> mItemsArrayList = new ArrayList<>();
    private Context mContext;
    private MediatorInterface mediatorInterface;
    //the constructor of the LastRecordsRecyclerViewAdapter
    public MediaRecyclerViewAdapter(ArrayList<ApiMediaProcedureHolder.MediaProcedure> mItemsArrayList, Context context) {
        this.mContext = context;
        this.mItemsArrayList = mItemsArrayList;
        mediatorInterface = (MediatorInterface) context;
    }

    //onCreateViewHolder allows you to inflate the "List items view"
//also allows to make some action during once the list created
    @Override
    public MediaRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_procedures, parent, false);
        return new MediaRecyclerViewAdapter.MyViewHolder(itemView);
    }

    //onBindViewHolder , allows you to write the data into the fields
    @Override
    public void onBindViewHolder(@NonNull final MediaRecyclerViewAdapter.MyViewHolder holder, int position) {
        final ApiMediaProcedureHolder.MediaProcedure mediaProcedure = mItemsArrayList.get(position);

        holder.tvProcedureName.setText(mediaProcedure.getMediaSubType());


        holder.tvDateWritten.setText(mediaProcedure.getCreationTime());
        holder.tvEstName.setText(mediaProcedure.getEstFullname());
        if (GlobalMethods.getStoredLanguage(mContext).equals(LANGUAGE_ARABIC)){
            //holder.tvEstName.setText(mediaProcedure.getEstFullnameNls());
            holder.moreDetailArrow.setImageBitmap(GlobalMethods.flipImage(mContext));
        }else {
            //holder.tvEstName.setText(mediaProcedure.getEstFullname());
            holder.moreDetailArrow.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_arrow_right));
        }


            holder.cardView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

                mediatorInterface.changeFragmentTo(ProceduresReportsDetailsFragment.newInstance(mediaProcedure), mediaProcedure.getMediaSubType());
        }
    });



    }


    @Override
    public int getItemCount() {
        return mItemsArrayList.size();
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
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.ic_arrow_right);
            moreDetailArrow.setImageBitmap(bitmap);

        }
    }
}