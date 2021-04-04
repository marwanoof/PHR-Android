package om.gov.moh.phr.apimodels;

import android.text.format.DateFormat;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ApiVitalPivotV2 implements Serializable {
    @SerializedName("result")
    private ApiVitalPivotV2.PivotV2 result;

    public ApiVitalPivotV2.PivotV2 getResult() {
        return result;
    }

    @SerializedName("code")
    int code;

    public int getCode() {
        return code;
    }

    public class PivotV2 {
        @SerializedName("vitalCode")
        private String vitalCode;

        @SerializedName("vitalSign")
        private String vitalSign;

        @SerializedName("rangeFrom")
        private String rangeFrom;

        @SerializedName("rangeTo")
        private String rangeTo;

        @SerializedName("vitalRecord")
        private ArrayList<ApiVitalPivotV2.VitalRecord> vitalRecord = new ArrayList<>();

        public String getVitalCode() {
            return vitalCode;
        }

        public String getVitalSign() {
            if (vitalSign == null)
                return "";
            else
                return vitalSign;
        }

        public String getRangeFrom() {
            return rangeFrom;
        }

        public String getRangeTo() {
            return rangeTo;
        }

        public ArrayList<VitalRecord> getVitalRecord() {
            return vitalRecord;
        }

    }

    public class VitalRecord {
        @SerializedName("patientId")
        private String patientId;

        @SerializedName("encounterId")
        private String encounterId;

        @SerializedName("estShortName")
        private String estShortName;

        @SerializedName("estName")
        private String estName;

        @SerializedName("estNameNls")
        private String estNameNls;

        @SerializedName("vitalDate")
        private long vitalDate;

        @SerializedName("vitalDateFormat")
        private String vitalDateFormat;

        @SerializedName("value")
        private String value;

        @SerializedName("high")
        private int high;

        @SerializedName("low")
        private int low;

        @SerializedName("unit")
        private String unit;

        @SerializedName("unitNls")
        private String unitNls;

        public String getPatientId() {
            return patientId;
        }

        public String getEncounterId() {
            return encounterId;
        }

        public String getEstShortName() {
            return estShortName;
        }

        public String getEstName() {
            return estName;
        }

        public String getEstNameNls() {
            return estNameNls;
        }

        public String getVitalDate() {
            if (vitalDate != 0) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(vitalDate);
                Date serverDate = new Date(vitalDate);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy hh:mm a", Locale.ENGLISH);
                String date = dateFormat.format(serverDate);
                return date;
            } else return "";
        }

        public String getVitalDateFormat() {
            if (vitalDateFormat == null)
                return "";
            else
                return vitalDateFormat;
        }

        public String getValue() {
            if (value == null)
                return "";
            else
                return value;
        }

        public int getHigh() {
            return high;
        }

        public int getLow() {
            return low;
        }

        public String getUnit() {
            if (unit == null)
                return "";
            else
                return unit;
        }

        public String getUnitNls() {
            if(unitNls==null)
                return unit;
            else
            return unitNls;
        }


    }
}
