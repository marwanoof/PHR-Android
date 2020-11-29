package om.gov.moh.phr.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import om.gov.moh.phr.R;
import om.gov.moh.phr.adapters.LabResultsDetailsRecyclerViewAdapter;
import om.gov.moh.phr.adapters.TextualDataRecyclerViewAdapter;
import om.gov.moh.phr.apimodels.ApiLabOrdersListHolder;
import om.gov.moh.phr.apimodels.ApiTextualDataHolder;
import om.gov.moh.phr.apimodels.DLabResultsHolder;
import om.gov.moh.phr.apimodels.Notification;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.MyProgressDialog;

import static om.gov.moh.phr.models.MyConstants.API_CULTURE_TEMPLATE;
import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;
import static om.gov.moh.phr.models.MyConstants.API_TABULAR_TEMPLATE;
import static om.gov.moh.phr.models.MyConstants.API_TEXTUAL_TEMPLATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class LabResultDetailsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String API_URL_GET_LAB_RESULTS_TAMPLATE_D = API_NEHR_URL + "labReport/patient/tabular/";
    private static final String API_URL_GET_LAB_RESULTS_TAMPLATE_C = API_NEHR_URL + "labReport/patient/culture/";
    private static final String API_URL_GET_LAB_RESULTS_TAMPLATE_H = API_NEHR_URL + "labReport/textual/";
    private static final String PARAM_API_ORDERLAB_ITEM = "OrderObj";
    private static final String PARAM_API_NOTIFICATION_ITEM = "NotificationObj";
    private LabResultsDetailsRecyclerViewAdapter labResultsDetailsRecyclerViewAdapter;
    private ApiLabOrdersListHolder.LabOrder mApiOderItem;
    private MyProgressDialog mProgressDialog;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private MediatorInterface mMediatorCallback;
    private TextView tvAlert, tvStatus, tvReleasedDate, tvReleasedBy, tvHospital, tvReport, tvOrderDate, tvOrderBy, tvConclusion, tvProcName;
    private LinearLayout ll_testColumns;
    private Context mContext;
    private RequestQueue mQueue;
    private RecyclerView rvLabResults, rvTextualResults;
    private ImageButton ibRefresh;
    private Notification notificationObj;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextualDataRecyclerViewAdapter mAdapter;

    public LabResultDetailsFragment() {
        // Required empty public constructor
    }

    public static LabResultDetailsFragment newInstance(ApiLabOrdersListHolder.LabOrder orderObj) {
        LabResultDetailsFragment fragment = new LabResultDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(PARAM_API_ORDERLAB_ITEM, orderObj);
        fragment.setArguments(args);
        return fragment;
    }

    public static LabResultDetailsFragment newInstance(Notification notification) {
        LabResultDetailsFragment fragment = new LabResultDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(PARAM_API_NOTIFICATION_ITEM, notification);
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
        assert getArguments() != null;
        if (getArguments().getSerializable(PARAM_API_ORDERLAB_ITEM) != null)
            mApiOderItem = (ApiLabOrdersListHolder.LabOrder) getArguments().getSerializable(PARAM_API_ORDERLAB_ITEM);
        if (getArguments().getSerializable(PARAM_API_NOTIFICATION_ITEM) != null)
            notificationObj = (Notification) getArguments().getSerializable(PARAM_API_NOTIFICATION_ITEM);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lab_result_details, container, false);
        TextView tvToolbarTitle = view.findViewById(R.id.tv_toolbar_title);
        ImageButton ibToolbarBackButton = view.findViewById(R.id.ib_toolbar_back_button);
        tvToolbarTitle.setText(getResources().getString(R.string.title_lab_results));
        tvToolbarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mToolbarControllerCallback.customToolbarBackButtonClicked();
            }
        });
        ibToolbarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mToolbarControllerCallback.customToolbarBackButtonClicked();
            }
        });
        //enableHomeandRefresh(view);
        rvLabResults = view.findViewById(R.id.rv_tests);
        mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
        mProgressDialog = new MyProgressDialog(mContext);
        tvAlert = view.findViewById(R.id.tv_alert);
        tvProcName = view.findViewById(R.id.tv_procName);
        tvConclusion = view.findViewById(R.id.tv_conclusion);
        ll_testColumns = view.findViewById(R.id.ll_test_columns);
        tvOrderDate = view.findViewById(R.id.tv_ScheduledDate);
        tvOrderBy = view.findViewById(R.id.tv_orderBy);
        tvStatus = view.findViewById(R.id.tv_estName);
        tvReleasedDate = view.findViewById(R.id.tv_released_date);
        tvReleasedBy = view.findViewById(R.id.tv_released_by);
        tvHospital = view.findViewById(R.id.tv_hospital);
        tvReport = view.findViewById(R.id.tv_report);
        rvTextualResults = view.findViewById(R.id.rv_textual_recyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        setupPage();
                                    }
                                }
        );
        setupPage();
        if (mApiOderItem != null) {
            tvProcName.setText(mApiOderItem.getProcName());
            Date date = new Date(mApiOderItem.getOrderDate());
            SimpleDateFormat df2 = new SimpleDateFormat("dd/ MM /yyyy  HH:mm");
            String dateText = df2.format(date);
            tvOrderDate.setText(getResources().getString(R.string.ordered_date_feild) + "   " + dateText);
            tvOrderBy.setText(getResources().getString(R.string.ordered_by_feild) + "   " + mApiOderItem.getOrderedBy());
            tvHospital.setText(getResources().getString(R.string.hospital_feild) + "   " + mApiOderItem.getEstName());
        }
        if (notificationObj != null) {
            tvProcName.setText(notificationObj.getTitle());
        }
        /*ibRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupPage();
                if (mApiOderItem != null) {
                    tvProcName.setText(mApiOderItem.getProcName());
                    Date date = new Date(mApiOderItem.getOrderDate());
                    SimpleDateFormat df2 = new SimpleDateFormat("dd/ MM /yyyy  HH:mm");
                    String dateText = df2.format(date);
                    tvOrderDate.setText(getResources().getString(R.string.ordered_date_feild) + "   " + dateText);
                    tvOrderBy.setText(getResources().getString(R.string.ordered_by_feild) + "   " + mApiOderItem.getOrderedBy());
                    tvHospital.setText(getResources().getString(R.string.hospital_feild) + "   " + mApiOderItem.getEstName());
                } else if (notificationObj != null)
                    tvProcName.setText(notificationObj.getTitle());
            }
        });*/
        return view;
    }

    private void enableHomeandRefresh(View view) {
        ibRefresh = view.findViewById(R.id.ib_refresh);
        ibRefresh.setVisibility(View.VISIBLE);
    }

    private void setupPage() {
        if (mApiOderItem != null) {
            if (mApiOderItem.getTemplateType().equals(API_TABULAR_TEMPLATE)) {
                setVisibleItems(true);
            } else {
                setVisibleItems(false);
                if (mApiOderItem.getTemplateType().equals(API_CULTURE_TEMPLATE)) {
                    String fullUrl = API_URL_GET_LAB_RESULTS_TAMPLATE_C + mApiOderItem.getOrderId() + "/" + mApiOderItem.getPatientId();
                    setReportsDetailsForTextual(fullUrl);
                } else if (mApiOderItem.getTemplateType().equals(API_TEXTUAL_TEMPLATE)) {
                    String fullUrl = API_URL_GET_LAB_RESULTS_TAMPLATE_H + mApiOderItem.getOrderId();
                    rvTextualResults.setVisibility(View.VISIBLE);
                    setupTextualRecyclerView();
                    tvReport.setVisibility(View.GONE);
                    setReportsDetailsForTextual(fullUrl);
                }
            }
        }
        if (notificationObj != null) {
            if (notificationObj.getLabType().equals(API_TABULAR_TEMPLATE)) {
                setVisibleItems(true);
            } else {
                setVisibleItems(false);
                if (notificationObj.getLabType().equals(API_CULTURE_TEMPLATE)) {
                    String fullUrl =API_NEHR_URL + "labReport/culture/" + notificationObj.getKeyId();
                    setReportsDetailsForTextual(fullUrl);
                } else if (notificationObj.getLabType().equals(API_TEXTUAL_TEMPLATE)) {
                    String fullUrl = API_NEHR_URL + "labReport/textual/" + notificationObj.getKeyId();
                    rvTextualResults.setVisibility(View.VISIBLE);
                    tvReport.setVisibility(View.GONE);
                    setReportsDetailsForTextual(fullUrl);
                }
            }
        }
    }

    private void setVisibleItems(boolean isDTamplate) {
        if (isDTamplate) {
            tvConclusion.setVisibility(View.VISIBLE);
            ll_testColumns.setVisibility(View.VISIBLE);
            //rvLabResults.setVisibility(View.VISIBLE);
            tvReleasedDate.setVisibility(View.VISIBLE);
            //tvReleasedDate.setBackground(null);
            setupTabualrRecyclerView();
            setReportsDetailsForTabular();
        } else {
            tvConclusion.setVisibility(View.GONE);
            ll_testColumns.setVisibility(View.GONE);
            //rvLabResults.setVisibility(View.GONE);
            //tvOrderDate.setVisibility(View.VISIBLE);
           // tvOrderBy.setVisibility(View.VISIBLE);
            //tvStatus.setVisibility(View.VISIBLE);
            tvReleasedDate.setVisibility(View.VISIBLE);
            //tvReleasedBy.setVisibility(View.VISIBLE);
            //tvHospital.setVisibility(View.VISIBLE);
            //tvReport.setVisibility(View.VISIBLE);
        }
    }

    private void setupTabualrRecyclerView() {
        labResultsDetailsRecyclerViewAdapter = new LabResultsDetailsRecyclerViewAdapter(mContext);
        rvLabResults.addItemDecoration(new DividerItemDecoration(mContext,LinearLayoutManager.VERTICAL));
        rvLabResults.setLayoutManager(new LinearLayoutManager(mContext,
                RecyclerView.VERTICAL, false));
        rvLabResults.setAdapter(labResultsDetailsRecyclerViewAdapter);
    }

    private void setupTextualRecyclerView() {
        mAdapter = new TextualDataRecyclerViewAdapter(mContext);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(rvTextualResults.getContext(),
                layoutManager.getOrientation());
        rvTextualResults.addItemDecoration(mDividerItemDecoration);
        rvTextualResults.setLayoutManager(layoutManager);
        rvTextualResults.setItemAnimator(new DefaultItemAnimator());
        rvTextualResults.setAdapter(mAdapter);
    }

    private void displayAlert(String msg) {
        tvAlert.setVisibility(View.VISIBLE);
        tvConclusion.setVisibility(View.GONE);
        ll_testColumns.setVisibility(View.GONE);
        rvLabResults.setVisibility(View.GONE);
        tvStatus.setVisibility(View.GONE);
        tvReleasedDate.setVisibility(View.GONE);
        tvReleasedBy.setVisibility(View.GONE);
        tvHospital.setVisibility(View.GONE);
        tvReport.setVisibility(View.GONE);
        tvConclusion.setVisibility(View.GONE);
        ll_testColumns.setVisibility(View.GONE);
        rvLabResults.setVisibility(View.GONE);
        tvAlert.setText(msg);
    }

    private void setReportsDetailsForTextual(String url) {
        mProgressDialog.showDialog();
        swipeRefreshLayout.setRefreshing(true);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mContext != null && isAdded()) {
                    try {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {
                            Gson gson = new Gson();
                            ApiTextualDataHolder resultsHolder = gson.fromJson(response.toString(), ApiTextualDataHolder.class);
                        //    Log.d("resultResponse", resultsHolder.getResult().getConclusion() + ", " + resultsHolder.getResult().getReleasedTime() + ", " + resultsHolder.getResult().getStatus()+ ", " + ", " + resultsHolder.getResult().getTextualData().get(0).getParamName()+", "+ resultsHolder.getResult().getTextualData().get(0).getResult());

                            ApiTextualDataHolder.TextualLabResult obj = resultsHolder.getResult();
                            tvStatus.setText(getResources().getString(R.string.status_feild) + "  " + obj.getStatus());
                            Date date = new Date(obj.getReleasedTime());
                            SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
                            String releasedTime = df2.format(date);
                            tvReleasedDate.setText(getResources().getString(R.string.released_date_feild) + "   " + releasedTime);
                            tvReleasedBy.setText(getResources().getString(R.string.released_by_feild) + "   " + obj.getReleasedBy());
                            tvReport.setText(getResources().getString(R.string.report_feild) + " \n \n " + obj.getConclusion());

                            if (mApiOderItem.getTemplateType().equals(API_TEXTUAL_TEMPLATE))
                                updateTextualRecyclerView(obj.getTextualData());

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

    private void setReportsDetailsForTabular() {
        mProgressDialog.showDialog();
        swipeRefreshLayout.setRefreshing(true);
        String fullUrl = null;
        if (mApiOderItem != null)
            fullUrl = API_URL_GET_LAB_RESULTS_TAMPLATE_D + mApiOderItem.getOrderId() + "/" + mApiOderItem.getPatientId();
        else if (notificationObj != null)
            fullUrl = API_NEHR_URL + "labReport/tabular/" + notificationObj.getKeyId();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fullUrl, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mContext != null && isAdded()) {

                    try {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {
                            Gson gson = new Gson();
                            DLabResultsHolder resultsHolder = gson.fromJson(response.toString(), DLabResultsHolder.class);
                            String objConclusion = resultsHolder.getResult().getConclusion();
                            if (objConclusion.equals("Abnormal"))
                                tvConclusion.setTextColor(getResources().getColor(R.color.colorRed));
                            else
                                tvConclusion.setTextColor(getResources().getColor(R.color.colorGreen));
                            tvConclusion.setText(objConclusion);
                            String objReleasedTime = resultsHolder.getResult().getReleasedTime();
                            /*SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
                            String releasedTime = df2.format(objReleasedTime);*/
                            tvReleasedDate.setText(getResources().getString(R.string.released_date_feild)+" "+objReleasedTime);
                            updateRecyclerView(resultsHolder.getResult().getTabularData());

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

    private void updateRecyclerView(ArrayList<DLabResultsHolder.TabularData> items) {
        labResultsDetailsRecyclerViewAdapter.updateItemsList(items);
    }

    private void updateTextualRecyclerView(ArrayList<ApiTextualDataHolder.TextualDataResult> items) {
        mAdapter.updateItemsList(items);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onRefresh() {
        setupPage();
    }
}
