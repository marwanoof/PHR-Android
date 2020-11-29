package om.gov.moh.phr.apimodels;

import android.text.TextUtils;
import android.text.format.DateFormat;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ApiProceduresReportsHolder implements Serializable {
    @SerializedName("result")
    private ArrayList<ProceduresByEncounter> result;

    public ArrayList<ProceduresByEncounter> getResult() {
        return result;
    }

    public class ProceduresByEncounter implements Serializable{
        @SerializedName("encounterDate")
        private long encounterDate;

        @SerializedName("encounterDateFormat")
        private String encounterDateFormat;

        @SerializedName("encounterId")
        private String encounterId;

        @SerializedName("estFullName")
        private String estFullName;

        @SerializedName("procedures")
        private ArrayList<Procedures> procedures;

        public String getEncounterDate() {
            Date date = new Date(encounterDate);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy",Locale.ENGLISH);
            String dateResult = simpleDateFormat.format(date);
            return dateResult;
        }

        public String getEncounterDateFormat() {
            return encounterDateFormat;
        }

        public String getEncounterId() {
            return encounterId;
        }

        public String getEstFullName() {
            return estFullName;
        }

        public ArrayList<Procedures> getProcedures() {
            return procedures;
        }
    }

    public class Procedures implements Serializable{

        @SerializedName("procedureId")
        private String procedureId;

        @SerializedName("procedureDoneDate")
        private long procedureDoneDate;

        @SerializedName("procedure")
        private ArrayList<Procedure> procedure;

        @SerializedName("performer")
        private ArrayList<Performer> performer;

        @SerializedName("notes")
        private ArrayList<Notes> notes;

        @SerializedName("status")
        private String status;

        @SerializedName("startTime")
        private long startTime;

        @SerializedName("endTime")
        private long endTime;

        @SerializedName("locationCode")
        private String locationCode;

        @SerializedName("reasonText")
        private String reasonText;

        @SerializedName("profileCode")
        private int profileCode;

        @SerializedName("outcomeDesc")
        private String outcomeDesc;

        @SerializedName("estName")
        private String estName;

        @SerializedName("encounterId")
        private String encounterId;

        @SerializedName("diagnosis")
        private String diagnosis;

        @SerializedName("estFullname")
        private String estFullname;

        @SerializedName("reportId")
        private String reportId;

        @SerializedName("patientId")
        private String patientId;

        @SerializedName("encounterDate")
        private long encounterDate;

        @SerializedName("patientClass")
        private String patientClass;

        @SerializedName("estFullnameNls")
        private String estFullnameNls;

        public String getProcedureId() {
            return procedureId;
        }

        public long getProcedureDoneDate() {
            return procedureDoneDate;
        }

        public ArrayList<Procedure> getProcedure() {
            return procedure;
        }

        public ArrayList<Performer> getPerformer() {
            return performer;
        }

        public ArrayList<Notes> getNotes() {
            return notes;
        }

        public String getStatus() {
            return status;
        }

        public long getStartTime() {
            return startTime;
        }

        public long getEndTime() {
            return endTime;
        }

        public String getLocationCode() {
            return locationCode;
        }

        public String getReasonText() {
            return reasonText;
        }

        public int getProfileCode() {
            return profileCode;
        }

        public String getOutcomeDesc() {
            return outcomeDesc;
        }

        public String getEstName() {
            return estName;
        }

        public String getEncounterId() {
            return encounterId;
        }

        public String getDiagnosis() {
            return diagnosis;
        }

        public String getEstFullname() {
            return estFullname;
        }

        public String getReportId() {
            return reportId;
        }

        public String getPatientId() {
            return patientId;
        }

        public long getEncounterDate() {
            return encounterDate;
        }

        public String getPatientClass() {
            return patientClass;
        }

        public String getEstFullnameNls() {
            return estFullnameNls;
        }
    }
    public class Procedure implements Serializable{
        @SerializedName("code")
        private String code;

        @SerializedName("name")
        private String name;

        @SerializedName("typeCode")
        private int typeCode;

        @SerializedName("typeName")
        private String typeName;

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public int getTypeCode() {
            return typeCode;
        }

        public String getTypeName() {
            return typeName;
        }
    }
    public class Performer implements Serializable{
        @SerializedName("id")
        private int id;

        @SerializedName("procedureId")
        private String procedureId;

        @SerializedName("roleCode")
        private String roleCode;

        @SerializedName("actorName")
        private String actorName;

        @SerializedName("roleDesc")
        private String roleDesc;

        public int getId() {
            return id;
        }

        public String getProcedureId() {
            return procedureId;
        }

        public String getRoleCode() {
            return roleCode;
        }

        public String getActorName() {
            return actorName;
        }

        public String getRoleDesc() {
            return roleDesc;
        }
    }

    public class Notes implements Serializable{
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
            return text;
        }

        public long getTime() {
            return time;
        }

        public String getProcedureId() {
            return procedureId;
        }
    }
}
