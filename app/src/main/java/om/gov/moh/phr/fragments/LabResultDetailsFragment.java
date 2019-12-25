package om.gov.moh.phr.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import om.gov.moh.phr.R;
import om.gov.moh.phr.adapters.LabResultsDetailsRecyclerViewAdapter;
import om.gov.moh.phr.adapters.TextualDataRecyclerViewAdapter;
import om.gov.moh.phr.apimodels.ApiLabOrdersListHolder;
import om.gov.moh.phr.apimodels.ApiTextualDataHolder;
import om.gov.moh.phr.apimodels.DLabResultsHolder;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.MyProgressDialog;

import static om.gov.moh.phr.models.MyConstants.API_CULTURE_TEMPLATE;
import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_MESSAGE;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_RESULT;
import static om.gov.moh.phr.models.MyConstants.API_TABULAR_TEMPLATE;
import static om.gov.moh.phr.models.MyConstants.API_TEXTUAL_TEMPLATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class LabResultDetailsFragment extends Fragment {

    private static final String API_URL_GET_LAB_RESULTS_TAMPLATE_D = API_NEHR_URL + "labReport/tabular/";
    private static final String API_URL_GET_LAB_RESULTS_TAMPLATE_C = API_NEHR_URL + "labReport/culture/";
    private static final String API_URL_GET_LAB_RESULTS_TAMPLATE_H = API_NEHR_URL + "labReport/textual/";
    private static final String PARAM_API_ORDERLAB_ITEM = "OrderObj";
    private static final String STATUS_KEY = "status";
    private static final String RELEASED_DATE_KEY = "releasedTime";
    private static final String RELEASED_BY_KEY = "releasedBy";
    private static final String CONCLUSION_KEY = "conclusion";
    private static final String TEXTUALDATA_ARRAY_KEY = "textualData";
    private static final String TEXTUAL_ITEM_NAME_KEY = "paramName";
    private static final String RESULT_KEY = "result";
    private static final String TABULARDATA_ARRAY_KEY = "tabularData";
    private static final String TABULARDATA_TESTNAME_KEY = "testName";
    private static final String TABULARDATA_TESTUNIT_KEY = "unit";
    private static final String TABULARDATA_TESTRANGELOW_KEY = "rangeLow";
    private static final String TABULARDATA_TESTRANGEHIGH = "rangeHigh";
    private static final String TABULARDATA_INTERPRETATION = "interpretation";
    private ApiLabOrdersListHolder.ApiOredresList mApiOderItem;
    private MyProgressDialog mProgressDialog;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private MediatorInterface mMediatorCallback;
    private TextView tvAlert, tvStatus, tvReleasedDate, tvReleasedBy, tvHospital, tvReport, tvOrderDate, tvOrderBy, tvConclusion, tvProcName;
    private LinearLayout ll_testColumns;
    private Context mContext;
    private RequestQueue mQueue;
    private RecyclerView rvLabResults, rvTextualResults;
    private ImageButton ibHome, ibRefresh;

    public LabResultDetailsFragment() {
        // Required empty public constructor
    }

    public static LabResultDetailsFragment newInstance(ApiLabOrdersListHolder.ApiOredresList orderObj) {
        LabResultDetailsFragment fragment = new LabResultDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(PARAM_API_ORDERLAB_ITEM, orderObj);
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
            mApiOderItem = (ApiLabOrdersListHolder.ApiOredresList) getArguments().getSerializable(PARAM_API_ORDERLAB_ITEM);
        }
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
        enableHomeandRefresh(view);
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
        setupPage();
        tvProcName.setText(mApiOderItem.getProcName());
        Date date = new Date(mApiOderItem.getOrderDate());
        SimpleDateFormat df2 = new SimpleDateFormat("dd/ MM /yyyy  HH:mm");
        String dateText = df2.format(date);
        tvOrderDate.setText(getResources().getString(R.string.ordered_date_feild) + "   " + dateText);
        tvOrderBy.setText(getResources().getString(R.string.ordered_by_feild) + "   " + mApiOderItem.getOrderedBy());
        tvHospital.setText(getResources().getString(R.string.hospital_feild) + "   " + mApiOderItem.getEstName());
        ibRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupPage();
                tvProcName.setText(mApiOderItem.getProcName());
                Date date = new Date(mApiOderItem.getOrderDate());
                SimpleDateFormat df2 = new SimpleDateFormat("dd/ MM /yyyy  HH:mm");
                String dateText = df2.format(date);
                tvOrderDate.setText(getResources().getString(R.string.ordered_date_feild) + "   " + dateText);
                tvOrderBy.setText(getResources().getString(R.string.ordered_by_feild) + "   " + mApiOderItem.getOrderedBy());
                tvHospital.setText(getResources().getString(R.string.hospital_feild) + "   " + mApiOderItem.getEstName());
            }
        });
        ibHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToHome();
            }
        });
        return view;
    }

    private void enableHomeandRefresh(View view) {
        ibHome = view.findViewById(R.id.ib_home);
        ibRefresh = view.findViewById(R.id.ib_refresh);
        ibHome.setVisibility(View.VISIBLE);
        ibRefresh.setVisibility(View.VISIBLE);
    }

    private void backToHome() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    private void setupPage() {
        if (mApiOderItem.getTemplateType().equals(API_TABULAR_TEMPLATE)) {
            setVisibleItems(true);
        } else {
            setVisibleItems(false);
            if (mApiOderItem.getTemplateType().equals(API_CULTURE_TEMPLATE)) {
                String fullUrl = API_URL_GET_LAB_RESULTS_TAMPLATE_C + mApiOderItem.getOrderId();
                setReportsDetailsForTextual(fullUrl);
            } else if (mApiOderItem.getTemplateType().equals(API_TEXTUAL_TEMPLATE)) {
                String fullUrl = API_URL_GET_LAB_RESULTS_TAMPLATE_H + mApiOderItem.getOrderId();
                rvTextualResults.setVisibility(View.VISIBLE);
                tvReport.setVisibility(View.GONE);
                setReportsDetailsForTextual(fullUrl);
            }
        }
    }

    private void setVisibleItems(boolean isDTamplate) {
        if (isDTamplate) {
            tvConclusion.setVisibility(View.VISIBLE);
            ll_testColumns.setVisibility(View.VISIBLE);
            rvLabResults.setVisibility(View.VISIBLE);
            setReportsDetailsForTabular();
        } else {
            tvConclusion.setVisibility(View.GONE);
            ll_testColumns.setVisibility(View.GONE);
            rvLabResults.setVisibility(View.GONE);
            tvOrderDate.setVisibility(View.VISIBLE);
            tvOrderBy.setVisibility(View.VISIBLE);
            tvStatus.setVisibility(View.VISIBLE);
            tvReleasedDate.setVisibility(View.VISIBLE);
            tvReleasedBy.setVisibility(View.VISIBLE);
            tvHospital.setVisibility(View.VISIBLE);
            tvReport.setVisibility(View.VISIBLE);
        }
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

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt(API_RESPONSE_CODE) == 0) {
                        JSONObject obj = response.getJSONObject(API_RESPONSE_RESULT);
                        tvStatus.setText(getResources().getString(R.string.status_feild) + "  " + obj.getString(STATUS_KEY));
                        Date date = new Date(obj.getLong(RELEASED_DATE_KEY));
                        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
                        String releasedTime = df2.format(date);
                        tvReleasedDate.setText(getResources().getString(R.string.released_date_feild) + "   " + releasedTime);
                        tvReleasedBy.setText(getResources().getString(R.string.released_by_feild) + "   " + obj.getString(RELEASED_BY_KEY));
                        tvReport.setText(getResources().getString(R.string.report_feild) + " \n \n " + obj.getString(CONCLUSION_KEY));
                        if (mApiOderItem.getTemplateType().equals(API_TEXTUAL_TEMPLATE))
                            getTextualData(obj);

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

    private void getTextualData(JSONObject response) {
        try {
            JSONArray jsonArray = response.getJSONArray(TEXTUALDATA_ARRAY_KEY);
            ArrayList<ApiTextualDataHolder> textualDataArrayList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ApiTextualDataHolder textualobj = new ApiTextualDataHolder();
                textualobj.setParamName(jsonObject.getString(TEXTUAL_ITEM_NAME_KEY));
                textualobj.setResult(jsonObject.getString(RESULT_KEY));
                textualDataArrayList.add(textualobj);
            }
            setupRecyclerView(textualDataArrayList);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setupRecyclerView(ArrayList<ApiTextualDataHolder> textualData) {
        TextualDataRecyclerViewAdapter mAdapter = new TextualDataRecyclerViewAdapter(textualData, mContext);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(rvTextualResults.getContext(),
                layoutManager.getOrientation());
        rvTextualResults.addItemDecoration(mDividerItemDecoration);
        rvTextualResults.setLayoutManager(layoutManager);
        rvTextualResults.setItemAnimator(new DefaultItemAnimator());
        rvTextualResults.setAdapter(mAdapter);
    }

    private void setReportsDetailsForTabular() {
        mProgressDialog.showDialog();
        String fullUrl = API_URL_GET_LAB_RESULTS_TAMPLATE_D + mApiOderItem.getOrderId();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fullUrl, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt(API_RESPONSE_CODE) == 0) {
                        String objConclusion = response.getJSONObject(API_RESPONSE_RESULT).getString(CONCLUSION_KEY);
                        if (objConclusion.equals("Abnormal"))
                            tvConclusion.setTextColor(getResources().getColor(R.color.colorRed));
                        else
                            tvConclusion.setTextColor(getResources().getColor(R.color.colorGreen));
                        tvConclusion.setText(objConclusion);
                        JSONArray jsonLabResultsArray = response.getJSONObject(API_RESPONSE_RESULT).getJSONArray(TABULARDATA_ARRAY_KEY);
                        ArrayList<DLabResultsHolder> labResultsArrayList = new ArrayList<>();
                        for (int i = 0; i < jsonLabResultsArray.length(); i++) {
                            JSONObject jsonObject = jsonLabResultsArray.getJSONObject(i);
                            DLabResultsHolder obj = new DLabResultsHolder();
                            obj.setTestName(jsonObject.getString(TABULARDATA_TESTNAME_KEY));
                            obj.setResult(jsonObject.getString(RESULT_KEY));
                            obj.setUnit(jsonObject.getString(TABULARDATA_TESTUNIT_KEY));
                            obj.setRangeLow(jsonObject.getString(TABULARDATA_TESTRANGELOW_KEY));
                            obj.setRangeHigh(jsonObject.getString(TABULARDATA_TESTRANGEHIGH));
                            obj.setInterpretation(jsonObject.getString(TABULARDATA_INTERPRETATION));
                            labResultsArrayList.add(obj);
                        }


                        LabResultsDetailsRecyclerViewAdapter labResultsDetailsRecyclerViewAdapter = new LabResultsDetailsRecyclerViewAdapter(labResultsArrayList, mContext);
                        rvLabResults.addItemDecoration(new DividerItemDecoration(mContext,
                                LinearLayoutManager.VERTICAL));
                        rvLabResults.setLayoutManager(new LinearLayoutManager(mContext,
                                RecyclerView.VERTICAL, false));
                        rvLabResults.setAdapter(labResultsDetailsRecyclerViewAdapter);


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
}
