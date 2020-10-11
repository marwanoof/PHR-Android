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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiOrganDonationHolder;
import om.gov.moh.phr.apimodels.ApiRelationMaster;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.MyProgressDialog;
import om.gov.moh.phr.models.UserEmailFetcher;

import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
//import static om.gov.moh.phr.models.MyConstants.API_MSHIFA_URL;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_MESSAGE;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_ARABIC;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_PREFS;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_SELECTED;


public class OrganDonationFragment extends Fragment {

    private static final String API_URL_GET_RELATION_MAST = API_NEHR_URL + "master/relation";
    private static final String API_URL_GET_ORGAN = API_NEHR_URL + "donation/findByCivilId";
    private Context mContext;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private MediatorInterface mMediatorCallback;
    private EditText mobileNo, email, familyMember, etMobileNoOfFamilyMember;
    private CheckBox allOrgans, kidneys, liver, heart, lungs, pancreas, corneas;
    private Button saveBtn, cancelBtn;
    private Spinner relation;
    private RequestQueue mQueue;
    private MyProgressDialog mProgressDialog;
    private ArrayList<String> relationMastArrayList;
    ArrayList<Integer> relationsCodesArrayList;
    private ArrayAdapter<String> spinnerArrayAdapter;
    private int mRelationCode;
    private Long mDonerID = null;
    private boolean isAllChecked = true;

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

    public static OrganDonationFragment newInstance() {

        Bundle args = new Bundle();

        OrganDonationFragment fragment = new OrganDonationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_organ_donation, container, false);
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
        relationMastArrayList = new ArrayList<>();
        relationMastArrayList.add(getResources().getString(R.string.title_select_relation));
        relationsCodesArrayList = new ArrayList<>();
        relationsCodesArrayList.add(0);
        mobileNo = parentView.findViewById(R.id.et_phone_organ);
        etMobileNoOfFamilyMember = parentView.findViewById(R.id.et_mobileNo_family);
        email = parentView.findViewById(R.id.et_email_organ);
        familyMember = parentView.findViewById(R.id.et_nameFamily_organ);
        mProgressDialog = new MyProgressDialog(mContext);// initializes progress dialog
        allOrgans = parentView.findViewById(R.id.checkBox_allOrgans);
        kidneys = parentView.findViewById(R.id.checkBox_kidneys);
        liver = parentView.findViewById(R.id.checkBox_liver);
        heart = parentView.findViewById(R.id.checkBox_heart);
        lungs = parentView.findViewById(R.id.checkBox_lungs);
        pancreas = parentView.findViewById(R.id.checkBox_pancreas);
        corneas = parentView.findViewById(R.id.checkBox_corneas);
        saveBtn = parentView.findViewById(R.id.btn_save_organ);
        cancelBtn = parentView.findViewById(R.id.btn_cancel_organ);
        relation = parentView.findViewById(R.id.et_releation_organ);

        email.setText(UserEmailFetcher.getEmail(mContext));

        allOrgans.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isAllChecked=false)
                    allOrgans.setChecked(false);
                  else if (isChecked) {
                        kidneys.setChecked(true);
                        liver.setChecked(true);
                        heart.setChecked(true);
                        lungs.setChecked(true);
                        pancreas.setChecked(true);
                        corneas.setChecked(true);
                    } else {
                        kidneys.setChecked(false);
                        liver.setChecked(false);
                        heart.setChecked(false);
                        lungs.setChecked(false);
                        pancreas.setChecked(false);
                        corneas.setChecked(false);
                    }
                }
        });
        kidneys.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                   isAllChecked = false;
                    allOrgans.setChecked(false);
                }
            }
        });
        liver.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    isAllChecked = false;
                    allOrgans.setChecked(false);
                }
            }
        });
        heart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    isAllChecked = false;
                    allOrgans.setChecked(false);
                }
            }
        });
        lungs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    isAllChecked = false;
                    allOrgans.setChecked(false);
                }
            }
        });
        pancreas.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    isAllChecked = false;
                    allOrgans.setChecked(false);
                }
            }
        });
        corneas.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    isAllChecked = false;
                    allOrgans.setChecked(false);
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mToolbarControllerCallback.customToolbarBackButtonClicked();
                mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.VISIBLE);
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getRelationCode();
                if (!email.getText().toString().isEmpty() && !isEmailValid(email.getText().toString()))
                    email.setError(getResources().getString(R.string.invalid_email));
                else if (familyMember.getText().toString().isEmpty())
                    familyMember.setError(getResources().getString(R.string.alert_empty_field));
                else if (mRelationCode == 0)
                    Toast.makeText(mContext, getResources().getString(R.string.select_relation_msg), Toast.LENGTH_SHORT).show();
                else
                    saveOrgan();
            }
        });
        getRelationMast();

        return parentView;
    }

    public void saveOrgan() {
        mProgressDialog.showDialog();
        // showing refresh animation before making http call

        Log.d("enc", "Called");
        String fullUrl = API_NEHR_URL + "donation/save";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, fullUrl, getJSONRequestParams()
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("organDonation", response.toString());
                try {
                    if (response.getInt(API_RESPONSE_CODE) == 0) {
                        Toast.makeText(mContext, "Saved Successfully.", Toast.LENGTH_SHORT).show();
                        mToolbarControllerCallback.customToolbarBackButtonClicked();
                    } else
                        Toast.makeText(mContext, response.getString(API_RESPONSE_MESSAGE), Toast.LENGTH_SHORT).show();

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
        params.put("civilId", mMediatorCallback.getCurrentUser().getCivilId());
        params.put("mobileNo", mobileNo.getText().toString());
        params.put("email", email.getText().toString());
        params.put("familyMemberName", familyMember.getText().toString());
        if (kidneys.isChecked())
            params.put("kidneysYn", "Y");
        else
            params.put("kidneysYn", "N");
        if (liver.isChecked())
            params.put("liverYn", "Y");
        else
            params.put("liverYn", "N");
        if (heart.isChecked())
            params.put("heartYn", "Y");
        else
            params.put("heartYn", "N");
        if (lungs.isChecked())
            params.put("lungsYn", "Y");
        else
            params.put("lungsYn", "N");
        if (pancreas.isChecked())
            params.put("pancreasYn", "Y");
        else
            params.put("pancreasYn", "N");
        if (corneas.isChecked())
            params.put("corneasYn", "Y");
        else
            params.put("corneasYn", "N");
        params.put("relationCode", mRelationCode);
        if (!etMobileNoOfFamilyMember.getText().toString().trim().isEmpty())
            params.put("relationContactNo", Long.parseLong(etMobileNoOfFamilyMember.getText().toString()));
        Log.d("organDonation", params.toString());
        if (mDonerID != null)
            params.put("donorId", mDonerID);
        return new JSONObject(params);
    }

    public void getRelationMast() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, API_URL_GET_RELATION_MAST, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt(API_RESPONSE_CODE) == 0) {
                        Gson gson = new Gson();
                        ApiRelationMaster responseHolder = gson.fromJson(response.toString(), ApiRelationMaster.class);
                        Log.d("organDonation", response.toString());
                        for (int i = 0; i < responseHolder.getResult().size(); i++) {
                            if (getStoredLanguage().equals(LANGUAGE_ARABIC) && responseHolder.getResult().get(i).getRelationNameNls() != null)
                                relationMastArrayList.add(responseHolder.getResult().get(i).getRelationNameNls());
                            else
                                relationMastArrayList.add(responseHolder.getResult().get(i).getRelationName());
                            relationsCodesArrayList.add(responseHolder.getResult().get(i).getRelationCode());
                        }

                        setupSelectRelationSpinner(relationMastArrayList);
                        getOrganDonationData();

                    } else
                        Toast.makeText(mContext, response.getString(API_RESPONSE_MESSAGE), Toast.LENGTH_SHORT).show();
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

    private void setupSelectRelationSpinner(final ArrayList<String> relationMaster) {
        // Initializing an ArrayAdapter
        spinnerArrayAdapter = new ArrayAdapter<String>(
                mContext, R.layout.spinner_item, relationMaster) {
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
        relation.setAdapter(spinnerArrayAdapter);
    }

    public void getOrganDonationData() {
        mProgressDialog.showDialog();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, API_URL_GET_ORGAN, getJSONRequestCivilIDParam()
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt(API_RESPONSE_CODE) == 0) {
                        Gson gson = new Gson();
                        ApiOrganDonationHolder responseHolder = gson.fromJson(response.toString(), ApiOrganDonationHolder.class);
                        Log.d("organDonation", response.toString());
                        setupForm(responseHolder.getResult());
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

    private JSONObject getJSONRequestCivilIDParam() {
        Map<String, Object> params = new HashMap<>();
        params.put("civilId", mMediatorCallback.getCurrentUser().getCivilId());
        return new JSONObject(params);
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

        mDonerID = result.getDonorId();

        etMobileNoOfFamilyMember.setText(String.valueOf(result.getRelationContactNo()));


        saveBtn.setText(R.string.title_update);
        int spinnerPosition = spinnerArrayAdapter.getPosition(getRelationByCode(result.getRelationCode()));
        relation.setSelection(spinnerPosition);
    }

    public String getRelationByCode(int code) {
        String result = "";
        for (int i = 0; i < relationsCodesArrayList.size(); i++) {
            if (code == relationsCodesArrayList.get(i))
                result = relationMastArrayList.get(i);
        }
        return result;
    }

    private String getStoredLanguage() {
        SharedPreferences sharedPref = mContext.getSharedPreferences(LANGUAGE_PREFS, Context.MODE_PRIVATE);
        return sharedPref.getString(LANGUAGE_SELECTED, LANGUAGE_ARABIC);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.VISIBLE);
    }

    private void getRelationCode() {
        for (int i = 0; i < relationsCodesArrayList.size(); i++) {
            if (relation.getSelectedItemPosition() == i) {
                relation.setSelection(i);
                mRelationCode = relationsCodesArrayList.get(i);

            }
        }
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}