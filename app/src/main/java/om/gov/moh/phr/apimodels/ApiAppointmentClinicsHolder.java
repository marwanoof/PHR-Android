package om.gov.moh.phr.apimodels;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ApiAppointmentClinicsHolder {
    private ArrayList<Clinic> result = new ArrayList<>();

    public ArrayList<Clinic> getResult() {
        return result;
    }


    public class Clinic {

        @SerializedName("doctDeptId")
        private String doctDeptId;

        @SerializedName("doctDeptName")
        private String doctDeptName;


        public String getDoctDeptId() {
            if (TextUtils.isEmpty(doctDeptId))
                return "";
            else
                return doctDeptId;
        }

        public String getDoctDeptName() {
            if (TextUtils.isEmpty(doctDeptName))
                return "";
            else
                return doctDeptName;
        }


    }

}
