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

import java.io.BufferedOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import om.gov.moh.phr.R;
import om.gov.moh.phr.adapters.ImmunizationRecyclerViewAdapter;
import om.gov.moh.phr.apimodels.ApiImmunizationHolder;
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
public class ImmunizationFragment extends Fragment implements SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String API_URL_GET_IMMUNIZATION_INFO = API_NEHR_URL + "immunization/civilId/";
    private static final String API_URL_GET_SCHEDULE_IMMUNIZATION_INFO = API_NEHR_URL + "immunization/scheduled/";
    private static final String ARG_PARAM1 = "ARG_PARAM1";
    private static final String ARG_NOTIFICATION = "ARG_NOTIFICATION";
    private RequestQueue mQueue;
    private MyProgressDialog mProgressDialog;
    private Context mContext;
    private MediatorInterface mMediatorCallback;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private RecyclerView rvImmunizationList;
    private TextView tvAlert;
    private ImmunizationRecyclerViewAdapter mAdapter;
    private boolean isSchedule = false;
    private boolean isFromNotifications = false;
    private SwipeRefreshLayout swipeRefreshLayout;

    public ImmunizationFragment() {
        // Required empty public constructor
    }

    public static ImmunizationFragment newInstance(String type, boolean isFromNotifications) {
        ImmunizationFragment fragment = new ImmunizationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, type);
        args.putBoolean(ARG_NOTIFICATION, isFromNotifications);
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
            String immunizationType = getArguments().getString(ARG_PARAM1);
            isSchedule = !immunizationType.equals("Given");
            isFromNotifications = getArguments().getBoolean(ARG_NOTIFICATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_immunization, container, false);

        TextView tvTitle = view.findViewById(R.id.tv_Title);
        tvTitle.setText(getResources().getString(R.string.title_immunization));
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFromNotifications)
                    mMediatorCallback.changeFragmentTo(NotificationsFragment.newInstance(), NotificationsFragment.class.getSimpleName());
                else
                    mToolbarControllerCallback.customToolbarBackButtonClicked();
            }
        });
        mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
        mProgressDialog = new MyProgressDialog(mContext);
        tvAlert = view.findViewById(R.id.tv_alert);
        rvImmunizationList = view.findViewById(R.id.rv_immunization);
        SearchView searchView = (SearchView) view.findViewById(R.id.sv_searchView);
        searchView.setOnQueryTextListener(this);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        if (mMediatorCallback.isConnected()) {
            if (!isSchedule) {
                String url = API_URL_GET_IMMUNIZATION_INFO + mMediatorCallback.getCurrentUser().getCivilId();
                getMedicationList(url);
            } else {
                String url = API_URL_GET_SCHEDULE_IMMUNIZATION_INFO + mMediatorCallback.getCurrentUser().getCivilId();
                getMedicationList(url);
            }
            swipeRefreshLayout.setOnRefreshListener(this);
            swipeRefreshLayout.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            swipeRefreshLayout.setRefreshing(true);
                                            rvImmunizationList.setVisibility(View.VISIBLE);
                                            tvAlert.setVisibility(View.GONE);
                                            if (!isSchedule) {
                                                String url = API_URL_GET_IMMUNIZATION_INFO + mMediatorCallback.getCurrentUser().getCivilId();
                                                getMedicationList(url);
                                            } else {
                                                String url = API_URL_GET_SCHEDULE_IMMUNIZATION_INFO + mMediatorCallback.getCurrentUser().getCivilId();
                                                getMedicationList(url);
                                            }
                                        }
                                    }
            );
        } else {
            displayAlert(getString(R.string.alert_no_connection));
        }
        return view;
    }

    private void getMedicationList(String url) {
        mProgressDialog.showDialog();
        swipeRefreshLayout.setRefreshing(true);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mContext != null&&isAdded()) {
                    try {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {
                            Gson gson = new Gson();
                            ApiImmunizationHolder responseHolder = gson.fromJson(response.toString(), ApiImmunizationHolder.class);
                            Log.d("resp-dependants", response.getJSONArray(API_RESPONSE_RESULT).toString());
                            setupRecyclerView(responseHolder.getmResult());
                        }else {
                            displayAlert(getResources().getString(R.string.no_record_found));
                            mProgressDialog.dismissDialog();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mProgressDialog.dismissDialog();
                swipeRefreshLayout.setRefreshing(false);
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
                headers.put("Accept", "application/json");
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

    private void setupRecyclerView(ArrayList<ApiImmunizationHolder.ApiImmunizationInfo> getmResult) {
        mAdapter =
                new ImmunizationRecyclerViewAdapter(getmResult, mContext, isSchedule);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(rvImmunizationList.getContext(),
                layoutManager.getOrientation());
        rvImmunizationList.addItemDecoration(mDividerItemDecoration);
        rvImmunizationList.setLayoutManager(layoutManager);
        rvImmunizationList.setItemAnimator(new DefaultItemAnimator());
        rvImmunizationList.setAdapter(mAdapter);
    }

    private void displayAlert(String msg) {
        rvImmunizationList.setVisibility(View.GONE);
        tvAlert.setVisibility(View.VISIBLE);
        tvAlert.setText(msg);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (isFromNotifications)
            mMediatorCallback.changeFragmentTo(NotificationsFragment.newInstance(), NotificationsFragment.class.getSimpleName());
        else {
            mMediatorCallback.changeFragmentContainerVisibility(View.GONE, View.VISIBLE);
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
        rvImmunizationList.setVisibility(View.VISIBLE);
        tvAlert.setVisibility(View.GONE);
        if (!isSchedule) {
            String url = API_URL_GET_IMMUNIZATION_INFO + mMediatorCallback.getCurrentUser().getCivilId();
            getMedicationList(url);
        } else {
            String url = API_URL_GET_SCHEDULE_IMMUNIZATION_INFO + mMediatorCallback.getCurrentUser().getCivilId();
            getMedicationList(url);
        }
    }
}
