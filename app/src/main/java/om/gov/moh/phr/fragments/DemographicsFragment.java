package om.gov.moh.phr.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import om.gov.moh.phr.R;
import om.gov.moh.phr.activities.MainActivity;
import om.gov.moh.phr.apimodels.ApiDependentsHolder;
import om.gov.moh.phr.apimodels.ApiHomeHolder;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.GlobalMethodsKotlin;
import om.gov.moh.phr.models.MyProgressDialog;

import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_ENGLISH;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_PREFS;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_SELECTED;
import static om.gov.moh.phr.models.MyConstants.PARAM_API_DEMOGRAPHICS_ITEM;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DemographicsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DemographicsFragment extends Fragment {
    private static final String API_URL_DEPENDENT_INFO = API_NEHR_URL + "demographics/v2/dependent";
    private static final String DEPENDENT_CIVILID = "DependentCivilID";
    private static final String PARAM2 = "PARAM2";
    private Context mContext;
    private ArrayList<ApiHomeHolder.Patients> mApiDemographicItem;

    private RequestQueue mQueue;
    private TextView tvAlert;
    private MyProgressDialog mProgressDialog;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private MediatorInterface mMediatorCallback;
    private TextView tvNoDependents, tvNoInstitutes;
    private ScrollView scrollView;
    private String pageTitle;
    private CardView cvNoDependentsRecord, cvNoInstitutesRecord;

    public static DemographicsFragment newInstance() {

        Bundle args = new Bundle();

        DemographicsFragment fragment = new DemographicsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static DemographicsFragment newInstance(ArrayList<ApiHomeHolder.Patients> apiDemographicItem, String title) {
        DemographicsFragment fragment = new DemographicsFragment();
        Bundle args = new Bundle();
        args.putSerializable(PARAM_API_DEMOGRAPHICS_ITEM, apiDemographicItem);
        args.putSerializable(PARAM2, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;

        mMediatorCallback = (MediatorInterface) context;
        mToolbarControllerCallback = (ToolbarControllerInterface) context;
        mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.GONE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mApiDemographicItem = (ArrayList<ApiHomeHolder.Patients>) getArguments().getSerializable(PARAM_API_DEMOGRAPHICS_ITEM);
            pageTitle = (String) getArguments().getSerializable(PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_demographics, container, false);

        mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
        mProgressDialog = new MyProgressDialog(mContext);
        tvAlert = parentView.findViewById(R.id.tv_alert);
        scrollView = parentView.findViewById(R.id.scroll_view);


        //simple toolbar
        ImageButton ibToolbarBackButton = parentView.findViewById(R.id.ib_toolbar_back_button);
        ibToolbarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mToolbarControllerCallback.customToolbarBackButtonClicked();
            }
        });
        TextView tvToolBarTitle = parentView.findViewById(R.id.tv_toolbar_title);
        tvToolBarTitle.setText(pageTitle);
        tvToolBarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mToolbarControllerCallback.customToolbarBackButtonClicked();
            }
        });
        tvNoDependents = parentView.findViewById(R.id.tv_dependents_alert);
        tvNoInstitutes = parentView.findViewById(R.id.tv_institutes_alert);
        cvNoDependentsRecord = parentView.findViewById(R.id.cardViewNoDependentsRecords);
        cvNoInstitutesRecord = parentView.findViewById(R.id.cardViewNoInstitutesRecords);
        //setupPersonalInfo(parentView);
        LinearLayout llDependentsContainer = parentView.findViewById(R.id.ll_dependents_container);
        if (mMediatorCallback.isConnected()) {
            getDependents(container, inflater, llDependentsContainer);
        } else {
            GlobalMethodsKotlin.Companion.showAlertDialog(mContext, getResources().getString(R.string.no_internet_title), getResources().getString(R.string.alert_no_connection), getResources().getString(R.string.ok), R.drawable.ic_error);
        }


        LinearLayout llInstitutesContainer = parentView.findViewById(R.id.ll_institutes_container);
        prepareInstitutesCards(container, inflater, llInstitutesContainer);

        return parentView;
    }

    /*private void setupPersonalInfo(View parentView) {
        ImageView ivProfile = parentView.findViewById(R.id.iv_avatar);
        TextView tvName = parentView.findViewById(R.id.tv_name);
        TextView tvCivilId = parentView.findViewById(R.id.tv_civil_id);
        TextView tvAge = parentView.findViewById(R.id.tv_age);
        TextView tvDOB = parentView.findViewById(R.id.tv_month);
        TextView tvGender = parentView.findViewById(R.id.tv_gender);
        TextView tvNationality = parentView.findViewById(R.id.tv_nationality);
        TextView tvNoOfDonations = parentView.findViewById(R.id.tv_blood_drop);
        TextView tvBloddGroup = parentView.findViewById(R.id.tv_blood_bag);
        View G6PDView = parentView.findViewById(R.id.view3);
        TextView tvG6PD = parentView.findViewById(R.id.tv_G6PD);
        View chronicView = parentView.findViewById(R.id.view4);
        ImageView ivChronic = parentView.findViewById(R.id.iv_chronic);
        View pregnantView = parentView.findViewById(R.id.view5);
        ImageView ivPregnant = parentView.findViewById(R.id.iv_pregnant);
        tvName.setText(mApiDemographicItem.getFullName());
        if (mApiDemographicItem.getAliasYn().equals("Y"))
            tvCivilId.setText("XXXXXXXX");
        else
            tvCivilId.setText(mApiDemographicItem.getCivilId());
        Glide.with(mContext).load(mApiDemographicItem.getPersonPhoto(mContext)).into(ivProfile);
        tvAge.setText(mApiDemographicItem.getNewAge());
        tvDOB.setText(mApiDemographicItem.getDob());
        tvGender.setText(mApiDemographicItem.getGenderfull());
        tvNationality.setText(mApiDemographicItem.getNationalityDesc());
        tvNoOfDonations.setText(mApiDemographicItem.getDonorCount());
        tvBloddGroup.setText(mApiDemographicItem.getBloodGroup());
        if (mApiDemographicItem.getRecentVitalsArrayList() != null) {
            for (int i = 0; i < mApiDemographicItem.getRecentVitalsArrayList().size(); i++) {
                if (mApiDemographicItem.getRecentVitalsArrayList().get(i).getName().equals("G6PD")) {
                    G6PDView.setVisibility(View.VISIBLE);
                    tvG6PD.setVisibility(View.VISIBLE);
                    final int finalI = i;
                    G6PDView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(mContext, mApiDemographicItem.getRecentVitalsArrayList().get(finalI).getValue(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                }
            }
        }
        if(mApiDemographicItem.getAlertsArrayList()!=null){
            for (int i = 0; i < mApiDemographicItem.getAlertsArrayList().size(); i++) {
                if (mApiDemographicItem.getAlertsArrayList().get(i).getCodeDescription().equals("Chronic Case DM")) {
                    chronicView.setVisibility(View.VISIBLE);
                    ivChronic.setVisibility(View.VISIBLE);
                    final int finalI = i;
                    chronicView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(mContext, mApiDemographicItem.getAlertsArrayList().get(finalI).getCodeDescription(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                if (mApiDemographicItem.getAlertsArrayList().get(i).getCodeDescription().equals("Pregnant")) {
                    pregnantView.setVisibility(View.VISIBLE);
                    ivPregnant.setVisibility(View.VISIBLE);
                    final int finalI1 = i;
                    pregnantView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(mContext, mApiDemographicItem.getAlertsArrayList().get(finalI1).getCodeDescription(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }

    }*/

    private void getDependents(final ViewGroup container, final LayoutInflater inflater, final LinearLayout llDependentsContainer) {
        mProgressDialog.showDialog();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, API_URL_DEPENDENT_INFO, getJSONRequestParams()
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mContext != null && isAdded()) {
                    try {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {
                            Gson gson = new Gson();
                            ApiDependentsHolder responseHolder = gson.fromJson(response.toString(), ApiDependentsHolder.class);
                            if (responseHolder != null)
                                prepareDependantsCards(responseHolder, container, inflater, llDependentsContainer);


                        } else {
                            tvNoDependents.setVisibility(View.VISIBLE);
                            cvNoDependentsRecord.setVisibility(View.VISIBLE);
                            // displayAlert(getResources().getString(R.string.no_record_found));
                            mProgressDialog.dismissDialog();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    mProgressDialog.dismissDialog();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mContext != null && isAdded()) {
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

    private JSONObject getJSONRequestParams() {
        Map<String, String> params = new HashMap<>();
        params.put("data", "");
        params.put("source", "");
        params.put("civilId", mMediatorCallback.getCurrentUser().getCivilId());
        return new JSONObject(params);

    }

    private void prepareDependantsCards(ApiDependentsHolder responseHolder, ViewGroup container, LayoutInflater inflater, LinearLayout llDependentsContainer) {
        int size = responseHolder.getResult().size();

        for (int i = 0; i < size; i++) {
            if (i == 0) {//top
                addDependentCard(container, inflater, llDependentsContainer, R.drawable.shape_rect_round_top, responseHolder.getResult().get(i));
            }
            if (i > 0 && i < size - 1) {//center
                addDependentCard(container, inflater, llDependentsContainer, R.drawable.shape_rect_round_center, responseHolder.getResult().get(i));
            }
            if (i == size - 1 && size != 1) { //bottom
                addDependentCard(container, inflater, llDependentsContainer, R.drawable.shape_rect_round_bottom, responseHolder.getResult().get(i));
            }
        }
    }


    /**
     * this function will add programmatically dependent card to dependants container
     *
     * @param container
     * @param inflater
     * @param llDependentsContainer
     * @param cardShape             : R.drawable.shape_rect_round_top
     * @param dependent
     */
    private void addDependentCard(ViewGroup container, @NonNull LayoutInflater inflater, LinearLayout llDependentsContainer, int cardShape, final ApiDependentsHolder.Dependent dependent) {
        View dependentCard = inflater.inflate(R.layout.fragment_demographics_dependents_card, container, false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMarginStart(16);
        params.setMarginEnd(16);
        dependentCard.setLayoutParams(params);
        dependentCard.setBackground(ContextCompat.getDrawable(mContext, cardShape));

        TextView tvDName = dependentCard.findViewById(R.id.tv_dependant_name);
        tvDName.setText(dependent.getDependentName());
        dependentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mContext, dependent.getDependentName() + "/" + dependent.getDependentCivilId() + "/" + dependent.getRelationType(), Toast.LENGTH_SHORT).show();

                updateCurrentUser(dependent.getDependentCivilId());

            }
        });

        llDependentsContainer.addView(dependentCard);

        View divider = inflater.inflate(R.layout.divider, container, false);
        llDependentsContainer.addView(divider);
    }

    private void updateCurrentUser(String civilId) {
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra(DEPENDENT_CIVILID, civilId);
        startActivity(intent);
    }


    private void prepareInstitutesCards(ViewGroup container, LayoutInflater inflater, LinearLayout llDependentsContainer) {

        ArrayList<ApiHomeHolder.Patients> institutesArrayList = mApiDemographicItem;
        if (mApiDemographicItem.size() > 0) {
            int size = institutesArrayList.size();

            for (int i = 0; i < size; i++) {
                if (i == 0) {//top
                    addInstituteCard(container, inflater, llDependentsContainer, R.drawable.shape_rect_round_top, institutesArrayList.get(i));
                }
                if (i > 0 && i < size - 1) {//center
                    addInstituteCard(container, inflater, llDependentsContainer, R.drawable.shape_rect_round_center, institutesArrayList.get(i));
                }
                if (i == size - 1 && size != 1) { //bottom
                    addInstituteCard(container, inflater, llDependentsContainer, R.drawable.shape_rect_round_bottom, institutesArrayList.get(i));
                }
            }
        } else {
            tvNoInstitutes.setVisibility(View.VISIBLE);
            cvNoInstitutesRecord.setVisibility(View.VISIBLE);
        }
    }

    private void addInstituteCard(ViewGroup container, @NonNull LayoutInflater inflater, LinearLayout llInstitutesContainer, int cardShape, final ApiHomeHolder.Patients patients) {
        View instituteCard = inflater.inflate(R.layout.fragment_demographics_dependents_card, container, false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMarginStart(16);
        params.setMarginEnd(16);
        instituteCard.setLayoutParams(params);
        instituteCard.setBackground(ContextCompat.getDrawable(mContext, cardShape));

        TextView tvDName = instituteCard.findViewById(R.id.tv_dependant_name);
        String patientID = patients.getEstPatientId();
        String grayColoredID = "<br><br><font color='#B1BCBE'>" + patientID + "</font>";
        if (getStoredLanguage().equals(LANGUAGE_ENGLISH))
            tvDName.setText(Html.fromHtml(patients.getEstName() + grayColoredID));
        else
            tvDName.setText(Html.fromHtml(patients.getEstNameNls() + grayColoredID));
        /*instituteCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, patients.getEstName() + "/" + patients.getEstPatientId(), Toast.LENGTH_SHORT).show();
            }
        });*/

        ImageButton ibArrow = instituteCard.findViewById(R.id.ib_arrow);
        ibArrow.setVisibility(View.GONE);

        llInstitutesContainer.addView(instituteCard);

        View divider = inflater.inflate(R.layout.divider, container, false);
        llInstitutesContainer.addView(divider);
    }

    private String getStoredLanguage() {
        SharedPreferences sharedPref = mContext.getSharedPreferences(LANGUAGE_PREFS, Context.MODE_PRIVATE);
        return sharedPref.getString(LANGUAGE_SELECTED, getDeviceLanguage());
    }

    private String getDeviceLanguage() {
        return Locale.getDefault().getLanguage();
    }

    private void displayAlert(String msg) {
        scrollView.setVisibility(View.GONE);
        tvAlert.setVisibility(View.VISIBLE);
        tvAlert.setText(msg);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.VISIBLE);
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
    }
}
