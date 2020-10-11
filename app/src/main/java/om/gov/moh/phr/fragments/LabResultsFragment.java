package om.gov.moh.phr.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import om.gov.moh.phr.R;
import om.gov.moh.phr.adapters.OrderLabRecyclerViewAdapter;
import om.gov.moh.phr.apimodels.ApiEncountersHolder;
import om.gov.moh.phr.apimodels.ApiLabOrdersListHolder;
import om.gov.moh.phr.apimodels.ApiOtherDocsHolder;
import om.gov.moh.phr.apimodels.ApiProceduresReportsHolder;
import om.gov.moh.phr.interfaces.AdapterToFragmentConnectorInterface;
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
public class LabResultsFragment extends Fragment implements SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String API_URL_GET_LAB_ORDERS_INFO = API_NEHR_URL + "labOrder/civilId/";
    private static final String API_URL_GET_LAB_ENCOUNTER_INFO = API_NEHR_URL + "labOrder/encounterId/";
    private static final String ARG_PARAM1 = "ARG_PARAM1";
    private static final String ARG_PARAM2 = "ARG_PARAM2";
    private static final String ARG_PARAM3 = "ARG_PARAM3";
    private static final String ARG_PARAM4 = "ARG_PARAM4";
    private RequestQueue mQueue;
    private MyProgressDialog mProgressDialog;
    private Context mContext;
    private MediatorInterface mMediatorCallback;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private RecyclerView rvLabOrders;
    private TextView tvAlert;
    private OrderLabRecyclerViewAdapter mAdapter;
    private boolean isRecent = false;
    private ApiEncountersHolder.Encounter encounterInfo;
    private ApiOtherDocsHolder.ApiDocInfo docInfo;
    private ApiProceduresReportsHolder procedureInfo;
    private String labResultsType;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ApiLabOrdersListHolder responseHolder;
    private View parentView;

    public LabResultsFragment() {
        // Required empty public constructor
    }

    public static LabResultsFragment newInstance(String param1) {
        LabResultsFragment fragment = new LabResultsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }
    public static LabResultsFragment newInstance(ApiOtherDocsHolder.ApiDocInfo docInfo) {
        LabResultsFragment fragment = new LabResultsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM3, docInfo);
        fragment.setArguments(args);
        return fragment;
    }
    public static LabResultsFragment newInstance(ApiEncountersHolder.Encounter encounterObj) {
        LabResultsFragment fragment = new LabResultsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM2, encounterObj);
        fragment.setArguments(args);
        return fragment;
    }
    public static LabResultsFragment newInstance(ApiProceduresReportsHolder procedureObj) {
        LabResultsFragment fragment = new LabResultsFragment();
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
                labResultsType = getArguments().getString(ARG_PARAM1);
                isRecent = labResultsType.equals("Recent");
            }
            if (getArguments().getSerializable(ARG_PARAM2) != null)
                encounterInfo = (ApiEncountersHolder.Encounter) getArguments().getSerializable(ARG_PARAM2);
            if (getArguments().getSerializable(ARG_PARAM3) != null)
                docInfo = (ApiOtherDocsHolder.ApiDocInfo) getArguments().getSerializable(ARG_PARAM3);
            if (getArguments().getSerializable(ARG_PARAM3) != null)
                docInfo = (ApiOtherDocsHolder.ApiDocInfo) getArguments().getSerializable(ARG_PARAM3);
            if (getArguments().getSerializable(ARG_PARAM4) != null)
                procedureInfo = (ApiProceduresReportsHolder) getArguments().getSerializable(ARG_PARAM4);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (parentView == null) {
        parentView = inflater.inflate(R.layout.fragment_lab_results, container, false);
        TextView tvTitle = parentView.findViewById(R.id.tv_Title);
        mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
        mProgressDialog = new MyProgressDialog(mContext);
        tvAlert = parentView.findViewById(R.id.tv_alert);
        rvLabOrders = parentView.findViewById(R.id.rv_lab_oreders);
        SearchView searchView = (SearchView) parentView.findViewById(R.id.sv_searchView);
        searchView.setOnQueryTextListener(this);
        swipeRefreshLayout = parentView.findViewById(R.id.swipe_refresh_layout);
        if (labResultsType != null) {

        } else {
            tvTitle.setVisibility(View.GONE);
            // ibToolbarBackButton.setVisibility(View.GONE);
            searchView.setVisibility(View.GONE);
        }
        setupRecyclerView(rvLabOrders);
        if (mMediatorCallback.isConnected()) {
            if (labResultsType != null) {
                tvTitle.setText(getResources().getString(R.string.title_lab_results));
                if (isRecent) {
                    String fullUrl = API_URL_GET_LAB_ORDERS_INFO + mMediatorCallback.getCurrentUser().getCivilId() + "?data=recent";
                    getLabOrdersList(fullUrl);
                } else {
                    String fullUrl = API_URL_GET_LAB_ORDERS_INFO + mMediatorCallback.getCurrentUser().getCivilId() + "?data=all";
                    getLabOrdersList(fullUrl);
                }

            } else if(encounterInfo!=null) {
                String fullUrl = API_URL_GET_LAB_ENCOUNTER_INFO + encounterInfo.getEncounterId();
                getLabOrdersList(fullUrl);
            }else if(docInfo!=null) {
                String fullUrl = API_URL_GET_LAB_ENCOUNTER_INFO + docInfo.getEncounterId();
                getLabOrdersList(fullUrl);
            }else {
                String fullUrl = API_URL_GET_LAB_ENCOUNTER_INFO + procedureInfo.getEncounterId();
                getLabOrdersList(fullUrl);
            }
            swipeRefreshLayout.setOnRefreshListener(this);
            swipeRefreshLayout.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            swipeRefreshLayout.setRefreshing(true);
                                            rvLabOrders.setVisibility(View.VISIBLE);
                                            tvAlert.setVisibility(View.GONE);
                                            if (labResultsType != null) {
                                                if (isRecent) {
                                                    String fullUrl = API_URL_GET_LAB_ORDERS_INFO + mMediatorCallback.getCurrentUser().getCivilId() + "?data=recent";
                                                    getLabOrdersList(fullUrl);
                                                } else {
                                                    String fullUrl = API_URL_GET_LAB_ORDERS_INFO + mMediatorCallback.getCurrentUser().getCivilId() + "?data=all";
                                                    getLabOrdersList(fullUrl);
                                                }
                                            } else if(encounterInfo!=null){
                                                String fullUrl = API_URL_GET_LAB_ENCOUNTER_INFO + encounterInfo.getEncounterId();
                                                getLabOrdersList(fullUrl);
                                            }else if(docInfo!=null){
                                                String fullUrl = API_URL_GET_LAB_ENCOUNTER_INFO + docInfo.getEncounterId();
                                                getLabOrdersList(fullUrl);
                                            }else {
                                                String fullUrl = API_URL_GET_LAB_ENCOUNTER_INFO + procedureInfo.getEncounterId();
                                                getLabOrdersList(fullUrl);
                                            }
                                        }
                                    }
            );
        } else {
            displayAlert(getString(R.string.alert_no_connection));
        }
        } else {
            if(parentView.getParent()!=null)
            ((ViewGroup) parentView.getParent()).removeView(parentView);
        }

        return parentView;
    }

    private void getLabOrdersList(String url) {
        mProgressDialog.showDialog();
        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mContext != null && isAdded()) {
                    try {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {

                            Gson gson = new Gson();
                            responseHolder = gson.fromJson(response.toString(), ApiLabOrdersListHolder.class);
                            Log.d("resp-dependants", response.getJSONArray(API_RESPONSE_RESULT).toString());
                            updateRecyclerView(responseHolder.getmResult());

                        } else {
                            displayAlert(getResources().getString(R.string.no_record_found));
                            mProgressDialog.dismissDialog();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    mProgressDialog.dismissDialog();
                    swipeRefreshLayout.setRefreshing(false);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mContext != null && isAdded()) {
                    Log.d("resp-demographic", error.toString());
                    error.printStackTrace();
                    mProgressDialog.dismissDialog();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        }) {
            //
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                //     headers.put("Accept", "application/json");
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

    private void setupRecyclerView(RecyclerView recyclerView) {
        mAdapter =
                new OrderLabRecyclerViewAdapter(mMediatorCallback, mContext);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(rvLabOrders.getContext(),
                layoutManager.getOrientation());
        //recyclerView.addItemDecoration(mDividerItemDecoration);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

    }

    private void updateRecyclerView(ArrayList<ApiLabOrdersListHolder.ApiOredresList> items) {
        mAdapter.updateItemsList(items);
    }

    private void displayAlert(String msg) {
        rvLabOrders.setVisibility(View.GONE);
        tvAlert.setVisibility(View.VISIBLE);
        tvAlert.setText(msg);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (labResultsType != null) {
            mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.VISIBLE);
        }
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

    @Override
    public void onRefresh() {
        rvLabOrders.setVisibility(View.VISIBLE);
        tvAlert.setVisibility(View.GONE);
        if (labResultsType != null) {
            if (isRecent) {
                String fullUrl = API_URL_GET_LAB_ORDERS_INFO + mMediatorCallback.getCurrentUser().getCivilId() + "?data=recent";
                getLabOrdersList(fullUrl);
            } else {
                String fullUrl = API_URL_GET_LAB_ORDERS_INFO + mMediatorCallback.getCurrentUser().getCivilId() + "?data=all";
                getLabOrdersList(fullUrl);
            }
        } else if(encounterInfo!=null){
            String fullUrl = API_URL_GET_LAB_ENCOUNTER_INFO + encounterInfo.getEncounterId();
            getLabOrdersList(fullUrl);
        }else if(docInfo!=null){
            String fullUrl = API_URL_GET_LAB_ENCOUNTER_INFO + docInfo.getEncounterId();
            getLabOrdersList(fullUrl);
        }else {
            String fullUrl = API_URL_GET_LAB_ENCOUNTER_INFO + procedureInfo.getEncounterId();
            getLabOrdersList(fullUrl);
        }
    }
}
