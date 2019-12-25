package om.gov.moh.phr.fragments;


import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import om.gov.moh.phr.R;
import om.gov.moh.phr.adapters.ProceduresReportsRecyclerView;
import om.gov.moh.phr.apimodels.ApiLabOrdersListHolder;
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
public class ProceduresReportsDetailsFragment extends Fragment {
    private static final String API_PROCEDURES_REPORTS_WEBVIEW = API_NEHR_URL + "diagnosticReport/report/";
    private static final String API_PROCEDURES_REPORTS_RECYCLERVIEW = API_NEHR_URL + "procedure/nurseNotes/";
    private static final String API_PROCEDURES_REPORTS_TEXT = API_NEHR_URL + "procedure/notes/";
    private static final String PARAM_API_PROCEDURE_REPORT_ITEM = "PARAM_API_PROCEDURE_REPORT_ITEM";
    private static final String PARAM_RAD_ITEM = "PARAM_API_RAD_ITEM";
    private static final String REPORT_KEY = "report";
    private static final String TEXT_KEY = "text";
    private MyProgressDialog mProgressDialog;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private MediatorInterface mMediatorCallback;
    private Context mContext;
    private RequestQueue mQueue;
    private ApiProceduresReportsHolder mProcedureReport;
    private RecyclerView rvRportDetails;
    private TextView tvAlert, tvProcedureName, tvTime, tvHospital, tvSummary, tvReport;
    private WebView wvReportPic;
    private boolean isWebView = false;
    private ImageButton ibHome, ibRefresh;
    private boolean isRAD;

    public ProceduresReportsDetailsFragment() {
        // Required empty public constructor
    }

    public static ProceduresReportsDetailsFragment newInstance(ApiProceduresReportsHolder procedureObj) {
        ProceduresReportsDetailsFragment fragment = new ProceduresReportsDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(PARAM_API_PROCEDURE_REPORT_ITEM, procedureObj);
        fragment.setArguments(args);
        return fragment;
    }

    public static ProceduresReportsDetailsFragment newInstance(ApiProceduresReportsHolder procedureObj, String radHRD) {
        ProceduresReportsDetailsFragment fragment = new ProceduresReportsDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(PARAM_API_PROCEDURE_REPORT_ITEM, procedureObj);
        args.putString(PARAM_RAD_ITEM, radHRD);
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
        if (getArguments().getSerializable(PARAM_API_PROCEDURE_REPORT_ITEM) != null) {
            mProcedureReport = (ApiProceduresReportsHolder) getArguments().getSerializable(PARAM_API_PROCEDURE_REPORT_ITEM);
            isRAD = getArguments().getString(PARAM_RAD_ITEM) != null && getArguments().getString(PARAM_RAD_ITEM).equals("RAD");
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
        enableHomeandRefresh(view);
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
        ibRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupPage();
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
        if (isRAD) {
            tvProcedureName.setText(mProcedureReport.getProcName());
            tvHospital.setText(mProcedureReport.getEstName());
            Date date = new Date(mProcedureReport.getProcedureDoneDate());
            SimpleDateFormat df2 = new SimpleDateFormat("dd /MM /yyyy - HH:mm:ss", Locale.US);
            String dateText = df2.format(date);
            tvTime.setText(dateText);
            if (mProcedureReport.getReportId() != null) {
                isWebView = true;
                String fullUrl = API_PROCEDURES_REPORTS_WEBVIEW + mProcedureReport.getReportId();
                getReportDetails(fullUrl);
            } else {
                wvReportPic.setVisibility(View.GONE);
                tvReport.setVisibility(View.VISIBLE);
                tvReport.setText(getResources().getString(R.string.alert_not_available));
            }
        } else {
            tvProcedureName.setText(mProcedureReport.getName());
            tvHospital.setText(mProcedureReport.getEstName());
            Date date = new Date(mProcedureReport.getStartTime());
            SimpleDateFormat df2 = new SimpleDateFormat("dd /MM /yyyy - HH:mm:ss", Locale.US);
            String dateText = df2.format(date);
            tvTime.setText(dateText);
            if (mProcedureReport.getName().equals("ECG")) {
                isWebView = true;
                try {
                    if (mProcedureReport.getMediaString() != null) {
                        byte[] data1 = Base64.decode(mProcedureReport.getMediaString(), Base64.DEFAULT);
                        String text = new String(data1, "UTF-8");
                        //data == html data which you want to load
                        wvReportPic.loadData(text, "text/html", "utf-8");
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else if (mProcedureReport.getProfileCode() == 101) {
                isWebView = true;
                String fullUrl = API_PROCEDURES_REPORTS_WEBVIEW + mProcedureReport.getReportId();
                getReportDetails(fullUrl);
            } else if (mProcedureReport.getProfileCode() == 113) {
                tvHospital.setVisibility(View.GONE);
                tvTime.setVisibility(View.GONE);
                wvReportPic.setVisibility(View.GONE);
                tvSummary.setVisibility(View.GONE);
                rvRportDetails.setVisibility(View.VISIBLE);
                String fullUrl = API_PROCEDURES_REPORTS_RECYCLERVIEW + mMediatorCallback.getCurrentUser().getCivilId();
                getReportDetails(fullUrl);
            } else {
                isWebView = false;
                wvReportPic.setVisibility(View.GONE);
                tvReport.setVisibility(View.VISIBLE);
                getReportText();
            }
        }
    }

    private void getReportText() {
        mProgressDialog.showDialog();
        String url = API_PROCEDURES_REPORTS_TEXT + mProcedureReport.getProcedureId();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt(API_RESPONSE_CODE) == 0) {
                        JSONArray array = response.getJSONArray(API_RESPONSE_RESULT);
                        JSONObject obj = array.getJSONObject(0);
                        tvReport.setText(obj.getString("text"));

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

    private void getReportDetails(String url) {
        mProgressDialog.showDialog();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt(API_RESPONSE_CODE) == 0) {
                        if (isWebView) {
                            JSONArray array = response.getJSONArray(API_RESPONSE_RESULT);
                            JSONObject obj = array.getJSONObject(0);
                            try {
                                byte[] data1 = Base64.decode(obj.getString(REPORT_KEY), Base64.DEFAULT);
                                String text = new String(data1, "UTF-8");
                                //data == html data which you want to load
                                wvReportPic.loadData(text, "text/html", "utf-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        } else {
                            JSONArray jsonElements = response.getJSONArray(API_RESPONSE_RESULT);
                            ArrayList<String> reportsArrayList = new ArrayList<>();
                            for (int i = 0; i < jsonElements.length(); i++) {
                                JSONObject jsonObject = jsonElements.getJSONObject(i);
                                String ReportText = jsonObject.getString(TEXT_KEY);
                                reportsArrayList.add(ReportText);
                            }
                            setupRecyclerView(reportsArrayList);
                        }

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

    private void setupRecyclerView(ArrayList<String> reportsArrayList) {
        ProceduresReportsRecyclerView mAdapter =
                new ProceduresReportsRecyclerView(reportsArrayList, mContext);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(rvRportDetails.getContext(),
                layoutManager.getOrientation());
        rvRportDetails.addItemDecoration(mDividerItemDecoration);
        rvRportDetails.setLayoutManager(layoutManager);
        rvRportDetails.setItemAnimator(new DefaultItemAnimator());
        rvRportDetails.setAdapter(mAdapter);
    }


}
