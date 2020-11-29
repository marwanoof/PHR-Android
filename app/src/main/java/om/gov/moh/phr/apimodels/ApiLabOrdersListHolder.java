package om.gov.moh.phr.apimodels;

import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ApiLabOrdersListHolder {
    @SerializedName("result")
    private ArrayList<ApiOredresList> mResult;

    public ArrayList<ApiOredresList> getmResult() {
        return mResult;
    }

    public class ApiOredresList implements Serializable {
        @SerializedName("encounterDate")
        private long encounterDate;

        @SerializedName("encounterDateFormat")
        private String encounterDateFormat;

        @SerializedName("encounterId")
        private String encounterId;

        @SerializedName("estFullName")
        private String estFullName;

        @SerializedName("labOrders")
        private ArrayList<LabOrder> labOrders;

        public String getEncounterDate() {
            Date date = new Date(encounterDate);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy",Locale.ENGLISH);
            String dateResult = simpleDateFormat.format(date);
            return dateResult;
        }

        public String getEncounterDateFormat() {
            return encounterDateFormat;
        }

        public String getEncounterId() {
            return encounterId;
        }

        public String getEstFullName() {
            return estFullName;
        }

        public ArrayList<LabOrder> getLabOrders() {
            return labOrders;
        }
    }
    public class LabOrder implements Serializable{
        @SerializedName("orderId")
        private String orderId;

        @SerializedName("orderDate")
        private long orderDate;

        @SerializedName("procType")
        private String procType;

        @SerializedName("procName")
        private String procName;

        @SerializedName("reason")
        private String reason;

        @SerializedName("orderedBy")
        private String orderedBy;

        @SerializedName("estName")
        private String estName;

        @SerializedName("status")
        private String status;

        @SerializedName("encounterId")
        private String encounterId;

        @SerializedName("reportId")
        private String reportId;

        //***
        @SerializedName("priority")
        private String priority;

        @SerializedName("statusDateTime")
        private long statusDateTime;

        @SerializedName("estFullname")
        private String estFullname;

        @SerializedName("speciman")
        private ArrayList<String> speciman;

        @SerializedName("specimanValue")
        private ArrayList<SpecimanValue> specimanValue;

        @SerializedName("reportLink")
        private String reportLink;

        @SerializedName("doneDate")
        private long doneDate;

        @SerializedName("templateType")
        private String templateType;

        @SerializedName("patientId")
        private String patientId;

        @SerializedName("encounterDate")
        private long encounterDate;

        @SerializedName("patientClass")
        private String patientClass;

        @SerializedName("estFullnameNls")
        private String estFullnameNls;

        public String getOrderId() {
            return orderId;
        }

        public long getOrderDate() {
            return orderDate;
        }

        public String getProcType() {
            return procType;
        }

        public String getProcName() {
            return procName;
        }

        public String getReason() {
            return reason;
        }

        public String getOrderedBy() {
            return orderedBy;
        }

        public String getEstName() {
            return estName;
        }

        public String getStatus() {
            return status;
        }

        public String getEncounterId() {
            return encounterId;
        }

        public String getReportId() {
            return reportId;
        }

        public String getPriority() {
            return priority;
        }

        public long getStatusDateTime() {
            return statusDateTime;
        }

        public String getEstFullname() {
            return estFullname;
        }

        public ArrayList<String> getSpeciman() {
            return speciman;
        }

        public ArrayList<SpecimanValue> getSpecimanValue() {
            return specimanValue;
        }

        public String getReportLink() {
            return reportLink;
        }

        public long getDoneDate() {
            return doneDate;
        }

        public String getTemplateType() {
            return templateType;
        }

        public String getPatientId() {
            return patientId;
        }

        public long getEncounterDate() {
            return encounterDate;
        }

        public String getPatientClass() {
            return patientClass;
        }

        public String getEstFullnameNls() {
            return estFullnameNls;
        }
    }
    private class SpecimanValue implements Serializable{
        @SerializedName("value")
        private String value;

        @SerializedName("valueNls")
        private String valueNls;

        public String getValue() {
            return value;
        }

        public String getValueNls() {
            return valueNls;
        }
    }

}
