package om.gov.moh.phr.fragments;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import om.gov.moh.phr.adapters.LinkedInstitutesRecyclerViewAdapter;
import om.gov.moh.phr.apimodels.ApiDemographicsHolder;
import om.gov.moh.phr.apimodels.ApiPatientHolder;
import om.gov.moh.phr.interfaces.AdapterToFragmentConnectorInterface;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.MyProgressDialog;

import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;

public class LinkedInstitutesFragment extends Fragment implements AdapterToFragmentConnectorInterface {

    private static final String API_URL_GET_PENDING_API = API_NEHR_URL + "mpi/getPendingMpiLink?civilId=";


    private static final String PARAM_PATIENTS_ARRAY = "PARAM_PATIENTS_ARRAY";
    private ArrayList<ApiDemographicsHolder.ApiDemographicItem.Patients> mPatientsArrayList;
    private Context mContext;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private MediatorInterface mMediatorCallback;
    private RequestQueue mQueue;
    private TextView tvAlert;
    private MyProgressDialog mProgressDialog;
    private RecyclerView rvLinkedInstitutes;
    private LinkedInstitutesRecyclerViewAdapter mAdapter;

    public LinkedInstitutesFragment() {
        // Required empty public constructor
    }

    public static LinkedInstitutesFragment newInstance(ArrayList<ApiDemographicsHolder.ApiDemographicItem.Patients> patientsArrayList) {
        LinkedInstitutesFragment fragment = new LinkedInstitutesFragment();
        Bundle args = new Bundle();
        args.putSerializable(PARAM_PATIENTS_ARRAY, patientsArrayList);
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
            mPatientsArrayList = (ArrayList<ApiDemographicsHolder.ApiDemographicItem.Patients>) getArguments().getSerializable(PARAM_PATIENTS_ARRAY);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_linked_institutes, container, false);

        mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
        mProgressDialog = new MyProgressDialog(mContext);
        tvAlert = parentView.findViewById(R.id.tv_alert);
        rvLinkedInstitutes = parentView.findViewById(R.id.rv_linked_institutes);

//simple toolbar
        ImageButton ibToolbarBackButton = parentView.findViewById(R.id.ib_toolbar_back_button);
        ibToolbarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mToolbarControllerCallback.customToolbarBackButtonClicked();
            }
        });
        TextView tvToolBarTitle = parentView.findViewById(R.id.tv_toolbar_title);
        tvToolBarTitle.setText(getString(R.string.title_linked_institutes));

        ImageButton ibAddLinkingRequest = parentView.findViewById(R.id.ib_icon);
        ibAddLinkingRequest.setImageResource(R.drawable.ic_add_24dp);
        ibAddLinkingRequest.setColorFilter(ContextCompat.getColor(mContext,
                R.color.colorWhite));
        ibAddLinkingRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediatorCallback.changeFragmentTo(AddLinkingInstitutesFragment.newInstance(), AddLinkingInstitutesFragment.class.getSimpleName());
            }
        });


        if (mMediatorCallback.isConnected()) {
            setupRecyclerView();
            getPendingLinkedInstitutesRequests();
        } else {
            displayAlert(getString(R.string.alert_no_connection));
        }

        return parentView;
    }

    private void getPendingLinkedInstitutesRequests() {
        mProgressDialog.showDialog();
        String fullUrl = API_URL_GET_PENDING_API + mMediatorCallback.getCurrentUser().getCivilId();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fullUrl, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt(API_RESPONSE_CODE) == 0) {
                        Gson gson = new Gson();
                        ApiPatientHolder responseHolder
                                = gson.fromJson(response.toString(), ApiPatientHolder.class);
                        Log.d("resp-pending", response.getJSONArray("result").toString());

                        updateInstitutesList(responseHolder.getResult());

                    } else {
                        displayAlert(getResources().getString(R.string.no_record_found));
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
                Log.d("resp-pending", error.toString());
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

    private void setupRecyclerView() {

        mAdapter = new LinkedInstitutesRecyclerViewAdapter(LinkedInstitutesFragment.this, mContext, mPatientsArrayList);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(rvLinkedInstitutes.getContext(),
                layoutManager.getOrientation());
        rvLinkedInstitutes.addItemDecoration(mDividerItemDecoration);
        rvLinkedInstitutes.setLayoutManager(layoutManager);
        rvLinkedInstitutes.setItemAnimator(new DefaultItemAnimator());
        rvLinkedInstitutes.setAdapter(mAdapter);
    }

    private void updateInstitutesList(ArrayList<ApiDemographicsHolder.ApiDemographicItem.Patients> items) {
        for (ApiDemographicsHolder.ApiDemographicItem.Patients p : items) {
            p.setIsPending(true);
        }
        mAdapter.update(items);
        mAdapter.notifyDataSetChanged();
    }


    private void displayAlert(String msg) {
        rvLinkedInstitutes.setVisibility(View.GONE);
        tvAlert.setVisibility(View.VISIBLE);
        tvAlert.setText(msg);
    }

    @Override
    public <T> void onMyListItemClicked(T dataToPass, String dataTitle) {


    }

    @Override
    public <T> void onMyListItemClicked(T dataToPass, String dataTitle, int position) {

    }
}
