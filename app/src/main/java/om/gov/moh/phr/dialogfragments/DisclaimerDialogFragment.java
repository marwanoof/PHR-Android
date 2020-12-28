package om.gov.moh.phr.dialogfragments;


import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;

import java.util.Locale;

import om.gov.moh.phr.R;
import om.gov.moh.phr.interfaces.DialogFragmentInterface;

import static om.gov.moh.phr.models.MyConstants.LANGUAGE_ARABIC;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_ENGLISH;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_PREFS;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_SELECTED;

public class DisclaimerDialogFragment extends DialogFragment {


    private Context mContext;
    private DialogFragmentInterface mDialogListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public DisclaimerDialogFragment() {
        // Required empty public constructor
    }


    public static DisclaimerDialogFragment newInstance() {
        DisclaimerDialogFragment fragment = new DisclaimerDialogFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }*/
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_disclaimer, container, false);
        WebView wvTerms = parentView.findViewById(R.id.tv_terms);
        Button btnAccept = parentView.findViewById(R.id.btn_accept);
        Button btnDecline = parentView.findViewById(R.id.btn_decline);

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogListener.onAccept();
            }
        });
        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogListener.onDecline();
            }
        });
        setUpWebView(wvTerms);
        return parentView;
    }

    private void setUpWebView(WebView wvTerms) {

        WebSettings settings = wvTerms.getSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        float fontSize = mContext.getResources().getDimension(R.dimen.text_size_16sp);
        settings.setDefaultFontSize((int) fontSize);

//        wvTerms.loadDataWithBaseURL("file:///android_asset/terms.html", "", "text/html", "UTF-8", null);
        if (getStoredLanguage().equals(LANGUAGE_ARABIC))
            wvTerms.loadUrl("file:///android_asset/terms-ar.html");
        else
            wvTerms.loadUrl("file:///android_asset/terms.html");
    }

    public void setDialogFragmentListener(DialogFragmentInterface listener) {
        mDialogListener = listener;
    }
    private String getStoredLanguage() {
        SharedPreferences sharedPref = mContext.getSharedPreferences(LANGUAGE_PREFS, Context.MODE_PRIVATE);
        return sharedPref.getString(LANGUAGE_SELECTED, getDeviceLanguage());
    }
    private String getDeviceLanguage() {
        return Locale.getDefault().getLanguage();
    }
}


