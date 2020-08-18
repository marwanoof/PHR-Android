package om.gov.moh.phr.fragments;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import om.gov.moh.phr.R;
import om.gov.moh.phr.adapters.VitalsGraphRecyclerViewAdapter;
import om.gov.moh.phr.apimodels.ApiVitalsPivotHolder;
import om.gov.moh.phr.interfaces.AdapterToFragmentConnectorInterface;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.MyProgressDialog;

import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_MESSAGE;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VitalsGraphFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VitalsGraphFragment extends Fragment implements AdapterToFragmentConnectorInterface, SwipeRefreshLayout.OnRefreshListener {
    //    public static final String API_URL = "https://5.162.223.156/nehrapi/";
    private static final String API_URL_GET_VITAL_SIGNS_PIVOT_LIST = API_NEHR_URL + "vitalSigns/pivot/";
    private static final String API_URL_GET_VITAL_SIGNS_CHART = API_NEHR_URL + "chart/vitalChart/";
    private static final String SELECTED_ITEM = "SELECTED_ITEM";
    private Context mContext;
    private MediatorInterface mMediatorCallback;
    private ToolbarControllerInterface toolbarControllerCallback;
    private RequestQueue mQueue;
    private MyProgressDialog mProgressDialog;
    private RecyclerView rvVitalsList;
    private WebView wvGraphViewHolder;
    private TextView tvAlert;
    private ImageButton ibHome, ibRefresh;
    private String selectedItem;
    private int itemPosition = 100;
    private SwipeRefreshLayout swipeRefreshLayout;

    public VitalsGraphFragment() {
        // Required empty public constructor
    }

    public static VitalsGraphFragment newInstance(String selectedItem) {
        VitalsGraphFragment fragment = new VitalsGraphFragment();
        Bundle args = new Bundle();
        args.putString(SELECTED_ITEM, selectedItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mMediatorCallback = (MediatorInterface) context;
        toolbarControllerCallback = (ToolbarControllerInterface) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedItem = getArguments().getString(SELECTED_ITEM);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_vitals_graph, container, false);

        mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
        mProgressDialog = new MyProgressDialog(mContext);
        rvVitalsList = parentView.findViewById(R.id.rv_graph_list);
        wvGraphViewHolder = parentView.findViewById(R.id.wv_graph_holder);
        tvAlert = parentView.findViewById(R.id.tv_alert);
        enableHomeandRefresh(parentView);
        ibRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPivotData();
            }
        });
        ibHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToHome();
            }
        });
        TextView tvToolbarTitle = parentView.findViewById(R.id.tv_toolbar_title);
        ImageButton ibToolbarBackButton = parentView.findViewById(R.id.ib_toolbar_back_button);
        tvToolbarTitle.setText(getResources().getString(R.string.title_vital_signs));
        tvToolbarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbarControllerCallback.customToolbarBackButtonClicked();
            }
        });
        ibToolbarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbarControllerCallback.customToolbarBackButtonClicked();
            }
        });


        //  setRecyclerView(rvVitalsGraphList, getItems());
//        setupWebView(wvGraphViewHolder);
//        setupWebView(wvGraphViewHolder);
//
        swipeRefreshLayout = parentView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);

                                        getPivotData();
                                    }
                                }
        );
        getPivotData();
        return parentView;
    }

    private void enableHomeandRefresh(View view) {
        ibHome = view.findViewById(R.id.ib_home);
        ibRefresh = view.findViewById(R.id.ib_refresh);
        ibHome.setVisibility(View.VISIBLE);
        ibRefresh.setVisibility(View.VISIBLE);
    }

    private void backToHome() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    private void getPivotData() {
        mProgressDialog.showDialog();
// showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);
        String fullUrl = API_URL_GET_VITAL_SIGNS_PIVOT_LIST + mMediatorCallback.getCurrentUser().getCivilId();//mMediatorCallback.getAccessTokenString().getAccessCivilId();
        Log.d("repo-graph", fullUrl);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fullUrl, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt(API_RESPONSE_CODE) == 0) {
                        Gson gson = new Gson();
                        ApiVitalsPivotHolder responseHolder = gson.fromJson(response.toString(), ApiVitalsPivotHolder.class);
                        // setRecyclerView(responseHolder.getResult());
                        Log.d("repo-graph", response.getJSONArray("result").toString());
                        for (int i = 0; i < responseHolder.getResult().size(); i++) {
                            if (responseHolder.getResult().get(i).getVitalSign().contains(selectedItem)) {
                                itemPosition = i;
                                setDefaultGraph(responseHolder.getResult().get(i));
                            }
                        }
                        setRecyclerView(responseHolder.getResult(), itemPosition);


                    } else {
                        displayAlert(getResources().getString(R.string.no_record_found));
                        Log.d("repo-graph", response.getString(API_RESPONSE_MESSAGE));
                        mProgressDialog.dismissDialog();
                    }
                } catch (JSONException e) {
//                    Log.d("enc", e.getMessage());

                    e.printStackTrace();
                }

                mProgressDialog.dismissDialog();
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.d("enc", error.toString());
                error.printStackTrace();
                mProgressDialog.dismissDialog();
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
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

    private void setDefaultGraph(final ApiVitalsPivotHolder.Pivot pivot) {
        mProgressDialog.showDialog();
        WebSettings settings = wvGraphViewHolder.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(false);
        settings.setLoadWithOverviewMode(false);
       // settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        wvGraphViewHolder.setVerticalScrollBarEnabled(false);
        wvGraphViewHolder.setHorizontalScrollBarEnabled(false);
        wvGraphViewHolder.getSettings().setLoadWithOverviewMode(false);
        wvGraphViewHolder.getSettings().setUseWideViewPort(false);
        wvGraphViewHolder.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        wvGraphViewHolder.setScrollbarFadingEnabled(false);

// zoom in zoom out in web view
        wvGraphViewHolder.getSettings().setSupportZoom(true);
        wvGraphViewHolder.getSettings().setBuiltInZoomControls(true);
        wvGraphViewHolder.getSettings().setDisplayZoomControls(false);

        wvGraphViewHolder.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                mProgressDialog.dismissDialog();
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                view.evaluateJavascript("displayGraph(" + getItemsArrayList(pivot) + ")", null);
                /*} else {
                    view.loadUrl("javascript:loadMsg('How are you today!')");
                }*/
            }
        });
        wvGraphViewHolder.loadUrl("file:///android_asset/graph.html");

    }

    private JSONArray getItemsArrayList(ApiVitalsPivotHolder.Pivot pivot) {
        JSONArray container = new JSONArray();
        JSONArray item;
        item = new JSONArray();
        item.put("Year");// x axis
        item.put(pivot.getVitalSign()); // y axis
        container.put(item);
        try {
            if (pivot.getValue1() != 0) {
                item = new JSONArray();
                item.put(1); // y axis
                item.put(pivot.getValue1());// x axis
                container.put(item);
            }
            if (pivot.getValue2() != 0) {
                item = new JSONArray();
                item.put(2); // y axis
                item.put(pivot.getValue2());// x axis
                container.put(item);
            }
            if (pivot.getValue3() != 0) {
                item = new JSONArray();
                item.put(3); // y axis
                item.put(pivot.getValue3());// x axis
                container.put(item);
            }
            if (pivot.getValue4() != 0) {
                item = new JSONArray();
                item.put(4); // y axis
                item.put(pivot.getValue4());// x axis
                container.put(item);
            }
            if (pivot.getValue5() != 0) {
                item = new JSONArray();
                item.put(5); // y axis
                item.put(pivot.getValue5());// x axis
                container.put(item);
            }
            if (pivot.getValue6() != 0) {
                item = new JSONArray();
                item.put(6); // y axis
                item.put(pivot.getValue6());// x axis
                container.put(item);
            }
            if (pivot.getValue7() != 0) {
                item = new JSONArray();
                item.put(7); // y axis
                item.put(pivot.getValue7());// x axis
                container.put(item);
            }
            if (pivot.getValue8() != 0) {
                item = new JSONArray();
                item.put(8); // y axis
                item.put(pivot.getValue8());// x axis
                container.put(item);
            }
            if (pivot.getValue9() != 0) {
                item = new JSONArray();
                item.put(9); // y axis
                item.put(pivot.getValue9());// x axis
                container.put(item);
            }
            if (pivot.getValue10() != 0) {
                item = new JSONArray();
                item.put(10); // y axis
                item.put(pivot.getValue10());// x axis
                container.put(item);
            }
            /*for(int i =0; i < container.length();i++){
                try {

                    Log.d("mJSON", container.get(i).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }*/


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return container;
    }

    private void displayAlert(String msg) {
        rvVitalsList.setVisibility(View.GONE);
        wvGraphViewHolder.setVisibility(View.GONE);
        tvAlert.setVisibility(View.VISIBLE);
        tvAlert.setText(msg);
    }


    private void setRecyclerView(ArrayList<ApiVitalsPivotHolder.Pivot> result, int activatedItem) {
        VitalsGraphRecyclerViewAdapter mAdapter =
                new VitalsGraphRecyclerViewAdapter(VitalsGraphFragment.this, mContext, result, activatedItem, selectedItem);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(mContext,
                ((LinearLayoutManager) mLayoutManager).getOrientation());
        rvVitalsList.addItemDecoration(mDividerItemDecoration);
        rvVitalsList.setLayoutManager(mLayoutManager);
        rvVitalsList.setItemAnimator(new DefaultItemAnimator());
        if (activatedItem != 100)
            rvVitalsList.smoothScrollToPosition(activatedItem);
        rvVitalsList.setAdapter(mAdapter);
        mProgressDialog.dismissDialog();
    }


    @Override
    public <T> void onMyListItemClicked(T dataToPass, String dataTitle) {
        setDefaultGraph((ApiVitalsPivotHolder.Pivot) dataToPass);
    }

    @Override
    public <T> void onMyListItemClicked(T dataToPass, String dataTitle, int position) {

    }

    @Override
    public void onRefresh() {
        getPivotData();
    }
}
