package om.gov.moh.phr.apimodels;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ApiPatientHolder {
    @SerializedName("result")
    private ArrayList<ApiDemographicsHolder.ApiDemographicItem.Patients> result = new ArrayList<>();

    public ArrayList<ApiDemographicsHolder.ApiDemographicItem.Patients> getResult() {
        return result;
    }
}
