package om.gov.moh.phr.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import om.gov.moh.phr.R;
import om.gov.moh.phr.adapters.OrderLabRecyclerViewAdapter;
import om.gov.moh.phr.apimodels.ApiEncountersHolder;
import om.gov.moh.phr.apimodels.ApiLabOrdersListHolder;
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
public class LabResultsFragment extends Fragment implements SearchView.OnQueryTextListener {
    private static final String API_URL_GET_LAB_ORDERS_INFO = API_NEHR_URL + "labOrder/civilId/";
    private static final String API_URL_GET_LAB_ENCOUNTER_INFO = API_NEHR_URL + "labOrder/encounterId/";
    private static final String ARG_PARAM1 = "ARG_PARAM1";
    private static final String ARG_PARAM2 = "ARG_PARAM2";
    private RequestQueue mQueue;
    private MyProgressDialog mProgressDialog;
    private Context mContext;
    private MediatorInterface mMediatorCallback;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private RecyclerView rvLabOrders;
    private TextView tvAlert;
    private OrderLabRecyclerViewAdapter mAdapter;
    private View parentView;
    private boolean isRecent;
    private ApiEncountersHolder.Encounter encounterInfo;
    private String labResultsType;

    public LabResultsFragment() {
        // Required empty public constructor
    }

    public static LabResultsFragment newInstance(String param1) {
        LabResultsFragment fragment = new LabResultsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public static LabResultsFragment newInstance(ApiEncountersHolder.Encounter encounterObj) {
        LabResultsFragment fragment = new LabResultsFragment();
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
            if(getArguments().getString(ARG_PARAM1)!=null) {
                labResultsType = getArguments().getString(ARG_PARAM1);
                isRecent = labResultsType.equals("Recent");
            }
            if (getArguments().getSerializable(ARG_PARAM2) != null)
                encounterInfo = (ApiEncountersHolder.Encounter) getArguments().getSerializable(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       // if (parentView == null) {
            parentView = inflater.inflate(R.layout.fragment_lab_results, container, false);
            ImageButton ibToolbarBackButton = parentView.findViewById(R.id.ib_toolbar_back_button);
            TextView tvTitle = parentView.findViewById(R.id.tv_Title);
            mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
            mProgressDialog = new MyProgressDialog(mContext);
            tvAlert = parentView.findViewById(R.id.tv_alert);
            rvLabOrders = parentView.findViewById(R.id.rv_lab_oreders);
            SearchView searchView = (SearchView) parentView.findViewById(R.id.sv_searchView);
            searchView.setOnQueryTextListener(this);
            if (mMediatorCallback.isConnected()) {
                if (labResultsType != null) {
                    tvTitle.setText(getResources().getString(R.string.title_lab_results));
                    if (isRecent) {
                        String fullUrl = API_URL_GET_LAB_ORDERS_INFO + mMediatorCallback.getCurrentUser().getCivilId() + "?data=recent";
                        getLabOrdersList(fullUrl);
                    }
                    {
                        String fullUrl = API_URL_GET_LAB_ORDERS_INFO + mMediatorCallback.getCurrentUser().getCivilId();
                        getLabOrdersList(fullUrl);
                    }
                    ibToolbarBackButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mToolbarControllerCallback.customToolbarBackButtonClicked();
                        }
                    });
                } else {
                    tvTitle.setText(encounterInfo.getDepartmentArrayList().get(0) + ", " + encounterInfo.getEstShortName());
                    searchView.setVisibility(View.GONE);
                    String fullUrl = API_URL_GET_LAB_ENCOUNTER_INFO + encounterInfo.getEncounterId();
                    getLabOrdersList(fullUrl);
                    ibToolbarBackButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mMediatorCallback.changeFragmentTo(HealthRecordDetailsFragment.newInstance(encounterInfo), encounterInfo.getEstFullname());
                        }
                    });
                }
            } else {
                displayAlert(getString(R.string.alert_no_connection));
            }
     /*   } else {
            ((ViewGroup) parentView.getParent()).removeView(parentView);
        }*/

        return parentView;
    }

    private void getLabOrdersList(String url) {
        mProgressDialog.showDialog();


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt(API_RESPONSE_CODE) == 0) {

                        Gson gson = new Gson();
                        ApiLabOrdersListHolder responseHolder = gson.fromJson(response.toString(), ApiLabOrdersListHolder.class);
                        Log.d("resp-dependants", response.getJSONArray(API_RESPONSE_RESULT).toString());
                        setupRecyclerView(responseHolder.getmResult());

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

    private void setupRecyclerView(ArrayList<ApiLabOrdersListHolder.ApiOredresList> getmResult) {
        mAdapter =
                new OrderLabRecyclerViewAdapter(mMediatorCallback, getmResult, mContext);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(rvLabOrders.getContext(),
                layoutManager.getOrientation());
        rvLabOrders.addItemDecoration(mDividerItemDecoration);
        rvLabOrders.setLayoutManager(layoutManager);
        rvLabOrders.setItemAnimator(new DefaultItemAnimator());
        rvLabOrders.setAdapter(mAdapter);

    }

    private void displayAlert(String msg) {
        rvLabOrders.setVisibility(View.GONE);
        tvAlert.setVisibility(View.VISIBLE);
        tvAlert.setText(msg);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mMediatorCallback.changeFragmentContainerVisibility(View.GONE, View.VISIBLE);
        mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.VISIBLE);
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
}
