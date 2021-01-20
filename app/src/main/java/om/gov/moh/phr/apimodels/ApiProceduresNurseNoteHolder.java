package om.gov.moh.phr.apimodels;

import android.text.format.DateFormat;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ApiProceduresNurseNoteHolder implements Serializable {
    @SerializedName("result")
    private ArrayList<NurseNote> result;

    public ArrayList<NurseNote> getResult() {
        return result;
    }

    public class NurseNote implements Serializable {
        @SerializedName("id")
        private int id;

        @SerializedName("authorName")
        private String authorName;

        @SerializedName("text")
        private String text;

        @SerializedName("time")
        private long time;

        @SerializedName("procedureId")
        private String procedureId;

        public int getId() {
            return id;
        }

        public String getAuthorName() {
            return authorName;
        }

        public String getText() {
            if (text == null)
                return "";
            else
                return text;
        }

        public String getTime() {
            if(time!=0) {
                Date date = new Date(time);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy hh:mm a", Locale.ENGLISH);
                String dateResult = simpleDateFormat.format(date);
                return dateResult;
            }else
                return "--";
        }

        public String getProcedureId() {
            return procedureId;
        }
    }
}
