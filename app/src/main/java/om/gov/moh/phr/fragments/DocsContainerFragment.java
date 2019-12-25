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
    public DocsContainerFragment() {
        // Required empty public constructor
    }

    public static DocsContainerFragment newInstance() {
        DocsContainerFragment fragment = new DocsContainerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_docs_container, container, false);
        ViewPager mViewPager = view.findViewById(R.id.container);
        TabLayout tabs = view.findViewById(R.id.tabs);
        tabs.bringToFront();
        DocsCategorySectionsPagerAdapter mDocsCategorySectionsPagerAdapter
                = new DocsCategorySectionsPagerAdapter(getChildFragmentManager(),
                mContext);
        mViewPager.setAdapter(mDocsCategorySectionsPagerAdapter);
        tabs.setupWithViewPager(mViewPager);

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
            if (position == 0)
              return   ProviderDocumentsFragment.newInstance();
            else
                return SelfDocsFragment.newInstance();

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
