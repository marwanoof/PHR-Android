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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import om.gov.moh.phr.R;
import om.gov.moh.phr.adapters.MeasurementRecyclerViewAdapter;
import om.gov.moh.phr.adapters.MedicationRecyclerViewAdapter;
import om.gov.moh.phr.adapters.OrderLabRecyclerViewAdapter;
import om.gov.moh.phr.apimodels.ApiEncountersHolder;
import om.gov.moh.phr.apimodels.ApiMedicationHolder;
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
public class MedicationFragment extends Fragment implements SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String API_URL_GET_MEDICATIONS_INFO = API_NEHR_URL + "medicationOrder/civilId/";
    private static final String API_URL_GET_MED_HRD_INFO = API_NEHR_URL + "medicationOrder/encounterId/";
    private static final String ARG_PARAM1 = "ARG_PARAM1";
    private static final String ARG_PARAM2 = "ARG_PARAM2";
    private RequestQueue mQueue;
    private MyProgressDialog mProgressDialog;
    private Context mContext;
    private MediatorInterface mMediatorCallback;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private RecyclerView rvMedicationList;
    private TextView tvAlert;
    private MedicationRecyclerViewAdapter mAdapter;
    private boolean isRecent = false;
    private String medicationType;
    private ApiEncountersHolder.Encounter encounterInfo;
    private SwipeRefreshLayout swipeRefreshLayout;

    public MedicationFragment() {
        // Required empty public constructor
    }

    public static MedicationFragment newInstance(String param1) {
        MedicationFragment fragment = new MedicationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public static MedicationFragment newInstance(ApiEncountersHolder.Encounter encounterObj) {
        MedicationFragment fragment = new MedicationFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM2, encounterObj);
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
                medicationType = getArguments().getString(ARG_PARAM1);
                isRecent = medicationType.equals("Active");
            }
            if (getArguments().getSerializable(ARG_PARAM2) != null)
                encounterInfo = (ApiEncountersHolder.Encounter) getArguments().getSerializable(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_medication, container, false);

        TextView tvTitle = view.findViewById(R.id.tv_Title);
        tvTitle.setText(getResources().getString(R.string.title_medication));

        mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
        mProgressDialog = new MyProgressDialog(mContext);
        tvAlert = view.findViewById(R.id.tv_alert);
        rvMedicationList = view.findViewById(R.id.rv_maedication);
        SearchView searchView = (SearchView) view.findViewById(R.id.sv_searchView);
        searchView.setOnQueryTextListener(this);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        if (medicationType != null) {

        } else {
            tvTitle.setVisibility(View.GONE);
            searchView.setVisibility(View.GONE);
        }
        if (mMediatorCallback.isConnected()) {
            if (medicationType != null) {
                if (isRecent) {
                    String recentMedicationUrl = API_URL_GET_MEDICATIONS_INFO + mMediatorCallback.getCurrentUser().getCivilId() + "?data=recent";
                    getMedicationList(recentMedicationUrl);
                } else {
                    String allMedicationUrl = API_URL_GET_MEDICATIONS_INFO + mMediatorCallback.getCurrentUser().getCivilId();
                    getMedicationList(allMedicationUrl);
                }

            } else {
                String medHRDurl = API_URL_GET_MED_HRD_INFO + encounterInfo.getEncounterId();
                getMedicationList(medHRDurl);

            }
            swipeRefreshLayout.setOnRefreshListener(this);
            swipeRefreshLayout.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            swipeRefreshLayout.setRefreshing(true);
                                            rvMedicationList.setVisibility(View.VISIBLE);
                                            tvAlert.setVisibility(View.GONE);
                                            if (medicationType != null) {
                                                if (isRecent) {
                                                    String recentMedicationUrl = API_URL_GET_MEDICATIONS_INFO + mMediatorCallback.getCurrentUser().getCivilId() + "?data=recent";
                                                    getMedicationList(recentMedicationUrl);
                                                } else {
                                                    String allMedicationUrl = API_URL_GET_MEDICATIONS_INFO + mMediatorCallback.getCurrentUser().getCivilId();
                                                    getMedicationList(allMedicationUrl);
                                                }
                                            } else {
                                                String medHRDurl = API_URL_GET_MED_HRD_INFO + encounterInfo.getEncounterId();
                                                getMedicationList(medHRDurl);
                                            }
                                        }
                                    }
            );

        } else {
            displayAlert(getString(R.string.alert_no_connection));
        }
        return view;
    }

    private void displayAlert(String msg) {
        rvMedicationList.setVisibility(View.GONE);
        tvAlert.setVisibility(View.VISIBLE);
        tvAlert.setText(msg);
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
                            ApiMedicationHolder responseHolder = gson.fromJson(response.toString(), ApiMedicationHolder.class);
                            Log.d("resp-dependants", response.getJSONArray(API_RESPONSE_RESULT).toString());
                            setupRecyclerView(responseHolder.getmResult());

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

    private void setupRecyclerView(ArrayList<ApiMedicationHolder.ApiMedicationInfo> getmResult) {
        mAdapter =
                new MedicationRecyclerViewAdapter(getmResult, mContext);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(rvMedicationList.getContext(),
                layoutManager.getOrientation());
        //rvMedicationList.addItemDecoration(mDividerItemDecoration);
        rvMedicationList.setLayoutManager(layoutManager);
        rvMedicationList.setItemAnimator(new DefaultItemAnimator());
        rvMedicationList.setAdapter(mAdapter);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (medicationType != null) {
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
        rvMedicationList.setVisibility(View.VISIBLE);
        tvAlert.setVisibility(View.GONE);
        if (medicationType != null) {
            if (isRecent) {
                String recentMedicationUrl = API_URL_GET_MEDICATIONS_INFO + mMediatorCallback.getCurrentUser().getCivilId() + "?data=recent";
                getMedicationList(recentMedicationUrl);
            } else {
                String allMedicationUrl = API_URL_GET_MEDICATIONS_INFO + mMediatorCallback.getCurrentUser().getCivilId();
                getMedicationList(allMedicationUrl);
            }
        } else {
            String medHRDurl = API_URL_GET_MED_HRD_INFO + encounterInfo.getEncounterId();
            getMedicationList(medHRDurl);
        }
    }
}
