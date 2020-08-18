package om.gov.moh.phr.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import om.gov.moh.phr.apimodels.ApiSlotsHolder;
import om.gov.moh.phr.fragments.AppointmentDateFragment;
import om.gov.moh.phr.interfaces.AppointmentsListInterface;

public class DatesViewPagerAdapter extends FragmentStatePagerAdapter {
    private final ApiSlotsHolder mResponseHolder;
    private final String mEstCode;
    private final String mClinicId;
    private final AppointmentsListInterface mListener;
    private int mPagesCount = 1;

    public DatesViewPagerAdapter(FragmentManager fm, ApiSlotsHolder responseHolder, String estCode, String clinicId, AppointmentsListInterface listener) {
        super(fm);
        mResponseHolder = responseHolder;
        mEstCode = estCode;
        mClinicId = clinicId;
        mListener = listener;

    }

    @Override
    public Fragment getItem(int i) {
        AppointmentDateFragment fragment = AppointmentDateFragment.newInstance(mResponseHolder, mEstCode, mClinicId);
        fragment.setAppointmentListListener(mListener);
        return fragment;

    }

    @Override
    public int getCount() {
        return mPagesCount;
    }

    public void setViewPagerPagesCount(int pages) {
        mPagesCount = pages;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
