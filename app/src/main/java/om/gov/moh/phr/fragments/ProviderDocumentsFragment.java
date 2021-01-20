package om.gov.moh.phr.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import om.gov.moh.phr.R;
import om.gov.moh.phr.adapters.OtherDocsRecyclerViewAdapter;
import om.gov.moh.phr.apimodels.ApiOtherDocsHolder;
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
public class ProviderDocumentsFragment extends Fragment implements SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String API_URL_GET_OTHER_DOCS = API_NEHR_URL + "documentReference/v2/civilId";
    private RequestQueue mQueue;
    private MyProgressDialog mProgressDialog;
    private Context mContext;
    private MediatorInterface mMediatorCallback;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private RecyclerView rvOtherDocsList;
    private TextView tvAlert, tvNoRecords;
    private OtherDocsRecyclerViewAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchView searchView;
    private View view;

    public ProviderDocumentsFragment() {
        // Required empty public constructor
    }

    public static ProviderDocumentsFragment newInstance() {
        ProviderDocumentsFragment fragment = new ProviderDocumentsFragment();
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
            view = inflater.inflate(R.layout.fragment_other_documents, container, false);

            //  TextView tvTitle = view.findViewById(R.id.tv_Title);
            // tvTitle.setText(getResources().getString(R.string.title_other_document));
            mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
            mProgressDialog = new MyProgressDialog(mContext);
            tvAlert = view.findViewById(R.id.tv_alert);
            tvNoRecords = view.findViewById(R.id.tv_no_records_alert);
            rvOtherDocsList = view.findViewById(R.id.rv_DocsList);
            searchView = (SearchView) view.findViewById(R.id.sv_searchView);
            searchView.setOnQueryTextListener(this);
            swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

            if (mMediatorCallback.isConnected()) {

                getProviderDocsList(API_URL_GET_OTHER_DOCS);
                swipeRefreshLayout.setOnRefreshListener(this);
                swipeRefreshLayout.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                swipeRefreshLayout.setRefreshing(true);
                                                rvOtherDocsList.setVisibility(View.VISIBLE);
                                                tvAlert.setVisibility(View.GONE);
                                                getProviderDocsList(API_URL_GET_OTHER_DOCS);
                                            }
                                        }
                );
            } else {
                displayAlert(getString(R.string.alert_no_connection));
                GlobalMethodsKotlin.Companion.showAlertDialog(mContext, getResources().getString(R.string.no_internet_title), getResources().getString(R.string.alert_no_connection), getResources().getString(R.string.ok), R.drawable.ic_error);
            }

        } else {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        return view;
    }

    private void displayAlert(String msg) {
        rvOtherDocsList.setVisibility(View.GONE);
        tvAlert.setVisibility(View.VISIBLE);
        tvAlert.setText(msg);
    }

    private void getProviderDocsList(String url) {
        mProgressDialog.showDialog();
        swipeRefreshLayout.setRefreshing(true);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, getJSONRequestParams(mMediatorCallback.getCurrentUser().getCivilId(), "", "")
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("ProviderResp", response.toString());
                if (mContext != null && isAdded()) {
                    try {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {
                            Gson gson = new Gson();
                            ApiOtherDocsHolder responseHolder = gson.fromJson(response.toString(), ApiOtherDocsHolder.class);
                            setupRecyclerView(responseHolder.getmResult());

                        } else {
                            tvNoRecords.setVisibility(View.VISIBLE);
                            rvOtherDocsList.setVisibility(View.GONE);
                            searchView.setVisibility(View.GONE);
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

    private void setupRecyclerView(ArrayList<ApiOtherDocsHolder.ApiDocInfo> getmResult) {
        rvOtherDocsList.setAdapter(null);
        mAdapter = new OtherDocsRecyclerViewAdapter(mMediatorCallback, getmResult, mContext);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(mContext, R.drawable.divider));
        rvOtherDocsList.addItemDecoration(dividerItemDecoration);
        rvOtherDocsList.setLayoutManager(layoutManager);
        rvOtherDocsList.setItemAnimator(new DefaultItemAnimator());
        rvOtherDocsList.setAdapter(mAdapter);

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
        //  mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.VISIBLE);
    }

    @Override
    public void onRefresh() {
        rvOtherDocsList.setVisibility(View.VISIBLE);
        tvAlert.setVisibility(View.GONE);
        getProviderDocsList(API_URL_GET_OTHER_DOCS);
    }
}
