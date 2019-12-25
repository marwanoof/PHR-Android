package om.gov.moh.phr.models;

import java.util.ArrayList;

public class CustomSlot {
    private String appointmentDate;
    private String appointmentDay;
    private String appointmentMonth;
    private ArrayList<String> timeBlockArrayList = new ArrayList<>();
    private ArrayList<String> runIdArrayList = new ArrayList<>();
    private String selectedRunId;
    private String selectedTime;


    public String getAppointmentDay() {
        return appointmentDay;
    }

    public String getAppointmentMonth() {
        return appointmentMonth;
    }


    public ArrayList<String> getTimeBlock() {
        return timeBlockArrayList;
    }

    public void addTimeBlock(String timeBlock) {
        timeBlockArrayList.add(timeBlock);
    }

    public void setAppointmentDay(String appointmentDay) {
        this.appointmentDay = appointmentDay;
    }

    public ArrayList<String> getRunIdArrayList() {
        return runIdArrayList;
    }

    public void addRunId(String runId) {
        runIdArrayList.add(runId);
    }

    public void setAppointmentMonth(String appointmentMonth) {
        this.appointmentMonth = appointmentMonth;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getSelectedRunId() {
        return selectedRunId;
    }

    public void setSelectedRunId(String selectedRunId) {
        this.selectedRunId = selectedRunId;
    }

    public String getSelectedTime() {
        return selectedTime;
    }

    public void setSelectedTime(String selectedTime) {
        this.selectedTime = selectedTime;
    }
}
