package om.gov.moh.phr.apimodels;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class ApiOtherDocsHolder {
    @SerializedName("result")
    private ArrayList<ApiDocInfo> mResult;

    public ArrayList<ApiDocInfo> getmResult() {
        return mResult;
    }

    public class ApiDocInfo implements Serializable, Comparable<ApiDocInfo> {
        @SerializedName("typeD")
        private String typeD;

        @SerializedName("estName")
        private String estName;

        @SerializedName("estFullname")
        private String estFullname;

        public String getLocationName() {
            return locationName;
        }

        @SerializedName("indexed")
        private long indexed;

        @SerializedName("documentRefId")
        private String documentRefId;

        @SerializedName("locationName")
        private String locationName;

        @SerializedName("title")
        private String title;

        @SerializedName("encounterDate")
        private long encounterDate;

        @SerializedName("encounterId")
        private String encounterId;

        @SerializedName("patientClass")
        private String patientClass;

        @SerializedName("type")
        private String type;

        public String getDocumentRefId() {
            return documentRefId;
        }

        public String getTypeD() {
            if (TextUtils.isEmpty(typeD))
                return "";
            else
                return typeD;
        }

        public String getEstName() {
            if (TextUtils.isEmpty(estName))
                return "";
            else
                return estName;
        }

        public String getTitle() {
            return title;
        }

        public String getEstFullname() {
            if (TextUtils.isEmpty(estFullname))
                return "";
            else
                return estFullname;
        }

        public long getIndexed() {
            return indexed;
        }

        public long getEncounterDate() {
            return encounterDate;
        }

        @Override
        public int compareTo(ApiDocInfo apiDocInfo) {
            Date date = new Date(getEncounterDate());
            Date date1 = new Date(apiDocInfo.getEncounterDate());
            return date.compareTo(date1);
        }

        public String getEncounterId() {
            if (TextUtils.isEmpty(encounterId) || encounterId == null)
                return "";
            else
                return encounterId;
        }

        public String getPatientClass() {
            if (TextUtils.isEmpty(patientClass) || patientClass == null)
                return "";
            else
                return patientClass;
        }

        public String getType() {
            if (TextUtils.isEmpty(type) || type == null)
                return "--";
            else
                return type;
        }
    }
}
