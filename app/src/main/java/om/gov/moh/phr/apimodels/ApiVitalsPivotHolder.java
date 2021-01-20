package om.gov.moh.phr.apimodels;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ApiVitalsPivotHolder {

    @SerializedName("result")
    ArrayList<Pivot> result = new ArrayList<>();

    public ArrayList<Pivot> getResult() {
        return result;
    }

    public class Pivot {
        @SerializedName("vitalDate1")
        private String vitalDate1;

        @SerializedName("vitalDate2")
        private String vitalDate2;

        @SerializedName("vitalDate3")
        private String vitalDate3;

        @SerializedName("vitalDate4")
        private String vitalDate4;

        @SerializedName("vitalDate5")
        private String vitalDate5;

        @SerializedName("vitalSign")
        private String vitalSign;

        @SerializedName("value1")
        private String value1;

        @SerializedName("value2")
        private String value2;

        @SerializedName("value3")
        private String value3;

        @SerializedName("value4")
        private String value4;

        @SerializedName("value5")
        private String value5;

        @SerializedName("value6")
        private String value6;

        @SerializedName("value7")
        private String value7;

        @SerializedName("value8")
        private String value8;

        @SerializedName("value9")
        private String value9;

        @SerializedName("value10")
        private String value10;


        public String getVitalDate1() {
            if (vitalDate1==null||TextUtils.isEmpty(vitalDate1))
                return "";
            else
                return new java.text.SimpleDateFormat("dd/MM HH:mm").format(new java.util.Date(Long.parseLong(vitalDate1)));

        }

        public String getVitalDate2() {
            if (vitalDate2==null||TextUtils.isEmpty(vitalDate2))
                return "";
            else
                return new java.text.SimpleDateFormat("dd/MM HH:mm").format(new java.util.Date(Long.parseLong(vitalDate2)));
        }

        public String getVitalDate3() {
            if (vitalDate3==null||TextUtils.isEmpty(vitalDate3))
                return "";
            else
                return new java.text.SimpleDateFormat("dd/MM HH:mm").format(new java.util.Date(Long.parseLong(vitalDate3)));
        }

        public String getVitalDate4() {
            if (vitalDate4==null||TextUtils.isEmpty(vitalDate4))
                return "";
            else
                return new java.text.SimpleDateFormat("dd/MM HH:mm").format(new java.util.Date(Long.parseLong(vitalDate4)));
        }

        public String getVitalDate5() {
            if (vitalDate5==null||TextUtils.isEmpty(vitalDate5))
                return "";
            else
                return new java.text.SimpleDateFormat("dd/MM HH:mm").format(new java.util.Date(Long.parseLong(vitalDate5)));
        }

        public String getVitalSign() {
            if (vitalSign==null||TextUtils.isEmpty(vitalSign))
                return "";
            else
                return vitalSign;
        }

        public double getValue1() {
            if (value1==null||TextUtils.isEmpty(value1))
                return 0;
            else
                return Double.parseDouble(value1);
        }

        public double getValue2() {
            if (value2==null||TextUtils.isEmpty(value2))
                return 0;
            else
                return Double.parseDouble(value2);
        }

        public double getValue3() {
            if (value3==null||TextUtils.isEmpty(value3))
                return 0;
            else
                return Double.parseDouble(value3);
        }

        public double getValue4() {
            if (value4==null||TextUtils.isEmpty(value4))
                return 0;
            else
                return Double.parseDouble(value4);
        }

        public double getValue5() {
            if (value5==null||TextUtils.isEmpty(value5))
                return 0;
            else
                return Double.parseDouble(value5);
        }

        public double getValue6() {
            if (value6==null||TextUtils.isEmpty(value6))
                return 0;
            else
                return Double.parseDouble(value6);
        }


        public double getValue7() {
            if (value7==null||TextUtils.isEmpty(value7))
                return 0;
            else
                return Double.parseDouble(value7);
        }

        public double getValue8() {
            if (value8==null||TextUtils.isEmpty(value8))
                return 0;
            else
                return Double.parseDouble(value8);
        }

        public double getValue9() {
            if (value9==null||TextUtils.isEmpty(value9))
                return 0;
            else
                return Double.parseDouble(value9);
        }

        public double getValue10() {
            if (value10==null||TextUtils.isEmpty(value10))
                return 0;
            else
                return Double.parseDouble(value10);
        }
    }
}
