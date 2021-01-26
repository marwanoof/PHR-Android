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

        @SerializedName("estFullNameNls")
        private String estFullnameNls;

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

        public String getTitleNls() {
            return titleNls;
        }

        @SerializedName("titleNls")
        private String titleNls;

        @SerializedName("encounterDate")
        private long encounterDate;

        @SerializedName("encounterId")
        private String encounterId;

        @SerializedName("patientClass")
        private String patientClass;

        @SerializedName("type")
        private String type;
        @SerializedName("typeNls")
        private String typeNls;

        public String getTypeNls() {
            return typeNls;
        }

        public String getDocumentRefId() {
            if (documentRefId == null)
                return "";
            else
                return documentRefId;
        }

        public String getTypeD() {
            if (typeD == null || TextUtils.isEmpty(typeD))
                return "";
            else
                return typeD;
        }

        public String getEstName() {
            if (estName == null || TextUtils.isEmpty(estName))
                return "";
            else
                return estName;
        }

        public String getEstFullnameNls() {
            if (estFullnameNls == null)
                return estFullname;
            else
                return estFullnameNls;
        }

        public String getTitle() {
            if (title == null)
                return "";
            else
                return title;
        }

        public String getEstFullname() {
            if (estFullname==null||TextUtils.isEmpty(estFullname))
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
            if (encounterId == null||TextUtils.isEmpty(encounterId))
                return "";
            else
                return encounterId;
        }

        public String getPatientClass() {
            if (patientClass == null||TextUtils.isEmpty(patientClass))
                return "";
            else
                return patientClass;
        }

        public String getType() {
            if (type == null||TextUtils.isEmpty(type))
                return "--";
            else
                return type;
        }
    }
}
