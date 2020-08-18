package om.gov.moh.phr.interfaces;

import java.io.Serializable;

public interface DialogFragmentInterface extends Serializable {
    void onAccept();

    void onAccept(int position);
    void onDecline();
}
