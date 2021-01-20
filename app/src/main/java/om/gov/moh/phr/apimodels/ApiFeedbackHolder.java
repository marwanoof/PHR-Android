package om.gov.moh.phr.apimodels;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ApiFeedbackHolder {
    @SerializedName("result")
    ArrayList<ApiFeedbackHolder.Questions> result = new ArrayList<>();

    public ArrayList<Questions> getResult() {
        return result;
    }

    public class Questions {
        @SerializedName("paramId")
        private int paramId;

        @SerializedName("paramValue")
        private String paramValue;

        @SerializedName("paramValueNls")
        private String paramValueNls;

        @SerializedName("prevId")
        private String prevId;

        @SerializedName("activeYn")
        private String activeYn;

        @SerializedName("dataType")
        private String dataType;

        @SerializedName("optionValue")
        private String optionValue;


        @SerializedName("optionMast")
        ArrayList<OptionsList> optionMast = new ArrayList<>();
        ;

        public ArrayList<OptionsList> getOptionMast() {
            return optionMast;
        }

        public class OptionsList {
            @SerializedName("optionId")
            private int optionId;

            @SerializedName("optionName")
            private String optionName;

            public int getOptionId() {
                return optionId;
            }

            public String getOptionName() {
                if (optionName == null || TextUtils.isEmpty(optionName))
                    return "";
                else
                return optionName;
            }

            public String getOptionNameNls() {
                if (optionNameNls == null || TextUtils.isEmpty(optionNameNls))
                    return "";
                else
                return optionNameNls;
            }

            @SerializedName("optionNameNls")
            private String optionNameNls;
        }

        private String userFeedback;

        public void setUserFeedback(String userFeedback) {
            this.userFeedback = userFeedback;
        }

        public String getUserFeedback() {
            if (userFeedback == null || TextUtils.isEmpty(userFeedback))
                return "";
            else
            return userFeedback;
        }

        public int getParamId() {
            return paramId;
        }

        public String getParamValue() {
            if (paramValue == null || TextUtils.isEmpty(paramValue))
                return "";
            else
                return paramValue;
        }

        public String getParamValueNls() {
            if (paramValueNls == null || TextUtils.isEmpty(paramValueNls))
                return "";
            else
                return paramValueNls;
        }

        public String getPrevId() {
            return prevId;
        }

        public String getActiveYn() {
            if (activeYn == null || TextUtils.isEmpty(activeYn))
                return "";
            else
                return activeYn;
        }

        public String getDataType() {
            if (dataType == null || TextUtils.isEmpty(dataType))
                return "";
            else
                return dataType;
        }

        public String getOptionValue() {
            if (optionValue == null || TextUtils.isEmpty(optionValue))
                return "";
            else
                return optionValue;
        }
    }
}
