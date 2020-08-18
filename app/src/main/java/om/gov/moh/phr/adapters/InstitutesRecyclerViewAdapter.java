package om.gov.moh.phr.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import om.gov.moh.phr.R;
import om.gov.moh.phr.interfaces.AdapterToFragmentConnectorInterface;

public class InstitutesRecyclerViewAdapter extends ListAdapter<String, InstitutesRecyclerViewAdapter.MyViewHolder> {
    private static final DiffUtil.ItemCallback<String> DIFF_CALLBACK = new DiffUtil.ItemCallback<String>() {
        @Override
        public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return oldItem.equalsIgnoreCase(newItem);
        }
    };
    private AdapterToFragmentConnectorInterface mListener;

    public InstitutesRecyclerViewAdapter() {
        super(DIFF_CALLBACK);
    }


    //onCreateViewHolder allows you to inflate the "List items view"
//also allows to make some action during once the list created
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.add_fragment_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    //onBindViewHolder , allows you to write the data into the fields
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final String result = getItem(position);
        holder.tvTitle.setText(result);
    }

    public String getInstituteAt(int position) {
        return getItem(position);
    }

    public void setOnItemClickListener(AdapterToFragmentConnectorInterface listener) {
        mListener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;

        public MyViewHolder(View view) {
            super(view);
            tvTitle = itemView.findViewById(R.id.tv_title);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (mListener != null && position != RecyclerView.NO_POSITION) {
                        mListener.onMyListItemClicked(getInstituteAt(position), null);
                    }
                }
            });
        }
    }
}