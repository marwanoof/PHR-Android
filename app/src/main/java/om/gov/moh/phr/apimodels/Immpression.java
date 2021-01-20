package om.gov.moh.phr.apimodels;

public class Immpression {
    private String noteTitle;

    public String getNoteTitle() {
        if(noteTitle==null)
            return "";
        else
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteText() {
        if(noteText==null)
            return "";
        else
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    private String noteText;
}
