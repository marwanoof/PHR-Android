package om.gov.moh.phr.fragments;


import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import om.gov.moh.phr.adapters.HealthRecordsRecyclerViewAdapter;
import om.gov.moh.phr.adapters.YearsRecyclerViewAdapter;
import om.gov.moh.phr.apimodels.ApiEncountersHolder;
import om.gov.moh.phr.interfaces.AdapterToFragmentConnectorInterface;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.MyProgressDialog;

import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HealthRecordListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HealthRecordListFragment extends Fragment implements AdapterToFragmentConnectorInterface, SwipeRefreshLayout.OnRefreshListener {

    private HealthRecordsRecyclerViewAdapter mHelathRecordsAdapter;
    private RequestQueue mQueue;
    private MyProgressDialog mProgressDialog;
    private Context mContext;
    private MediatorInterface mMediatorCallback;
    private ToolbarControllerInterface mToolbarCallback;
    private TextView tvAlert;
    private RecyclerView rvHealthRecords;
    private RecyclerView rvYears;
    private View vResultsFound;
    private TextView tvResultsFound;
    private ArrayList<ApiEncountersHolder.Encounter> mEncountersList;
    private YearsRecyclerViewAdapter mYearsAdapter;
    private ArrayList<ApiEncountersHolder.Encounter> result ;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchView searchView;
    private String pageTitle;
    private static final String PARAM1 = "PARAM1";
    //ArrayList<ApiEncountersHolder.Encounter> filteredList;
    public HealthRecordListFragment() {
        // Required empty public constructor
    }


    public static HealthRecordListFragment newInstance(String title) {
        HealthRecordListFragment fragment = new HealthRecordListFragment();
        Bundle args = new Bundle();
        args.putString(PARAM1, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        mMediatorCallback = (MediatorInterface) context;
        mToolbarCallback = (ToolbarControllerInterface) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pageTitle = (String) getArguments().getString(PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        View parentView = inflater.inflate(R.layout.fragment_health_record_list, container, false);



        //simple toolbar
        ImageButton ibToolbarBackButton = parentView.findViewById(R.id.ib_toolbar_back_button);
        ibToolbarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mToolbarCallback.customToolbarBackButtonClicked();
            }
        });
        TextView tvToolBarTitle = parentView.findViewById(R.id.tv_toolbar_title);
        tvToolBarTitle.setText(pageTitle);
        tvToolBarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mToolbarCallback.customToolbarBackButtonClicked();
            }
        });
        mProgressDialog = new MyProgressDialog(mContext);// initializes progress dialog
        mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory())); // initializes mQueue : we need to use  Volley.newRequestQueue(this, new HurlStack(null, getSocketFactory())) because we need to connect the app to secure server "https".
        rvHealthRecords = parentView.findViewById(R.id.rv_health_records);
        setRecyclerView();
        tvAlert = parentView.findViewById(R.id.tv_alert);
        rvYears = parentView.findViewById(R.id.rv_years);
        setupListView();
        vResultsFound = parentView.findViewById(R.id.v_results_found);
        tvResultsFound = parentView.findViewById(R.id.tv_results_found);
        vResultsFound.setVisibility(View.GONE);
        tvResultsFound.setVisibility(View.GONE);

        searchView = parentView.findViewById(R.id.searchViewHealthRecord);
        /*TextView searchTextView = (TextView) parentView.findViewById(androidx.appcompat.R.id.search_src_text);
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(),"sky.ttf");
        searchTextView.setTypeface(typeface);*/
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.length() == 0){
                    updateRecyclerViewItems(mEncountersList);
                }else {
                    ArrayList<ApiEncountersHolder.Encounter> filteredList = new ArrayList<>();
                    for (ApiEncountersHolder.Encounter encounter : mEncountersList) {
                        if (encounter.getDepartmentArrayList().get(0).getValue().toLowerCase().contains(s) ||
                                encounter.getEstShortName().toLowerCase().contains(s) ||
                                encounter.getEstFullname().toLowerCase().contains(s) ||
                                encounter.getPatientClass().toLowerCase().contains(s))
                        {
                            filteredList.add(encounter);
                        }

                    }
                    updateRecyclerViewItems(filteredList);
                }

                return false;
            }
        });

        swipeRefreshLayout = parentView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        /*swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        getEncounterResponse();
                                    }
                                }
        );*/
        getEncounterResponse();



        return parentView;
}

    private void setupListView() {
        mYearsAdapter = new YearsRecyclerViewAdapter(HealthRecordListFragment.this, mContext);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false);
        rvYears.setLayoutManager(mLayoutManager);
        rvYears.setItemAnimator(new DefaultItemAnimator());
        rvYears.setAdapter(mYearsAdapter);
    }

    private void getEncounterResponse() {
        mProgressDialog.showDialog();
        // showing refresh animation before making http call
        //swipeRefreshLayout.setRefreshing(true);
        Log.d("enc", "Called");
        String fullUrl = API_NEHR_URL + "encounter/v2/civilId";
        //String fullUrl = "https://5.162.223.156/nehrapi/encounter/civilId/" + mMediatorCallback.getCurrentUser().getCivilId();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, fullUrl, getJSONRequestParams(mMediatorCallback.getCurrentUser().getCivilId(),"PHR")
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Activity activity = getActivity();
                if (activity != null) {
                    try {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {
                            Gson gson = new Gson();
                            Log.d("resp-encount", response.getJSONArray("result").toString());
                            ApiEncountersHolder responseHolder = gson.fromJson(response.toString(), ApiEncountersHolder.class);
                            Log.d("resp-encount", response.getJSONArray("result").toString());

                            mEncountersList = responseHolder.getResult();
                            tvResultsFound.setText(responseHolder.getResult().size() + " " + getResources().getString(R.string.results_found));
                            result = new ArrayList<>();
                            result.addAll(responseHolder.getResult());
                            updateYearsListView(result);
                            updateRecyclerViewItems(result);
                            //setUpAddresseesList(responseHolder.getmResult());

                        } else {
                            displayAlert(getResources().getString(R.string.no_record_found), View.VISIBLE, View.GONE);
                            mProgressDialog.dismissDialog();
                        }
                    } catch (JSONException e) {
//                    Log.d("enc", e.getMessage());

                        e.printStackTrace();
                    }

                    mProgressDialog.dismissDialog();
                    // showing refresh animation before making http call
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.d("enc", error.toString());
                Activity activity = getActivity();
                if (activity != null && isAdded()) {
                    mProgressDialog.dismissDialog();
                    // showing refresh animation before making http call
                    swipeRefreshLayout.setRefreshing(false);
                    error.printStackTrace();
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
    private JSONObject getJSONRequestParams(String civilId, String source) {
        Map<String, Object> params = new HashMap<>();
        params.put("civilId", Long.parseLong(civilId));
        params.put("source", source);
        return new JSONObject(params);
    }
    private void updateYearsListView(ArrayList<ApiEncountersHolder.Encounter> encounterArrayList) {
        ArrayList<String> yearsArrayList = new ArrayList<>();
        yearsArrayList.add(getString(R.string.title_all));
        for (ApiEncountersHolder.Encounter e : encounterArrayList) {
            String year = e.getEncounterYear();
            if (!yearsArrayList.contains(year)) {
                yearsArrayList.add(year);
            }
        }
        mYearsAdapter.updateList(yearsArrayList);
    }

    private void setRecyclerView() {
        rvHealthRecords.setVisibility(View.VISIBLE);

        mHelathRecordsAdapter = new HealthRecordsRecyclerViewAdapter(HealthRecordListFragment.this, mContext, mMediatorCallback);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(rvHealthRecords.getContext(),
                mLayoutManager.getOrientation());
        rvHealthRecords.addItemDecoration(mDividerItemDecoration);
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(mContext, R.anim.delay_slide_down);
        rvHealthRecords.setLayoutAnimation(animation);
        rvHealthRecords.setLayoutManager(mLayoutManager);
        rvHealthRecords.setItemAnimator(new DefaultItemAnimator());
        rvHealthRecords.setAdapter(mHelathRecordsAdapter);

    }

    private void updateRecyclerViewItems(ArrayList<ApiEncountersHolder.Encounter> result) {
        mHelathRecordsAdapter.submitList(result);
    }


    private void filter(String searchKey) {
        if (!searchKey.equalsIgnoreCase(getString(R.string.title_all))) {
            ArrayList<ApiEncountersHolder.Encounter> filteredList = new ArrayList<>();


            for (ApiEncountersHolder.Encounter encounter : mEncountersList) {
                if (encounter.getEncounterYear().contains(searchKey)) {
                    filteredList.add(encounter);
                }
            }
            tvResultsFound.setText(filteredList.size() + " " + getResources().getString(R.string.health_records_result_found) + searchKey + "!");

            updateRecyclerViewItems(filteredList);
        } else {
            tvResultsFound.setText(mEncountersList.size() + " " + getResources().getString(R.string.results_found));
            updateRecyclerViewItems(mEncountersList);
        }
    }


    private void displayAlert(String msg, int alertVisibility, int otherViewsVisibility) {
        rvHealthRecords.setVisibility(otherViewsVisibility);
        tvAlert.setVisibility(otherViewsVisibility);
        rvYears.setVisibility(otherViewsVisibility);
        vResultsFound.setVisibility(otherViewsVisibility);
        tvResultsFound.setVisibility(otherViewsVisibility);
        tvAlert.setVisibility(alertVisibility);
        tvAlert.setText(msg);
    }

    @Override
    public <T> void onMyListItemClicked(T dataToPass, String dataTitle) {
        filter((String) dataToPass);
    }

    @Override
    public <T> void onMyListItemClicked(T dataToPass, String dataTitle, int position) {
        mMediatorCallback.changeFragmentTo(HealthRecordDetailsFragment.newInstance(result.get(position)), "HeathRecordsDetails");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mToolbarCallback.changeSideMenuToolBarVisibility(View.VISIBLE);
    }

    @Override
    public void onRefresh() {
        getEncounterResponse();
    }


}
