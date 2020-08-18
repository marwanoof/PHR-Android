package om.gov.moh.phr.dialogfragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
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
import om.gov.moh.phr.fragments.AppointmentsListFragment;
import om.gov.moh.phr.interfaces.DialogFragmentInterface;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.models.MyProgressDialog;

import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_MESSAGE;
import static om.gov.moh.phr.models.MyConstants.PARAM_EST_CODE;
import static om.gov.moh.phr.models.MyConstants.PARAM_RESERVATION_ID;

public class SaveRescheduleAppointmentDialogFragment extends DialogFragment {


    private static final String PARAM_RUN_ID = "PARAM_RUN_ID";
    private static final String PARAM_PATIENT = "PARAM_PATIENT";
    private static final String PARAM_CLINIC_ID = "PARAM_CLINIC_ID";
    private static final String PARAM_DATE = "PARAM_DATE";
    private static final String PARAM_TIME = "PARAM_TIME";
    private String mEstCode;
    private String mRunId;
    private String mPatientId;
    private String mDate;
    private String mTime;
    private String mReservationId;
    private DialogFragmentInterface mDialogListener;
    private Context mContext;

    private MediatorInterface mMediatorCallback;
    private MyProgressDialog mProgressDialog;
    private RequestQueue mQueue;


    public SaveRescheduleAppointmentDialogFragment() {
        // Required empty public constructor
    }


    public static SaveRescheduleAppointmentDialogFragment newInstance(String estCode, String reservationId, String runId, String patientId, String date, String time) {
        SaveRescheduleAppointmentDialogFragment fragment = new SaveRescheduleAppointmentDialogFragment();
        Bundle args = new Bundle();
        args.putString(PARAM_EST_CODE, estCode);
        args.putString(PARAM_RESERVATION_ID, reservationId);
        args.putString(PARAM_RUN_ID, runId);
        args.putString(PARAM_PATIENT, patientId);
        args.putString(PARAM_DATE, date);
        args.putString(PARAM_TIME, time);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEstCode = getArguments().getString(PARAM_EST_CODE);
            mReservationId = getArguments().getString(PARAM_RESERVATION_ID);
            mRunId = getArguments().getString(PARAM_RUN_ID);
            mPatientId = getArguments().getString(PARAM_PATIENT);
            mDate = getArguments().getString(PARAM_DATE);
            mTime = getArguments().getString(PARAM_TIME);
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
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mMediatorCallback = (MediatorInterface) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_save_appointment_dialog, container, false);


        mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
        mProgressDialog = new MyProgressDialog(mContext);


        String title = " " + mDate + getResources().getString(R.string.at) + mTime;


        TextView tvConfirmMessage = parentView.findViewById(R.id.tv_confirm_message);
        tvConfirmMessage.append(title);
        final TextInputEditText tietRemarks = parentView.findViewById(R.id.tiet_remarks);
        Button btnConfirm = parentView.findViewById(R.id.btn_confirm);
        Button btnDecline = parentView.findViewById(R.id.btn_decline);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveAppointment(tietRemarks.getText().toString());
            }
        });

        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogListener.onDecline();
            }
        });


        return parentView;
    }

    private void saveAppointment(String remarks) {


        String fullUrl = API_NEHR_URL+"appointment/saveReschedule?estCode="+mEstCode+"&reservationId=" + mReservationId + "&runId=" + mRunId + "&patientId=" + mPatientId + "&remarks=" + remarks;
        Log.d("re-runId-res", "link : " + fullUrl);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fullUrl, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    if (response.getInt(API_RESPONSE_CODE) == 0) {

                        Toast.makeText(mContext, "Success : " + response.getString(API_RESPONSE_MESSAGE), Toast.LENGTH_SHORT).show();
                        Log.d("re-runId-res", "Success " + response.getString(API_RESPONSE_MESSAGE));
                        mDialogListener.onAccept();
                    } else {
                        Toast.makeText(mContext, "Fail : " + response.getString(API_RESPONSE_MESSAGE), Toast.LENGTH_SHORT).show();
                        Log.d("re-runId-res", "Fail " + response.getString(API_RESPONSE_MESSAGE));
                        mProgressDialog.dismissDialog();
                        mDialogListener.onDecline();
                    }
                } catch (JSONException e) {
                    Log.d("re-runId-res", "JSONException " + e);
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
