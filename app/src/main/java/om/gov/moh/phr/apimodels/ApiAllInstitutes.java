package om.gov.moh.phr.apimodels;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ApiAllInstitutes {
    @SerializedName("result")
    private ArrayList<Institute> result = new ArrayList<>();

    public ArrayList<Institute> getResult() {
        return result;
    }

    public ArrayList<String> getInstitutesArrayList(ArrayList<Institute> result) {
        ArrayList<String> institutesArrayList = new ArrayList<>();
//        institutesArrayList.add(label);
        for (int i = 0; i < result.size(); i++) {
            institutesArrayList.add(getResult().get(i).getInstituteFullName());
        }
        return institutesArrayList;
    }

    public class Institute {

        @SerializedName("estName")
        private String estName;

        @SerializedName("estFullName")
        private String estFullName;


        private String getEstName() {
            if (TextUtils.isEmpty(estName))
                return "";
            else
                return "(" + estName + ") - ";
        }

        private String getEstFullName() {
            if (TextUtils.isEmpty(estFullName))
                return "";
            else
                return estFullName;
        }

        public String getInstituteFullName() {
            return getEstName() + getEstFullName();
        }
    }
}
