package om.gov.moh.phr.apimodels;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ApiMedicationHolder {
    @SerializedName("result")
    private ArrayList<ApiMedicationInfo> mResult;

    public ArrayList<ApiMedicationInfo> getmResult() {
        return mResult;
    }

    public class ApiMedicationInfo implements Serializable {
        @SerializedName("medicineName")
        private String medicineName;

        @SerializedName("estName")
        private String estName;

        @SerializedName("dosage")
        private String dosage;

        @SerializedName("dateWrittern")
        private long dateWrittern;

        public String getEstName() {
            if (TextUtils.isEmpty(estName))
                return "";
            else
                return estName;
        }

        public long getDateWrittern() {
            return dateWrittern;
        }

        public String getMedicineName() {
            if (TextUtils.isEmpty(medicineName))
                return "";
            else
                return medicineName;
        }

        public String getDosage() {
            if (TextUtils.isEmpty(dosage))
                return "";
            else
            return dosage;
        }

    }
}
