package om.gov.moh.phr.apimodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ApiDisclaimerHolder implements Serializable {
    @SerializedName("result")
    private DisclaimerByCivilId mResult;

    public DisclaimerByCivilId getmResult() {
        return mResult;
    }

    public class DisclaimerByCivilId{
        @SerializedName("civilId")
        private String civilId;

        @SerializedName("acceptYN")
        private String acceptYN;

        public String getCivilId() {
            return civilId;
        }

        public String getAcceptYN() {
            return acceptYN;
        }
    }
}
