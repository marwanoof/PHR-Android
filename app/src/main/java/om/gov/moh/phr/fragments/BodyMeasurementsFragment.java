package om.gov.moh.phr.fragments;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import om.gov.moh.phr.R;
import om.gov.moh.phr.adapters.MeasurementRecyclerViewAdapter;
import om.gov.moh.phr.apimodels.ApiHomeHolder;
import om.gov.moh.phr.interfaces.AdapterToFragmentConnectorInterface;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;

public class BodyMeasurementsFragment extends Fragment implements AdapterToFragmentConnectorInterface {

    private static final String PARAM_RECENT_VITALS = "PARAM_RECENT_VITALS";
    private static final String PARAM2 = "PARAM2";
    private ArrayList<ApiHomeHolder.ApiRecentVitals> mRecentVitalsArrayList;
    private Context mContext;
    private MediatorInterface mMediatorCallback;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private TextView tvAlert;
    private RecyclerView recyclerView;
    private String pageTitle;
    private CardView noRecordsVitalSigns;
    public BodyMeasurementsFragment() {
        // Required empty public constructor
    }

    public static BodyMeasurementsFragment newInstance(ArrayList<ApiHomeHolder.ApiRecentVitals> recentVitalsArrayList,String title) {
        BodyMeasurementsFragment fragment = new BodyMeasurementsFragment();
        Bundle args = new Bundle();
        args.putSerializable(PARAM_RECENT_VITALS, recentVitalsArrayList);
        args.putSerializable(PARAM2, title);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mMediatorCallback = (MediatorInterface) context;
        mToolbarControllerCallback = (ToolbarControllerInterface) context;
        mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.GONE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRecentVitalsArrayList = (ArrayList<ApiHomeHolder.ApiRecentVitals>) getArguments().getSerializable(PARAM_RECENT_VITALS);
            pageTitle = (String) getArguments().getSerializable(PARAM2);
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
        tvToolBarTitle.setText(pageTitle);
        tvToolBarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mToolbarControllerCallback.customToolbarBackButtonClicked();
            }
        });
        recyclerView = parentView.findViewById(R.id.recycler_view);
        tvAlert = parentView.findViewById(R.id.tv_alert);
        noRecordsVitalSigns = parentView.findViewById(R.id.noRecordCardView);
        setupRecyclerView(recyclerView);
        return parentView;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        MeasurementRecyclerViewAdapter mAdapter =
                new MeasurementRecyclerViewAdapter(BodyMeasurementsFragment.this, mContext, getMeasurementArrayList());
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(mContext, R.anim.delay_slide_down);
        recyclerView.setLayoutAnimation(animation);
        recyclerView.setAdapter(mAdapter);
    }


    @Override
    public <T> void onMyListItemClicked(T dataToPass, String dataTitle) {
    }

    @Override
    public <T> void onMyListItemClicked(T dataToPass, String dataTitle, int position) {
       // mMediatorCallback.changeFragmentTo(VitalsGraphFragment.newInstance(mRecentVitalsArrayList.get(position).getName()), VitalsGraphFragment.class.getSimpleName());
    }

    private ArrayList<ApiHomeHolder.ApiRecentVitals> getMeasurementArrayList() {
        ArrayList<ApiHomeHolder.ApiRecentVitals> measurementArrayList = new ArrayList<>();
        if (mRecentVitalsArrayList != null&&mRecentVitalsArrayList.size()>0) {
            for (int i = 0; i < mRecentVitalsArrayList.size(); i++) {
                if (mRecentVitalsArrayList.get(i).getShowVitalPageYn().equals("Y")){
                    measurementArrayList.add(mRecentVitalsArrayList.get(i));
                }
                /*if (!mRecentVitalsArrayList.get(i).getName().equals("G6PD")&&!mRecentVitalsArrayList.get(i).getName().equals("ABO Screening")) {
                        measurementArrayList.add(new ApiDemographicsHolder().new ApiDemographicItem().new RecentVitals(mRecentVitalsArrayList.get(i).getName(), mRecentVitalsArrayList.get(i).getVitalNameNls(), mRecentVitalsArrayList.get(i).getValue(),
                                mRecentVitalsArrayList.get(i).getUnit(),mRecentVitalsArrayList.get(i).getType()));
                }*/
            }
        } else
            displayAlert(getResources().getString(R.string.no_recent_vital_sign_msg));
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

    private void displayAlert(String msg) {
        recyclerView.setVisibility(View.GONE);
        tvAlert.setVisibility(View.VISIBLE);
        noRecordsVitalSigns.setVisibility(View.VISIBLE);
        tvAlert.setText(msg);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.VISIBLE);
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
    }
}
