package om.gov.moh.phr.apimodels;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import om.gov.moh.phr.fragments.LabResultDetailsFragment;

public class DLabResultsHolder {
    @SerializedName("result")
    private LabResultDetails result;

    public LabResultDetails getResult() {
        return result;
    }

    public class LabResultDetails implements Serializable{
        @SerializedName("conclusion")
        private String conclusion;

        public String getConclusion() {
            if (TextUtils.isEmpty(conclusion))
                return "--";
            else
            return conclusion;
        }

        @SerializedName("releasedTime")
        private long releasedTime;

        public long getReleasedTime() {
            return releasedTime;
        }
        @SerializedName("releasedBy")
        private String releasedBy;

        public String getReleasedBy() {
            if (TextUtils.isEmpty(releasedBy))
                return "--";
            else
            return releasedBy;
        }

        @SerializedName("status")
        private String status;

        public String getStatus() {
            if (TextUtils.isEmpty(status))
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
            if (TextUtils.isEmpty(orderId))
                return "";
            else
                return orderId;
        }

        public String getTestName() {
            if (TextUtils.isEmpty(testName))
                return null;
            else
                return testName;
        }

        public String getResult() {
            if (TextUtils.isEmpty(result) || result.equals("null"))
                return "-";
            else
                return result;
        }


        public String getRangeLow() {
            if (TextUtils.isEmpty(rangeLow) || rangeLow.equals("null"))
                return "-";
            else
                return rangeLow;
        }

        public String getRangeHigh() {
            if (TextUtils.isEmpty(rangeHigh) || rangeHigh.equals("null"))
                return "-";
            else
                return rangeHigh;
        }

        public String getInterpretation() {
            return interpretation;
        }


        public String getUnit() {
            if (TextUtils.isEmpty(unit) || unit.equals("null"))
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
            if (TextUtils.isEmpty(paramName))
                return "--";
            else
            return paramName;
        }

        public String getResult() {
            if (TextUtils.isEmpty(result))
                return "--";
            else
            return result;
        }
    }
}
