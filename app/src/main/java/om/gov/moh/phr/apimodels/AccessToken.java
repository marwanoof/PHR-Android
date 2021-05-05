package om.gov.moh.phr.apimodels;


import java.io.Serializable;

public class AccessToken implements Serializable {
    private static final AccessToken ourInstance = new AccessToken();
    private String accessTokenString;
    private String accessCivilId;
    private String accessLoginId;
    private String personName;
    private String image;


    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

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

    public String getAccessLoginId() {
        return accessLoginId;
    }

    public void setAccessLoginId(String accessLoginId) {
        this.accessLoginId = accessLoginId;
    }

    public void setAccessCivilId(String accessCivilId) {
        this.accessCivilId = accessCivilId;
    }
}
