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


public class ListHomeContentAdapter extends RecyclerView.Adapter<ListHomeContentAdapter.MyViewHolder> {
    private ArrayList<String> content;



    private Context context;

    public ListHomeContentAdapter(ArrayList<String> content, Context context) {
        this.content = content;
        this.context = context;

    }








    @NonNull
    @Override
    public ListHomeContentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_home_content, parent, false);
        return new ListHomeContentAdapter.MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ListHomeContentAdapter.MyViewHolder holder, int position) {

        holder.content.setText(content.get(position));

    }

    @Override
    public int getItemCount() {
        return 0;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView content;
        ImageView contentImg;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.tv_title_content);
            contentImg = itemView.findViewById(R.id.img_content);


            // holder.setupRecyclerView(dependents);
        }

    }
}