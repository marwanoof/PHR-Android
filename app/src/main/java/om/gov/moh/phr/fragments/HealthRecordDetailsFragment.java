package om.gov.moh.phr.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiEncountersHolder;
import om.gov.moh.phr.apimodels.ApiOtherDocsHolder;
import om.gov.moh.phr.apimodels.ApiProceduresReportsHolder;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.MyProgressDialog;

import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;


public class HealthRecordDetailsFragment extends Fragment {
    private Context mContext;
    private MediatorInterface mMediatorCallback;
    private RequestQueue mQueue;
    private MyProgressDialog mProgressDialog;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private static final String ARG_PARAM1 = "ARG_PARAM1";
    private static final String ARG_PARAM2 = "ARG_PARAM2";
    private static final String ARG_PARAM3 = "ARG_PARAM3";
    private static final String ARG_PARAM4 = "ARG_PARAM4";
   private ApiEncountersHolder.Encounter encounterInfo;
   private ApiOtherDocsHolder.ApiDocInfo docInfo;
   private ApiProceduresReportsHolder.ProceduresByEncounter procedureObj;

    public HealthRecordDetailsFragment() {
        // Required empty public constructor
    }

  /*  public static HealthRecordDetailsFragment newInstance(String encounterId) {
        HealthRecordDetailsFragment fragment = new HealthRecordDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM4, encounterId);
        fragment.setArguments(args);
        return fragment;
    }*/
    public static HealthRecordDetailsFragment newInstance(ApiEncountersHolder.Encounter encounterObj) {
        HealthRecordDetailsFragment fragment = new HealthRecordDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, encounterObj);
        fragment.setArguments(args);
        return fragment;
    }
    public static HealthRecordDetailsFragment newInstance(ApiOtherDocsHolder.ApiDocInfo docObj) {
        HealthRecordDetailsFragment fragment = new HealthRecordDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM2, docObj);
        fragment.setArguments(args);
        return fragment;
    }
    public static HealthRecordDetailsFragment newInstance(ApiProceduresReportsHolder.ProceduresByEncounter procedureObj) {
        HealthRecordDetailsFragment fragment = new HealthRecordDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM3, procedureObj);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        mMediatorCallback = (MediatorInterface) context;
        mToolbarControllerCallback = (ToolbarControllerInterface) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null) {
            if (getArguments().getSerializable(ARG_PARAM1) != null)
                encounterInfo = (ApiEncountersHolder.Encounter) getArguments().getSerializable(ARG_PARAM1);
            if (getArguments().getSerializable(ARG_PARAM2) != null)
                docInfo = (ApiOtherDocsHolder.ApiDocInfo) getArguments().getSerializable(ARG_PARAM2);
            if (getArguments().getSerializable(ARG_PARAM3) != null)
                procedureObj = (ApiProceduresReportsHolder.ProceduresByEncounter) getArguments().getSerializable(ARG_PARAM3);
            /*if (getArguments().getSerializable(ARG_PARAM4) != null)
                getEncounterResponse((String) getArguments().getSerializable(ARG_PARAM4));*/
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        View parentView = inflater.inflate(R.layout.fragment_health_record_details, container, false);
        mProgressDialog = new MyProgressDialog(mContext);// initializes progress dialog
        mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
        ImageButton ibToolbarBackButton = parentView.findViewById(R.id.ib_toolbar_back_button);
        ibToolbarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    mToolbarControllerCallback.customToolbarBackButtonClicked();
            }
        });
        TextView tvTitle = parentView.findViewById(R.id.tv_title);

        if(encounterInfo!=null)
        tvTitle.setText(encounterInfo.getDepartmentArrayList().get(0).getValue() + ", " + encounterInfo.getEstShortName());
        else if(docInfo!=null)
            tvTitle.setText(docInfo.getLocationName() + ", " + docInfo.getEstName());
        else
            tvTitle.setText("department name" + ", " + procedureObj.getEstFullName());
        ViewPager mViewPager = parentView.findViewById(R.id.container);
        TabLayout tabs = parentView.findViewById(R.id.tabs);
        tabs.bringToFront();
        HRDSectionsPagerAdapter mAdapter
                = new HRDSectionsPagerAdapter(getChildFragmentManager(),
                mContext);
        mViewPager.setAdapter(mAdapter);
        tabs.setupWithViewPager(mViewPager);

        return parentView;
    }

    private class HRDSectionsPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<String> mFragmentTitles = new ArrayList<>();

        private HRDSectionsPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            mFragmentTitles.add(context.getResources().getString(R.string.hrd_imp));//val : 1
            mFragmentTitles.add(context.getResources().getString(R.string.hrd_med));//val : 2
            mFragmentTitles.add(context.getResources().getString(R.string.hrd_lab));//val : 3
            mFragmentTitles.add(context.getResources().getString(R.string.hrd_rad));//val : 4
            mFragmentTitles.add(context.getResources().getString(R.string.hrd_proc));//val : 5
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if(encounterInfo!=null) {
                if (position == 0)
                    return ImpressionFragment.newInstance(encounterInfo);
                else if (position == 1)
                    return MedicationFragment.newInstance(encounterInfo);
                else if (position == 2)
                    return LabResultsFragment.newInstance(encounterInfo);
                else if (position == 3)
                    return RadFragment.newInstance(encounterInfo);
                else
                    return ProceduresReportsFragment.newInstance(encounterInfo);
            }else if(docInfo!=null){
                if (position == 0)
                    return ImpressionFragment.newInstance(docInfo);
                else if (position == 1)
                    return MedicationFragment.newInstance(docInfo);
                else if (position == 2)
                    return LabResultsFragment.newInstance(docInfo);
                else if (position == 3)
                    return RadFragment.newInstance(docInfo);
                else
                    return ProceduresReportsFragment.newInstance(docInfo);
            }else {
                if (position == 0)
                    return ImpressionFragment.newInstance(procedureObj);
                else if (position == 1)
                    return MedicationFragment.newInstance(procedureObj);
                else if (position == 2)
                    return LabResultsFragment.newInstance(procedureObj);
                else if (position == 3)
                    return RadFragment.newInstance(procedureObj);
                else
                    return ProceduresReportsFragment.newInstance(procedureObj);
            }
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentTitles.size();
        }
    }
}
