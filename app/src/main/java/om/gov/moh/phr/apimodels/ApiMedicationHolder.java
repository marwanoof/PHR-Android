package om.gov.moh.phr.apimodels;

import android.text.TextUtils;
import android.text.format.DateFormat;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ApiMedicationHolder {
    @SerializedName("result")
    private ArrayList<ApiMedicationInfo> mResult;

    public ArrayList<ApiMedicationInfo> getmResult() {
        return mResult;
    }

    public class ApiMedicationInfo implements Serializable {
        @SerializedName("encounterDate")
        private long encounterDate;

        @SerializedName("encounterDateFormat")
        private String encounterDateFormat;

        @SerializedName("encounterId")
        private String encounterId;

        @SerializedName("estFullName")
        private String estFullName;

        @SerializedName("medication")
        private ArrayList<Medication> medication;

        public String getEncounterDate() {
            if (encounterDate != 0) {
                Date date = new Date(encounterDate);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                String dateResult = simpleDateFormat.format(date);
                return dateResult;
            } else
                return "--";
        }

        public String getEncounterDateFormat() {
            return encounterDateFormat;
        }

        public String getEncounterId() {
            return encounterId;
        }

        public String getEstFullName() {
            return estFullName;
        }

        public ArrayList<Medication> getMedication() {
            return medication;
        }
    }

    public class Medication implements Serializable {
        @SerializedName("dateWrittenFormat")
        private String dateWrittenFormat;

        @SerializedName("dateWrittern")
        private long dateWrittern;

        @SerializedName("medicineName")
        private String medicineName;

        @SerializedName("prescriberName")
        private String prescriberName;

        @SerializedName("estName")
        private String estName;

        @SerializedName("status")
        private String status;

        @SerializedName("validityEnd")
        private String validityEnd;

        @SerializedName("dosage")
        private String dosage;

        @SerializedName("dosageNls")
        private String dosageNls;

        @SerializedName("additionalInstructions")
        private String additionalInstructions;

        @SerializedName("reasonForMedication")
        private String reasonForMedication;

        @SerializedName("dateEnded")
        private long dateEnded;

        @SerializedName("medicationOrderId")
        private String medicationOrderId;

        @SerializedName("encounterId")
        private String encounterId;

        @SerializedName("estFullname")
        private String estFullname;

        @SerializedName("category")
        private String category;

        @SerializedName("dischargeDate")
        private long dischargeDate;

        @SerializedName("startDate")
        private long startDate;

        @SerializedName("timingDuration")
        private int timingDuration;

        @SerializedName("timingDurationUnit")
        private String timingDurationUnit;

        @SerializedName("timingCourseCode")
        private int timingCourseCode;

        @SerializedName("timingCourseName")
        private String timingCourseName;

        @SerializedName("doseValueUnitCode")
        private int doseValueUnitCode;

        @SerializedName("doseValueUnitName")
        private String doseValueUnitName;

        @SerializedName("unitNameNls")
        private String unitNameNls;

        @SerializedName("encounterDate")
        private long encounterDate;

        @SerializedName("patientClass")
        private String patientClass;

        @SerializedName("doseQty")
        private double doseQty;

        @SerializedName("doseDays")
        private int doseDays;

        @SerializedName("doseTimes")
        private double doseTimes;

        @SerializedName("doseDesc")
        private String doseDesc;

        @SerializedName("doseDescNls")
        private String doseDescNls;

        public String getDateWrittenFormat() {
            return dateWrittenFormat;
        }

        public long getDateWrittern() {
            return dateWrittern;
        }

        public String getMedicineName() {
            if (medicineName == null)
                return "--";
            else
                return medicineName;
        }

        public String getPrescriberName() {
            return prescriberName;
        }

        public String getEstName() {
            if (estName == null)
                return "--";
            else
                return estName;
        }

        public String getStatus() {
            if (status == null)
                return "";
            else
                return status;
        }

        public String getValidityEnd() {
            return validityEnd;
        }

        public String getDosage() {
            if (dosage == null)
                return "";
            else
                return dosage;
        }

        public String getDosageNls() {
            if (dosageNls == null)
                return dosage;
            else
                return dosageNls;
        }

        public String getAdditionalInstructions() {
            return additionalInstructions;
        }

        public String getReasonForMedication() {
            return reasonForMedication;
        }

        public long getDateEnded() {
            return dateEnded;
        }

        public String getMedicationOrderId() {
            if (medicationOrderId == null)
                return "";
            else
                return medicationOrderId;
        }

        public String getEncounterId() {
            return encounterId;
        }

        public String getEstFullname() {
            return estFullname;
        }

        public String getCategory() {
            return category;
        }

        public long getDischargeDate() {
            return dischargeDate;
        }

        public Date getStartDate() {
            Calendar cal = Calendar.getInstance(Locale.getDefault());
            cal.setTimeInMillis(startDate);
            Date date = cal.getTime();
            return date;
        }

        public int getTimingDuration() {
            return timingDuration;
        }

        public String getTimingDurationUnit() {
            return timingDurationUnit;
        }

        public int getTimingCourseCode() {
            return timingCourseCode;
        }

        public String getTimingCourseName() {
            return timingCourseName;
        }

        public int getDoseValueUnitCode() {
            return doseValueUnitCode;
        }

        public String getDoseValueUnitName() {
            if (doseValueUnitName == null)
                return "";
            else
                return doseValueUnitName;
        }

        public String getUnitNameNls() {
            if (unitNameNls == null)
                return "";
            else
                return unitNameNls;
        }

        public long getEncounterDate() {
            return encounterDate;
        }

        public String getPatientClass() {
            return patientClass;
        }

        public double getDoseQty() {
            return doseQty;
        }

        public int getDoseDays() {
            return doseDays;
        }

        public double getDoseTimes() {
            return doseTimes;
        }

        public String getDoseDesc() {
            if (doseDesc == null)
                return "";
            else
                return doseDesc;
        }

        public String getDoseDescNls() {
            if (doseDescNls == null)
                return doseDesc;
            else
            return doseDescNls;
        }
    }
}
