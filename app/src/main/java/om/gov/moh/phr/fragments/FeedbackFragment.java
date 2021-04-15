package om.gov.moh.phr.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiFeedbackHolder;
import om.gov.moh.phr.interfaces.AdapterToFragmentConnectorInterface;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.GlobalMethodsKotlin;
import om.gov.moh.phr.models.MyProgressDialog;

import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_MESSAGE;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_ARABIC;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_PREFS;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_SELECTED;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedbackFragment extends Fragment implements AdapterToFragmentConnectorInterface {
    private Context mContext;
    private MediatorInterface mMediatorCallback;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private TextView tvEmail, tvFirstPhone, tvSecondPhone, tvThirdPhone;
    private RequestQueue mQueue;
    private MyProgressDialog mProgressDialog;
    private EditText etUserEmail, /*etUserMobileNo,*/ editText, etOtherComments;
    private ApiFeedbackHolder responseHolder;
    private ArrayList<RadioGroup> radioGroupArray;
    private LinearLayout linearLayout;
    private RadioButton[] rb;
   private InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                if (!Character.isLetterOrDigit(source.charAt(i))) {
                    return "";
                }
            }
            return null;
        }
    };
    public FeedbackFragment() {
        // Required empty public constructor
    }

    public static FeedbackFragment newInstance() {
        FeedbackFragment fragment = new FeedbackFragment();
        Bundle args = new Bundle();
        //  args.putString(ARG_PARAM1, param1);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_feedback, container, false);
        TextView tvToolbarTitle = view.findViewById(R.id.tv_toolbar_title);
        tvToolbarTitle.setText(getResources().getString(R.string.phr_feedback));
        ImageButton ibToolbarBackButton = view.findViewById(R.id.ib_toolbar_back_button);
        tvToolbarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.VISIBLE);
                mToolbarControllerCallback.customToolbarBackButtonClicked();
            }
        });
        ibToolbarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.VISIBLE);
                mToolbarControllerCallback.customToolbarBackButtonClicked();
            }
        });
        linearLayout = view.findViewById(R.id.main_layout);
        mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
        mProgressDialog = new MyProgressDialog(mContext);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvFirstPhone = view.findViewById(R.id.tvFirstPhone);
        tvSecondPhone = view.findViewById(R.id.tvSecondPhone);
        tvThirdPhone = view.findViewById(R.id.tvThirdPhone);
        tvFirstPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TelephonyChecker(mContext)) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:22357426"));
                    startActivity(intent);
                }
            }
        });
        tvThirdPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TelephonyChecker(mContext)) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:98994998"));
                    startActivity(intent);
                }
            }
        });
        tvSecondPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TelephonyChecker(mContext)) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:93244095"));
                    startActivity(intent);
                }
            }
        });
        tvEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "app.support@moh.gov.om" });
                intent.putExtra(Intent.EXTRA_SUBJECT, "PHR Feedback");
                startActivity(Intent.createChooser(intent, ""));
            }
        });
        Button btnSubmit = view.findViewById(R.id.btn_submit);
        Button btnCancel = view.findViewById(R.id.btn_cancel);
        if (mMediatorCallback.isConnected())
            getQuestions();
        else
            displayAlert();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (responseHolder != null)
                    checkDataFilled(view);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          mToolbarControllerCallback.customToolbarBackButtonClicked();
            }
        });



        return view;
    }

    private void getQuestions() {
        String QuestionsUrl = API_NEHR_URL + "feedback/questions";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, QuestionsUrl, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mContext != null&&isAdded()) {
                    try {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {
                            Gson gson = new Gson();
                            responseHolder = gson.fromJson(response.toString(), ApiFeedbackHolder.class);
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            radioGroupArray = new ArrayList<>();
                            for (int i = 0; i < responseHolder.getResult().size(); i++) {
                                TextView textView = new TextView(mContext);


                                if (responseHolder.getResult().get(i).getDataType().equals("select")) {
                                    if (getStoredLanguage().equals(LANGUAGE_ARABIC))
                                        textView.setText(responseHolder.getResult().get(i).getParamValueNls());
                                    else
                                        textView.setText(responseHolder.getResult().get(i).getParamValue());
                                    linearLayout.addView(textView);
                                    // add radio buttons
                                    rb = new RadioButton[responseHolder.getResult().get(i).getOptionMast().size()];
                                    RadioGroup radioGroup = new RadioGroup(mContext);//create the RadioGroup
                                    radioGroup.setId(responseHolder.getResult().get(i).getParamId());
                                    for (int y = 0; y < responseHolder.getResult().get(i).getOptionMast().size(); y++) {
                                        rb[y] = new RadioButton(mContext);
                                        if (getStoredLanguage().equals(LANGUAGE_ARABIC))
                                            rb[y].setText(" " + responseHolder.getResult().get(i).getOptionMast().get(y).getOptionNameNls());
                                        else
                                            rb[y].setText(" " + responseHolder.getResult().get(i).getOptionMast().get(y).getOptionName());
                                        rb[y].setId(responseHolder.getResult().get(i).getOptionMast().get(y).getOptionId());
                                        radioGroup.addView(rb[y]);
                                    }
                                    radioGroupArray.add(radioGroup);
                                    linearLayout.addView(radioGroup);
                                } else if (responseHolder.getResult().get(i).getDataType().equals("multiselect")) {
                                    if (getStoredLanguage().equals(LANGUAGE_ARABIC))
                                        textView.setText(responseHolder.getResult().get(i).getParamValueNls());
                                    else
                                        textView.setText(responseHolder.getResult().get(i).getParamValue());
                                    //add check boxes
                                    CheckBox[] checkBox = new CheckBox[responseHolder.getResult().get(i).getOptionMast().size()];
                                    LinearLayout checkBoxLayout = new LinearLayout(mContext);
                                    checkBoxLayout.setOrientation(LinearLayout.VERTICAL);
                                    checkBoxLayout.setId(responseHolder.getResult().get(i).getParamId()+100);
                                    checkBoxLayout.setTag(responseHolder.getResult().get(i).getParamId());
                                    for (int x = 0; x < responseHolder.getResult().get(i).getOptionMast().size(); x++) {
                                        checkBox[x] = new CheckBox(mContext);

                                        if (getStoredLanguage().equals(LANGUAGE_ARABIC))
                                            checkBox[x].setText(" " + responseHolder.getResult().get(i).getOptionMast().get(x).getOptionNameNls());
                                        else
                                            checkBox[x].setText(" " + responseHolder.getResult().get(i).getOptionMast().get(x).getOptionName());
                                        checkBox[x].setId(responseHolder.getResult().get(i).getOptionMast().get(x).getOptionId());
                                        checkBoxLayout.addView(checkBox[x]);

                                    }
                                    // checkBoxesArray.add(checkBoxLayout);
                                    linearLayout.addView(textView);
                                    linearLayout.addView(checkBoxLayout);
                                } else {
                                    if (getStoredLanguage().equals(LANGUAGE_ARABIC))
                                        textView.setText(responseHolder.getResult().get(i).getParamValueNls());
                                    else
                                        textView.setText(responseHolder.getResult().get(i).getParamValue());
                                    linearLayout.addView(textView);
                                    // add text area
                                    editText = new EditText(mContext);
                                    editText.setLayoutParams(layoutParams);
                                    editText.setTextSize(14);
                                    editText.setMaxLines(3);
                                    editText.setLines(3);

                                    //editText.setHint("write here your comments.");
                                    editText.setId(responseHolder.getResult().get(i).getParamId());
                                    //editText.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(100)});
                                    linearLayout.addView(editText);
                                }


                            }
                            etUserEmail = new EditText(mContext);
                            etUserEmail.setLayoutParams(layoutParams);
                            etUserEmail.setHint(getResources().getString(R.string.enter_your_email));
                            etUserEmail.setTextSize(14);
                            etUserEmail.setSingleLine(true);


                            linearLayout.addView(etUserEmail);
                       /*     etOtherComments = new EditText(mContext);
                            etOtherComments.setLayoutParams(layoutParams);
                            etOtherComments.setTextSize(14);
                            etOtherComments.setHint(getResources().getString(R.string.other_commets));
                            etOtherComments.setMaxLines(3);
                            etOtherComments.setLines(3);

                            etOtherComments.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(100)});
                           // etOtherComments.setFilters(new InputFilter[] { new InputFilter.LengthFilter(100) });
                            linearLayout.addView(etOtherComments);

                            etUserMobileNo = new EditText(mContext);
                            etUserMobileNo.setLayoutParams(layoutParams);
                            etUserMobileNo.setHint(getResources().getString(R.string.enter_your_mobile_no));
                            etUserMobileNo.setTextSize(14);
                            etUserMobileNo.setSingleLine(true);
                            etUserMobileNo.setFilters(new InputFilter[] { new InputFilter.LengthFilter(8) });
                            etUserMobileNo.setInputType(InputType.TYPE_CLASS_PHONE);
                            linearLayout.addView(etUserMobileNo);*/
                        } else {
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

    private void saveFeedback() {
        String saveFeedbackUrl = API_NEHR_URL + "feedback/save";
        mProgressDialog.showDialog();
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, saveFeedbackUrl, getJSONRequestParams()
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                if (mContext != null && isAdded()) {
                    try {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {
                            Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(android.R.id.content), getResources().getString(R.string.feedback_saved_msg), Snackbar.LENGTH_SHORT)
                                    .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                                    .show();
                            mProgressDialog.dismissDialog();
                            mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.VISIBLE);
                            mToolbarControllerCallback.customToolbarBackButtonClicked();
                        }else {
                            Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(android.R.id.content), response.getString(API_RESPONSE_MESSAGE), Snackbar.LENGTH_SHORT)
                                    .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                                    .show();
                            mProgressDialog.dismissDialog();
                        }
                    } catch (JSONException e) {
                        Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(android.R.id.content), getResources().getString(R.string.wrong_msg), Snackbar.LENGTH_SHORT)
                                .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                                .show();
                        mProgressDialog.dismissDialog();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mContext != null && isAdded()) {
                    error.printStackTrace();
                    Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(android.R.id.content), error.toString(), Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                            .show();
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

    private JSONObject getJSONRequestParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("civilId", Long.parseLong(mMediatorCallback.getCurrentUser().getCivilId()));
        params.put("appType", "phrApp");
        params.put("feedbackType", 1);
        if (getStoredLanguage().equals(LANGUAGE_ARABIC)){
            params.put("feedbackLang", "ar");
        }else{
            params.put("feedbackLang", "en");
        }

       // params.put("otherComments", etOtherComments.getText().toString());
        String userEmail = etUserEmail.getText().toString();
        if (!userEmail.equals("")){
              params.put("userEmail", userEmail);
        }

        SharedPreferences pref = mContext.getSharedPreferences("UserMobileNo", 0);
        String userMobileNo = pref.getString("mobileNo","");
        params.put("userMobile", userMobileNo);
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < responseHolder.getResult().size(); i++) {
            try {
                JSONObject jsonObject = new JSONObject();
                if (responseHolder.getResult().get(i).getDataType().equals("select")) {
                    jsonObject.put("paramId", responseHolder.getResult().get(i).getParamId());
                    for (int u = 0; u < radioGroupArray.size(); u++) {
                        Object object = radioGroupArray.get(u).findViewById(responseHolder.getResult().get(i).getParamId());
                       if (object instanceof RadioGroup) {
                            RadioGroup radioGroup = (RadioGroup) object;
                            final int childCount = radioGroup.getChildCount();
                            for (int obj = 0; obj < childCount; obj++) {
                                View v = radioGroup.getChildAt(obj);
                                if (v instanceof RadioButton) {
                                    RadioButton radioButton = (RadioButton) v;
                                    if(radioButton.isChecked()) {
                                        jsonObject.put("value", String.valueOf(radioButton.getId()));
                                    }
                                }
                            }
                        }
                    }
                } else if (responseHolder.getResult().get(i).getDataType().equals("multiselect")) {
                    ArrayList<String> getCheckBoxesId = new ArrayList<>();
                    Object object = linearLayout.findViewById(responseHolder.getResult().get(i).getParamId()+100);
                    if (object instanceof LinearLayout) {
                        LinearLayout linearLayout = (LinearLayout) object;
                        final int childCount = linearLayout.getChildCount();
                        for (int obj = 0; obj < childCount; obj++) {
                            View v = linearLayout.getChildAt(obj);
                            if (v instanceof CheckBox) {
                                CheckBox checkBox = (CheckBox) v;
                                if(checkBox.isChecked()) {
                                    getCheckBoxesId.add(String.valueOf(checkBox.getId()));
                                }
                            }
                        }
                    }
                    jsonObject.put("paramId", responseHolder.getResult().get(i).getParamId());
                    StringBuilder valueM = new StringBuilder();
                    for (String x : getCheckBoxesId){
                        valueM.append(x).append(",");
                    }
                    if (valueM.length() > 0){
                        valueM.deleteCharAt(valueM.length()-1);
                    }
                    jsonObject.put("value", valueM);
                } else {
                    jsonObject.put("paramId", responseHolder.getResult().get(i).getParamId());
                    jsonObject.put("value", editText.getText().toString());
                }

                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        params.put("values", jsonArray);
        System.out.println(new JSONObject(params));
        return new JSONObject(params);
    }

    private String getCommonSeperatedString(List<String> actionObjects) {
        StringBuilder sb = new StringBuilder();
        for (String actionObject : actionObjects) {
            sb.append(actionObject).append(",");
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        return sb.toString();
    }

    private void checkDataFilled(View view) {
        if (radioGroupArray == null /*&& radioGroupArray.getCheckedRadioButtonId() == -1*/) {
            Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(android.R.id.content), "Please try to answer all questions!", Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                    .show();
        }  /*else if (etUserEmail.getText().toString().trim().equals("")) {
            etUserEmail.setError(getResources().getString(R.string.alert_empty_field));
            etUserEmail.requestFocus();
            Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(android.R.id.content), getResources().getString(R.string.alert_empty_field), Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                    .show();
        } else if (!isEmailValid(etUserEmail.getText().toString())) {
            etUserEmail.setError(getResources().getString(R.string.invalid_email));
            etUserEmail.requestFocus();
        } else if (etUserMobileNo.getText().toString().trim().equals("")) {
            etUserMobileNo.setError(getResources().getString(R.string.alert_empty_field));
            etUserMobileNo.requestFocus();
        } else if (!isMobileNoValid(etUserMobileNo.getText().toString())||etUserMobileNo.getText().toString().trim().length()<8) {
            etUserMobileNo.setError(getResources().getString(R.string.invalid_phoneNo));
            etUserMobileNo.requestFocus();
        } else {*/
             saveFeedback();
        //}
    }


    private void displayAlert() {
        GlobalMethodsKotlin.Companion.showAlertDialog(mContext, getResources().getString(R.string.no_internet_title), getResources().getString(R.string.alert_no_connection), getResources().getString(R.string.ok), R.drawable.ic_error);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.VISIBLE);
    }

    @Override
    public <T> void onMyListItemClicked(T dataToPass, String dataTitle) {

    }

    @Override
    public <T> void onMyListItemClicked(T dataToPass, String dataTitle, int position) {

    }


    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    boolean isMobileNoValid(CharSequence phoneNo) {
        return Patterns.PHONE.matcher(phoneNo).matches();
    }

    private String getStoredLanguage() {
        SharedPreferences sharedPref = mContext.getSharedPreferences(LANGUAGE_PREFS, Context.MODE_PRIVATE);
        return sharedPref.getString(LANGUAGE_SELECTED, getDeviceLanguage());
    }
    private String getDeviceLanguage() {
        return Locale.getDefault().getLanguage();
    }
    private boolean TelephonyChecker (Context context) {
        TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        return manager.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE;
    }
}
