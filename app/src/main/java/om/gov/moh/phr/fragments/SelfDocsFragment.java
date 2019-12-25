package om.gov.moh.phr.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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

import om.gov.moh.phr.R;
import om.gov.moh.phr.adapters.OtherDocsRecyclerViewAdapter;
import om.gov.moh.phr.adapters.UploadedDocsRecyclerViewAdapter;
import om.gov.moh.phr.apimodels.ApiUploadsDocsHolder;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.MyProgressDialog;

import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_RESULT;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelfDocsFragment extends Fragment implements SearchView.OnQueryTextListener {
    private Context mContext;
    private MediatorInterface mMediatorCallback;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private static final String API_URL_GET_UPLOADS_DOCS = API_NEHR_URL + "file/list/";
    private RequestQueue mQueue;
    private MyProgressDialog mProgressDialog;
    private UploadedDocsRecyclerViewAdapter mUploadedAdapter;
    private RecyclerView rvOtherDocsList;
    private TextView tvAlert;

    public SelfDocsFragment() {
        // Required empty public constructor
    }
    public static SelfDocsFragment newInstance() {
        SelfDocsFragment fragment = new SelfDocsFragment();
        Bundle args = new Bundle();
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    //    if (view == null) {
        View view = inflater.inflate(R.layout.fragment_self_docs, container, false);
            ImageButton ibToolbarBackButton = view.findViewById(R.id.ib_toolbar_back_button);
            ibToolbarBackButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mToolbarControllerCallback.customToolbarBackButtonClicked();
                }
            });
            mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
            mProgressDialog = new MyProgressDialog(mContext);
            rvOtherDocsList = view.findViewById(R.id.rv_DocsList);
            tvAlert = view.findViewById(R.id.tv_alert);
            SearchView searchView = (SearchView) view.findViewById(R.id.sv_searchView);
            searchView.setOnQueryTextListener(this);
            TextView tvAddDoc = view.findViewById(R.id.addDoc);
            tvAddDoc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMediatorCallback.changeFragmentTo(AddDocFragment.newInstance(), getResources().getString(R.string.title_other_document));
                }
            });
            if (mMediatorCallback.isConnected()) {

                String uploadedListDocsUrl = API_URL_GET_UPLOADS_DOCS + mMediatorCallback.getCurrentUser().getCivilId() + "/" + mMediatorCallback.getCurrentUser().getCivilId();
                Log.d("Docs-url", uploadedListDocsUrl);
                getUploadedDocsList(uploadedListDocsUrl);
            } else {
                displayAlert(getString(R.string.alert_no_connection));
            }
       /* }else {
            ((ViewGroup)view.getParent()).removeView(view);
        }*/
        return view;
    }
    private void getUploadedDocsList(String UploadedDocsListUrl) {
        mProgressDialog.showDialog();


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, UploadedDocsListUrl, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Gson gson = new Gson();
                    ApiUploadsDocsHolder responseHolder = gson.fromJson(response.toString(), ApiUploadsDocsHolder.class);
                    Log.d("resp-dependants", response.getJSONArray(API_RESPONSE_RESULT).toString());
                    setupUploadedRecyclerView(responseHolder.getmResult());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mProgressDialog.dismissDialog();

            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("resp-demographic", error.toString());
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
    private void setupUploadedRecyclerView(ArrayList<ApiUploadsDocsHolder.ApiUploadDocInfo> getmResult) {
        mUploadedAdapter =
                new UploadedDocsRecyclerViewAdapter(mMediatorCallback, getmResult, mContext);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(rvOtherDocsList.getContext(),
                layoutManager.getOrientation());
        rvOtherDocsList.addItemDecoration(mDividerItemDecoration);
        rvOtherDocsList.setLayoutManager(layoutManager);
        rvOtherDocsList.setItemAnimator(new DefaultItemAnimator());
        rvOtherDocsList.setAdapter(mUploadedAdapter);

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if ( mUploadedAdapter != null)
            mUploadedAdapter.filter(newText);
        return false;
    }
    private void displayAlert(String msg) {
        rvOtherDocsList.setVisibility(View.GONE);
        tvAlert.setVisibility(View.VISIBLE);
        tvAlert.setText(msg);
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mMediatorCallback.changeFragmentContainerVisibility(View.GONE, View.VISIBLE);
        mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.VISIBLE);
    }
}
