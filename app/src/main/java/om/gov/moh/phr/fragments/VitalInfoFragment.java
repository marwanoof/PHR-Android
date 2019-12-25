package om.gov.moh.phr.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import om.gov.moh.phr.R;
import om.gov.moh.phr.adapters.VitalInfoRecyclerViewAdapter;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.MyProgressDialog;

import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_HEALTH_NET_URL;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_MESSAGE;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_RESULT;


public class VitalInfoFragment extends Fragment {
    private static final String API_URL_GET_DEMOGRAPHICS_INFO = API_NEHR_URL + "demographics/getVitalInfo/";
    private static final String ALLERGY_KEY = "allergy";
    private static final String HISTORY_KEY = "history";
    private static final String PROBLEMS_KEY = "problems";
    private static final String PROBLEMS_DISEASE_KEY = "disease";
    private static final String HISTORY_NOTE_KEY = "note";
    private RequestQueue mQueue;
    private MyProgressDialog mProgressDialog;
    private Context mContext;
    private MediatorInterface mMediatorCallback;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private RecyclerView rvAllergy, rvHistory, rvProblems;
    private TextView tvAlert;

    public VitalInfoFragment() {
        // Required empty public constructor
    }


    public static VitalInfoFragment newInstance() {
        VitalInfoFragment fragment = new VitalInfoFragment();
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //       mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_vital_info, container, false);

        TextView tvToolbarTitle = parentView.findViewById(R.id.tv_toolbar_title);
        ImageButton ibToolbarBackButton = parentView.findViewById(R.id.ib_toolbar_back_button);
        tvToolbarTitle.setText(getResources().getString(R.string.title_vital_info));
        tvToolbarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mToolbarControllerCallback.customToolbarBackButtonClicked();
            }
        });
        ibToolbarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mToolbarControllerCallback.customToolbarBackButtonClicked();
            }
        });

        mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
        mProgressDialog = new MyProgressDialog(mContext);
        tvAlert = parentView.findViewById(R.id.tv_alert);
        rvAllergy = parentView.findViewById(R.id.rv_allergy_recycler_view);
        rvHistory = parentView.findViewById(R.id.rv_history_recycler_view);
        rvProblems = parentView.findViewById(R.id.rv_problem_recycler_view);
        if (mMediatorCallback.isConnected()) {

            getVitalInfo();

        } else {
            displayAlert(getString(R.string.alert_no_connection));
        }
        return parentView;

    }

    private void getVitalInfo() {
        mProgressDialog.showDialog();
        String fullUrl = API_URL_GET_DEMOGRAPHICS_INFO + mMediatorCallback.getCurrentUser().getCivilId();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fullUrl, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Activity activity = getActivity();
                    if(activity != null && isAdded()) {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {
                            getAllergyResponse(response);
                            getHistoryResponse(response);
                            getProblemsResponse(response);
                        } else {
                            displayAlert(response.getString(API_RESPONSE_MESSAGE));
                            mProgressDialog.dismissDialog();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mProgressDialog.dismissDialog();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Activity activity = getActivity();
                if(activity != null && isAdded()) {
                    Log.d("resp-demographic", error.toString());
                    error.printStackTrace();
                    mProgressDialog.dismissDialog();
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

    private void getAllergyResponse(JSONObject response) {
        try {
            JSONArray jsonAllergyArray = response.getJSONObject(API_RESPONSE_RESULT).getJSONArray(ALLERGY_KEY);
            ArrayList<String> allergyArrayList = new ArrayList<>();
            for (int i = 0; i < jsonAllergyArray.length(); i++) {
                JSONObject jsonObject = jsonAllergyArray.getJSONObject(i);

                String obj = jsonObject.getString("note");
                allergyArrayList.add(obj);
            }
            if (allergyArrayList.size() == 0)
                allergyArrayList.add(getString(R.string.no_allergy_msg));

            VitalInfoRecyclerViewAdapter vitalInfoRecyclerViewAdapter = new VitalInfoRecyclerViewAdapter(allergyArrayList, mContext);
            rvAllergy.addItemDecoration(new DividerItemDecoration(mContext,
                    LinearLayoutManager.VERTICAL));
            rvAllergy.setLayoutManager(new LinearLayoutManager(mContext,
                    RecyclerView.VERTICAL, false));
            rvAllergy.setAdapter(vitalInfoRecyclerViewAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getHistoryResponse(JSONObject response) {
        try {
            JSONArray jsonAllergyArray = response.getJSONObject(API_RESPONSE_RESULT).getJSONArray(HISTORY_KEY);
            ArrayList<String> historyArrayList = new ArrayList<>();
            for (int i = 0; i < jsonAllergyArray.length(); i++) {
                JSONObject jsonObject = jsonAllergyArray.getJSONObject(i);
                String obj = jsonObject.getString(HISTORY_NOTE_KEY);
                historyArrayList.add(obj);
            }
            if (historyArrayList.size() == 0)
                historyArrayList.add(getString(R.string.no_history_msg));

            VitalInfoRecyclerViewAdapter vitalInfoRecyclerViewAdapter = new VitalInfoRecyclerViewAdapter(historyArrayList, mContext);
            rvHistory.addItemDecoration(new DividerItemDecoration(mContext,
                    LinearLayoutManager.VERTICAL));
            rvHistory.setLayoutManager(new LinearLayoutManager(mContext,
                    RecyclerView.VERTICAL, false));
            rvHistory.setAdapter(vitalInfoRecyclerViewAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getProblemsResponse(JSONObject response) {
        try {
            JSONArray jsonAllergyArray = response.getJSONObject(API_RESPONSE_RESULT).getJSONArray(PROBLEMS_KEY);
            ArrayList<String> problemsArrayList = new ArrayList<>();
            for (int i = 0; i < jsonAllergyArray.length(); i++) {
                JSONObject jsonObject = jsonAllergyArray.getJSONObject(i);
                String obj = jsonObject.getString(PROBLEMS_DISEASE_KEY);
                problemsArrayList.add(obj);
            }
            if (problemsArrayList.size() == 0)
                problemsArrayList.add(getString(R.string.no_problem_msg));

            VitalInfoRecyclerViewAdapter vitalInfoRecyclerViewAdapter = new VitalInfoRecyclerViewAdapter(problemsArrayList, mContext);
            rvProblems.addItemDecoration(new DividerItemDecoration(mContext,
                    LinearLayoutManager.VERTICAL));
            rvProblems.setLayoutManager(new LinearLayoutManager(mContext,
                    RecyclerView.VERTICAL, false));
            rvProblems.setAdapter(vitalInfoRecyclerViewAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void displayAlert(String msg) {
        rvAllergy.setVisibility(View.GONE);
        rvHistory.setVisibility(View.GONE);
        rvProblems.setVisibility(View.GONE);
        tvAlert.setVisibility(View.VISIBLE);
        tvAlert.setText(msg);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mMediatorCallback.changeFragmentContainerVisibility(View.GONE, View.VISIBLE);
        mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.VISIBLE);
    }
}
