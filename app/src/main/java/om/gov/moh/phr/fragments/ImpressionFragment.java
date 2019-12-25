package om.gov.moh.phr.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import om.gov.moh.phr.R;
import om.gov.moh.phr.adapters.HRDRecyclerViewAdapter;
import om.gov.moh.phr.apimodels.ApiEncountersHolder;
import om.gov.moh.phr.apimodels.Immpression;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.MyProgressDialog;

import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;

public class ImpressionFragment extends Fragment {
    private Context mContext;
    private MediatorInterface mMediatorCallback;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private static final String ARG_PARAM1 = "ARG_PARAM1";
    private ApiEncountersHolder.Encounter encounterInfo;
    private RecyclerView recyclerView;
    private MyProgressDialog mProgressDialog;
    private RequestQueue mQueue;
    private static final String API_URL_GET_IMMPRESSION_INFO = API_NEHR_URL + "clinicalNotes/encounter/";
    private TextView tvDate, tvTime;

    public static ImpressionFragment newInstance(ApiEncountersHolder.Encounter encounterObj) {
        ImpressionFragment fragment = new ImpressionFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, encounterObj);
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
        if (getArguments() != null)
            encounterInfo = (ApiEncountersHolder.Encounter) getArguments().getSerializable(ARG_PARAM1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View parentView = inflater.inflate(R.layout.details_imp, container, false);
        mProgressDialog = new MyProgressDialog(mContext);
        mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
        TextView tvTitle = parentView.findViewById(R.id.tv_title);
        ImageButton ibToolbarBackButton = parentView.findViewById(R.id.ib_toolbar_back_button);
        ibToolbarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediatorCallback.changeFragmentTo(HealthRecordListFragment.newInstance(), encounterInfo.getEstFullname());
               // mToolbarControllerCallback.customToolbarBackButtonClicked();
            }
        });
        ImageButton ibHome = parentView.findViewById(R.id.ib_home);
        ibHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToHome();
            }
        });
        TextView tvPatientClass = parentView.findViewById(R.id.tv_patient_class);
        TextView tvEstName = parentView.findViewById(R.id.tv_estName);
        tvDate = parentView.findViewById(R.id.tv_month);
        tvTime = parentView.findViewById(R.id.tv_time);
        recyclerView = parentView.findViewById(R.id.recycler_view);
        tvTitle.setText(encounterInfo.getDepartmentArrayList().get(0) + ", " + encounterInfo.getEstShortName());
        tvPatientClass.setText(encounterInfo.getPatientClass());
        tvEstName.setText(encounterInfo.getEstShortName());
        String url = API_URL_GET_IMMPRESSION_INFO + encounterInfo.getEncounterId()+"?source=PHR";
        getImmpressionNotes(url);
        return parentView;
    }
    private void backToHome() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    private void getImmpressionNotes(String url) {
        mProgressDialog.showDialog();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt(API_RESPONSE_CODE) == 0) {

                        JSONObject jsonObject = response.getJSONArray("result").getJSONObject(0);
                        long noteDate = jsonObject.getLong("noteDate");
                        Date date = new Date(noteDate);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        String NoteDate = dateFormat.format(date);
                        tvDate.setText(NoteDate);
                        SimpleDateFormat ClockFormat = new SimpleDateFormat("hh:mm:ss aa", Locale.US);
                        String NoteTime = ClockFormat.format(date);
                        tvTime.setText(NoteTime);
                        ArrayList<Immpression> immpressionNotes = new ArrayList<>();
                        JSONArray noteslist = jsonObject.getJSONArray("dto");
                        for (int i =0; i<noteslist.length(); i++){
                            Immpression imp = new Immpression();
                            imp.setNoteTitle(noteslist.getJSONObject(i).getString("noteTitle"));
                            imp.setNoteText(noteslist.getJSONObject(i).getString("notes"));
                            immpressionNotes.add(imp);
                        }
                        setupRecyclerView(immpressionNotes);

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
    private void setupRecyclerView(ArrayList<Immpression> getmResult) {
        HRDRecyclerViewAdapter mAdapter = new HRDRecyclerViewAdapter(getmResult, mContext);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(mDividerItemDecoration);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }
}
