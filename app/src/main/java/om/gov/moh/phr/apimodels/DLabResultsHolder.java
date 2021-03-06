package om.gov.moh.phr.apimodels;

import android.text.TextUtils;
import android.text.format.DateFormat;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import om.gov.moh.phr.fragments.LabResultDetailsFragment;

public class DLabResultsHolder implements Serializable{
    @SerializedName("result")
    private LabResultDetails result;

    public LabResultDetails getResult() {
        return result;
    }

    public class LabResultDetails implements Serializable{
        @SerializedName("conclusion")
        private String conclusion;

        public String getConclusion() {
            if (conclusion==null||TextUtils.isEmpty(conclusion))
                return "--";
            else
            return conclusion;
        }

        @SerializedName("releasedTime")
        private long releasedTime;

        public String getReleasedTime() {
            if(releasedTime!=0) {
                Date date = new Date(releasedTime);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH);
                String dateResult = simpleDateFormat.format(date);
                return dateResult;
            }else
                return "--";
        }
        @SerializedName("releasedBy")
        private String releasedBy;

        public String getReleasedBy() {
            if (releasedBy==null||TextUtils.isEmpty(releasedBy))
                return "--";
            else
            return releasedBy;
        }

        @SerializedName("status")
        private String status;

        public String getStatus() {
            if (status==null||TextUtils.isEmpty(status))
                return "--";
            else
            return status;
        }

        public ArrayList<TabularData> getTabularData() {
            return tabularData;
        }

        @SerializedName("tabularData")
        private ArrayList<TabularData> tabularData = new ArrayList<>();

        public ArrayList<TextualData> getTextualData() {
            return textualData;
        }

        @SerializedName("textualData")
        private ArrayList<TextualData> textualData = new ArrayList<>();
    }


    public class TabularData implements Serializable {
        @SerializedName("testName")
        private String testName;

        @SerializedName("result")
        private String result;

        @SerializedName("unit")
        private String unit;

        @SerializedName("rangeLow")
        private String rangeLow;

        @SerializedName("interpretation")
        private String interpretation;

        @SerializedName("rangeHigh")
        private String rangeHigh;
        @SerializedName("orderId")
        private String orderId;

        public String getOrderId() {
            if (orderId==null||TextUtils.isEmpty(orderId))
                return "";
            else
                return orderId;
        }

        public String getTestName() {
            if (testName==null||TextUtils.isEmpty(testName))
                return null;
            else
                return testName;
        }

        public String getResult() {
            if (result==null||TextUtils.isEmpty(result) || result.equals("null"))
                return "-";
            else
                return result;
        }


        public String getRangeLow() {
            if (rangeLow==null||TextUtils.isEmpty(rangeLow) || rangeLow.equals("null"))
                return "-";
            else
                return rangeLow;
        }

        public String getRangeHigh() {
            if (rangeHigh==null||TextUtils.isEmpty(rangeHigh) || rangeHigh.equals("null"))
                return "-";
            else
                return rangeHigh;
        }

        public String getInterpretation() {
            if(interpretation==null)
                return "";
            else
            return interpretation;
        }


        public String getUnit() {
            if (unit==null||TextUtils.isEmpty(unit) || unit.equals("null"))
                return "-";
            else
                return unit;
        }
    }
    public class TextualData implements Serializable {
        @SerializedName("paramName")
        private String paramName;

        @SerializedName("result")
        private String result;

        public String getParamName() {
            if (paramName==null||TextUtils.isEmpty(paramName))
                return "--";
            else
            return paramName;
        }

        public String getResult() {
            if (result==null||TextUtils.isEmpty(result))
                return "--";
            else
            return result;
        }
    }
}
