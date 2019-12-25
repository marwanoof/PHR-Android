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

        @SerializedName("clinicDept")
        private String clinicDept;

        public String getDeptId() {
            if (TextUtils.isEmpty(deptId))
                return "";
            else
                return deptId;
        }

        public String getDeptName() {
            if (TextUtils.isEmpty(deptName))
                return "";
            else
                return deptName;
        }

        public String getClinicDept() {
            if (TextUtils.isEmpty(clinicDept))
                return "";
            else
                return clinicDept;
        }


    }

}
