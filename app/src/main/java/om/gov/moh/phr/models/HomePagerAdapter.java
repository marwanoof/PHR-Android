package om.gov.moh.phr.models;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import om.gov.moh.phr.R;
import om.gov.moh.phr.fragments.AppointmentsListFragment;
import om.gov.moh.phr.fragments.ChatFragment;
import om.gov.moh.phr.fragments.HomeFragment;
import om.gov.moh.phr.fragments.NotificationsFragment;
import om.gov.moh.phr.fragments.VitalInfoFragment;

public class HomePagerAdapter extends FragmentPagerAdapter {
    private String[] mFragmentTitles = new String[4];
    private Fragment[] mFragmentsArray = new Fragment[4];

    public HomePagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mFragmentTitles[0] = (context.getString(R.string.title_home));//val : 1
        mFragmentTitles[1] = (context.getString(R.string.title_appointments));//val : 2
        mFragmentTitles[2] = (context.getString(R.string.title_notification));//val : 3
        mFragmentTitles[3] = (context.getString(R.string.title_chat));//val : 4


        mFragmentsArray[0] = new HomeFragment().newInstance();//val : 1
        mFragmentsArray[1] = new AppointmentsListFragment().newInstance();//val : 2
        mFragmentsArray[2] = new NotificationsFragment().newInstance();//val : 3
        mFragmentsArray[3] = new ChatFragment().newInstance();//val : 4


    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        //return newIns of RequestListFragment
//        PlaceholderFragment.newInstance(position + 1);
        return mFragmentsArray[position];

    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitles[position];
    }

    @Override
    public int getCount() {
        return mFragmentTitles.length;
    }


}