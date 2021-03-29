package om.gov.moh.phr.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.text.InputFilter;
import android.text.Spanned;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiOrganDonationHolder;
import om.gov.moh.phr.apimodels.ApiRelationMaster;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.GlobalMethodsKotlin;
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
    private static final String PARAM1 = "PARAM1";
    private Context mContext;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private MediatorInterface mMediatorCallback;
    private EditText mobileNo, email, familyMember, etMobileNoOfFamilyMember;
    private CheckBox allOrgans, kidneys, liver, heart, lungs, pancreas, corneas;
    private Button saveBtn;
    private Spinner relation;
    private RequestQueue mQueue;
    private MyProgressDialog mProgressDialog;
    private ArrayList<String> relationMastArrayList;
    ArrayList<Integer> relationsCodesArrayList;
    private ArrayAdapter<String> spinnerArrayAdapter;
    private int mRelationCode;
    private Long mDonerID = null;
    private RadioButton radioButtonYes, radioButtonNo, radioButtonD;
    private String afterDeath = "D";
    private String pageTitle;
    private CardView cvPersonalInfo, cvSelectionOrganDonated;


    public OrganDonationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mMediatorCallback = (MediatorInterface) context;
        mToolbarControllerCallback = (ToolbarControllerInterface) context;
        mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.GONE);
    }

    public static OrganDonationFragment newInstance(String title) {

        Bundle args = new Bundle();
        args.putSerializable(PARAM1, title);
        OrganDonationFragment fragment = new OrganDonationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pageTitle = (String) getArguments().getSerializable(PARAM1);
        }
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
        String title = pageTitle;
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
        relation = parentView.findViewById(R.id.et_releation_organ);
        cvPersonalInfo = parentView.findViewById(R.id.cardView5);
        cvSelectionOrganDonated = parentView.findViewById(R.id.cardView6);

        email.setText(UserEmailFetcher.getEmail(mContext));

        final RadioGroup radioGroup = parentView.findViewById(R.id.radioGroup);
        radioButtonYes = parentView.findViewById(R.id.radioButtonYes);
        radioButtonNo = parentView.findViewById(R.id.radioButtonNo);
        radioButtonD = parentView.findViewById(R.id.radioButtonD);

        allOrgans.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    kidneys.setChecked(true);
                    liver.setChecked(true);
                    heart.setChecked(true);
                    lungs.setChecked(true);
                    pancreas.setChecked(true);
                    corneas.setChecked(true);
                } else if (checkIfAllOrgansChecked()) {
                    allOrgans.setChecked(true);
                } else if (!checkIfAllOrgansChecked())
                    allOrgans.setChecked(false);
            }
        });
        kidneys.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                allOrgans.setChecked(checkIfAllOrgansChecked());
                if (!isChecked)
                    kidneys.setChecked(false);
            }
        });
        liver.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                allOrgans.setChecked(checkIfAllOrgansChecked());
                if (!isChecked)
                    liver.setChecked(false);
            }
        });
        heart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                allOrgans.setChecked(checkIfAllOrgansChecked());
                if (!isChecked)
                    heart.setChecked(false);
            }
        });
        lungs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                allOrgans.setChecked(checkIfAllOrgansChecked());
                if (!isChecked)
                    lungs.setChecked(false);
            }
        });
        pancreas.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                allOrgans.setChecked(checkIfAllOrgansChecked());
                if (!isChecked)
                    pancreas.setChecked(false);
            }
        });
        corneas.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                allOrgans.setChecked(checkIfAllOrgansChecked());
                if (!isChecked)
                    corneas.setChecked(false);
            }
        });


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                switch (checkedId) {
                    case R.id.radioButtonYes:
                        afterDeath = "Y";
                        cvPersonalInfo.setVisibility(View.VISIBLE);
                        cvSelectionOrganDonated.setVisibility(View.VISIBLE);
                        break;
                    case R.id.radioButtonNo:
                        afterDeath = "N";
                        resetAllInfo();
                        cvPersonalInfo.setVisibility(View.GONE);
                        cvSelectionOrganDonated.setVisibility(View.GONE);
                        break;
                    case R.id.radioButtonD:
                        afterDeath = "D";
                        resetAllInfo();
                        cvPersonalInfo.setVisibility(View.GONE);
                        cvSelectionOrganDonated.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }
                System.out.println("isAgreeToDonate" + afterDeath);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getRelationCode();
                if (radioButtonYes.isChecked()) {
                    if (mobileNo.getText().toString().isEmpty())
                        mobileNo.setError(getResources().getString(R.string.alert_empty_field));
                    else if(mobileNo.getText().toString().trim().length()<8)
                             mobileNo.setError(getResources().getString(R.string.invalid_phoneNo));
                    else if (email.getText().toString().isEmpty())
                        email.setError(getResources().getString(R.string.alert_empty_field));
                    else if (!isEmailValid(email.getText().toString()))
                        email.setError(getResources().getString(R.string.invalid_email));
                    else if (familyMember.getText().toString().isEmpty())
                        familyMember.setError(getResources().getString(R.string.alert_empty_field));
                    else if (etMobileNoOfFamilyMember.getText().toString().isEmpty())
                        etMobileNoOfFamilyMember.setError(getResources().getString(R.string.alert_empty_field));
                    else if (etMobileNoOfFamilyMember.getText().toString().trim().length() < 8)
                        etMobileNoOfFamilyMember.setError(getResources().getString(R.string.invalid_phoneNo));
                    else if (mRelationCode == 0) {
                        Snackbar.make(relation, getResources().getString(R.string.select_relation_msg), Snackbar.LENGTH_SHORT)
                                .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                                .show();
                    } else if(!kidneys.isChecked()&&!liver.isChecked()&&!heart.isChecked()&&!lungs.isChecked()&&!pancreas.isChecked()&&!corneas.isChecked()){
                        Snackbar.make(radioGroup, getResources().getString(R.string.select_organ_msg), Snackbar.LENGTH_SHORT)
                                .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                                .show();
                    }else
                        saveOrgan();
                } else
                    saveOrgan();
            }
        });
        if (mMediatorCallback.isConnected()) {
            getRelationMast();
        } else {
            GlobalMethodsKotlin.Companion.showAlertDialog(mContext, getResources().getString(R.string.no_internet_title), getResources().getString(R.string.alert_no_connection), getResources().getString(R.string.ok), R.drawable.ic_error);
        }

        return parentView;
    }


    public void saveOrgan() {

        mProgressDialog.showDialog();

        String fullUrl = API_NEHR_URL + "donation/save";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, fullUrl, getJSONRequestParams()
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("saveDonation", response.toString());
                if (mContext != null && isAdded()) {
                    try {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {
                            showSuccessfulAlert();
                            mToolbarControllerCallback.customToolbarBackButtonClicked();
                        } else
                            System.out.println(response.getString(API_RESPONSE_MESSAGE));
                            GlobalMethodsKotlin.Companion.showAlertErrorDialog(mContext);

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
                    mProgressDialog.dismissDialog();
                    error.printStackTrace();
                }
            }
        }) {
            //
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
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

    private void showSuccessfulAlert() {
        String title = "";
        String body = mContext.getResources().getString(R.string.saved_successfully);
        GlobalMethodsKotlin.Companion.showAlertDialog(mContext, title, body, mContext.getResources().getString(R.string.ok), R.drawable.ic_successful);
    }

    private JSONObject getJSONRequestParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("civilId", mMediatorCallback.getCurrentUser().getCivilId());
        if (radioButtonYes.isChecked()) {
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
        } else {
            params.put("mobileNo", 0);
            params.put("email", "");
            params.put("familyMemberName", "");
            params.put("kidneysYn", "N");
            params.put("liverYn", "N");
            params.put("heartYn", "N");
            params.put("lungsYn", "N");
            params.put("pancreasYn", "N");
            params.put("corneasYn", "N");
            params.put("relationCode", 0);
            params.put("relationContactNo", null);
        }
        if (mDonerID != null)
            params.put("donorId", mDonerID);
        params.put("afterDeathYn", afterDeath);
        return new JSONObject(params);
    }

    public void getRelationMast() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, API_URL_GET_RELATION_MAST, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mContext != null && isAdded()) {
                    try {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {
                            Gson gson = new Gson();
                            ApiRelationMaster responseHolder = gson.fromJson(response.toString(), ApiRelationMaster.class);
                            for (int i = 0; i < responseHolder.getResult().size(); i++) {
                                if (getStoredLanguage().equals(LANGUAGE_ARABIC) && responseHolder.getResult().get(i).getRelationNameNls() != null && !responseHolder.getResult().get(i).getRelationNameNls().equals(""))
                                    relationMastArrayList.add(responseHolder.getResult().get(i).getRelationNameNls());
                                else
                                    relationMastArrayList.add(responseHolder.getResult().get(i).getRelationName());
                                relationsCodesArrayList.add(responseHolder.getResult().get(i).getRelationCode());
                            }

                            setupSelectRelationSpinner(relationMastArrayList);

                            if (mMediatorCallback.isConnected()) {
                                getOrganDonationData();
                            } else {
                                GlobalMethodsKotlin.Companion.showAlertDialog(mContext, getResources().getString(R.string.no_internet_title), getResources().getString(R.string.alert_no_connection), getResources().getString(R.string.ok), R.drawable.ic_error);
                            }
                        } else
                            GlobalMethodsKotlin.Companion.showAlertErrorDialog(mContext);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mContext != null && isAdded()) {
                    GlobalMethodsKotlin.Companion.showAlertErrorDialog(mContext);
                    error.printStackTrace();
                }
            }
        }) {
            //
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
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
                if (mContext != null && isAdded()) {
                    try {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {

                            Gson gson = new Gson();
                            ApiOrganDonationHolder responseHolder = gson.fromJson(response.toString(), ApiOrganDonationHolder.class);
                            setupForm(responseHolder.getResult());
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
                    GlobalMethodsKotlin.Companion.showAlertErrorDialog(mContext);
                    Toast.makeText(mContext, "sth went wrong", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                    mProgressDialog.dismissDialog();
                }
            }
        }) {
            //
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
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

    private JSONObject getJSONRequestCivilIDParam() {
        Map<String, Object> params = new HashMap<>();
        params.put("civilId", mMediatorCallback.getCurrentUser().getCivilId());
        return new JSONObject(params);
    }

    private void setupForm(ApiOrganDonationHolder.OrganDonationJson result) {
        if (result.getMobileNo() != null && !result.getMobileNo().equals(""))
            mobileNo.setText(result.getMobileNo());
        if (result.getEmail() != null && !result.getEmail().equals(""))
            email.setText(result.getEmail());
        if (result.getFamilyMemberName() != null && !result.getFamilyMemberName().equals(""))
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
        if (result.getDonorId() != null)
            mDonerID = result.getDonorId();
        if (result.getAfterDeathYn() != null) {
            switch (result.getAfterDeathYn()) {
                case "Y":
                    radioButtonYes.setChecked(true);
                    int spinnerPosition = spinnerArrayAdapter.getPosition(getRelationByCode(result.getRelationCode()));
                    relation.setSelection(spinnerPosition);
                    break;
                case "N":
                    radioButtonNo.setChecked(true);
                    resetAllInfo();
                    break;
                case "D":
                    radioButtonD.setChecked(true);
                    resetAllInfo();
                    break;
                default:
                    radioButtonYes.setChecked(false);
                    radioButtonNo.setChecked(false);
                    radioButtonD.setChecked(true);
                    resetAllInfo();
                    break;
            }
            if (result.getRelationContactNo() != 0)
                etMobileNoOfFamilyMember.setText(String.valueOf(result.getRelationContactNo()));
            saveBtn.setText(R.string.title_update);
        }
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
        return sharedPref.getString(LANGUAGE_SELECTED, getDeviceLanguage());
    }

    private String getDeviceLanguage() {
        return Locale.getDefault().getLanguage();
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

    private void resetAllInfo() {
        mobileNo.setText("");
        email.setText("");
        familyMember.setText("");
        kidneys.setChecked(false);
        liver.setChecked(false);
        heart.setChecked(false);
        lungs.setChecked(false);
        pancreas.setChecked(false);
        corneas.setChecked(false);
        radioButtonYes.setChecked(false);
        etMobileNoOfFamilyMember.setText("");
        relation.setSelection(0);
    }

    private boolean checkIfAllOrgansChecked() {
        return (kidneys.isChecked() && liver.isChecked() && heart.isChecked() && lungs.isChecked() && pancreas.isChecked() && corneas.isChecked());
    }
}