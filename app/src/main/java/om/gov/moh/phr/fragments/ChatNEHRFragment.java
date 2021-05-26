package om.gov.moh.phr.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import om.gov.moh.phr.R;
import om.gov.moh.phr.adapters.ChatRoomAdapter;
import om.gov.moh.phr.adapters.ChatsRoomNEHRAdapter;
import om.gov.moh.phr.apimodels.ApigetMessages;
import om.gov.moh.phr.interfaces.AdapterToFragmentConnectorInterface;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.GlobalMethodsKotlin;
import om.gov.moh.phr.models.MyConstants;
import om.gov.moh.phr.models.MyProgressDialog;

import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;

public class ChatNEHRFragment extends Fragment implements AdapterToFragmentConnectorInterface {
    private Context mContext;
    private MediatorInterface mMediatorCallback;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private MyProgressDialog mProgressDialog;
    private RequestQueue mQueue;
    private static final String ARG_PARAM1 = "param1";
    private ApigetMessages.Result mPatient;
    private RecyclerView rvPatientList;
    private ChatsRoomNEHRAdapter mAdapter;
    private ImageView ivSend;
    private EditText etTxtMessage;
    private ArrayList<ApigetMessages.Result> messagesArrayList;
    private ApigetMessages responseHolder;
    private DataUpdateReceiver dataUpdateReceiver;
    public static ChatNEHRFragment newInstance(ApigetMessages.Result patient) {
        ChatNEHRFragment fragment = new ChatNEHRFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, patient);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPatient = (ApigetMessages.Result) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        mProgressDialog = new MyProgressDialog(mContext);// initializes progress dialog
        mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
        setUpToolBar(view);
        setUpView(view);
        getChatRoomMessages();
        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etTxtMessage.getText().toString().trim().equals(""))
                    sendNewMessage();
            }
        });
        etTxtMessage.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if(!etTxtMessage.getText().toString().trim().equals(""))
                        sendNewMessage();
                    return true;
                }
                return false;
            }
        });
        //hide the keyboard when etTxtMessage lose the focus
        etTxtMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideSoftKeyboard(v);
                }
            }
        });
        return view;
    }

    private void setUpToolBar(View parentView) {
        ImageButton ibBackBtn = parentView.findViewById(R.id.ib_toolbar_back_button);
        ibBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.VISIBLE);
                mToolbarControllerCallback.customToolbarBackButtonClicked();
            }
        });
        TextView tvTitle = parentView.findViewById(R.id.tv_toolbar_title);
        tvTitle.setTextSize(12);
        tvTitle.setText(mPatient.getCreatedName());
    }

    private void setUpView(View parentView) {
        rvPatientList = parentView.findViewById(R.id.rv_chat_messages);
        ivSend = parentView.findViewById(R.id.iv_send);
        etTxtMessage = parentView.findViewById(R.id.et_message);
    }

    private void getChatRoomMessages() {
        mProgressDialog.showDialog();
        String fullUrl = API_NEHR_URL + "chat/getMessage/" + mPatient.getMessageId();
        Log.d("resp-getMessages-URl", fullUrl);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fullUrl, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mContext != null && isAdded()) {
                    try {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {
                            Log.d("resp-getMessages-URl", response.toString());
                            Gson gson = new Gson();
                            responseHolder = gson.fromJson(response.toString(), ApigetMessages.class);
                            setUpPatientListRecyclerView(responseHolder.getResult());
                            messagesArrayList = new ArrayList<>();
                            messagesArrayList.addAll(responseHolder.getResult());
                            mProgressDialog.dismissDialog();
                        }else
                            GlobalMethodsKotlin.Companion.showAlertErrorDialog(mContext);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if (mContext != null && isAdded())
                    mProgressDialog.dismissDialog();
                if(error instanceof AuthFailureError)
                    GlobalMethodsKotlin.Companion.showAuthFailureAlertErrorDialog(mContext);
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

    private void setUpPatientListRecyclerView(ArrayList<ApigetMessages.Result> patientList) {
        mAdapter = new ChatsRoomNEHRAdapter(patientList, mContext, ChatNEHRFragment.this);
        final LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        rvPatientList.setLayoutManager(layoutManager);
        rvPatientList.setItemAnimator(new DefaultItemAnimator());
        rvPatientList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        // this line to show the last message in the chat room ..
        layoutManager.setStackFromEnd(true);
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
    private void sendNewMessage() {
        String url = API_NEHR_URL+"chat/create";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, getJSONRequestParams()
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mContext != null) {
                    try {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {
                            Log.d("createChat", response.getString(MyConstants.API_RESPONSE_MESSAGE));
                            addNewItemIntoRecyclerView();
                        }else
                            GlobalMethodsKotlin.Companion.showAlertErrorDialog(mContext);
                    } catch (JSONException e) {
                        Log.d("createChat", e.getMessage());
                        e.printStackTrace();
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mContext != null && isAdded()) {
                    Log.d("creatChat_error", error.toString());
                    error.printStackTrace();
                    if(error instanceof AuthFailureError)
                        GlobalMethodsKotlin.Companion.showAuthFailureAlertErrorDialog(mContext);
                }
            }
        }) {
            //
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);

        mQueue.add(jsonObjectRequest);

    }

    private void addNewItemIntoRecyclerView() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm", Locale.US);
        Calendar calobj = Calendar.getInstance();
        String ReminderDate = dateFormat.format(calobj.getTime());
        ApigetMessages.Result e = new ApigetMessages().new Result();
        e.setCreatedBy(mMediatorCallback.getAccessToken().getAccessLoginId());
        e.setMessageBody(etTxtMessage.getText().toString());
        e.setCreatedDate(ReminderDate);
        messagesArrayList.add(e);
        setUpPatientListRecyclerView(messagesArrayList);
        etTxtMessage.setText("");
    }

    private JSONObject getJSONRequestParams() {
        Map<String, Object> params = new HashMap<>();
        try {
            params.put("subject", "Subject");
            params.put("createdBy", mMediatorCallback.getAccessToken().getAccessLoginId());
            params.put("prevMessageId", mPatient.getMessageId());
            params.put("reminderYn", "Y");
            params.put("reminderFreqId", 1);
            DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
            Calendar calobj = Calendar.getInstance();
            String ReminderDate = dateFormat.format(calobj.getTime());
            params.put("nextRemindDate", ReminderDate);
            params.put("expiryDate", ReminderDate);
            params.put("messageBody", etTxtMessage.getText().toString());
            JSONObject recipientObj = new JSONObject();
            recipientObj.put("recipientCode", mPatient.getCreatedBy());
            params.put("recipient", recipientObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject(params);
    }
    private void hideSoftKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dataUpdateReceiver == null) dataUpdateReceiver = new DataUpdateReceiver();
        IntentFilter intentFilter = new IntentFilter("BODY");
        mContext.registerReceiver(dataUpdateReceiver, intentFilter);
    }
    @Override
    public void onPause() {
        super.onPause();
        if (dataUpdateReceiver != null) mContext.unregisterReceiver(dataUpdateReceiver);
    }
    private class DataUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.requireNonNull(intent.getAction()).equals("BODY")) {
                SharedPreferences sharedPref = mContext.getSharedPreferences("CHAT-BODY", Context.MODE_PRIVATE);
                String messageBody = sharedPref.getString("MESSAGE-BODY", null);
                String messageSender = sharedPref.getString("MESSAGE-SENDER", null);
                if (mPatient.getCreatedBy().equals(messageSender)) {
                    DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm", Locale.US);
                    Calendar calobj = Calendar.getInstance();
                    String ReminderDate = dateFormat.format(calobj.getTime());
                    ApigetMessages.Result e = new ApigetMessages().new Result();
                    e.setCreatedBy(messageSender);
                    e.setMessageBody(messageBody);
                    e.setCreatedDate(ReminderDate);
                    messagesArrayList.add(e);
                    setUpPatientListRecyclerView(messagesArrayList);
                    etTxtMessage.setText("");
                }
                clearChatBodySharedPrefs();
            }
        }
    }

    private void clearChatBodySharedPrefs() {
        SharedPreferences sharedPref;
        SharedPreferences.Editor editor;

        sharedPref = mContext.getSharedPreferences("CHAT-BODY", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        editor.remove("MESSAGE-BODY");
        editor.remove("MESSAGE-SENDER");
        editor.apply();

    }
}