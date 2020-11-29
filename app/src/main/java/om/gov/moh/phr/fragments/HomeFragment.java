package om.gov.moh.phr.fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import om.gov.moh.phr.R;
import om.gov.moh.phr.activities.MainActivity;
import om.gov.moh.phr.adapters.ComingAppointmentListAdapter;
import om.gov.moh.phr.adapters.DependentRecyclerViewAdapter;
import om.gov.moh.phr.adapters.MessageChatsAdapter;
import om.gov.moh.phr.adapters.MyVitalListAdapter;
import om.gov.moh.phr.adapters.NotificationHomeAdapter;
import om.gov.moh.phr.adapters.NotificationsRecyclerViewAdapter;
import om.gov.moh.phr.adapters.PaginationRecyclerViewAdapter;
import om.gov.moh.phr.adapters.UpdatesListAdapter;
import om.gov.moh.phr.apimodels.ApiDemographicsHolder;
import om.gov.moh.phr.apimodels.ApiDependentsHolder;
import om.gov.moh.phr.apimodels.ApiHomeHolder;
import om.gov.moh.phr.apimodels.Notification;
import om.gov.moh.phr.interfaces.AdapterToFragmentConnectorInterface;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.DBHelper;
import om.gov.moh.phr.models.GlobalMethods;
import om.gov.moh.phr.models.GlobalMethodsKotlin;
import om.gov.moh.phr.models.MyProgressDialog;


import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_MESSAGE;
import static om.gov.moh.phr.models.MyConstants.IS_SCROLL_LIST;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_ARABIC;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_PREFS;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_SELECTED;

public class HomeFragment extends Fragment implements AdapterToFragmentConnectorInterface, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String API_URL_GET_DEMOGRAPHICS_INFO = API_NEHR_URL + "demographics/phrHome";

    private static final int NUMBER_OF_COLUMNS = 1;
    private Context mContext;
    private RequestQueue mQueue;
    private TextView tvAlert, tvPatientId, tvFullName, tvAge, tvBloodGroup, tvUserHeight, tvUserWeight, tvNameInfo, tvUserAddress, tvMobile, tvGender, tvNationality, tvDependentsTitle, tvFirstDependent, tvSecondDependent;
    private ImageView ivUserProfile, ivFirstArrow, ivSecondArrow;
    private MyProgressDialog mProgressDialog;
    private RecyclerView rvGrid;
    private MediatorInterface mMediatorCallback;
    private ToolbarControllerInterface mToolbarCallback;
    private PaginationRecyclerViewAdapter mAdapter;
    int dotscount = 3;
    private ImageView[] dots;
    // private PagerCardMainAdapter pagerCardMainAdapter;
    private ScrollView menuListScrollView;
    private ImageButton menuButton, myVitalExpandBtn, appointmentExpandBtn, notificationExpandBtn, updatesExpandBtn, chatsExpandBtn;
    private RecyclerView myVital, appointmentList, notificationList/*, updatesList*/, chatsList;
    private ViewFlipper viewFlipper;
    private float lastX;
    private ApiHomeHolder responseHolder;
    private ArrayList<ApiHomeHolder.ApiRecentVitals> recentVitalsArrayList = new ArrayList<>();
    private DBHelper dbHelper;
    private LinearLayout llMyVitalSigns, llAppointments, llNotification, llUnReadMessages;
    private View dependentDivider, parentView;
    private boolean isArabic = false;
    private ConstraintLayout personInfo, recentVitalConstraintLayout,appointmentConstraintLayout, notificationConstraintLayout, chatConstraintLayout, updateConstraintLayout;
    private static final String DEPENDENT_CIVILID = "DependentCivilID";
    private SwipeRefreshLayout swipeRefreshLayout;
    private LayoutAnimationController animation;
    private Boolean isVitalShow = false, isAppointmentShow = false, isChatShow = false, isUpdateShow = false, isNotificationShow = false;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        mMediatorCallback = (MediatorInterface) context;
        mToolbarCallback = (ToolbarControllerInterface) context;
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        isArabic = getStoredLanguage().equals(LANGUAGE_ARABIC);
        animation = AnimationUtils.loadLayoutAnimation(mContext, R.anim.delay_slide_down);
        if(parentView==null){
        // Inflate the layout for this fragment
             parentView = inflater.inflate(R.layout.fragment_home, container, false);
        //pageView = pageView.findViewById(R.id.view1);

            mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
            mProgressDialog = new MyProgressDialog(mContext);
            swipeRefreshLayout = parentView.findViewById(R.id.swipeRefreshLayoutHome);
            swipeRefreshLayout.setOnRefreshListener(this);
            setupView(parentView);
        if (IS_SCROLL_LIST) {
            menuListScrollView.setVisibility(View.VISIBLE);
            rvGrid.setVisibility(View.GONE);
            menuButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_item));
        } else {
            menuListScrollView.setVisibility(View.GONE);
            rvGrid.setVisibility(View.VISIBLE);
            menuButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_list));
        }


        //   if (mMediatorCallback.getAccessToken().getAccessCivilId().equals(mMediatorCallback.getCurrentUser().getCivilId()))

        if (mMediatorCallback.isConnected()) {
            setRecyclerViewGrid();
            getDemographicResponse();
        } else {

            displayAlert(getString(R.string.alert_no_connection));
        }
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (IS_SCROLL_LIST) {
                    menuButton.animate()
                            .alpha(0.0f)
                            .setDuration(100)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    if (isAdded() && mContext != null) {
                                        menuButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_list));
                                        menuButton.animate().alpha(1.0f);
                                    }
                                }
                            });
                    menuButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_list));
                    rvGrid.setVisibility(View.VISIBLE);
                    rvGrid.setLayoutAnimation(animation);
                    menuListScrollView.setVisibility(View.GONE);
                    IS_SCROLL_LIST = false;
                } else {
                    menuButton.animate()
                            .alpha(0.0f)
                            .setDuration(100)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    if(isAdded()) {
                                        menuButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_item));
                                        menuButton.animate().alpha(1.0f);
                                    }
                                }
                            });
                    menuButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_item));
                    rvGrid.setVisibility(View.GONE);
                    menuListScrollView.setVisibility(View.VISIBLE);
                    if (isVitalShow)
                        myVital.setLayoutAnimation(animation);
                    if (isAppointmentShow)
                        appointmentList.setLayoutAnimation(animation);
                    if (isChatShow)
                        chatsList.setLayoutAnimation(animation);

                    IS_SCROLL_LIST = true;
                }
            }
        });

        myVitalExpandBtn.setOnClickListener(this);
        appointmentExpandBtn.setOnClickListener(this);
        notificationExpandBtn.setOnClickListener(this);
        updatesExpandBtn.setOnClickListener(this);
        chatsExpandBtn.setOnClickListener(this);
        //getNotificationList();

        /* setup updates list */
      /*  ArrayList<String> updates = new ArrayList<>();
        updates.add("New Lab Result");
        updates.add("New Procedure Report");*/
        //  setupUpdatesList(updates);
        } else {
            if(parentView.getParent()!=null)
                ((ViewGroup) parentView.getParent()).removeView(parentView);
        }
        return parentView;
    }

    /*private void getNotificationList() {
        dbHelper = new DBHelper(mContext);
        ArrayList<Notification> notificationsList = new ArrayList<>();
        notificationsList = dbHelper.retrieveNotificationsRecord();
      *//*  if (notificationsList.size() == 0) {
            Notification notification = new Notification();
            notification.setTitle(getResources().getString(R.string.no_notification));
            notification.setBody("");
            notification.setPnsType("10");
            notificationsList.add(notification);
        }*//*
        //creating Calendar instance
        Calendar calendar = Calendar.getInstance();
        //Returns current time in millis
        long timeMilli = calendar.getTimeInMillis();
        for (int i = 0; i < notificationsList.size(); i++) {
            long oneDayLater = Long.parseLong(notificationsList.get(i).getCreatedDate()) + TimeUnit.HOURS.toMillis(24l);
            int result1 = Long.compare(timeMilli, oneDayLater);
            if (result1 > 0) {
                dbHelper.deleteTitle(notificationsList.get(i).getKeyId());
            }

        }
        notificationsList = dbHelper.retrieveNotificationsRecord();
        if (notificationsList.size() == 0)
            llNotification.setVisibility(View.GONE);
        else
            setNotificationRecyclerView(notificationsList);
    }*/

    private void setNotificationRecyclerView(ArrayList<Notification> notificationsList) {
        NotificationsRecyclerViewAdapter mAdapter =
                new NotificationsRecyclerViewAdapter(notificationsList, mContext, mMediatorCallback);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(notificationList
                .getContext(),
                ((LinearLayoutManager) mLayoutManager).getOrientation());
        notificationList.addItemDecoration(mDividerItemDecoration);
        notificationList.setLayoutManager(mLayoutManager);
        notificationList.setItemAnimator(new DefaultItemAnimator());
        notificationList.setAdapter(mAdapter);
    }

    private void setupView(View parentView) {
        personInfo = parentView.findViewById(R.id.personInfoConstraintLayout);
        personInfo.setVisibility(View.VISIBLE);
        tvAlert = parentView.findViewById(R.id.tv_alert);
        rvGrid = parentView.findViewById(R.id.vp_container);
        menuListScrollView = parentView.findViewById(R.id.menuListScrollView);
        menuButton = parentView.findViewById(R.id.btnMenu);
        myVital = parentView.findViewById(R.id.recyclerView_myvital);
        myVitalExpandBtn = parentView.findViewById(R.id.btn_myvital_expand);
        appointmentExpandBtn = parentView.findViewById(R.id.btn_appointment_expand);
        appointmentList = parentView.findViewById(R.id.recyclerView_coming_appointment);
        //notificationList = parentView.findViewById(R.id.recyclerView_notification_home);
        notificationExpandBtn = parentView.findViewById(R.id.btn_notification_expand);
        //   updatesList = parentView.findViewById(R.id.recyclerView_updates_home);
        updatesExpandBtn = parentView.findViewById(R.id.btn_updates_expand);
        chatsList = parentView.findViewById(R.id.recyclerView_chats_home);
        chatsExpandBtn = parentView.findViewById(R.id.btn_chats_expand);
        rvGrid.setVisibility(View.GONE);

        recentVitalConstraintLayout = parentView.findViewById(R.id.myVitalLayout);
        appointmentConstraintLayout = parentView.findViewById(R.id.appointmentLayout);
        updateConstraintLayout = parentView.findViewById(R.id.updatesLayout);
        chatConstraintLayout = parentView.findViewById(R.id.chatsLayout);
        notificationConstraintLayout = parentView.findViewById(R.id.notificationLayout);

        recentVitalConstraintLayout.setVisibility(View.INVISIBLE);
        appointmentConstraintLayout.setVisibility(View.INVISIBLE);
        updateConstraintLayout.setVisibility(View.INVISIBLE);
        chatConstraintLayout.setVisibility(View.INVISIBLE);
        notificationConstraintLayout.setVisibility(View.INVISIBLE);

        LinearLayout sliderDotspanel = parentView.findViewById(R.id.slider_dots);
        dots = new ImageView[3];
        dots[0] = new ImageView(mContext);
        dots[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFlipper.setDisplayedChild(0);
            }
        });
        dots[1] = new ImageView(mContext);
        dots[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFlipper.setDisplayedChild(1);
            }
        });
        dots[2] = new ImageView(mContext);
        dots[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFlipper.setDisplayedChild(2);
            }
        });

        for (int i = 0; i < dotscount; i++) {
            dots[i].setImageDrawable(mContext.getDrawable(R.drawable.non_active_dot));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            sliderDotspanel.addView(dots[i], params);
        }
        viewFlipper = parentView.findViewById(R.id.viewFlipper);
        viewFlipper.setOnTouchListener(new android.view.View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onTouchEvent(event);
                viewFlipper.performClick();
                return true;
            }
        });
        viewFlipper.addOnLayoutChangeListener(onLayoutChangeListener_viewFlipper);
        tvPatientId = parentView.findViewById(R.id.tv_patientid_main);
        tvFullName = parentView.findViewById(R.id.tv_name_main);
        tvAge = parentView.findViewById(R.id.tvAge_main);
        tvBloodGroup = parentView.findViewById(R.id.tvBlood_main);
        tvUserHeight = parentView.findViewById(R.id.tvHeight_main);
        tvUserWeight = parentView.findViewById(R.id.tvWeight_main);
        ivUserProfile = parentView.findViewById(R.id.imgProfile_main);
        tvGender = parentView.findViewById(R.id.tvGender_info);
        tvMobile = parentView.findViewById(R.id.tvPhone_info);
        tvNameInfo = parentView.findViewById(R.id.tv_name_info);
        tvNationality = parentView.findViewById(R.id.tvNationality_info);
        tvUserAddress = parentView.findViewById(R.id.tvAddress_info);
        tvDependentsTitle = parentView.findViewById(R.id.tvDependents);
        tvDependentsTitle.setText(getResources().getString(R.string.title_dependents) + ": ");
        tvFirstDependent = parentView.findViewById(R.id.tvFirstDependent);
        /*tvFirstDependent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mToolbarCallback.changeSideMenuToolBarVisibility(View.GONE);
                mMediatorCallback.changeFragmentTo(DemographicsFragment.newInstance(responseHolder.getmResult().getmHome().getInstitutesArrayList()), DemographicsFragment.class.getSimpleName());
            }
        });*/
        tvSecondDependent = parentView.findViewById(R.id.tvSecondDependent);
        tvSecondDependent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mToolbarCallback.changeSideMenuToolBarVisibility(View.GONE);
                mMediatorCallback.changeFragmentTo(DemographicsFragment.newInstance(responseHolder.getmResult().getmHome().getInstitutesArrayList(),getPageTitle(responseHolder.getmResult().getmHome().getmMainMenus(),283)), DemographicsFragment.class.getSimpleName());
            }
        });
        ivFirstArrow = parentView.findViewById(R.id.ivFirstDepArrow);
        ivFirstArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mToolbarCallback.changeSideMenuToolBarVisibility(View.GONE);
                mMediatorCallback.changeFragmentTo(DemographicsFragment.newInstance(responseHolder.getmResult().getmHome().getInstitutesArrayList(),getPageTitle(responseHolder.getmResult().getmHome().getmMainMenus(),283)), DemographicsFragment.class.getSimpleName());
            }
        });
        ivSecondArrow = parentView.findViewById(R.id.ivSecondDepArrow);
        ivSecondArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mToolbarCallback.changeSideMenuToolBarVisibility(View.GONE);
                mMediatorCallback.changeFragmentTo(DemographicsFragment.newInstance(responseHolder.getmResult().getmHome().getInstitutesArrayList(),getPageTitle(responseHolder.getmResult().getmHome().getmMainMenus(),283)), DemographicsFragment.class.getSimpleName());
            }
        });
        llMyVitalSigns = parentView.findViewById(R.id.linearLayout4);
        llAppointments = parentView.findViewById(R.id.linearLayout44);
        llNotification = parentView.findViewById(R.id.linearLayout42);
        llUnReadMessages = parentView.findViewById(R.id.linearLayout335);
        dependentDivider = parentView.findViewById(R.id.divider);
    }

    public void setupMyVitalList(ArrayList<ApiHomeHolder.ApiRecentVitals> myVitals) {
        recentVitalConstraintLayout.setVisibility(View.VISIBLE);
        recentVitalConstraintLayout.setAnimation(GlobalMethodsKotlin.Companion.setAnimation(mContext,R.anim.fade_in));
        MyVitalListAdapter myVitalListAdapter = new MyVitalListAdapter(myVitals, mContext);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        //DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(myVital.getContext(),layoutManager.getOrientation());
        // myVital.addItemDecoration(mDividerItemDecoration);
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(mContext, R.anim.delay_slide_down);
        myVital.setLayoutAnimation(animation);
        myVital.setLayoutManager(layoutManager);
        myVital.setItemAnimator(new DefaultItemAnimator());
        myVital.setAdapter(myVitalListAdapter);

    }

    public void setupAppointmentList(ArrayList<ApiHomeHolder.ApiAppointments> appointments) {
        appointmentConstraintLayout.setVisibility(View.VISIBLE);
        appointmentConstraintLayout.setAnimation(GlobalMethodsKotlin.Companion.setAnimation(mContext,R.anim.fade_in));
        ComingAppointmentListAdapter comingAppointmentListAdapter = new ComingAppointmentListAdapter(appointments, mContext);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(appointmentList.getContext(), layoutManager.getOrientation());
        appointmentList.addItemDecoration(mDividerItemDecoration);
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(mContext, R.anim.delay_slide_down);
        appointmentList.setLayoutAnimation(animation);
        appointmentList.setLayoutManager(layoutManager);
        appointmentList.setItemAnimator(new DefaultItemAnimator());
        appointmentList.setAdapter(comingAppointmentListAdapter);

    }

    public void setupNotificationList(ArrayList<String> notifications) {
        notificationConstraintLayout.setVisibility(View.VISIBLE);
        notificationConstraintLayout.setAnimation(GlobalMethodsKotlin.Companion.setAnimation(mContext,R.anim.fade_in));
        NotificationHomeAdapter comingAppointmentListAdapter = new NotificationHomeAdapter(notifications, mContext);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(notificationList.getContext(), layoutManager.getOrientation());
        notificationList.addItemDecoration(mDividerItemDecoration);
        notificationList.setLayoutManager(layoutManager);
        notificationList.setItemAnimator(new DefaultItemAnimator());
        notificationList.setAdapter(comingAppointmentListAdapter);

    }

    public void setupUpdatesList(ArrayList<String> updates) {
        updateConstraintLayout.setVisibility(View.VISIBLE);
        updateConstraintLayout.setAnimation(GlobalMethodsKotlin.Companion.setAnimation(mContext,R.anim.fade_in));
     /*

     UpdatesListAdapter comingAppointmentListAdapter = new UpdatesListAdapter(updates, mContext);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(updatesList.getContext(), layoutManager.getOrientation());
        updatesList.addItemDecoration(mDividerItemDecoration);
        updatesList.setLayoutManager(layoutManager);
        updatesList.setItemAnimator(new DefaultItemAnimator());
        updatesList.setAdapter(comingAppointmentListAdapter);*/

    }

    public void setupChatsList(ArrayList<ApiHomeHolder.ApiChatMessages> chatsModels) {
        chatConstraintLayout.setVisibility(View.VISIBLE);
        chatConstraintLayout.setAnimation(GlobalMethodsKotlin.Companion.setAnimation(mContext,R.anim.fade_in));
        MessageChatsAdapter messageChatsAdapter = new MessageChatsAdapter(mMediatorCallback, chatsModels, mContext);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(chatsList.getContext(), layoutManager.getOrientation());
        chatsList.addItemDecoration(mDividerItemDecoration);
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(mContext, R.anim.delay_slide_down);
        chatsList.setLayoutAnimation(animation);
        chatsList.setLayoutManager(layoutManager);
        chatsList.setItemAnimator(new DefaultItemAnimator());
        chatsList.setAdapter(messageChatsAdapter);

    }

    private void getDemographicResponse() {
        mProgressDialog.showDialog();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, API_URL_GET_DEMOGRAPHICS_INFO, getJSONRequestCivilIDParam()
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt(API_RESPONSE_CODE) == 0) {
                        Gson gson = new Gson();
                        responseHolder = gson.fromJson(response.toString(), ApiHomeHolder.class);

                        if (mContext != null) {
                            mAdapter.updateList(responseHolder.getmResult().getmHome().getmMainMenus());
                            setupDempgraphicsData(responseHolder.getmResult().getmHome().getmDemographics());
                            setupRecentVitalsData(responseHolder.getmResult().getmHome().getmRecentVitals());
                            setupDependentsData(responseHolder.getmResult().getmHome().getmDependents());
                            setupChatMessages(responseHolder.getmResult().getmHome().getmChatMessages());
                            setupAppointments(responseHolder.getmResult().getmHome().getmAppointments());

                            if (responseHolder.getmResult().getmHome().getmRecentVitals().size() >0)
                                isVitalShow = true;
                            if (responseHolder.getmResult().getmHome().getmAppointments().size() >0)
                                isAppointmentShow = true;
                            if (responseHolder.getmResult().getmHome().getmChatMessages().size() >0)
                                isChatShow = true;

                        }

                    } else
                        GlobalMethodsKotlin.Companion.showAlertErrorDialog(mContext);
                        //Toast.makeText(mContext, response.getString(API_RESPONSE_MESSAGE), Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    GlobalMethodsKotlin.Companion.showAlertErrorDialog(mContext);
                    //e.printStackTrace();
                }

                mProgressDialog.dismissDialog();
                swipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mContext != null && isAdded()) {
                    mProgressDialog.dismissDialog();
                    Log.d("resp-home", error.toString());
                    error.printStackTrace();
                    GlobalMethodsKotlin.Companion.showAlertErrorDialog(mContext);
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



    private JSONObject getJSONRequestCivilIDParam() {
        Map<String, Object> params = new HashMap<>();
        params.put("civilId", mMediatorCallback.getCurrentUser().getCivilId());
        return new JSONObject(params);
    }

    private void setupDempgraphicsData(ApiHomeHolder.ApiDemographics apiDemographicItem) {

        String nameShort = "";
        String fullName = "";
        if (getStoredLanguage().equals(LANGUAGE_ARABIC)){
            nameShort = "{ " + apiDemographicItem.getFirstNameNls() + " " + apiDemographicItem.getSecondNameNls() + " " + apiDemographicItem.getSixthNameNls() + " }";
            fullName = apiDemographicItem.getFirstNameNls() + " " + apiDemographicItem.getSecondNameNls() + " " + apiDemographicItem.getThirdNameNls() + " " + apiDemographicItem.getFourthNameNls() + " " + apiDemographicItem.getFifthNameNls()+ " " + apiDemographicItem.getSixthNameNls();
        }else {
            nameShort = "{ " + apiDemographicItem.getFirstName() + " " + apiDemographicItem.getSecondName() + " " + apiDemographicItem.getSixthName() + " }";
            fullName = apiDemographicItem.getFirstName() + " " + apiDemographicItem.getSecondName() + " " + apiDemographicItem.getThirdName() + " " + apiDemographicItem.getFourthName() + " " + apiDemographicItem.getFifthName()+ " " + apiDemographicItem.getSixthName();
        }
        tvFullName.setText(nameShort);
        tvPatientId.setText(String.valueOf(apiDemographicItem.getCivilId()));
        tvAge.setText(apiDemographicItem.getAge());
        tvBloodGroup.setText(apiDemographicItem.getBloodGroup());
        Glide.with(mContext).load(getPersonPhoto(mContext, apiDemographicItem.getImage())).into(ivUserProfile);
        tvNameInfo.setText(fullName);
        tvMobile.setText(String.valueOf(apiDemographicItem.getMobile()));
        tvGender.setText(apiDemographicItem.getGender());
        tvNationality.setText(apiDemographicItem.getNationality());
        tvUserAddress.setText(apiDemographicItem.getBirthDown());
        switch (apiDemographicItem.getGender()){
            case "Male":
                if (isArabic)
                    tvGender.setText("ذكر");
                else
                    tvGender.setText("Male");
                break;
            case "Female":
                if (isArabic)
                    tvGender.setText("أنثى");
                else
                    tvGender.setText("Female");
                break;
            default:
                tvGender.setText("");
                break;
        }
    }

    private void setupRecentVitalsData(ArrayList<ApiHomeHolder.ApiRecentVitals> apiRecentVitals) {
        if (apiRecentVitals == null || apiRecentVitals.size() == 0)
            llMyVitalSigns.setVisibility(View.GONE);
        else {
            recentVitalsArrayList.clear();
            for (int i = 0; i < apiRecentVitals.size(); i++) {
                ApiHomeHolder.ApiRecentVitals vitalSign = apiRecentVitals.get(i);
                if (vitalSign.getName().equals("Body height"))
                    tvUserHeight.setText(vitalSign.getValue() + " " + vitalSign.getUnit());
                else if (vitalSign.getName().equals("Weight Measured"))
                    tvUserWeight.setText(vitalSign.getValue() + " " + vitalSign.getUnit());
                else if (!vitalSign.getName().equals("G6PD"))
                    recentVitalsArrayList.add(vitalSign);
            }
            /* setup my vital list */
            setupMyVitalList(recentVitalsArrayList);
        }
    }

    private void setupChatMessages(ArrayList<ApiHomeHolder.ApiChatMessages> apiChatMessages) {
        /* setup chats list */
        if (apiChatMessages == null || apiChatMessages.size() == 0)
            llUnReadMessages.setVisibility(View.GONE);
        else
            setupChatsList(apiChatMessages);
    }

    private void setupAppointments(ArrayList<ApiHomeHolder.ApiAppointments> apiAppointments) {
        /* setup appointment list */
        if (apiAppointments == null || apiAppointments.size() == 0)
            llAppointments.setVisibility(View.GONE);
        else
            setupAppointmentList(apiAppointments);
    }

    private void setupDependentsData(final ArrayList<ApiHomeHolder.ApiDependents> apiDependents) {
        if (apiDependents != null && apiDependents.size() != 0) {
            if (apiDependents.get(0) != null)
                if (isArabic){
                    tvFirstDependent.setText(apiDependents.get(0).getDependentNameNls() + "\n" + apiDependents.get(0).getRelationType() + " | " + apiDependents.get(0).getDependentCivilId());
                }else {
                    tvFirstDependent.setText(apiDependents.get(0).getDependentName() + "\n" + apiDependents.get(0).getRelationType() + " | " + apiDependents.get(0).getDependentCivilId());
                }

            if (apiDependents.size() > 1) {
                if (isArabic){
                    tvSecondDependent.setText(apiDependents.get(1).getDependentNameNls() + "\n" + apiDependents.get(1).getRelationType() + " | " + apiDependents.get(1).getDependentCivilId());
                }else {
                    tvSecondDependent.setText(apiDependents.get(1).getDependentName() + "\n" + apiDependents.get(1).getRelationType() + " | " + apiDependents.get(1).getDependentCivilId());
                }
            } else {
                tvSecondDependent.setText("");
                ivSecondArrow.setVisibility(View.GONE);
            }
            tvFirstDependent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   updateCurrentUser(String.valueOf(apiDependents.get(0).getDependentCivilId()));
                }
            });
            tvSecondDependent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateCurrentUser(String.valueOf(apiDependents.get(1).getDependentCivilId()));
                }
            });
        } else {
            tvFirstDependent.setText("");
            tvSecondDependent.setText("");
            dependentDivider.setVisibility(View.GONE);
            ivFirstArrow.setVisibility(View.GONE);
            ivSecondArrow.setVisibility(View.GONE);
        }
    }

    private Bitmap getPersonPhoto(Context context, String image) {
        if (image == null || TextUtils.isEmpty(image)) {
            return BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.avatar);
        } else {
            //decode base64 string to image
            byte[] imageBytes = Base64.decode(image, Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            return decodedImage;
        }

    }

    private void setRecyclerViewGrid() {
        mAdapter = new PaginationRecyclerViewAdapter(HomeFragment.this, mContext);
        rvGrid.setLayoutManager(new GridLayoutManager(mContext, NUMBER_OF_COLUMNS));
        rvGrid.setItemAnimator(new DefaultItemAnimator());
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(mContext, R.anim.delay_slide_down);
        rvGrid.setLayoutAnimation(animation);
        rvGrid.setHasFixedSize(true);
        rvGrid.setAdapter(mAdapter);
    }

    private void displayAlert(String msg) {
        menuListScrollView.setVisibility(View.GONE);
        rvGrid.setVisibility(View.GONE);
        personInfo.setVisibility(View.GONE);
        String title = mContext.getResources().getString(R.string.alert_error_title);
        String body = mContext.getResources().getString(R.string.alert_no_connection_body_1) + "\n" + mContext.getResources().getString(R.string.alert_connection_body2);
        GlobalMethodsKotlin.Companion.showAlertDialog(mContext,title,body,mContext.getResources().getString(R.string.ok),R.drawable.ic_error);
    }

    @Override
    public <T> void onMyListItemClicked(T dataToPass, String dataTitle) {
        if (dataToPass instanceof DemographicsFragment) {
            mToolbarCallback.changeSideMenuToolBarVisibility(View.GONE);
            mMediatorCallback.changeFragmentTo(DemographicsFragment.newInstance(responseHolder.getmResult().getmHome().getInstitutesArrayList(),getPageTitle(responseHolder.getmResult().getmHome().getmMainMenus(),283)), dataTitle);
        } else if (dataToPass instanceof BodyMeasurementsFragment && responseHolder.getmResult().getmHome().getmRecentVitals() != null) {
            mToolbarCallback.changeSideMenuToolBarVisibility(View.GONE);
            mMediatorCallback.changeFragmentTo(BodyMeasurementsFragment.newInstance(responseHolder.getmResult().getmHome().getmRecentVitals(),getPageTitle(responseHolder.getmResult().getmHome().getmMainMenus(),284)), dataTitle);
        }else if (dataToPass instanceof AppointmentsListFragment){
            mToolbarCallback.changeSideMenuToolBarVisibility(View.GONE);
            mMediatorCallback.changeFragmentTo(AppointmentsListFragment.newInstance(responseHolder.getmResult().getmHome().getInstitutesArrayList()), dataTitle);
        }else {
            mToolbarCallback.changeSideMenuToolBarVisibility(View.GONE);
            mMediatorCallback.changeFragmentTo((Fragment) dataToPass, dataTitle);
        }
    }

    @Override
    public <T> void onMyListItemClicked(T dataToPass, String dataTitle, int position) {

    }
    private String getPageTitle(ArrayList<ApiHomeHolder.ApiMainMenus> mainMenus,int menuId){
        String result = "";
        for (ApiHomeHolder.ApiMainMenus menus : mainMenus){
            if (menus.getMenuId() == menuId){
                if (GlobalMethods.getStoredLanguage(mContext).equals(LANGUAGE_ARABIC))
                    result = menus.getMenuNameNls();
                else
                    result = menus.getMenuName();
            }
        }
        return result;
    }


    public void expandCollapseBtn(View view) {
        Bitmap imgArrow = ((BitmapDrawable) getResources().getDrawable(R.drawable.ic_arrow_down)).getBitmap();
        switch (view.getId()) {
            case R.id.btn_myvital_expand:

                Bitmap imgBtn = ((BitmapDrawable) myVitalExpandBtn.getDrawable()).getBitmap();

                if (imgBtn == imgArrow) {

                    myVital.setVisibility(View.GONE);
                    if (getStoredLanguage().equals(LANGUAGE_ARABIC)){
                        myVitalExpandBtn.setImageBitmap(flipImage());
                    }else {
                        myVitalExpandBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_right));
                    }

                } else {

                    myVital.setVisibility(View.VISIBLE);
                    myVitalExpandBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
                }
                break;
            case R.id.btn_appointment_expand:
                Bitmap imgBtnApp = ((BitmapDrawable) appointmentExpandBtn.getDrawable()).getBitmap();

                if (imgBtnApp == imgArrow) {

                    appointmentList.setVisibility(View.GONE);
                    if (getStoredLanguage().equals(LANGUAGE_ARABIC)){
                        appointmentExpandBtn.setImageBitmap(flipImage());
                    }else {
                        appointmentExpandBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_right));
                    }
                } else {

                    appointmentList.setVisibility(View.VISIBLE);
                    appointmentExpandBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
                }
                break;
            case R.id.btn_notification_expand:
               /* Bitmap imgBtnNot = ((BitmapDrawable) notificationExpandBtn.getDrawable()).getBitmap();

                if (imgBtnNot == imgArrow) {

                    notificationList.setVisibility(View.GONE);
                    if (getStoredLanguage().equals(LANGUAGE_ARABIC)){
                        notificationExpandBtn.setImageBitmap(flipImage());
                    }else {
                        notificationExpandBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_right));
                    }
                } else {

                    notificationList.setVisibility(View.VISIBLE);
                    notificationExpandBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
                }*/
                break;
            case R.id.btn_updates_expand:
             /*   Bitmap imgBtnUpd = ((BitmapDrawable) updatesExpandBtn.getDrawable()).getBitmap();

                if (imgBtnUpd == imgArrow) {

                    updatesList.setVisibility(View.GONE);
                    if (getStoredLanguage().equals(LANGUAGE_ARABIC)){
                        updatesExpandBtn.setImageBitmap(flipImage());
                    }else {
                        updatesExpandBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_right));
                    }
                } else {

                    updatesList.setVisibility(View.GONE);
                    updatesExpandBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
                }*/
                break;
            case R.id.btn_chats_expand:
                Bitmap imgBtnChat = ((BitmapDrawable) chatsExpandBtn.getDrawable()).getBitmap();

                if (imgBtnChat == imgArrow) {

                    chatsList.setVisibility(View.GONE);
                    if (getStoredLanguage().equals(LANGUAGE_ARABIC)){
                        chatsExpandBtn.setImageBitmap(flipImage());
                    }else {
                        chatsExpandBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_right));
                    }
                } else {

                    chatsList.setVisibility(View.VISIBLE);
                    chatsExpandBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
                }
                break;

        }
    }
    private Bitmap flipImage() {
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.ic_arrow_right);
// create new matrix for transformation
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);

        Bitmap flipped_bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        return flipped_bitmap;

    }

    @Override
    public void onClick(View view) {

        expandCollapseBtn(view);
    }

    // Method to handle touch event like left to right swap and right to left swap
    public boolean onTouchEvent(MotionEvent touchevent) {
        switch (touchevent.getAction()) {
            // when user first touches the screen to swap
            case MotionEvent.ACTION_DOWN: {
                lastX = touchevent.getX();
                break;
            }
            case MotionEvent.ACTION_UP: {
                float currentX = touchevent.getX();

                // if left to right swipe on screen
                if (lastX < currentX) {
                    // If no more View/Child to flip
                   /* if (viewFlipper.getDisplayedChild() == 0)
                        break;*/

                    // set the required Animation type to ViewFlipper
                    // The Next screen will come in form Left and current Screen will go OUT from Right
                    viewFlipper.setInAnimation(mContext, android.R.anim.fade_in);
                    viewFlipper.setOutAnimation(mContext, android.R.anim.fade_out);
                    // Show the next Screen
                    viewFlipper.showPrevious();
                }

                // if right to left swipe on screen
                if (lastX > currentX) {
                   /* if (viewFlipper.getDisplayedChild() == 1)
                        break;*/
                    // set the required Animation type to ViewFlipper
                    // The Next screen will come in form Right and current Screen will go OUT from Left
                    viewFlipper.setInAnimation(mContext, android.R.anim.fade_in);
                    viewFlipper.setOutAnimation(mContext, android.R.anim.fade_out);
                    // Show The Previous Screen
                    viewFlipper.showNext();
                }
                break;
            }
        }
        viewFlipper.performClick();
        return false;
    }

    View.OnLayoutChangeListener onLayoutChangeListener_viewFlipper = new View.OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            if (dots != null) {
                if (viewFlipper.getDisplayedChild() == 0) {
                    dots[0].setImageDrawable(mContext.getDrawable(R.drawable.active_dot));
                    dots[1].setImageDrawable(mContext.getDrawable(R.drawable.non_active_dot));
                    dots[2].setImageDrawable(mContext.getDrawable(R.drawable.non_active_dot));
                } else if (viewFlipper.getDisplayedChild() == 1) {
                    dots[1].setImageDrawable(mContext.getDrawable(R.drawable.active_dot));
                    dots[0].setImageDrawable(mContext.getDrawable(R.drawable.non_active_dot));
                    dots[2].setImageDrawable(mContext.getDrawable(R.drawable.non_active_dot));
                } else if (viewFlipper.getDisplayedChild() == 2) {
                    dots[2].setImageDrawable(mContext.getDrawable(R.drawable.active_dot));
                    dots[0].setImageDrawable(mContext.getDrawable(R.drawable.non_active_dot));
                    dots[1].setImageDrawable(mContext.getDrawable(R.drawable.non_active_dot));
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewFlipper.removeOnLayoutChangeListener(onLayoutChangeListener_viewFlipper);
    }
    private String getStoredLanguage() {
        SharedPreferences sharedPref = mContext.getSharedPreferences(LANGUAGE_PREFS, Context.MODE_PRIVATE);
        return sharedPref.getString(LANGUAGE_SELECTED, LANGUAGE_ARABIC);
    }

    private void updateCurrentUser(String civilId) {

        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra(DEPENDENT_CIVILID, civilId);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        getDemographicResponse();
    }
}