package om.gov.moh.phr.models;

public class DummyVitalSigns {
    String name;
    String nameNls;
    String value;

    public DummyVitalSigns(String name, String nameNls, String value) {
        this.name = name;
        this.nameNls = nameNls;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameNls() {
        return nameNls;
    }

    public void setNameNls(String nameNls) {
        this.nameNls = nameNls;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
