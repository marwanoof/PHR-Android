package om.gov.moh.phr.adapters;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import om.gov.moh.phr.apimodels.ApiSlotsHolder;
import om.gov.moh.phr.fragments.AppointmentRescheduleDateFragment;

public class RescheduleDatesViewAdapter extends FragmentStatePagerAdapter {

    private final ApiSlotsHolder mResponseHolder;

    private int mPagesCount = 1;
    private String mEstCode;
    private String mReservationId;

    public RescheduleDatesViewAdapter(FragmentManager fm, ApiSlotsHolder responseHolder, String estCode, String reservationId) {
        super(fm);
        mResponseHolder = responseHolder;
        mEstCode = estCode;
        mReservationId = reservationId;
       // Log.d("re-runId", " RescheduleDatesViewAdapter:" + responseHolder.getResult().getSlotsArrayList().get(0).getTimeBlock() + " / " + responseHolder.getResult().getSlotsArrayList().get(0).getRunId());

    }

    @Override
    public Fragment getItem(int i) {
        return AppointmentRescheduleDateFragment.newInstance(mResponseHolder, mEstCode, mReservationId);
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
