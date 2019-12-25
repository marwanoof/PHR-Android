package om.gov.moh.phr.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiAllInstitutes;
import om.gov.moh.phr.dialogfragments.SelectInstituteFragmentDialog;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.MyProgressDialog;

import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_HEALTH_NET_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;
import static om.gov.moh.phr.models.MyConstants.PARAM_SELECTED_INSTITUTE;
import static om.gov.moh.phr.models.MyConstants.REQUEST_CODE_SELECTED_INSTITUTE;

public class AddLinkingInstitutesFragment extends Fragment {

    private static final String API_URL_GET_All_INSTITUTES = API_NEHR_HEALTH_NET_URL + "master/getNehrInstitutes";
    private Context mContext;
    private MediatorInterface mMediatorCallback;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private RequestQueue mQueue;
    private MyProgressDialog mProgressDialog;
    private SelectInstituteFragmentDialog dialogFragment;
    private TextView tvLabel;
    private ApiAllInstitutes mInstitutesViewModel;

    public AddLinkingInstitutesFragment() {
        // Required empty public constructor
    }


    public static AddLinkingInstitutesFragment newInstance() {
        AddLinkingInstitutesFragment fragment = new AddLinkingInstitutesFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
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
/*        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }*/
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_add_linking_institutes, container, false);
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
        tvToolBarTitle.setText(getString(R.string.title_linked_institutes));


        final TextInputEditText tietPatientId = parentView.findViewById(R.id.tiet_patient_id);


        tvLabel = parentView.findViewById(R.id.tv_label);
        LinearLayout llSpnrInstitutes = parentView.findViewById(R.id.ll_spnr_institutes);
        final FragmentManager fm = getActivity().getSupportFragmentManager();


        Button btnSubmit = parentView.findViewById(R.id.btn_submit);


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tietPatientId.getText().toString();

            }
        });

        if (mMediatorCallback.isConnected()) {
            if (mInstitutesViewModel == null) {
                getInstitutesList(fm, llSpnrInstitutes);
            } else {
                dialogFragment = SelectInstituteFragmentDialog.newInstance(mInstitutesViewModel.getInstitutesArrayList(mInstitutesViewModel.getResult()));
                dialogFragment.setTargetFragment(AddLinkingInstitutesFragment.this, REQUEST_CODE_SELECTED_INSTITUTE);

                llSpnrInstitutes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialogFragment.show(fm, SelectInstituteFragmentDialog.class.getSimpleName());
                    }
                });
            }
        } else {


        }


        return parentView;
    }


    private void getInstitutesList(final FragmentManager fm, final LinearLayout llSpnrInstitutes) {

        mProgressDialog.showDialog();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, API_URL_GET_All_INSTITUTES, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt(API_RESPONSE_CODE) == 0) {
                        Gson gson = new Gson();
                        mInstitutesViewModel
                                = gson.fromJson(response.toString(), ApiAllInstitutes.class);
                        Log.d("resp-pending", response.getJSONArray("result").toString());
                        Toast.makeText(mContext, "" + mInstitutesViewModel.getInstitutesArrayList(mInstitutesViewModel.getResult()).size(), Toast.LENGTH_SHORT).show();


                        dialogFragment = SelectInstituteFragmentDialog.newInstance(mInstitutesViewModel.getInstitutesArrayList(mInstitutesViewModel.getResult()));
                        dialogFragment.setTargetFragment(AddLinkingInstitutesFragment.this, REQUEST_CODE_SELECTED_INSTITUTE);

                        llSpnrInstitutes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                dialogFragment.show(fm, SelectInstituteFragmentDialog.class.getSimpleName());
                            }
                        });


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
                Log.d("resp-pending", error.toString());
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


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Make sure fragment codes match up
        if (requestCode == REQUEST_CODE_SELECTED_INSTITUTE) {
            String selectedInstitute = data.getStringExtra(PARAM_SELECTED_INSTITUTE);
            tvLabel.setText(selectedInstitute);
            dialogFragment.dismiss();
        }
    }


}
