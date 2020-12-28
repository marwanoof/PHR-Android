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

public class ApiHomeHolder {
    @SerializedName("result")
    private ApiHomeHolder.ApiHome mResult;

    public ApiHome getmResult() {
        return mResult;
    }

    public class ApiHome implements Serializable {
        @SerializedName("home")
        private ApiHomeHolder.ApiHomeItems mHome;

        public ApiHomeItems getmHome() {
            return mHome;
        }
    }

    public class ApiHomeItems implements Serializable {
        @SerializedName("demographics")
        private ApiHomeHolder.ApiDemographics mDemographics;

        public ApiDemographics getmDemographics() {
            return mDemographics;
        }

        @SerializedName("dependents")
        private ArrayList<ApiHomeHolder.ApiDependents> mDependents;

        public ArrayList<ApiDependents> getmDependents() {
            return mDependents;
        }

        @SerializedName("mainMenus")
        private ArrayList<ApiMainMenus> mMainMenus;

        public ArrayList<ApiMainMenus> getmMainMenus() {
            return mMainMenus;
        }

        @SerializedName("recentVitals")
        private ArrayList<ApiRecentVitals> mRecentVitals;

        public ArrayList<ApiRecentVitals> getmRecentVitals() {
            return mRecentVitals;
        }

        @SerializedName("vitalData")
        private ArrayList<ApiVitalData> mVitalData;

        public ArrayList<ApiVitalData> getmVitalData() {
            return mVitalData;
        }

        @SerializedName("appointments")
        private ArrayList<ApiAppointments> mAppointments;

        public ArrayList<ApiAppointments> getmAppointments() {
            return mAppointments;
        }

        @SerializedName("chatMessage")
        private ArrayList<ApiChatMessages> mChatMessages;

        public ArrayList<ApiChatMessages> getmChatMessages() {
            return mChatMessages;
        }

        @SerializedName("patients")
        ArrayList<Patients> institutesArrayList = new ArrayList<>();

        public ArrayList<Patients> getInstitutesArrayList() {
            return institutesArrayList;
        }

        @SerializedName("referrals")
        ArrayList<Referrals> referralsArrayList = new ArrayList<>();

        public ArrayList<Referrals> getReferralsArrayList() {
            return referralsArrayList;
        }
    }

    public class ApiDemographics implements Serializable {
        @SerializedName("civilId")
        private long civilId;

        @SerializedName("firstName")
        private String firstName;

        @SerializedName("secondName")
        private String secondName;

        @SerializedName("thirdName")
        private String thirdName;

        @SerializedName("fourthName")
        private String fourthName;

        @SerializedName("fifthName")
        private String fifthName;

        @SerializedName("sixthName")
        private String sixthName;

        @SerializedName("firstNameNls")
        private String firstNameNls;

        @SerializedName("secondNameNls")
        private String secondNameNls;

        @SerializedName("thirdNameNls")
        private String thirdNameNls;

        @SerializedName("fourthNameNls")
        private String fourthNameNls;

        @SerializedName("fifthNameNls")
        private String fifthNameNls;

        @SerializedName("sixthNameNls")
        private String sixthNameNls;

        @SerializedName("dateOfBirth")
        private long dateOfBirth;

        @SerializedName("gender")
        private String gender;

        @SerializedName("age")
        private String age;

        @SerializedName("nationality")
        private String nationality;

        @SerializedName("bloodGroup")
        private String bloodGroup;

        @SerializedName("dependentCount")
        private int dependentCount;

        @SerializedName("mobile")
        private long mobile;

        @SerializedName("cardExpiryDate")
        private long cardExpiryDate;

        @SerializedName("birthDown")
        private String birthDown;

        @SerializedName("image")
        private String image;

        public String getFirstNameNls() {
            return firstNameNls;
        }

        public String getSecondNameNls() {
            return secondNameNls;
        }

        public String getThirdNameNls() {
            return thirdNameNls;
        }

        public String getFourthNameNls() {
            return fourthNameNls;
        }

        public String getFifthNameNls() {
            return fifthNameNls;
        }

        public String getSixthNameNls() {
            return sixthNameNls;
        }

        public long getCivilId() {
            return civilId;
        }


        public String getFirstName() {
            if (TextUtils.isEmpty(firstName) || firstName == null)
                return "";
            else
                return firstName;
        }

        public String getSecondName() {
            if (TextUtils.isEmpty(secondName) || secondName == null)
                return "";
            else
                return secondName;
        }

        public String getThirdName() {
            if (TextUtils.isEmpty(thirdName) || thirdName == null)
                return "";
            else
                return thirdName;
        }

        public String getFourthName() {
            if (TextUtils.isEmpty(fourthName) || fourthName == null)
                return "";
            else
                return fourthName;
        }

        public String getFifthName() {
            if (TextUtils.isEmpty(fifthName) || fifthName == null)
                return "";
            else
                return fifthName;
        }

        public String getSixthName() {
            if (TextUtils.isEmpty(sixthName) || sixthName == null)
                return "";
            else
                return sixthName;
        }

        public long getMobile() {
            return mobile;
        }

        public long getCardExpiryDate() {
            return cardExpiryDate;
        }

        public String getBirthDown() {
            return birthDown;
        }

        public String getImage() {
            return image;
        }

        public long getDateOfBirth() {
            return dateOfBirth;
        }

        public String getGender() {
            return gender;
        }

        public String getAge() {
            if (TextUtils.isEmpty(age) || age == null)
                return "--";
            else
                return age;
        }

        public String getNationality() {
            return nationality;
        }

        public String getBloodGroup() {
            if (TextUtils.isEmpty(bloodGroup) || bloodGroup == null)
                return "--";
            else
                return bloodGroup;
        }

        public int getDependentCount() {
            return dependentCount;
        }
    }

    public class ApiDependents implements Serializable {
        @SerializedName("runId")
        private long runId;

        @SerializedName("mpiId")
        private long mpiId;

        @SerializedName("civilId")
        private long civilId;

        @SerializedName("dependentCivilId")
        private long dependentCivilId;

        @SerializedName("dependentName")
        private String dependentName;

        @SerializedName("dependentNameNls")
        private String dependentNameNls;

        @SerializedName("relationType")
        private String relationType;

        public String getDependentNameNls() {
            return dependentNameNls;
        }

        public long getRunId() {
            return runId;
        }

        public long getMpiId() {
            return mpiId;
        }

        public long getCivilId() {
            return civilId;
        }

        public long getDependentCivilId() {
            return dependentCivilId;
        }

        public String getDependentName() {
            if (TextUtils.isEmpty(dependentName) || dependentName == null)
                return "";
            else
                return dependentName;
        }

        public String getRelationType() {
            if (TextUtils.isEmpty(relationType) || relationType == null)
                return "";
            else
                return relationType;
        }
    }

    public class ApiMainMenus implements Serializable {
        @SerializedName("menuId")
        private int menuId;

        @SerializedName("menuName")
        private String menuName;

        @SerializedName("menuNameNls")
        private String menuNameNls;

        @SerializedName("activeYn")
        private String activeYn;

        @SerializedName("iconClass")
        private String iconClass;

        @SerializedName("menuDesc")
        private String menuDesc;

        @SerializedName("menuDescNls")
        private String menuDescNls;

        public int getMenuId() {
            return menuId;
        }

        public String getMenuName() {
            return menuName;
        }

        public String getActiveYn() {
            return activeYn;
        }

        public String getIconClass() {
            return iconClass;
        }

        public String getMenuDesc() {
            return menuDesc;
        }

        public String getMenuDescNls() {
            return menuDescNls;
        }

        public String getMenuNameNls() {
            return menuNameNls;
        }
    }

    public class ApiRecentVitals implements Serializable {
        @SerializedName("name")
        private String name;



        @SerializedName("type")
        private String type;

        public String getType() {
            return type;
        }

        @SerializedName("unit")
        private String unit;

        @SerializedName("value")
        private String value;

        @SerializedName("unitNls")
        private String unitNls;

        @SerializedName("leftColor")
        private String leftColor;

        @SerializedName("rightColor")
        private String rightColor;

        @SerializedName("showHomePageYn")
        private String showHomePageYn;

        @SerializedName("showVitalPageYn")
        private String showVitalPageYn;

        public String getUnitNls() {
            if (TextUtils.isEmpty(unitNls))
                return "";
            else
            return unitNls;
        }

        public String getLeftColor() {
            if (TextUtils.isEmpty(leftColor))
                return "";
            else
            return leftColor;
        }

        public String getRightColor() {
            if (TextUtils.isEmpty(rightColor))
                return "";
            else
            return rightColor;
        }

        public String getShowHomePageYn() {
            if (TextUtils.isEmpty(showHomePageYn))
                return "";
            else
            return showHomePageYn;
        }

        public String getShowVitalPageYn() {
            if (TextUtils.isEmpty(showVitalPageYn))
                return "";
            else
            return showVitalPageYn;
        }

        public String getVitalNameNls() {
            if (TextUtils.isEmpty(vitalNameNls))
                return "";
            else
                return vitalNameNls;
        }

        @SerializedName("vitalNameNls")
        private String vitalNameNls;

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
                return "--";
            else
                return value;
        }
    }

    public class ApiVitalData implements Serializable {

    }

    public class ApiAppointments implements Serializable {
            @SerializedName("reservationId")
            private String reservationId;

            @SerializedName("description")
            private String description;

            @SerializedName("estName")
            private String estName;

            /**
             * estCode is label as "lastModifiedBy" in the response
             */
            @SerializedName("lastModifiedBy")
            private String estCode;

            public String getReservationId() {
                if (TextUtils.isEmpty(reservationId))
                    return "";
                else
                    return reservationId;
            }

            public String getDescription() {
                if (TextUtils.isEmpty(description))
                    return "";
                else
                    return description;
            }

            public String getEstName() {
                if (TextUtils.isEmpty(estName))
                    return "";
                else
                    return " | " + estName;
            }

            public String getEstCode() {
                if (TextUtils.isEmpty(estCode))
                    return "";
                else
                    return estCode;
            }
    }

    public class ApiChatMessages implements Serializable {
        @SerializedName("messageId")
        private long messageId;

        @SerializedName("createdBy")
        private String createdBy;

        @SerializedName("unreadCount")
        private int unreadCount;

        @SerializedName("createdName")
        private String createdName;

        @SerializedName("createdDate")
        private String createdDate;

        @SerializedName("isNew")
        private boolean isNew;

        public boolean isNew() {
            return isNew;
        }

        public void setNew(boolean aNew) {
            isNew = aNew;
        }

        public long getMessageId() {
            return messageId;
        }

        public String getCreatedBy() {
            return createdBy;
        }

        public int getUnreadCount() {
            return unreadCount;
        }

        public String getCreatedName() {
            return createdName;
        }

        public String getCreatedDate() {
            return createdDate;
        }
    }
    public class Patients implements Serializable {

        @SerializedName("estPatientId")
        private String estPatientId;

        @SerializedName("estName")
        private String estName;

        @SerializedName("estNameNls")
        private String estNameNls;

        @SerializedName("estCode")
        private String estCode;

        @SerializedName("estTypeCode")
        private int estTypeCode;

        private boolean isPending;

        public String getEstNameNls() {
            if (TextUtils.isEmpty(estNameNls))
                return "";
            else
                return estNameNls;
        }

        public String getEstPatientId() {
            if (TextUtils.isEmpty(estPatientId))
                return "";
            else
                return estPatientId;
        }

        public int getEstTypeCode() {
            return estTypeCode;
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
    public class Referrals implements Serializable {
        @SerializedName("referralBy")
        private String referralBy;

        @SerializedName("sendDate")
        private long sendDate;

        @SerializedName("refInstitute")
        private String refInstitute;

        public String getReferralBy() {
            return referralBy;
        }

        public long getSendDate() {
            return sendDate;
        }

        public String getRefInstitute() {
            return refInstitute;
        }

        public String getDescription() {
            return description;
        }

        @SerializedName("description")
        private String description;
    }
}
