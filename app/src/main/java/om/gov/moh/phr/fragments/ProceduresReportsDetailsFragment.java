package om.gov.moh.phr.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiRadiologyHolder;
import om.gov.moh.phr.adapters.NurseNoteRecyclerViewAdapter;
import om.gov.moh.phr.adapters.ProceduresReportsRecyclerView;
import om.gov.moh.phr.apimodels.ApiMediaProcedureHolder;
import om.gov.moh.phr.apimodels.ApiProceduresNurseNoteHolder;
import om.gov.moh.phr.apimodels.ApiProceduresReportsHolder;
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
public class ProceduresReportsDetailsFragment extends Fragment {
    private static final String API_PROCEDURES_REPORTS_WEBVIEW = API_NEHR_URL + "diagnosticReport/report/";
    private static final String API_PROCEDURES_REPORTS_RECYCLERVIEW = API_NEHR_URL + "procedure/nurseNotes/";
    private static final String API_PROCEDURES_REPORTS_TEXT = API_NEHR_URL + "procedure/notes/";
    private static final String PARAM_API_PROCEDURE_REPORT_ITEM = "PARAM_API_PROCEDURE_REPORT_ITEM";
    private static final String PARAM_API_MEDIA_REPORT_ITEM = "PARAM_API_MEDIA_REPORT_ITEM";
    private static final String PARAM_API_RADIOLOGY_REPORT_ITEM = "PARAM_API_RADIOLOGY_REPORT_ITEM";
    private static final String PARAM_RAD_ITEM = "PARAM_API_RAD_ITEM";
    private static final String REPORT_KEY = "report";
    private static final String TEXT_KEY = "text";
    private MyProgressDialog mProgressDialog;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private MediatorInterface mMediatorCallback;
    private Context mContext;
    private RequestQueue mQueue;
    private ApiProceduresReportsHolder.Procedures mProcedureReport;
    private ApiMediaProcedureHolder.MediaProcedure mMediaReport;
    private ApiRadiologyHolder.Radiology radiology;
    private RecyclerView rvRportDetails;
    private TextView tvAlert, tvProcedureName, tvTime, tvHospital, tvSummary, tvReport;
    private WebView wvReportPic;
    private boolean isWebView = false;
    private boolean isNotes = false;
    private ImageButton ibRefresh;
    private boolean isRAD;
    private ConstraintLayout swipeRefreshLayout;

    public ProceduresReportsDetailsFragment() {
        // Required empty public constructor
    }

    public static ProceduresReportsDetailsFragment newInstance(ApiProceduresReportsHolder.Procedures procedureObj) {
        ProceduresReportsDetailsFragment fragment = new ProceduresReportsDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(PARAM_API_PROCEDURE_REPORT_ITEM, procedureObj);
        fragment.setArguments(args);
        return fragment;
    }

    public static ProceduresReportsDetailsFragment newInstance(ApiRadiologyHolder.Radiology radiologyHolder, String radHRD) {
        ProceduresReportsDetailsFragment fragment = new ProceduresReportsDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(PARAM_API_RADIOLOGY_REPORT_ITEM, radiologyHolder);
        args.putString(PARAM_RAD_ITEM, radHRD);
        fragment.setArguments(args);
        return fragment;
    }

    public static ProceduresReportsDetailsFragment newInstance(ApiMediaProcedureHolder.MediaProcedure mediaProcedure) {
        ProceduresReportsDetailsFragment fragment = new ProceduresReportsDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(PARAM_API_MEDIA_REPORT_ITEM, mediaProcedure);
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
            if (getArguments().getSerializable(PARAM_API_PROCEDURE_REPORT_ITEM) != null) {
                mProcedureReport = (ApiProceduresReportsHolder.Procedures) getArguments().getSerializable(PARAM_API_PROCEDURE_REPORT_ITEM);
            }
            if (getArguments().getSerializable(PARAM_API_MEDIA_REPORT_ITEM) != null) {
                mMediaReport = (ApiMediaProcedureHolder.MediaProcedure) getArguments().getSerializable(PARAM_API_MEDIA_REPORT_ITEM);
            }
            if (getArguments().getSerializable(PARAM_API_RADIOLOGY_REPORT_ITEM) != null) {
                radiology = (ApiRadiologyHolder.Radiology) getArguments().getSerializable(PARAM_API_RADIOLOGY_REPORT_ITEM);
                isRAD = getArguments().getString(PARAM_RAD_ITEM) != null && getArguments().getString(PARAM_RAD_ITEM).equals("RAD");
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_procedures_reports_details, container, false);
        TextView tvToolbarTitle = view.findViewById(R.id.tv_toolbar_title);
        ImageButton ibToolbarBackButton = view.findViewById(R.id.ib_toolbar_back_button);
        tvToolbarTitle.setText(getResources().getString(R.string.title_procedures_reports));
        ibToolbarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mToolbarControllerCallback.customToolbarBackButtonClicked();
            }
        });
        tvToolbarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mToolbarControllerCallback.customToolbarBackButtonClicked();
            }
        });
        //enableHomeandRefresh(view);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        rvRportDetails = view.findViewById(R.id.rv_reportDetails);
        mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
        mProgressDialog = new MyProgressDialog(mContext);
        tvAlert = view.findViewById(R.id.tv_alert);
        tvProcedureName = view.findViewById(R.id.tv_procedureName);
        tvTime = view.findViewById(R.id.tv_time);
        tvHospital = view.findViewById(R.id.tv_hospital);
        wvReportPic = view.findViewById(R.id.wv_report);
        tvSummary = view.findViewById(R.id.tv_mediaSummary);
        tvReport = view.findViewById(R.id.tv_report);

        setupPage();

        return view;
    }

    private void setupPage() {
        if (isRAD) {
            tvProcedureName.setText(radiology.getProcName());
            tvHospital.setText(radiology.getEstFullname());
            if (radiology.getReportDoneDate() != 0) {
                Date date = new Date(radiology.getReportDoneDate());
                SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                String dateText = df2.format(date);
                tvTime.setText(dateText);
            }
            isWebView = true;
            String fullUrl = API_PROCEDURES_REPORTS_WEBVIEW + mProcedureReport.getReportId();

            if (mMediatorCallback.isConnected()) {
                getReportDetails(fullUrl);
            } else {
                GlobalMethodsKotlin.Companion.showAlertDialog(mContext, getResources().getString(R.string.no_internet_title), getResources().getString(R.string.alert_no_connection), getResources().getString(R.string.ok), R.drawable.ic_error);
            }

        } else {
            if (mMediaReport != null) {
                tvProcedureName.setText(mMediaReport.getMediaSubType());
                tvHospital.setText(mMediaReport.getEstFullname());
                tvTime.setText(mMediaReport.getCreationTime());
                isWebView = true;
                try {
                    if (mMediaReport.getMediaString() != null) {
                        byte[] data1 = Base64.decode(mMediaReport.getMediaString(), Base64.DEFAULT);
                        String text = new String(data1, "UTF-8");
                        //data == html data which you want to load
                        wvReportPic.getSettings().setDefaultFontSize(10);
                        wvReportPic.loadData(URLEncoder.encode(text, "utf-8").replaceAll("\\+"," "), "text/html", "utf-8");
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


            } else {
                if(mProcedureReport.getProcedure()!=null) {
                    if(mProcedureReport.getProcedure().size()>0)
                        tvProcedureName.setText(mProcedureReport.getProcedure().get(0).getName());
                }
                tvHospital.setText(mProcedureReport.getEstFullname());
                if (mProcedureReport.getProcedureDoneDate() != 0) {
                    Date date = new Date(mProcedureReport.getProcedureDoneDate());
                    SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH);
                    String dateText = df2.format(date);
                    tvTime.setText(dateText);
                }
                if (mProcedureReport.getProfileCode() == 101) {
                    isWebView = true;
                    String fullUrl = API_PROCEDURES_REPORTS_WEBVIEW + mProcedureReport.getReportId();

                    if (mMediatorCallback.isConnected()) {
                        getReportDetails(fullUrl);
                    } else {
                        GlobalMethodsKotlin.Companion.showAlertDialog(mContext, getResources().getString(R.string.no_internet_title), getResources().getString(R.string.alert_no_connection), getResources().getString(R.string.ok), R.drawable.ic_error);
                    }
                } else if (mProcedureReport.getProfileCode() == 113) {
                    if (mProcedureReport.getEstFullname() != null)
                        tvHospital.setText(mProcedureReport.getEstFullname());
                    //  tvHospital.setVisibility(View.GONE);
                    //tvTime.setVisibility(View.GONE);
                    wvReportPic.setVisibility(View.GONE);
                    tvSummary.setVisibility(View.GONE);
                    rvRportDetails.setVisibility(View.VISIBLE);
                    String fullUrl = API_PROCEDURES_REPORTS_RECYCLERVIEW + mProcedureReport.getPatientId();

                    if (mMediatorCallback.isConnected()) {
                        getNurseNoteData(fullUrl);
                    } else {
                        GlobalMethodsKotlin.Companion.showAlertDialog(mContext, getResources().getString(R.string.no_internet_title), getResources().getString(R.string.alert_no_connection), getResources().getString(R.string.ok), R.drawable.ic_error);
                    }
                } else {
                    isWebView = false;
                    if (mProcedureReport.getEstFullname() != null)
                        tvHospital.setText(mProcedureReport.getEstFullname());
                    // tvHospital.setVisibility(View.GONE);
                    //tvTime.setVisibility(View.GONE);
                    wvReportPic.setVisibility(View.GONE);
                    tvSummary.setVisibility(View.GONE);
                    wvReportPic.setVisibility(View.GONE);
                    rvRportDetails.setVisibility(View.VISIBLE);
                    isNotes = true;
                    String url = API_PROCEDURES_REPORTS_TEXT + mProcedureReport.getProcedureId();

                    if (mMediatorCallback.isConnected()) {
                        getNurseNoteData(url);
                    } else {
                        GlobalMethodsKotlin.Companion.showAlertDialog(mContext, getResources().getString(R.string.no_internet_title), getResources().getString(R.string.alert_no_connection), getResources().getString(R.string.ok), R.drawable.ic_error);
                    }
                }
            }
        }
    }

    private void getReportDetails(String url) {
        mProgressDialog.showDialog();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mContext != null && isAdded()) {
                    try {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {
                            if (isWebView) {
                                JSONArray array = response.getJSONArray(API_RESPONSE_RESULT);
                                JSONObject obj = array.getJSONObject(0);
                                try {
                                    byte[] data1 = Base64.decode(obj.getString(REPORT_KEY), Base64.DEFAULT);
                                    String text = new String(data1, "UTF-8");
                                    //data == html data which you want to load
                                    wvReportPic.getSettings().setDefaultFontSize(10);
                                    wvReportPic.loadData(URLEncoder.encode(text, "utf-8").replaceAll("\\+"," "), "text/html", "utf-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                JSONArray jsonElements = response.getJSONArray(API_RESPONSE_RESULT);
                                ArrayList<ReportData> reportsArrayList = new ArrayList<>();
                                for (int i = 0; i < jsonElements.length(); i++) {
                                    JSONObject jsonObject = jsonElements.getJSONObject(i);
                                    String ReportText = jsonObject.getString(TEXT_KEY);
                                    ReportData reportData = new ReportData();
                                    reportData.setReportText(ReportText);
                                    if (!isNotes) {
                                        long ReportTime = jsonObject.getLong("time");
                                        reportData.setReportTime(ReportTime);
                                    }
                                    reportsArrayList.add(reportData);
                                }
                                setupRecyclerView(reportsArrayList);
                            }

                        } else {
                            displayAlert(getResources().getString(R.string.no_record_found));
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

    private void getDiagnosticReport(String url, String data, String source) {
        mProgressDialog.showDialog();
        //swipeRefreshLayout.setRefreshing(true);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, getJSONRequestParams(mMediatorCallback.getCurrentUser().getCivilId(), data, source)
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mContext != null && isAdded()) {
                    try {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {
                            if (isWebView) {
                                JSONArray array = response.getJSONArray(API_RESPONSE_RESULT);
                                JSONObject obj = array.getJSONObject(0);
                                try {
                                    byte[] data1 = Base64.decode(obj.getString(REPORT_KEY), Base64.DEFAULT);
                                    String text = new String(data1, "UTF-8");
                                    //data == html data which you want to load
                                    wvReportPic.getSettings().setDefaultFontSize(10);
                                    wvReportPic.loadData(URLEncoder.encode(text, "utf-8").replaceAll("\\+"," "), "text/html", "utf-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                JSONArray jsonElements = response.getJSONArray(API_RESPONSE_RESULT);
                                ArrayList<ReportData> reportsArrayList = new ArrayList<>();
                                for (int i = 0; i < jsonElements.length(); i++) {
                                    JSONObject jsonObject = jsonElements.getJSONObject(i);
                                    String ReportText = jsonObject.getString(TEXT_KEY);
                                    ReportData reportData = new ReportData();
                                    reportData.setReportText(ReportText);
                                    if (!isNotes) {
                                        long ReportTime = jsonObject.getLong("time");
                                        reportData.setReportTime(ReportTime);
                                    }
                                    reportsArrayList.add(reportData);
                                }
                                setupRecyclerView(reportsArrayList);
                            }

                        } else {
                            displayAlert(getResources().getString(R.string.no_record_found));
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

    private JSONObject getJSONRequestParams(String civilId, String data, String source) {
        Map<String, Object> params = new HashMap<>();
        params.put("civilId", Long.parseLong(civilId));
        params.put("data", data);
        params.put("source", source);
        return new JSONObject(params);
    }

    private void getNurseNoteData(String url) {
        mProgressDialog.showDialog();
        //swipeRefreshLayout.setRefreshing(true);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mContext != null && isAdded()) {
                    try {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {
                            Gson gson = new Gson();
                            ApiProceduresNurseNoteHolder responseHolder = gson.fromJson(response.toString(), ApiProceduresNurseNoteHolder.class);
                            ArrayList<ApiProceduresNurseNoteHolder.NurseNote> reportsArrayList = responseHolder.getResult();

                            setupRecyclerViewNurseNote(reportsArrayList);


                        } else {
                            displayAlert(getResources().getString(R.string.no_record_found));
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

    private void displayAlert(String msg) {
        tvAlert.setVisibility(View.VISIBLE);
        tvProcedureName.setVisibility(View.GONE);
        tvTime.setVisibility(View.GONE);
        tvHospital.setVisibility(View.GONE);
        tvSummary.setVisibility(View.GONE);
        rvRportDetails.setVisibility(View.GONE);
        wvReportPic.setVisibility(View.GONE);
        tvAlert.setText(msg);
    }

    private void setupRecyclerView(ArrayList<ReportData> reportsArrayList) {
        ProceduresReportsRecyclerView mAdapter =
                new ProceduresReportsRecyclerView(reportsArrayList, mContext);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(rvRportDetails.getContext(),
                layoutManager.getOrientation());
        //rvRportDetails.addItemDecoration(mDividerItemDecoration);
        rvRportDetails.setLayoutManager(layoutManager);
        rvRportDetails.setItemAnimator(new DefaultItemAnimator());
        rvRportDetails.setAdapter(mAdapter);
    }

    private void setupRecyclerViewNurseNote(ArrayList<ApiProceduresNurseNoteHolder.NurseNote> reportsArrayList) {
        NurseNoteRecyclerViewAdapter mAdapter = new NurseNoteRecyclerViewAdapter(reportsArrayList, mContext);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(mContext, R.drawable.divider));
        rvRportDetails.addItemDecoration(dividerItemDecoration);
        rvRportDetails.setLayoutManager(layoutManager);
        rvRportDetails.setItemAnimator(new DefaultItemAnimator());
        rvRportDetails.setAdapter(mAdapter);
    }

    /*  @Override
      public void onRefresh() {
          setupPage();
      }
  */
    public class ReportData {
        private String reportText;
        private Long reportTime;

        public String getReportText() {
            return reportText;
        }

        private void setReportText(String reportText) {
            this.reportText = reportText;
        }

        public Long getReportTime() {
            return reportTime;
        }

        private void setReportTime(Long reportTime) {
            this.reportTime = reportTime;
        }
    }
}
