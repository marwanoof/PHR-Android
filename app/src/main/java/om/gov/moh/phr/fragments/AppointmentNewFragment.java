package om.gov.moh.phr.fragments;


import android.content.Context;
import android.content.SharedPreferences;
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

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import om.gov.moh.phr.R;
import om.gov.moh.phr.adapters.DateItemsGridViewAdapter;
import om.gov.moh.phr.adapters.DatesViewPagerAdapter;
import om.gov.moh.phr.apimodels.ApiAppointmentClinicsHolder;
import om.gov.moh.phr.apimodels.ApiAppointmentDepartmentsHolder;
import om.gov.moh.phr.apimodels.ApiDemographicsHolder;
import om.gov.moh.phr.apimodels.ApiHomeHolder;
import om.gov.moh.phr.apimodels.ApiSlotsHolder;
import om.gov.moh.phr.interfaces.AppointmentsListInterface;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.MyProgressDialog;
import om.gov.moh.phr.models.NonSwipeableViewPager;

import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_MESSAGE;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_ARABIC;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_PREFS;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_SELECTED;


public class AppointmentNewFragment extends Fragment {


    private static final String API_URL_GET_DEMOGRAPHICS_INFO = API_NEHR_URL + "demographics/civilId/";
    private static final int NUMBER_OF_COLUMNS = 3;


    private Context mContext;

    private DateItemsGridViewAdapter mDateAdapter;
    private int mCurrentItem = 0;
    private MediatorInterface mMediatorCallback;
    private Spinner spnrDepartment;
    private Spinner spnrClinic;
    private Spinner spnrDatesRange;
    private Spinner spnrHospital;
    private MyProgressDialog mProgressDialog;
    private RequestQueue mQueue;
    private String mEstCode;
    private String mClinicId;
    private ImageButton ibNext;
    private ImageButton ibPrevious;
    private NonSwipeableViewPager vpContainer;
    private String mAppointmentPeriod;
    private TextView tvAppointmentsLabel;
    // private View vAppointmentContainer;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private AppointmentsListInterface mListener;
    private CardView vAppointmentContainer;
    private static final String PARAM1 = "PARAM1";
    private ArrayList<ApiHomeHolder.Patients> arrayList;
    private View parentView;

    public AppointmentNewFragment() {
        // Required empty public constructor
    }

    public static AppointmentNewFragment newInstance(ArrayList<ApiHomeHolder.Patients> patients) {
        AppointmentNewFragment fragment = new AppointmentNewFragment();
        Bundle args = new Bundle();
        args.putSerializable(PARAM1, patients);
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
            arrayList = (ArrayList<ApiHomeHolder.Patients>) getArguments().getSerializable(PARAM1);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         parentView = inflater.inflate(R.layout.fragment_appointment, container, false);

        mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
        mProgressDialog = new MyProgressDialog(mContext);
        spnrHospital = parentView.findViewById(R.id.spnr_hospital);
        spnrDepartment = parentView.findViewById(R.id.spnr_department);
        spnrClinic = parentView.findViewById(R.id.spnr_clinic);
        spnrDatesRange = parentView.findViewById(R.id.spnr_date_range);
        ibNext = parentView.findViewById(R.id.ib_next);
        ibPrevious = parentView.findViewById(R.id.ib_previous);
        vpContainer = parentView.findViewById(R.id.vp_container);
        tvAppointmentsLabel = parentView.findViewById(R.id.tv_select_label);
        vAppointmentContainer = parentView.findViewById(R.id.appointmentDate_cardView);

        //simple toolbar
        ImageButton ibToolbarBackButton = parentView.findViewById(R.id.ib_toolbar_back_button);
        ibToolbarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mToolbarControllerCallback.customToolbarBackButtonClicked();
            }
        });
        TextView tvToolBarTitle = parentView.findViewById(R.id.tv_toolbar_title);
        tvToolBarTitle.setText(getString(R.string.title_appointments));
        tvToolBarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mToolbarControllerCallback.customToolbarBackButtonClicked();
            }
        });
        setAppointmentGroupVisibility(View.GONE);
        getDemographicResponse();


        return parentView;
    }

    private void setupSelectDateRangeSpinner() {
        final ArrayList<String> datesRange = new ArrayList<>();
        datesRange.add(getResources().getString(R.string.after_10_days));
        datesRange.add(getResources().getString(R.string.after_15_days));
        datesRange.add(getResources().getString(R.string.after_20_days));
        datesRange.add(getResources().getString(R.string.after_25_days));
        datesRange.add(getResources().getString(R.string.after_30_days));

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
                getSlots();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    private void viewPagerPrevious() {
        /*if (mCurrentItem > 0) {
            mCurrentItem--;
            vpContainer.setCurrentItem(mCurrentItem);
        }*/

        int next = Integer.parseInt(mAppointmentPeriod) - 5;
        if (next >= 10) {
            mAppointmentPeriod = String.valueOf(next);
            getSlots();
        }
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

        getSlots();
    }

    private void getDemographicResponse() {
        setupSelectHospitalSpinner(arrayList);
        /*mProgressDialog.showDialog();

        String fullUrl = API_URL_GET_DEMOGRAPHICS_INFO + mMediatorCallback.getCurrentUser().getCivilId() + "?source=PHR";
//        String fullUrl = API_URL_GET_DEMOGRAPHICS_INFO + "62163078" + "?source=PHR";
        Log.d("fullURL-getDemo", fullUrl);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fullUrl, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt(API_RESPONSE_CODE) == 0) {
                        Gson gson = new Gson();
                        ApiDemographicsHolder responseHolder = gson.fromJson(response.toString(), ApiDemographicsHolder.class);
                        Log.d("appointmentFrag", "-Demo" + response.getJSONObject("result").toString());

                        setupSelectHospitalSpinner(responseHolder.getmResult());


                    } else {

                        mProgressDialog.dismissDialog();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mProgressDialog.dismissDialog();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("resp-demographic", error.toString());
                error.printStackTrace();
                mProgressDialog.dismissDialog();
            }
        }) {
            //
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
//                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", API_GET_TOKEN_BEARER + mMediatorCallback.getAccessToken().getAccessTokenString());
                return headers;
            }

        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);

        mQueue.add(jsonObjectRequest);*/
    }

    private void setupSelectHospitalSpinner(final ArrayList<ApiHomeHolder.Patients> demographicItem) {
        final ArrayList<ApiHomeHolder.Patients> institutes = new ArrayList<>();
        ArrayList<String> institutesNames = new ArrayList<>();
        institutesNames.add(0, getString(R.string.title_select_institute));
        for (ApiHomeHolder.Patients patients : demographicItem) {
            if (patients.getEstTypeCode() == 106) {
                institutes.add(patients);
                if (getStoredLanguage().equals(LANGUAGE_ARABIC) && !patients.getEstNameNls().isEmpty())
                    institutesNames.add(patients.getEstNameNls());
                else
                    institutesNames.add(patients.getEstName());
            }
        }


        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                mContext, android.R.layout.simple_spinner_dropdown_item, institutesNames) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrHospital.setAdapter(spinnerArrayAdapter);

        spnrHospital.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if (position > 0) {
                    // Notify the selected item text : selectedItemText

                    if (spnrDepartment.getAdapter() != null) {
                        spnrDepartment.setAdapter(null);
                    }

                    if (spnrClinic.getAdapter() != null) {
                        spnrClinic.setAdapter(null);
                    }
                    mEstCode = institutes.get(position - 1).getEstCode();
                    getDepartments(institutes.get(position - 1).getEstCode());

                    setAppointmentGroupVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void getDepartments(final String estCode) {


        String fullUrl = API_NEHR_URL + "appointment/getDepts?estCode=" + estCode;//Integer.parseInt(estCode);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fullUrl, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (isAdded() && mContext != null) {
                    try {

                        if (response.getInt(API_RESPONSE_CODE) == 0) {
                            Gson gson = new Gson();
                            ApiAppointmentDepartmentsHolder responseHolder = gson.fromJson(response.toString(), ApiAppointmentDepartmentsHolder.class);
                            setupSelectDepartmentSpinner(responseHolder.getResult(), estCode);

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
//                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", API_GET_TOKEN_BEARER + mMediatorCallback.getAccessToken().getAccessTokenString());
//                headers.put("estCode", TEST_EST_CODE);
                return headers;
            }

        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);

        mQueue.add(jsonObjectRequest);
    }

    private void setupSelectDepartmentSpinner(final ArrayList<ApiAppointmentDepartmentsHolder.Department> result, final String mEstCode) {
        ArrayList<String> departmentsNames = new ArrayList<>();
        for (ApiAppointmentDepartmentsHolder.Department d : result) {
            if (getStoredLanguage().equals(LANGUAGE_ARABIC) && !d.getDeptnameNl().isEmpty())
                departmentsNames.add(d.getDeptnameNl());
            else
                departmentsNames.add(d.getDeptName());
        }
        departmentsNames.add(0, getString(R.string.title_select_department));


        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                mContext, android.R.layout.simple_spinner_dropdown_item, departmentsNames) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrDepartment.setAdapter(spinnerArrayAdapter);

        spnrDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if (position > 0) {
                    // Notify the selected item text : selectedItemText
                    if (spnrClinic.getAdapter() != null) {
                        spnrClinic.setAdapter(null);
                    }
                    Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(android.R.id.content), "position/" + position, Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                            .show();
                    setAppointmentGroupVisibility(View.GONE);
                    getClinics(result.get(position - 1), mEstCode);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getClinics(ApiAppointmentDepartmentsHolder.Department department, String mEstCode) {

        String fullUrl = API_NEHR_URL + "appointment/getClinics?estCode=" + mEstCode + "&deptId=" + department.getDeptId();//Integer.parseInt(estCode);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fullUrl, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mContext != null && isAdded()) {
                    try {

                        if (response.getInt(API_RESPONSE_CODE) == 0) {
                            Gson gson = new Gson();
                            ApiAppointmentClinicsHolder responseHolder = gson.fromJson(response.toString(), ApiAppointmentClinicsHolder.class);
                            setupSelectClinicSpinner(responseHolder.getResult());

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
                headers.put("Authorization", API_GET_TOKEN_BEARER + mMediatorCallback.getAccessToken().getAccessTokenString());
                return headers;
            }

        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);

        mQueue.add(jsonObjectRequest);
    }

    private void setupSelectClinicSpinner(final ArrayList<ApiAppointmentClinicsHolder.Clinic> result) {
        final ArrayList<String> clinicsNames = new ArrayList<>();
        for (ApiAppointmentClinicsHolder.Clinic c : result) {
            if (getStoredLanguage().equals(LANGUAGE_ARABIC) && !c.getDoctdeptnameNl().isEmpty())
                clinicsNames.add(c.getDoctdeptnameNl());
            else
                clinicsNames.add(c.getDoctDeptName());
        }
        clinicsNames.add(0, getString(R.string.title_select_clinic));


        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                mContext, android.R.layout.simple_spinner_dropdown_item, clinicsNames) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrClinic.setAdapter(spinnerArrayAdapter);

        spnrClinic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if (position > 0) {
                    // Notify the selected item text : selectedItemText

                    mClinicId = result.get(position - 1).getDoctDeptId();

                    setAppointmentGroupVisibility(View.VISIBLE);
                    setupSelectDateRangeSpinner();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getSlots() {
        mProgressDialog.showDialog();

        String fullUrl = API_NEHR_URL + "appointment/getSlots";
//        String fullUrl = API_URL_GET_DEMOGRAPHICS_INFO + "62163078" + "?source=PHR";

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
                            final DatesViewPagerAdapter datesViewPagerAdapter =
                                    new DatesViewPagerAdapter(getChildFragmentManager(), responseHolder, mEstCode, mClinicId, mListener);
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
        params.put("estCode", mEstCode);
        params.put("clinicId", mClinicId);
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


    private void setAppointmentGroupVisibility(int visibility) {
        tvAppointmentsLabel.setVisibility(visibility);
        vAppointmentContainer.setVisibility(visibility);
        spnrDatesRange.setVisibility(visibility);
        vpContainer.setVisibility(visibility);
        ibNext.setVisibility(visibility);
        ibPrevious.setVisibility(visibility);
    }

    @Override
    public void onResume() {
        super.onResume();
        mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.GONE);
    }


    public void setAppointmentListListener(AppointmentsListInterface listener) {
        mListener = listener;

    }

    private String getStoredLanguage() {
        SharedPreferences sharedPref = mContext.getSharedPreferences(LANGUAGE_PREFS, Context.MODE_PRIVATE);
        return sharedPref.getString(LANGUAGE_SELECTED, getDeviceLanguage());
    }
    private String getDeviceLanguage() {
        return Locale.getDefault().getLanguage();
    }
}

