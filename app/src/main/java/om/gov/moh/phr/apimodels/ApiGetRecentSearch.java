package om.gov.moh.phr.apimodels;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ApiGetRecentSearch {
    @SerializedName("result")
    private ArrayList<Result> result = new ArrayList<>();

    public ArrayList<Result> getResult() {
        return result;
    }
    public class Result implements Serializable {
        @SerializedName("civilId")
        private Long civilId;

        @SerializedName("firstName")
        private String firstName;

        @SerializedName("secondName")
        private String secondName;

        @SerializedName("thirdName")
        private String thirdName;

        @SerializedName("fourthName")
        private String fourthName;

        @SerializedName("lastAccessedTime")
        private Long lastAccessedTime;

        @SerializedName("dataOfBirth")
        private Long dataOfBirth;

        @SerializedName("sex")
        private String sex;

        @SerializedName("age")
        private String age;

        @SerializedName("personPhoto")
        private String personPhoto;

        public Long getCivilId() {
            return civilId;
        }

        public String getFirstName() {
            if(firstName==null|| TextUtils.isEmpty(firstName))
                return "";
            return firstName;
        }

        public String getSecondName() {
            if(secondName==null|| TextUtils.isEmpty(secondName))
                return "";
            return secondName;
        }

        public String getThirdName() {
            if(thirdName==null|| TextUtils.isEmpty(thirdName))
                return "";
            return thirdName;
        }

        public String getFourthName() {
            if(fourthName==null|| TextUtils.isEmpty(fourthName))
                return "";
            return fourthName;
        }

        public Long getLastAccessedTime() {
            return lastAccessedTime;
        }

        public Long getDataOfBirth() {
            return dataOfBirth;
        }

        public String getSex() {
            if(sex==null|| TextUtils.isEmpty(sex))
                return "";
            return sex;
        }

        public String getAge() {
            if(age==null|| TextUtils.isEmpty(age))
                return "";
            return age;
        }

        public String getPersonPhoto() {
            return personPhoto;
        }
    }
}
