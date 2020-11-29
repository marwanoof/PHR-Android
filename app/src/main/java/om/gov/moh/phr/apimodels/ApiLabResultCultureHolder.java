package om.gov.moh.phr.apimodels;

import android.text.format.DateFormat;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ApiLabResultCultureHolder implements Serializable {
    @SerializedName("result")
    private CultureResult result;

    public CultureResult getResult() {
        return result;
    }

    public class CultureResult implements Serializable{
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

        @SerializedName("culture")
        private ArrayList<Culture> culture;

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
            return status;
        }

        public long getOrderDate() {
            return orderDate;
        }

        public String getReleasedTime() {
            Date date = new Date(releasedTime);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a",Locale.ENGLISH);
            String dateResult = simpleDateFormat.format(date);
            return dateResult;
        }

        public String getTemplateType() {
            return templateType;
        }

        public String getConclusion() {
            return conclusion;
        }

        public String getResultId() {
            return resultId;
        }

        public String getPerformer() {
            return performer;
        }

        public String getReleasedBy() {
            return releasedBy;
        }

        public ArrayList<Culture> getCulture() {
            return culture;
        }

        public String getEstFullname() {
            return estFullname;
        }
    }
    public class Culture implements Serializable{
        @SerializedName("runId")
        private String runId;

        @SerializedName("componentId")
        private int componentId;

        @SerializedName("observationId")
        private String observationId;

        @SerializedName("bacteriaId")
        private String bacteriaId;

        @SerializedName("bacteriaName")
        private String bacteriaName;

        @SerializedName("antibioticCode")
        private String antibioticCode;

        @SerializedName("antibioticName")
        private String antibioticName;

        @SerializedName("result")
        private String result;

        public String getRunId() {
            return runId;
        }

        public int getComponentId() {
            return componentId;
        }

        public String getObservationId() {
            return observationId;
        }

        public String getBacteriaId() {
            return bacteriaId;
        }

        public String getBacteriaName() {
            return bacteriaName;
        }

        public String getAntibioticCode() {
            return antibioticCode;
        }

        public String getAntibioticName() {
            return antibioticName;
        }

        public String getResult() {
            return result;
        }
    }
}
