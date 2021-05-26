package om.gov.moh.phr.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.SearchView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import om.gov.moh.phr.R;
import om.gov.moh.phr.adapters.ChatsHomeAdapter;
import om.gov.moh.phr.adapters.RecentlySearchedPatientAdapter;
import om.gov.moh.phr.apimodels.ApiGetRecentSearch;
import om.gov.moh.phr.apimodels.ApigetMessages;
import om.gov.moh.phr.interfaces.AdapterToFragmentConnectorInterface;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.GlobalMethodsKotlin;
import om.gov.moh.phr.models.MyProgressDialog;

import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;

public class HomeNEHRFragment extends Fragment implements AdapterToFragmentConnectorInterface, SwipeRefreshLayout.OnRefreshListener{
    private MyProgressDialog mProgressDialog;
    private RequestQueue mQueue;
    private Context mContext;
    private MediatorInterface mMediatorCallback;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private TextView tvChatsBtn, tvRecentlySearched, tvNotificationBtn, tvSearchBtn, tvNoUnreadChats, tvNoChats;
    private RecyclerView recentSearhedRecyclerView, rvUnreadMessages, rvAll;
    private RecentlySearchedPatientAdapter mAdapter;
    private ImageView ivLeftArrow, ivRightArrow;
    private LinearLayoutManager HorizontalLayout;
    private View parentView;
    private ArrayList<ApigetMessages.Result> mUnreadMessages, mAllMessages;
    private SearchView searchView;
    private ApigetMessages getMessagesResponse;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ApiGetRecentSearch responseRecentSearchedHolder;
    public static HomeNEHRFragment newInstance() {
        HomeNEHRFragment fragment = new HomeNEHRFragment();
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
        if (parentView == null) {
            parentView = inflater.inflate(R.layout.fragment_home_nehr, container, false);
            mProgressDialog = new MyProgressDialog(mContext);// initializes progress dialog
            mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
            setupViews(parentView);
            if (mMediatorCallback.isConnected()) {
                getMessages();
                getRecentlySearchedPatients();
                swipeRefreshLayout.setOnRefreshListener(this);
                swipeRefreshLayout.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                swipeRefreshLayout.setRefreshing(true);
                                                rvUnreadMessages.setAdapter(null);
                                                rvAll.setAdapter(null);
                                                recentSearhedRecyclerView.setAdapter(null);
                                                getMessages();
                                                getRecentlySearchedPatients();
                                            }
                                        }
                );
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        if (responseRecentSearchedHolder != null && responseRecentSearchedHolder.getResult() != null) {
                            if (s.length() == 0) {
                                setupRecentlySearchedPatientsRecyclerView(responseRecentSearchedHolder.getResult());
                            } else {
                                ArrayList<ApiGetRecentSearch.Result> filteredList = new ArrayList<>();
                                for (ApiGetRecentSearch.Result searchedItem : responseRecentSearchedHolder.getResult()) {
                                    if (String.valueOf(searchedItem.getCivilId()).contains(s) ||
                                            searchedItem.getFirstName().toLowerCase().contains(s) ||
                                            searchedItem.getSecondName().toLowerCase().contains(s) ||
                                            searchedItem.getThirdName().toLowerCase().contains(s) ||
                                            searchedItem.getFourthName().toLowerCase().contains(s)) {
                                        filteredList.add(searchedItem);
                                    }
                                }
                                setupRecentlySearchedPatientsRecyclerView(filteredList);
                            }
                        }
                        return false;
                    }

                });
            } else {
                hideAllView();
                GlobalMethodsKotlin.Companion.showAlertDialog(mContext, getResources().getString(R.string.no_internet_title), getResources().getString(R.string.alert_no_connection), getResources().getString(R.string.ok), R.drawable.ic_error);
            }
            setActiveButton(tvChatsBtn, tvNotificationBtn);
            tvChatsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setActiveButton(tvChatsBtn, tvNotificationBtn);
                    rvAll.setVisibility(View.VISIBLE);
                    rvUnreadMessages.setVisibility(View.VISIBLE);
                }
            });
            tvNotificationBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setActiveButton(tvNotificationBtn, tvChatsBtn);
                    rvAll.setVisibility(View.GONE);
                    rvUnreadMessages.setVisibility(View.GONE);
                }
            });

        } else {
            if (parentView.getParent() != null)
                ((ViewGroup) parentView.getParent()).removeView(parentView);
        }
        return parentView;
    }

    private void setupViews(View view) {
        TextView tvPersonName = view.findViewById(R.id.tvPersonName);
        tvPersonName.setText(getString(R.string.hello) + " " + mMediatorCallback.getAccessToken().getPersonName() + " " + getString(R.string.dots));
        recentSearhedRecyclerView = view.findViewById(R.id.recyclerview);
        ivLeftArrow = view.findViewById(R.id.iv_leftArrow);
        ivRightArrow = view.findViewById(R.id.iv_rightArrow);
        tvChatsBtn = view.findViewById(R.id.tvChatsBtn);
        tvNotificationBtn = view.findViewById(R.id.tvNotificationBtn);
        rvUnreadMessages = view.findViewById(R.id.rvUnreadMessages);
        rvAll = view.findViewById(R.id.rvAll);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        searchView = view.findViewById(R.id.etSearchedText);
        tvRecentlySearched = view.findViewById(R.id.tvRecentlySearched);
        tvSearchBtn = view.findViewById(R.id.tvSearchBtn);
        tvNoUnreadChats = view.findViewById(R.id.tvNoUnreadChat);
        tvNoChats = view.findViewById(R.id.tvNoChatMessages);
        AutoCompleteTextView search_text = (AutoCompleteTextView) searchView.findViewById(searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null));
        search_text.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.text_size_10sp));
        HorizontalLayout
                = new LinearLayoutManager(
                mContext,
                LinearLayoutManager.HORIZONTAL,
                false);
        ivLeftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HorizontalLayout.findFirstVisibleItemPosition() > 0) {
                    recentSearhedRecyclerView.smoothScrollToPosition(HorizontalLayout.findFirstVisibleItemPosition() - 1);
                } else {
                    recentSearhedRecyclerView.smoothScrollToPosition(0);
                }

            }
        });

        ivRightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recentSearhedRecyclerView.smoothScrollToPosition(HorizontalLayout.findLastVisibleItemPosition() + 1);
            }
        });
    }

    private void setActiveButton(TextView activeButton, TextView disActivatedButton) {
        activeButton.setBackground(getResources().getDrawable(R.drawable.card_edge));
        activeButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
        activeButton.setTextColor(getResources().getColor(android.R.color.white));

        disActivatedButton.setBackground(null);
        disActivatedButton.setBackgroundTintList(null);
        disActivatedButton.setTextColor(getResources().getColor(android.R.color.black));
    }

    private void setupRecentlySearchedPatientsRecyclerView(ArrayList<ApiGetRecentSearch.Result> result) {
        if (result.size() == 0) {
            ivLeftArrow.setVisibility(View.GONE);
            ivRightArrow.setVisibility(View.GONE);
            recentSearhedRecyclerView.setVisibility(View.GONE);
            tvRecentlySearched.setVisibility(View.GONE);
        } else {
            ivLeftArrow.setVisibility(View.VISIBLE);
            ivRightArrow.setVisibility(View.VISIBLE);
            recentSearhedRecyclerView.setVisibility(View.VISIBLE);
            tvRecentlySearched.setVisibility(View.VISIBLE);
            LinearLayoutManager recyclerViewLayoutManager
                    = new LinearLayoutManager(
                    mContext);
            // Set LayoutManager on Recycler View
            recentSearhedRecyclerView.setLayoutManager(
                    recyclerViewLayoutManager);
            // calling constructor of adapter
            // with source list as a parameter
            mAdapter = new RecentlySearchedPatientAdapter(mContext, result);
            // Set Horizontal Layout Manager
            // for Recycler view
            recentSearhedRecyclerView.setLayoutManager(HorizontalLayout);
            // Set adapter on recycler view
            recentSearhedRecyclerView.setAdapter(mAdapter);
        }
    }


    private void getRecentlySearchedPatients() {
        mProgressDialog.showDialog();
        swipeRefreshLayout.setRefreshing(true);
        String fullUrl = API_NEHR_URL + "demographics/getRecentSearch";
        Log.d("resp-getRecentSearchURl", fullUrl);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fullUrl, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("resp-getRecentSearch", response.toString());
                try {
                    if (response.getInt(API_RESPONSE_CODE) == 0) {
                        if (mContext != null && isAdded()) {
                            Gson gson = new Gson();
                            responseRecentSearchedHolder = gson.fromJson(response.toString(), ApiGetRecentSearch.class);
                            setupRecentlySearchedPatientsRecyclerView(responseRecentSearchedHolder.getResult());
                            mProgressDialog.dismissDialog();
                        }
                    } else
                        GlobalMethodsKotlin.Companion.showAlertErrorDialog(mContext);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if (mContext != null && isAdded())
                    mProgressDialog.dismissDialog();
                swipeRefreshLayout.setRefreshing(false);
                if (error instanceof AuthFailureError)
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

    private void showUnReadMessages() {
        LinearLayoutManager RecyclerViewLayoutManager
                = new LinearLayoutManager(
                mContext);
        rvUnreadMessages.setLayoutManager(
                RecyclerViewLayoutManager);
        ChatsHomeAdapter chatsHomeAdapter = new ChatsHomeAdapter(mUnreadMessages, mContext, HomeNEHRFragment.this);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(rvUnreadMessages.getContext(),
                RecyclerViewLayoutManager.getOrientation());
        rvUnreadMessages.addItemDecoration(mDividerItemDecoration);
        rvUnreadMessages.setAdapter(chatsHomeAdapter);
    }

    private void showAllMessages() {
        LinearLayoutManager RecyclerViewLayoutManager
                = new LinearLayoutManager(
                mContext);
        rvAll.setLayoutManager(
                RecyclerViewLayoutManager);
        ChatsHomeAdapter chatsHomeAdapter = new ChatsHomeAdapter(mAllMessages, mContext, HomeNEHRFragment.this);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(rvAll.getContext(),
                RecyclerViewLayoutManager.getOrientation());
        rvAll.addItemDecoration(mDividerItemDecoration);
        rvAll.setAdapter(chatsHomeAdapter);
    }

    private void getMessages() {
        mProgressDialog.showDialog();
        swipeRefreshLayout.setRefreshing(true);
        String fullUrl = API_NEHR_URL + "chat/getMessageByRecipient/" + mMediatorCallback.getAccessToken().getAccessLoginId();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fullUrl, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d("resp-getMessages", response.toString());
                if (mContext != null && isAdded()) {
                    try {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {
                            Gson gson = new Gson();
                            getMessagesResponse = gson.fromJson(response.toString(), ApigetMessages.class);
                            mUnreadMessages = new ArrayList<>();
                            mAllMessages = new ArrayList<>();
                            if (getMessagesResponse.getResult() != null) {
                                for (int i = 0; i < getMessagesResponse.getResult().size(); i++) {
                                    ApigetMessages.Result obj = getMessagesResponse.getResult().get(i);
                                    if (obj.getUnreadCount() != null) {
                                        if (obj.getUnreadCount() > 0)
                                            mUnreadMessages.add(obj);
                                        else
                                            mAllMessages.add(obj);
                                    }
                                }

                                showUnReadMessages();
                                showAllMessages();
                            }
                        } else {
                            rvUnreadMessages.setVisibility(View.GONE);
                            rvAll.setVisibility(View.GONE);
                            tvNoChats.setVisibility(View.VISIBLE);
                            tvNoUnreadChats.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mProgressDialog.dismissDialog();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if (mContext != null && isAdded())
                    mProgressDialog.dismissDialog();
                if (error instanceof AuthFailureError)
                    GlobalMethodsKotlin.Companion.showAuthFailureAlertErrorDialog(mContext);
                swipeRefreshLayout.setRefreshing(false);
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
        int socketTimeout = 50000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        mQueue.add(jsonObjectRequest);
    }

    @Override
    public <T> void onMyListItemClicked(T dataToPass, String dataTitle) {
        mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.GONE);
        if (dataToPass instanceof ApigetMessages.Result)
            mMediatorCallback.changeFragmentTo(ChatNEHRFragment.newInstance((ApigetMessages.Result) dataToPass), ChatNEHRFragment.class.getSimpleName());
    }

    @Override
    public <T> void onMyListItemClicked(T dataToPass, String dataTitle, int position) {

    }

    @Override
    public void onRefresh() {
        rvUnreadMessages.setAdapter(null);
        rvAll.setAdapter(null);
        recentSearhedRecyclerView.setAdapter(null);
        getMessages();
        getRecentlySearchedPatients();
    }

    private void hideAllView() {
        tvRecentlySearched.setVisibility(View.GONE);
        ivRightArrow.setVisibility(View.GONE);
        ivLeftArrow.setVisibility(View.GONE);
        searchView.setVisibility(View.GONE);
        tvSearchBtn.setVisibility(View.GONE);
    }
}