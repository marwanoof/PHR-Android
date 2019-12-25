package om.gov.moh.phr.models;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.ProgressBar;

import om.gov.moh.phr.R;


public class MyProgressDialog {
    private final ProgressDialog myProgressDialog;

    /**
     * we need to pass the context from the fragment which request the dialog
     *
     * @param context
     */
    public MyProgressDialog(Context context) {
        myProgressDialog = new ProgressDialog(context, R.style.MyProgressTheme);//// Allocate a new ProgressBar.
        final ProgressBar progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyle);  // Configure the IndeterminateDrawable.  progressBar.getIndeterminateDrawable ().setColorFilter (context.getResources ().getColor (R.color.colorAccent), android.graphics.PorterDuff.Mode.MULTIPLY);  // Assign the ProgressBar to the ProgressDialog.
        myProgressDialog.setIndeterminateDrawable(progressBar.getIndeterminateDrawable());
    }

    /**
     * calling this function will display progressDialog
     */
    public void showDialog() {
        myProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        myProgressDialog.show();
    }

    /**
     * calling this function  will remove the progress dialog
     */
    public void dismissDialog() {
        myProgressDialog.dismiss();
    }

}
