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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import om.gov.moh.phr.R;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;

/**
 * A simple {@link Fragment} subclass.
 */
public class DocsContainerFragment extends Fragment {
    private Context mContext;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private MediatorInterface mMediatorCallback;
    private static final String ARG_PARAM1 = "ARG_PARAM1";
    private String pageTitle;
    public DocsContainerFragment() {
        // Required empty public constructor
    }

    public static DocsContainerFragment newInstance(String title) {
        DocsContainerFragment fragment = new DocsContainerFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1,title);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null) {
            if (getArguments().getSerializable(ARG_PARAM1) != null)
                pageTitle = (String) getArguments().getSerializable(ARG_PARAM1);

        }
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        mToolbarControllerCallback = (ToolbarControllerInterface) context;
        mMediatorCallback = (MediatorInterface) context;
        mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_docs_container, container, false);
        ImageButton ibToolbarBackButton = view.findViewById(R.id.ib_toolbar_back_button);
        ibToolbarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mToolbarControllerCallback.customToolbarBackButtonClicked();
            }
        });
    /*    final TextView tvAddDoc = view.findViewById(R.id.addDoc);
        tvAddDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediatorCallback.changeFragmentTo(AddDocFragment.newInstance(), getResources().getString(R.string.title_other_document));
            }
        });*/
        ViewPager mViewPager = view.findViewById(R.id.container);
        TabLayout tabs = view.findViewById(R.id.tabs);
        tabs.bringToFront();
        DocsCategorySectionsPagerAdapter mDocsCategorySectionsPagerAdapter
                = new DocsCategorySectionsPagerAdapter(getChildFragmentManager(),
                mContext);
        mViewPager.setAdapter(mDocsCategorySectionsPagerAdapter);
        tabs.setupWithViewPager(mViewPager);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
               // if(position==1)
                   // tvAddDoc.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
              //  if(position==1)
               // tvAddDoc.setVisibility(View.GONE);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }
    public class DocsCategorySectionsPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<String> mFragmentTitles = new ArrayList<>();

        private DocsCategorySectionsPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            mFragmentTitles.add(context.getString(R.string.doc_provider));//val : 1
            mFragmentTitles.add(context.getString(R.string.doc_self));//val : 2
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return ProviderDocumentsFragment.newInstance();
            }  else {
                return SelfDocsFragment.newInstance();
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
