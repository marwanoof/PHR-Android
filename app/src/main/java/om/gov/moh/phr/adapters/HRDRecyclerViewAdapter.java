package om.gov.moh.phr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;

import java.util.ArrayList;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.Immpression;

public class HRDRecyclerViewAdapter extends RecyclerView.Adapter<HRDRecyclerViewAdapter.MyViewHolder>{
    private ArrayList<Immpression> ImpArrayList;
    private Context context;
    public HRDRecyclerViewAdapter(ArrayList<Immpression> impArrayList, Context context) {
        ImpArrayList = impArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hrd_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Immpression impObj = ImpArrayList.get(position);
        holder.tvNoteTitle.setText(impObj.getNoteTitle());
        holder.tvNoteTxt.setText(impObj.getNoteText());
    }

    @Override
    public int getItemCount() {
        return ImpArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvNoteTitle, tvNoteTxt;

        public MyViewHolder(View view) {
            super(view);
            tvNoteTitle = view.findViewById(R.id.tv_noteTitle);
            tvNoteTxt = view.findViewById(R.id.note_txt);
        }
    }
}
