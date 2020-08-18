package om.gov.moh.phr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



import java.util.ArrayList;

import om.gov.moh.phr.R;


public class ListHomeAdapter extends RecyclerView.Adapter<ListHomeAdapter.MyViewHolder> {
    private String sectionName;
    private ArrayList<String> models;
    //private ArrayList<ApiDependentsHolder.Dependent> dependents;
    private LayoutInflater layoutInflater;
    private Context context;
    /*ArrayList<ApiDependentsHolder.Dependent> dependents*/
    public ListHomeAdapter(ArrayList<String> models, Context context) {
        this.models = models;
        //this.dependents = dependents;
        this.context = context;

    }
    /*public PagerCardMainAdapter(ArrayList<PersonalDetailMain> models, Context context) {
        this.models = models;
        this.context = context;
    }*/







    @NonNull
    @Override
    public ListHomeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_time_home_list, parent, false);
        return new ListHomeAdapter.MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ListHomeAdapter.MyViewHolder holder, int position) {



    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView sectionName;
        ImageView sectionImg;
        ImageButton expandBtn;
        RecyclerView contentList;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            sectionName = itemView.findViewById(R.id.tv_section_list_home);
            sectionImg = itemView.findViewById(R.id.img_section);
            expandBtn = itemView.findViewById(R.id.btn_expand_list_home);
            contentList = itemView.findViewById(R.id.recyclerView_content_list_home);

            // holder.setupRecyclerView(dependents);
        }
        /*private void setupRecyclerView(ArrayList<ApiDependentsHolder.Dependent> dependentArrayList) {
            DependentRecyclerViewAdapter mAdapter =
                    new DependentRecyclerViewAdapter((AdapterToFragmentConnectorInterface) ListHomeAdapter.this, context, dependentArrayList);
            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
            DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(contentList.getContext(),
                    layoutManager.getOrientation());
            contentList.addItemDecoration(mDividerItemDecoration);
            contentList.setLayoutManager(layoutManager);
            contentList.setItemAnimator(new DefaultItemAnimator());
            contentList.setAdapter(mAdapter);
        }*/
    }
}