package om.gov.moh.phr.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
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
public class MediaProceduresReportsFragment extends Fragment implements SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {
    private RequestQueue mQueue;
    private MyProgressDialog mProgressDialog;
    private Context mContext;
    private MediatorInterface mMediatorCallback;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private RecyclerView rvProceduresReportsList;
    private TextView tvAlert;
    private static final String API_URL_GET_MEDIA_PROCEDURES_REPORTS_INFO = API_NEHR_URL + "diagnosticOrder/media/";
    private ArrayList<ApiProceduresReportsHolder> reportsArrayList;
    private ProceduresReportsRecyclerView mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View view;
    public MediaProceduresReportsFragment() {
        // Required empty public constructor
    }
    public static MediaProceduresReportsFragment newInstance() {
        MediaProceduresReportsFragment fragment = new MediaProceduresReportsFragment();
        Bundle args = new Bundle();
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null) {
         view = inflater.inflate(R.layout.fragment_media_procedures_reports, container, false);

            TextView tvTitle = view.findViewById(R.id.tv_Title);
            tvTitle.setText(getResources().getString(R.string.title_media));
            tvAlert = view.findViewById(R.id.tv_alert);
            rvProceduresReportsList = view.findViewById(R.id.rv_reportsList);
            mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
            mProgressDialog = new MyProgressDialog(mContext);
            SearchView searchView = (SearchView) view.findViewById(R.id.sv_searchView);
            searchView.setOnQueryTextListener(this);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
            if (mMediatorCallback.isConnected()) {
                String mediaProceduresReportsUrl = API_URL_GET_MEDIA_PROCEDURES_REPORTS_INFO + mMediatorCallback.getCurrentUser().getCivilId();
                getProceduresReportsList(mediaProceduresReportsUrl);
                swipeRefreshLayout.setOnRefreshListener(this);
                swipeRefreshLayout.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                swipeRefreshLayout.setRefreshing(true);
                                                rvProceduresReportsList.setVisibility(View.VISIBLE);
                                                tvAlert.setVisibility(View.GONE);
                                                String mediaProceduresReportsUrl = API_URL_GET_MEDIA_PROCEDURES_REPORTS_INFO + mMediatorCallback.getCurrentUser().getCivilId();
                                                getProceduresReportsList(mediaProceduresReportsUrl);
                                            }
                                        }
                );
            } else {
                displayAlert(getString(R.string.alert_no_connection));
            }
        }else {
            ((ViewGroup)view.getParent()).removeView(view);
        }
        return view;
    }
    private void displayAlert(String msg) {
        rvProceduresReportsList.setVisibility(View.GONE);
        tvAlert.setVisibility(View.VISIBLE);
        tvAlert.setText(msg);
    }
    private void getProceduresReportsList(String url) {
        mProgressDialog.showDialog();
        swipeRefreshLayout.setRefreshing(true);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mContext != null) {
                    try {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {
                            JSONArray jsonElements = response.getJSONArray(API_RESPONSE_RESULT);
                            reportsArrayList = new ArrayList<>();
                            for (int i = 0; i < jsonElements.length(); i++) {
                                JSONObject jsonObject = jsonElements.getJSONObject(i);
                                ApiProceduresReportsHolder reportDetails = new ApiProceduresReportsHolder();
                                String mediaSubType = jsonObject.getString("mediaSubType");
                                reportDetails.setName(mediaSubType);
                                reportDetails.setStartTime(jsonObject.getLong("creationTime"));
                                reportDetails.setEstName(jsonObject.getString("estCode"));
                                reportDetails.setMediaString(jsonObject.getString("mediaString"));
                                reportDetails.setEstFullname(jsonObject.getString("estFullname"));
                                reportsArrayList.add(reportDetails);
                            }
                            setupRecyclerView(reportsArrayList);

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
                if(mContext!=null&&isAdded()) {
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

    private void setupRecyclerView(ArrayList<ApiProceduresReportsHolder> getmResult) {
        mAdapter =
                new ProceduresReportsRecyclerView(mMediatorCallback, getmResult, mContext, false);
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
    public void onDetach() {
        super.onDetach();
        mMediatorCallback.changeFragmentContainerVisibility(View.GONE, View.VISIBLE);
        mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.VISIBLE);
    }

    @Override
    public void onRefresh() {
        rvProceduresReportsList.setVisibility(View.VISIBLE);
        tvAlert.setVisibility(View.GONE);
        String mediaProceduresReportsUrl = API_URL_GET_MEDIA_PROCEDURES_REPORTS_INFO + mMediatorCallback.getCurrentUser().getCivilId();
        getProceduresReportsList(mediaProceduresReportsUrl);
    }
}
