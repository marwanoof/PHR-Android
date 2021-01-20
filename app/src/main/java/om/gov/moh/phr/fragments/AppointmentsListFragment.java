package om.gov.moh.phr.fragments;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
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
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import om.gov.moh.phr.R;
import om.gov.moh.phr.adapters.AppointmentsListRecyclerViewAdapter;
import om.gov.moh.phr.adapters.RefferalsListRecyclerViewAdapter;
import om.gov.moh.phr.apimodels.ApiAppointmentsListHolder;
import om.gov.moh.phr.apimodels.ApiHomeHolder;
import om.gov.moh.phr.dialogfragments.CancelAppointmentDialogFragment;
import om.gov.moh.phr.interfaces.AdapterToFragmentConnectorInterface;
import om.gov.moh.phr.interfaces.AppointmentsListInterface;
import om.gov.moh.phr.interfaces.DialogFragmentInterface;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.GlobalMethodsKotlin;
import om.gov.moh.phr.models.MyProgressDialog;

import static om.gov.moh.phr.models.MyConstants.ACTION_CANCEL;
import static om.gov.moh.phr.models.MyConstants.ACTION_RESCHEDULE;
import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;

public class AppointmentsListFragment extends Fragment implements AdapterToFragmentConnectorInterface, SwipeRefreshLayout.OnRefreshListener {


    private AppointmentsListRecyclerViewAdapter mAdapter;
    private RefferalsListRecyclerViewAdapter mRefferalAdapter;
    private Context mContext;
    private MediatorInterface mMediatorCallback;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private MyProgressDialog mProgressDialog;
    private RequestQueue mQueue;
    private CancelAppointmentDialogFragment mCancelAppointmentDialogFragment;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvNoAppointmentAlert;
    private TextView tvNoRefferals;
    private RecyclerView recyclerView;
    private RecyclerView rvRefferalls;
    private static final String ARG_PARAM1 = "ARG_PARAM1";
    private ArrayList<ApiHomeHolder.Patients> patientsArrayList;

    public AppointmentsListFragment() {
        // Required empty public constructor
    }


    public static AppointmentsListFragment newInstance(ArrayList<ApiHomeHolder.Patients> patients) {
        AppointmentsListFragment fragment = new AppointmentsListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, patients);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            patientsArrayList = (ArrayList<ApiHomeHolder.Patients>) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mMediatorCallback = (MediatorInterface) context;
        mToolbarControllerCallback = (ToolbarControllerInterface) context;
        mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.GONE);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_appoinments_list, container, false);

        mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
        mProgressDialog = new MyProgressDialog(mContext);

        //simple toolbar
        ImageButton ibToolbarBackButton = parentView.findViewById(R.id.ib_toolbar_back_button);
        ibToolbarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mToolbarControllerCallback.customToolbarBackButtonClicked();
            }
        });
        TextView tvToolBarTitle = parentView.findViewById(R.id.tv_toolbar_title);
        tvToolBarTitle.setText(getString(R.string.title_appointments));

        tvNoAppointmentAlert = parentView.findViewById(R.id.tv_no_appointment_alert);
        tvNoRefferals = parentView.findViewById(R.id.tv_no_refferals_alert);
        //ImageButton ibRefresh = parentView.findViewById(R.id.ib_refresh);
        //ibRefresh.setVisibility(View.VISIBLE);
        recyclerView = parentView.findViewById(R.id.recycler_view);
        rvRefferalls = parentView.findViewById(R.id.rv_referral);
        swipeRefreshLayout = parentView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        getAppointmentsList();
                                    }
                                }
        );
        /*ibRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAppointmentsList();
            }
        });*/
        ImageButton ibAddAppointment = parentView.findViewById(R.id.ib_add_appointment);
        ibAddAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppointmentNewFragment fragment = AppointmentNewFragment.newInstance(patientsArrayList);
                mMediatorCallback.changeFragmentTo(fragment, AppointmentNewFragment.class.getSimpleName());
               /* AppointmentsListInterface listener = new AppointmentsListInterface() {
                    @Override
                    public void onItemsChanged(int position) {
                        if (mMediatorCallback.isConnected()) {
                            Toast.makeText(mContext, getResources().getString(R.string.refresh_msg), Toast.LENGTH_SHORT).show();
                            getAppointmentsList();
                        }
                    }
                };

                fragment.setAppointmentListListener(listener);

                mMediatorCallback.changeFragmentTo(fragment, AppointmentNewFragment.class.getSimpleName());*/
            }
        });


        setupRecyclerView(recyclerView);
        setupRefferalsRecyclerView(rvRefferalls);
        if (mMediatorCallback.isConnected()) {
            getAppointmentsList();
        } else {
            GlobalMethodsKotlin.Companion.showAlertDialog(mContext, getResources().getString(R.string.no_internet_title), getResources().getString(R.string.alert_no_connection), getResources().getString(R.string.ok), R.drawable.ic_error);
        }
        return parentView;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        mAdapter = new AppointmentsListRecyclerViewAdapter(AppointmentsListFragment.this, mContext);
        final LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(mDividerItemDecoration);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    private void setupRefferalsRecyclerView(RecyclerView recyclerView) {
        mRefferalAdapter = new RefferalsListRecyclerViewAdapter(AppointmentsListFragment.this, mContext, true);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(mDividerItemDecoration);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mRefferalAdapter);
    }

    private void updateRecyclerView(ArrayList<ApiAppointmentsListHolder.Appointments> items) {
        mAdapter.updateItemsList(items);
    }

    private void updateRefferalsRecyclerView(ArrayList<ApiAppointmentsListHolder.Referrals> items) {
        mRefferalAdapter.updateItemsList(items);
    }

    private void getAppointmentsList() {
        mProgressDialog.showDialog();
// showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);
        String fullUrl = API_NEHR_URL + "demographics/v2/appointmentReferral";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, fullUrl, getJSONRequestParams()
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mContext != null && isAdded()) {
                    try {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {
                            Gson gson = new Gson();
                            ApiAppointmentsListHolder responseHolder = gson.fromJson(response.toString(), ApiAppointmentsListHolder.class);
                            if (responseHolder.getResult() != null) {
                                if (responseHolder.getResult().getAppointmentsArrayList() != null) {
                                    updateRecyclerView(responseHolder.getResult().getAppointmentsArrayList());
                                    if (responseHolder.getResult().getAppointmentsArrayList().size() == 0)
                                        tvNoAppointmentAlert.setVisibility(View.VISIBLE);
                                }else
                                    tvNoAppointmentAlert.setVisibility(View.VISIBLE);
                                if (responseHolder.getResult().getReferralsArrayList() != null) {
                                    updateRefferalsRecyclerView(responseHolder.getResult().getReferralsArrayList());
                                    if (responseHolder.getResult().getReferralsArrayList().size() == 0)
                                        tvNoRefferals.setVisibility(View.VISIBLE);
                                }else
                                    tvNoRefferals.setVisibility(View.VISIBLE);
                            }
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

    private JSONObject getJSONRequestParams() {
        Map<String, String> params = new HashMap<>();
        params.put("source", "");
        params.put("data", "");
        params.put("civilId", mMediatorCallback.getCurrentUser().getCivilId());
        return new JSONObject(params);

    }

    @Override
    public void onResume() {
        super.onResume();
        //   mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.GONE);
    }


    @Override
    public <T> void onMyListItemClicked(T dataToPass, String dataTitle, int position) {
   /*     if (dataToPass instanceof String) {
            String referrals = dataToPass.toString();
            if ("REFFERAL".equals(dataTitle))
                mMediatorCallback.changeFragmentTo(RefferalsDetailsFragment.newInstance(referrals), RefferalsDetailsFragment.class.getSimpleName());
     } else {*/
        ApiAppointmentsListHolder.Appointments appointment = (ApiAppointmentsListHolder.Appointments) dataToPass;
        switch (dataTitle) {
            case ACTION_RESCHEDULE: {
                showRescheduleAppointmentFragment(appointment);
            }
            break;
            case ACTION_CANCEL: {
                showCancelDialogFragment(appointment, position);
            }
            break;
        }
        //  }


    }


    private void showRescheduleAppointmentFragment(ApiAppointmentsListHolder.Appointments appointment) {
        mMediatorCallback.changeFragmentTo(AppointmentRescheduleFragment.newInstance(appointment), AppointmentRescheduleDateFragment.class.getSimpleName());
    }

    private void showCancelDialogFragment(ApiAppointmentsListHolder.Appointments appointment, int position) {
        mCancelAppointmentDialogFragment = CancelAppointmentDialogFragment.newInstance(appointment, position);
        mCancelAppointmentDialogFragment.setDialogFragmentListener(new DialogFragmentInterface() {
            @Override
            public void onAccept() {

            }

            @Override
            public void onAccept(int position) {
                mAdapter.deleteItemAt(position);
                onDecline();
            }

            @Override
            public void onDecline() {
                mCancelAppointmentDialogFragment.dismiss();
            }

        });
        mCancelAppointmentDialogFragment.show(getChildFragmentManager(), CancelAppointmentDialogFragment.class.getSimpleName());
    }


    @Override
    public <T> void onMyListItemClicked(T dataToPass, String dataTitle) {

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.VISIBLE);
    }


    @Override
    public void onRefresh() {
        getAppointmentsList();
    }

}
