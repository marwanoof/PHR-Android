package om.gov.moh.phr.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.LinearLayout;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import om.gov.moh.phr.R;
import om.gov.moh.phr.adapters.ProceduresReportsRecyclerView;
import om.gov.moh.phr.apimodels.ApiEncountersHolder;
import om.gov.moh.phr.apimodels.ApiLabOrdersListHolder;
import om.gov.moh.phr.apimodels.ApiMedicationHolder;
import om.gov.moh.phr.apimodels.ApiOtherDocsHolder;
import om.gov.moh.phr.apimodels.ApiProceduresReportsHolder;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.MyProgressDialog;

import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_RESULT;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProceduresReportsFragment extends Fragment implements SearchView.OnQueryTextListener{

    private static final String API_URL_GET_ALL_PROCEDURES_REPORTS_INFO = API_NEHR_URL + "procedure/groupByEncounters";
    //private static final String API_URL_GET_PROC_HRD_INFO = API_NEHR_URL + "procedure/encounterId/";
    private static final String PRPCEDURE_KEY = "procedure";
    private static final String REPORT_NAME_KEY = "name";
    private static final String EST_NAME_KEY = "estName";
    private static final String EST_FULL_NAME_KEY = "estFullname";
    private static final String PROFILE_CODE_KEY = "profileCode";
    private static final String START_TIME_KEY = "startTime";
    private static final String REPORTID_KEY = "reportId";
    private static final String PROCEDURE_DONEDATE_KEY = "procedureDoneDate";
    private static final String ARG_PARAM1 = "ARG_PARAM1";
    private static final String ARG_PARAM2 = "ARG_PARAM2";
    private static final String ARG_PARAM3 = "ARG_PARAM3";
    private static final String ARG_PARAM4 = "ARG_PARAM4";
    private RequestQueue mQueue;
    private MyProgressDialog mProgressDialog;
    private Context mContext;
    private MediatorInterface mMediatorCallback;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private RecyclerView rvProceduresReportsList;
    private TextView tvAlert;
    private ArrayList<ApiProceduresReportsHolder.ProceduresByEncounter> reportsArrayList;
    private ProceduresReportsRecyclerView mAdapter = new ProceduresReportsRecyclerView();
    private ApiEncountersHolder.Encounter encounterInfo;
    private ApiOtherDocsHolder.ApiDocInfo docInfo;
    private ApiProceduresReportsHolder.ProceduresByEncounter procedureInfo;
    //private SwipeRefreshLayout swipeRefreshLayout;
    private View view;
    private CardView noRecordCardView;
    private SearchView searchView;
    private String pageTitle;

    public ProceduresReportsFragment() {
        // Required empty public constructor
    }

    public static ProceduresReportsFragment newInstance(String title) {
        ProceduresReportsFragment fragment = new ProceduresReportsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM4, title);
        fragment.setArguments(args);
        return fragment;
    }

    public static ProceduresReportsFragment newInstance(ApiEncountersHolder.Encounter encounterObj) {
        ProceduresReportsFragment fragment = new ProceduresReportsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, encounterObj);
        fragment.setArguments(args);
        return fragment;
    }

    public static ProceduresReportsFragment newInstance(ApiOtherDocsHolder.ApiDocInfo docInfo) {
        ProceduresReportsFragment fragment = new ProceduresReportsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM2, docInfo);
        fragment.setArguments(args);
        return fragment;
    }

    public static ProceduresReportsFragment newInstance(ApiProceduresReportsHolder.ProceduresByEncounter procedureInfo) {
        ProceduresReportsFragment fragment = new ProceduresReportsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM3, procedureInfo);
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().getSerializable(ARG_PARAM1) != null)
            encounterInfo = (ApiEncountersHolder.Encounter) getArguments().getSerializable(ARG_PARAM1);
        if (getArguments().getSerializable(ARG_PARAM2) != null)
            docInfo = (ApiOtherDocsHolder.ApiDocInfo) getArguments().getSerializable(ARG_PARAM2);
        if (getArguments().getSerializable(ARG_PARAM3) != null)
            procedureInfo = (ApiProceduresReportsHolder.ProceduresByEncounter) getArguments().getSerializable(ARG_PARAM3);
        if (getArguments().getSerializable(ARG_PARAM4) != null)
            pageTitle = (String) getArguments().getSerializable(ARG_PARAM4);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_procedures_reports, container, false);

            TextView tvTitle = view.findViewById(R.id.tv_Title);
            tvTitle.setText(pageTitle);
            mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
            mProgressDialog = new MyProgressDialog(mContext);
            tvAlert = view.findViewById(R.id.tv_alert);
            rvProceduresReportsList = view.findViewById(R.id.rv_reportsList);
            searchView = view.findViewById(R.id.sv_searchView);
            searchView.setOnQueryTextListener(this);
            noRecordCardView = view.findViewById(R.id.cardViewNoRecords);
            noRecordCardView.setVisibility(View.GONE);
            //swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
            if (encounterInfo != null || docInfo != null || procedureInfo!=null) {
                tvTitle.setVisibility(View.GONE);
                searchView.setVisibility(View.GONE);
            }
            if (mMediatorCallback.isConnected()) {
                if (encounterInfo != null) {
                    getProceduresReportsList(API_URL_GET_ALL_PROCEDURES_REPORTS_INFO,"","",encounterInfo.getEncounterId());

                } else if (docInfo != null) {

                    getProceduresReportsList(API_URL_GET_ALL_PROCEDURES_REPORTS_INFO,"","",docInfo.getEncounterId());

                } else if (procedureInfo != null) {

                    getProceduresReportsList(API_URL_GET_ALL_PROCEDURES_REPORTS_INFO,"","",procedureInfo.getEncounterId());

                }else {

                    getProceduresReportsList(API_URL_GET_ALL_PROCEDURES_REPORTS_INFO,"recent","","");

                }
                /*swipeRefreshLayout.setOnRefreshListener(this);
                swipeRefreshLayout.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                swipeRefreshLayout.setRefreshing(true);
                                                rvProceduresReportsList.setVisibility(View.VISIBLE);
                                                tvAlert.setVisibility(View.GONE);
                                                if (encounterInfo != null) {
                                                    String procHRDUrl = API_URL_GET_PROC_HRD_INFO + encounterInfo.getEncounterId();
                                                    getProceduresReportsList(procHRDUrl);
                                                } else if (docInfo != null) {
                                                    String procHRDUrl = API_URL_GET_PROC_HRD_INFO + docInfo.getEncounterId();
                                                    getProceduresReportsList(procHRDUrl);

                                                } else if (procedureInfo != null) {
                                                    String procHRDUrl = API_URL_GET_PROC_HRD_INFO + procedureInfo.getEncounterId();
                                                    getProceduresReportsList(procHRDUrl);

                                                } else {
                                                    String recentProceduresReportsUrl = API_URL_GET_ALL_PROCEDURES_REPORTS_INFO + mMediatorCallback.getCurrentUser().getCivilId() + "?data=recent";
                                                    getProceduresReportsList(recentProceduresReportsUrl);
                                                }
                                            }
                                        }
                );*/
            } else {
                displayAlert(getString(R.string.alert_no_connection));
            }
        } else {
            if (view.getParent() != null)
                ((ViewGroup) view.getParent()).removeView(view);
        }
        return view;
    }

    private void displayAlert(String msg) {
        noRecordCardView.setVisibility(View.VISIBLE);
        searchView.setVisibility(View.GONE);
        rvProceduresReportsList.setVisibility(View.GONE);
        tvAlert.setVisibility(View.VISIBLE);
        tvAlert.setText(msg);
    }

    private void getProceduresReportsList(String url, final String data, String source, final String encounterId) {
        mProgressDialog.showDialog();
        //swipeRefreshLayout.setRefreshing(true);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, getJSONRequestParams(mMediatorCallback.getCurrentUser().getCivilId(),data,source)
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                if (isAdded() && mContext != null) {
                    try {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {
                            Gson gson = new Gson();
                            ApiProceduresReportsHolder responseHolder = gson.fromJson(response.toString(), ApiProceduresReportsHolder.class);
                            reportsArrayList = responseHolder.getResult();
                            ArrayList<ApiProceduresReportsHolder.ProceduresByEncounter> filteredLabInfo = new ArrayList<>();
                            if (!encounterId.isEmpty()){
                                for (ApiProceduresReportsHolder.ProceduresByEncounter proceduresInfo:reportsArrayList){
                                    if (proceduresInfo.getEncounterId().equals(encounterId)){
                                        filteredLabInfo.add(proceduresInfo);
                                    }
                                }
                                if (filteredLabInfo.size() > 0)
                                    setupRecyclerView(filteredLabInfo,false);
                                else
                                    displayAlert(getResources().getString(R.string.no_records_proc_encounter));
                            }else {
                                setupRecyclerView(reportsArrayList,true);
                            }


                        } else {
                            if (data.equals("all"))
                                displayAlert(getResources().getString(R.string.no_records_proc_all));
                            else if (data.equals("recent"))
                                displayAlert(getResources().getString(R.string.no_records_proc_recent));
                            mProgressDialog.dismissDialog();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    mProgressDialog.dismissDialog();
                    //swipeRefreshLayout.setRefreshing(false);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("resp-demographic", error.toString());
                error.printStackTrace();
                mProgressDialog.dismissDialog();
                //swipeRefreshLayout.setRefreshing(false);
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

        mQueue.add(jsonObjectRequest);
    }
    private JSONObject getJSONRequestParams(String civilId, String data, String source) {
        Map<String, Object> params = new HashMap<>();
        params.put("civilId", Long.parseLong(civilId));
        params.put("data", data);
        params.put("source", source);
        return new JSONObject(params);
    }
    private void setupRecyclerView(ArrayList<ApiProceduresReportsHolder.ProceduresByEncounter> getmResult, Boolean showVisitDate) {
        mAdapter = new ProceduresReportsRecyclerView(mMediatorCallback, getmResult, mContext, false, showVisitDate);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
/*        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(rvProceduresReportsList.getContext(),
                layoutManager.getOrientation());
        rvProceduresReportsList.addItemDecoration(mDividerItemDecoration);*/
        rvProceduresReportsList.setLayoutManager(layoutManager);
        rvProceduresReportsList.setItemAnimator(new DefaultItemAnimator());
        rvProceduresReportsList.setAdapter(mAdapter);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (encounterInfo == null && docInfo == null && procedureInfo==null) {
            mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.VISIBLE);
        }
    }
    private void updateRecyclerViewItems(ArrayList<ApiProceduresReportsHolder.ProceduresByEncounter> result) {
        mAdapter.updateItemsListFiltered(result);
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if (s.length() == 0){
            updateRecyclerViewItems(reportsArrayList);
        }else {
            ArrayList<ApiProceduresReportsHolder.ProceduresByEncounter> filteredList = new ArrayList<>();
            for (int i = 0;i< reportsArrayList.size();i++) {
                for (int j = 0; j< reportsArrayList.get(i).getProcedures().size();j++){
                    if (reportsArrayList.get(i).getProcedures().get(j).getProcedure().get(0).getName().toLowerCase().contains(s))
                    {
                        filteredList.add(reportsArrayList.get(i));
                    }
                }


            }
            updateRecyclerViewItems(filteredList);
        }

        return false;
    }

    /*@Override
    public void onRefresh() {
        rvProceduresReportsList.setVisibility(View.VISIBLE);
        tvAlert.setVisibility(View.GONE);
        if (encounterInfo != null) {
            String procHRDUrl = API_URL_GET_PROC_HRD_INFO + encounterInfo.getEncounterId();
            getProceduresReportsList(procHRDUrl);
        } else if (docInfo != null) {
            String procHRDUrl = API_URL_GET_PROC_HRD_INFO + docInfo.getEncounterId();
            getProceduresReportsList(procHRDUrl);

        }  else if (procedureInfo != null) {
            String procHRDUrl = API_URL_GET_PROC_HRD_INFO + procedureInfo.getEncounterId();
            getProceduresReportsList(procHRDUrl);

        }else {
            String recentProceduresReportsUrl = API_URL_GET_ALL_PROCEDURES_REPORTS_INFO + mMediatorCallback.getCurrentUser().getCivilId() + "?data=recent";
            getProceduresReportsList(recentProceduresReportsUrl);
        }
    }*/
}
