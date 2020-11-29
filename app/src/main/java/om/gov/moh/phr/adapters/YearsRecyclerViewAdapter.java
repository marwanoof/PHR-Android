package om.gov.moh.phr.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import om.gov.moh.phr.R;
import om.gov.moh.phr.interfaces.AdapterToFragmentConnectorInterface;
import om.gov.moh.phr.models.GlobalMethods;

import static om.gov.moh.phr.models.MyConstants.LANGUAGE_ARABIC;


public class YearsRecyclerViewAdapter extends
        RecyclerView.Adapter<YearsRecyclerViewAdapter.MyViewHolder> {

    private Context mContext;
    private AdapterToFragmentConnectorInterface mCallback;
    private ArrayList<String> mYearsList = new ArrayList<>();
    private int selectedPosition = 0;

    //the constructor of the LastRecordsRecyclerViewAdapter
    public YearsRecyclerViewAdapter(AdapterToFragmentConnectorInterface fragment, Context context) {
        this.mContext = context;
        mCallback = fragment;
    }

    //onCreateViewHolder allows you to inflate the "List items view"
//also allows to make some action during once the list created
    @Override
    public YearsRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.health_record_year, parent, false);
        return new YearsRecyclerViewAdapter.MyViewHolder(itemView);
    }

    //onBindViewHolder , allows you to write the data into the fields
    @Override
    public void onBindViewHolder(@NonNull YearsRecyclerViewAdapter.MyViewHolder holder, final int position) {
        final String result = mYearsList.get(position);
        if (position == selectedPosition){
            holder.setSelected();
        }else {
            holder.setUnselected();
        }

        if (position > 0){
            if (GlobalMethods.getStoredLanguage(mContext).equals(LANGUAGE_ARABIC))
                holder.tvYear.setText(GlobalMethods.convertToEnglishNumber(result));
            else
                holder.tvYear.setText(result);
        }else
            holder.tvYear.setText(result);


    }


    @Override
    public int getItemCount() {
        return mYearsList.size();
    }

    public void updateList(ArrayList<String> newItems) {
        mYearsList = newItems;
        notifyDataSetChanged();
    }

    private String getItemAt(int position) {
        return mYearsList.get(position);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvYear;
        private final View yearView;

        public MyViewHolder(View view) {
            super(view);
            tvYear = itemView.findViewById(R.id.tv_year);
            yearView = itemView.findViewById(R.id.v_container);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //yearView.setBackground(mContext.getResources().getDrawable(R.drawable.shape_rect_year_red));
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        selectedPosition = position;
                        notifyDataSetChanged();
                        mCallback.onMyListItemClicked(getItemAt(position), null);
                    }
                }
            });
        }
        public void setSelected(){
            yearView.setBackground(mContext.getDrawable(R.drawable.shape_rect_year_red));
            tvYear.setTextColor(Color.WHITE);
        }
        public void setUnselected(){
            yearView.setBackground(mContext.getDrawable(R.drawable.shape_rect_year));
            tvYear.setTextColor(Color.BLACK);
        }
    }


}
  