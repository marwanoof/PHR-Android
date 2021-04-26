package om.gov.moh.phr.apimodels;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ApiOrganDonationHolder implements Serializable {
    @SerializedName("result")
    private OrganDonationJson result;

    public OrganDonationJson getResult() {
        return result;
    }

    public class OrganDonationJson {
        @SerializedName("donorId")
        private Long donorId;

        @SerializedName("mobileNo")
        private String mobileNo;

        @SerializedName("email")
        private String email;

        @SerializedName("familyMemberName")
        private String familyMemberName;

        @SerializedName("kidneysYn")
        private String kidneysYn;

        @SerializedName("liverYn")
        private String liverYn;

        @SerializedName("heartYn")
        private String heartYn;

        @SerializedName("lungsYn")
        private String lungsYn;

        @SerializedName("pancreasYn")
        private String pancreasYn;

        @SerializedName("corneasYn")
        private String corneasYn;

        @SerializedName("afterDeathYn")
        private String afterDeathYn;

        @SerializedName("relationCode")
        private int relationCode;

        @SerializedName("relationContactNo")
        private long relationContactNo;

        @SerializedName("otherRelationDesc")
        private String otherRelationDesc;

        public Long getDonorId() {
            return donorId;
        }

        public String getMobileNo() {
            if (mobileNo == null)
                return "";
            else
                return mobileNo;
        }

        public String getEmail() {
            if (email == null)
                return "";
            else
                return email;
        }

        public String getFamilyMemberName() {
            if (familyMemberName == null)
                return "";
            else
                return familyMemberName;
        }

        public String getKidneysYn() {
            if (kidneysYn == null || TextUtils.isEmpty(kidneysYn))
                return "";
            else
                return kidneysYn;
        }

        public String getLiverYn() {
            if (liverYn == null || TextUtils.isEmpty(liverYn))
                return "";
            else
                return liverYn;
        }

        public String getOtherRelationDesc() {
            if (otherRelationDesc == null)
                return "";
            else
                return otherRelationDesc;
        }

        public String getHeartYn() {
            if (heartYn == null || TextUtils.isEmpty(heartYn))
                return "";
            else
                return heartYn;
        }

        public String getLungsYn() {
            if (lungsYn == null || TextUtils.isEmpty(lungsYn))
                return "";
            else
                return lungsYn;
        }

        public String getPancreasYn() {
            if (pancreasYn == null || TextUtils.isEmpty(pancreasYn))
                return "";
            else
                return pancreasYn;
        }

        public String getCorneasYn() {
            if (corneasYn == null || TextUtils.isEmpty(corneasYn))
                return "";
            else
                return corneasYn;
        }

        public String getAfterDeathYn() {
            if (afterDeathYn == null || TextUtils.isEmpty(afterDeathYn))
                return "";
            else
                return afterDeathYn;
        }

        public int getRelationCode() {
            return relationCode;
        }

        public long getRelationContactNo() {
            return relationContactNo;
        }
    }
}
