package om.gov.moh.phr.apimodels;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ApiDependentsHolder implements Serializable {
    @SerializedName("result")
    private ArrayList<Dependent> result = new ArrayList<>();

    public ArrayList<Dependent> getResult() {
        return result;
    }

    public class Dependent {
        @SerializedName("dependentCivilId")
        private String dependentCivilId;

        @SerializedName("dependentName")
        private String dependentName;

        @SerializedName("relationType")
        private String relationType;

        public String getDependentCivilId() {
            if (dependentCivilId==null||TextUtils.isEmpty(dependentCivilId))
                return "";
            else
                return dependentCivilId;
        }

        public String getDependentName() {
            if (dependentName==null||TextUtils.isEmpty(dependentName))
                return "";
            else
                return dependentName;
        }

        public String getRelationType() {
            if (TextUtils.isEmpty(relationType))
                return "";
            else
                return relationType;
        }


        //TODO : delete set funs


        public void setDependentCivilId(String dependentCivilId) {
            this.dependentCivilId = dependentCivilId;
        }

        public void setDependentName(String dependentName) {
            this.dependentName = dependentName;
        }

        public void setRelationType(String relationType) {
            this.relationType = relationType;
        }
    }
}
