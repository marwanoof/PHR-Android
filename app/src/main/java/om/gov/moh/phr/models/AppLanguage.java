package om.gov.moh.phr.models;

public class AppLanguage {
    private static AppLanguage instance;
    private String selectedLanguage;

    public static synchronized AppLanguage getInstance(){
        if(instance == null) {
            synchronized (AppCurrentUser.class) {
                if (instance == null) {
                    instance = new AppLanguage();
                }
            }
        }
        return instance;
    }

    private AppLanguage() {
    }

    public String getSelectedLanguage() {
        return selectedLanguage;
    }

    public void setSelectedLanguage(String selectedLanguage) {
        this.selectedLanguage = selectedLanguage;
    }
}
