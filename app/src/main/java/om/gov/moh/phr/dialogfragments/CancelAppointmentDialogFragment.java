package om.gov.moh.phr.dialogfragments;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiAppointmentsListHolder;
import om.gov.moh.phr.fragments.AppointmentNewFragment;
import om.gov.moh.phr.interfaces.DialogFragmentInterface;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.models.MyProgressDialog;

import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_MESSAGE;
import static om.gov.moh.phr.models.MyConstants.PARAM_APPOINTMENTS;


public class CancelAppointmentDialogFragment extends DialogFragment {


    private static final String PARAM_POSITION = "PARAM_POSITION";
    private ApiAppointmentsListHolder.Appointments mAppointment;
    private Context mContext;
    private MediatorInterface mMediatorCallback;

    private RequestQueue mQueue;
    private MyProgressDialog mProgressDialog;
    private DialogFragmentInterface mDialogListener;
    private int mPosition;

    public CancelAppointmentDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mMediatorCallback = (MediatorInterface) context;
    }


    public static CancelAppointmentDialogFragment newInstance(ApiAppointmentsListHolder.Appointments appointment, int position) {
        CancelAppointmentDialogFragment fragment = new CancelAppointmentDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(PARAM_APPOINTMENTS, appointment);
        args.putInt(PARAM_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAppointment = (ApiAppointmentsListHolder.Appointments) getArguments().getSerializable(PARAM_APPOINTMENTS);
            mPosition = getArguments().getInt(PARAM_POSITION);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_cancel_appointment_dialog, container, false);

        mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
        mProgressDialog = new MyProgressDialog(mContext);

        TextView tvMessage = parentView.findViewById(R.id.tv_cancel_message);
        Button btnConfirm = parentView.findViewById(R.id.btn_confirm);
        Button btnDecline = parentView.findViewById(R.id.btn_decline);
        final TextInputEditText tietReason = parentView.findViewById(R.id.tiet_reason);

        String title = " " + mAppointment.getDescription() + mAppointment.getEstName();
        tvMessage.append(title);


        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reason = tietReason.getText().toString();
                if (TextUtils.isEmpty(reason)) {
                    Toast.makeText(mContext, getString(R.string.alert_cancellation), Toast.LENGTH_SHORT).show();
                } else {
                    cancelAppointment(reason);
                }
            }
        });


        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return parentView;
    }

    @Override
    public void dismiss() {
        super.dismiss();

    }


    private void cancelAppointment(String reason) {

        String fullUrl = "http://10.99.9.36:9000/nehrapi/appointment/cancel?estCode=" + AppointmentNewFragment.TEST_EST_CODE + "&reservationId=" + mAppointment.getReservationId() + "&reason=" + reason + "&civilId=" + mMediatorCallback.getCurrentUser().getCivilId();
        Log.d("saveAppointment", "link : " + fullUrl);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fullUrl, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    if (response.getInt(API_RESPONSE_CODE) == 0) {

                        Toast.makeText(mContext, response.getString(API_RESPONSE_MESSAGE), Toast.LENGTH_SHORT).show();
                        mDialogListener.onAccept(mPosition);
                        dismiss();

                    } else {
                        Toast.makeText(mContext, response.getString(API_RESPONSE_MESSAGE), Toast.LENGTH_SHORT).show();
//                        mProgressDialog.dismissDialog();
                        dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mProgressDialog.dismissDialog();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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

    public void setDialogFragmentListener(DialogFragmentInterface listener) {
        mDialogListener = listener;
    }

}
