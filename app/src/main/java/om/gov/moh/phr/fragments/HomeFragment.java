package om.gov.moh.phr.fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

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
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import om.gov.moh.phr.R;
import om.gov.moh.phr.adapters.ComingAppointmentListAdapter;
import om.gov.moh.phr.adapters.DependentRecyclerViewAdapter;
import om.gov.moh.phr.adapters.MessageChatsAdapter;
import om.gov.moh.phr.adapters.MyVitalListAdapter;
import om.gov.moh.phr.adapters.NotificationHomeAdapter;
import om.gov.moh.phr.adapters.PagerCardMainAdapter;
import om.gov.moh.phr.adapters.PaginationRecyclerViewAdapter;
import om.gov.moh.phr.adapters.UpdatesListAdapter;
import om.gov.moh.phr.apimodels.ApiDemographicsHolder;
import om.gov.moh.phr.interfaces.AdapterToFragmentConnectorInterface;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.ChatsModels;
import om.gov.moh.phr.models.MyProgressDialog;
import om.gov.moh.phr.models.MyVital;
import om.gov.moh.phr.models.Pagination;

import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;

public class HomeFragment extends Fragment implements AdapterToFragmentConnectorInterface, View.OnClickListener {

    private static final String API_URL_GET_DEMOGRAPHICS_INFO = API_NEHR_URL + "demographics/civilId/";

    private static final int NUMBER_OF_COLUMNS = 1;
    private Context mContext;
    private RequestQueue mQueue;
    private TextView tvAlert;
    private MyProgressDialog mProgressDialog;
    private RecyclerView rvGrid;
    private View parentView;
    private MediatorInterface mMediatorCallback;
    private ToolbarControllerInterface mToolbarCallback;
    private ArrayList<ApiDemographicsHolder.ApiDemographicItem.Patients> mPatientsArrayList = new ArrayList<>();
    private ApiDemographicsHolder.ApiDemographicItem mApiDemographicItem;
    private PaginationRecyclerViewAdapter mAdapter;
    //private TextView tvFullName;
    //private TextView tvCivilId;
    //private CircleImageView ivUserPhoto;
    // private ViewPager2 viewPager;
    private LinearLayout sliderDotspanel;
    int dotscount = 3;
    private ImageView[] dots;
    // private PagerCardMainAdapter pagerCardMainAdapter;
    private ScrollView menuListScrollView;
    private ImageButton menuButton, myVitalExpandBtn, appointmentExpandBtn, notificationExpandBtn, updatesExpandBtn, chatsExpandBtn;
    private RecyclerView myVital, appointmentList, notificationList, updatesList, chatsList;
    private ViewFlipper viewFlipper;
    private float lastX;


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
        // Inflate the layout for this fragment
        parentView = inflater.inflate(R.layout.fragment_home, container, false);


        mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
        mProgressDialog = new MyProgressDialog(mContext);
        tvAlert = parentView.findViewById(R.id.tv_alert);
        rvGrid = parentView.findViewById(R.id.vp_container);
        menuListScrollView = parentView.findViewById(R.id.menuListScrollView);
        menuButton = parentView.findViewById(R.id.btnMenu);
        myVital = parentView.findViewById(R.id.recyclerView_myvital);
        myVitalExpandBtn = parentView.findViewById(R.id.btn_myvital_expand);
        appointmentExpandBtn = parentView.findViewById(R.id.btn_appointment_expand);
        appointmentList = parentView.findViewById(R.id.recyclerView_coming_appointment);
        notificationList = parentView.findViewById(R.id.recyclerView_notification_home);
        notificationExpandBtn = parentView.findViewById(R.id.btn_notification_expand);
        updatesList = parentView.findViewById(R.id.recyclerView_updates_home);
        updatesExpandBtn = parentView.findViewById(R.id.btn_updates_expand);
        chatsList = parentView.findViewById(R.id.recyclerView_chats_home);
        chatsExpandBtn = parentView.findViewById(R.id.btn_chats_expand);
        rvGrid.setVisibility(View.GONE);
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
        //tvFullName = parentView.findViewById(R.id.tv_name);
        //tvCivilId = parentView.findViewById(R.id.tv_civil_id);
        //ivUserPhoto = parentView.findViewById(R.id.iv_avatar);
        //tvCivilId.setText(mMediatorCallback.getCurrentUser().getCivilId());
        if (mMediatorCallback.getAccessToken().getAccessCivilId().equals(mMediatorCallback.getCurrentUser().getCivilId()))
            //setupLoginData();
      /*   setRecyclerViewGrid();
       if (mContext != null && isAdded()) {
            if (mApiDemographicItem == null)
                getDemographicResponse();
            else {
                mAdapter.updateList(getPaginationArrayList());
                setupData(mApiDemographicItem);
            }
        }*/
            if (mMediatorCallback.isConnected()) {
                setRecyclerViewGrid();
                getDemographicResponse();


            } else {
                displayAlert(getString(R.string.alert_no_connection));
            }
        // viewPager = parentView.findViewById(R.id.viewPager_main);
        sliderDotspanel = parentView.findViewById(R.id.slider_dots);
        /*personalDetailMains = new ArrayList();
        personalDetailMains.add(new PersonalDetailMain(R.drawable.about_ic,"","","","","",""));
*/
        ArrayList<String> testArr = new ArrayList();
        testArr.add("Marwan");
        testArr.add("Ahmed");
        testArr.add("Alseryani");


        // pagerCardMainAdapter = new PagerCardMainAdapter(testArr, mContext);


        //viewPager.setPadding(30, 0, 30, 0);
       /* viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        viewPager.setAdapter(pagerCardMainAdapter);
        viewPager.setCurrentItem(1);*/
        // dotscount = pagerCardMainAdapter.getItemCount();
        //ImageView nonActive = new ImageView(this);
        //nonActive.setImageDrawable(getDrawable(R.drawable.non_active_dot));
        //final ArrayList<ImageView> dots = new ArrayList<>();


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
        //val dots = arrayOfNulls<ImageView>(dotscount)

        for (int i = 0; i < dotscount; i++) {
            dots[i].setImageDrawable(mContext.getDrawable(R.drawable.non_active_dot));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            sliderDotspanel.addView(dots[i], params);
        }

    /*    viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                for (int i = 0; i < dotscount; i++) {
                    //dots.set(i, new ImageView(this));
                    dots[i].setImageDrawable(mContext.getDrawable(R.drawable.non_active_dot));
                }

                dots[position].setImageDrawable(mContext.getDrawable(R.drawable.active_dot));
                Log.e("Selected_Page", String.valueOf(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });*/

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (menuListScrollView.getVisibility() == View.VISIBLE) {
        /*    menuListRecyclerView.animate()
                    .alpha(0.0f)
                    .setDuration(100)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            menuListRecyclerView.setVisibility(View.GONE);
                            menuItemScrollView.setAlpha(0.0f);
                            menuItemScrollView.setVisibility(View.VISIBLE);
                            menuItemScrollView.animate().alpha(1.0f);



                        }
                    });*/
            menuButton.animate()
                    .alpha(0.0f)
                    .setDuration(100)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            menuButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_list));
                            menuButton.animate().alpha(1.0f);
                        }
                    });
                    menuButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_list));
                    rvGrid.setVisibility(View.VISIBLE);
                    menuListScrollView.setVisibility(View.GONE);
                } else {
        /*    menuItemScrollView.animate()
                    .alpha(0.0f)
                    .setDuration(100)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            menuItemScrollView.setVisibility(View.GONE);
                            //menuListRecyclerView.setAlpha(0.0f);
                            menuListRecyclerView.setVisibility(View.VISIBLE);
                            //menuListRecyclerView.animate().alpha(1.0f);



                        }
                    });*/
            menuButton.animate()
                    .alpha(0.0f)
                    .setDuration(100)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            menuButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_item));
                            menuButton.animate().alpha(1.0f);
                        }
                    });
                    menuButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_item));
                    rvGrid.setVisibility(View.GONE);
                    menuListScrollView.setVisibility(View.VISIBLE);
                }
            }
        });

        myVitalExpandBtn.setOnClickListener(this);
        appointmentExpandBtn.setOnClickListener(this);
        notificationExpandBtn.setOnClickListener(this);
        updatesExpandBtn.setOnClickListener(this);
        chatsExpandBtn.setOnClickListener(this);
        /* setup my vital list */
        ArrayList<MyVital> myVitals = new ArrayList<>();
        myVitals.add(new MyVital("Heart rate:", "68", ""));
        myVitals.add(new MyVital("Oxygen saturation:", "44", "%"));
        myVitals.add(new MyVital("Body temperature:", "36.5", "\u2103"));
        myVitals.add(new MyVital("Blood pressure:", "119/79", "mm[Hg]"));
        setupMyVitalList(myVitals);

        /* setup appointment list */
        ArrayList<String> appointments = new ArrayList<>();
        appointments.add("Gen. Medicine clinic | 12-Aug-2020 | 07:30 | Directorate General of Information Technology");
        appointments.add("Gen. Medicine clinic | 20-Aug-2020 | 11:30 | Directorate General of Information Technology");
        setupAppointmentList(appointments);

        /* setup notification list */
        ArrayList<String> notifications = new ArrayList<>();
        notifications.add("This is notification 1");
        notifications.add("This is notification 2");
        setupNotificationList(notifications);

        /* setup updates list */
        ArrayList<String> updates = new ArrayList<>();
        updates.add("New Lab Result");
        updates.add("New Procedure Report");
        setupUpdatesList(updates);

        /* setup chats list */
        ArrayList<ChatsModels> chatsModels = new ArrayList<>();
        chatsModels.add(new ChatsModels("Dr.Binu", "21-07-2020 11:30:54"));
        chatsModels.add(new ChatsModels("Dr.Jalil", "21-07-2020 09:11:28"));
        setupChatsList(chatsModels);

        return parentView;
    }


    public void setupMyVitalList(ArrayList<MyVital> myVitals) {
        MyVitalListAdapter myVitalListAdapter = new MyVitalListAdapter(myVitals, mContext);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        //DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(myVital.getContext(),layoutManager.getOrientation());
        // myVital.addItemDecoration(mDividerItemDecoration);
        myVital.setLayoutManager(layoutManager);
        myVital.setItemAnimator(new DefaultItemAnimator());
        myVital.setAdapter(myVitalListAdapter);

    }

    public void setupAppointmentList(ArrayList<String> appointments) {
        ComingAppointmentListAdapter comingAppointmentListAdapter = new ComingAppointmentListAdapter(appointments, mContext);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(appointmentList.getContext(), layoutManager.getOrientation());
        appointmentList.addItemDecoration(mDividerItemDecoration);
        appointmentList.setLayoutManager(layoutManager);
        appointmentList.setItemAnimator(new DefaultItemAnimator());
        appointmentList.setAdapter(comingAppointmentListAdapter);

    }

    public void setupNotificationList(ArrayList<String> notifications) {
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
        UpdatesListAdapter comingAppointmentListAdapter = new UpdatesListAdapter(updates, mContext);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(updatesList.getContext(), layoutManager.getOrientation());
        updatesList.addItemDecoration(mDividerItemDecoration);
        updatesList.setLayoutManager(layoutManager);
        updatesList.setItemAnimator(new DefaultItemAnimator());
        updatesList.setAdapter(comingAppointmentListAdapter);

    }

    public void setupChatsList(ArrayList<ChatsModels> chatsModels) {
        MessageChatsAdapter messageChatsAdapter = new MessageChatsAdapter(chatsModels, mContext);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(chatsList.getContext(), layoutManager.getOrientation());
        chatsList.addItemDecoration(mDividerItemDecoration);
        chatsList.setLayoutManager(layoutManager);
        chatsList.setItemAnimator(new DefaultItemAnimator());
        chatsList.setAdapter(messageChatsAdapter);

    }

    private void getDemographicResponse() {
        mProgressDialog.showDialog();

        String fullUrl = API_URL_GET_DEMOGRAPHICS_INFO + mMediatorCallback.getCurrentUser().getCivilId() + "?source=PHR";
//        String fullUrl = API_URL_GET_DEMOGRAPHICS_INFO + "62163078" + "?source=PHR";
        Log.d("fullURL-demo", fullUrl);


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fullUrl, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt(API_RESPONSE_CODE) == 0) {
                        Gson gson = new Gson();
                        ApiDemographicsHolder responseHolder = gson.fromJson(response.toString(), ApiDemographicsHolder.class);
                        Log.d("resp-demo", response.getJSONObject("result").toString());
                        if (mContext != null) {
                            mApiDemographicItem = responseHolder.getmResult();
                            mAdapter.updateList(getPaginationArrayList());
                            setupData(responseHolder.getmResult());
                        }

                    } else {
                        //displayAlert(getResources().getString(R.string.no_record_found));
                        mProgressDialog.dismissDialog();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mProgressDialog.dismissDialog();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mContext != null && isAdded()) {
                    mProgressDialog.dismissDialog();
                    Log.d("resp-demographic", error.toString());
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

    private void setupData(ApiDemographicsHolder.ApiDemographicItem apiDemographicItem) {
        mPatientsArrayList = apiDemographicItem.getInstitutesArrayList();
        //tvFullName.setText(apiDemographicItem.getFullName());
  /*      if (apiDemographicItem.getAliasYn().equals("Y"))
            tvCivilId.setText("XXXXXXXX");*/
        //Glide.with(mContext).load(apiDemographicItem.getPersonPhoto(mContext)).into(ivUserPhoto);
    }

    private void setupLoginData() {
        //tvFullName.setText(mMediatorCallback.getAccessToken().getPersonName());
        //Glide.with(mContext).load(getPersonPhoto(mContext)).into(ivUserPhoto);
    }

    private Bitmap getPersonPhoto(Context context) {
        if (mMediatorCallback.getAccessToken().getImage() == null || TextUtils.isEmpty(mMediatorCallback.getAccessToken().getImage())) {
            return BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.avatar);
        } else {
            //decode base64 string to image
            byte[] imageBytes = Base64.decode(mMediatorCallback.getAccessToken().getImage(), Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            return decodedImage;
        }

    }

    private void setRecyclerViewGrid() {
        mAdapter = new PaginationRecyclerViewAdapter(HomeFragment.this, mContext);
        rvGrid.setLayoutManager(new GridLayoutManager(mContext, NUMBER_OF_COLUMNS));
        rvGrid.setItemAnimator(new DefaultItemAnimator());
        rvGrid.setHasFixedSize(true);
        rvGrid.setAdapter(mAdapter);
    }

    private ArrayList<Pagination> getPaginationArrayList() {

        ArrayList<Pagination> paginationArrayList = new ArrayList<>();
        if (mContext != null && isAdded()) {
            paginationArrayList.add(new Pagination(
                    R.drawable.ic_demographoc,
                    getString(R.string.title_demographic),
                    DemographicsFragment.newInstance(mApiDemographicItem),
                    DemographicsFragment.class.getSimpleName()));


            paginationArrayList.add(new Pagination(
                    R.drawable.ic_vital,
                    getString(R.string.title_body_measurements),
                    BodyMeasurementsFragment.newInstance(mApiDemographicItem.getRecentVitalsArrayList()),
                    BodyMeasurementsFragment.class.getSimpleName()));

            paginationArrayList.add(new Pagination(
                    R.drawable.ic_medical_history,
                    getString(R.string.title_vital_info),
                    VitalInfoFragment.newInstance(),
                    VitalInfoFragment.class.getSimpleName()));


            paginationArrayList.add(new Pagination(
                    R.drawable.ic_health_records,
                    getString(R.string.title_health_records),
                    HealthRecordListFragment.newInstance(),
                    HealthRecordListFragment.class.getSimpleName()));

            paginationArrayList.add(new Pagination(
                    R.drawable.ic_lab,
                    getString(R.string.title_lab_results),
                    LabResultsContainerFragment.newInstance(),
                    LabResultsFragment.class.getSimpleName()));

            paginationArrayList.add(new Pagination(
                    R.drawable.ic_medication,
                    getString(R.string.title_medication),
                    MedicationContainerFragment.newInstance(),
                    MedicationFragment.class.getSimpleName()));

            paginationArrayList.add(new Pagination(
                    R.drawable.ic_procedure,
                    getString(R.string.title_procedures_reports),
                    ProceduresReportsContainerFragment.newInstance(),
                    ProceduresReportsFragment.class.getSimpleName()));

            paginationArrayList.add(new Pagination(
                    R.drawable.ic_documents,
                    getString(R.string.title_other_document),
                    DocsContainerFragment.newInstance(),
                    ""));

            paginationArrayList.add(new Pagination(
                    R.drawable.ic_immunization,
                    getString(R.string.title_immunization),
                    ImmunizationContainerFragment.newInstance(),
                    ImmunizationFragment.class.getSimpleName()));

            paginationArrayList.add(new Pagination(
                    R.drawable.ic_appointment,
                    getString(R.string.title_appointments),
                    AppointmentNewFragment.newInstance(),
                    AppointmentNewFragment.class.getSimpleName()));

            paginationArrayList.add(new Pagination(
                    R.drawable.ic_chat,
                    getString(R.string.title_chat),
                    ChatFragment.newInstance(),
                    ChatFragment.class.getSimpleName()));

            paginationArrayList.add(new Pagination(
                    R.drawable.ic_organ,
                    getString(R.string.title_organ),
                    OrganDonationFragment.newInstance(),
                    OrganDonationFragment.class.getSimpleName()));
        }
        return paginationArrayList;
    }


    private void displayAlert(String msg) {
        rvGrid.setVisibility(View.GONE);
        tvAlert.setVisibility(View.VISIBLE);
        tvAlert.setText(msg);
    }

    @Override
    public <T> void onMyListItemClicked(T dataToPass, String dataTitle) {

        if (mApiDemographicItem != null) {
            mToolbarCallback.changeSideMenuToolBarVisibility(View.GONE);
            mMediatorCallback.changeFragmentTo((Fragment) dataToPass, dataTitle);
        }
    }

    @Override
    public <T> void onMyListItemClicked(T dataToPass, String dataTitle, int position) {

    }

    public void expandCollapseBtn(View view) {
        Bitmap imgArrow = ((BitmapDrawable) getResources().getDrawable(R.drawable.ic_arrow_down)).getBitmap();
        switch (view.getId()) {
            case R.id.btn_myvital_expand:

                Bitmap imgBtn = ((BitmapDrawable) myVitalExpandBtn.getDrawable()).getBitmap();

                if (imgBtn == imgArrow) {

                    myVital.setVisibility(View.GONE);
                    myVitalExpandBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_right));
                } else {

                    myVital.setVisibility(View.VISIBLE);
                    myVitalExpandBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
                }
                break;
            case R.id.btn_appointment_expand:
                Bitmap imgBtnApp = ((BitmapDrawable) appointmentExpandBtn.getDrawable()).getBitmap();

                if (imgBtnApp == imgArrow) {

                    appointmentList.setVisibility(View.GONE);
                    appointmentExpandBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_right));
                } else {

                    appointmentList.setVisibility(View.VISIBLE);
                    appointmentExpandBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
                }
                break;
            case R.id.btn_notification_expand:
                Bitmap imgBtnNot = ((BitmapDrawable) notificationExpandBtn.getDrawable()).getBitmap();

                if (imgBtnNot == imgArrow) {

                    notificationList.setVisibility(View.GONE);
                    notificationExpandBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_right));
                } else {

                    notificationList.setVisibility(View.VISIBLE);
                    notificationExpandBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
                }
                break;
            case R.id.btn_updates_expand:
                Bitmap imgBtnUpd = ((BitmapDrawable) updatesExpandBtn.getDrawable()).getBitmap();

                if (imgBtnUpd == imgArrow) {

                    updatesList.setVisibility(View.GONE);
                    updatesExpandBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_right));
                } else {

                    updatesList.setVisibility(View.VISIBLE);
                    updatesExpandBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
                }
                break;
            case R.id.btn_chats_expand:
                Bitmap imgBtnChat = ((BitmapDrawable) chatsExpandBtn.getDrawable()).getBitmap();

                if (imgBtnChat == imgArrow) {

                    chatsList.setVisibility(View.GONE);
                    chatsExpandBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_right));
                } else {

                    chatsList.setVisibility(View.VISIBLE);
                    chatsExpandBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
                }
                break;

        }
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
}