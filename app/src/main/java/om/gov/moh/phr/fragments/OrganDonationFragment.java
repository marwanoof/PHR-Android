package om.gov.moh.phr.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.HashMap;
import java.util.Map;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiDemographicsHolder;
import om.gov.moh.phr.apimodels.ApiRelationMaster;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.UserEmailFetcher;

import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_MSHIFA_URL;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;


public class OrganDonationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String API_URL_GET_RELATION_MAST = API_MSHIFA_URL + "nehrapi/master/relation";
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
        return parentView;
    }

    public void saveOrgan(){

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

                        Log.d("relation master result", responseHolder.getResult().toString());
                        //setupSelectHospitalSpinner(responseHolder.getmResult());


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
                headers.put("Authorization", "886db47b-b2b4-4eeb-92f2-4a18d6efd410");
               // headers.put("Authorization", API_GET_TOKEN_BEARER + mMediatorCallback.getAccessToken().getAccessTokenString());
                return headers;
            }

        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);

        mQueue.add(jsonObjectRequest);
    }
}