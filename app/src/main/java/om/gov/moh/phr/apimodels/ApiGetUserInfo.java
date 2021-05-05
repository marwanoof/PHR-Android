package om.gov.moh.phr.apimodels;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ApiGetUserInfo {
    @SerializedName("result")
    private Result result = new Result();

    public Result getResult() {
        return result;
    }

    public class Result implements Serializable {

        @SerializedName("person")
        private Person person;

        public Person getPerson() {
            return person;
        }
        @SerializedName("loginId")
        private String loginId;

        public String getLoginId() {
            if (loginId == null || TextUtils.isEmpty(loginId))
                return "--";
            else
                return loginId;
        }
    }

    public class Person implements Serializable {

        @SerializedName("personName")
        private String personName;

        public String getPersonName() {
            if (personName == null || TextUtils.isEmpty(personName))
                return "--";
            else
                return personName;
        }
    }
}
