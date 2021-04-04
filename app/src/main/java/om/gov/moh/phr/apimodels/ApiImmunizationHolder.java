package om.gov.moh.phr.apimodels;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ApiImmunizationHolder implements Serializable{
    @SerializedName("result")
    private ArrayList<ApiImmunizationHolder.ApiImmunizationInfo> mResult;

    public ArrayList<ApiImmunizationHolder.ApiImmunizationInfo> getmResult() {
        return mResult;
    }

    public class ApiImmunizationInfo implements Serializable {
        @SerializedName("vaccineName")
        private String vaccineName;

        @SerializedName("status")
        private String status;

        @SerializedName("immunizationDate")
        private long immunizationDate;

        @SerializedName("scheduledOn")
        private Long scheduledOn;

        @SerializedName("givenOn")
        private Long givenOn;

        @SerializedName("estFullName")
        private String estFullName;

        @SerializedName("estFullNameNls")
        private String estFullNameNls;

        public Long getScheduledOn() {
            return scheduledOn;
        }

        public Long getGivenOn() {
            return givenOn;
        }

        public String getVaccineName() {
            if (vaccineName == null || TextUtils.isEmpty(vaccineName))
                return "";
            else
                return vaccineName;
        }

        public String getEstFullName() {
            if (estFullName == null)
                return "";
            else
                return estFullName;
        }

        public String getEstFullNameNls() {
            if (estFullNameNls == null || TextUtils.isEmpty(estFullNameNls))
                return estFullName;
            else
                return estFullNameNls;
        }

        public String getStatus() {
            if (status == null || TextUtils.isEmpty(status))
                return "";
            else
                return status;
        }

        public long getImmunizationDate() {
            return immunizationDate;
        }

    }
}
