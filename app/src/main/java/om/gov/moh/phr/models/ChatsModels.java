package om.gov.moh.phr.models;

public class ChatsModels {
    private String sender;
    private String date;

    public ChatsModels(String sender, String date) {
        this.sender = sender;
        this.date = date;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
