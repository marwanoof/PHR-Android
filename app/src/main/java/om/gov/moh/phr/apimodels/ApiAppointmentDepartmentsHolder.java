package om.gov.moh.phr.apimodels;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ApiAppointmentDepartmentsHolder {
    private ArrayList<Department> result = new ArrayList<>();

    public ArrayList<Department> getResult() {
        return result;
    }

    public class Department {

        @SerializedName("deptId")
        private String deptId;

        @SerializedName("deptName")
        private String deptName;

        @SerializedName("deptnameNl")
        private String deptnameNl;

        public String getDeptnameNl() {
            if (deptnameNl == null || TextUtils.isEmpty(deptnameNl))
                return deptName;
            else
                return deptnameNl;
        }

        @SerializedName("clinicDept")
        private String clinicDept;

        public String getDeptId() {
            if (deptId == null || TextUtils.isEmpty(deptId))
                return "";
            else
                return deptId;
        }

        public String getDeptName() {
            if (deptName == null || TextUtils.isEmpty(deptName))
                return "";
            else
                return deptName;
        }

        public String getClinicDept() {
            if (clinicDept == null || TextUtils.isEmpty(clinicDept))
                return "";
            else
                return clinicDept;
        }


    }

}
