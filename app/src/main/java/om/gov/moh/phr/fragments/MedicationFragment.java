package om.gov.moh.phr.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

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
import om.gov.moh.phr.adapters.MeasurementRecyclerViewAdapter;
import om.gov.moh.phr.adapters.MedicationRecyclerViewAdapter;
import om.gov.moh.phr.adapters.OrderLabRecyclerViewAdapter;
import om.gov.moh.phr.apimodels.ApiEncountersHolder;
import om.gov.moh.phr.apimodels.ApiMedicationHolder;
import om.gov.moh.phr.apimodels.ApiOtherDocsHolder;
import om.gov.moh.phr.apimodels.ApiProceduresReportsHolder;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.GlobalMethodsKotlin;
import om.gov.moh.phr.models.MyProgressDialog;

import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_RESULT;

/**
 * A simple {@link Fragment} subclass.
 */
public class MedicationFragment extends Fragment implements SearchView.OnQueryTextListener {
    private static final String API_URL_GET_MEDICATIONS_INFO = API_NEHR_URL + "medicationOrder/groupByEncounters";
    private static final String API_URL_GET_MED_HRD_INFO = API_NEHR_URL + "medicationOrder/encounterId/";
    private static final String ARG_PARAM1 = "ARG_PARAM1";
    private static final String ARG_PARAM2 = "ARG_PARAM2";
    private static final String ARG_PARAM3 = "ARG_PARAM3";
    private static final String ARG_PARAM4 = "ARG_PARAM4";
    private static final String ARG_PARAM5 = "ARG_PARAM5";
    private RequestQueue mQueue;
    private MyProgressDialog mProgressDialog;
    private Context mContext;
    private MediatorInterface mMediatorCallback;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private RecyclerView rvMedicationList;
    private TextView tvAlert;
    private MedicationRecyclerViewAdapter mAdapter;
    private boolean isRecent = false;
    private String medicationType;
    private ApiEncountersHolder.Encounter encounterInfo;
    private ApiOtherDocsHolder.ApiDocInfo docInfo;
    private ApiProceduresReportsHolder.ProceduresByEncounter procedureObj;
    private ConstraintLayout swipeRefreshLayout;
    private ArrayList<ApiMedicationHolder.ApiMedicationInfo> medicationInfoArrayList;
    private CardView noRecordCardView;
    private SearchView searchView;
    private String pageTitle;

    public MedicationFragment() {
        // Required empty public constructor
    }

    public static MedicationFragment newInstance(String param1, String title) {
        MedicationFragment fragment = new MedicationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM5, title);
        fragment.setArguments(args);
        return fragment;
    }

    public static MedicationFragment newInstance(ApiOtherDocsHolder.ApiDocInfo docInfo) {
        MedicationFragment fragment = new MedicationFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM3, docInfo);
        fragment.setArguments(args);
        return fragment;
    }

    public static MedicationFragment newInstance(ApiEncountersHolder.Encounter encounterObj) {
        MedicationFragment fragment = new MedicationFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM2, encounterObj);
        fragment.setArguments(args);
        return fragment;
    }

    public static MedicationFragment newInstance(ApiProceduresReportsHolder.ProceduresByEncounter procedureObj) {
        MedicationFragment fragment = new MedicationFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM4, procedureObj);
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
        if (getArguments() != null) {
            if (getArguments().getString(ARG_PARAM1) != null) {
                medicationType = getArguments().getString(ARG_PARAM1);
                isRecent = medicationType.equals("Active");
            }
            if (getArguments().getString(ARG_PARAM5) != null) {
                pageTitle = (String) getArguments().getSerializable(ARG_PARAM5);
            }
            if (getArguments().getSerializable(ARG_PARAM2) != null)
                encounterInfo = (ApiEncountersHolder.Encounter) getArguments().getSerializable(ARG_PARAM2);
            if (getArguments().getSerializable(ARG_PARAM3) != null)
                docInfo = (ApiOtherDocsHolder.ApiDocInfo) getArguments().getSerializable(ARG_PARAM3);
            if (getArguments().getSerializable(ARG_PARAM4) != null)
                procedureObj = (ApiProceduresReportsHolder.ProceduresByEncounter) getArguments().getSerializable(ARG_PARAM4);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_medication, container, false);

        // TextView tvTitle = view.findViewById(R.id.tv_Title);
        // tvTitle.setText(pageTitle);

        mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
        mProgressDialog = new MyProgressDialog(mContext);
        tvAlert = view.findViewById(R.id.tv_alert);
        rvMedicationList = view.findViewById(R.id.rv_maedication);
        searchView = (SearchView) view.findViewById(R.id.sv_searchView);
        searchView.setOnQueryTextListener(this);
        //searchView.setEnabled(false);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        noRecordCardView = view.findViewById(R.id.cardViewNoRecords);
        noRecordCardView.setVisibility(View.GONE);

        if (medicationType != null) {

        } else {
            // tvTitle.setVisibility(View.GONE);
            searchView.setVisibility(View.GONE);
        }
        if (mMediatorCallback.isConnected()) {
            if (medicationType != null) {
                if (isRecent) {
                    //String recentMedicationUrl = API_URL_GET_MEDICATIONS_INFO + mMediatorCallback.getCurrentUser().getCivilId() + "?data=recent";
                    getMedicationList(API_URL_GET_MEDICATIONS_INFO, "recent", "", "");
                } else {
                    //String allMedicationUrl = API_URL_GET_MEDICATIONS_INFO + mMediatorCallback.getCurrentUser().getCivilId();
                    getMedicationList(API_URL_GET_MEDICATIONS_INFO, "all", "", "");
                }

            } else if (encounterInfo != null) {
                //String medHRDurl = API_URL_GET_MED_HRD_INFO + encounterInfo.getEncounterId();
                getMedicationList(API_URL_GET_MEDICATIONS_INFO, "", "", encounterInfo.getEncounterId());
            } else if (docInfo != null) {
                //String docUrl = API_URL_GET_MED_HRD_INFO + docInfo.getEncounterId();
                getMedicationList(API_URL_GET_MEDICATIONS_INFO, "", "", docInfo.getEncounterId());
            } else {
                // String procedureUrl = API_URL_GET_MED_HRD_INFO + procedureObj.getEncounterId();
                getMedicationList(API_URL_GET_MEDICATIONS_INFO, "", "", procedureObj.getEncounterId());
            }

            //swipeRefreshLayout.setOnRefreshListener(this);
            /*swipeRefreshLayout.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            swipeRefreshLayout.setRefreshing(true);
                                            rvMedicationList.setVisibility(View.VISIBLE);
                                            tvAlert.setVisibility(View.GONE);
                                            if (medicationType != null) {
                                                if (isRecent) {
                                                    String recentMedicationUrl = API_URL_GET_MEDICATIONS_INFO + mMediatorCallback.getCurrentUser().getCivilId() + "?data=recent";
                                                    getMedicationList(recentMedicationUrl);
                                                } else {
                                                    String allMedicationUrl = API_URL_GET_MEDICATIONS_INFO + mMediatorCallback.getCurrentUser().getCivilId();
                                                    getMedicationList(allMedicationUrl);
                                                }
                                            } else if(encounterInfo!=null) {
                                                String medHRDurl = API_URL_GET_MED_HRD_INFO + encounterInfo.getEncounterId();
                                                getMedicationList(medHRDurl);
                                            }else if(docInfo!=null) {
                                                String docUrl = API_URL_GET_MED_HRD_INFO + docInfo.getEncounterId();
                                                getMedicationList(docUrl);
                                            }else {
                                                String procedureUrl = API_URL_GET_MED_HRD_INFO + procedureObj.getEncounterId();
                                                getMedicationList(procedureUrl);
                                            }
                                        }
                                    }
            );*/

        } else {
            displayAlert(getString(R.string.alert_no_connection));
            GlobalMethodsKotlin.Companion.showAlertDialog(mContext, getResources().getString(R.string.no_internet_title), getResources().getString(R.string.alert_no_connection), getResources().getString(R.string.ok), R.drawable.ic_error);
        }
        return view;
    }

    private void displayAlert(String msg) {
        searchView.clearFocus();
        searchView.setEnabled(false);
        searchView.setVisibility(View.GONE);
        rvMedicationList.setVisibility(View.GONE);
        noRecordCardView.setVisibility(View.VISIBLE);
        tvAlert.setVisibility(View.VISIBLE);
        tvAlert.setText(msg);
    }

    private void getMedicationList(String url, final String data, String source, final String encounterId) {
        mProgressDialog.showDialog();
        //swipeRefreshLayout.setRefreshing(true);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, getJSONRequestParams(mMediatorCallback.getCurrentUser().getCivilId(), data, source)
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("MedicationResp", response.toString());
                if (mContext != null && isAdded()) {
                    try {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {

                            Gson gson = new Gson();
                            ApiMedicationHolder responseHolder = gson.fromJson(response.toString(), ApiMedicationHolder.class);
                            medicationInfoArrayList = responseHolder.getmResult();
                            ArrayList<ApiMedicationHolder.ApiMedicationInfo> filteredMedicationInfo = new ArrayList<>();
                            if (!encounterId.isEmpty()) {
                                for (ApiMedicationHolder.ApiMedicationInfo medicationInfo : medicationInfoArrayList) {
                                    if (medicationInfo.getEncounterId().equals(encounterId)) {
                                        filteredMedicationInfo.add(medicationInfo);
                                    }
                                }
                                if (filteredMedicationInfo.size() > 0)
                                    setupRecyclerView(filteredMedicationInfo, false);
                                else
                                    displayAlert(getResources().getString(R.string.no_records_med_encounter));
                            } else {
                                setupRecyclerView(medicationInfoArrayList, true);
                            }


                        } else {
                            if (data.equals("all"))
                                displayAlert(getResources().getString(R.string.no_records_med_all));
                            else if (data.equals("recent"))
                                displayAlert(getResources().getString(R.string.no_records_med_recent));
                            mProgressDialog.dismissDialog();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    mProgressDialog.dismissDialog();
                    // swipeRefreshLayout.setRefreshing(false);
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

    private JSONObject getJSONRequestParams(String civilId, String data, String source) {
        Map<String, Object> params = new HashMap<>();
        params.put("civilId", Long.parseLong(civilId));
        params.put("data", data);
        params.put("source", source);
        return new JSONObject(params);
    }

    private void setupRecyclerView(ArrayList<ApiMedicationHolder.ApiMedicationInfo> getmResult, Boolean showVisitDetails) {
        mAdapter =
                new MedicationRecyclerViewAdapter(getmResult, mContext, showVisitDetails);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(rvMedicationList.getContext(),
                layoutManager.getOrientation());
        //rvMedicationList.addItemDecoration(mDividerItemDecoration);
        rvMedicationList.setLayoutManager(layoutManager);
        rvMedicationList.setItemAnimator(new DefaultItemAnimator());
        rvMedicationList.setAdapter(mAdapter);

    }

    private void updateRecyclerViewItems(ArrayList<ApiMedicationHolder.ApiMedicationInfo> result) {
        if (mAdapter != null && result != null)
            mAdapter.updateItemsListFiltered(result);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (medicationType != null) {
            mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if (s.length() == 0) {
            updateRecyclerViewItems(medicationInfoArrayList);
        } else {
            ArrayList<ApiMedicationHolder.ApiMedicationInfo> filteredList = new ArrayList<>();
            for (int i = 0; i < medicationInfoArrayList.size(); i++) {
                for (int j = 0; j < medicationInfoArrayList.get(i).getMedication().size(); j++) {
                    if (medicationInfoArrayList.get(i).getMedication().get(j).getMedicineName().toLowerCase().contains(s)) {
                        filteredList.add(medicationInfoArrayList.get(i));
                    }
                }
            }
            updateRecyclerViewItems(filteredList);
        }

        return false;
    }

    @Override
    public void onResume() {
        super.onResume();

       /* if (mAdapter != null){
            mAdapter.updateItemsList();
        }*/


    }
/* @Override
    public void onRefresh() {
        rvMedicationList.setVisibility(View.VISIBLE);
        tvAlert.setVisibility(View.GONE);
        if (medicationType != null) {
            if (isRecent) {
                //String recentMedicationUrl = API_URL_GET_MEDICATIONS_INFO + mMediatorCallback.getCurrentUser().getCivilId() + "?data=recent";
                getMedicationList(API_URL_GET_MEDICATIONS_INFO,"recent","");
            } else {
                //String allMedicationUrl = API_URL_GET_MEDICATIONS_INFO + mMediatorCallback.getCurrentUser().getCivilId();
                getMedicationList(API_URL_GET_MEDICATIONS_INFO,"","");
            }
        } *//*else if(encounterInfo!=null){
            String medHRDurl = API_URL_GET_MED_HRD_INFO + encounterInfo.getEncounterId();
            getMedicationList(medHRDurl);
        }else if(docInfo!=null) {
            String docUrl = API_URL_GET_MED_HRD_INFO + docInfo.getEncounterId();
            getMedicationList(docUrl);
        }else {
            String procedureUrl = API_URL_GET_MED_HRD_INFO + procedureObj.getEncounterId();
            getMedicationList(procedureUrl);
        }*//*
    }*/
}
