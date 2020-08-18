package om.gov.moh.phr.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiFeedbackHolder;
import om.gov.moh.phr.interfaces.AdapterToFragmentConnectorInterface;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.MyProgressDialog;

import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_RESULT;
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
    private TextView tvAlert;
    private RequestQueue mQueue;
    private MyProgressDialog mProgressDialog;
    private EditText etUserEmail, etUserMobileNo, editText;
    private CheckBox[] checkBox;
    private ApiFeedbackHolder responseHolder;
    private LinearLayout radioGroupLayout, checkBoxesLayout, editTextsLayout;
    private ArrayList<RadioGroup> radioGroupArray;
    private ArrayList<LinearLayout> checkBoxesArray;
    private RadioButton[] rb;

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
                mMediatorCallback.changeFragmentContainerVisibility(View.GONE, View.VISIBLE);
                mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.VISIBLE);
                mToolbarControllerCallback.customToolbarBackButtonClicked();
            }
        });
        ibToolbarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediatorCallback.changeFragmentContainerVisibility(View.GONE, View.VISIBLE);
                mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.VISIBLE);
                mToolbarControllerCallback.customToolbarBackButtonClicked();
            }
        });
        radioGroupLayout = view.findViewById(R.id.radioGroupLayout);
        checkBoxesLayout = view.findViewById(R.id.checkBoxesLayout);
        editTextsLayout = view.findViewById(R.id.editTextsLayout);
        //  checkBoxesArray = view.findViewById(R.id.checkBoxesArray);
        tvAlert = view.findViewById(R.id.tv_alert);
        mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
        mProgressDialog = new MyProgressDialog(mContext);
        Button btnSubmit = view.findViewById(R.id.btn_submit);
        Button btnCancel = view.findViewById(R.id.btn_cancel);
        etUserEmail = view.findViewById(R.id.et_userEmail);
        etUserMobileNo = view.findViewById(R.id.et_userMobileNo);
        if (mMediatorCallback.isConnected())
            getQuestions();
        else
            displayAlert(getResources().getString(R.string.alert_no_connection));

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (responseHolder != null)
                    checkDataFilled();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (responseHolder != null)
                    //   setupRecyclerView(responseHolder.getResult());
                    etUserEmail.setText("");
                etUserMobileNo.setText("");
                Toast.makeText(mContext, getResources().getString(R.string.cancel_done_msg), Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    private void getQuestions() {
        String QuestionsUrl = API_NEHR_URL + "feedback/questions";
        Log.d("QuestionsUrl", QuestionsUrl);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, QuestionsUrl, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mContext != null) {
                    try {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {
                            Gson gson = new Gson();
                            responseHolder = gson.fromJson(response.toString(), ApiFeedbackHolder.class);
                            Log.d("questions", response.getJSONArray(API_RESPONSE_RESULT).toString());
                            for (int i = 0; i < responseHolder.getResult().size(); i++) {
                                TextView textView = new TextView(mContext);
                                if (responseHolder.getResult().get(i).getDataType().equals("select")) {
                                    if (getStoredLanguage().equals(LANGUAGE_ARABIC))
                                        textView.setText(responseHolder.getResult().get(i).getParamValueNls());
                                    else
                                        textView.setText(responseHolder.getResult().get(i).getParamValue());
                                    radioGroupLayout.addView(textView);
                                    // add radio buttons
                                    rb = new RadioButton[responseHolder.getResult().get(i).getOptionMast().size()];
                                    radioGroupArray = new ArrayList<>();
                                    RadioGroup radioGroup = new RadioGroup(mContext);//create the RadioGroup
                                    radioGroup.setId(responseHolder.getResult().get(i).getParamId());
                                    //  radioGroupArray.setOrientation(RadioGroup.HORIZONTAL);//or RadioGroup.VERTICAL
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
                                    for (int u = 0; u < radioGroupArray.size(); u++)
                                        radioGroupLayout.addView(radioGroupArray.get(u));//you add the whole RadioGroup to the layout
                                } else if (responseHolder.getResult().get(i).getDataType().equals("multiselect")) {
                                    if (getStoredLanguage().equals(LANGUAGE_ARABIC))
                                        textView.setText(responseHolder.getResult().get(i).getParamValueNls());
                                    else
                                        textView.setText(responseHolder.getResult().get(i).getParamValue());
                                    checkBoxesLayout.addView(textView);
                                    //add check boxes
                                    checkBox = new CheckBox[responseHolder.getResult().get(i).getOptionMast().size()];
                                    checkBoxesArray = new ArrayList<>();
                                    LinearLayout checkBoxLayout = new LinearLayout(mContext);
                                    checkBoxLayout.setOrientation(LinearLayout.VERTICAL);
                                    checkBoxLayout.setId(responseHolder.getResult().get(i).getParamId());
                                    for (int x = 0; x < responseHolder.getResult().get(i).getOptionMast().size(); x++) {
                                        checkBox[x] = new CheckBox(mContext);

                                        if (getStoredLanguage().equals(LANGUAGE_ARABIC))
                                            checkBox[x].setText(" " + responseHolder.getResult().get(i).getOptionMast().get(x).getOptionNameNls());
                                        else
                                            checkBox[x].setText(" " + responseHolder.getResult().get(i).getOptionMast().get(x).getOptionName());
                                        checkBox[x].setId(responseHolder.getResult().get(i).getOptionMast().get(x).getOptionId());
                                        //  LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        //  itemBox = inflater.inflate(R.layout.check_box_container, linearLayout, false);

                                        checkBoxLayout.addView(checkBox[x]);
                                    }
                                    checkBoxesArray.add(checkBoxLayout);
                                    for (int u = 0; u < checkBoxesArray.size(); u++)
                                        checkBoxesLayout.addView(checkBoxesArray.get(u));
                                } else {
                                    if (getStoredLanguage().equals(LANGUAGE_ARABIC))
                                        textView.setText(responseHolder.getResult().get(i).getParamValueNls());
                                    else
                                        textView.setText(responseHolder.getResult().get(i).getParamValue());
                                    editTextsLayout.addView(textView);
                                    // add text area
                                    editText = new EditText(mContext);
                                    editTextsLayout.addView(editText);
                                }


                            }

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
                    Log.d("add_doc", error.toString());
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

    private void saveFeedback() {
        String saveFeedbackUrl = API_NEHR_URL + "feedback/save";
        mProgressDialog.showDialog();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, saveFeedbackUrl, getJSONRequestParams()
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mContext != null && isAdded()) {
                    Toast.makeText(mContext, getResources().getString(R.string.feedback_saved_msg), Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismissDialog();
                    mMediatorCallback.changeFragmentContainerVisibility(View.GONE, View.VISIBLE);
                    mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.VISIBLE);
                    mToolbarControllerCallback.customToolbarBackButtonClicked();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mContext != null && isAdded()) {
                    Log.d("upload_error", error.toString());
                    error.printStackTrace();
                    Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
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
        Map<String, Object> params = new HashMap<>();
        params.put("civilId", Long.parseLong(mMediatorCallback.getCurrentUser().getCivilId()));
        params.put("appType", "phrApp");
        params.put("feedbackType", 1);
        params.put("otherComments", "Test other comment");
        params.put("userEmail", etUserEmail.getText().toString());
        params.put("userMobile", etUserMobileNo.getText().toString());
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < responseHolder.getResult().size(); i++) {
            try {
                JSONObject jsonObject = new JSONObject();
                if (responseHolder.getResult().get(i).getDataType().equals("select")) {
                    jsonObject.put("paramId", responseHolder.getResult().get(i).getParamId());
                    for (int u = 0; u < radioGroupArray.size(); u++) {
                        if (radioGroupArray.get(u).getId() == responseHolder.getResult().get(i).getParamId()) {
                            for (RadioButton radioButton : rb) {
                                if (radioButton.getId() == responseHolder.getResult().get(i).getOptionMast().get(u).getOptionId() && radioButton.isChecked())
                                    jsonObject.put("value", radioButton.getId());
                            }

                        }
                    }

                } else if (responseHolder.getResult().get(i).getDataType().equals("multiselect")) {
                    jsonObject.put("paramId", responseHolder.getResult().get(i).getParamId());
                    for (int u = 0; u < checkBoxesArray.size(); u++) {
                        ArrayList<String> getCheckBoxesId = new ArrayList<>();
                        if(checkBoxesArray.get(u).getId()==responseHolder.getResult().get(i).getParamId()){
                                 for (int x = 0; x <checkBoxesArray.size(); x++) {
                                         for (CheckBox box : checkBox) {
                                             if (box.getId() == responseHolder.getResult().get(i).getOptionMast().get(u).getOptionId() &&box.isChecked())
                                                 getCheckBoxesId.add(String.valueOf(box.getId()));
                                         }
                                         jsonObject.put("value", getCommonSeperatedString(getCheckBoxesId));
                                     }
                        }

                    }
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
        Log.d("submitFeedback", new JSONObject(params).toString());
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

    private void checkDataFilled() {
        if (radioGroupArray == null /*&& radioGroupArray.getCheckedRadioButtonId() == -1*/) {
            Toast.makeText(mContext, "Please try to answer all questions!", Toast.LENGTH_SHORT).show();
        } else if (editText != null && editText.getText().toString().trim().equals("")) {
            editText.setError(getResources().getString(R.string.alert_empty_field));
            editText.requestFocus();
        } else if (etUserEmail.getText().toString().trim().equals("")) {
            etUserEmail.setError(getResources().getString(R.string.alert_empty_field));
            etUserEmail.requestFocus();
            Toast.makeText(mContext, getResources().getString(R.string.alert_empty_field), Toast.LENGTH_SHORT).show();
        } else if (!isEmailValid(etUserEmail.getText().toString())) {
            etUserEmail.setError(getResources().getString(R.string.invalid_email));
            etUserEmail.requestFocus();
        } else if (etUserMobileNo.getText().toString().trim().equals("")) {
            etUserMobileNo.setError(getResources().getString(R.string.alert_empty_field));
            etUserMobileNo.requestFocus();
        } else if (!isMobileNoValid(etUserMobileNo.getText().toString())) {
            etUserMobileNo.setError(getResources().getString(R.string.invalid_phoneNo));
            etUserMobileNo.requestFocus();
        } else {
            saveFeedback();
        }
    }


    private void displayAlert(String msg) {
        tvAlert.setVisibility(View.VISIBLE);
        tvAlert.setText(msg);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mMediatorCallback.changeFragmentContainerVisibility(View.GONE, View.VISIBLE);
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
        return sharedPref.getString(LANGUAGE_SELECTED, LANGUAGE_ARABIC);
    }
}
