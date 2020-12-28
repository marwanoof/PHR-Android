package om.gov.moh.phr.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Base64;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiOtherDocsHolder;
import om.gov.moh.phr.apimodels.ApiProceduresReportsHolder;
import om.gov.moh.phr.apimodels.Notification;
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
public class OtherDocsDetailsFragment extends Fragment {
    private static final String PARAM_API_DOC_ITEM_ITEM = "PARAM_API_PROCEDURE_REPORT_ITEM";
    private static final String API_DOC_INFO = API_NEHR_URL + "documentReference/id/";
    private static final String PARAM_API_PROCEDURE_REPORT_ITEM = "PARAM_API_PROCEDURE_REPORT_ITEM";
    private static final String DATA_KEY = "data";
    private MyProgressDialog mProgressDialog;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private MediatorInterface mMediatorCallback;
    private Context mContext;
    private RequestQueue mQueue;
    private ApiOtherDocsHolder.ApiDocInfo mDocInfo;
    private TextView tvAlert, tvDocType, tvTime, tvHospital, tvSource;
    private WebView wvDocView;
   // private ImageButton ibRefresh;

    public OtherDocsDetailsFragment() {
        // Required empty public constructor
    }

    public static OtherDocsDetailsFragment newInstance(ApiOtherDocsHolder.ApiDocInfo docObj) {
        OtherDocsDetailsFragment fragment = new OtherDocsDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(PARAM_API_DOC_ITEM_ITEM, docObj);
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
            mDocInfo = (ApiOtherDocsHolder.ApiDocInfo) getArguments().getSerializable(PARAM_API_PROCEDURE_REPORT_ITEM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_other_docs_details, container, false);
        TextView tvToolbarTitle = view.findViewById(R.id.tv_toolbar_title);
        tvToolbarTitle.setText(getResources().getString(R.string.title_other_document));
        ImageButton ibToolbarBackButton = view.findViewById(R.id.ib_toolbar_back_button);
        tvToolbarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mToolbarControllerCallback.customToolbarBackButtonClicked();
            }
        });
        ibToolbarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    mToolbarControllerCallback.customToolbarBackButtonClicked();
            }
        });
       // enableHomeandRefresh(view);
        mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
        mProgressDialog = new MyProgressDialog(mContext);
        tvAlert = view.findViewById(R.id.tv_alert);
        tvDocType = view.findViewById(R.id.tv_docType);
        tvTime = view.findViewById(R.id.tv_time);
        tvHospital = view.findViewById(R.id.tv_hospital);
        tvSource = view.findViewById(R.id.tv_source);
        wvDocView = view.findViewById(R.id.wv_report);
        if (mDocInfo != null) {
            tvDocType.setText(mDocInfo.getTitle());
            tvSource.setText(getResources().getString(R.string.department_label) + " " + mDocInfo.getLocationName());
            tvHospital.setText(getResources().getString(R.string.hospital_feild) + " " + mDocInfo.getEstFullname());
            Date date = new Date(mDocInfo.getIndexed());
            SimpleDateFormat df2 = new SimpleDateFormat("dd-MM-yyyy");
            String dateText = df2.format(date);
            tvTime.setText(dateText);
            final String fullUrl = API_DOC_INFO + mDocInfo.getDocumentRefId();
            getReportDetails(fullUrl);
        }

        return view;
    }

    private void backToHome() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    private void getReportDetails(String url) {
        mProgressDialog.showDialog();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mContext != null&&isAdded()) {
                    try {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {
                        JSONArray array = response.getJSONArray(API_RESPONSE_RESULT);
                        JSONObject obj = array.getJSONObject(0);
                        try {
                            byte[] data1 = Base64.decode(obj.getString(DATA_KEY), Base64.DEFAULT);
                            String text = new String(data1, "UTF-8");
                            //data == html data which you want to load
                            wvDocView.loadData(text, "text/html", "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
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

    private void displayAlert(String msg) {
        tvAlert.setVisibility(View.VISIBLE);
        tvDocType.setVisibility(View.GONE);
        tvTime.setVisibility(View.GONE);
        tvHospital.setVisibility(View.GONE);
        tvSource.setVisibility(View.GONE);
        wvDocView.setVisibility(View.GONE);
        tvAlert.setText(msg);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.GONE);
    }
}
