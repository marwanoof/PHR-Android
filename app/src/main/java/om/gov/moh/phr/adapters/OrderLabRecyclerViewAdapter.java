package om.gov.moh.phr.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
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

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiEncountersHolder;
import om.gov.moh.phr.apimodels.ApiLabOrdersListHolder;
import om.gov.moh.phr.apimodels.ApiMedicationHolder;
import om.gov.moh.phr.fragments.HealthRecordDetailsFragment;
import om.gov.moh.phr.fragments.LabResultDetailsFragment;
import om.gov.moh.phr.fragments.LabResultsFragment;
import om.gov.moh.phr.interfaces.AdapterToFragmentConnectorInterface;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.models.DividerItemDecorator;
import om.gov.moh.phr.models.MyProgressDialog;

import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;

public class OrderLabRecyclerViewAdapter extends RecyclerView.Adapter<OrderLabRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<ApiLabOrdersListHolder.ApiOredresList> labOrdersArrayList;

    private Context context;
    private ArrayList<ApiLabOrdersListHolder.ApiOredresList> arraylist = new ArrayList<>();
    private MediatorInterface mediatorInterface;
    private LabResultItemAdapter labResultItemAdapter;
    private RequestQueue mQueue;
    private MyProgressDialog mProgressDialog;
    private Boolean showVisitDetails;
   // private int row_index = -1;
    //private int clickCount = 0;

    public OrderLabRecyclerViewAdapter(){}
    public OrderLabRecyclerViewAdapter(ArrayList<ApiLabOrdersListHolder.ApiOredresList> labOrdersArrayList,
                                       MediatorInterface mMediatorCallback, Context context,Boolean showVisitDetails) {
        this.context = context;
        this.labOrdersArrayList = labOrdersArrayList;
        this.mediatorInterface = mMediatorCallback;
        this.showVisitDetails = showVisitDetails;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_lab_encounter, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

            final ApiLabOrdersListHolder.ApiOredresList orderObj = labOrdersArrayList.get(position);
            ArrayList<ApiLabOrdersListHolder.LabOrder> labsArray = orderObj.getLabOrders();
            //ApiMedicationHolder.Medication medicineObj = medicinesArray.get(position);
            if (showVisitDetails) {
                holder.encounterDate.setVisibility(View.VISIBLE);
                holder.visitDetailsBtn.setVisibility(View.VISIBLE);
            } else {
                holder.encounterDate.setVisibility(View.GONE);
                holder.visitDetailsBtn.setVisibility(View.GONE);
            }

            holder.encounterDate.setText(orderObj.getEncounterDate());

            labResultItemAdapter = new LabResultItemAdapter(labsArray, context);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
            holder.recyclerView.setLayoutManager(layoutManager);
            RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(context, R.drawable.divider));
            holder.recyclerView.addItemDecoration(dividerItemDecoration);
            holder.recyclerView.setItemAnimator(new DefaultItemAnimator());
            holder.recyclerView.setAdapter(labResultItemAdapter);

            holder.visitDetailsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getEncounterResponse(orderObj.getEncounterId());
                }
            });



        /*if(orderObj.getEncounterDate()!=null) {
            long encounterDate = orderObj.getEncounterDate();
            SimpleDateFormat dateFormatGroupedDate = new SimpleDateFormat("dd-MM-YYYY", Locale.ENGLISH);
            if (position != 0) {
                int prev = position - 1;
                long prevEncounterDate = labOrdersArrayList.get(prev).getEncounterDate();
                if (dateFormatGroupedDate.format(new Date(encounterDate)).equals(dateFormatGroupedDate.format(new Date(prevEncounterDate)))) {
                    holder.tvDate.setVisibility(View.GONE);
                } else {
                    holder.tvDate.setVisibility(View.VISIBLE);
                    holder.tvDate.setText(context.getResources().getString(R.string.visit_date) + ": " + dateFormatGroupedDate.format(new Date(encounterDate)));
                }
                } else {
                holder.tvDate.setVisibility(View.VISIBLE);
                holder.tvDate.setText(context.getResources().getString(R.string.visit_date) + ": " + dateFormatGroupedDate.format(new Date(encounterDate)));
            }
        }
        //holder.tvStatus.setText(orderObj.getStatus());
        LabResultDetailsFragment.newInstance(orderObj);
        Date date = new Date(orderObj.getOrderDate());
        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy \n hh:mm");
        String dateText = df2.format(date);
        holder.tvOrderDate.setText(dateText);
        holder.tvEstName.setText(orderObj.getEstName());*/

    }

    @Override
    public int getItemCount() {
        return labOrdersArrayList.size();
    }

    public void updateItemsList(ArrayList<ApiLabOrdersListHolder.ApiOredresList> items) {
        labOrdersArrayList = items;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView encounterDate, noRecords;
        Button visitDetailsBtn;
        RecyclerView recyclerView;

        public MyViewHolder(View view) {
            super(view);
            encounterDate = view.findViewById(R.id.tv_encounter_date_lab);
            recyclerView = view.findViewById(R.id.rv_lab_encounter);
            visitDetailsBtn = view.findViewById(R.id.btn_visit_details_lab);
            noRecords = view.findViewById(R.id.tv_alert2);
            noRecords.setVisibility(View.GONE);
            //ibImageButton.setVisibility(View.GONE);
        }
        /*TextView tvProcName, tvStatus, tvOrderDate, tvEstName, tvDate;
        ConstraintLayout clOrderItem;
        ImageButton imageButton;
        CardView labDetails_card;

        public MyViewHolder(View view) {
            super(view);
            tvProcName = view.findViewById(R.id.tv_lab_title);
            //tvStatus = view.findViewById(R.id.tv_lab_desc);
            tvOrderDate = view.findViewById(R.id.tv_section_title_lab);
            tvEstName = view.findViewById(R.id.tv_lab_desc);
            clOrderItem = view.findViewById(R.id.labResult_constraint);
            imageButton=view.findViewById(R.id.btn_lab_status);
            tvDate = view.findViewById(R.id.tvDate);

        }*/
    }
    private void getEncounterResponse(final String encounterId) {
        //ApiEncountersHolder.Encounter filteredEncounter;
        mQueue = Volley.newRequestQueue(context, new HurlStack(null, mediatorInterface.getSocketFactory()));
        mProgressDialog = new MyProgressDialog(context);// initializes progress dialog
        mProgressDialog.showDialog();
        String fullUrl = API_NEHR_URL + "encounter/v2/civilId";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, fullUrl, getJSONRequestParams(mediatorInterface.getCurrentUser().getCivilId(),"PHR")
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
                            mediatorInterface.changeFragmentTo(HealthRecordDetailsFragment.newInstance(encounterInfo),"HeathRecordsDetails");
                        }

                    } else {
                        //displayAlert(getResources().getString(R.string.no_record_found), View.VISIBLE, View.GONE);
                        mProgressDialog.dismissDialog();
                    }
                } catch (JSONException e) {

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
                headers.put("Authorization", API_GET_TOKEN_BEARER + mediatorInterface.getAccessToken().getAccessTokenString());

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
    // Filter Class
 /*   public void filter(String charText) {

        this.arraylist.addAll(labOrdersArrayList);
        charText = charText.toLowerCase(Locale.getDefault());
        labOrdersArrayList.clear();
        if (charText.length() == 0) {
            labOrdersArrayList.addAll(arraylist);
        } else {
            for (ApiLabOrdersListHolder.ApiOredresList wp : arraylist) {
                if (wp.getProcName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    labOrdersArrayList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }*/

}
