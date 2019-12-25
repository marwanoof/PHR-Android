package om.gov.moh.phr.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import om.gov.moh.phr.R;
import om.gov.moh.phr.adapters.ProceduresReportsRecyclerView;
import om.gov.moh.phr.apimodels.ApiEncountersHolder;
import om.gov.moh.phr.apimodels.ApiProceduresReportsHolder;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.MyProgressDialog;

import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_MESSAGE;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_RESULT;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProceduresReportsFragment extends Fragment implements SearchView.OnQueryTextListener {

    private static final String API_URL_GET_ALL_PROCEDURES_REPORTS_INFO = API_NEHR_URL + "procedure/civilId/";
    private static final String API_URL_GET_PROC_HRD_INFO = API_NEHR_URL + "procedure/encounterId/";
    private static final String PRPCEDURE_KEY = "procedure";
    private static final String REPORT_NAME_KEY = "name";
    private static final String EST_NAME_KEY = "estName";
    private static final String PROFILE_CODE_KEY = "profileCode";
    private static final String START_TIME_KEY = "startTime";
    private static final String REPORTID_KEY = "reportId";
    private static final String PROCEDURE_DONEDATE_KEY = "procedureDoneDate";
    private static final String ARG_PARAM1 = "ARG_PARAM1";
    private RequestQueue mQueue;
    private MyProgressDialog mProgressDialog;
    private Context mContext;
    private MediatorInterface mMediatorCallback;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private RecyclerView rvProceduresReportsList;
    private TextView tvAlert;
    private ArrayList<ApiProceduresReportsHolder> reportsArrayList;
    private ProceduresReportsRecyclerView mAdapter;
    private View view;
    private ApiEncountersHolder.Encounter encounterInfo;

    public ProceduresReportsFragment() {
        // Required empty public constructor
    }

    public static ProceduresReportsFragment newInstance() {
        ProceduresReportsFragment fragment = new ProceduresReportsFragment();
        Bundle args = new Bundle();
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
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // if (view == null) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_procedures_reports, container, false);
        ImageButton ibToolbarBackButton = view.findViewById(R.id.ib_toolbar_back_button);
        TextView tvTitle = view.findViewById(R.id.tv_Title);
        tvTitle.setText(getResources().getString(R.string.title_procedures_reports));
        mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
        mProgressDialog = new MyProgressDialog(mContext);
        tvAlert = view.findViewById(R.id.tv_alert);
        rvProceduresReportsList = view.findViewById(R.id.rv_reportsList);
        SearchView searchView = (SearchView) view.findViewById(R.id.sv_searchView);
        searchView.setOnQueryTextListener(this);
        if (mMediatorCallback.isConnected()) {
       if (encounterInfo != null) {
                tvTitle.setText(encounterInfo.getDepartmentArrayList().get(0) + ", " + encounterInfo.getEstShortName());
                searchView.setVisibility(View.GONE);
                String procHRDUrl = API_URL_GET_PROC_HRD_INFO + encounterInfo.getEncounterId();
                getProceduresReportsList(procHRDUrl);
                ibToolbarBackButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMediatorCallback.changeFragmentTo(HealthRecordDetailsFragment.newInstance(encounterInfo), encounterInfo.getEstFullname());
                    }
                });
            } else {
                String recentProceduresReportsUrl = API_URL_GET_ALL_PROCEDURES_REPORTS_INFO + mMediatorCallback.getCurrentUser().getCivilId() + "?data=recent";
                getProceduresReportsList(recentProceduresReportsUrl);
                ibToolbarBackButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mToolbarControllerCallback.customToolbarBackButtonClicked();
                    }
                });
            }

        } else {
            displayAlert(getString(R.string.alert_no_connection));
        }
       /* } else {
            ((ViewGroup) view.getParent()).removeView(view);
        }*/
        return view;
    }

    private void displayAlert(String msg) {
        rvProceduresReportsList.setVisibility(View.GONE);
        tvAlert.setVisibility(View.VISIBLE);
        tvAlert.setText(msg);
    }

    private void getProceduresReportsList(String url) {
        mProgressDialog.showDialog();


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt(API_RESPONSE_CODE) == 0) {
                        JSONArray jsonElements = response.getJSONArray(API_RESPONSE_RESULT);
                        reportsArrayList = new ArrayList<>();
                        for (int i = 0; i < jsonElements.length(); i++) {
                            JSONObject jsonObject = jsonElements.getJSONObject(i);
                            ApiProceduresReportsHolder reportDetails = new ApiProceduresReportsHolder();

                                String ReportName = jsonObject.getJSONArray(PRPCEDURE_KEY).getJSONObject(0).getString(REPORT_NAME_KEY);
                                reportDetails.setName(ReportName);
                                reportDetails.setEstName(jsonObject.getString(EST_NAME_KEY));
                                reportDetails.setProfileCode(jsonObject.getInt(PROFILE_CODE_KEY));
                                reportDetails.setStartTime(jsonObject.getLong(START_TIME_KEY));
                                if (jsonObject.getInt(PROFILE_CODE_KEY) == 101) {
                                    reportDetails.setReportId(jsonObject.getString(REPORTID_KEY));
                                    reportDetails.setProcedureDoneDate(jsonObject.getLong(PROCEDURE_DONEDATE_KEY));
                                }
                            reportsArrayList.add(reportDetails);
                        }
                        setupRecyclerView(reportsArrayList);

                    } else {
                        displayAlert(response.getString(API_RESPONSE_MESSAGE));
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

        mQueue.add(jsonObjectRequest);
    }

    private void setupRecyclerView(ArrayList<ApiProceduresReportsHolder> getmResult) {
        mAdapter = new ProceduresReportsRecyclerView(mMediatorCallback, getmResult, mContext, false);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(rvProceduresReportsList.getContext(),
                layoutManager.getOrientation());
        rvProceduresReportsList.addItemDecoration(mDividerItemDecoration);
        rvProceduresReportsList.setLayoutManager(layoutManager);
        rvProceduresReportsList.setItemAnimator(new DefaultItemAnimator());
        rvProceduresReportsList.setAdapter(mAdapter);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mMediatorCallback.changeFragmentContainerVisibility(View.GONE, View.VISIBLE);
        mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.VISIBLE);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (mAdapter != null)
            mAdapter.filter(newText);
        return false;
    }

}
