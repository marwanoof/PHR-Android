package om.gov.moh.phr.apimodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ApiTextualDataHolder implements Serializable{

        @SerializedName("paramId")
        private String paramId;

        @SerializedName("paramName")
        private String paramName;

        @SerializedName("result")
        private String result;

    public void setParamId(String paramId) {
        this.paramId = paramId;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getParamId() {
            return paramId;
        }

        public String getParamName() {
            return paramName;
        }

        public String getResult() {
            return result;
        }

}
