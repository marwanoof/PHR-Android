package om.gov.moh.phr.fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import om.gov.moh.phr.R;
import om.gov.moh.phr.adapters.VitalInfoRecyclerViewAdapter;
import om.gov.moh.phr.apimodels.ApiEncountersHolder;
import om.gov.moh.phr.apimodels.ApiOtherDocsHolder;
import om.gov.moh.phr.apimodels.ApiProceduresReportsHolder;
import om.gov.moh.phr.apimodels.ApiVitalInfo;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.DividerItemDecorator;
import om.gov.moh.phr.models.GlobalMethodsKotlin;
import om.gov.moh.phr.models.MyProgressDialog;

import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;


public class VitalInfoFragment extends Fragment implements  View.OnClickListener {
    private static final String API_URL_GET_DEMOGRAPHICS_INFO = API_NEHR_URL + "demographics/getVitalInfo/";
    private RequestQueue mQueue;
    private MyProgressDialog mProgressDialog;
    private Context mContext;
    private MediatorInterface mMediatorCallback;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private RecyclerView rvAllergy, rvHistory, rvProblems;
    private TextView tvAlert;
    //private SwipeRefreshLayout swipeRefreshLayout;
    private ImageButton allergyExpBtn, medicalExpBtn, finalExpBtn;
    private CardView allergyCardView, medicalCardView, finalCardView;
    private LinearLayout linearLayoutAllergy, linearLayoutMedical, linearLayoutFinal, linearLayoutNoData;
    private ArrayList<ApiVitalInfo.ApiAllergy> allergyArrayList;
    private ArrayList<ApiVitalInfo.ApiHistory> historyArrayList;
    private ArrayList<ApiVitalInfo.ApiProblem> problemArrayList;
    private static final String ARG_PARAM1 = "ARG_PARAM1";
    private String pageTitle;
    private ConstraintLayout constraintLayout;
    public VitalInfoFragment() {
        // Required empty public constructor
    }


    public static VitalInfoFragment newInstance(String title) {
        VitalInfoFragment fragment = new VitalInfoFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        mMediatorCallback = (MediatorInterface) context;
        mToolbarControllerCallback = (ToolbarControllerInterface) context;
        mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.GONE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null) {
            if (getArguments().getSerializable(ARG_PARAM1) != null)
                pageTitle = (String) getArguments().getSerializable(ARG_PARAM1);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_vital_info, container, false);

        TextView tvToolbarTitle = parentView.findViewById(R.id.tv_toolbar_title);
        ImageButton ibToolbarBackButton = parentView.findViewById(R.id.ib_toolbar_back_button);
        tvToolbarTitle.setText(pageTitle);
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
        constraintLayout = parentView.findViewById(R.id.medicalHistoryConstraintLayout);
        rvAllergy = parentView.findViewById(R.id.rv_allergy_recycler_view);
        rvHistory = parentView.findViewById(R.id.rv_history_recycler_view);
        rvProblems = parentView.findViewById(R.id.rv_problem_recycler_view);
       // swipeRefreshLayout = parentView.findViewById(R.id.swipe_refresh_layout);

        allergyCardView = parentView.findViewById(R.id.cardView_allergy);
        allergyExpBtn = parentView.findViewById(R.id.btn_allergy_expand);

        medicalCardView = parentView.findViewById(R.id.cardView_medical);
        medicalExpBtn = parentView.findViewById(R.id.btn_medicalhistory_expand);

        linearLayoutAllergy = parentView.findViewById(R.id.ll_allergy);
        linearLayoutMedical = parentView.findViewById(R.id.ll_history);
        linearLayoutFinal = parentView.findViewById(R.id.ll_problem);
        linearLayoutNoData = parentView.findViewById(R.id.ll_no_record);

        finalExpBtn = parentView.findViewById(R.id.btn_final_expand);
        finalCardView = parentView.findViewById(R.id.cardView_final);

        allergyCardView.setVisibility(View.GONE);
        medicalCardView.setVisibility(View.GONE);
        finalCardView.setVisibility(View.GONE);
        linearLayoutFinal.setVisibility(View.GONE);
        linearLayoutMedical.setVisibility(View.GONE);
        linearLayoutAllergy.setVisibility(View.GONE);
        linearLayoutNoData.setVisibility(View.GONE);

        if (mMediatorCallback.isConnected()) {

            getVitalInfo();
          /*  swipeRefreshLayout.setOnRefreshListener(this);
            swipeRefreshLayout.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            swipeRefreshLayout.setRefreshing(true);
                                            rvAllergy.setVisibility(View.VISIBLE);
                                            rvHistory.setVisibility(View.VISIBLE);
                                            rvProblems.setVisibility(View.VISIBLE);
                                            tvAlert.setVisibility(View.GONE);
                                            getVitalInfo();
                                        }
                                    }
            );*/
        } else {
            displayAlert(getString(R.string.alert_no_connection));
        }

        linearLayoutAllergy.setOnClickListener(this);
        linearLayoutMedical.setOnClickListener(this);
        linearLayoutFinal.setOnClickListener(this);


        return parentView;

    }

    private void getVitalInfo() {
        mProgressDialog.showDialog();
        // showing refresh animation before making http call
       // swipeRefreshLayout.setRefreshing(true);
        String fullUrl = API_URL_GET_DEMOGRAPHICS_INFO + mMediatorCallback.getCurrentUser().getCivilId();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fullUrl, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Activity activity = getActivity();
                    if (activity != null && isAdded()) {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {

                            Gson gson = new Gson();
                            ApiVitalInfo responseHolder = gson.fromJson(response.toString(), ApiVitalInfo.class);
                            allergyArrayList = responseHolder.getmResult().getmAllergyResult();
                            historyArrayList = responseHolder.getmResult().getmHistoryResult();
                            problemArrayList = responseHolder.getmResult().getmProblemsResult();

                            if (allergyArrayList.size() != 0) {
                                allergyCardView.setVisibility(View.VISIBLE);
                                allergyCardView.setAnimation(GlobalMethodsKotlin.Companion.setAnimation(mContext,R.anim.slide_down));
                                linearLayoutAllergy.setVisibility(View.VISIBLE);
                                linearLayoutAllergy.setAnimation(GlobalMethodsKotlin.Companion.setAnimation(mContext,R.anim.fade_in));
                                getAllergyResponse(responseHolder.getmResult().getmAllergyResult());
                            }

                            if (historyArrayList.size() != 0){
                                medicalCardView.setVisibility(View.VISIBLE);
                                medicalCardView.setAnimation(GlobalMethodsKotlin.Companion.setAnimation(mContext,R.anim.slide_down));
                                linearLayoutMedical.setVisibility(View.VISIBLE);
                                linearLayoutMedical.setAnimation(GlobalMethodsKotlin.Companion.setAnimation(mContext,R.anim.fade_in));
                                getHistoryResponse(responseHolder.getmResult().getmHistoryResult());
                            }else {
                                ConstraintSet constraintSet = new ConstraintSet();
                                constraintSet.clone(constraintLayout);
                                constraintSet.connect(R.id.ll_problem,ConstraintSet.TOP,R.id.cardView_allergy,ConstraintSet.BOTTOM,15);
                                constraintSet.applyTo(constraintLayout);
                            }

                            if (problemArrayList.size() != 0){
                                finalCardView.setVisibility(View.VISIBLE);
                                finalCardView.setAnimation(GlobalMethodsKotlin.Companion.setAnimation(mContext,R.anim.slide_down));
                                linearLayoutFinal.setVisibility(View.VISIBLE);
                                linearLayoutFinal.setAnimation(GlobalMethodsKotlin.Companion.setAnimation(mContext,R.anim.fade_in));
                                getProblemsResponse(responseHolder.getmResult().getmProblemsResult());

                            }
                            if (allergyArrayList.size() == 0 && historyArrayList.size() == 0 && problemArrayList.size() == 0){
                                linearLayoutNoData.setVisibility(View.VISIBLE);
                            }


                        } else {
                            displayAlert(getResources().getString(R.string.no_record_found));
                            mProgressDialog.dismissDialog();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mProgressDialog.dismissDialog();
                //swipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Activity activity = getActivity();
                if (activity != null && isAdded()) {
                    Log.d("resp-demographic", error.toString());
                    error.printStackTrace();
                    mProgressDialog.dismissDialog();
                    // showing refresh animation before making http call
                    //swipeRefreshLayout.setRefreshing(false);
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

    private void getAllergyResponse(ArrayList<ApiVitalInfo.ApiAllergy> response) {
        ArrayList<String> allergyArrayList = new ArrayList<>();
        for (int i = 0; i < response.size(); i++) {
            String obj = response.get(i).getNote();
            allergyArrayList.add(obj);
        }
        if (allergyArrayList.size() == 0)
            allergyArrayList.add(getString(R.string.no_allergy_msg));
        VitalInfoRecyclerViewAdapter vitalInfoRecyclerViewAdapter = new VitalInfoRecyclerViewAdapter(allergyArrayList, mContext);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(mContext, R.drawable.divider));
        rvAllergy.addItemDecoration(dividerItemDecoration);
        rvAllergy.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        rvAllergy.setAdapter(vitalInfoRecyclerViewAdapter);
    }

    private void getHistoryResponse(ArrayList<ApiVitalInfo.ApiHistory> response) {
        ArrayList<String> historyArrayList = new ArrayList<>();
        for (int i = 0; i < response.size(); i++) {
            String obj = response.get(i).getNote();
            historyArrayList.add(obj);
        }
        if (historyArrayList.size() == 0)
            historyArrayList.add(getString(R.string.no_history_msg));

        VitalInfoRecyclerViewAdapter vitalInfoRecyclerViewAdapter = new VitalInfoRecyclerViewAdapter(historyArrayList, mContext);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(mContext, R.drawable.divider));
        rvHistory.addItemDecoration(dividerItemDecoration);
        rvHistory.setLayoutManager(new LinearLayoutManager(mContext,
                RecyclerView.VERTICAL, false));
        rvHistory.setAdapter(vitalInfoRecyclerViewAdapter);


    }

    private void getProblemsResponse(ArrayList<ApiVitalInfo.ApiProblem> response) {
        ArrayList<String> problemsArrayList = new ArrayList<>();
        for (int i = 0; i < response.size(); i++) {
            String obj = response.get(i).getDisease();
            problemsArrayList.add(obj);
        }
        if (problemsArrayList.size() == 0)
            problemsArrayList.add(getString(R.string.no_problem_msg));

        VitalInfoRecyclerViewAdapter vitalInfoRecyclerViewAdapter = new VitalInfoRecyclerViewAdapter(problemsArrayList, mContext);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(mContext, R.drawable.divider));
        rvProblems.addItemDecoration(dividerItemDecoration);
        rvProblems.setLayoutManager(new LinearLayoutManager(mContext,
                RecyclerView.VERTICAL, false));
        rvProblems.setAdapter(vitalInfoRecyclerViewAdapter);

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
        mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.VISIBLE);
    }

 /*   @Override
    public void onRefresh() {
        rvAllergy.setVisibility(View.VISIBLE);
        rvHistory.setVisibility(View.VISIBLE);
        rvProblems.setVisibility(View.VISIBLE);
        tvAlert.setVisibility(View.GONE);
        getVitalInfo();
    }*/

    public void expandCollapseBtn(View view) {
        Bitmap imgArrow = ((BitmapDrawable)getResources().getDrawable(R.drawable.ic_arrow_down)).getBitmap();
        switch (view.getId()){
            case R.id.ll_allergy:

                Bitmap imgBtn = ((BitmapDrawable)allergyExpBtn.getDrawable()).getBitmap();

                if (imgBtn == imgArrow){


                    //allergyCardView.setAnimation(GlobalMethodsKotlin.Companion.setAnimation(mContext,R.anim.fade_out));
                    allergyCardView.setVisibility(View.GONE);

                    allergyExpBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_right));
                }else {

                    allergyCardView.setVisibility(View.VISIBLE);
                    //allergyCardView.setAnimation(GlobalMethodsKotlin.Companion.setAnimation(mContext,R.anim.slide_down));
                    allergyExpBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
                }
                break;
            case R.id.ll_history:
                Bitmap imgBtnApp = ((BitmapDrawable)medicalExpBtn.getDrawable()).getBitmap();

                if (imgBtnApp == imgArrow){

                    medicalCardView.setVisibility(View.GONE);
                    medicalExpBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_right));

                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(constraintLayout);
                    constraintSet.connect(R.id.ll_problem,ConstraintSet.TOP,R.id.ll_history,ConstraintSet.BOTTOM,15);
                    constraintSet.applyTo(constraintLayout);
                }else {

                    medicalCardView.setVisibility(View.VISIBLE);
                    medicalExpBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));

                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(constraintLayout);
                    constraintSet.connect(R.id.ll_problem,ConstraintSet.TOP,R.id.cardView_medical,ConstraintSet.BOTTOM,15);
                    constraintSet.applyTo(constraintLayout);
                }
                break;
            case R.id.ll_problem:
                Bitmap imgBtnNot = ((BitmapDrawable)finalExpBtn.getDrawable()).getBitmap();

                if (imgBtnNot == imgArrow){

                    finalCardView.setVisibility(View.GONE);
                    finalExpBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_right));
                }else {

                    finalCardView.setVisibility(View.VISIBLE);
                    finalExpBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
                }
                break;


        }
    }


    @Override
    public void onClick(View view) {

        expandCollapseBtn(view);
    }
}
