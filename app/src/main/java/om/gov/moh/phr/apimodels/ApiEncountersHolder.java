package om.gov.moh.phr.apimodels;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

public class ApiEncountersHolder {

    @SerializedName("result")
    private ArrayList<Encounter> result = new ArrayList<>();

    public ArrayList<Encounter> getResult() {
        return result;
    }

    public void setResult(ArrayList<Encounter> result) {
        this.result = result;
    }

    public class Encounter implements Serializable {

        @SerializedName("periodStartDate")
        private String periodStartDate;

        @SerializedName("encounterId")
        private String encounterId;

        @SerializedName("estFullname")
        private String estFullname;

        @SerializedName("estName")
        private String estShortName;

        @SerializedName("patientClass")
        private String patientClass;

        @SerializedName("diagnosis")
        private ArrayList<String> diagnosisArrayList = new ArrayList<>();

        @SerializedName("department")
        private ArrayList<String> departmentArrayList = new ArrayList<>();


        public String getEncounterId() {
            if (TextUtils.isEmpty(estFullname))
                return "";
            else
                return encounterId;
        }

        public void setEncounterId(String encounterId) {
            this.encounterId = encounterId;
        }

        public String getPeriodStartDate() {
            if (TextUtils.isEmpty(periodStartDate))
                return "";
            else
                return new java.text.SimpleDateFormat("dd/MMM/yyyy").format(new java.util.Date(Long.parseLong(periodStartDate)));

        }


        public String getEstFullname() {
            if (TextUtils.isEmpty(estFullname))
                return "";
            else
                return estFullname;
        }

        public void setEstFullname(String estFullname) {
            this.estFullname = estFullname;
        }

        public ArrayList<String> getDiagnosisArrayList() {
            return diagnosisArrayList;
        }

        public void setDiagnosisArrayList(ArrayList<String> diagnosisArrayList) {
            this.diagnosisArrayList = diagnosisArrayList;
        }

        public ArrayList<String> getDepartmentArrayList() {
            return departmentArrayList;
        }

        public void setDepartmentArrayList(ArrayList<String> departmentArrayList) {
            this.departmentArrayList = departmentArrayList;
        }

        public String getEncounterDay() {
            if (TextUtils.isEmpty(getPeriodStartDate()))
                return "";
            else {
                return getPeriodStartDate().substring(0, 2);
            }
        }

        public String getEncounterMonth() {
            Log.d("epoch-month", getPeriodStartDate());
            if (TextUtils.isEmpty(getPeriodStartDate()))
                return "";
            else {
                return getPeriodStartDate().substring(3, 6);
            }
        }

        public String getEncounterYear() {
            if (TextUtils.isEmpty(getPeriodStartDate()))
                return "";
            else {
           // to get same output of the year regardless of setting language
                return getPeriodStartDate().substring(getPeriodStartDate().lastIndexOf("/")+1,getPeriodStartDate().length());

            }
        }

        public String getEstShortName() {
            if (TextUtils.isEmpty(estShortName))
                return "";
            else
                return estShortName;
        }


        public String getPatientClass() {
            if (TextUtils.isEmpty(patientClass))
                return "";
            else
                return patientClass;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            Encounter encounterNewItem = (Encounter) obj;

            if (!getPeriodStartDate().equalsIgnoreCase(encounterNewItem.getPeriodStartDate()))
                return false;
            if (!getEncounterId().equalsIgnoreCase(encounterNewItem.getEncounterId())) return false;
            if (!getEstFullname().equalsIgnoreCase(encounterNewItem.getEstFullname())) return false;
            if (!isEqualArrayList(getDiagnosisArrayList(), encounterNewItem.getDiagnosisArrayList()))
                return false;
            return isEqualArrayList(getDepartmentArrayList(), encounterNewItem.getDepartmentArrayList());
        }

        private boolean isEqualArrayList(ArrayList<String> one, ArrayList<String> two) {


            if (one == null && two == null) {
                return true;
            }

            if ((one == null && two != null)
                    || (one != null && two == null)
                    || (one.size() != two.size())) {
                return false;
            }

            return one.equals(two);
        }

    }
}
