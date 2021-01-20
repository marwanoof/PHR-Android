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
            if (reservationId == null || TextUtils.isEmpty(reservationId))
                return "";
            else
                return reservationId;
        }

        public String getDescription() {
            if (description == null || TextUtils.isEmpty(description))
                return "";
            else
                return description;
        }

        public String getEstName() {
            if (estName == null || TextUtils.isEmpty(estName))
                return "";
            else
                return " | " + estName;
        }

        public String getEstCode() {
            if (estCode == null || TextUtils.isEmpty(estCode))
                return "";
            else
                return estCode;
        }
    }

    public class Referrals implements Serializable {
        @SerializedName("referralBy")
        private String referralBy;

        @SerializedName("sendDate")
        private long sendDate;

        @SerializedName("refInstitute")
        private String refInstitute;

        public String getReferralBy() {
            if (referralBy == null || TextUtils.isEmpty(referralBy))
                return "";
            else
                return referralBy;
        }

        public long getSendDate() {
            return sendDate;
        }

        public String getRefInstitute() {
            if (refInstitute == null || TextUtils.isEmpty(refInstitute))
                return "";
            else
                return refInstitute;
        }

        public String getDescription() {
            if(TextUtils.isEmpty(description))
                return null;
            else
                return description;
        }

        @SerializedName("description")
        private String description;
    }
}
