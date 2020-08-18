package om.gov.moh.phr.models;

public class Measurement {
    private String measurementTitle;
    private String measurementValue;
    private int measurementIcon;

    public Measurement(String measurementTitle, String measurementValue, int measurementIcon) {
        this.measurementTitle = measurementTitle;
        this.measurementValue = measurementValue;
        this.measurementIcon = measurementIcon;
    }

    public String getMeasurementTitle() {
        return measurementTitle;
    }

    public String getMeasurementValue() {
        return measurementValue;
    }

    public int getMeasurementIcon() {
        return measurementIcon;
    }
}
