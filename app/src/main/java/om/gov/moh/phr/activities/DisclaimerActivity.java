package om.gov.moh.phr.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiDisclaimerHolder;
import om.gov.moh.phr.models.AppCurrentUser;
import om.gov.moh.phr.models.GlobalMethodsKotlin;
import om.gov.moh.phr.models.MyProgressDialog;

import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_PHR;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_RESULT;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_ARABIC;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_PREFS;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_SELECTED;
import static om.gov.moh.phr.models.MyConstants.PREFS_API_GET_TOKEN;
import static om.gov.moh.phr.models.MyConstants.PREFS_API_REGISTER_DEVICE;
import static om.gov.moh.phr.models.MyConstants.PREFS_CURRENT_USER;
import static org.apache.http.conn.ssl.SSLSocketFactory.getSocketFactory;

public class DisclaimerActivity extends AppCompatActivity {
    private RequestQueue mQueue;
    private MyProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeLanguageTo(getStoredLanguage(), false);
        setContentView(R.layout.activity_disclaimer);
        mQueue = Volley.newRequestQueue(this, new HurlStack(null, getSocketFactory())); // initializes mQueue : we need to use  Volley.newRequestQueue(this, new HurlStack(null, getSocketFactory())) because we need to connect the app to secure server "https".
        mProgressDialog = new MyProgressDialog(this);// initializes progress dialog
        WebView wvTerms = findViewById(R.id.tv_terms);
        Button btnAccept = findViewById(R.id.btn_accept);
        Button btnDecline = findViewById(R.id.btn_decline);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDisclaimerByCivilId();
            }
        });
        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSharedPrefs();
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

    private SSLSocketFactory getSocketFactory() {
        CertificateFactory cf = null;
        try {
            cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = getResources().openRawResource(R.raw.server_cert);
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
                Log.e("CERT", "ca=" + ((X509Certificate) ca).getSubjectDN());
            } finally {
                caInput.close();
            }
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore trustStore = KeyStore.getInstance(keyStoreType);
            trustStore.load(null, null);
            trustStore.setCertificateEntry("ca", ca);

            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(trustStore);
            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    Log.e("CipherUsed", session.getCipherSuite());
                    return hostname.endsWith(".moh.gov.om"); //The Hostname of your server.
                }
            };
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            InputStream is = getResources().openRawResource(R.raw.client);
            keyStore.load(is, "newchangeit".toCharArray());
            is.close();

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("X509");
            try {
                keyManagerFactory.init(keyStore, "newchangeit".toCharArray());
            } catch (UnrecoverableKeyException e) {
                e.printStackTrace();
            }
            HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
            SSLContext context = null;
            context = SSLContext.getInstance("TLS");
            context.init(keyManagerFactory.getKeyManagers(), tmf.getTrustManagers(), null);
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
            SSLSocketFactory sf = context.getSocketFactory();
            return sf;
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void clearSharedPrefs() {
        SharedPreferences sharedPref;
        SharedPreferences.Editor editor;

        sharedPref = getSharedPreferences(PREFS_API_GET_TOKEN, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.clear();
        editor.apply();

        sharedPref = getSharedPreferences(PREFS_CURRENT_USER, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.clear();
        editor.apply();


        sharedPref = getSharedPreferences(PREFS_API_REGISTER_DEVICE, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.clear();
        editor.apply();
    }

    private void saveDisclaimerByCivilId() {
        mProgressDialog.showDialog();
        String url = API_PHR + "saveDisclaimer";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, getJSONRequestParams()
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("getDisclaimerByCivilId", response.toString());
                try {
                    if (response.getInt(API_RESPONSE_CODE) == 0) {
                        finish();
                        Intent intent = new Intent(DisclaimerActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        clearSharedPrefs();
                        finish();
                    }
                } catch (JSONException e) {
                    GlobalMethodsKotlin.Companion.showAlertDialog(DisclaimerActivity.this, getResources().getString(R.string.alert_error_title), getResources().getString(R.string.wrong_msg), getResources().getString(R.string.ok), R.drawable.ic_error);
                }
                mProgressDialog.dismissDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                clearSharedPrefs();
                GlobalMethodsKotlin.Companion.showAlertDialog(DisclaimerActivity.this, getResources().getString(R.string.alert_error_title), getResources().getString(R.string.wrong_msg), getResources().getString(R.string.ok), R.drawable.ic_error);
                mProgressDialog.dismissDialog();
            }
        });
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        mQueue.add(jsonObjectRequest);
    }

    private JSONObject getJSONRequestParams() {
        Map<String, String> params = new HashMap<>();
        AppCurrentUser currentUser = AppCurrentUser.getInstance();
        params.put("acceptYN", "Y");
        params.put("civilId", currentUser.getCivilId());
        return new JSONObject(params);
    }

}