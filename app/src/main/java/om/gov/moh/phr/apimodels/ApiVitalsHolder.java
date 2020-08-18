package om.gov.moh.phr.apimodels;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ApiVitalsHolder {
    @SerializedName("result")
    ArrayList<Vitals> result = new ArrayList<>();

    public ArrayList<Vitals> getResult() {
        return result;
    }

    public class Vitals {
        @SerializedName("bpd")
        private String bpd;

        @SerializedName("bps")
        private String bps;

        @SerializedName("tem")
        private String tem;

        @SerializedName("spo2")
        private String spo2;

        @SerializedName("vitalDate")
        private String vitalDate;

        @SerializedName("hr")
        private String hr;

        public String getBpd() {
            if (TextUtils.isEmpty(bpd))
                return "-";
            else
                return bpd;
        }

        public String getBps() {
            if (TextUtils.isEmpty(bps))
                return "-";
            else
                return bps + "/";// + " mm/Hg  |  ";
        }

        public String getTem() {
            if (TextUtils.isEmpty(tem))
                return "-";
            else
                return tem;// + "'c  |  ";
        }

        public String getSpo2() {
            if (TextUtils.isEmpty(spo2))
                return "-";
            else
                return spo2;
        }

        public String getVitalDate() {
            if (TextUtils.isEmpty(vitalDate))
                return "";
            else
                return new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date(Long.parseLong(vitalDate)));
        }

        public String getHr() {
            if (TextUtils.isEmpty(hr))
                return "-";
            else
                return hr;//bts/m
        }


    }
}
