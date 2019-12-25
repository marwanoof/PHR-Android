package om.gov.moh.phr.apimodels;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ApiAppointmentsListHolder {

    @SerializedName("result")
    private AppointmentsHolder result;

    public AppointmentsHolder getResult() {
        return result;
    }

    public class AppointmentsHolder {
        @SerializedName("appointments")
        ArrayList<Appointments> appointmentsArrayList = new ArrayList<>();

        public ArrayList<Appointments> getAppointmentsArrayList() {
            return appointmentsArrayList;
        }


        @SerializedName("referrals")
        ArrayList<Referrals> referralsArrayList = new ArrayList<>();

        public ArrayList<Referrals> getReferralsArrayList() {
            return referralsArrayList;
        }


    }//estName

    public class Appointments implements Serializable {
        @SerializedName("reservationId")
        private String reservationId;

        @SerializedName("description")
        private String description;

        @SerializedName("estName")
        private String estName;

        /**
         * estCode is label as "lastModifiedBy" in the response
         */
        @SerializedName("lastModifiedBy")
        private String estCode;

        public String getReservationId() {
            if (TextUtils.isEmpty(reservationId))
                return "";
            else
                return reservationId;
        }

        public String getDescription() {
            if (TextUtils.isEmpty(description))
                return "";
            else
                return description;
        }

        public String getEstName() {
            if (TextUtils.isEmpty(estName))
                return "";
            else
                return " | " + estName;
        }

        public String getEstCode() {
            if (TextUtils.isEmpty(estCode))
                return "";
            else
                return estCode;
        }
    }

    public class Referrals {

    }
}
