package om.gov.moh.phr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiProceduresNurseNoteHolder;

public class NurseNoteRecyclerViewAdapter extends RecyclerView.Adapter<NurseNoteRecyclerViewAdapter.MyViewHolder> {


    private ArrayList<ApiProceduresNurseNoteHolder.NurseNote> mItemsArrayList = new ArrayList<>();
    private Context mContext;

    //the constructor of the LastRecordsRecyclerViewAdapter
    public NurseNoteRecyclerViewAdapter(ArrayList<ApiProceduresNurseNoteHolder.NurseNote> mItemsArrayList, Context context) {
        this.mContext = context;
        this.mItemsArrayList = mItemsArrayList;
    }

    //onCreateViewHolder allows you to inflate the "List items view"
//also allows to make some action during once the list created
    @Override
    public NurseNoteRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_nurse_note, parent, false);
        return new NurseNoteRecyclerViewAdapter.MyViewHolder(itemView);
    }

    //onBindViewHolder , allows you to write the data into the fields
    @Override
    public void onBindViewHolder(@NonNull final NurseNoteRecyclerViewAdapter.MyViewHolder holder, int position) {


        holder.text.setText(mItemsArrayList.get(position).getText());
        holder.date.setText(mItemsArrayList.get(position).getTime());



    }


    @Override
    public int getItemCount() {
        return mItemsArrayList.size();
    }






    public class MyViewHolder extends RecyclerView.ViewHolder {


        private TextView text,date;


        public MyViewHolder(View view) {
            super(view);

            text = itemView.findViewById(R.id.tvTextNurse);
            date = itemView.findViewById(R.id.tvTimeNurse);

        }
    }
}