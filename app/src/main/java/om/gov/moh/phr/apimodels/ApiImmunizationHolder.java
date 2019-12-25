package om.gov.moh.phr.apimodels;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ApiImmunizationHolder {
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
        private long scheduledOn;

        @SerializedName("givenOn")
        private long givenOn;

        public long getScheduledOn() {
            return scheduledOn;
        }

        public long getGivenOn() {
            return givenOn;
        }

        public String getVaccineName() {
            if (TextUtils.isEmpty(vaccineName))
                return "";
            else
            return vaccineName;
        }

        public String getStatus() {
            if (TextUtils.isEmpty(status))
                return "";
            else
            return status;
        }

        public long getImmunizationDate() {
            return immunizationDate;
        }

    }
}
