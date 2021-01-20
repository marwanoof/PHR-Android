package om.gov.moh.phr.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
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
import om.gov.moh.phr.apimodels.ApiMedicationHolder;
import om.gov.moh.phr.apimodels.ApiOtherDocsHolder;
import om.gov.moh.phr.apimodels.ApiProceduresReportsHolder;
import om.gov.moh.phr.interfaces.AdapterToFragmentConnectorInterface;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.DividerItemDecorator;
import om.gov.moh.phr.models.GlobalMethodsKotlin;
import om.gov.moh.phr.models.MyProgressDialog;

import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_RESULT;

/**
 * A simple {@link Fragment} subclass.
 */
public class LabResultsFragment extends Fragment implements SearchView.OnQueryTextListener {
    private static final String API_URL_GET_LAB_ORDERS_INFO = API_NEHR_URL + "labOrder/groupByEncounters";
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
    private OrderLabRecyclerViewAdapter mAdapter = new OrderLabRecyclerViewAdapter();
    private boolean isRecent = false;
    private ApiEncountersHolder.Encounter encounterInfo;
    private ApiOtherDocsHolder.ApiDocInfo docInfo;
    private ApiProceduresReportsHolder.ProceduresByEncounter procedureInfo;
    private String labResultsType;
   // private SwipeRefreshLayout swipeRefreshLayout;
    private ApiLabOrdersListHolder responseHolder;
    private View parentView;
    private ArrayList<ApiLabOrdersListHolder.ApiOredresList> apiOredresListArrayList;
    private CardView noRecordCardView;
    private SearchView searchView;

    private static final String PARAM5 = "PARAM5";
    private String pageTitle;

    public LabResultsFragment() {
        // Required empty public constructor
    }

    public static LabResultsFragment newInstance(String param1,String title) {
        LabResultsFragment fragment = new LabResultsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(PARAM5,title);
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
    public static LabResultsFragment newInstance(ApiProceduresReportsHolder.ProceduresByEncounter procedureObj) {
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
                procedureInfo = (ApiProceduresReportsHolder.ProceduresByEncounter) getArguments().getSerializable(ARG_PARAM4);
            if (getArguments().getString(PARAM5) != null)
                pageTitle = getArguments().getString(PARAM5);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (parentView == null) {
        parentView = inflater.inflate(R.layout.fragment_lab_results, container, false);
       // TextView tvTitle = parentView.findViewById(R.id.tv_Title);
        mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
        mProgressDialog = new MyProgressDialog(mContext);
        tvAlert = parentView.findViewById(R.id.tv_alert);
        rvLabOrders = parentView.findViewById(R.id.rv_lab_oreders);
        searchView = parentView.findViewById(R.id.sv_searchView);
        searchView.setOnQueryTextListener(this);
        noRecordCardView = parentView.findViewById(R.id.cardViewNoRecords);
        //swipeRefreshLayout = parentView.findViewById(R.id.swipe_refresh_layout);
        if (labResultsType != null) {

        } else {
         //   tvTitle.setVisibility(View.GONE);
            // ibToolbarBackButton.setVisibility(View.GONE);
            searchView.setVisibility(View.GONE);
        }
        //setupRecyclerView(rvLabOrders);
        if (mMediatorCallback.isConnected()) {
            if (labResultsType != null) {
            //    tvTitle.setText(pageTitle);
                if (isRecent) {
                    getLabOrdersList(API_URL_GET_LAB_ORDERS_INFO,"recent","PHR","");
                } else {
                    getLabOrdersList(API_URL_GET_LAB_ORDERS_INFO,"all","PHR","");
                }

            } else if(encounterInfo!=null) {
                String fullUrl = API_URL_GET_LAB_ENCOUNTER_INFO + encounterInfo.getEncounterId();
                getLabOrdersList(API_URL_GET_LAB_ORDERS_INFO,"all","PHR",encounterInfo.getEncounterId());
            }else if(docInfo!=null) {
                String fullUrl = API_URL_GET_LAB_ENCOUNTER_INFO + docInfo.getEncounterId();
                getLabOrdersList(API_URL_GET_LAB_ORDERS_INFO,"all","PHR",docInfo.getEncounterId());
            }else {

                getLabOrdersList(API_URL_GET_LAB_ORDERS_INFO,"all","PHR",procedureInfo.getEncounterId());
            }
            /*swipeRefreshLayout.setOnRefreshListener(this);
            swipeRefreshLayout.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            swipeRefreshLayout.setRefreshing(true);
                                            rvLabOrders.setVisibility(View.VISIBLE);
                                            tvAlert.setVisibility(View.GONE);
                                            if (labResultsType != null) {
                                                if (isRecent) {
                                                    getLabOrdersList(API_URL_GET_LAB_ORDERS_INFO,"recent","PHR","");
                                                } else {
                                                    getLabOrdersList(API_URL_GET_LAB_ORDERS_INFO,"all","PHR","");
                                                }
                                            } else if(encounterInfo!=null) {
                                                String fullUrl = API_URL_GET_LAB_ENCOUNTER_INFO + encounterInfo.getEncounterId();
                                                getLabOrdersList(API_URL_GET_LAB_ORDERS_INFO,"all","PHR",encounterInfo.getEncounterId());
                                            }else if(docInfo!=null) {
                                                String fullUrl = API_URL_GET_LAB_ENCOUNTER_INFO + docInfo.getEncounterId();
                                                getLabOrdersList(API_URL_GET_LAB_ORDERS_INFO,"all","PHR",docInfo.getEncounterId());
                                            }else {
                                                String fullUrl = API_URL_GET_LAB_ENCOUNTER_INFO + procedureInfo.getEncounterId();
                                                getLabOrdersList(API_URL_GET_LAB_ORDERS_INFO,"all","PHR",docInfo.getEncounterId());
                                            }
                                        }
                                    }
            );*/
        } else {
            displayAlert(getString(R.string.alert_no_connection));
            GlobalMethodsKotlin.Companion.showAlertDialog(mContext, getResources().getString(R.string.no_internet_title), getResources().getString(R.string.alert_no_connection), getResources().getString(R.string.ok), R.drawable.ic_error);
        }
        } else {
            if(parentView.getParent()!=null)
            ((ViewGroup) parentView.getParent()).removeView(parentView);
        }

        return parentView;
    }

    private void getLabOrdersList(String url, final String data, String source, final String encounterId) {
        mProgressDialog.showDialog();
        // showing refresh animation before making http call
        //swipeRefreshLayout.setRefreshing(true);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, getJSONRequestParams(mMediatorCallback.getCurrentUser().getCivilId(),data,source)
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("LabResultResp", response.toString());
                if (mContext != null && isAdded()) {
                    try {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {

                            Gson gson = new Gson();
                            responseHolder = gson.fromJson(response.toString(), ApiLabOrdersListHolder.class);
                            apiOredresListArrayList = responseHolder.getmResult();
                            ArrayList<ApiLabOrdersListHolder.ApiOredresList> filteredLabInfo = new ArrayList<>();
                            if (!encounterId.isEmpty()){
                                for (ApiLabOrdersListHolder.ApiOredresList labInfo:apiOredresListArrayList){
                                    if (labInfo.getEncounterId().equals(encounterId)){
                                        filteredLabInfo.add(labInfo);
                                    }
                                }
                                //setupRecyclerView(filteredLabInfo,false);
                                if (filteredLabInfo.size() > 0)
                                    setupRecyclerView(filteredLabInfo,false);
                                else
                                    displayAlert(getResources().getString(R.string.no_records_lab_encounter));

                            }else {

                                setupRecyclerView(apiOredresListArrayList,true);
                            }



                            //updateRecyclerView(responseHolder.getmResult());

                        } else {
                            if (data.equals("all"))
                                displayAlert(getResources().getString(R.string.no_records_lab_all));
                            else if (data.equals("recent"))
                                displayAlert(getResources().getString(R.string.no_records_lab_recent));

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
    private void setupRecyclerView(ArrayList<ApiLabOrdersListHolder.ApiOredresList> apiOredresLists,Boolean showVisitDate) {
        mAdapter = new OrderLabRecyclerViewAdapter(apiOredresLists,mMediatorCallback, mContext,showVisitDate);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);


        //DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(rvLabOrders.getContext(), layoutManager.getOrientation());
        //recyclerView.addItemDecoration(mDividerItemDecoration);
        rvLabOrders.setLayoutManager(layoutManager);
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(mContext, R.anim.delay_slide_down);
        rvLabOrders.setLayoutAnimation(animation);
       // rvLabOrders.setItemAnimator(new DefaultItemAnimator());
        rvLabOrders.setAdapter(mAdapter);

    }

    private void updateRecyclerView(ArrayList<ApiLabOrdersListHolder.ApiOredresList> items) {
        mAdapter.updateItemsList(items);
    }

    private void displayAlert(String msg) {
        searchView.clearFocus();
        searchView.setEnabled(false);
        searchView.setVisibility(View.GONE);
        rvLabOrders.setVisibility(View.GONE);
        noRecordCardView.setVisibility(View.VISIBLE);
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
    public boolean onQueryTextChange(String s) {
        if (s.length() == 0){
            updateRecyclerView(apiOredresListArrayList);
        }else {
            ArrayList<ApiLabOrdersListHolder.ApiOredresList> filteredList = new ArrayList<>();
            for (int i = 0;i< apiOredresListArrayList.size();i++) {
                for (int j = 0; j< apiOredresListArrayList.get(i).getLabOrders().size();j++){
                    if (apiOredresListArrayList.get(i).getLabOrders().get(j).getProcName().toLowerCase().contains(s)
                    || apiOredresListArrayList.get(i).getLabOrders().get(j).getEstFullname().toLowerCase().contains(s)
                            || apiOredresListArrayList.get(i).getLabOrders().get(j).getEstName().toLowerCase().contains(s)
                            || apiOredresListArrayList.get(i).getLabOrders().get(j).getEstFullnameNls().contains(s))
                    {
                        filteredList.add(apiOredresListArrayList.get(i));
                    }
                }


            }
            updateRecyclerView(filteredList);
        }
        return false;
    }

  /*  @Override
    public void onRefresh() {
        rvLabOrders.setVisibility(View.VISIBLE);
        tvAlert.setVisibility(View.GONE);
        if (labResultsType != null) {
            if (isRecent) {
                getLabOrdersList(API_URL_GET_LAB_ORDERS_INFO,"recent","PHR","");
            } else {
                getLabOrdersList(API_URL_GET_LAB_ORDERS_INFO,"all","PHR","");
            }
        } else if(encounterInfo!=null) {
            String fullUrl = API_URL_GET_LAB_ENCOUNTER_INFO + encounterInfo.getEncounterId();
            getLabOrdersList(API_URL_GET_LAB_ORDERS_INFO,"all","PHR",encounterInfo.getEncounterId());
        }else if(docInfo!=null) {
            String fullUrl = API_URL_GET_LAB_ENCOUNTER_INFO + docInfo.getEncounterId();
            getLabOrdersList(API_URL_GET_LAB_ORDERS_INFO,"all","PHR",docInfo.getEncounterId());
        }else {
            String fullUrl = API_URL_GET_LAB_ENCOUNTER_INFO + procedureInfo.getEncounterId();
            getLabOrdersList(API_URL_GET_LAB_ORDERS_INFO,"all","PHR",docInfo.getEncounterId());
        }
    }*/
}
