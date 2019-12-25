package om.gov.moh.phr.apimodels;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ApiLabOrdersListHolder {
    @SerializedName("result")
    private ArrayList<ApiOredresList> mResult;

    public ArrayList<ApiOredresList> getmResult() {
        return mResult;
    }

    public class ApiOredresList implements Serializable {
        @SerializedName("procName")
        private String procName;

        @SerializedName("status")
        private String status;

        @SerializedName("orderDate")
        private long orderDate;

        @SerializedName("estName")
        private String estName;

        @SerializedName("templateType")
        private String templateType;

        @SerializedName("patientId")
        private String patientId;

        @SerializedName("orderId")
        private String orderId;


        @SerializedName("orderedBy")
        private String orderedBy;

        public String getOrderedBy() {
            return orderedBy;
        }

        public String getOrderId() {
            if (TextUtils.isEmpty(orderId))
                return "";
            else
                return orderId;
        }

        public String getTemplateType() {
            if (TextUtils.isEmpty(templateType))
                return "";
            else
                return templateType;
        }

        public String getProcName() {
            if (TextUtils.isEmpty(procName))
                return "";
            else
                return procName;
        }

        public String getStatus() {
            if (TextUtils.isEmpty(status))
                return "";
            else
                return status;
        }

        public long getOrderDate() {
            if (TextUtils.isEmpty(estName))
                return 0;
            return orderDate;
        }

        public String getEstName() {
            if (TextUtils.isEmpty(estName))
                return "";
            return estName;
        }
        public String getPatientId() {
            if (TextUtils.isEmpty(patientId))
                return "";
            return patientId;
        }
    }

}
