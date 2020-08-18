package om.gov.moh.phr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;



import java.util.ArrayList;

import om.gov.moh.phr.R;


public class PagerCardMainAdapter extends RecyclerView.Adapter<PagerCardMainAdapter.MyViewHolder> {

    //private ArrayList<PersonalDetailMain> models;
    private ArrayList<String> models;
    //private ArrayList<ApiDependentsHolder.Dependent> dependents;
    private LayoutInflater layoutInflater;
    private Context context;

    public PagerCardMainAdapter(ArrayList<String> models, Context context) {
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
    public PagerCardMainAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pager_card_main, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull PagerCardMainAdapter.MyViewHolder holder, int position) {
        if (position == 1){
            holder.constraintLayoutMain.setVisibility(View.VISIBLE);
            holder.constraintLayoutInfo.setVisibility(View.GONE);
            holder.constraintLayoutDep.setVisibility(View.GONE);

        }else if (position == 0){
            holder.constraintLayoutMain.setVisibility(View.GONE);
            holder.constraintLayoutDep.setVisibility(View.GONE);
            holder.constraintLayoutInfo.setVisibility(View.VISIBLE);

        }else {

            holder.constraintLayoutMain.setVisibility(View.GONE);
            holder.constraintLayoutDep.setVisibility(View.VISIBLE);
            holder.constraintLayoutInfo.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName,tvPatientid;
        ConstraintLayout constraintLayoutMain, constraintLayoutInfo,constraintLayoutDep;
        RecyclerView depList;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name_main);
            tvPatientid = itemView.findViewById(R.id.tv_patientid_main);
            constraintLayoutMain = itemView.findViewById(R.id.mainCardConstraint);
            constraintLayoutInfo = itemView.findViewById(R.id.infoCardConstraint);
            constraintLayoutDep = itemView.findViewById(R.id.depCardConstraint);
            depList = itemView.findViewById(R.id.recycler_dep_home);

            // holder.setupRecyclerView(dependents);
        }
       /* private void setupRecyclerView(ArrayList<ApiDependentsHolder.Dependent> dependentArrayList) {
            DependentRecyclerViewAdapter mAdapter =
                    new DependentRecyclerViewAdapter((AdapterToFragmentConnectorInterface) PagerCardMainAdapter.this, context, dependentArrayList);
            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
            DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(depList.getContext(),
                    layoutManager.getOrientation());
            depList.addItemDecoration(mDividerItemDecoration);
            depList.setLayoutManager(layoutManager);
            depList.setItemAnimator(new DefaultItemAnimator());
            depList.setAdapter(mAdapter);
        }*/
    }
}