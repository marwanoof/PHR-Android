package om.gov.moh.phr.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiEncountersHolder;
import om.gov.moh.phr.apimodels.ApiLabOrdersListHolder;
import om.gov.moh.phr.apimodels.ApiMedicationHolder;
import om.gov.moh.phr.fragments.HealthRecordDetailsFragment;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.models.MyProgressDialog;

import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;

public class MedicationRecyclerViewAdapter extends RecyclerView.Adapter<MedicationRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<ApiMedicationHolder.ApiMedicationInfo> medicineArrayList;
    private Context context;
    private ArrayList<ApiMedicationHolder.Medication> arraylist;
    private String visitDate = "";
    private MedicineItemAdapter medicineItemAdapter;
    private MediatorInterface mMediatorCallback;
    private RequestQueue mQueue;
    private MyProgressDialog mProgressDialog;
    private Boolean showVisitDetails;
    public MedicationRecyclerViewAdapter(ArrayList<ApiMedicationHolder.ApiMedicationInfo> medicineArrayList,
                                         Context context,Boolean showVisitDetails) {
        this.medicineArrayList = medicineArrayList;
        this.context = context;
        this.arraylist = new ArrayList<ApiMedicationHolder.Medication>();
        for (ApiMedicationHolder.ApiMedicationInfo item : medicineArrayList){
            this.arraylist.addAll(item.getMedication());
        }
        this.showVisitDetails = showVisitDetails;
        //this.arraylist.addAll(medicineArrayList.get().getMedication());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_medication_encounter, parent, false);
        mMediatorCallback = (MediatorInterface) context;
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ApiMedicationHolder.ApiMedicationInfo medicineEncounter = medicineArrayList.get(position);
        ArrayList<ApiMedicationHolder.Medication> medicinesArray = medicineEncounter.getMedication();
        //ApiMedicationHolder.Medication medicineObj = medicinesArray.get(position);
        if (showVisitDetails){
            holder.encounterDate.setVisibility(View.VISIBLE);
            holder.visitDetailsBtn.setVisibility(View.VISIBLE);
        }else {
            holder.encounterDate.setVisibility(View.GONE);
            holder.visitDetailsBtn.setVisibility(View.GONE);
        }
        holder.encounterDate.setText(medicineEncounter.getEncounterDate());

        medicineItemAdapter = new MedicineItemAdapter(medicinesArray,context);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        holder.recyclerView.setLayoutManager(layoutManager);
        holder.recyclerView.setItemAnimator(new DefaultItemAnimator());
        holder.recyclerView.setAdapter(medicineItemAdapter);

        holder.visitDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getEncounterResponse(medicineEncounter.getEncounterId());
            }
        });

        //SimpleDateFormat dateFormatGroupedDate = new SimpleDateFormat("dd-MM-YYYY", Locale.ENGLISH);
        /*if (position != 0) {
            int prev = position - 1;
            long prevEncounterDate = medicineArrayList.get(prev).getDateWrittern();
            if (dateFormatGroupedDate.format(new Date(medicineObj.getDateWrittern())).equals(dateFormatGroupedDate.format(new Date(prevEncounterDate)))) {
                holder.tvDateWritten.setVisibility(View.GONE);
            } else {
                holder.tvDateWritten.setVisibility(View.VISIBLE);
                holder.tvDateWritten.setText(context.getResources().getString(R.string.date_label)+ " " + dateFormatGroupedDate.format(new Date(medicineObj.getDateWrittern())));
            }
        } else {
            holder.tvDateWritten.setVisibility(View.VISIBLE);
            holder.tvDateWritten.setText(context.getResources().getString(R.string.date_label) + " " +dateFormatGroupedDate.format(new Date(medicineObj.getDateWrittern())));
        }*/


    }

    @Override
    public int getItemCount() {
        return medicineArrayList.size();
    }
    public void updateItemsList() {
        //medicineArrayList = items;
        notifyDataSetChanged();
    }
    public void updateItemsListFiltered(ArrayList<ApiMedicationHolder.ApiMedicationInfo> items) {
        medicineArrayList = items;
        notifyDataSetChanged();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView encounterDate;
        Button visitDetailsBtn;
        RecyclerView recyclerView;

        public MyViewHolder(View view) {
            super(view);
            encounterDate = view.findViewById(R.id.tv_encounter_date);
            recyclerView = view.findViewById(R.id.rv_medication_encounter);
            visitDetailsBtn = view.findViewById(R.id.btn_visit_details_med);
            //ibImageButton.setVisibility(View.GONE);
        }
    }

    private void getEncounterResponse(final String encounterId) {
        //ApiEncountersHolder.Encounter filteredEncounter;
        mQueue = Volley.newRequestQueue(context, new HurlStack(null, mMediatorCallback.getSocketFactory()));
        mProgressDialog = new MyProgressDialog(context);// initializes progress dialog
        mProgressDialog.showDialog();
        String fullUrl = API_NEHR_URL + "encounter/v2/civilId";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, fullUrl, getJSONRequestParams(mMediatorCallback.getCurrentUser().getCivilId(),"PHR")
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                    try {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {
                            Gson gson = new Gson();

                            ApiEncountersHolder responseHolder = gson.fromJson(response.toString(), ApiEncountersHolder.class);


                            ApiEncountersHolder.Encounter encounterInfo = null;

                            for (ApiEncountersHolder.Encounter encounter: responseHolder.getResult()){
                                if (encounter.getEncounterId().equals(encounterId)){

                                    encounterInfo = encounter;
                                }
                            }
                            if (encounterInfo == null){
                                    displayAlert(context.getResources().getString(R.string.no_visit_details));
                            }else {
                                mMediatorCallback.changeFragmentTo(HealthRecordDetailsFragment.newInstance(encounterInfo),"HeathRecordsDetails");
                            }

                        } else {
                            //displayAlert(getResources().getString(R.string.no_record_found), View.VISIBLE, View.GONE);
                            mProgressDialog.dismissDialog();
                        }
                    } catch (JSONException e) {
//                    Log.d("enc", e.getMessage());

                        e.printStackTrace();
                    }

                    mProgressDialog.dismissDialog();
                    // showing refresh animation before making http call


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                    mProgressDialog.dismissDialog();
                    error.printStackTrace();

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
    private JSONObject getJSONRequestParams(String civilId, String source) {
        Map<String, Object> params = new HashMap<>();
        params.put("civilId", Long.parseLong(civilId));
        params.put("source", source);
        return new JSONObject(params);
    }

    private void displayAlert(String msg){
        android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(context);
        builder1.setMessage(msg);
        builder1.setCancelable(false);

        builder1.setNegativeButton(
                context.getResources().getString(R.string.no_dialog),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        android.app.AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
