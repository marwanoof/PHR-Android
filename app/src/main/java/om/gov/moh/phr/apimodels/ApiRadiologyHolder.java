package om.gov.moh.phr.apimodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ApiRadiologyHolder implements Serializable {
    @SerializedName("result")
    private ArrayList<Radiology> result;

    public ArrayList<Radiology> getResult() {
        return result;
    }

    public class Radiology implements Serializable{
        @SerializedName("orderId")
        private String orderId;

        @SerializedName("orderDate")
        private long orderDate;

        @SerializedName("encounterId")
        private String encounterId;

        @SerializedName("procType")
        private String procType;

        @SerializedName("procName")
        private String procName;

        @SerializedName("side")
        private String side;

        @SerializedName("reason")
        private String reason;

        @SerializedName("orderedBy")
        private String orderedBy;

        @SerializedName("estName")
        private String estName;

        @SerializedName("reportDoneDate")
        private long reportDoneDate;

        @SerializedName("status")
        private String status;

        @SerializedName("reportLink")
        private String reportLink;

        @SerializedName("reportId")
        private String reportId;

        @SerializedName("reportBy")
        private String reportBy;

        @SerializedName("reportQualification")
        private String reportQualification;

        @SerializedName("statusDateTime")
        private long statusDateTime;

        @SerializedName("estFullname")
        private String estFullname;

        @SerializedName("estFullnameNls")
        private String estFullnameNls;

        public String getOrderId() {
            return orderId;
        }

        public long getOrderDate() {
            return orderDate;
        }

        public String getEncounterId() {
            return encounterId;
        }

        public String getProcType() {
            return procType;
        }

        public String getProcName() {
            return procName;
        }

        public String getSide() {
            return side;
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

        public long getReportDoneDate() {
            return reportDoneDate;
        }

        public String getStatus() {
            return status;
        }

        public String getReportLink() {
            return reportLink;
        }

        public String getReportId() {
            return reportId;
        }

        public String getReportBy() {
            return reportBy;
        }

        public String getReportQualification() {
            return reportQualification;
        }

        public long getStatusDateTime() {
            return statusDateTime;
        }

        public String getEstFullname() {
            return estFullname;
        }

        public String getEstFullnameNls() {
            return estFullnameNls;
        }
    }
}
