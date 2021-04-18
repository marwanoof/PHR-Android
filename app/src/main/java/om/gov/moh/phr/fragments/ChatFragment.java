package om.gov.moh.phr.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.BroadcastReceiver;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import java.util.Objects;

import om.gov.moh.phr.R;
import om.gov.moh.phr.activities.MainActivity;
import om.gov.moh.phr.adapters.ChatRecyclerViewAdapter;
import om.gov.moh.phr.apimodels.ApiFriendChatListHolder;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.GlobalMethodsKotlin;
import om.gov.moh.phr.models.MyProgressDialog;

import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_RESULT;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String API_URL_FRIEND_LIST = API_NEHR_URL + "chat/getMessageByRecipient/";
    private RequestQueue mQueue;
    private MyProgressDialog mProgressDialog;
    private Context mContext;
    private MediatorInterface mMediatorCallback;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private RecyclerView rvChatMessages;
    private ApiFriendChatListHolder responseHolder;
    private DataUpdateReceiver dataUpdateReceiver;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View view;
    private static final String PARAM1 = "PARAM1";
    private String pageTitle;
    private ChatRecyclerViewAdapter mAdapter;
    private CardView noRecordCardView;
    private TextView tvAlert;

    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance(String title) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(PARAM1, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        mMediatorCallback = (MediatorInterface) context;
        mToolbarControllerCallback = (ToolbarControllerInterface) context;
        mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.GONE);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            pageTitle = getArguments().getString(PARAM1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_chat, container, false);
            clearNotificationSharedPrefs(3);
            TextView tvTitle = view.findViewById(R.id.tv_toolbar_title);
            tvTitle.setText(pageTitle);
            ImageButton ibBack = view.findViewById(R.id.ib_toolbar_back_button);
            ibBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mToolbarControllerCallback.customToolbarBackButtonClicked();
                }
            });
            rvChatMessages = view.findViewById(R.id.rv_chat_messages);
            mProgressDialog = new MyProgressDialog(mContext);
            mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
            swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
            noRecordCardView = view.findViewById(R.id.cardViewNoRecords);
            tvAlert = view.findViewById(R.id.tv_alert);
            String url = API_URL_FRIEND_LIST + mMediatorCallback.getCurrentUser().getCivilId();
            if (mMediatorCallback.isConnected()) {
                getChatFriendList(url);
            } else {
                GlobalMethodsKotlin.Companion.showAlertDialog(mContext, getResources().getString(R.string.no_internet_title), getResources().getString(R.string.alert_no_connection), getResources().getString(R.string.ok), R.drawable.ic_error);
            }
            swipeRefreshLayout.setOnRefreshListener(this);
            swipeRefreshLayout.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            swipeRefreshLayout.setRefreshing(true);
                                            String url = API_URL_FRIEND_LIST + mMediatorCallback.getCurrentUser().getCivilId();
                                            getChatFriendList(url);
                                        }
                                    }
            );
        } else {
            if (view.getParent() != null)
                ((ViewGroup) view.getParent()).removeView(view);
            SharedPreferences sharedPref = mContext.getSharedPreferences("CHAT-BODY", Context.MODE_PRIVATE);
            String messageSender = sharedPref.getString("MESSAGE-SENDER", null);
            if (messageSender != null) {
                if (responseHolder != null) {
                    for (int i = 0; i < responseHolder.getmResult().size(); i++) {
                        if (responseHolder.getmResult().get(i).getCreatedBy().trim().equalsIgnoreCase(messageSender.trim()))
                            responseHolder.getmResult().get(i).setNew(true);
                    }
                }
            }
            if (mAdapter != null)
                mAdapter.notifyDataSetChanged();
        }
        return view;
    }

    private void getChatFriendList(String url) {
        mProgressDialog.showDialog();
        swipeRefreshLayout.setRefreshing(true);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mContext != null && isAdded()) {
                    try {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {
                            Gson gson = new Gson();
                            responseHolder = gson.fromJson(response.toString(), ApiFriendChatListHolder.class);
                            setupRecyclerView(responseHolder.getmResult());
                        } else {
                            displayAlert(getResources().getString(R.string.no_chats_msg));
                            mProgressDialog.dismissDialog();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    mProgressDialog.dismissDialog();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mContext != null && isAdded()) {
                    error.printStackTrace();
                    mProgressDialog.dismissDialog();
                    swipeRefreshLayout.setRefreshing(false);
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
        mAdapter = new ChatRecyclerViewAdapter(getmResult, mContext, mMediatorCallback);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(rvChatMessages.getContext(),
                layoutManager.getOrientation());
        //rvChatMessages.addItemDecoration(mDividerItemDecoration);
        rvChatMessages.setLayoutManager(layoutManager);
        rvChatMessages.setItemAnimator(new DefaultItemAnimator());
        rvChatMessages.setAdapter(mAdapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //  mMediatorCallback.changeFragmentContainerVisibility(View.GONE, View.VISIBLE);
        mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.VISIBLE);
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
    public void onRefresh() {
        String url = API_URL_FRIEND_LIST + mMediatorCallback.getCurrentUser().getCivilId();
        getChatFriendList(url);
    }

    private class DataUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
         /*   if (intent.getAction().equals("BODY")) {
                    setupRecyclerView(responseHolder.getmResult(), true);
            }*/
            if (Objects.requireNonNull(intent.getAction()).equals("BODY")) {
                SharedPreferences sharedPref = mContext.getSharedPreferences("CHAT-BODY", Context.MODE_PRIVATE);
                String messageSender = sharedPref.getString("MESSAGE-SENDER", null);
                for (int i = 0; i < responseHolder.getmResult().size(); i++) {
                    if (responseHolder.getmResult().get(i).getCreatedBy().trim().equalsIgnoreCase(messageSender.trim()))
                        responseHolder.getmResult().get(i).setNew(true);
                }
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private void clearNotificationSharedPrefs(int notificationType) {
        SharedPreferences sharedPref;
        SharedPreferences.Editor editor;

        sharedPref = mContext.getSharedPreferences("Counting", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        switch (notificationType) {

            case 1:
                editor.remove("appointmentCount");
                editor.apply();
                break;
            case 2:
                editor.remove("notificationCount");
                editor.apply();
                break;
            case 3:
                editor.remove("chatCount");
                editor.apply();
                break;
            default:
                editor.remove("appointmentCount");
                editor.remove("notificationCount");
                editor.remove("chatCount");
                editor.apply();
        }
    }

    private void displayAlert(String msg) {
        rvChatMessages.setVisibility(View.GONE);
        noRecordCardView.setVisibility(View.VISIBLE);
        tvAlert.setVisibility(View.VISIBLE);
        tvAlert.setText(msg);
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
    }
}
