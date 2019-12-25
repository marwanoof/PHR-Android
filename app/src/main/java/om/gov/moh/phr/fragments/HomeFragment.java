package om.gov.moh.phr.fragments;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
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
import om.gov.moh.phr.adapters.PaginationRecyclerViewAdapter;
import om.gov.moh.phr.apimodels.ApiDemographicsHolder;
import om.gov.moh.phr.interfaces.AdapterToFragmentConnectorInterface;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.MyProgressDialog;
import om.gov.moh.phr.models.Pagination;

import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_MESSAGE;

public class HomeFragment extends Fragment implements AdapterToFragmentConnectorInterface {

    private static final String API_URL_GET_DEMOGRAPHICS_INFO = API_NEHR_URL + "demographics/civilId/";

    private static final int NUMBER_OF_COLUMNS = 3;
    private Context mContext;
    private RequestQueue mQueue;
    private TextView tvAlert;
    private MyProgressDialog mProgressDialog;
    private RecyclerView rvGrid;
    private View parentView;
    private MediatorInterface mMediatorCallback;
    private ToolbarControllerInterface mToolbarCallback;
    private ArrayList<ApiDemographicsHolder.ApiDemographicItem.Patients> mPatientsArrayList = new ArrayList<>();
    private ApiDemographicsHolder.ApiDemographicItem mApiDemographicItem;
    private PaginationRecyclerViewAdapter mAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        mMediatorCallback = (MediatorInterface) context;
        mToolbarCallback = (ToolbarControllerInterface) context;
    }

    public HomeFragment() {
        // Required empty public constructor
    }


    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      /*  if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            // Inflate the layout for this fragment

            parentView = inflater.inflate(R.layout.fragment_home, container, false);


            mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
            mProgressDialog = new MyProgressDialog(mContext);
            tvAlert = parentView.findViewById(R.id.tv_alert);
            rvGrid = parentView.findViewById(R.id.vp_container);

   /*     if (mMediatorCallback.isConnected()) {
            setRecyclerViewGrid();
            getDemographicResponse();


        } else {
            displayAlert(getString(R.string.alert_no_connection));
        }*/
            setRecyclerViewGrid();
            if(mApiDemographicItem==null)
                getDemographicResponse();
            else {
                mAdapter.updateList(getPaginationArrayList());
                setupData(mApiDemographicItem);
            }

        return parentView;
    }


    private void getDemographicResponse() {
        mProgressDialog.showDialog();

        String fullUrl = API_URL_GET_DEMOGRAPHICS_INFO + mMediatorCallback.getCurrentUser().getCivilId() + "?source=PHR";
//        String fullUrl = API_URL_GET_DEMOGRAPHICS_INFO + "62163078" + "?source=PHR";
        Log.d("fullURL-demo", fullUrl);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fullUrl, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt(API_RESPONSE_CODE) == 0) {
                        Gson gson = new Gson();
                        ApiDemographicsHolder responseHolder = gson.fromJson(response.toString(), ApiDemographicsHolder.class);
                        Log.d("resp-demo", response.getJSONObject("result").toString());

                        mApiDemographicItem = responseHolder.getmResult();
                        mAdapter.updateList(getPaginationArrayList());
                        setupData(responseHolder.getmResult());

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

    private void setupData(ApiDemographicsHolder.ApiDemographicItem apiDemographicItem) {
        TextView tvFullName = parentView.findViewById(R.id.tv_name);
        TextView tvCivilId = parentView.findViewById(R.id.tv_civil_id);
        mPatientsArrayList = apiDemographicItem.getInstitutesArrayList();
        tvFullName.setText(apiDemographicItem.getFullName());
        tvCivilId.setText(mMediatorCallback.getCurrentUser().getCivilId());
    }


    private void setRecyclerViewGrid() {
        mAdapter = new PaginationRecyclerViewAdapter(HomeFragment.this, mContext);
        rvGrid.setLayoutManager(new GridLayoutManager(mContext, NUMBER_OF_COLUMNS));
        rvGrid.setItemAnimator(new DefaultItemAnimator());
        rvGrid.setHasFixedSize(true);
        rvGrid.setAdapter(mAdapter);
    }

    private ArrayList<Pagination> getPaginationArrayList() {
        ArrayList<Pagination> paginationArrayList = new ArrayList<>();

        paginationArrayList.add(new Pagination(
                R.drawable.demographic,
                getString(R.string.title_demographic),
                DemographicsFragment.newInstance(mApiDemographicItem),
                DemographicsFragment.class.getSimpleName()));


        paginationArrayList.add(new Pagination(
                R.drawable.body_measurement,
                getString(R.string.title_body_measurements),
                BodyMeasurementsFragment.newInstance(mApiDemographicItem.getRecentVitalsArrayList()),
                BodyMeasurementsFragment.class.getSimpleName()));

        paginationArrayList.add(new Pagination(
                R.drawable.vital_info,
                getString(R.string.title_vital_info),
                        VitalInfoFragment.newInstance(),
                VitalInfoFragment.class.getSimpleName()));


        paginationArrayList.add(new Pagination(
                R.drawable.health_record,
                getString(R.string.title_health_records),
                HealthRecordListFragment.newInstance(),
                HealthRecordListFragment.class.getSimpleName()));

        paginationArrayList.add(new Pagination(
                R.drawable.lab_result,
                getString(R.string.title_lab_results),
                LabResultsContainerFragment.newInstance(),
                LabResultsFragment.class.getSimpleName()));

        paginationArrayList.add(new Pagination(
                R.drawable.medication,
                getString(R.string.title_medication),
                MedicationContainerFragment.newInstance(),
                MedicationFragment.class.getSimpleName()));

        paginationArrayList.add(new Pagination(
                R.drawable.proc_report,
                getString(R.string.title_procedures_reports),
                ProceduresReportsContainerFragment.newInstance(),
                ProceduresReportsFragment.class.getSimpleName()));

        paginationArrayList.add(new Pagination(
                R.drawable.other_doc,
                getString(R.string.title_other_document),
                DocsContainerFragment.newInstance(),
                ""));

        paginationArrayList.add(new Pagination(
                R.drawable.immunization,
                getString(R.string.title_immunization),
                ImmunizationContainerFragment.newInstance(),
                ImmunizationFragment.class.getSimpleName()));

        return paginationArrayList;
    }


    private void displayAlert(String msg) {
        rvGrid.setVisibility(View.GONE);
        tvAlert.setVisibility(View.VISIBLE);
        tvAlert.setText(msg);
    }

    @Override
    public <T> void onMyListItemClicked(T dataToPass, String dataTitle) {

        if (mApiDemographicItem != null) {
            mMediatorCallback.changeFragmentContainerVisibility(View.VISIBLE, View.GONE);
            mToolbarCallback.changeSideMenuToolBarVisibility(View.GONE);
            mMediatorCallback.changeFragmentTo((Fragment) dataToPass, dataTitle);
        }
    }

    @Override
    public <T> void onMyListItemClicked(T dataToPass, String dataTitle, int position) {

    }


}