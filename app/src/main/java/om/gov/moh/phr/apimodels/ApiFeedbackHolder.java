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
        ArrayList<OptionsList> optionMast= new ArrayList<>();;

        public ArrayList<OptionsList> getOptionMast() {
            return optionMast;
        }

        public class OptionsList{
            @SerializedName("optionId")
            private int optionId;

            @SerializedName("optionName")
            private String optionName;

            public int getOptionId() {
                return optionId;
            }

            public String getOptionName() {
                if(TextUtils.isEmpty(optionName))
                    return "";
                return optionName;
            }

            public String getOptionNameNls() {
                if(TextUtils.isEmpty(optionNameNls))
                    return "";
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
            if(TextUtils.isEmpty(userFeedback))
                return "";
            return userFeedback;
        }

        public int getParamId() {
            return paramId;
        }

        public String getParamValue() {
            return paramValue;
        }

        public String getParamValueNls() {
            return paramValueNls;
        }

        public String getPrevId() {
            return prevId;
        }

        public String getActiveYn() {
            return activeYn;
        }

        public String getDataType() {
            return dataType;
        }

        public String getOptionValue() {
            return optionValue;
        }
    }
}
