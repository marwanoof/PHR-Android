package om.gov.moh.phr.models;

import java.io.Serializable;

public class AppCurrentUser implements Serializable {
    private static final AppCurrentUser ourInstance = new AppCurrentUser();
    private boolean isParent;
    private String civilId;


    public static AppCurrentUser getInstance() {
        return ourInstance;
    }


    public boolean getIsParent() {
        return isParent;
    }

    public void setIsParent(boolean parent) {
        isParent = parent;
    }

    public String getCivilId() {
        return civilId;
    }

    public void setCivilId(String civilId) {
        this.civilId = civilId;
    }
}
