package om.gov.moh.phr.apimodels;

import com.google.gson.annotations.SerializedName;

public class ApiOrganDonationHolder {
    @SerializedName("result")
    private OrganDonationJson result;

    public OrganDonationJson getResult() {
        return result;
    }

    public class OrganDonationJson{
        @SerializedName("donorId")
        private long donorId;

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

        public long getDonorId() {
            return donorId;
        }

        public String getMobileNo() {
            return mobileNo;
        }

        public String getEmail() {
            return email;
        }

        public String getFamilyMemberName() {
            return familyMemberName;
        }

        public String getKidneysYn() {
            return kidneysYn;
        }

        public String getLiverYn() {
            return liverYn;
        }

        public String getHeartYn() {
            return heartYn;
        }

        public String getLungsYn() {
            return lungsYn;
        }

        public String getPancreasYn() {
            return pancreasYn;
        }

        public String getCorneasYn() {
            return corneasYn;
        }

        public String getAfterDeathYn() {
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
