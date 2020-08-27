package om.gov.moh.phr.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import om.gov.moh.phr.apimodels.ApiDemographicsHolder;
import om.gov.moh.phr.apimodels.ApiEncountersHolder;
import om.gov.moh.phr.apimodels.ApiOrganDonationHolder;
import om.gov.moh.phr.apimodels.ApiRelationMaster;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.MyProgressDialog;
import om.gov.moh.phr.models.UserEmailFetcher;

import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
//import static om.gov.moh.phr.models.MyConstants.API_MSHIFA_URL;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_PHR;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_ARABIC;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_PREFS;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_SELECTED;


public class OrganDonationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String API_URL_GET_RELATION_MAST = API_NEHR_URL + "master/relation";
    private static final String API_URL_GET_ORGAN = API_NEHR_URL + "donation/findByCivilId/";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Context mContext;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private MediatorInterface mMediatorCallback;
    private EditText mobileNo, email, familyMember;
    private CheckBox allOrgans, kidneys, liver, heart, lungs, pancreas, corneas;
    private Button saveBtn, cancelBtn;
    private Spinner relation;
    private RequestQueue mQueue;
    private MyProgressDialog mProgressDialog;
    private ArrayList<ApiRelationMaster.RelationMast> relationMastArrayList;
    private ArrayAdapter<String> spinnerArrayAdapter;
    public OrganDonationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;

        mMediatorCallback = (MediatorInterface) context;
        mToolbarControllerCallback = (ToolbarControllerInterface) context;
    }
    // TODO: Rename and change types and number of parameters
    public static OrganDonationFragment newInstance(String param1, String param2) {
        OrganDonationFragment fragment = new OrganDonationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static OrganDonationFragment newInstance() {

        Bundle args = new Bundle();

        OrganDonationFragment fragment = new OrganDonationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView =  inflater.inflate(R.layout.fragment_organ_donation, container, false);
        ImageButton ibToolbarBackButton = parentView.findViewById(R.id.ib_toolbar_back_button);
        ibToolbarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mToolbarControllerCallback.customToolbarBackButtonClicked();
            }
        });
        TextView tvToolBarTitle = parentView.findViewById(R.id.tv_toolbar_title);
        String title = getString(R.string.title_organ);
        tvToolBarTitle.setText(title);
        mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
        mobileNo = parentView.findViewById(R.id.et_phone_organ);
        email =  parentView.findViewById(R.id.et_email_organ);
        familyMember = parentView.findViewById(R.id.et_nameFamily_organ);
        mProgressDialog = new MyProgressDialog(mContext);// initializes progress dialog
        allOrgans =  parentView.findViewById(R.id.checkBox_allOrgans);
        kidneys =  parentView.findViewById(R.id.checkBox_kidneys);
        liver =  parentView.findViewById(R.id.checkBox_liver);
        heart =  parentView.findViewById(R.id.checkBox_heart);
        lungs =  parentView.findViewById(R.id.checkBox_lungs);
        pancreas =  parentView.findViewById(R.id.checkBox_pancreas);
        corneas =  parentView.findViewById(R.id.checkBox_corneas);
        saveBtn = parentView.findViewById(R.id.btn_save_organ);
        cancelBtn = parentView.findViewById(R.id.btn_cancel_organ);
        relation = parentView.findViewById(R.id.et_releation_organ);

        mobileNo.setText(UserEmailFetcher.getEmail(mContext));

        allOrgans.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    kidneys.setChecked(true);
                    liver.setChecked(true);
                    heart.setChecked(true);
                    lungs.setChecked(true);
                    pancreas.setChecked(true);
                    corneas.setChecked(true);
                }else {
                    kidneys.setChecked(false);
                    liver.setChecked(false);
                    heart.setChecked(false);
                    lungs.setChecked(false);
                    pancreas.setChecked(false);
                    corneas.setChecked(false);
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mToolbarControllerCallback.customToolbarBackButtonClicked();
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveOrgan();
            }
        });
        getRelationMast();
        getOrganDonationData();

        return parentView;
    }

    public void saveOrgan(){
        mProgressDialog.showDialog();
        // showing refresh animation before making http call

        Log.d("enc", "Called");
        String fullUrl = API_NEHR_URL + "donation/save";


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, fullUrl, getJSONRequestParams()
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                    try {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {
                            Gson gson = new Gson();

                            ApiOrganDonationHolder responseHolder = gson.fromJson(response.toString(), ApiOrganDonationHolder.class);
                            Log.d("resp-encount", response.getJSONArray("result").toString());


                        } else {

                            mProgressDialog.dismissDialog();
                        }
                    } catch (JSONException e) {
//                    Log.d("enc", e.getMessage());

                        e.printStackTrace();
                    }

                    mProgressDialog.dismissDialog();
                    // showing refresh animation before making http call

                }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.d("enc", error.toString());
                Activity activity = getActivity();
                if (activity != null && isAdded()) {
                    mProgressDialog.dismissDialog();
                    // showing refresh animation before making http call

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
                //Log.d("enc-auth", API_GET_TOKEN_BEARER + mMediatorCallback.getAccessToken().getAccessTokenString());
                return headers;
            }

        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        mQueue.add(jsonObjectRequest);
    }
    private JSONObject getJSONRequestParams() {
        Map<String, Object> params = new HashMap<>();
       /* params.put("civilId", Long.parseLong(civilId));
        params.put("mobileNo", source);
        params.put("email", source);
        params.put("familyMemberName", source);
        params.put("kidneysYn", source);
        params.put("liverYn", source);
        params.put("heartYn", source);
        params.put("lungsYn", source);
        params.put("pancreasYn", source);
        params.put("corneasYn", source);
        params.put("afterDeathYn", source);
        params.put("relationCode", source);
        params.put("relationContactNo", source);*/
        return new JSONObject(params);
    }

    public void getRelationMast(){
        String fullUrl = API_URL_GET_RELATION_MAST;


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fullUrl, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt(API_RESPONSE_CODE) == 0) {
                        Gson gson = new Gson();
                        ApiRelationMaster responseHolder = gson.fromJson(response.toString(), ApiRelationMaster.class);
                        Log.d("resp-encount", response.getJSONArray("result").toString());
                        Log.d("relation master result", responseHolder.getResult().toString());
                        relationMastArrayList = responseHolder.getResult();
                        setupSelectHospitalSpinner(responseHolder.getResult());


                    } else {


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("resp-demographic", error.toString());
                error.printStackTrace();

            }
        }) {
            //
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
//                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", API_GET_TOKEN_BEARER + mMediatorCallback.getAccessToken().getAccessTokenString());
               // headers.put("Authorization", API_GET_TOKEN_BEARER + mMediatorCallback.getAccessToken().getAccessTokenString());
                return headers;
            }

        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);

        mQueue.add(jsonObjectRequest);
    }

    private void setupSelectHospitalSpinner(final ArrayList<ApiRelationMaster.RelationMast> relationMaster) {
        Log.d("appointmentFrag", "-setupSelectHospitalSpinner");

        ArrayList<String> relationName = new ArrayList<>();
        relationName.add(0, getString(R.string.title_select_relation));
        for (int i = 0; i< relationMaster.size(); i++){
            if (getStoredLanguage().equals(LANGUAGE_ARABIC)&& !relationMaster.get(i).getRelationNameNls().isEmpty())
                relationName.add(relationMaster.get(i).getRelationNameNls());
            else
                relationName.add(relationMaster.get(i).getRelationName());

        }



        // Initializing an ArrayAdapter
         spinnerArrayAdapter = new ArrayAdapter<String>(
                mContext, R.layout.spinner_item, relationName) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        //spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        relation.setAdapter(spinnerArrayAdapter);

        relation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if (position > 0) {
                    // Notify the selected item text : selectedItemText

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void getOrganDonationData(){
        mProgressDialog.showDialog();
        String fullUrl = API_URL_GET_ORGAN + mMediatorCallback.getCurrentUser().getCivilId();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fullUrl, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt(API_RESPONSE_CODE) == 0) {
                        Gson gson = new Gson();
                        ApiOrganDonationHolder responseHolder = gson.fromJson(response.toString(), ApiOrganDonationHolder.class);


                       setupForm(responseHolder.getResult());


                    }else {

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
                // headers.put("Authorization", API_GET_TOKEN_BEARER + mMediatorCallback.getAccessToken().getAccessTokenString());
                return headers;
            }

        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);

        mQueue.add(jsonObjectRequest);
    }

    private void setupForm(ApiOrganDonationHolder.OrganDonationJson result) {
        mobileNo.setText(result.getMobileNo());
        email.setText(result.getEmail());
        familyMember.setText(result.getFamilyMemberName());

        if (result.getKidneysYn().equals("Y"))
            kidneys.setChecked(true);

        if (result.getLiverYn().equals("Y"))
            liver.setChecked(true);

        if (result.getHeartYn().equals("Y"))
            heart.setChecked(true);

        if (result.getLungsYn().equals("Y"))
            lungs.setChecked(true);

        if (result.getPancreasYn().equals("Y"))
            pancreas.setChecked(true);

        if (result.getCorneasYn().equals("Y"))
            corneas.setChecked(true);


        saveBtn.setText(R.string.title_update);
        int spinnerPosition = spinnerArrayAdapter.getPosition(getRelationByCode(result.getRelationCode()));
        relation.setSelection(spinnerPosition);


    }

    public String getRelationByCode(int code){
        String result = "";
        for (ApiRelationMaster.RelationMast relationMast: relationMastArrayList){
            if (relationMast.getRelationCode() == code){
                result = relationMast.getRelationName();

            }
        }
        return result;
    }
    private String getStoredLanguage() {
        SharedPreferences sharedPref = mContext.getSharedPreferences(LANGUAGE_PREFS, Context.MODE_PRIVATE);
        return sharedPref.getString(LANGUAGE_SELECTED, LANGUAGE_ARABIC);
    }
}