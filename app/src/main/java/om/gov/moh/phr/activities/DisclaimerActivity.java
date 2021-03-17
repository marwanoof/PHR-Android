package om.gov.moh.phr.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

import java.util.Locale;

import om.gov.moh.phr.R;

import static om.gov.moh.phr.models.MyConstants.LANGUAGE_ARABIC;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_PREFS;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_SELECTED;

public class DisclaimerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeLanguageTo(getStoredLanguage(), false);
        setContentView(R.layout.activity_disclaimer);
        WebView wvTerms = findViewById(R.id.tv_terms);
        Button btnAccept = findViewById(R.id.btn_accept);
        Button btnDecline = findViewById(R.id.btn_decline);

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(DisclaimerActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setUpWebView(wvTerms, btnAccept, btnDecline);
    }
    private void setUpWebView(WebView wvTerms, Button btnAccept, Button btnDecline) {

        WebSettings settings = wvTerms.getSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        float fontSize = getResources().getDimension(R.dimen.text_size_16sp);
        settings.setDefaultFontSize((int) fontSize);


        if (getStoredLanguage().equals(LANGUAGE_ARABIC)) {
            btnAccept.setText("موافق");
            btnDecline.setText("غير موافق");
            wvTerms.loadUrl("file:///android_asset/terms-ar.html");
        } else {
            btnAccept.setText("Accept");
            btnDecline.setText("Decline");
            wvTerms.loadUrl("file:///android_asset/terms.html");
        }
    }
    private String getStoredLanguage() {
        SharedPreferences sharedPref = getSharedPreferences(LANGUAGE_PREFS, Context.MODE_PRIVATE);
        return sharedPref.getString(LANGUAGE_SELECTED, getDeviceLanguage());
    }

    private String getDeviceLanguage() {
        return Locale.getDefault().getLanguage();
    }

    private void changeLanguageTo(String language, boolean recreate) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        configuration.setLocale(locale);
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
        if (recreate) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }
}