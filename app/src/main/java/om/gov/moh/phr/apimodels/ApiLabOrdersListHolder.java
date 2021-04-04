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

public class ApiLabOrdersListHolder implements Serializable{
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
            if (encounterDate != 0) {
                Date date = new Date(encounterDate);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                return simpleDateFormat.format(date);
            } else
                return "--";
        }

        public String getEncounterDateFormat() {
            return encounterDateFormat;
        }

        public String getEncounterId() {
            return encounterId;
        }

        public String getEstFullName() {
            if (getEstFullName() == null)
                return "";
            else
                return estFullName;
        }

        public ArrayList<LabOrder> getLabOrders() {
            return labOrders;
        }
    }

    public class LabOrder implements Serializable {
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
            if (procType == null)
                return "";
            else
                return procType;
        }

        public String getProcName() {
            if (procName == null)
                return "";
            else
                return procName;
        }

        public String getReason() {
            return reason;
        }

        public String getOrderedBy() {
            if (orderedBy == null)
                return "--";
            else
                return orderedBy;
        }

        public String getEstName() {
            if (estName == null)
                return "";
            else
                return estName;
        }

        public String getStatus() {
            if (status == null)
                return "";
            else
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
            if (estFullname == null)
                return "";
            else
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
            if (templateType == null)
                return "";
            else
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
            if (estFullnameNls == null)
                return estFullname;
            else
                return estFullnameNls;
        }
    }

    private class SpecimanValue implements Serializable {
        @SerializedName("value")
        private String value;

        @SerializedName("valueNls")
        private String valueNls;

        public String getValue() {
            if (value == null)
                return "";
            else
                return value;
        }

        public String getValueNls() {
            if (valueNls == null)
                return value;
            else
                return valueNls;
        }
    }

}
