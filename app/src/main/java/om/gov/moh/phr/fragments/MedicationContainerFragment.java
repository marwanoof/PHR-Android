package om.gov.moh.phr.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiHomeHolder;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;

import static om.gov.moh.phr.models.MyConstants.PARAM_API_DEMOGRAPHICS_ITEM;

/**
 * A simple {@link Fragment} subclass.
 */
public class MedicationContainerFragment extends Fragment {
    private Context mContext;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private String pageTitle;
    private static final String PARAM1 = "PARAM1";
    public MedicationContainerFragment() {
        // Required empty public constructor
    }

    public static MedicationContainerFragment newInstance(String title) {
        MedicationContainerFragment fragment = new MedicationContainerFragment();
        Bundle args = new Bundle();
        args.putSerializable(PARAM1, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        mToolbarControllerCallback = (ToolbarControllerInterface) context;
        mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.GONE);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pageTitle = (String) getArguments().getSerializable(PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_medication_container, container, false);
        ImageButton ibToolbarBackButton = view.findViewById(R.id.ib_toolbar_back_button);
        ibToolbarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mToolbarControllerCallback.customToolbarBackButtonClicked();
            }
        });
        ViewPager mViewPager = view.findViewById(R.id.container);
        TabLayout tabs = view.findViewById(R.id.tabs);
        tabs.bringToFront();
        MedicationsSectionsPagerAdapter mAdapter
                = new MedicationsSectionsPagerAdapter(getChildFragmentManager(),
                mContext);
        mViewPager.setAdapter(mAdapter);
        tabs.setupWithViewPager(mViewPager);
        return view;
    }

    private class MedicationsSectionsPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<String> mFragmentTitles = new ArrayList<>();

        private MedicationsSectionsPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            mFragmentTitles.add(context.getString(R.string.active));//val : 1
            mFragmentTitles.add(context.getString(R.string.title_all));//val : 2
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return MedicationFragment.newInstance("Active",pageTitle);
            else
                return MedicationFragment.newInstance("All",pageTitle);
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

    @Override
    public void onDetach() {
        super.onDetach();
        mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.VISIBLE);
    }
}
