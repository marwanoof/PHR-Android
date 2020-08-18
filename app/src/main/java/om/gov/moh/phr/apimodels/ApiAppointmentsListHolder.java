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

    public class Referrals implements Serializable {
        @SerializedName("referralBy")
        private String referralBy;

        @SerializedName("sendDate")
        private long sendDate;

        @SerializedName("refInstitute")
        private String refInstitute;

        public String getReferralBy() {
            return referralBy;
        }

        public long getSendDate() {
            return sendDate;
        }

        public String getRefInstitute() {
            return refInstitute;
        }

        public String getDescription() {
            return description;
        }

        @SerializedName("description")
        private String description;
    }
}
