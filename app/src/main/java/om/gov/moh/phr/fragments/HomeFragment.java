package om.gov.moh.phr.fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
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
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import om.gov.moh.phr.R;
import om.gov.moh.phr.activities.LoginActivity;
import om.gov.moh.phr.activities.MainActivity;
import om.gov.moh.phr.adapters.ComingAppointmentListAdapter;
import om.gov.moh.phr.adapters.MessageChatsAdapter;
import om.gov.moh.phr.adapters.MyVitalListAdapter;
import om.gov.moh.phr.adapters.PaginationRecyclerViewAdapter;
import om.gov.moh.phr.adapters.RefferalsListRecyclerViewAdapter;
import om.gov.moh.phr.apimodels.ApiAppointmentsListHolder;
import om.gov.moh.phr.apimodels.ApiHomeHolder;
import om.gov.moh.phr.apimodels.Notification;
import om.gov.moh.phr.interfaces.AdapterToFragmentConnectorInterface;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.AppCurrentUser;
import om.gov.moh.phr.models.DBHelper;
import om.gov.moh.phr.models.DummyVitalSigns;
import om.gov.moh.phr.models.GlobalMethods;
import om.gov.moh.phr.models.GlobalMethodsKotlin;
import om.gov.moh.phr.models.MyProgressDialog;


import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;
import static om.gov.moh.phr.models.MyConstants.Chat_Enable;
import static om.gov.moh.phr.models.MyConstants.Clinical_Notes;
import static om.gov.moh.phr.models.MyConstants.IS_SCROLL_LIST;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_ARABIC;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_PREFS;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_SELECTED;
import static om.gov.moh.phr.models.MyConstants.PREFS_API_GET_TOKEN;
import static om.gov.moh.phr.models.MyConstants.PREFS_API_REGISTER_DEVICE;
import static om.gov.moh.phr.models.MyConstants.PREFS_CURRENT_USER;
import static om.gov.moh.phr.models.MyConstants.appointment_Enable;
import static om.gov.moh.phr.models.MyConstants.user_age;

public class HomeFragment extends Fragment implements AdapterToFragmentConnectorInterface, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String API_URL_GET_DEMOGRAPHICS_INFO = API_NEHR_URL + "demographics/phrHome";

    private static final int NUMBER_OF_COLUMNS = 1;
    private Context mContext;
    private RequestQueue mQueue;
    private TextView tvPatientId, tvFullName, tvAge, tvBloodGroup, tvUserHeight, tvUserWeight, tvNameInfo, tvUserAddress, tvMobile, tvGender, tvNationality, tvDependentsTitle, tvFirstDependent, tvSecondDependent, tvNoOfChatNotification, tvNotAvailableDependents;
    private ImageView ivUserProfile, ivFirstArrow, ivSecondArrow;
    private MyProgressDialog mProgressDialog;
    private RecyclerView rvGrid;
    private MediatorInterface mMediatorCallback;
    private ToolbarControllerInterface mToolbarCallback;
    private PaginationRecyclerViewAdapter mAdapter;
    private int dotscount = 3;
    private ImageView[] dots;
    // private PagerCardMainAdapter pagerCardMainAdapter;
    private NestedScrollView menuListScrollView;
    private ImageButton menuButton, myVitalExpandBtn, appointmentExpandBtn, notificationExpandBtn, updatesExpandBtn, chatsExpandBtn, referralsExpandBtn;
    private RecyclerView myVital, appointmentList, notificationList/*, updatesList*/, chatsList, rvReferrals;
    private ViewFlipper viewFlipper;
    private float lastX;
    private ApiHomeHolder responseHolder;
    private ArrayList<ApiHomeHolder.ApiRecentVitals> recentVitalsArrayList = new ArrayList<>();
    private DBHelper dbHelper;
    private LinearLayout llMyVitalSigns, llAppointments, llNotification, llUnReadMessages, llReferrals;
    private View dependentDivider, parentView;
    private boolean isArabic = false;
    private ConstraintLayout personInfo, recentVitalConstraintLayout, appointmentConstraintLayout, notificationConstraintLayout, chatConstraintLayout, updateConstraintLayout, referralsLayout;
    private static final String DEPENDENT_CIVILID = "DependentCivilID";
    private SwipeRefreshLayout swipeRefreshLayout;
    private LayoutAnimationController animation;
    private Boolean isVitalShow = false, isAppointmentShow = false, isChatShow = false, isUpdateShow = false, isNotificationShow = false;
    private DataUpdateReceiver dataUpdateReceiver;
    private MessageChatsAdapter messageChatsAdapter;
    private RefferalsListRecyclerViewAdapter mRefferalAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        mMediatorCallback = (MediatorInterface) context;
        mToolbarCallback = (ToolbarControllerInterface) context;
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
        if (parentView == null) {
            // Inflate the layout for this fragment
            parentView = inflater.inflate(R.layout.fragment_home, container, false);
            isArabic = getStoredLanguage().equals(LANGUAGE_ARABIC);
            animation = AnimationUtils.loadLayoutAnimation(mContext, R.anim.delay_slide_down);
           // showBetaVersionMsg();
            //pageView = pageView.findViewById(R.id.view1);
            mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
            mProgressDialog = new MyProgressDialog(mContext);
            swipeRefreshLayout = parentView.findViewById(R.id.swipeRefreshLayoutHome);
            swipeRefreshLayout.setOnRefreshListener(this);
            setupView(parentView);

            setupRefferalsRecyclerView(rvReferrals);
            if (mMediatorCallback.isConnected()) {
                setRecyclerViewGrid();
                getDemographicResponse();
            } else {
                displayAlert();
            }
            menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (IS_SCROLL_LIST) {
                        disableScrollingList();
                    } else {
                        enableScrollingList();
                    }
                }
            });

            myVitalExpandBtn.setOnClickListener(this);
            appointmentExpandBtn.setOnClickListener(this);
            notificationExpandBtn.setOnClickListener(this);
            updatesExpandBtn.setOnClickListener(this);
            chatsExpandBtn.setOnClickListener(this);
            referralsExpandBtn.setOnClickListener(this);
            llMyVitalSigns.setOnClickListener(this);
            llAppointments.setOnClickListener(this);
            llUnReadMessages.setOnClickListener(this);
            llReferrals.setOnClickListener(this);
        } else {
            if (parentView.getParent() != null)
                ((ViewGroup) parentView.getParent()).removeView(parentView);
            checkNotificationsCounter();
        }
        return parentView;
    }

    private void showBetaVersionMsg() {
        SharedPreferences pref = mContext.getSharedPreferences("betaValidation", 0);
        Boolean isBeta = pref.getBoolean("beta", true);
        if (isBeta) {
            String title = mContext.getResources().getString(R.string.betaTitle);
            String msg = mContext.getResources().getString(R.string.betaMsg);
            String btnTitle = mContext.getResources().getString(R.string.ok);
            GlobalMethodsKotlin.Companion.showAlertDialog(mContext, title, msg, btnTitle, R.drawable.phr_logo);

            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("beta", false);
            editor.apply();

        }

    }

    private void getNotificationList() {
        dbHelper = new DBHelper(mContext);
        ArrayList<Notification> notificationsList = new ArrayList<>();
        notificationsList = dbHelper.retrieveNotificationsRecord();
      /*  if (notificationsList.size() == 0) {
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
            */
    }

    private void setNotificationRecyclerView(ArrayList<Notification> notificationsList) {
        //  NotificationsRecyclerViewAdapter mAdapter = new NotificationsRecyclerViewAdapter(notificationsList, mContext, mMediatorCallback);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(notificationList
                .getContext(),
                ((LinearLayoutManager) mLayoutManager).getOrientation());
        notificationList.addItemDecoration(mDividerItemDecoration);
        notificationList.setLayoutManager(mLayoutManager);
        notificationList.setItemAnimator(new DefaultItemAnimator());
        // notificationList.setAdapter(mAdapter);
    }

    private void setupView(View parentView) {
        personInfo = parentView.findViewById(R.id.personInfoConstraintLayout);
        personInfo.setVisibility(View.VISIBLE);
        rvGrid = parentView.findViewById(R.id.vp_container);
        menuListScrollView = parentView.findViewById(R.id.menuListScrollView);
        menuButton = parentView.findViewById(R.id.btnMenu);
        myVital = parentView.findViewById(R.id.recyclerView_myvital);
        myVitalExpandBtn = parentView.findViewById(R.id.btn_myvital_expand);
        appointmentExpandBtn = parentView.findViewById(R.id.btn_appointment_expand);
        referralsExpandBtn = parentView.findViewById(R.id.btn_referrals_expand);
        appointmentList = parentView.findViewById(R.id.recyclerView_coming_appointment);

        rvReferrals = parentView.findViewById(R.id.rv_referrals);

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
        referralsLayout = parentView.findViewById(R.id.referralsLayout);

        recentVitalConstraintLayout.setVisibility(View.GONE);
        appointmentConstraintLayout.setVisibility(View.GONE);
        updateConstraintLayout.setVisibility(View.GONE);
        chatConstraintLayout.setVisibility(View.GONE);
        notificationConstraintLayout.setVisibility(View.GONE);
        referralsLayout.setVisibility(View.GONE);
        viewFlipper = parentView.findViewById(R.id.viewFlipper);
        viewFlipper.setOnTouchListener(new android.view.View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onTouchEvent(event);
                viewFlipper.performClick();
                return true;
            }
        });

        LinearLayout sliderDotspanel = parentView.findViewById(R.id.slider_dots);
        dots = new ImageView[3];
        dots[0] = new ImageView(mContext);
        dots[0].setPadding(5, 5, 5, 5);
        dots[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFlipper.setDisplayedChild(0);
            }
        });
        dots[1] = new ImageView(mContext);
        dots[1].setPadding(5, 5, 5, 5);
        dots[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFlipper.setDisplayedChild(1);
            }
        });
        dots[2] = new ImageView(mContext);
        dots[2].setPadding(5, 5, 5, 5);
        dots[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFlipper.setDisplayedChild(2);
            }
        });
        if (dotscount != 0) {
            for (int i = 0; i < dotscount; i++) {
                dots[i].setImageDrawable(mContext.getDrawable(R.drawable.non_active_dot));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 0);
                sliderDotspanel.addView(dots[i], params);
            }
        }
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
        tvFirstDependent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mToolbarCallback.changeSideMenuToolBarVisibility(View.GONE);
                mMediatorCallback.changeFragmentTo(DemographicsFragment.newInstance(responseHolder.getmResult().getmHome().getInstitutesArrayList(), getPageTitle(responseHolder.getmResult().getmHome().getmMainMenus(), 283)), DemographicsFragment.class.getSimpleName());
            }
        });
        tvSecondDependent = parentView.findViewById(R.id.tvSecondDependent);
        tvSecondDependent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mToolbarCallback.changeSideMenuToolBarVisibility(View.GONE);
                mMediatorCallback.changeFragmentTo(DemographicsFragment.newInstance(responseHolder.getmResult().getmHome().getInstitutesArrayList(), getPageTitle(responseHolder.getmResult().getmHome().getmMainMenus(), 283)), DemographicsFragment.class.getSimpleName());
            }
        });
        ivFirstArrow = parentView.findViewById(R.id.ivFirstDepArrow);
        ivFirstArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mToolbarCallback.changeSideMenuToolBarVisibility(View.GONE);
                mMediatorCallback.changeFragmentTo(DemographicsFragment.newInstance(responseHolder.getmResult().getmHome().getInstitutesArrayList(), getPageTitle(responseHolder.getmResult().getmHome().getmMainMenus(), 283)), DemographicsFragment.class.getSimpleName());
            }
        });
        ivSecondArrow = parentView.findViewById(R.id.ivSecondDepArrow);
        ivSecondArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mToolbarCallback.changeSideMenuToolBarVisibility(View.GONE);
                mMediatorCallback.changeFragmentTo(DemographicsFragment.newInstance(responseHolder.getmResult().getmHome().getInstitutesArrayList(), getPageTitle(responseHolder.getmResult().getmHome().getmMainMenus(), 283)), DemographicsFragment.class.getSimpleName());
            }
        });
        llMyVitalSigns = parentView.findViewById(R.id.linearLayout4);
        llAppointments = parentView.findViewById(R.id.linearLayout44);
        llNotification = parentView.findViewById(R.id.linearLayout42);
        llUnReadMessages = parentView.findViewById(R.id.linearLayout335);
        llReferrals = parentView.findViewById(R.id.llReferrals);
        dependentDivider = parentView.findViewById(R.id.divider);
        tvNoOfChatNotification = parentView.findViewById(R.id.tvNoOfChatNotification);
        tvNotAvailableDependents = parentView.findViewById(R.id.tvNotAvailableDependents);
        if (IS_SCROLL_LIST) {
            menuListScrollView.setVisibility(View.VISIBLE);
            rvGrid.setVisibility(View.GONE);
            menuButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_item));
        } else {
            menuListScrollView.setVisibility(View.GONE);
            rvGrid.setVisibility(View.VISIBLE);
            menuButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_list));
        }
    }

    public void setupMyVitalList(ArrayList<ApiHomeHolder.ApiRecentVitals> myVitals) {
        recentVitalConstraintLayout.setVisibility(View.VISIBLE);
        recentVitalConstraintLayout.setAnimation(GlobalMethodsKotlin.Companion.setAnimation(mContext, R.anim.fade_in));
        MyVitalListAdapter myVitalListAdapter;

        if (myVitals.size() > 0) {
            recentVitalsArrayList.clear();
            for (int i = 0; i < myVitals.size(); i++) {
                ApiHomeHolder.ApiRecentVitals vitalSign = myVitals.get(i);

                if (vitalSign.getName().equals("Body height")) {
                    tvUserHeight.setText(vitalSign.getValue() + " " + vitalSign.getUnit());
                } else if (vitalSign.getName().equals("Weight Measured")) {
                    tvUserWeight.setText(vitalSign.getValue() + " " + vitalSign.getUnit());
                } else if (!vitalSign.getName().equals("G6PD") && !vitalSign.getName().equalsIgnoreCase("blood group"))
                    recentVitalsArrayList.add(vitalSign);
            }
            myVitalListAdapter = new MyVitalListAdapter(recentVitalsArrayList, mContext);

        } else {

            ArrayList<DummyVitalSigns> dummyVitals = new ArrayList<>();
            dummyVitals.add(new DummyVitalSigns("Body temperature", "درجة حرارة الجسم", "---"));
            dummyVitals.add(new DummyVitalSigns("Respiratory rate", "معدل التنفس", "---"));
            dummyVitals.add(new DummyVitalSigns("Oxygen saturation", "تركيز الأكسجين", "---"));
            dummyVitals.add(new DummyVitalSigns("Heart rate", "معدل نبضات القلب", "---"));
            dummyVitals.add(new DummyVitalSigns("Blood pressure", "ضغط الدم", "---"));
            myVitalListAdapter = new MyVitalListAdapter(mContext, dummyVitals, true);
        }

        final LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(mContext, R.anim.delay_slide_down);
        myVital.setLayoutAnimation(animation);
        myVital.setLayoutManager(layoutManager);
        myVital.setItemAnimator(new DefaultItemAnimator());
        myVital.setAdapter(myVitalListAdapter);
        myVital.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                swipeRefreshLayout.setEnabled(layoutManager.findFirstCompletelyVisibleItemPosition() == 0);

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        if (tvUserHeight.getText().toString().equals(""))
            tvUserHeight.setText("--");
        if (tvUserWeight.getText().toString().equals(""))
            tvUserWeight.setText("--");
    }

    public void setupAppointmentList(ArrayList<ApiHomeHolder.ApiAppointments> appointments) {
        appointmentConstraintLayout.setVisibility(View.VISIBLE);
        appointmentConstraintLayout.setAnimation(GlobalMethodsKotlin.Companion.setAnimation(mContext, R.anim.fade_in));
        ComingAppointmentListAdapter comingAppointmentListAdapter = new ComingAppointmentListAdapter(appointments, mContext);
        final LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(appointmentList.getContext(), layoutManager.getOrientation());
        appointmentList.addItemDecoration(mDividerItemDecoration);
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(mContext, R.anim.delay_slide_down);
        appointmentList.setLayoutAnimation(animation);
        appointmentList.setLayoutManager(layoutManager);
        appointmentList.setItemAnimator(new DefaultItemAnimator());
        appointmentList.setAdapter(comingAppointmentListAdapter);
        appointmentList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                swipeRefreshLayout.setEnabled(layoutManager.findFirstCompletelyVisibleItemPosition() == 0);

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    private void updateRefferalsRecyclerView(ArrayList<ApiHomeHolder.Referrals> items) {
        mRefferalAdapter.updateHomeReferralsList(items);
    }

    private void setupRefferalsRecyclerView(RecyclerView recyclerView) {
        mRefferalAdapter = new RefferalsListRecyclerViewAdapter(HomeFragment.this, mContext, false);
        final LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(mContext, R.anim.delay_slide_down);
        recyclerView.setLayoutAnimation(animation);
        recyclerView.addItemDecoration(mDividerItemDecoration);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mRefferalAdapter);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                swipeRefreshLayout.setEnabled(layoutManager.findFirstCompletelyVisibleItemPosition() == 0);

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    public void setupNotificationList(ArrayList<String> notifications) {
        notificationConstraintLayout.setVisibility(View.VISIBLE);
        notificationConstraintLayout.setAnimation(GlobalMethodsKotlin.Companion.setAnimation(mContext, R.anim.fade_in));
        // NotificationHomeAdapter comingAppointmentListAdapter = new NotificationHomeAdapter(notifications, mContext);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(notificationList.getContext(), layoutManager.getOrientation());
        notificationList.addItemDecoration(mDividerItemDecoration);
        notificationList.setLayoutManager(layoutManager);
        notificationList.setItemAnimator(new DefaultItemAnimator());
        //  notificationList.setAdapter(comingAppointmentListAdapter);

    }

    public void setupUpdatesList(ArrayList<String> updates) {
        updateConstraintLayout.setVisibility(View.VISIBLE);
        updateConstraintLayout.setAnimation(GlobalMethodsKotlin.Companion.setAnimation(mContext, R.anim.fade_in));
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
        chatConstraintLayout.setAnimation(GlobalMethodsKotlin.Companion.setAnimation(mContext, R.anim.fade_in));
        messageChatsAdapter = new MessageChatsAdapter(mMediatorCallback, chatsModels, mContext);
        final LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(chatsList.getContext(), layoutManager.getOrientation());
        chatsList.addItemDecoration(mDividerItemDecoration);
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(mContext, R.anim.delay_slide_down);
        chatsList.setLayoutAnimation(animation);
        chatsList.setLayoutManager(layoutManager);
        chatsList.setItemAnimator(new DefaultItemAnimator());
        chatsList.setAdapter(messageChatsAdapter);
        chatsList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                swipeRefreshLayout.setEnabled(layoutManager.findFirstCompletelyVisibleItemPosition() == 0);

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

    }

    private void getDemographicResponse() {
        mProgressDialog.showDialog();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, API_URL_GET_DEMOGRAPHICS_INFO, getJSONRequestCivilIDParam()
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("registerDemo", response.toString());
                if (mContext != null && isAdded()) {
                    try {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {
                            Gson gson = new Gson();
                            responseHolder = gson.fromJson(response.toString(), ApiHomeHolder.class);
                            if (responseHolder.getmResult() != null) {
                                if (mAdapter == null) {
                                    dotscount = 0;
                                    setupView(parentView);
                                    setRecyclerViewGrid();
                                    setupRefferalsRecyclerView(rvReferrals);
                                }
                                if (responseHolder.getmResult().getmHome() != null) {
                                    if(responseHolder.getmResult().getmHome().getClinicalNotesEnableYN() != null){
                                        Clinical_Notes = responseHolder.getmResult().getmHome().getClinicalNotesEnableYN().equalsIgnoreCase("y");
                                    }
                                    if(responseHolder.getmResult().getmHome().getChatEnableYN() != null)
                                        Chat_Enable = responseHolder.getmResult().getmHome().getChatEnableYN();
                                    if(responseHolder.getmResult().getmHome().getAppointmentEnableYN() != null)
                                        appointment_Enable = responseHolder.getmResult().getmHome().getAppointmentEnableYN();
                                    if (responseHolder.getmResult().getmHome().getmMainMenus() != null)
                                        mAdapter.updateList(responseHolder.getmResult().getmHome().getmMainMenus());
                                    if (responseHolder.getmResult().getmHome().getmDemographics() != null)
                                        setupDempgraphicsData(responseHolder.getmResult().getmHome().getmDemographics());

                                    setupMyVitalList(responseHolder.getmResult().getmHome().getmRecentVitals());
                                    //setupRecentVitalsData(responseHolder.getmResult().getmHome().getmRecentVitals());
                                    isVitalShow = true;
                                    /*if (responseHolder.getmResult().getmHome().getmRecentVitals() != null) {
                                        setupRecentVitalsData(responseHolder.getmResult().getmHome().getmRecentVitals());
                                        if (responseHolder.getmResult().getmHome().getmRecentVitals().size() > 0)
                                            isVitalShow = true;
                                    }*/
                                    if (responseHolder.getmResult().getmHome().getmDependents() != null)
                                        setupDependentsData(responseHolder.getmResult().getmHome().getmDependents());
                                    if (responseHolder.getmResult().getmHome().getmChatMessages() != null) {
                                        setupChatMessages(responseHolder.getmResult().getmHome().getmChatMessages());
                                        if (responseHolder.getmResult().getmHome().getmChatMessages().size() > 0)
                                            isChatShow = true;
                                    }
                                    //  if (responseHolder.getmResult().getmHome().getmAppointments() != null) {
                                    setupAppointments(responseHolder.getmResult().getmHome().getmAppointments());
                                    // if (responseHolder.getmResult().getmHome().getmAppointments().size() > 0)
                                    isAppointmentShow = true;
                                    //  }
                                    if (responseHolder.getmResult().getmHome().getReferralsArrayList() != null)
                                        setupReferrals(responseHolder.getmResult().getmHome().getReferralsArrayList());
                          /*      if (llMyVitalSigns.getVisibility() == View.GONE && llAppointments.getVisibility() == View.GONE && llReferrals.getVisibility() == View.GONE && llUnReadMessages.getVisibility() == View.GONE)
                                    disableScrollingList();*/

                                } else
                                    GlobalMethodsKotlin.Companion.showAlertErrorDialog(mContext);
                            }
                        } else if (response.getInt(API_RESPONSE_CODE) == 99) {
                            clearSharedPrefs();
                            Activity thisActivity = getActivity();
                            Intent loginIntent = new Intent(thisActivity, LoginActivity.class);
                            startActivity(loginIntent);
                            thisActivity.finish();
                        }

                    } catch (JSONException e) {
                        GlobalMethodsKotlin.Companion.showAlertErrorDialog(mContext);
                        //e.printStackTrace();
                    }

                    mProgressDialog.dismissDialog();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mContext != null && isAdded()) {
                    mProgressDialog.dismissDialog();
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
        params.put("data", "");
        params.put("source", "");
        return new JSONObject(params);
    }

    private void setupDempgraphicsData(ApiHomeHolder.ApiDemographics apiDemographicItem) {

        String nameShort = "";
        String fullName = "";
        if (getStoredLanguage().equals(LANGUAGE_ARABIC)) {
            nameShort = "{ " + apiDemographicItem.getFirstNameNls() + " " + apiDemographicItem.getSecondNameNls() + " " + apiDemographicItem.getSixthNameNls() + " }";
            fullName = apiDemographicItem.getFirstNameNls() + " " + apiDemographicItem.getSecondNameNls() + " " + apiDemographicItem.getThirdNameNls() + " " + apiDemographicItem.getFourthNameNls() + " " + apiDemographicItem.getFifthNameNls() + " " + apiDemographicItem.getSixthNameNls();
        } else {
            nameShort = "{ " + apiDemographicItem.getFirstName() + " " + apiDemographicItem.getSecondName() + " " + apiDemographicItem.getSixthName() + " }";
            fullName = apiDemographicItem.getFirstName() + " " + apiDemographicItem.getSecondName() + " " + apiDemographicItem.getThirdName() + " " + apiDemographicItem.getFourthName() + " " + apiDemographicItem.getFifthName() + " " + apiDemographicItem.getSixthName();
        }
        tvFullName.setText(nameShort);
        tvPatientId.setText(String.valueOf(apiDemographicItem.getCivilId()));
        user_age = saveAge(apiDemographicItem.getAge());
        tvAge.setText(apiDemographicItem.getAge());
        tvBloodGroup.setText(apiDemographicItem.getBloodGroup());
        Glide.with(mContext).load(getPersonPhoto(mContext, apiDemographicItem.getImage(), apiDemographicItem.getGender())).into(ivUserProfile);
        tvNameInfo.setText(fullName);

        tvMobile.setText(apiDemographicItem.getMobile());
        SharedPreferences pref = mContext.getApplicationContext().getSharedPreferences("UserMobileNo", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("mobileNo", apiDemographicItem.getMobile());
        editor.apply();

        tvGender.setText(apiDemographicItem.getGender());
        if (getStoredLanguage().equals(LANGUAGE_ARABIC))
            tvNationality.setText(apiDemographicItem.getNationalityNls());
        else
            tvNationality.setText(apiDemographicItem.getNationality());
        tvUserAddress.setText(apiDemographicItem.getBirthDown());
        switch (apiDemographicItem.getGender()) {
            case "Male":
                if (isArabic) {
                    //tvGender.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_male, 0);
                    tvGender.setText("ذكر");
                } else {
                    //tvGender.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_male, 0, 0, 0);
                    tvGender.setText("Male");
                }
                break;
            case "Female":
                if (isArabic) {
                    //tvGender.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_female, 0);
                    tvGender.setText("أنثى");
                } else {

                    //tvGender.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_female, 0, 0, 0);
                    tvGender.setText("Female");
                }
                break;
            default:
                tvGender.setText("");
                break;
        }
    }

    private String saveAge(String age) {
        int index = age.indexOf("Y");
        if (index > 0)
            age = age.substring(0, index);
        return age;
    }

    private void setupRecentVitalsData(ArrayList<ApiHomeHolder.ApiRecentVitals> apiRecentVitals) {
        /*if (apiRecentVitals == null || apiRecentVitals.size() == 0)
            llMyVitalSigns.setVisibility(View.GONE);
        else {*/
        //recentVitalsArrayList.clear();
            /*for (int i = 0; i < apiRecentVitals.size(); i++) {
                ApiHomeHolder.ApiRecentVitals vitalSign = apiRecentVitals.get(i);
                if (vitalSign.getName().equals("Body height"))
                    tvUserHeight.setText(vitalSign.getValue() + " " + vitalSign.getUnit());
                else if (vitalSign.getName().equals("Weight Measured"))
                    tvUserWeight.setText(vitalSign.getValue() + " " + vitalSign.getUnit());
                else if (!vitalSign.getName().equals("G6PD") && !vitalSign.getName().equalsIgnoreCase("blood group"))
                    recentVitalsArrayList.add(vitalSign);*/
        //}
        /* setup my vital list */
        setupMyVitalList(apiRecentVitals);
        //}
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
        if (apiAppointments == null || apiAppointments.size() == 0) {
            apiAppointments = new ArrayList<ApiHomeHolder.ApiAppointments>();
            // llAppointments.setVisibility(View.GONE);
            ApiHomeHolder.ApiAppointments defaultAppointmentMsg = new ApiHomeHolder().new ApiAppointments();
            defaultAppointmentMsg.setDescription(getResources().getString(R.string.default_appointment_msg));
            apiAppointments.add(defaultAppointmentMsg);
            setupAppointmentList(apiAppointments);
        } else
            setupAppointmentList(apiAppointments);
    }

    private void setupReferrals(ArrayList<ApiHomeHolder.Referrals> apiReferrals) {
        if (apiReferrals == null || apiReferrals.size() == 0)
            llReferrals.setVisibility(View.GONE);
        else {
            referralsLayout.setVisibility(View.VISIBLE);
            referralsLayout.setAnimation(GlobalMethodsKotlin.Companion.setAnimation(mContext, R.anim.fade_in));
            updateRefferalsRecyclerView(apiReferrals);
        }
    }

    private void setupDependentsData(final ArrayList<ApiHomeHolder.ApiDependents> apiDependents) {
        if (apiDependents != null && apiDependents.size() != 0) {
            if (apiDependents.get(0) != null) {

                if (isArabic) {
                    String relationType;
                    if (apiDependents.get(0).getRelationType().trim().equalsIgnoreCase("son"))
                        relationType = "ابن";
                    else if (apiDependents.get(0).getRelationType().trim().equalsIgnoreCase("daughter"))
                        relationType = "ابنة";
                    else
                        relationType = apiDependents.get(0).getRelationType();
                    tvFirstDependent.setText(apiDependents.get(0).getDependentNameNls() + "\n" + relationType + " | " + apiDependents.get(0).getDependentCivilId());
                } else {
                    tvFirstDependent.setText(apiDependents.get(0).getDependentName() + "\n" + apiDependents.get(0).getRelationType() + " | " + apiDependents.get(0).getDependentCivilId());
                }
            }

            if (apiDependents.size() > 1) {
                if (isArabic) {
                    String relationType;
                    if (apiDependents.get(1).getRelationType().trim().equalsIgnoreCase("son"))
                        relationType = "ابن";
                    else if (apiDependents.get(1).getRelationType().trim().equalsIgnoreCase("daughter"))
                        relationType = "ابنة";
                    else
                        relationType = apiDependents.get(1).getRelationType();
                    tvSecondDependent.setText(apiDependents.get(1).getDependentNameNls() + "\n" + relationType + " | " + apiDependents.get(1).getDependentCivilId());
                } else {
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
            tvDependentsTitle.setVisibility(View.GONE);
            tvNotAvailableDependents.setVisibility(View.VISIBLE);
        }
    }

    private Bitmap getPersonPhoto(Context context, String image, String gender) {
        if (image == null || TextUtils.isEmpty(image)) {
            if ("Female".equals(gender)) {
                return BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.ic_patient_female);
            } else {
                return BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.ic_patient_male);
            }
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
        rvGrid.setAdapter(mAdapter);
    }

    private void displayAlert() {
        menuListScrollView.setVisibility(View.GONE);
        rvGrid.setVisibility(View.GONE);
        personInfo.setVisibility(View.GONE);
        GlobalMethodsKotlin.Companion.showAlertDialog(mContext, getResources().getString(R.string.no_internet_title), getResources().getString(R.string.alert_no_connection), getResources().getString(R.string.ok), R.drawable.ic_error);
    }

    @Override
    public <T> void onMyListItemClicked(T dataToPass, String dataTitle) {
        if (dataToPass instanceof DemographicsFragment) {
            mMediatorCallback.changeFragmentTo(DemographicsFragment.newInstance(responseHolder.getmResult().getmHome().getInstitutesArrayList(), getPageTitle(responseHolder.getmResult().getmHome().getmMainMenus(), 283)), dataTitle);
        } else if (dataToPass instanceof BodyMeasurementsFragment && responseHolder.getmResult().getmHome().getmRecentVitals() != null) {
            mMediatorCallback.changeFragmentTo(BodyMeasurementsFragment.newInstance(responseHolder.getmResult().getmHome().getmRecentVitals(), getPageTitle(responseHolder.getmResult().getmHome().getmMainMenus(), 284)), dataTitle);
        } else if (dataToPass instanceof AppointmentsListFragment) {
            mMediatorCallback.changeFragmentTo(AppointmentsListFragment.newInstance(responseHolder.getmResult().getmHome().getInstitutesArrayList()), dataTitle);
        } else {
            if (dataToPass instanceof OrganDonationFragment && !user_age.equals("--") && Integer.parseInt(user_age) < 18) {
                Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(android.R.id.content), getResources().getString(R.string.allowed_age_msg), Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                        .show();
            } else
                mMediatorCallback.changeFragmentTo((Fragment) dataToPass, dataTitle);
        }
    }

    @Override
    public <T> void onMyListItemClicked(T dataToPass, String dataTitle, int position) {

    }

    private String getPageTitle(ArrayList<ApiHomeHolder.ApiMainMenus> mainMenus, int menuId) {
        String result = "";
        for (ApiHomeHolder.ApiMainMenus menus : mainMenus) {
            if (menus.getMenuId() == menuId) {
                if (GlobalMethods.getStoredLanguage(mContext).equals(LANGUAGE_ARABIC))
                    result = menus.getMenuNameNls();
                else
                    result = menus.getMenuName();
            }
        }
        return result;
    }


    @SuppressLint("NonConstantResourceId")
    public void expandCollapseBtn(View view) {
        Bitmap imgArrow = ((BitmapDrawable) getResources().getDrawable(R.drawable.ic_arrow_down)).getBitmap();
        switch (view.getId()) {
            case R.id.btn_myvital_expand:
            case R.id.linearLayout4:

                Bitmap imgBtn = ((BitmapDrawable) myVitalExpandBtn.getDrawable()).getBitmap();

                if (imgBtn == imgArrow) {

                    myVital.setVisibility(View.GONE);
                    if (getStoredLanguage().equals(LANGUAGE_ARABIC)) {
                        myVitalExpandBtn.setImageBitmap(flipImage());
                    } else {
                        myVitalExpandBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_right));
                    }

                } else {

                    myVital.setVisibility(View.VISIBLE);
                    myVitalExpandBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
                }
                break;

            case R.id.btn_appointment_expand:
            case R.id.linearLayout44:
                Bitmap imgBtnApp = ((BitmapDrawable) appointmentExpandBtn.getDrawable()).getBitmap();

                if (imgBtnApp == imgArrow) {

                    appointmentList.setVisibility(View.GONE);
                    if (getStoredLanguage().equals(LANGUAGE_ARABIC)) {
                        appointmentExpandBtn.setImageBitmap(flipImage());
                    } else {
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
            case R.id.linearLayout335:
                Bitmap imgBtnChat = ((BitmapDrawable) chatsExpandBtn.getDrawable()).getBitmap();

                if (imgBtnChat == imgArrow) {

                    chatsList.setVisibility(View.GONE);
                    if (getStoredLanguage().equals(LANGUAGE_ARABIC)) {
                        chatsExpandBtn.setImageBitmap(flipImage());
                    } else {
                        chatsExpandBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_right));
                    }
                } else {

                    chatsList.setVisibility(View.VISIBLE);
                    chatsExpandBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
                }
                break;
            case R.id.btn_referrals_expand:
            case R.id.llReferrals:
                Bitmap imgBtnReferrals = ((BitmapDrawable) referralsExpandBtn.getDrawable()).getBitmap();

                if (imgBtnReferrals == imgArrow) {

                    rvReferrals.setVisibility(View.GONE);
                    if (getStoredLanguage().equals(LANGUAGE_ARABIC)) {
                        referralsExpandBtn.setImageBitmap(flipImage());
                    } else {
                        referralsExpandBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_right));
                    }
                } else {

                    rvReferrals.setVisibility(View.VISIBLE);
                    referralsExpandBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
                }
                break;
        }
    }

    private Bitmap flipImage() {
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_arrow_right);
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
        if (viewFlipper != null && onLayoutChangeListener_viewFlipper != null)
            viewFlipper.removeOnLayoutChangeListener(onLayoutChangeListener_viewFlipper);
        super.onDestroy();
    }

    private String getStoredLanguage() {
        SharedPreferences sharedPref = mContext.getSharedPreferences(LANGUAGE_PREFS, Context.MODE_PRIVATE);
        return sharedPref.getString(LANGUAGE_SELECTED, getDeviceLanguage());
    }

    private String getDeviceLanguage() {
        return Locale.getDefault().getLanguage();
    }

    private void updateCurrentUser(String civilId) {

        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra(DEPENDENT_CIVILID, civilId);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        getDemographicResponse();
        checkNotificationsCounter();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dataUpdateReceiver == null) dataUpdateReceiver = new DataUpdateReceiver();
        IntentFilter intentFilter = new IntentFilter("TEST");
        mContext.registerReceiver(dataUpdateReceiver, intentFilter);
        IntentFilter intentFilter1 = new IntentFilter("BODY");
        mContext.registerReceiver(dataUpdateReceiver, intentFilter1);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (dataUpdateReceiver != null) mContext.unregisterReceiver(dataUpdateReceiver);
    }

    private class DataUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.requireNonNull(intent.getAction()).equals("TEST")) {
                if (AppCurrentUser.getInstance().getIsParent())
                    checkNotificationsCounter();
            }
            if (Objects.requireNonNull(intent.getAction()).equals("BODY")) {
                SharedPreferences sharedPref = mContext.getSharedPreferences("CHAT-BODY", Context.MODE_PRIVATE);
                String messageSender = sharedPref.getString("MESSAGE-SENDER", null);
                for (int i = 0; i < responseHolder.getmResult().getmHome().getmChatMessages().size(); i++) {
                    if (messageSender != null && responseHolder.getmResult().getmHome().getmChatMessages().get(i).getCreatedBy().trim().equalsIgnoreCase(messageSender.trim()))
                        responseHolder.getmResult().getmHome().getmChatMessages().get(i).setNew(true);
                }
                messageChatsAdapter.notifyDataSetChanged();
            }
        }
    }

    public void checkNotificationsCounter() {
        SharedPreferences sharedPref = mContext.getSharedPreferences("Counting", Context.MODE_PRIVATE);
        int NoOfAppointmentsNotifications = sharedPref.getInt("appointmentCount", 0);
        int NoOfNotifications = sharedPref.getInt("notificationCount", 0);
        if (mContext.getSharedPreferences("Counting", Context.MODE_PRIVATE).contains("chatCount")) {
            int NoOfChatNotifications = sharedPref.getInt("chatCount", 0);
            if (NoOfChatNotifications > 0) {
                tvNoOfChatNotification.setVisibility(View.VISIBLE);
                tvNoOfChatNotification.setText(String.valueOf(NoOfChatNotifications));
            } else
                tvNoOfChatNotification.setVisibility(View.GONE);
        } else
            tvNoOfChatNotification.setVisibility(View.GONE);
        if (NoOfAppointmentsNotifications > 0) {

        }
        if (NoOfNotifications > 0) {

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

    private void enableScrollingList() {
        menuButton.animate()
                .alpha(0.0f)
                .setDuration(100)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (isAdded()) {
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

    private void disableScrollingList() {
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
    }

    private void clearSharedPrefs() {
        SharedPreferences sharedPref;
        SharedPreferences.Editor editor;

        sharedPref = mContext.getSharedPreferences(PREFS_API_GET_TOKEN, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.clear();
        editor.apply();

        sharedPref = mContext.getSharedPreferences(PREFS_CURRENT_USER, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.clear();
        editor.apply();


        sharedPref = mContext.getSharedPreferences(PREFS_API_REGISTER_DEVICE, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.clear();
        editor.apply();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
    }
}