package om.gov.moh.phr.fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import om.gov.moh.phr.R;
import om.gov.moh.phr.adapters.MeasurementRecyclerViewAdapter;
import om.gov.moh.phr.apimodels.ApiDemographicsHolder;
import om.gov.moh.phr.interfaces.AdapterToFragmentConnectorInterface;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;

public class BodyMeasurementsFragment extends Fragment implements AdapterToFragmentConnectorInterface {

    private static final String PARAM_RECENT_VITALS = "PARAM_RECENT_VITALS";
    private ArrayList<ApiDemographicsHolder.ApiDemographicItem.RecentVitals> mRecentVitalsArrayList;
    private Context mContext;
    private MediatorInterface mMediatorCallback;
    private ToolbarControllerInterface mToolbarControllerCallback;

    public BodyMeasurementsFragment() {
        // Required empty public constructor
    }

    public static BodyMeasurementsFragment newInstance(ArrayList<ApiDemographicsHolder.ApiDemographicItem.RecentVitals> recentVitalsArrayList) {
        BodyMeasurementsFragment fragment = new BodyMeasurementsFragment();
        Bundle args = new Bundle();
        args.putSerializable(PARAM_RECENT_VITALS, recentVitalsArrayList);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mMediatorCallback = (MediatorInterface) context;
        mToolbarControllerCallback = (ToolbarControllerInterface) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRecentVitalsArrayList = (ArrayList<ApiDemographicsHolder.ApiDemographicItem.RecentVitals>) getArguments().getSerializable(PARAM_RECENT_VITALS);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_body_measurements, container, false);

        //simple toolbar
        ImageButton ibToolbarBackButton = parentView.findViewById(R.id.ib_toolbar_back_button);
        ibToolbarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mToolbarControllerCallback.customToolbarBackButtonClicked();
            }
        });
        TextView tvToolBarTitle = parentView.findViewById(R.id.tv_toolbar_title);
        tvToolBarTitle.setText(getString(R.string.title_body_measurements));
        tvToolBarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mToolbarControllerCallback.customToolbarBackButtonClicked();
            }
        });
        RecyclerView recyclerView = parentView.findViewById(R.id.recycler_view);
        setupRecyclerView(recyclerView);
        return parentView;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        //declare recycler view adapter
        //TODO : change getMeasurementArrayList() to mRecentVitalsArrayList
        MeasurementRecyclerViewAdapter mAdapter =
                new MeasurementRecyclerViewAdapter(BodyMeasurementsFragment.this, mContext, getMeasurementArrayList());
        recyclerView.setHasFixedSize(true);// not required
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }


    @Override
    public <T> void onMyListItemClicked(T dataToPass, String dataTitle) {
   }

    @Override
    public <T> void onMyListItemClicked(T dataToPass, String dataTitle, int position) {
        mMediatorCallback.changeFragmentTo(VitalsGraphFragment.newInstance(mRecentVitalsArrayList.get(position).getName()),VitalsGraphFragment.class.getSimpleName());
    }

    private ArrayList<ApiDemographicsHolder.ApiDemographicItem.RecentVitals> getMeasurementArrayList() {
        ArrayList<ApiDemographicsHolder.ApiDemographicItem.RecentVitals> measurementArrayList = new ArrayList<>();
for(int i=0; i<mRecentVitalsArrayList.size(); i++){
    measurementArrayList.add(new ApiDemographicsHolder().new ApiDemographicItem().new RecentVitals(mRecentVitalsArrayList.get(i).getName(), mRecentVitalsArrayList.get(i).getValue(),
            mRecentVitalsArrayList.get(i).getUnit()));
}

     /*   measurementArrayList.add(new ApiDemographicsHolder().new ApiDemographicItem().new RecentVitals(getString(R.string.title_height_cm),
                mRecentVitalsArrayList.get(1).getValue()));
        measurementArrayList.add(new ApiDemographicsHolder().new ApiDemographicItem().new RecentVitals(getString(R.string.title_bmi),
                mRecentVitalsArrayList.get(2).getValue()));

        measurementArrayList.add(new ApiDemographicsHolder().new ApiDemographicItem().new RecentVitals(getString(R.string.title_blood_pressure),
                mRecentVitalsArrayList.get(3).getValue()));
        measurementArrayList.add(new ApiDemographicsHolder().new ApiDemographicItem().new RecentVitals(getString(R.string.title_respiration_rate),
                mRecentVitalsArrayList.get(4).getValue()));
        measurementArrayList.add(new ApiDemographicsHolder().new ApiDemographicItem().new RecentVitals(getString(R.string.title_temperature),
                mRecentVitalsArrayList.get(5).getValue()));*/

        return measurementArrayList;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mMediatorCallback.changeFragmentContainerVisibility(View.GONE, View.VISIBLE);
        mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.VISIBLE);
    }
}
