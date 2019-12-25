package om.gov.moh.phr.fragments;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import om.gov.moh.phr.apimodels.ApiAppointmentsListHolder;
import om.gov.moh.phr.dialogfragments.CancelAppointmentDialogFragment;
import om.gov.moh.phr.interfaces.AdapterToFragmentConnectorInterface;
import om.gov.moh.phr.interfaces.AppointmentsListInterface;
import om.gov.moh.phr.interfaces.DialogFragmentInterface;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.MyProgressDialog;

import static om.gov.moh.phr.models.MyConstants.ACTION_CANCEL;
import static om.gov.moh.phr.models.MyConstants.ACTION_RESCHEDULE;
import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;

public class AppointmentsListFragment extends Fragment implements AdapterToFragmentConnectorInterface {


    AppointmentsListRecyclerViewAdapter mAdapter;
    private Context mContext;
    private MediatorInterface mMediatorCallback;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private MyProgressDialog mProgressDialog;
    private RequestQueue mQueue;

    private CancelAppointmentDialogFragment mCancelAppointmentDialogFragment;

    public AppointmentsListFragment() {
        // Required empty public constructor
    }


    public static AppointmentsListFragment newInstance() {
        AppointmentsListFragment fragment = new AppointmentsListFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mMediatorCallback = (MediatorInterface) context;
        mToolbarControllerCallback = (ToolbarControllerInterface) context;
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
                mMediatorCallback.slideTo(0);

            }
        });
        TextView tvToolBarTitle = parentView.findViewById(R.id.tv_toolbar_title);
        tvToolBarTitle.setText(getString(R.string.title_appointments));
        tvToolBarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediatorCallback.slideTo(0);
            }
        });

        ImageButton ibAddAppointment = parentView.findViewById(R.id.ib_add_appointment);
        ibAddAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediatorCallback.changeFragmentContainerVisibility(View.VISIBLE, View.GONE);

                AppointmentNewFragment fragment = AppointmentNewFragment.newInstance();

                AppointmentsListInterface listener = new AppointmentsListInterface() {
                    @Override
                    public void onItemsChanged(int position) {
                        if (mMediatorCallback.isConnected()) {
                            Toast.makeText(mContext, "refreshed", Toast.LENGTH_SHORT).show();
                            getAppointmentsList();
                        }
                    }
                };

                fragment.setAppointmentListListener(listener);

                mMediatorCallback.changeFragmentTo(fragment, AppointmentNewFragment.class.getSimpleName());
            }
        });

        RecyclerView recyclerView = parentView.findViewById(R.id.recycler_view);
        setupRecyclerView(recyclerView);

        if (mMediatorCallback.isConnected()) {
            getAppointmentsList();
        }

        return parentView;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        mAdapter = new AppointmentsListRecyclerViewAdapter(AppointmentsListFragment.this, mContext);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(mDividerItemDecoration);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        recyclerView.setAdapter(mAdapter);
    }

    private void updateRecyclerView(ArrayList<ApiAppointmentsListHolder.Appointments> items) {
        mAdapter.updateItemsList(items);
    }

    private void getAppointmentsList() {
        mProgressDialog.showDialog();

        String fullUrl = "http://10.99.9.36:9000/nehrapi/demographics/appointmentReferral/" + mMediatorCallback.getCurrentUser().getCivilId();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fullUrl, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt(API_RESPONSE_CODE) == 0) {
                        Gson gson = new Gson();
                        ApiAppointmentsListHolder responseHolder = gson.fromJson(response.toString(), ApiAppointmentsListHolder.class);
                        Log.d("appointmentList", "-List" + response.getJSONObject("result").toString());
                        updateRecyclerView(responseHolder.getResult().getAppointmentsArrayList());


                    } else {

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
                Log.d("appointmentList", error.toString());
                error.printStackTrace();
                mProgressDialog.dismissDialog();
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

    @Override
    public void onResume() {
        super.onResume();
     //   mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.GONE);
    }


    @Override
    public <T> void onMyListItemClicked(T dataToPass, String dataTitle, int position) {

        ApiAppointmentsListHolder.Appointments appointment = (ApiAppointmentsListHolder.Appointments) dataToPass;

        switch (dataTitle) {
            case ACTION_RESCHEDULE: {
                showRescheduleAppointmentFragment(appointment);
            }
            break;
            case ACTION_CANCEL: {
                showCancelDialogFragment(appointment, position);
            }
        }

    }


    private void showRescheduleAppointmentFragment(ApiAppointmentsListHolder.Appointments appointment) {
        mMediatorCallback.changeFragmentContainerVisibility(View.VISIBLE, View.GONE);
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
        mMediatorCallback.changeFragmentContainerVisibility(View.GONE, View.VISIBLE);
        mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.VISIBLE);
    }
}
