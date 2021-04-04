package om.gov.moh.phr.apimodels;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ApiAppointmentClinicsHolder implements Serializable {
    private ArrayList<Clinic> result = new ArrayList<>();

    public ArrayList<Clinic> getResult() {
        return result;
    }


    public class Clinic {

        @SerializedName("doctDeptId")
        private String doctDeptId;

        @SerializedName("doctDeptName")
        private String doctDeptName;

        @SerializedName("doctdeptnameNl")
        private String doctdeptnameNl;

        public String getDoctdeptnameNl() {
            if (doctdeptnameNl==null||TextUtils.isEmpty(doctdeptnameNl))
                return doctDeptName;
            else
            return doctdeptnameNl;
        }

        public String getDoctDeptId() {
            if (doctDeptId==null||TextUtils.isEmpty(doctDeptId))
                return "";
            else
                return doctDeptId;
        }

        public String getDoctDeptName() {
            if (doctDeptName==null||TextUtils.isEmpty(doctDeptName))
                return "";
            else
                return doctDeptName;
        }
    }

}
