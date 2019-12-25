package om.gov.moh.phr.apimodels;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import om.gov.moh.phr.R;

public class ApiDemographicsHolder {
    @SerializedName("result")
    private ApiDemographicItem mResult;

    public ApiDemographicItem getmResult() {
        return mResult;
    }

    public class ApiDemographicItem implements Serializable {

        @SerializedName("patients")
        ArrayList<Patients> institutesArrayList = new ArrayList<>();
        @SerializedName("addresses")
        ArrayList<Addressees> addressesArrayList = new ArrayList<>();
        @SerializedName("recentVitals")
        ArrayList<RecentVitals> recentVitalsArrayList = new ArrayList<>();
        @SerializedName("flags")
        ArrayList<Alerts> alertsArrayList = new ArrayList<>();
        @SerializedName("civilId")
        private String civilId;
        @SerializedName("fullName")
        private String fullName;
        @SerializedName("dob")
        private String dob;
        @SerializedName("newAge")
        private String newAge;
        @SerializedName("genderfull")
        private String genderfull;
        @SerializedName("image")
        private String personPhoto;
        @SerializedName("nationalityDesc")
        private String nationalityDesc;
        @SerializedName("bloodGroup")
        private String bloodGroup;
        @SerializedName("donorCount")
        private String donorCount;

        public String getBloodGroup() {
            if (TextUtils.isEmpty(bloodGroup))
                return "--";
            else
            return bloodGroup;
        }

        public void setBloodGroup(String bloodGroup) {
            this.bloodGroup = bloodGroup;
        }

        public String getDonorCount() {
            if (TextUtils.isEmpty(donorCount))
                return "--";
            else
            return donorCount;
        }

        public void setDonorCount(String donorCount) {
            this.donorCount = donorCount;
        }

        public String getCivilId() {
            if (TextUtils.isEmpty(civilId))
                return "";
            else
                return civilId;
        }

        public String getFullName() {
            if (TextUtils.isEmpty(fullName))
                return "";
            else
                return fullName;
        }

        public String getDob() {
            if (TextUtils.isEmpty(dob))
                return "";
            else
                return new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date(Long.parseLong(dob)));
        }

        public String getLongDob() {
            return dob;
        }

        public String getNewAge() {
            if (TextUtils.isEmpty(newAge))
                return "";
            else
                return newAge;
        }

        public String getGenderfull() {
            if (TextUtils.isEmpty(genderfull))
                return "";
            else
                return genderfull;
        }

        public Bitmap getPersonPhoto(Context context) {
            if (TextUtils.isEmpty(personPhoto)) {
                return BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.avatar);
            } else {
                //decode base64 string to image
                byte[] imageBytes = Base64.decode(personPhoto, Base64.DEFAULT);
                Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                return decodedImage;
            }
        }

        public String getPersonPhotoString() {
            return personPhoto;
        }

        public String getNationalityDesc() {
            if (TextUtils.isEmpty(nationalityDesc))
                return "";
            else
                return nationalityDesc;
        }

        public void setNationalityDesc(String nationalityDesc) {
            this.nationalityDesc = nationalityDesc;
        }

        public ArrayList<Patients> getInstitutesArrayList() {
            return institutesArrayList;
        }

        public void setInstitutesArrayList(ArrayList<Patients> institutesArrayList) {
            this.institutesArrayList = institutesArrayList;
        }

        public ArrayList<Addressees> getAddressesArrayList() {
            return addressesArrayList;
        }

        public void setAddressesArrayList(ArrayList<Addressees> addressesArrayList) {
            this.addressesArrayList = addressesArrayList;
        }

        public ArrayList<Alerts> getAlertsArrayList() {
            return alertsArrayList;
        }

        public void setAlertsArrayList(ArrayList<Alerts> alertsArrayList) {
            this.alertsArrayList = alertsArrayList;
        }

        public ArrayList<RecentVitals> getRecentVitalsArrayList() {
            return recentVitalsArrayList;
        }

        public class Patients implements Serializable {

            @SerializedName("estPatientId")
            private String estPatientId;

            @SerializedName("estName")
            private String estName;

            @SerializedName("estCode")
            private String estCode;

            private boolean isPending;

            public String getEstPatientId() {
                if (TextUtils.isEmpty(estPatientId))
                    return "";
                else
                    return estPatientId;
            }

            public void setEstPatientId(String estPatientId) {
                this.estPatientId = estPatientId;
            }

            public String getEstCode() {
                if (TextUtils.isEmpty(estCode))
                    return "";
                else
                    return estCode;
            }

            public String getEstName() {
                if (TextUtils.isEmpty(estName))
                    return "";
                else
                    return estName;
            }

            public void setEstName(String estName) {
                this.estName = estName;
            }

            public boolean getIsPending() {
                return isPending;
            }

            public void setIsPending(boolean pending) {
                isPending = pending;
            }
        }

        public class Addressees {
            private String walayatName;
            private String villageName;

            public String getWalayatName() {
                if (TextUtils.isEmpty(walayatName))
                    return "";
                else
                    return walayatName;
            }

            public void setWalayatName(String walayatName) {
                this.walayatName = walayatName;
            }

            public String getVillageName() {
                if (TextUtils.isEmpty(villageName))
                    return "";
                else
                    return villageName;
            }

            public void setVillageName(String villageName) {
                this.villageName = villageName;
            }
        }

        public class Alerts {
            @SerializedName("codeDescription")
            private String codeDescription;

            public String getCodeDescription() {
                if (TextUtils.isEmpty(codeDescription))
                    return "";
                else
                    return codeDescription;
            }
        }

        public class RecentVitals {
            @SerializedName("name")
            private String name;

            @SerializedName("unit")
            private String unit;

            @SerializedName("value")
            private String value;

            //TODO delete constructor
            public RecentVitals() {
            }

            public RecentVitals(String name, String value, String unit) {
                this.name = name;
                this.value = value;
                this.unit = unit;
            }

            public String getUnit() {
                if (TextUtils.isEmpty(unit))
                    return "";
                else
                return unit;
            }

            public String getName() {
                if (TextUtils.isEmpty(name))
                    return "";
                else
                    return name;
            }

            public String getValue() {
                if (TextUtils.isEmpty(value))
                    return "";
                else
                    return value;
            }

            /*TODO : delete set methods*/

            public void setName(String name) {
                this.name = name;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }
    }


}
