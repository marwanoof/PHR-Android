package om.gov.moh.phr.apimodels;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class


ApiSlotsHolder implements Serializable {
    @SerializedName("result")
    private SlotHolder result;

    public SlotHolder getResult() {
        return result;
    }


    public class SlotHolder {


        @SerializedName("patientId")
        private String patientId;

        @SerializedName("slots")
        private ArrayList<Slot> slotsArrayList = new ArrayList<>();

        public ArrayList<Slot> getSlotsArrayList() {
            return slotsArrayList;
        }


        public String getPatientId() {
            if (TextUtils.isEmpty(patientId))
                return "";
            else
                return patientId;
        }


    }

    public class Slot {
        @SerializedName("appointmentDate")
        private String appointmentDate;

        @SerializedName("timeBlock")
        private String timeBlock;

        @SerializedName("runId")
        private String runId;

        public String getAppointmentDay() {
            if (TextUtils.isEmpty(appointmentDate))
                return "";
            else {
                return appointmentDate.substring(0, 2);
            }
        }

        public String getAppointmentMonth() {
            if (TextUtils.isEmpty(appointmentDate))
                return "";
            else {
                return appointmentDate.substring(3, 6);
            }
        }

        public String getAppointmentDate() {
            if (TextUtils.isEmpty(appointmentDate))
                return "";
            else {
                return appointmentDate;
            }
        }

        public String getTimeBlock() {
            if (TextUtils.isEmpty(timeBlock))
                return "";
            else
                return timeBlock;
        }

        public String getRunId() {
            if (TextUtils.isEmpty(runId))
                return "";
            else
                return runId;
        }


    }

}
