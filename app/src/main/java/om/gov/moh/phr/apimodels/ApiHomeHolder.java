package om.gov.moh.phr.apimodels;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import om.gov.moh.phr.R;

public class ApiHomeHolder implements Serializable{
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

        @SerializedName("clinicalNotesEnableYN")
        private String clinicalNotesEnableYN;

        public String getClinicalNotesEnableYN() {
            return clinicalNotesEnableYN;
        }

        @SerializedName("chatEnableYN")
        private String chatEnableYN;

        public String getChatEnableYN() {
            return chatEnableYN;
        }

        @SerializedName("appointmentEnableYN")
        private String appointmentEnableYN;

        public String getAppointmentEnableYN() {
            return appointmentEnableYN;
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

        @SerializedName("nationalityNls")
        private String nationalityNls;

        @SerializedName("bloodGroup")
        private String bloodGroup;

        @SerializedName("dependentCount")
        private int dependentCount;

        @SerializedName("mobile")
        private String mobile;

        @SerializedName("cardExpiryDate")
        private long cardExpiryDate;

        @SerializedName("birthDown")
        private String birthDown;

        @SerializedName("image")
        private String image;

        public String getNationalityNls() {
            if (nationalityNls == null)
                return nationality;
            else
                return nationalityNls;
        }

        public String getFirstNameNls() {
            if (firstNameNls == null)
                return firstName;
            else
                return firstNameNls;
        }

        public String getSecondNameNls() {
            if (secondNameNls == null)
                return secondName;
            else
                return secondNameNls;
        }

        public String getThirdNameNls() {
            if (thirdNameNls == null)
                return thirdName;
            else
                return thirdNameNls;
        }

        public String getFourthNameNls() {
            if (fourthNameNls == null)
                return fourthName;
            else
                return fourthNameNls;
        }

        public String getFifthNameNls() {
            if (fifthNameNls == null)
                return fifthName;
            else
                return fifthNameNls;
        }

        public String getSixthNameNls() {
            if (sixthNameNls == null)
                return sixthName;
            else
                return sixthNameNls;
        }

        public long getCivilId() {
            return civilId;
        }


        public String getFirstName() {
            if (firstName == null)
                return "";
            else
                return firstName;
        }

        public String getSecondName() {
            if (secondName == null)
                return "";
            else
                return secondName;
        }

        public String getThirdName() {
            if (thirdName == null)
                return "";
            else
                return thirdName;
        }

        public String getFourthName() {
            if (fourthName == null)
                return "";
            else
                return fourthName;
        }

        public String getFifthName() {
            if (fifthName == null)
                return "";
            else
                return fifthName;
        }

        public String getSixthName() {
            if (sixthName == null)
                return "";
            else
                return sixthName;
        }

        public String getMobile() {
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
            if (gender == null || TextUtils.isEmpty(gender))
                return "";
            else
                return gender;
        }

        public String getAge() {
            if (TextUtils.isEmpty(age) || age == null)
                return "--";
            else
                return age;
        }

        public String getNationality() {
            if (nationality == null)
                return "";
            else
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
            if (dependentNameNls == null)
                return dependentName;
            else
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
            if (menuName == null)
                return "";
            else
                return menuName;
        }

        public String getActiveYn() {
            return activeYn;
        }

        public String getIconClass() {
            if (iconClass == null)
                return "";
            else
                return iconClass;
        }

        public String getMenuDesc() {
            if (menuDesc == null)
                return "";
            else
                return menuDesc;
        }

        public String getMenuDescNls() {
            if (menuDescNls == null)
                return "";
            else
                return menuDescNls;
        }

        public String getMenuNameNls() {
            if (menuNameNls == null)
                return menuName;
            else
                return menuNameNls;
        }
    }

    public class ApiRecentVitals implements Serializable {
        public ApiRecentVitals(String name, String type, String unit, String value, String unitNls, String showHomePageYn, String vitalNameNls) {
            this.name = name;
            this.type = type;
            this.unit = unit;
            this.value = value;
            this.unitNls = unitNls;
            this.showHomePageYn = showHomePageYn;
            this.vitalNameNls = vitalNameNls;
        }

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


        @SerializedName("vitalDate")
        private long vitalDate;

        public String getVitalDate() {
            String date ;
            if(vitalDate!=0) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(vitalDate);
                Date serverDate = new Date(vitalDate);
                SimpleDateFormat dateFormat = new SimpleDateFormat("E, MMM dd yyyy hh:mm a", Locale.ENGLISH);
                 date = dateFormat.format(serverDate);
            }else
                date = "";
            return date;
        }

        public String getUnitNls() {
            if (unitNls == null || TextUtils.isEmpty(unitNls))
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
            if (showVitalPageYn == null || TextUtils.isEmpty(showVitalPageYn))
                return "";
            else
                return showVitalPageYn;
        }

        public String getVitalNameNls() {
            if (vitalNameNls == null || TextUtils.isEmpty(vitalNameNls))
                return name;
            else
                return vitalNameNls;
        }

        @SerializedName("vitalNameNls")
        private String vitalNameNls;

        public String getUnit() {
            if (unit == null || TextUtils.isEmpty(unit))
                return "";
            else
                return unit;
        }

        public String getName() {
            if (name == null || TextUtils.isEmpty(name))
                return "";
            else
                return name;
        }

        public String getValue() {
            if (value == null || TextUtils.isEmpty(value))
                return "--";
            else
                return value;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public void setUnitNls(String unitNls) {
            this.unitNls = unitNls;
        }

        public void setLeftColor(String leftColor) {
            this.leftColor = leftColor;
        }

        public void setRightColor(String rightColor) {
            this.rightColor = rightColor;
        }

        public void setShowHomePageYn(String showHomePageYn) {
            this.showHomePageYn = showHomePageYn;
        }

        public void setShowVitalPageYn(String showVitalPageYn) {
            this.showVitalPageYn = showVitalPageYn;
        }

        public void setVitalDate(long vitalDate) {
            this.vitalDate = vitalDate;
        }

        public void setVitalNameNls(String vitalNameNls) {
            this.vitalNameNls = vitalNameNls;
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
            if (reservationId == null || TextUtils.isEmpty(reservationId))
                return "";
            else
                return reservationId;
        }

        public String getDescription() {
            if (description == null || TextUtils.isEmpty(description))
                return "";
            else
                return description;
        }

        public String getEstName() {
            if (estName == null || TextUtils.isEmpty(estName))
                return "";
            else
                return " | " + estName;
        }

        public String getEstCode() {
            if (estCode == null || TextUtils.isEmpty(estCode))
                return "";
            else
                return estCode;
        }

        public void setDescription(String description) {
            this.description = description;
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
            if (createdBy == null || TextUtils.isEmpty(createdBy))
                return "";
            else
                return createdBy;
        }

        public int getUnreadCount() {
            return unreadCount;
        }

        public String getCreatedName() {
            if (createdName == null || TextUtils.isEmpty(createdName))
                return "";
            else
                return createdName;
        }

        public String getCreatedDate() {
            if (createdDate == null)
                return "--";
            else
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
            if (estNameNls==null||TextUtils.isEmpty(estNameNls))
                return estName;
            else
                return estNameNls;
        }

        public String getEstPatientId() {
            if (estPatientId==null||TextUtils.isEmpty(estPatientId))
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
            if (estCode==null||TextUtils.isEmpty(estCode))
                return "";
            else
                return estCode;
        }

        public String getEstName() {
            if (estName==null||TextUtils.isEmpty(estName))
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
            if(referralBy==null)
                return "";
            else
            return referralBy;
        }

        public long getSendDate() {
            return sendDate;
        }

        public String getRefInstitute() {
            if(refInstitute==null)
                return "";
            else
            return refInstitute;
        }

        public String getDescription() {
            if(TextUtils.isEmpty(description))
                return null;
            else
            return description;
        }

        @SerializedName("description")
        private String description;
    }
}
