package om.gov.moh.phr.apimodels;

import java.io.Serializable;

public class Notification {
    private String keyId;
    private String createdDate;
    private String title;
    private String body;
    private String pnsType;
    private String labType;

    public String getLabType() {
        return labType;
    }

    public void setLabType(String labType) {
        this.labType = labType;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPnsType() {
        return pnsType;
    }

    public void setPnsType(String pnsType) {
        this.pnsType = pnsType;
    }
}
