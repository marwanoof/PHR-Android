package om.gov.moh.phr.apimodels;

import android.text.format.DateFormat;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ApiTextualDataHolder implements Serializable{

        @SerializedName("result")
        private TextualLabResult result;

    public TextualLabResult getResult() {
        return result;
    }

    public class TextualLabResult implements Serializable{
        @SerializedName("orderId")
        private String orderId;

        @SerializedName("reportId")
        private String reportId;

        @SerializedName("patientId")
        private String patientId;

        @SerializedName("encounterId")
        private String encounterId;

        @SerializedName("profileTest")
        private String profileTest;

        @SerializedName("profileName")
        private String profileName;

        @SerializedName("status")
        private String status;

        @SerializedName("orderDate")
        private long orderDate;

        @SerializedName("releasedTime")
        private long releasedTime;

        @SerializedName("templateType")
        private String templateType;

        @SerializedName("conclusion")
        private String conclusion;

        @SerializedName("resultId")
        private String resultId;

        @SerializedName("performer")
        private String performer;

        @SerializedName("releasedBy")
        private String releasedBy;

        @SerializedName("textualData")
        private ArrayList<TextualDataResult> textualData;

        @SerializedName("estFullname")
        private String estFullname;

        public String getOrderId() {
            return orderId;
        }

        public String getReportId() {
            return reportId;
        }

        public String getPatientId() {
            return patientId;
        }

        public String getEncounterId() {
            return encounterId;
        }

        public String getProfileTest() {
            return profileTest;
        }

        public String getProfileName() {
            return profileName;
        }

        public String getStatus() {
            if(status==null)
                return "";
            else
            return status;
        }

        public long getOrderDate() {
            return orderDate;
        }

        public String getReleasedTime() {
            if(releasedTime!=0) {
                Date date = new Date(releasedTime);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH);
                String dateResult = simpleDateFormat.format(date);
                return dateResult;
            }else return "--";
        }

        public String getTemplateType() {
            return templateType;
        }

        public String getConclusion() {
            if(conclusion==null)
                return "";
            else
            return conclusion;
        }

        public String getResultId() {
            return resultId;
        }

        public String getPerformer() {
            return performer;
        }

        public String getReleasedBy() {
            if(releasedBy==null)
                return "--";
            else
            return releasedBy;
        }

        public ArrayList<TextualDataResult> getTextualData() {
            return textualData;
        }

        public String getEstFullname() {
            if(estFullname==null)
                return "";
            else
            return estFullname;
        }
    }
    public class TextualDataResult implements Serializable{
        @SerializedName("paramId")
        private String paramId;

        @SerializedName("paramName")
        private String paramName;

        @SerializedName("result")
        private String result;

        public String getParamId() {
            return paramId;
        }

        public String getParamName() {
            return paramName;
        }

        public String getResult() {
            return result;
        }
    }
}
