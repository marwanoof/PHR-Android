package om.gov.moh.phr.apimodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ApiScheduleImmunizationHolder {
    @SerializedName("result")
    private ArrayList<ApiScheduleImmunizationHolder.ApiScheduleImmunizationInfo> mResult;

    public ArrayList<ApiScheduleImmunizationHolder.ApiScheduleImmunizationInfo> getmResult() {
        return mResult;
    }

    public class ApiScheduleImmunizationInfo implements Serializable {




    }
}
