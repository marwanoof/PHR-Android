package om.gov.moh.phr.fragments;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import om.gov.moh.phr.R;
import om.gov.moh.phr.adapters.DateItemsGridViewAdapter;
import om.gov.moh.phr.adapters.TimeItemsRecyclerViewAdapter;
import om.gov.moh.phr.apimodels.ApiSlotsHolder;
import om.gov.moh.phr.dialogfragments.SaveAppointmentDialogFragment;
import om.gov.moh.phr.interfaces.AdapterToFragmentConnectorInterface;
import om.gov.moh.phr.interfaces.AppointmentsListInterface;
import om.gov.moh.phr.interfaces.DialogFragmentInterface;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.CustomSlot;

import static om.gov.moh.phr.models.MyConstants.PARAM_EST_CODE;


public class AppointmentDateFragment extends Fragment implements AdapterToFragmentConnectorInterface {

    private static final int NUMBER_OF_COLUMNS = 5;
    private static final String PARAM_SLOTS_HOLDER = "PARAM_SLOTS_HOLDER";
    private static final String PARAM_CLINIC_ID = "PARAM_CLINIC_ID";
    private static String mEstCode;
    private Context mContext;
    private DateItemsGridViewAdapter mAdapter;
    private int mActiveDayIndex = -1;
    private ApiSlotsHolder mSlotsHolder;
    private MediatorInterface mMediatorCallback;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private TimeItemsRecyclerViewAdapter mTimeAdapter;
    private SaveAppointmentDialogFragment mSaveAppointmentDialogFragment;
    private String mClinicId;
    private AppointmentsListInterface mListener;

    public AppointmentDateFragment() {
        // Required empty public constructor
    }


    public static AppointmentDateFragment newInstance(ApiSlotsHolder responseHolder, String estCode, String clinicId) {
        AppointmentDateFragment fragment = new AppointmentDateFragment();
        Bundle args = new Bundle();
        args.putSerializable(PARAM_SLOTS_HOLDER, responseHolder);
        args.putString(PARAM_EST_CODE, estCode);
        args.putString(PARAM_CLINIC_ID, clinicId);
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
            mSlotsHolder = (ApiSlotsHolder) getArguments().getSerializable(PARAM_SLOTS_HOLDER);
            mEstCode = getArguments().getString(PARAM_EST_CODE);
            mClinicId = getArguments().getString(PARAM_CLINIC_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_appointment_date, container, false);

        final GridView gvDates = parentView.findViewById(R.id.gv_grid);

        gvDates.setAdapter(new DateItemsGridViewAdapter(getCustomSlotsArrayList(), mContext));
        gvDates.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            View viewPrev;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View vCircleOutline;
                View vCircle;
                TextView tvDay;
                try {
                    if (mActiveDayIndex != -1) {
                        //change to default color
                        viewPrev = gvDates.getChildAt(mActiveDayIndex);
                        vCircleOutline = viewPrev.findViewById(R.id.v_circle_outline);
                        vCircle = viewPrev.findViewById(R.id.v_circle);
                        tvDay = viewPrev.findViewById(R.id.tv_title);
                        vCircleOutline.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.colorTimeItemGray));
                        vCircle.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.colorWhite));
                        tvDay.setTextColor(getResources().getColor(R.color.colorTextViewFontNormal));
                    }
                    mActiveDayIndex = position;

                    /* if (mActiveDayIndex == position)*/
                    {
                        //change to active color
                        vCircleOutline = view.findViewById(R.id.v_circle_outline);
                        vCircle = view.findViewById(R.id.v_circle);
                        tvDay = view.findViewById(R.id.tv_title);
                        vCircleOutline.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.colorRed));
                        vCircle.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.colorRed));
                        tvDay.setTextColor(getResources().getColor(R.color.colorWhite));
                    }

                    mTimeAdapter.updateList((CustomSlot) parent.getItemAtPosition(position));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        gvDates.setVerticalScrollBarEnabled(false);
        gvDates.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return event.getAction() == MotionEvent.ACTION_MOVE;
            }

        });


        RecyclerView rvTimeGrid = parentView.findViewById(R.id.recycler_view);
        setRecyclerViewGrid(rvTimeGrid);


        return parentView;
    }


    private ArrayList<CustomSlot> getCustomSlotsArrayList() {
        ArrayList<CustomSlot> slots = new ArrayList<>();
        String day = "";
        String month = "";
        CustomSlot customSlot = new CustomSlot();
        int size = mSlotsHolder.getResult().getSlotsArrayList().size();

        for (int i = 0; i < size; i++) {
            ApiSlotsHolder.Slot slot = mSlotsHolder.getResult().getSlotsArrayList().get(i);
            if (slot.getAppointmentDay().equals(day) && slot.getAppointmentMonth().equals(month)) {
                customSlot.addTimeBlock(slot.getTimeBlock());
                customSlot.addRunId(slot.getRunId());
                if (i == size - 1) {
                    //if last index add custom slot to the arrayList
                    slots.add(customSlot);
                }
            } else {
                if (customSlot.getAppointmentDay() != null) {
                    slots.add(customSlot);
                }
                day = slot.getAppointmentDay();
                month = slot.getAppointmentMonth();

                customSlot = new CustomSlot();
                customSlot.setAppointmentDay(day);
                customSlot.setAppointmentMonth(month);
                customSlot.setAppointmentDate(slot.getAppointmentDate());
                customSlot.addTimeBlock(slot.getTimeBlock());
                customSlot.addRunId(slot.getRunId());

                Log.d("re-runId", " new getCustomSlotsArrayList:" + slot.getTimeBlock() + " / " + slot.getRunId());

            }

        }
        return slots;
    }

    private void setRecyclerViewGrid(RecyclerView recyclerView) {

        mTimeAdapter = new TimeItemsRecyclerViewAdapter(AppointmentDateFragment.this, mContext);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, NUMBER_OF_COLUMNS));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mTimeAdapter);
    }

    @Override
    public <T> void onMyListItemClicked(T dataToPass, String dataTitle) {
        CustomSlot customSlot = (CustomSlot) dataToPass;
        showSaveAppointmentDialog(mEstCode, customSlot.getSelectedRunId(), mSlotsHolder.getResult().getPatientId(), mClinicId, customSlot.getAppointmentDate(), customSlot.getSelectedTime());
        Log.d("saveAppointment", "onMyListItemClicked : " + mEstCode + "/" + customSlot.getSelectedRunId() + "/" + mSlotsHolder.getResult().getPatientId() + "/" + mClinicId);

    }

    @Override
    public <T> void onMyListItemClicked(T dataToPass, String dataTitle, int position) {

    }

    private void showSaveAppointmentDialog(String estCode, String runId, String patientId, String clinicId, String date, String time) {
        mSaveAppointmentDialogFragment = SaveAppointmentDialogFragment.newInstance(estCode, runId, patientId, clinicId, date, time, AppointmentDateFragment.class.getSimpleName());
        mSaveAppointmentDialogFragment.setDialogFragmentListener(new DialogFragmentInterface() {

            @Override
            public void onAccept() {
              //  onDecline();

                mMediatorCallback.slideTo(0);

                mToolbarControllerCallback.customToolbarBackButtonClicked();
                mMediatorCallback.changeFragmentContainerVisibility(View.GONE, View.VISIBLE);
                mListener.onItemsChanged(-1);


                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms

                        mMediatorCallback.slideTo(1);
                    }
                }, 1000);
            }

            @Override
            public void onAccept(int position) {

            }

            @Override
            public void onDecline() {
                mSaveAppointmentDialogFragment.dismiss();

            }
        });
        mSaveAppointmentDialogFragment.show(getChildFragmentManager(), SaveAppointmentDialogFragment.class.getSimpleName());
    }


    @Override
    public void onResume() {
        super.onResume();
        mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.GONE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
   /*     mMediatorCallback.changeFragmentContainerVisibility(View.GONE, View.VISIBLE);
        mMediatorCallback.slideTo(0);*/
    }

    public void setAppointmentListListener(AppointmentsListInterface listener) {
        mListener = listener;

    }

}
