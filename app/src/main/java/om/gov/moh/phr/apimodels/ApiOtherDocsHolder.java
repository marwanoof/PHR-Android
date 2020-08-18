package om.gov.moh.phr.apimodels;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ApiOtherDocsHolder {
    @SerializedName("result")
    private ArrayList<ApiDocInfo> mResult;

    public ArrayList<ApiDocInfo> getmResult() {
        return mResult;
    }

    public class ApiDocInfo implements Serializable {
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
    }
}
