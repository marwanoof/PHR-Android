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
import om.gov.moh.phr.apimodels.ApigetMessages;
import om.gov.moh.phr.fragments.ChatFragment;
import om.gov.moh.phr.interfaces.AdapterToFragmentConnectorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;


public class ChatsHomeAdapter  extends RecyclerView.Adapter<ChatsHomeAdapter.ViewHolder>{
    private ArrayList<ApigetMessages.Result> mStringArrayList;
    private Context context;
    private ToolbarControllerInterface toolBarControllerInterface;
    private AdapterToFragmentConnectorInterface mCallback;

    public ChatsHomeAdapter(ArrayList<ApigetMessages.Result> mStringArrayList, Context context, AdapterToFragmentConnectorInterface mCallback) {
        this.mStringArrayList = mStringArrayList;
        this.context = context;
        this.mCallback = mCallback;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.simple_item_arrow, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ApigetMessages.Result obj = mStringArrayList.get(position);
        //show infected violation case ..
            holder.tvTxt.setText(obj.getCreatedName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onMyListItemClicked(obj, ChatFragment.class.getSimpleName());
                }
            });
    }

    @Override
    public int getItemCount() {
        return mStringArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivArrow;
        private TextView tvTxt;
        public ViewHolder(View itemView) {
            super(itemView);
            ivArrow = itemView.findViewById(R.id.ivArrow);
            tvTxt = itemView.findViewById(R.id.tvTxt);
        }
    }
}
