package om.gov.moh.phr.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiEncountersHolder;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;


public class HealthRecordDetailsFragment extends Fragment {
    private Context mContext;
    private MediatorInterface mMediatorCallback;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private static final String ARG_PARAM1 = "ARG_PARAM1";
    ApiEncountersHolder.Encounter encounterInfo;

    public HealthRecordDetailsFragment() {
        // Required empty public constructor
    }

    public static HealthRecordDetailsFragment newInstance(ApiEncountersHolder.Encounter encounterObj) {
        HealthRecordDetailsFragment fragment = new HealthRecordDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, encounterObj);
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
        if (getArguments() != null)
            encounterInfo = (ApiEncountersHolder.Encounter) getArguments().getSerializable(ARG_PARAM1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        View parentView = inflater.inflate(R.layout.fragment_health_record_details, container, false);
        ImageButton ibToolbarBackButton = parentView.findViewById(R.id.ib_toolbar_back_button);
        ibToolbarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    mToolbarControllerCallback.customToolbarBackButtonClicked();
            }
        });
        TextView tvTitle = parentView.findViewById(R.id.tv_title);
        tvTitle.setText(encounterInfo.getDepartmentArrayList().get(0).getValue() + ", " + encounterInfo.getEstShortName());
        ImageButton ibHome = parentView.findViewById(R.id.ib_home);
        ibHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToHome();
            }
        });
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

    private void backToHome() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
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
