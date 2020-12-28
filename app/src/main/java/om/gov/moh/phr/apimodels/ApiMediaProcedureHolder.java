package om.gov.moh.phr.apimodels;

import android.text.format.DateFormat;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ApiMediaProcedureHolder implements Serializable {
    @SerializedName("result")
    private ArrayList<MediaProcedure> result;

    public ArrayList<MediaProcedure> getResult() {
        return result;
    }

    public class MediaProcedure implements Serializable {
        @SerializedName("civilId")
        private String civilId;

        @SerializedName("mediaId")
        private String mediaId;

        @SerializedName("contentType")
        private String contentType;

        @SerializedName("mediaType")
        private String mediaType;

        @SerializedName("mediaSubType")
        private String mediaSubType;

        @SerializedName("mediaString")
        private String mediaString;

        @SerializedName("mediaReportData")
        private String mediaReportData;

        @SerializedName("estCode")
        private String estCode;

        @SerializedName("creationTime")
        private long creationTime;

        @SerializedName("estFullname")
        private String estFullname;

        public String getCivilId() {
            return civilId;
        }

        public String getMediaId() {
            return mediaId;
        }

        public String getContentType() {
            return contentType;
        }

        public String getMediaType() {
            return mediaType;
        }

        public String getMediaSubType() {
            return mediaSubType;
        }

        public String getMediaString() {
            return mediaString;
        }

        public String getMediaReportData() {
            return mediaReportData;
        }

        public String getEstCode() {
            return estCode;
        }

        public String getEstFullname() {
            return estFullname;
        }

        public String getCreationTime() {
            String dateResult;
            if (creationTime != 0) {
                Date date = new Date(creationTime);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH);
                dateResult = simpleDateFormat.format(date);
            } else
                dateResult = "";
            return dateResult;
        }


    }
}