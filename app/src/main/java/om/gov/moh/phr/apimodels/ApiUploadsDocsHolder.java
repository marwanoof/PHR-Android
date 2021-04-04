package om.gov.moh.phr.apimodels;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ApiUploadsDocsHolder implements Serializable{
    @SerializedName("result")
    private ArrayList<ApiUploadDocInfo> mResult;

    public ArrayList<ApiUploadDocInfo> getmResult() {
        return mResult;
    }

    public class ApiUploadDocInfo implements Serializable {
        @SerializedName("docId")
        private long docId;

        @SerializedName("docTypeName")
        private String docTypeName;

        @SerializedName("docTypeNameNls")
        private String docTypeNameNls;

        @SerializedName("source")
        private String source;

        @SerializedName("createdDate")
        private String createdDate;

        @SerializedName("fileName")
        private String fileName;

        @SerializedName("status")
        private String status;


        public long getDocId() {
            return docId;
        }

        public String getDocTypeName() {
            if (docTypeName == null || TextUtils.isEmpty(docTypeName))
                return "";
            else
                return docTypeName;
        }

        public String getSource() {
            if (source == null || TextUtils.isEmpty(source))
                return "";
            else
                return source;
        }


        public String getCreatedDate() {
            if (createdDate == null)
                return "--";
            else
                return createdDate;
        }

        public String getFileName() {
            if (TextUtils.isEmpty(fileName))
                return "";
            else
                return fileName;
        }

        public String getStatus() {
            if (status == null || TextUtils.isEmpty(status))
                return "";
            else
                return status;
        }

        public String getDocTypeNameNls() {
            if (docTypeNameNls == null || TextUtils.isEmpty(docTypeNameNls))
                return docTypeName;
            else
                return docTypeNameNls;
        }
    }
}
