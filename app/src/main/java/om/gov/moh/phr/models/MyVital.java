package om.gov.moh.phr.models;

public class MyVital {
    private String title;
    private String value;
    private String sign;

    public MyVital(String title, String value, String sign) {
        this.title = title;
        this.value = value;
        this.sign = sign;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
