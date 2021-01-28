package om.gov.moh.phr.apimodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ApiVitalInfo implements Serializable {
    @SerializedName("result")
    private ApiMedicalHistoryInfo mResult;

    public ApiMedicalHistoryInfo getmResult() {
        return mResult;
    }

    public class ApiMedicalHistoryInfo implements Serializable {

        @SerializedName("allergy")
        private ArrayList<ApiAllergy> mAllergyResult;

        public ArrayList<ApiAllergy> getmAllergyResult() {
            return mAllergyResult;
        }

        @SerializedName("history")
        private ArrayList<ApiHistory> mHistoryResult;

        public ArrayList<ApiHistory> getmHistoryResult() {
            return mHistoryResult;
        }

        @SerializedName("problems")
        private ArrayList<ApiProblem> mProblemsResult;

        public ArrayList<ApiProblem> getmProblemsResult() {
            return mProblemsResult;
        }

    }
    public class ApiAllergy implements Serializable {

        public String getNote() {
            if (note==null)
                return "";
            else
            return note;
        }

        @SerializedName("note")
        private String note;

    }
    public class ApiHistory implements Serializable {

        public String getNote() {
            if(note==null)
                return "";
            else
            return note;
        }

        @SerializedName("note")
        private String note;

        @SerializedName("createdDate")
        private long createdDate;

        @SerializedName("estFullname")
        private String estFullname;

        public String getCreatedDate() {

            if (createdDate != 0) {
                Date date = new Date(createdDate);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                return simpleDateFormat.format(date);
            } else
                return "--";
        }

        public String getEstFullname() {
            return estFullname;
        }
    }
    public class ApiProblem implements Serializable {

        public String getDisease() {
            if(disease==null)
                return "";
            else
            return disease;
        }

        @SerializedName("disease")
        private String disease;

        @SerializedName("dateRecorded")
        private long dateRecorded;

        public String getDateRecorded() {
            if (dateRecorded != 0) {
                Date date = new Date(dateRecorded);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                return simpleDateFormat.format(date);
            } else
                return "--";
        }
    }
}
