package om.gov.moh.phr.apimodels;


import java.io.Serializable;

public class AccessToken implements Serializable {
    private static final AccessToken ourInstance = new AccessToken();
    private String accessTokenString;
    private String accessCivilId;

    private AccessToken() {
    }

    public static AccessToken getInstance() {
        return ourInstance;
    }

    public String getAccessTokenString() {
        return accessTokenString;
    }

    public void setAccessTokenString(String accessTokenString) {
        this.accessTokenString = accessTokenString;
    }

    public String getAccessCivilId() {
        return accessCivilId;
    }

    public void setAccessCivilId(String accessCivilId) {
        this.accessCivilId = accessCivilId;
    }
}
