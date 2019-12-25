package om.gov.moh.phr.apimodels;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DLabResultsHolder implements Serializable {
    @SerializedName("testName")
    private String testName;

    @SerializedName("result")
    private String result;

    @SerializedName("unit")
    private String unit;

    @SerializedName("rangeLow")
    private String rangeLow;

    @SerializedName("interpretation")
    private String interpretation;

    public String getInterpretation() {
        return interpretation;
    }

    public void setInterpretation(String interpretation) {
        this.interpretation = interpretation;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setResult(String result) {
        this.result = result;
    }



    public void setRangeLow(String rangeLow) {
        this.rangeLow = rangeLow;
    }

    public void setRangeHigh(String rangeHigh) {
        this.rangeHigh = rangeHigh;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @SerializedName("rangeHigh")
    private String rangeHigh;

    @SerializedName("orderId")
    private String orderId;

    public String getOrderId() {
        if (TextUtils.isEmpty(orderId))
            return "";
        else
            return orderId;
    }

    public String getTestName() {
        if (TextUtils.isEmpty(testName))
            return null;
        else
        return testName;
    }

    public String getResult() {
        if (TextUtils.isEmpty(result))
            return null;
        else
        return result;
    }



    public String getRangeLow() {
        if (TextUtils.isEmpty(rangeLow))
            return "--";
        else
        return rangeLow;
    }

    public String getRangeHigh() {
        if (TextUtils.isEmpty(rangeHigh))
            return "--";
        else
        return rangeHigh;
    }
}
