package om.gov.moh.phr.apimodels;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ApiProceduresReportsHolder implements Serializable {
    @SerializedName("reportId")
    private String reportId;

    @SerializedName("mediaSubType")
    private String mediaSubType;

    @SerializedName("mediaString")
    private String mediaString;


    @SerializedName("creationTime")
    private long creationTime;

    @SerializedName("estCode")
    private long estCode;

    public long getEstCode() {
        return estCode;
    }

    public void setEstCode(long estCode) {
        this.estCode = estCode;
    }

    @SerializedName("procedureDoneDate")
    private long procedureDoneDate;

    public String getReportId() {
        return reportId;
    }

    public long getProcedureDoneDate() {
        return procedureDoneDate;
    }

    public void setProcedureDoneDate(long procedureDoneDate) {
        this.procedureDoneDate = procedureDoneDate;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }


    @SerializedName("estName")
    private String estName;

    @SerializedName("profileCode")
    private long profileCode;

    @SerializedName("procedureId")
    private String procedureId;

    public String getProcedureId() {
        return procedureId;
    }

    @SerializedName("startTime")
    private long startTime;

    @SerializedName("name")
    private String name;

    @SerializedName("procName")
    private String procName;

    @SerializedName("reportDoneDate")
    private long reportDoneDate;

    public String getProcName() {
        return procName;
    }

    public long getReportDoneDate() {
        return reportDoneDate;
    }

    public void setReportDoneDate(long reportDoneDate) {
        this.reportDoneDate = reportDoneDate;
    }

    public void setProcName(String procName) {
        this.procName = procName;
    }

    public void setEstName(String estName) {
        this.estName = estName;
    }

    public void setProfileCode(long profileCode) {
        this.profileCode = profileCode;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEstName() {
        if (TextUtils.isEmpty(estName))
            return "";
        else
            return estName;
    }


    public long getProfileCode() {
        return profileCode;
    }

    public long getStartTime() {
        return startTime;
    }


    public String getName() {
        if (TextUtils.isEmpty(name))
            return "";
        else
            return name;
    }

    public void setMediaSubType(String mediaSubType) {
        this.mediaSubType = mediaSubType;
    }

    public void setMediaString(String mediaString) {
        this.mediaString = mediaString;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public String getMediaSubType() {
        return mediaSubType;
    }

    public String getMediaString() {
        return mediaString;
    }

    public long getCreationTime() {
        return creationTime;
    }
}
