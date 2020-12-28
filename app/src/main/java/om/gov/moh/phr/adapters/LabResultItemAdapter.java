package om.gov.moh.phr.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
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
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiLabOrdersListHolder;
import om.gov.moh.phr.apimodels.ApiLabResultCultureHolder;
import om.gov.moh.phr.apimodels.ApiTextualDataHolder;
import om.gov.moh.phr.apimodels.DLabResultsHolder;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.models.DividerItemDecorator;
import om.gov.moh.phr.models.GlobalMethods;

import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;
import static om.gov.moh.phr.models.MyConstants.API_TEXTUAL_TEMPLATE;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_ARABIC;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_PREFS;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_SELECTED;

public class LabResultItemAdapter extends RecyclerView.Adapter<LabResultItemAdapter.MyViewHolder> {
    private ArrayList<ApiLabOrdersListHolder.LabOrder> labsArrayList;
    private Context context;
    private ArrayList<ApiLabOrdersListHolder.LabOrder> arraylist;
    private String visitDate = "";
    private MediatorInterface mediatorInterface;
    private RequestQueue mQueue;
    private LabResultsDetailsRecyclerViewAdapter labResultsDetailsRecyclerViewAdapter;
    private CultureDataRecyclerViewAdapter cultureDataRecyclerViewAdapter;
    private TextualDataRecyclerViewAdapter textualDataRecyclerViewAdapter;
    private static final String API_URL_GET_LAB_RESULTS_TAMPLATE_D = API_NEHR_URL + "labReport/patient/tabular/";
    private static final String API_URL_GET_LAB_RESULTS_TAMPLATE_C = API_NEHR_URL + "labReport/patient/culture/";
    private static final String API_URL_GET_LAB_RESULTS_TAMPLATE_H = API_NEHR_URL + "labReport/patient/textual/";

    public LabResultItemAdapter(ArrayList<ApiLabOrdersListHolder.LabOrder> labsArrayList, Context context) {
        this.labsArrayList = labsArrayList;
        this.context = context;
        this.arraylist = new ArrayList<ApiLabOrdersListHolder.LabOrder>();
        //this.arraylist.addAll(labsArrayList);
        mediatorInterface = (MediatorInterface) context;

    }

    @NonNull
    @Override
    public LabResultItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_lab_result, parent, false);
        mQueue = Volley.newRequestQueue(context, new HurlStack(null, mediatorInterface.getSocketFactory()));
        return new LabResultItemAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final LabResultItemAdapter.MyViewHolder holder, int position) {
        final ApiLabOrdersListHolder.LabOrder labsEncounter = labsArrayList.get(position);


        holder.labName.setText(labsEncounter.getProcName());


        //Arabic Translation

        if (getStoredLanguage().equals(LANGUAGE_ARABIC)){
            holder.hospital.setText(labsEncounter.getEstFullnameNls());
            holder.moreDetails.setImageBitmap(flipImage());
        }else {
            holder.hospital.setText(labsEncounter.getEstFullname());
            holder.moreDetails.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_right));
        }
        String status = labsEncounter.getStatus();
        switch (status){
            case "completed":
                holder.resultStatus.setBackground(context.getResources().getDrawable(R.drawable.completed));
                break;
            case "rejected":
                holder.resultStatus.setBackground(context.getResources().getDrawable(R.drawable.rejected));
                holder.moreDetails.setVisibility(View.INVISIBLE);
                break;
            case "requested":
                holder.resultStatus.setBackground(context.getResources().getDrawable(R.drawable.requested));
                holder.moreDetails.setVisibility(View.INVISIBLE);
                break;
            case "cancelled":
                holder.resultStatus.setBackground(context.getResources().getDrawable(R.drawable.cancelled));
                holder.moreDetails.setVisibility(View.INVISIBLE);
                break;
            case "accepted":
                holder.resultStatus.setBackground(context.getResources().getDrawable(R.drawable.accepted));
                holder.moreDetails.setVisibility(View.INVISIBLE);
                break;
            case "received":
                holder.resultStatus.setBackground(context.getResources().getDrawable(R.drawable.pending));
                holder.moreDetails.setVisibility(View.INVISIBLE);
                break;
            case "draft":
                holder.resultStatus.setBackground(context.getResources().getDrawable(R.drawable.draft));
                holder.moreDetails.setVisibility(View.INVISIBLE);
                break;
        }

        final  String templateType = labsEncounter.getTemplateType();
        holder.clOrderItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (labsEncounter.getStatus().equals("completed")){
                    if (holder.resultDetailCard.getVisibility() == View.VISIBLE){
                        if (GlobalMethods.getStoredLanguage(context).equals(LANGUAGE_ARABIC))
                            holder.moreDetails.setImageBitmap(flipImage());
                        else {
                            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_arrow_right);
                            holder.moreDetails.setImageBitmap(bitmap);
                        }


                        if (templateType.equals("D")){
                            labResultsDetailsRecyclerViewAdapter.clear();
                            holder.linearLayoutColumns.setVisibility(View.GONE);
                        }
                        else if (templateType.equals("C"))
                            cultureDataRecyclerViewAdapter.clear();
                        else if (templateType.equals("H"))
                            textualDataRecyclerViewAdapter.clear();

                        holder.releasedDate.setVisibility(View.GONE);
                        holder.conclusion.setVisibility(View.GONE);
                        holder.resultDetailCard.setVisibility(View.GONE);
                    }else {
                        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_arrow_down);
                        holder.moreDetails.setImageBitmap(bitmap);
                        if (labsEncounter.getTemplateType().equals("D")){
                            holder.setReportsDetailsForTabular(labsEncounter.getOrderId(),labsEncounter.getPatientId());
                        }else if (labsEncounter.getTemplateType().equals("C")){
                            holder.setReportsDetailsForCulture(labsEncounter.getOrderId(),labsEncounter.getPatientId());
                        }else if (labsEncounter.getTemplateType().equals("H")){
                            holder.setReportsDetailsForTextual(labsEncounter.getOrderId(),labsEncounter.getPatientId());
                        }
                    }

                    //mediatorInterface.changeFragmentTo(LabResultDetailsFragment.newInstance(labsEncounter), labsEncounter.getProcName());
                }

            }
        });






    }

    @Override
    public int getItemCount() {
        return labsArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView labName,hospital,releasedDate,conclusion;
        ImageView resultStatus,moreDetails;
        ConstraintLayout clOrderItem;
        CardView resultDetailCard;
        RecyclerView recyclerViewResult;
        LinearLayout linearLayoutColumns;
        private final AVLoadingIndicatorView avLoadingIndicatorView;

        public MyViewHolder(View view) {
            super(view);
            labName = view.findViewById(R.id.tv_lab_title);
            hospital = view.findViewById(R.id.tv_lab_desc);
            resultStatus = view.findViewById(R.id.img_lab_status);
            moreDetails = view.findViewById(R.id.img_more_details_lab);
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_arrow_right);
            moreDetails.setImageBitmap(bitmap);
            clOrderItem = view.findViewById(R.id.labResult_constraint);
            avLoadingIndicatorView = view.findViewById(R.id.indicatorViewLabResult);
            avLoadingIndicatorView.hide();
            resultDetailCard = view.findViewById(R.id.cardViewLabResultDetail);
            resultDetailCard.setVisibility(View.GONE);

            releasedDate = view.findViewById(R.id.tvReleasedDateLabResult);
            releasedDate.setVisibility(View.GONE);

            conclusion = view.findViewById(R.id.tvConclusionLabResult);
            conclusion.setVisibility(View.GONE);

            recyclerViewResult = view.findViewById(R.id.rvLabResultReport);
            linearLayoutColumns = view.findViewById(R.id.ll_test_columns);
            linearLayoutColumns.setVisibility(View.GONE);
        }

        private void setReportsDetailsForTabular(String orderId, String patientId) {
            //mQueue = Volley.newRequestQueue(context, new HurlStack(null, mediatorInterface.getSocketFactory()));
            avLoadingIndicatorView.smoothToShow();
            String fullUrl = API_URL_GET_LAB_RESULTS_TAMPLATE_D + orderId + "/" + patientId;

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fullUrl, null
                    , new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (context != null) {

                        try {
                            if (response.getInt(API_RESPONSE_CODE) == 0) {
                                avLoadingIndicatorView.smoothToHide();
                                conclusion.setVisibility(View.VISIBLE);
                                releasedDate.setVisibility(View.VISIBLE);
                                resultDetailCard.setVisibility(View.VISIBLE);
                                linearLayoutColumns.setVisibility(View.VISIBLE);

                                Gson gson = new Gson();
                                DLabResultsHolder resultsHolder = gson.fromJson(response.toString(), DLabResultsHolder.class);
                                String objConclusion = resultsHolder.getResult().getConclusion();
                                if (objConclusion.equals("Abnormal"))
                                    conclusion.setTextColor(context.getResources().getColor(R.color.colorRed));
                                else
                                    conclusion.setTextColor(context.getResources().getColor(R.color.colorGreen));
                                conclusion.setText(objConclusion);
                                String objReleasedTime = resultsHolder.getResult().getReleasedTime();

                                releasedDate.setText(context.getResources().getString(R.string.released_date_feild)+" "+objReleasedTime);
                                setupTabualrRecyclerView();
                                updateRecyclerView(resultsHolder.getResult().getTabularData());

                            } else {

                                avLoadingIndicatorView.smoothToHide();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        avLoadingIndicatorView.smoothToHide();

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (context != null) {

                        error.printStackTrace();
                        avLoadingIndicatorView.smoothToHide();

                    }
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
        private void setupTabualrRecyclerView() {
            labResultsDetailsRecyclerViewAdapter = new LabResultsDetailsRecyclerViewAdapter(context);
            RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(context, R.drawable.divider));
            recyclerViewResult.addItemDecoration(dividerItemDecoration);
            recyclerViewResult.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
            recyclerViewResult.setAdapter(labResultsDetailsRecyclerViewAdapter);
        }
        private void updateRecyclerView(ArrayList<DLabResultsHolder.TabularData> items) {
            labResultsDetailsRecyclerViewAdapter.updateItemsList(items);
        }

        private void setReportsDetailsForCulture(String orderId, String patientId) {
            avLoadingIndicatorView.smoothToShow();
            String url = API_URL_GET_LAB_RESULTS_TAMPLATE_C + orderId + "/" + patientId;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null
                    , new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (context != null) {
                        try {
                            if (response.getInt(API_RESPONSE_CODE) == 0) {
                                avLoadingIndicatorView.smoothToHide();
                                conclusion.setVisibility(View.VISIBLE);
                                releasedDate.setVisibility(View.VISIBLE);
                                resultDetailCard.setVisibility(View.VISIBLE);
                                Gson gson = new Gson();
                                ApiLabResultCultureHolder resultsHolder = gson.fromJson(response.toString(), ApiLabResultCultureHolder.class);

                                ApiLabResultCultureHolder.CultureResult obj = resultsHolder.getResult();
                                releasedDate.setText(context.getResources().getString(R.string.released_date_feild) + "   " + obj.getReleasedTime());
                                conclusion.setText(obj.getConclusion());
                                if (obj.getCulture() != null){
                                    setupCultureRecyclerView();
                                    updateCultureRecyclerView(obj.getCulture());
                                }


                                /*if (mApiOderItem.getTemplateType().equals(API_TEXTUAL_TEMPLATE))
                                    updateTextualRecyclerView(obj.getTextualData());*/

                            } else {

                                avLoadingIndicatorView.smoothToHide();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        avLoadingIndicatorView.smoothToHide();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (context != null) {

                        error.printStackTrace();
                        avLoadingIndicatorView.smoothToHide();
                    }
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
        private void setupCultureRecyclerView() {
            cultureDataRecyclerViewAdapter = new CultureDataRecyclerViewAdapter(context);
            RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(context, R.drawable.divider));
            recyclerViewResult.addItemDecoration(dividerItemDecoration);
            recyclerViewResult.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
            recyclerViewResult.setAdapter(cultureDataRecyclerViewAdapter);
        }
        private void updateCultureRecyclerView(ArrayList<ApiLabResultCultureHolder.Culture> items) {
            cultureDataRecyclerViewAdapter.updateItemsList(items);
        }

        private void setReportsDetailsForTextual(String orderId, String patientId) {
            avLoadingIndicatorView.smoothToShow();
            String url = API_URL_GET_LAB_RESULTS_TAMPLATE_H +  orderId + "/" + patientId;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null
                    , new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (context != null ) {
                        try {
                            if (response.getInt(API_RESPONSE_CODE) == 0) {
                                avLoadingIndicatorView.smoothToHide();
                                conclusion.setVisibility(View.VISIBLE);
                                releasedDate.setVisibility(View.VISIBLE);
                                resultDetailCard.setVisibility(View.VISIBLE);
                                Gson gson = new Gson();
                                ApiTextualDataHolder resultsHolder = gson.fromJson(response.toString(), ApiTextualDataHolder.class);
                                ApiTextualDataHolder.TextualLabResult obj = resultsHolder.getResult();
                                releasedDate.setText(context.getResources().getString(R.string.released_date_feild) + "   " + obj.getReleasedTime());
                                conclusion.setText(obj.getConclusion());
                                setupTextualRecyclerView();
                                updateTextualRecyclerView(obj.getTextualData());



                            } else {
                                avLoadingIndicatorView.smoothToHide();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        avLoadingIndicatorView.smoothToHide();

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (context != null) {

                        error.printStackTrace();
                        avLoadingIndicatorView.smoothToHide();

                    }
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
        private void setupTextualRecyclerView() {
            textualDataRecyclerViewAdapter = new TextualDataRecyclerViewAdapter(context);
            //RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(context, R.drawable.divider));
            //recyclerViewResult.addItemDecoration(dividerItemDecoration);
            recyclerViewResult.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
            recyclerViewResult.setAdapter(textualDataRecyclerViewAdapter);
        }
        private void updateTextualRecyclerView(ArrayList<ApiTextualDataHolder.TextualDataResult> items) {
            textualDataRecyclerViewAdapter.updateItemsList(items);
        }

    }



    private String getStoredLanguage() {
        SharedPreferences sharedPref = context.getSharedPreferences(LANGUAGE_PREFS, Context.MODE_PRIVATE);
        return sharedPref.getString(LANGUAGE_SELECTED, getDeviceLanguage());
    }
    private String getDeviceLanguage() {
        return Locale.getDefault().getLanguage();
    }

    private Bitmap flipImage() {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_arrow_right);
// create new matrix for transformation
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);

        Bitmap flipped_bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        return flipped_bitmap;

    }



}