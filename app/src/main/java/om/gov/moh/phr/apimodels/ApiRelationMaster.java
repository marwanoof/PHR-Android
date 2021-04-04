package om.gov.moh.phr.apimodels;

import android.content.Intent;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ApiRelationMaster implements Serializable {
    @SerializedName("result")
    ArrayList<RelationMast> result = new ArrayList<>();

    public ArrayList<RelationMast> getResult() {
        return result;
    }
    public class RelationMast {
        @SerializedName("relationCode")
        private Integer relationCode;

        @SerializedName("relationName")
        private String relationName;

        @SerializedName("relationNameNls")
        private String relationNameNls;

        @SerializedName("activeYn")
        private String activeYn;

        public Integer getRelationCode() {
            return relationCode;
        }

        public String getRelationName() {
            if(relationName==null)
                return "";
            else
            return relationName;
        }

        public String getRelationNameNls() {
            if(relationNameNls==null)
                return relationName;
            else
            return relationNameNls;
        }

        public String getActiveYn() {
            return activeYn;
        }
    }
}
