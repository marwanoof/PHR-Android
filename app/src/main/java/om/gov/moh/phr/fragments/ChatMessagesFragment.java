package om.gov.moh.phr.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import om.gov.moh.phr.R;
import om.gov.moh.phr.activities.MainActivity;
import om.gov.moh.phr.adapters.ChatRoomAdapter;
import om.gov.moh.phr.apimodels.ApiFriendChatListHolder;
import om.gov.moh.phr.apimodels.ApiHomeHolder;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.MyProgressDialog;

import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_MESSAGE;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_RESULT;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatMessagesFragment extends Fragment {
    private static final String API_URL_GET_MESSAGES_LIST = API_NEHR_URL + "chat/getMessage/";
    private static final String API_URL_SEND_MESSAGES_LIST = API_NEHR_URL + "chat/create";
    private static final String PARAM_API_Message_ITEM = "PARAM_API_Message_ITEM";
    private static final String PARAM_API_HOME_Message_ITEM = "PARAM_API_HOME_Message_ITEM";
    private RequestQueue mQueue;
    private MyProgressDialog mProgressDialog;
    private Context mContext;
    private MediatorInterface mMediatorCallback;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private RecyclerView rvChatRoomMessages;
    private ApiFriendChatListHolder.ApiFriendListInfo messageObj;
    private ApiHomeHolder.ApiChatMessages apiChatMessagesObj;
    public ChatRoomAdapter mAdapter;
    private EditText etNewMessageToSend;
    private ArrayList<ApiFriendChatListHolder.ApiFriendListInfo> messagesArrayList;
    private DataUpdateReceiver dataUpdateReceiver;
    private View view;

    public ChatMessagesFragment() {
        // Required empty public constructor
    }

    public static ChatMessagesFragment newInstance(ApiFriendChatListHolder.ApiFriendListInfo messageObj) {
        ChatMessagesFragment fragment = new ChatMessagesFragment();
        Bundle args = new Bundle();
        args.putSerializable(PARAM_API_Message_ITEM, messageObj);
        fragment.setArguments(args);
        return fragment;
    }

    public static ChatMessagesFragment newInstance(ApiHomeHolder.ApiChatMessages apiChatMessagesObj) {
        ChatMessagesFragment fragment = new ChatMessagesFragment();
        Bundle args = new Bundle();
        args.putSerializable(PARAM_API_HOME_Message_ITEM, apiChatMessagesObj);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().getSerializable(PARAM_API_Message_ITEM) != null) {
            messageObj = (ApiFriendChatListHolder.ApiFriendListInfo) getArguments().getSerializable(PARAM_API_Message_ITEM);
        }else {
            apiChatMessagesObj = (ApiHomeHolder.ApiChatMessages) getArguments().getSerializable(PARAM_API_HOME_Message_ITEM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_chat_messages, container, false);
            TextView tvTitle = view.findViewById(R.id.tv_toolbar_title);
            ImageButton ibBack = view.findViewById(R.id.ib_toolbar_back_button);
            tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mToolbarControllerCallback.customToolbarBackButtonClicked();
                }
            });
            ibBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mToolbarControllerCallback.customToolbarBackButtonClicked();
                }
            });
            mProgressDialog = new MyProgressDialog(mContext);
            mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
            etNewMessageToSend = view.findViewById(R.id.et_message);
            rvChatRoomMessages = view.findViewById(R.id.rv_chat_room);
            ImageView ivSend = view.findViewById(R.id.iv_send);
            ivSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!etNewMessageToSend.getText().toString().isEmpty())
                        sendNewMessage();
                }
            });
            String getMessagesUrl;
            if(messageObj!=null) {
                tvTitle.setText(messageObj.getCreatedName());
                getMessagesUrl = API_URL_GET_MESSAGES_LIST + messageObj.getMessageId();
            }else {
                tvTitle.setText(apiChatMessagesObj.getCreatedName());
                getMessagesUrl = API_URL_GET_MESSAGES_LIST + apiChatMessagesObj.getMessageId();
            }
            getChatRoomMessages(getMessagesUrl);
        } else {
            if (view.getParent() != null)
                ((ViewGroup) view.getParent()).removeView(view);
        }
        return view;
    }

    private void getChatRoomMessages(String url) {
        mProgressDialog.showDialog();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mContext != null&&isAdded()) {
                    try {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {
                                Gson gson = new Gson();
                                ApiFriendChatListHolder responseHolder = gson.fromJson(response.toString(), ApiFriendChatListHolder.class);
                                messagesArrayList = new ArrayList<>();
                                messagesArrayList.addAll(responseHolder.getmResult());
                                setupRecyclerView(responseHolder.getmResult());

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

    private void setupRecyclerView(ArrayList<ApiFriendChatListHolder.ApiFriendListInfo> getmResult) {
        mAdapter = new ChatRoomAdapter(getmResult, mContext, mMediatorCallback);
        final LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        rvChatRoomMessages.setLayoutManager(layoutManager);
        rvChatRoomMessages.setItemAnimator(new DefaultItemAnimator());
        rvChatRoomMessages.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        // this line to show the last message in the chat room ..
        layoutManager.setStackFromEnd(true);
       // clearChatBodySharedPrefs();
    }

    private void sendNewMessage() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, API_URL_SEND_MESSAGES_LIST, getJSONRequestParams()
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mContext != null) {
                    try {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {
                            addNewItemIntoRecyclerView();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mContext != null && isAdded()) {
                    error.printStackTrace();
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
        ApiFriendChatListHolder.ApiFriendListInfo e = new ApiFriendChatListHolder().new ApiFriendListInfo();
        e.setSubject("Subject");
        e.setCreatedBy(mMediatorCallback.getCurrentUser().getCivilId());
        e.setMessageBody(etNewMessageToSend.getText().toString());
        e.setCreatedDate(ReminderDate);
        messagesArrayList.add(e);
        setupRecyclerView(messagesArrayList);
        etNewMessageToSend.setText("");
    }

    private JSONObject getJSONRequestParams() {
        Map<String, Object> params = new HashMap<>();
        try {
            params.put("subject", "Subject");
            params.put("createdBy", mMediatorCallback.getCurrentUser().getCivilId());
            params.put("reminderYn", "Y");
            params.put("reminderFreqId", 1);
            DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
            Calendar calobj = Calendar.getInstance();
            String ReminderDate = dateFormat.format(calobj.getTime());
            params.put("nextRemindDate", ReminderDate);
            params.put("expiryDate", ReminderDate);
            params.put("messageBody", etNewMessageToSend.getText().toString());
            JSONObject recipientObj = new JSONObject();
            if(messageObj!=null) {
                params.put("prevMessageId", messageObj.getMessageId());
                recipientObj.put("recipientCode", messageObj.getCreatedBy());
            }else {
                recipientObj.put("recipientCode", apiChatMessagesObj.getCreatedBy());
                params.put("prevMessageId", apiChatMessagesObj.getMessageId());
            }
            params.put("recipient", recipientObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject(params);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dataUpdateReceiver == null) dataUpdateReceiver = new DataUpdateReceiver();
        IntentFilter intentFilter = new IntentFilter("BODY");
        mContext.registerReceiver(dataUpdateReceiver, intentFilter);
        mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (dataUpdateReceiver != null) mContext.unregisterReceiver(dataUpdateReceiver);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(apiChatMessagesObj!=null)
            mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.VISIBLE);
    }

    private class DataUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.requireNonNull(intent.getAction()).equals("BODY")) {
                SharedPreferences sharedPref = mContext.getSharedPreferences("CHAT-BODY", Context.MODE_PRIVATE);
                String messageBody = sharedPref.getString("MESSAGE-BODY", null);
                String messageSender = sharedPref.getString("MESSAGE-SENDER", null);
             if(messageObj!=null&&messageObj.getCreatedName().trim().equalsIgnoreCase(messageSender.trim())){
                    DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm", Locale.US);
                    Calendar calobj = Calendar.getInstance();
                    String ReminderDate = dateFormat.format(calobj.getTime());
                    ApiFriendChatListHolder.ApiFriendListInfo e = new ApiFriendChatListHolder().new ApiFriendListInfo();
                    e.setSubject("Subject");
                    e.setCreatedBy(messageSender);
                    e.setMessageBody(messageBody);
                    e.setCreatedDate(ReminderDate);
                    messagesArrayList.add(e);
                   setupRecyclerView(messagesArrayList);
                }else if(apiChatMessagesObj!=null&&apiChatMessagesObj.getCreatedName().trim().equalsIgnoreCase(messageSender.trim())){
                    DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm", Locale.US);
                    Calendar calobj = Calendar.getInstance();
                    String ReminderDate = dateFormat.format(calobj.getTime());
                    ApiFriendChatListHolder.ApiFriendListInfo e = new ApiFriendChatListHolder().new ApiFriendListInfo();
                    e.setSubject("Subject");
                    e.setCreatedBy(messageSender);
                    e.setMessageBody(messageBody);
                    e.setCreatedDate(ReminderDate);
                    messagesArrayList.add(e);
                    setupRecyclerView(messagesArrayList);
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
