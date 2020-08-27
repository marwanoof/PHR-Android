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

import om.gov.moh.phr.R;
import om.gov.moh.phr.activities.MainActivity;
import om.gov.moh.phr.adapters.ChatRoomAdapter;
import om.gov.moh.phr.apimodels.ApiFriendChatListHolder;
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
    private RequestQueue mQueue;
    private MyProgressDialog mProgressDialog;
    private Context mContext;
    private MediatorInterface mMediatorCallback;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private RecyclerView rvChatRoomMessages;
    private ApiFriendChatListHolder.ApiFriendListInfo messageObj;
    public ChatRoomAdapter mAdapter;
    private EditText etNewMessageToSend;
    private ArrayList<ApiFriendChatListHolder.ApiFriendListInfo> messagesArrayList;
    private DataUpdateReceiver dataUpdateReceiver;

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
        if (getArguments() != null) {
            messageObj = (ApiFriendChatListHolder.ApiFriendListInfo) getArguments().getSerializable(PARAM_API_Message_ITEM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_messages, container, false);
        TextView tvTitle = view.findViewById(R.id.tv_toolbar_title);
        tvTitle.setText(getResources().getString(R.string.chat_messages_title));
        ImageButton ibBack = view.findViewById(R.id.ib_toolbar_back_button);
        /*tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mToolbarControllerCallback.customToolbarBackButtonClicked();
            }
        });*/
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
                if (etNewMessageToSend.getText().toString().isEmpty())
                    Toast.makeText(mContext, getResources().getString(R.string.enter_msg_to_send), Toast.LENGTH_SHORT).show();
                else
                    sendNewMessage();
            }
        });
        String getMessagesUrl = API_URL_GET_MESSAGES_LIST + messageObj.getMessageId();
        getChatRoomMessages(getMessagesUrl);
        return view;
    }

    private void getChatRoomMessages(String url) {
        mProgressDialog.showDialog();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mContext != null) {
                    try {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {
                            try {
                                Gson gson = new Gson();
                                ApiFriendChatListHolder responseHolder = gson.fromJson(response.toString(), ApiFriendChatListHolder.class);
                                Log.d("resp-dependants", response.getJSONArray(API_RESPONSE_RESULT).toString());
                                messagesArrayList = new ArrayList<>();
                                messagesArrayList.addAll(responseHolder.getmResult());
                                setupRecyclerView(responseHolder.getmResult());
                            } catch (JSONException e) {
                                e.printStackTrace();
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
                    Log.d("get_friendList", error.toString());
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

    private void setupRecyclerView(ArrayList<ApiFriendChatListHolder.ApiFriendListInfo> getmResult) {
        mAdapter = new ChatRoomAdapter(getmResult, mContext, mMediatorCallback);
        final LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        rvChatRoomMessages.setLayoutManager(layoutManager);
        rvChatRoomMessages.setItemAnimator(new DefaultItemAnimator());
        rvChatRoomMessages.setAdapter(mAdapter);
        // to go to the last message on chat
        rvChatRoomMessages.smoothScrollToPosition(getmResult.size() - 1);
        // this code is to scroll down automatically when new unread message is coming
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mAdapter.getItemCount();
                int lastVisiblePosition =
                        layoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    rvChatRoomMessages.scrollToPosition(positionStart);
                }
            }
        });
        mAdapter.notifyDataSetChanged();
        clearChatBodySharedPrefs();
    }

    private void sendNewMessage() {

        mProgressDialog.showDialog();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, API_URL_SEND_MESSAGES_LIST, getJSONRequestParams()
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mContext != null) {
                    try {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {
                            Log.d("upload", response.getString(API_RESPONSE_MESSAGE));
                            // Toast.makeText(mContext, response.getString(API_RESPONSE_MESSAGE), Toast.LENGTH_SHORT).show();
                            addNewItemIntoRecyclerView();
                        } else {

                            mProgressDialog.dismissDialog();
                        }
                    } catch (JSONException e) {
                        Log.d("createChat", e.getMessage());
                        e.printStackTrace();
                    }

                    mProgressDialog.dismissDialog();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mContext != null && isAdded()) {
                    Log.d("creatChat_error", error.toString());
                    error.printStackTrace();
                    Toast.makeText(mContext, "error", Toast.LENGTH_SHORT).show();
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
                //         headers.put("Authorization", API_GET_TOKEN_BEARER + mMediatorCallback.getAccessToken().getAccessTokenString());


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
            params.put("prevMessageId", messageObj.getMessageId());
            params.put("reminderYn", "Y");
            params.put("reminderFreqId", 1);
            DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
            Calendar calobj = Calendar.getInstance();
            String ReminderDate = dateFormat.format(calobj.getTime());
            params.put("nextRemindDate", ReminderDate);
            params.put("expiryDate", ReminderDate);
            params.put("messageBody", etNewMessageToSend.getText().toString());
            JSONObject recipientObj = new JSONObject();
            recipientObj.put("recipientCode", messageObj.getCreatedBy());
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
    }

    private class DataUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("BODY")) {
                SharedPreferences sharedPref = mContext.getSharedPreferences("CHAT-BODY", Context.MODE_PRIVATE);
                String messageBody = sharedPref.getString("MESSAGE-BODY", null);
                String messageSender = sharedPref.getString("MESSAGE-SENDER", null);
                if (messageObj.getCreatedBy().equals(messageSender)) {
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
                    etNewMessageToSend.setText("");
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
