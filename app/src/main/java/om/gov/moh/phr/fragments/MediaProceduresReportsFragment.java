package om.gov.moh.phr.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import om.gov.moh.phr.adapters.MediaRecyclerViewAdapter;
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
public class MediaProceduresReportsFragment extends Fragment implements SearchView.OnQueryTextListener{
    private RequestQueue mQueue;
    private MyProgressDialog mProgressDialog;
    private Context mContext;
    private MediatorInterface mMediatorCallback;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private RecyclerView rvProceduresReportsList;
    private TextView tvAlert, tvNoRecordsFound;
    private static final String API_URL_GET_MEDIA_PROCEDURES_REPORTS_INFO = API_NEHR_URL + "diagnosticOrder/media/";
    private ArrayList<ApiProceduresReportsHolder> reportsArrayList;
    private MediaRecyclerViewAdapter mAdapter;
   private SearchView searchView;
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

           // TextView tvTitle = view.findViewById(R.id.tv_Title);
           // tvTitle.setText(getResources().getString(R.string.title_media));
            tvAlert = view.findViewById(R.id.tv_alert);
            tvNoRecordsFound = view.findViewById(R.id.tv_no_records_alert);
            rvProceduresReportsList = view.findViewById(R.id.rv_reportsList);
            mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
            mProgressDialog = new MyProgressDialog(mContext);
             searchView = (SearchView) view.findViewById(R.id.sv_searchView);
            searchView.setOnQueryTextListener(this);

            if (mMediatorCallback.isConnected()) {
                String mediaProceduresReportsUrl = API_URL_GET_MEDIA_PROCEDURES_REPORTS_INFO + mMediatorCallback.getCurrentUser().getCivilId();
                getProceduresReportsList(mediaProceduresReportsUrl);

            } else {
                displayAlert(getString(R.string.alert_no_connection));
                GlobalMethodsKotlin.Companion.showAlertDialog(mContext, getResources().getString(R.string.no_internet_title), getResources().getString(R.string.alert_no_connection), getResources().getString(R.string.ok), R.drawable.ic_error);
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


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mContext != null&&isAdded()) {
                    try {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {
                            Gson gson = new Gson();
                            ApiMediaProcedureHolder responseHolder = gson.fromJson(response.toString(), ApiMediaProcedureHolder.class);
                            ArrayList<ApiMediaProcedureHolder.MediaProcedure> reportsArrayList = responseHolder.getResult();
                            setupRecyclerView(reportsArrayList);

                        } else {
                            searchView.setVisibility(View.GONE);
                            tvNoRecordsFound.setVisibility(View.VISIBLE);
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
                if(mContext!=null&&isAdded()) {
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

    private void setupRecyclerView(ArrayList<ApiMediaProcedureHolder.MediaProcedure> getmResult) {
        mAdapter = new MediaRecyclerViewAdapter( getmResult, mContext);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(mContext, R.drawable.divider));
        rvProceduresReportsList.addItemDecoration(dividerItemDecoration);
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
        //if (mAdapter != null)
            //mAdapter.filter(newText);
        return false;
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.VISIBLE);
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
    }

}
