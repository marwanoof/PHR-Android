package om.gov.moh.phr.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiHomeHolder;
import om.gov.moh.phr.apimodels.ApiVitalPivotV2;
import om.gov.moh.phr.fragments.BodyMeasurementsFragment;
import om.gov.moh.phr.interfaces.AdapterToFragmentConnectorInterface;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.models.FiguresMarkerView;
import om.gov.moh.phr.models.GlobalMethodsKotlin;
import om.gov.moh.phr.models.MyAxisValueFormatter;
import om.gov.moh.phr.models.MyFillFormatter;

import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_MESSAGE;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_ARABIC;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_ENGLISH;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_PREFS;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_SELECTED;

public class MeasurementRecyclerViewAdapter extends
        RecyclerView.Adapter<MeasurementRecyclerViewAdapter.MyViewHolder> {
    private static final String API_URL_GET_VITAL_DETAILS = API_NEHR_URL + "vitalSigns/vitalDatewise";
    private ArrayList<ApiHomeHolder.ApiRecentVitals> mItemsArrayList;
    private Context mContext;
    private AdapterToFragmentConnectorInterface mCallback;
    private int mColorIndex;
    private MediatorInterface mMediatorCallback;
    private ArrayList<ApiVitalPivotV2.VitalRecord> results;
    private RequestQueue mQueue;
    private  VitalPivotV2Adapter mAdapter;

    //the constructor of the LastRecordsRecyclerViewAdapter
    public MeasurementRecyclerViewAdapter(AdapterToFragmentConnectorInterface fragment, Context context, ArrayList<ApiHomeHolder.ApiRecentVitals> items) {
        this.mItemsArrayList = items;
        this.mContext = context;
        mCallback = fragment;
    }

    //onCreateViewHolder allows you to inflate the "List items view"
//also allows to make some action during once the list created
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_body_measurements_list_item, parent, false);
        mMediatorCallback = (MediatorInterface) mContext;
        mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
        return new MyViewHolder(itemView);
    }

    //onBindViewHolder , allows you to write the data into the fields
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final ApiHomeHolder.ApiRecentVitals result = mItemsArrayList.get(position);

        //in order to get color correctly add 0xff at the beginning
        holder.vRoundRect.setBackground(new DrawableGradient(getColorsArray(position)));

        String value = result.getValue();
        if (getStoredLanguage().equals(LANGUAGE_ARABIC)) {
            holder.tvTitle.setText(result.getVitalNameNls());
            holder.tvUnit.setText(result.getUnitNls());
        } else {
            holder.tvTitle.setText(result.getName());
            holder.tvUnit.setText(result.getUnit());
        }
        holder.tvValue.setText(value);
        holder.tvDate.setText(result.getVitalDate());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.vitalDetails.getVisibility() == View.VISIBLE){
                    holder.vitalDetails.setVisibility(View.GONE);
                }else {

                    holder.getVitalRecords(result.getType());


                }

               // mCallback.onMyListItemClicked(result, result.getName(), position);
            }
        });

        switch (result.getName()){
            case "Heart rate":
                holder.imgIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_v_heart));
                break;
            case "Oxygen saturation":
                holder.imgIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_v_oxygen));
                break;
            case "Body height":
                holder.imgIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_v_height));
                break;
            case "Body temperature":
                holder.imgIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_v_temp));
              //  holder.tvUnit.setText("\u2103");
                break;
            case "Respiratory rate":
                holder.imgIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_v_respiratory));
                break;
            case "Diastolic blood pressure":
                holder.imgIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_v_blood_low));
                break;
            case "Systolic blood pressure":
                holder.imgIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_v_blood_up));
                break;
            case "Weight Measured":
                holder.imgIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_v_weight));
                break;
            case "Blood pressure":
                holder.imgIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_v_blood_pressure));
                break;
        }
    }


    @Override
    public int getItemCount() {
        return mItemsArrayList.size();
    }


    //the requirements is to have display each item with a specific color, if items are more than the colors repeat the colors\
    // item color is gradient from left to right.

    private int[] getColorsArray(int i) {
/**
 Body measurement
 Gradient direction - horizontal
 Hexadecimal Codes
 Height  -      #fe9a3a    #ffb953
 weight  -      #ff5581    #ff6d97
 BMI       -    #f749b8    #f767e3
 BP         -   #30cfb9    #37e6af
 RR         -   #52a9ff    #52c7ff
 Temp    -      #6054ff    #536bff
 */
        int[] leftColors = {0xff30cfb9,
                0xff52a9ff,
                0xffff5581,
                0xfffe9a3a,
                0xfff749b8,
                0xffaf39ee,
                0xff6054ff};
        int[] rightColors = {0xff37e6af,
                0xff52c7ff,
                0xffff6d97,
                0xffffb953,
                0xfff767e3,
                0xffdcd3ea,
                0xff536bff};

        if (mColorIndex > leftColors.length - 1) {
            mColorIndex = 0;
        }

        int[] result = {leftColors[mColorIndex], rightColors[mColorIndex]};
        mColorIndex++;

        return result;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final CardView vRoundRect;
        private final CardView vitalDetails;
        private final TextView tvTitle, tvDate;
        private final TextView tvValue;
        private final TextView tvUnit;
        private final ImageView imgIcon;
        private final RecyclerView rvVitalDetails;
        private final LineChart lineChart;
        private final AVLoadingIndicatorView avLoadingIndicatorView;

        public MyViewHolder(View view) {
            super(view);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvValue = itemView.findViewById(R.id.tv_value);
            tvUnit = itemView.findViewById(R.id.tv_unit);
            tvDate = itemView.findViewById(R.id.tv_date);
            vRoundRect = itemView.findViewById(R.id.bodyM_cardView);
            imgIcon = itemView.findViewById(R.id.img_vitel);
            vitalDetails = itemView.findViewById(R.id.vitelDetails_cardView);
            rvVitalDetails = itemView.findViewById(R.id.rv_vitalV2);
            vitalDetails.setVisibility(View.GONE);
            lineChart = itemView.findViewById(R.id.lineChart);
            avLoadingIndicatorView = itemView.findViewById(R.id.indicatorViewVital);
            avLoadingIndicatorView.hide();
            //avLoadingIndicatorView.setVisibility(View.GONE);
        }

        private void getVitalRecords(String code) {
            avLoadingIndicatorView.smoothToShow();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, API_URL_GET_VITAL_DETAILS, getJSONRequestCivilIDParam(code)
                    , new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {
                            Gson gson = new Gson();
                            ApiVitalPivotV2 responseHolder = gson.fromJson(response.toString(), ApiVitalPivotV2.class);

                            if (mContext != null) {
                                avLoadingIndicatorView.smoothToHide();
                                vitalDetails.setVisibility(View.VISIBLE);
                                vitalDetails.setAnimation(GlobalMethodsKotlin.Companion.setAnimation(mContext,R.anim.fade_in));
                                results = responseHolder.getResult().getVitalRecord();
                                setupRecyclerView(results);
                                lineChartSettings();
                                if (responseHolder.getResult().getVitalSign().equals("Blood pressure")){
                                    setVitalChartBloodPressure(results);
                                }else {
                                    setVitalChart(results);
                                }

                            }

                        } else {
                            avLoadingIndicatorView.smoothToHide();
                            Toast.makeText(mContext, mContext.getResources().getString(R.string.msg_no_vital), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        avLoadingIndicatorView.smoothToHide();
                        e.printStackTrace();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (mContext != null) {
                        avLoadingIndicatorView.smoothToHide();

                        error.printStackTrace();

                    }
                }
            }) {
                //
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
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

        private JSONObject getJSONRequestCivilIDParam(String code) {
            Map<String, Object> params = new HashMap<>();
            params.put("civilId", mMediatorCallback.getCurrentUser().getCivilId());
            params.put("data", code);
            return new JSONObject(params);
        }

        private void setupRecyclerView(ArrayList<ApiVitalPivotV2.VitalRecord> arrayList) {
            mAdapter = new VitalPivotV2Adapter(mContext, arrayList);

            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);

            rvVitalDetails.setLayoutManager(layoutManager);
            rvVitalDetails.setItemAnimator(new DefaultItemAnimator());
            rvVitalDetails.setAdapter(mAdapter);
        }

        private void lineChartSettings() {
            // no description text
            lineChart.getDescription().setEnabled(false);

            //lineChart.setNoDataText(getString(R.string.loading_msg));
            //lineChart.setNoDataTextColor(getResources().getColor(R.color.infected_color));
            // enable touch gestures
            lineChart.setTouchEnabled(true);

            lineChart.setDragDecelerationFrictionCoef(0.9f);

            // enable scaling and dragging
            lineChart.setDragEnabled(true);
            lineChart.setScaleEnabled(true);
            lineChart.setDrawGridBackground(false);
            lineChart.setHighlightPerDragEnabled(true);


            // if disabled, scaling can be done on x- and y-axis separately
            lineChart.setPinchZoom(true);

            // set an alternative background color
            lineChart.setBackgroundColor(Color.TRANSPARENT);

            lineChart.animateX(500);

            // get the legend (only possible after setting data)
            Legend l = lineChart.getLegend();

            // modify the legend ...
            l.setForm(Legend.LegendForm.SQUARE);
            //  l.setTypeface(tfLight);
            l.setTextSize(11f);
            l.setTextColor(Color.BLACK);
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
            l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            l.setDrawInside(false);

        l.setYOffset(11f);

            XAxis xAxis = lineChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(true);
            xAxis.setGranularity(1f);
            xAxis.setGranularityEnabled(true);
            xAxis.setEnabled(false);
            xAxis.setValueFormatter(new MyAxisValueFormatter());

            YAxis leftAxis = lineChart.getAxisLeft();
            // leftAxis.setTypeface(tfLight);
            leftAxis.setTextColor(Color.BLACK);
            //leftAxis.setAxisMaximum(200f);
            //leftAxis.setAxisMinimum(0f);
            leftAxis.setDrawGridLines(true);
            leftAxis.setGranularityEnabled(true);
            leftAxis.setEnabled(true);
            leftAxis.setValueFormatter(new MyAxisValueFormatter());
//            if (getStoredLanguage().equals(LANGUAGE_ENGLISH))
//                leftAxis.setValueFormatter(new LargeValueFormatter());
            YAxis rightAxis = lineChart.getAxisRight();
            //rightAxis.setTypeface(tfLight);
            // rightAxis.setAxisMaximum(900);
            // rightAxis.setAxisMinimum(-200);
            rightAxis.setGranularityEnabled(false);
            //to hide right Y and top X border
            YAxis rightYAxis = lineChart.getAxisRight();

            rightYAxis.setValueFormatter(new MyAxisValueFormatter());
            rightYAxis.setEnabled(false);
            // XAxis topXAxis = lineChart.getXAxis();
            // topXAxis.setEnabled(false);
            // XAxis xAxis = lineChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(true);


            // lineChart.setViewPortOffsets(100, 0, 50, 50);
            IMarker marker = new FiguresMarkerView(mContext, R.layout.text_view, "LineChart");
            lineChart.setMarker(marker);
        }

        private void setVitalChart(ArrayList<ApiVitalPivotV2.VitalRecord> result) {
            lineChart.removeAllViews();

            ArrayList<Entry> values1 = new ArrayList<>();

            ArrayList<String> dates = new ArrayList<>();

            for (int i = 0; i < result.size(); i++) {
                values1.add(new Entry(i, Float.parseFloat(result.get(i).getValue()), result.get(i).getVitalDateFormat()));
                dates.add(result.get(i).getVitalDate());
            }
            //String setter in x-Axis
            lineChart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(dates));
            LineDataSet set1;

            if (lineChart.getData() != null &&
                    lineChart.getData().getDataSetCount() > 0) {
                set1 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);

                set1.setValues(values1);
                set1.setValueFormatter(new MyAxisValueFormatter());

                lineChart.getData().notifyDataChanged();
                lineChart.notifyDataSetChanged();
            } else {
                // create a dataset and give it a type

                set1 = new LineDataSet(values1, "");

                set1.setAxisDependency(YAxis.AxisDependency.RIGHT);
                set1.setColor(Color.RED);
                set1.setCircleColor(Color.RED);
                set1.setLineWidth(2f);
                set1.setCircleRadius(3f);
                set1.setFillAlpha(65);
                set1.setFillColor(ColorTemplate.colorWithAlpha(Color.BLACK, 200));
                set1.setHighLightColor(Color.rgb(244, 117, 117));
                set1.setDrawCircleHole(true);
                set1.setFillFormatter(new MyFillFormatter(0f));
                //set1.setDrawHorizontalHighlightIndicator(false);
                //set1.setVisible(false);
                //set1.setCircleHoleColor(Color.WHITE);


                // create a data object with the data sets
                LineData data = new LineData(set1);
                data.setValueTextColor(Color.BLACK);
                data.setValueTextSize(9f);
                // data.setDrawValues(false);
                // set data
                lineChart.setData(data);
                // graphView.setVisibleXRange(0,8);
                lineChart.getXAxis().setLabelCount(10, true);
                data.setValueFormatter(new MyAxisValueFormatter());
                /*data.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return String.valueOf((int) value);
                    }
                });*/
            }
        }
        private void setVitalChartBloodPressure(ArrayList<ApiVitalPivotV2.VitalRecord> result) {
            lineChart.removeAllViews();

            ArrayList<Entry> values1 = new ArrayList<>();
            ArrayList<Entry> values2 = new ArrayList<>();
            ArrayList<String> dates = new ArrayList<>();

            for (int i = 0; i < result.size(); i++) {
                values1.add(new Entry(i, result.get(i).getHigh(), result.get(i).getVitalDateFormat()));
                values2.add(new Entry(i, result.get(i).getLow(), result.get(i).getVitalDateFormat()));
                dates.add(result.get(i).getVitalDate());
            }
            //String setter in x-Axis
            lineChart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(dates));
            LineDataSet set1,set2;

            if (lineChart.getData() != null &&
                    lineChart.getData().getDataSetCount() > 0) {
                set1 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
                set1.setValues(values1);
                set1.setValueFormatter(new MyAxisValueFormatter());

                set2 = (LineDataSet) lineChart.getData().getDataSetByIndex(1);
                set2.setValues(values2);
                set2.setValueFormatter(new MyAxisValueFormatter());

                lineChart.getData().notifyDataChanged();
                lineChart.notifyDataSetChanged();
            } else {
                // create a dataset and give it a type

                set1 = new LineDataSet(values1, "");
                set1.setAxisDependency(YAxis.AxisDependency.RIGHT);
                set1.setColor(Color.RED);
                set1.setCircleColor(Color.RED);
                set1.setLineWidth(2f);
                set1.setCircleRadius(3f);
                set1.setFillAlpha(65);
                set1.setFillColor(ColorTemplate.colorWithAlpha(Color.BLACK, 200));
                set1.setHighLightColor(Color.rgb(244, 117, 117));
                set1.setDrawCircleHole(true);
                set1.setFillFormatter(new MyFillFormatter(0f));

                set2 = new LineDataSet(values2, "");
                set2.setAxisDependency(YAxis.AxisDependency.RIGHT);
                set2.setColor(Color.BLUE);
                set2.setCircleColor(Color.BLUE);
                set2.setLineWidth(2f);
                set2.setCircleRadius(3f);
                set2.setFillAlpha(65);
                set2.setFillColor(ColorTemplate.colorWithAlpha(Color.BLACK, 200));
                set2.setHighLightColor(Color.rgb(244, 117, 117));
                set2.setDrawCircleHole(true);
                set2.setFillFormatter(new MyFillFormatter(0f));



                // create a data object with the data sets
                LineData data = new LineData(set1,set2);
                data.setValueTextColor(Color.BLACK);
                data.setValueTextSize(9f);
                // data.setDrawValues(false);
                // set data
                lineChart.setData(data);
                // graphView.setVisibleXRange(0,8);
                lineChart.getXAxis().setLabelCount(10, true);
                data.setValueFormatter(new MyAxisValueFormatter());
                /*data.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return String.valueOf((int) value);
                    }
                });*/
            }
        }
    }

        public class DrawableGradient extends GradientDrawable {
            private static final float CORNER_RADIUS = 24;

            DrawableGradient(int[] colors) {
                super(Orientation.LEFT_RIGHT, colors);
                try {
                    this.setShape(GradientDrawable.RECTANGLE);
                    this.setGradientType(GradientDrawable.LINEAR_GRADIENT);
                    this.setCornerRadius(CORNER_RADIUS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }


        private String getStoredLanguage() {
            SharedPreferences sharedPref = mContext.getSharedPreferences(LANGUAGE_PREFS, Context.MODE_PRIVATE);
            return sharedPref.getString(LANGUAGE_SELECTED, getDeviceLanguage());
        }
    private String getDeviceLanguage() {
        return Locale.getDefault().getLanguage();
    }
    }
