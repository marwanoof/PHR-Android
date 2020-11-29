package om.gov.moh.phr.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
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
import om.gov.moh.phr.apimodels.ApiMedicationHolder;
import om.gov.moh.phr.apimodels.ApiProceduresReportsHolder;
import om.gov.moh.phr.fragments.HealthRecordDetailsFragment;
import om.gov.moh.phr.fragments.ProceduresReportsDetailsFragment;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.models.DividerItemDecorator;
import om.gov.moh.phr.models.MyProgressDialog;

import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;

public class ProceduresReportsRecyclerView extends RecyclerView.Adapter<ProceduresReportsRecyclerView.MyViewHolder> {
    private ArrayList<ApiProceduresReportsHolder.ProceduresByEncounter> proceduresReportArrayList;
    private Context context;
    private ArrayList<ApiProceduresReportsHolder.ProceduresByEncounter> arraylist;
    private MediatorInterface mediatorInterface;
    private ArrayList<ProceduresReportsDetailsFragment.ReportData> textReport;
    private boolean isTrue;
    private boolean isRad;
    private boolean showVisitDate;
    private ProceduresAdapterItem proceduresAdapterItem;
    private MediatorInterface mMediatorCallback;
    private RequestQueue mQueue;
    private MyProgressDialog mProgressDialog;
   // private int row_index = -1;
    public ProceduresReportsRecyclerView(){}
    public ProceduresReportsRecyclerView(ArrayList<ProceduresReportsDetailsFragment.ReportData> arrayList, Context mContext) {
        this.textReport = arrayList;
        this.context = mContext;
        isTrue = true;
    }

    public ProceduresReportsRecyclerView(MediatorInterface mMediatorCallback, ArrayList<ApiProceduresReportsHolder.ProceduresByEncounter> reportsList, Context context, boolean isRad ,boolean showVisitDate) {
        this.proceduresReportArrayList = reportsList;
        this.context = context;
        this.arraylist = new ArrayList<ApiProceduresReportsHolder.ProceduresByEncounter>();
        this.arraylist.addAll(reportsList);
        this.mediatorInterface = mMediatorCallback;
        this.isTrue = false;
        this.isRad = isRad;
        this.showVisitDate = showVisitDate;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_procedures, parent, false);
        mMediatorCallback = (MediatorInterface) context;
        if (isTrue){
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_procedures_details, parent, false);
        }else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_procedures_encounter, parent, false);
        }

        return new MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {



        if (isTrue) {

           /* ProceduresReportsDetailsFragment.ReportData reportData = textReport.get(position);
            holder.tvContentDetails.setTypeface(null,Typeface.NORMAL);
            holder.tvContentDetails.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);

            SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy \n hh:mm:ss", Locale.US);
            if(reportData.getReportTime()==null)
                holder.tvDateDetails.setVisibility(View.INVISIBLE);
            else {
                String dateReport = df2.format(reportData.getReportTime());
                holder.tvDateDetails.setText(dateReport);
            }*/
//            holder.tvEstName.setVisibility(View.INVISIBLE);
//            holder.moreDetailArrow.setVisibility(View.GONE);
//            holder.tvEstName.setVisibility(View.INVISIBLE);

        } else {

            final ApiProceduresReportsHolder.ProceduresByEncounter proceduresEncounter = proceduresReportArrayList.get(position);
            ArrayList<ApiProceduresReportsHolder.Procedures> proceduresArrayList = proceduresEncounter.getProcedures();

            if (showVisitDate){
                holder.encounterDate.setVisibility(View.VISIBLE);
                holder.visitDetailsBtn.setVisibility(View.VISIBLE);
            }else {
                holder.encounterDate.setVisibility(View.GONE);
                holder.visitDetailsBtn.setVisibility(View.GONE);
            }
            holder.encounterDate.setText(proceduresEncounter.getEncounterDate());

            proceduresAdapterItem = new ProceduresAdapterItem(proceduresArrayList,context,isRad);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
            RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(context, R.drawable.divider));
            holder.recyclerView.addItemDecoration(dividerItemDecoration);
            holder.recyclerView.setLayoutManager(layoutManager);
            holder.recyclerView.setItemAnimator(new DefaultItemAnimator());
            holder.recyclerView.setAdapter(proceduresAdapterItem);

            holder.visitDetailsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getEncounterResponse(proceduresEncounter.getEncounterId());
                }
            });


            /*
            holder.moreDetailArrow.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams = holder.cardView.getLayoutParams();
            layoutParams.height = 150;
            //holder.cardView.setLayoutParams(layoutParams);
            final ApiProceduresReportsHolder procedureObj = proceduresReportArrayList.get(position);
            if (isRad) {
                holder.tvProcedureName.setText(procedureObj.getProcName());
                Date date = new Date(procedureObj.getProcedureDoneDate());
                SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
                String dateText = df2.format(date);
                holder.tvDateWritten.setText(dateText);
                holder.tvEstName.setText(procedureObj.getEstName());
                holder.tvDate.setVisibility(View.GONE);
                holder.ivMoreArrow.setVisibility(View.GONE);

            } else {

                holder.tvProcedureName.setText(procedureObj.getName());
                holder.tvEstName.setText(procedureObj.getEstName());
                Date date = new Date(procedureObj.getStartTime());
                SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
                String dateText = df2.format(date);
                holder.tvDateWritten.setText(dateText);
                if(procedureObj.getEncounterId()!=null) {
                SimpleDateFormat dateFormatGroupedDate = new SimpleDateFormat("dd-MM-YYYY", Locale.ENGLISH);
                long encounterDate = procedureObj.getEncounterDate();
                if (position != 0) {
                    int prev = position - 1;
                    long prevEncounterDate = proceduresReportArrayList.get(prev).getEncounterDate();
                    if (dateFormatGroupedDate.format(new Date(encounterDate)).equals(dateFormatGroupedDate.format(new Date(prevEncounterDate)))) {
                        holder.tvDate.setVisibility(View.GONE);
                        holder.ivMoreArrow.setVisibility(View.GONE);
                    } else
                        holder.tvDate.setText(context.getResources().getString(R.string.visit_date)+ ": " + dateFormatGroupedDate.format(new Date(encounterDate)));
                } else
                    holder.tvDate.setText(context.getResources().getString(R.string.visit_date)+ ": " +dateFormatGroupedDate.format(new Date(encounterDate)));
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mediatorInterface.changeFragmentTo(HealthRecordDetailsFragment.newInstance(procedureObj), procedureObj.getEstFullname());
                        }
                    });
                }else {
                    holder.tvDate.setVisibility(View.GONE);
                    holder.ivMoreArrow.setVisibility(View.GONE);
                }
            }
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  //  row_index = position;
                   // notifyDataSetChanged();
                    if (isRad)
                        mediatorInterface.changeFragmentTo(ProceduresReportsDetailsFragment.newInstance(procedureObj, "RAD"), procedureObj.getName());
                    else
                        mediatorInterface.changeFragmentTo(ProceduresReportsDetailsFragment.newInstance(procedureObj), procedureObj.getName());
                }
            });*/
//            if (row_index == position) {
//
//                holder.clOrderItem.setBackgroundColor(context.getResources().getColor(R.color.colorPeach));
//            } else {
//
//                holder.clOrderItem.setBackgroundColor(context.getResources().getColor(R.color.colorWhite));
//            }
        }
    }

    @Override
    public int getItemCount() {
        if (isTrue)
            return textReport.size();
        else
            return proceduresReportArrayList.size();
    }
    public void updateItemsList() {
        //medicineArrayList = items;
        notifyDataSetChanged();
    }
    public void updateItemsListFiltered(ArrayList<ApiProceduresReportsHolder.ProceduresByEncounter> items) {
        proceduresReportArrayList = items;
        notifyDataSetChanged();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView encounterDate;
        Button visitDetailsBtn;
        RecyclerView recyclerView;

        /*TextView tvProcedureName, tvDosage, tvDateWritten, tvEstName, tvContentDetails, tvDateDetails, tvDate;
        ConstraintLayout clOrderItem;
        ImageView moreDetailArrow, ivMoreArrow;
        CardView cardView;*/

        //ImageButton imageButton;

        public MyViewHolder(View view) {
            super(view);

            encounterDate = view.findViewById(R.id.tv_encounter_date_proc);
            recyclerView = view.findViewById(R.id.rv_proc_encounter);
            visitDetailsBtn = view.findViewById(R.id.btn_visit_details_proc);

            /*tvProcedureName = view.findViewById(R.id.tv_name_proc);
            tvDateWritten = view.findViewById(R.id.tv_date_proc);
            tvEstName = view.findViewById(R.id.tv_hospital_proc);
            moreDetailArrow = view.findViewById(R.id.img_arrowDetails);
            cardView = view.findViewById(R.id.cardView_procContent);
            tvContentDetails = view.findViewById(R.id.tv_content_proc_details);
            tvDateDetails = view.findViewById(R.id.tv_date_proc_details);
            ivMoreArrow = view.findViewById(R.id.iv_moreArrow);
            tvDate = view.findViewById(R.id.tvDate);*/
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
