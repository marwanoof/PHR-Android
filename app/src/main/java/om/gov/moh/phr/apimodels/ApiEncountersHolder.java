package om.gov.moh.phr.apimodels;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static om.gov.moh.phr.models.MyConstants.LANGUAGE_ARABIC;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_PREFS;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_SELECTED;

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
        private long periodStartDate;

        @SerializedName("encounterId")
        private String encounterId;

        @SerializedName("estFullname")
        private String estFullname;

        @SerializedName("estName")
        private String estShortName;

        public String getEstFullnameNls() {
            return estFullnameNls;
        }

        @SerializedName("estFullnameNls")
        private String estFullnameNls;

        @SerializedName("patientClass")
        private String patientClass;

        @SerializedName("diagnosis")
        private ArrayList<DiagnosisList> diagnosisArrayList = new ArrayList<>();

        @SerializedName("department")
        private ArrayList<departmentList> departmentArrayList = new ArrayList<>();


        public String getEncounterId() {
            if (TextUtils.isEmpty(encounterId))
                return "";
            else
                return encounterId;
        }

        public void setEncounterId(String encounterId) {
            this.encounterId = encounterId;
        }

        public Date getPeriodStartDate() {

            Locale locale = new Locale("ar", "sa", "arab");
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(periodStartDate);
            Date date = cal.getTime();
            //String date2 = DateFormat.format("dd/MM/yyyy hh:mm a", cal).toString();
            return date;

            //return new java.text.SimpleDateFormat("dd/MMM/yyyy").format(new java.util.Date(Long.parseLong(periodStartDate)));

        }

        public String getPeriodStartDateString() {


            return new java.text.SimpleDateFormat("dd/MMM/yyyy").format(new java.util.Date(periodStartDate));

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

        public ArrayList<DiagnosisList> getDiagnosisArrayList() {
            return diagnosisArrayList;
        }

        public void setDiagnosisArrayList(ArrayList<DiagnosisList> diagnosisArrayList) {
            this.diagnosisArrayList = diagnosisArrayList;
        }

        public ArrayList<departmentList> getDepartmentArrayList() {
            return departmentArrayList;
        }

        public void setDepartmentArrayList(ArrayList<departmentList> departmentArrayList) {
            this.departmentArrayList = departmentArrayList;
        }

        public String getEncounterDay() {
            String day = (String) DateFormat.format("dd", getPeriodStartDate());
            return day;
            /*if (TextUtils.isEmpty(getPeriodStartDate()))
                return "";
            else {
                return getPeriodStartDate().substring(0, 2);
            }*/
        }

        public String getEncounterMonth() {
            String month = (String) DateFormat.format("MMM", getPeriodStartDate());
            return month;
            /*if (TextUtils.isEmpty(getPeriodStartDate()))
                return "";
            else {
                return getPeriodStartDate();
            }*/
        }

        public String getEncounterYear() {
            String year = (String) DateFormat.format("yyyy", getPeriodStartDate());
            return year;
            /*if (TextUtils.isEmpty(getPeriodStartDate()))
                return "";
            else {
           // to get same output of the year regardless of setting language
                return getPeriodStartDate().substring(getPeriodStartDate().lastIndexOf("/")+1,getPeriodStartDate().length());

            }*/
        }

        public String getEstShortName() {
            if (estShortName == null || TextUtils.isEmpty(estShortName))
                return "";
            else
                return estShortName;
        }


        public String getPatientClass() {
            if (patientClass == null || TextUtils.isEmpty(patientClass))
                return "";
            else
                return patientClass;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            Encounter encounterNewItem = (Encounter) obj;

            if (!getPeriodStartDateString().equalsIgnoreCase(encounterNewItem.getPeriodStartDateString()))
                return false;
            if (!getEncounterId().equalsIgnoreCase(encounterNewItem.getEncounterId())) return false;
            if (!getEstFullname().equalsIgnoreCase(encounterNewItem.getEstFullname())) return false;
            if (!isEqualArrayListDiag(getDiagnosisArrayList(), encounterNewItem.getDiagnosisArrayList()))
                return false;
            return isEqualArrayListDept(getDepartmentArrayList(), encounterNewItem.getDepartmentArrayList());
        }

        private boolean isEqualArrayListDept(ArrayList<departmentList> one, ArrayList<departmentList> two) {


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

        private boolean isEqualArrayListDiag(ArrayList<DiagnosisList> one, ArrayList<DiagnosisList> two) {


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

    public class departmentList {
        @SerializedName("value")
        private String value;

        @SerializedName("valueNls")
        private String valueNls;

        public String getValueNls() {
            if (valueNls == null || TextUtils.isEmpty(valueNls))
                return value;
            else
                return valueNls;
        }

        public String getValue() {
            if (value == null || TextUtils.isEmpty(value))
                return "";
            else
                return value;
        }
    }

    public class DiagnosisList {
        @SerializedName("value")
        private String value;

        public String getValue() {
            if(value==null||TextUtils.isEmpty(value))
                return "";
            return value;
        }
    }


}
