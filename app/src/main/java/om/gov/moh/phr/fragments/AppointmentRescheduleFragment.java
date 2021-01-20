package om.gov.moh.phr.fragments;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import om.gov.moh.phr.R;
import om.gov.moh.phr.adapters.RescheduleDatesViewAdapter;
import om.gov.moh.phr.apimodels.ApiAppointmentsListHolder;
import om.gov.moh.phr.apimodels.ApiSlotsHolder;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.GlobalMethodsKotlin;
import om.gov.moh.phr.models.MyProgressDialog;
import om.gov.moh.phr.models.NonSwipeableViewPager;


import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;
import static om.gov.moh.phr.models.MyConstants.PARAM_APPOINTMENTS;


public class AppointmentRescheduleFragment extends Fragment {

    private ApiAppointmentsListHolder.Appointments mAppointments;
    private Context mContext;
    private MediatorInterface mMediatorCallback;
    private ToolbarControllerInterface mToolbarControllerCallback;

    private MyProgressDialog mProgressDialog;
    private RequestQueue mQueue;

    private ImageButton ibNext;
    private ImageButton ibPrevious;
    private Spinner spnrDatesRange;
    private NonSwipeableViewPager vpContainer;
    private String mAppointmentPeriod;


    public AppointmentRescheduleFragment() {
        // Required empty public constructor
    }

    public static AppointmentRescheduleFragment newInstance(ApiAppointmentsListHolder.Appointments appointments) {
        AppointmentRescheduleFragment fragment = new AppointmentRescheduleFragment();
        Bundle args = new Bundle();
        args.putSerializable(PARAM_APPOINTMENTS, appointments);
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
            mAppointments = (ApiAppointmentsListHolder.Appointments) getArguments().getSerializable(PARAM_APPOINTMENTS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_appoitment_reschdule, container, false);

        mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
        mProgressDialog = new MyProgressDialog(mContext);
        spnrDatesRange = parentView.findViewById(R.id.spnr_date_range);
        ibNext = parentView.findViewById(R.id.ib_next);
        ibPrevious = parentView.findViewById(R.id.ib_previous);
        vpContainer = parentView.findViewById(R.id.vp_container);

        //simple toolbar
        ImageButton ibToolbarBackButton = parentView.findViewById(R.id.ib_toolbar_back_button);
        ibToolbarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mToolbarControllerCallback.customToolbarBackButtonClicked();
            }
        });

        TextView tvToolBarTitle = parentView.findViewById(R.id.tv_toolbar_title);
        tvToolBarTitle.setText(getString(R.string.title_reschedule));
        String title = mAppointments.getDescription() + mAppointments.getEstName();
        TextView tvAppointment = parentView.findViewById(R.id.tv_appointment);
        tvAppointment.setText(title);

        setupSelectDateRangeSpinner();

        return parentView;
    }

    private void setupSelectDateRangeSpinner() {
        final ArrayList<String> datesRange = new ArrayList<>();
        datesRange.add("After 10 days");
        datesRange.add("After 15 days");
        datesRange.add("After 20 days");
        datesRange.add("After 25 days");
        datesRange.add("After 30 days");

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                mContext, android.R.layout.simple_spinner_dropdown_item, datesRange) {
            @Override
            public boolean isEnabled(int position) {
               /* if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }*/
                return true;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
              /*  if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }*/
                tv.setTextColor(Color.BLACK);
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrDatesRange.setAdapter(spinnerArrayAdapter);

        spnrDatesRange.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
               /* if (position > 0) {
                    // Notify the selected item text : selectedItemText
                }*/
                mAppointmentPeriod = getAppointmentPeriod();
                if (mMediatorCallback.isConnected()) {
                    getSlots();
                } else {
                    GlobalMethodsKotlin.Companion.showAlertDialog(mContext, getResources().getString(R.string.no_internet_title), getResources().getString(R.string.alert_no_connection), getResources().getString(R.string.ok), R.drawable.ic_error);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    private void getSlots() {
        mProgressDialog.showDialog();
        String fullUrl = API_NEHR_URL + "appointment/getRescheduleSlots";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, fullUrl, getJSONRequestParams()
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mContext != null && isAdded()) {
                    try {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {
                            Gson gson = new Gson();
                            final ApiSlotsHolder responseHolder = gson.fromJson(response.toString(), ApiSlotsHolder.class);


                            vpContainer.setAdapter(null);
                            final RescheduleDatesViewAdapter datesViewPagerAdapter =
                                    new RescheduleDatesViewAdapter(getChildFragmentManager(), responseHolder, mAppointments.getEstCode(), mAppointments.getReservationId());
                            vpContainer.setAdapter(datesViewPagerAdapter);


                            final int pageCount = getPageCount(responseHolder.getResult().getSlotsArrayList());

                            ibNext.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    viewPagerNext();
                                }
                            });
                            ibPrevious.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    viewPagerPrevious();
                                }
                            });


                        } else {

                            mProgressDialog.dismissDialog();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    mProgressDialog.dismissDialog();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mContext != null && isAdded()) {
                    error.printStackTrace();
                    mProgressDialog.dismissDialog();
                }
            }
        }) {
            //
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);

        mQueue.add(jsonObjectRequest);
    }


    private void viewPagerNext() {

       /* datesViewPagerAdapter.setViewPagerPagesCount(pagesCount);
        datesViewPagerAdapter.notifyDataSetChanged();
        if (mCurrentItem < pagesCount) {
            mCurrentItem++;
            vpContainer.setCurrentItem(mCurrentItem);
        }*/

        int next = Integer.parseInt(mAppointmentPeriod) + 5;
        mAppointmentPeriod = String.valueOf(next);

        if (mMediatorCallback.isConnected()) {
            getSlots();
        } else {
            GlobalMethodsKotlin.Companion.showAlertDialog(mContext, getResources().getString(R.string.no_internet_title), getResources().getString(R.string.alert_no_connection), getResources().getString(R.string.ok), R.drawable.ic_error);
        }
    }

    private void viewPagerPrevious() {
        /*if (mCurrentItem > 0) {
            mCurrentItem--;
            vpContainer.setCurrentItem(mCurrentItem);
        }*/

        int next = Integer.parseInt(mAppointmentPeriod) - 5;
        if (next >= 10) {
            mAppointmentPeriod = String.valueOf(next);
            if (mMediatorCallback.isConnected()) {
                getSlots();
            } else {
                GlobalMethodsKotlin.Companion.showAlertDialog(mContext, getResources().getString(R.string.no_internet_title), getResources().getString(R.string.alert_no_connection), getResources().getString(R.string.ok), R.drawable.ic_error);
            }
        }
    }


    private int getPageCount(ArrayList<ApiSlotsHolder.Slot> slotsArrayList) {

        int count = 0;
        String day = "";
        String month = "";
        int size = slotsArrayList.size();
        for (int i = 0; i < size; i++) {
            ApiSlotsHolder.Slot slot = slotsArrayList.get(i);
            if (!slot.getAppointmentDay().equals(day) && !slot.getAppointmentMonth().equals(month)) {
                day = slot.getAppointmentDay();
                month = slot.getAppointmentMonth();
                count++;
            }
        }
        int pageCount;
        if (count < 5) {
            pageCount = 1;
        } else {
            pageCount = (count / 5) + count % 5;
        }
        return pageCount;
    }

    private JSONObject getJSONRequestParams() {
        Map<String, String> params = new HashMap<>();
        params.put("estCode", mAppointments.getEstCode());
        params.put("reservationId", mAppointments.getReservationId());
        params.put("period", mAppointmentPeriod);
        params.put("civilId", mMediatorCallback.getCurrentUser().getCivilId());
        return new JSONObject(params);

    }

    private String getAppointmentPeriod() {

        switch (spnrDatesRange.getSelectedItemPosition()) {

            case 0:
                return "10";
            case 1:
                return "15";
            case 2:
                return "20";
            case 3:
                return "25";
            case 4:
                return "30";
            default:
                return "2";
        }

//      return   spnrDatesRange.getSelectedItemPosition()+"0";

    }

    @Override
    public void onResume() {
        super.onResume();
        mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.GONE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
