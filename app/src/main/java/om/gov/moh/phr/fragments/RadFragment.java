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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
import om.gov.moh.phr.adapters.ApiRadiologyHolder;
import om.gov.moh.phr.adapters.ProceduresAdapterItem;
import om.gov.moh.phr.adapters.ProceduresReportsRecyclerView;
import om.gov.moh.phr.adapters.RadiologyRecyclerViewAdapter;
import om.gov.moh.phr.apimodels.ApiEncountersHolder;
import om.gov.moh.phr.apimodels.ApiOtherDocsHolder;
import om.gov.moh.phr.apimodels.ApiProceduresReportsHolder;
import om.gov.moh.phr.apimodels.Notification;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.DividerItemDecorator;
import om.gov.moh.phr.models.MyProgressDialog;

import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_RESULT;

/**
 * A simple {@link Fragment} subclass.
 */
public class RadFragment extends Fragment {
    private static final String API_URL_GET_RAD_HRD_INFO = API_NEHR_URL + "diagnosticOrder/encounterId/";
    private static final String API_URL_GET_RAD_NOTIFICATION_INFO = API_NEHR_URL + "diagnosticOrder/report/";
    private static final String ARG_PARAM1 = "ARG_PARAM1";
    private static final String ARG_PARAM2 = "ARG_PARAM2";
    private static final String ARG_PARAM3 = "ARG_PARAM3";
    private static final String ARG_NOTIFICATION = "ARG_NOTIFICATION";
    private static final String REPORTID_KEY = "reportId";
    private static final String EST_NAME_KEY = "estName";
    private RequestQueue mQueue;
    private MyProgressDialog mProgressDialog;
    private Context mContext;
    private MediatorInterface mMediatorCallback;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private RecyclerView rvProceduresReportsList;
    private TextView tvAlert;
    private ArrayList<ApiRadiologyHolder.Radiology> reportsArrayList;
    private ApiEncountersHolder.Encounter encounterInfo;
    private ApiOtherDocsHolder.ApiDocInfo docInfo;
    private Notification notificationObj;
    private ApiProceduresReportsHolder.ProceduresByEncounter procedureObj;
    //private SwipeRefreshLayout swipeRefreshLayout;

    public RadFragment() {
        // Required empty public constructor
    }

    public static RadFragment newInstance(ApiEncountersHolder.Encounter encounterObj) {
        RadFragment fragment = new RadFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, encounterObj);
        fragment.setArguments(args);
        return fragment;
    }

    public static RadFragment newInstance(Notification notification) {
        RadFragment fragment = new RadFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_NOTIFICATION, notification);
        fragment.setArguments(args);
        return fragment;
    }
    public static RadFragment newInstance(ApiOtherDocsHolder.ApiDocInfo docInfo) {
        RadFragment fragment = new RadFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM2, docInfo);
        fragment.setArguments(args);
        return fragment;
    }
    public static RadFragment newInstance(ApiProceduresReportsHolder.ProceduresByEncounter procedureInfo) {
        RadFragment fragment = new RadFragment();
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
        if (getArguments().getSerializable(ARG_NOTIFICATION) != null)
            notificationObj = (Notification) getArguments().getSerializable(ARG_NOTIFICATION);
        if (getArguments().getSerializable(ARG_PARAM2) != null)
            docInfo = (ApiOtherDocsHolder.ApiDocInfo) getArguments().getSerializable(ARG_PARAM2);
        if (getArguments().getSerializable(ARG_PARAM3) != null)
            procedureObj = (ApiProceduresReportsHolder.ProceduresByEncounter) getArguments().getSerializable(ARG_PARAM3);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rad, container, false);
        final ImageButton ibToolbarBackButton = view.findViewById(R.id.ib_toolbar_back_button);
            ibToolbarBackButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMediatorCallback.changeFragmentTo(NotificationsFragment.newInstance(), NotificationsFragment.class.getSimpleName());
                }
            });
        //swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        TextView tvTitle = view.findViewById(R.id.tv_Title);
        mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
        mProgressDialog = new MyProgressDialog(mContext);
        tvAlert = view.findViewById(R.id.tv_alert);
        rvProceduresReportsList = view.findViewById(R.id.rv_reportsList);
        if(encounterInfo!=null||docInfo!=null||procedureObj!=null) {
            ibToolbarBackButton.setVisibility(View.GONE);
            tvTitle.setVisibility(View.GONE);
        }
        if (mMediatorCallback.isConnected()) {

            String procRADHRDUrl = null;
            if (encounterInfo != null) {
                System.out.println("*********"+encounterInfo.getEncounterId());
                procRADHRDUrl = API_URL_GET_RAD_HRD_INFO + encounterInfo.getEncounterId();
            } else if (notificationObj != null) {
                tvTitle.setText(notificationObj.getTitle());
                procRADHRDUrl = API_URL_GET_RAD_NOTIFICATION_INFO + notificationObj.getKeyId();
            }else if(docInfo!=null){
                procRADHRDUrl = API_URL_GET_RAD_HRD_INFO + docInfo.getEncounterId();
            }else {
                procRADHRDUrl = API_URL_GET_RAD_HRD_INFO + procedureObj.getEncounterId();
            }
            getProceduresReportsList(procRADHRDUrl);
            /*swipeRefreshLayout.setOnRefreshListener(this);
            swipeRefreshLayout.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            swipeRefreshLayout.setRefreshing(true);
                                            String procRADHRDUrl = null;
                                            if (encounterInfo != null) {
                                               procRADHRDUrl = API_URL_GET_RAD_HRD_INFO + encounterInfo.getEncounterId();
                                            } else if (notificationObj != null) {
                                               procRADHRDUrl = API_URL_GET_RAD_NOTIFICATION_INFO + notificationObj.getKeyId();
                                            }else if(docInfo!=null){
                                                procRADHRDUrl = API_URL_GET_RAD_HRD_INFO + docInfo.getEncounterId();
                                            }else {
                                                procRADHRDUrl = API_URL_GET_RAD_HRD_INFO + procedureObj.getEncounterId();
                                            }
                                            getProceduresReportsList(procRADHRDUrl);
                                        }
                                    }
            );*/
        }
        return view;
    }

    private void getProceduresReportsList(String url) {
        mProgressDialog.showDialog();
        //swipeRefreshLayout.setRefreshing(true);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mContext != null && isAdded()) {
                    try {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {
                            JSONArray jsonElements = response.getJSONArray(API_RESPONSE_RESULT);
                            reportsArrayList = new ArrayList<>();
                            Gson gson = new Gson();
                            ApiRadiologyHolder responseHolder = gson.fromJson(response.toString(), ApiRadiologyHolder.class);
                            reportsArrayList = responseHolder.getResult();

                            setupRecyclerView(reportsArrayList);

                        } else {
                            displayAlert(getResources().getString(R.string.no_records_rad));
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
                if(mContext!=null&&isAdded()) {
                    Log.d("resp-demographic", error.toString());
                    error.printStackTrace();
                    mProgressDialog.dismissDialog();
                    //swipeRefreshLayout.setRefreshing(false);
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
                return headers;
            }

        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);

        mQueue.add(jsonObjectRequest);
    }

    private void setupRecyclerView(ArrayList<ApiRadiologyHolder.Radiology> getmResult) {
        RadiologyRecyclerViewAdapter mAdapter = new RadiologyRecyclerViewAdapter(mContext,getmResult);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(mContext, R.drawable.divider));
        rvProceduresReportsList.addItemDecoration(dividerItemDecoration);
        rvProceduresReportsList.setLayoutManager(layoutManager);
        rvProceduresReportsList.setItemAnimator(new DefaultItemAnimator());
        rvProceduresReportsList.setAdapter(mAdapter);

    }

    private void displayAlert(String msg) {
        rvProceduresReportsList.setVisibility(View.GONE);
        tvAlert.setVisibility(View.VISIBLE);
        tvAlert.setText(msg);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (notificationObj != null)
            mMediatorCallback.changeFragmentTo(NotificationsFragment.newInstance(), NotificationsFragment.class.getSimpleName());
    }

  /*  @Override
    public void onRefresh() {
        String procRADHRDUrl = null;
        if (encounterInfo != null) {
            procRADHRDUrl = API_URL_GET_RAD_HRD_INFO + encounterInfo.getEncounterId();
        } else if (notificationObj != null) {
            procRADHRDUrl = API_URL_GET_RAD_NOTIFICATION_INFO + notificationObj.getKeyId();
        }else if(docInfo!=null){
            procRADHRDUrl = API_URL_GET_RAD_HRD_INFO + docInfo.getEncounterId();
        }else {
            procRADHRDUrl = API_URL_GET_RAD_HRD_INFO + procedureObj.getEncounterId();
        }
        getProceduresReportsList(procRADHRDUrl);
    }*/
}

