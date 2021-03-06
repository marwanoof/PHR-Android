package om.gov.moh.phr.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.aaid.HmsInstanceId;

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
import om.gov.moh.phr.apimodels.AccessToken;
import om.gov.moh.phr.apimodels.ApiDisclaimerHolder;
import om.gov.moh.phr.apimodels.ApiGetUserInfo;
import om.gov.moh.phr.dialogfragments.DisclaimerDialogFragment;
import om.gov.moh.phr.interfaces.DialogFragmentInterface;
import om.gov.moh.phr.models.AppCurrentUser;
import om.gov.moh.phr.models.AppLanguage;
import om.gov.moh.phr.models.GlobalMethodsKotlin;
import om.gov.moh.phr.models.MyProgressDialog;
import om.gov.moh.phr.models.NetworkUtility;

import static om.gov.moh.phr.activities.MainActivity.checkPlayServices;
import static om.gov.moh.phr.models.MyConstants.API_ANDROID_APP_CODE;
import static om.gov.moh.phr.models.MyConstants.API_ANDROID_FLAG;
import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_ACCESS_TOKEN;
import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_HUAWEI_APP_CODE;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_PHR;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_RESULT;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_ARABIC;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_PREFS;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_SELECTED;
import static om.gov.moh.phr.models.MyConstants.PARAM_CIVIL_ID;
import static om.gov.moh.phr.models.MyConstants.PARAM_IMAGE;
import static om.gov.moh.phr.models.MyConstants.PARAM_LOGIN_ID;
import static om.gov.moh.phr.models.MyConstants.PARAM_PERSON_NAME;
import static om.gov.moh.phr.models.MyConstants.PARAM_SIDE_MENU;
import static om.gov.moh.phr.models.MyConstants.PREFS_API_GET_TOKEN;
import static om.gov.moh.phr.models.MyConstants.PREFS_API_REGISTER_DEVICE;
import static om.gov.moh.phr.models.MyConstants.PREFS_CURRENT_USER;
import static om.gov.moh.phr.models.MyConstants.PREFS_DEVICE_ID;
import static om.gov.moh.phr.models.MyConstants.PREFS_IS_PARENT;
import static om.gov.moh.phr.models.MyConstants.PREFS_SIDE_MENU;

public class LoginActivity extends AppCompatActivity {
    private RequestQueue mQueue;
    private MyProgressDialog mProgressDialog;
    private TextInputEditText tietCivilId;
    private TextInputLayout tilOTP;
    private TextInputEditText tietOTP;
    private Button btnGetOTP;
    private Button btnLogin;
    private TextView tvCancel, tvResetOtp, tvChangeCivilID;
    private ImageView ivMohLogo;
    private DisclaimerDialogFragment mDisclaimerDialogFragment;
    private String currentLanguage = getDeviceLanguage();
    private String loginId = null;
    private static final String TAG = "LoginActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeLanguageTo(getStoredLanguage(), false);
        adjustFontScale(getResources().getConfiguration());
        setContentView(R.layout.activity_login);
        storeLanguage(currentLanguage);
        setAppLanguage(currentLanguage);
        mProgressDialog = new MyProgressDialog(this);// initializes progress dialog
        mQueue = Volley.newRequestQueue(this, new HurlStack(null, getSocketFactory())); // initializes mQueue : we need to use  Volley.newRequestQueue(this, new HurlStack(null, getSocketFactory())) because we need to connect the app to secure server "https".

        setupViews();
        {
            tietOTP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(tietCivilId.getText().toString())) {
                        tietCivilId.setBackground(getResources().getDrawable(R.drawable.edit_text_round_error));
                        tietCivilId.setError(getString(R.string.alert_empty_field));
                    }
                }
            });

            btnGetOTP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(tietCivilId.getText().toString())) {
                        tietCivilId.setBackground(getResources().getDrawable(R.drawable.edit_text_round_error));
                        tietCivilId.setError(getString(R.string.alert_empty_field));

                    } else {
                        if (NetworkUtility.isConnected(LoginActivity.this)) {
                            String civilidStr = tietCivilId.getText().toString();
                            if (civilidStr.length() > 12) {
                                GlobalMethodsKotlin.Companion.showAlertDialog(LoginActivity.this, getResources().getString(R.string.alert_error_title), getResources().getString(R.string.civilidCount), getResources().getString(R.string.ok), R.drawable.ic_error);
                            } else {
                                getDisclaimerByCivilId(tietCivilId.getText().toString());
                            }

                        } else {
                            GlobalMethodsKotlin.Companion.showAlertDialog(LoginActivity.this, getResources().getString(R.string.no_internet_title), getResources().getString(R.string.alert_no_connection), getResources().getString(R.string.ok), R.drawable.ic_error);
                        }

                    }
                }
            });
            tvResetOtp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tietOTP.setText("");
                    if (TextUtils.isEmpty(tietCivilId.getText().toString())) {
                        tietCivilId.setBackground(getResources().getDrawable(R.drawable.edit_text_round_error));
                        tietCivilId.setError(getString(R.string.alert_empty_field));

                    } else {
                        if (NetworkUtility.isConnected(LoginActivity.this)) {
                            getDisclaimerByCivilId(tietCivilId.getText().toString());
                        } else {
                            GlobalMethodsKotlin.Companion.showAlertDialog(LoginActivity.this, getResources().getString(R.string.no_internet_title), getResources().getString(R.string.alert_no_connection), getResources().getString(R.string.ok), R.drawable.ic_error);
                        }
                    }
                }
            });
            tvChangeCivilID.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tietCivilId.setText("");
                    resetLayout();
                }
            });

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean shouldLogin = true;

                    if (TextUtils.isEmpty(tietCivilId.getText().toString())) {
                        tietCivilId.setBackground(getResources().getDrawable(R.drawable.edit_text_round_error));
                        tietCivilId.setError(getString(R.string.alert_empty_field));
                        shouldLogin = false;
                    }

                    if (TextUtils.isEmpty(tietOTP.getText().toString())) {
                        tietOTP.setBackground(getResources().getDrawable(R.drawable.edit_text_round_error));
                        tietOTP.setError(getString(R.string.alert_empty_field));
                        shouldLogin = false;
                    }

                    if (shouldLogin) {
                        if (NetworkUtility.isConnected(LoginActivity.this)) {
                            if (loginId != null)
                                validateDoctoreUser(tietCivilId.getText().toString(), tietOTP.getText().toString());
                            else
                                validateNormalUser(tietCivilId.getText().toString(), tietOTP.getText().toString());
                        } else {
                            GlobalMethodsKotlin.Companion.showAlertDialog(LoginActivity.this, getResources().getString(R.string.no_internet_title), getResources().getString(R.string.alert_no_connection), getResources().getString(R.string.ok), R.drawable.ic_error);
                        }
                    }
                }
            });
        }
    }

    private void setupViews() {
        tietCivilId = findViewById(R.id.tiet_civil_id);
        tilOTP = findViewById(R.id.til_otp);
        tietOTP = findViewById(R.id.tiet_otp);
        tvResetOtp = findViewById(R.id.tv_resetOtp);
        btnGetOTP = findViewById(R.id.btn_get_otp);
        btnLogin = findViewById(R.id.btn_login);
        ImageView ivLogo = findViewById(R.id.imageView);
        ivMohLogo = findViewById(R.id.iv_logo_moh);
        if (getStoredLanguage().equals(LANGUAGE_ARABIC))
            ivMohLogo.setImageResource(R.drawable.moh_logo_ar);
        tvCancel = findViewById(R.id.tv_cancel);
        tvChangeCivilID = findViewById(R.id.tv_changeCivilID);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (getStoredLanguage().equals(LANGUAGE_ARABIC))
            ivLogo.setBackground(getResources().getDrawable(R.drawable.phr_logo_ar));
    }

    private void storeLanguage(String language) {
        SharedPreferences sharedPref = getSharedPreferences(LANGUAGE_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(LANGUAGE_SELECTED, language);
        editor.apply();
    }

    private void setAppLanguage(String language) {
        AppLanguage appLanguage = AppLanguage.getInstance();
        appLanguage.setSelectedLanguage(language);
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

    private void login(String civilId) {
        mProgressDialog.showDialog();
        String fullUrl = API_PHR + "v2/getOtp";


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, fullUrl, getJSONRequestParams(civilId)
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt(API_RESPONSE_CODE) == 0 && response.getString(API_RESPONSE_RESULT).trim().toLowerCase().equals("true")) {
                        //Normal User
                        loginId = null;
                        GlobalMethodsKotlin.Companion.showSimpleAlertDialog(LoginActivity.this, "", getResources().getString(R.string.sent_otp_msg_dialog), getResources().getString(R.string.ok), R.drawable.completed);
                        displayLoginForm();
                    } else if (response.getInt(API_RESPONSE_CODE) == 0) {
                        // Doctor
                        loginId = response.getString(API_RESPONSE_RESULT).trim();
                        displayLoginForm();
                        tvResetOtp.setVisibility(View.GONE);
                        tietOTP.setHint("");
                        tilOTP.setHint(getResources().getString(R.string.enter_password));
                        tietOTP.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    } else {
                        loginId = null;
                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.invalid_civilID_msg), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    GlobalMethodsKotlin.Companion.showAlertDialog(LoginActivity.this, getResources().getString(R.string.alert_error_title), getResources().getString(R.string.wrong_msg), getResources().getString(R.string.ok), R.drawable.ic_error);
                }
                mProgressDialog.dismissDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                GlobalMethodsKotlin.Companion.showAlertDialog(LoginActivity.this, getResources().getString(R.string.alert_error_title), getResources().getString(R.string.wrong_msg), getResources().getString(R.string.ok), R.drawable.ic_error);
                mProgressDialog.dismissDialog();
            }
        }) {
            //
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        mQueue.add(jsonObjectRequest);
    }


    private JSONObject getJSONRequestParams(String civilId) {
        Map<String, Long> params = new HashMap<>();
        params.put("civilId", Long.parseLong(civilId));
        return new JSONObject(params);
    }

    private JSONObject getValidateJSONRequestParams(String civilId, String otp) {
        Map<String, Object> params = new HashMap<>();
        params.put("civilId", Long.parseLong(civilId));
        params.put("otp", Long.parseLong(otp));
        params.put("disclaimerValue", "Y");
        return new JSONObject(params);
    }

    /**
     * call displayLoginForm() to display login form : otp input + login button.
     */
    private void displayLoginForm() {
        {
            tilOTP.setVisibility(View.VISIBLE);
            tilOTP.requestFocus();
            btnLogin.setVisibility(View.VISIBLE);
            btnGetOTP.setVisibility(View.INVISIBLE);
            tvCancel.setVisibility(View.GONE);
            tvResetOtp.setVisibility(View.VISIBLE);
            tvChangeCivilID.setVisibility(View.VISIBLE);
            ivMohLogo.setVisibility(View.GONE);
        }

    }

    private void showDisclaimerDialog(final String civilId) {
        mDisclaimerDialogFragment = DisclaimerDialogFragment.newInstance();
        mDisclaimerDialogFragment.setDialogFragmentListener(new DialogFragmentInterface() {

            @Override
            public void onAccept() {
                login(civilId);
                mDisclaimerDialogFragment.dismiss();
            }

            @Override
            public void onAccept(int position) {

            }

            @Override
            public void onDecline() {
                resetLayout();
                mDisclaimerDialogFragment.dismiss();
            }

        });
        mDisclaimerDialogFragment.show(getSupportFragmentManager(), DisclaimerDialogFragment.class.getSimpleName());
    }

    private void validateNormalUser(final String civilId, String otp) {
        mProgressDialog.showDialog();
        String fullUrl = API_PHR + "v2/validateOtp";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, fullUrl, getValidateJSONRequestParams(civilId, otp)
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt(API_RESPONSE_CODE) == 0) {
                        JSONObject jsonObject = response.optJSONObject(API_RESPONSE_RESULT);
                        if (jsonObject != null) {
                            //  showDisclaimerDialog(jsonObject.optString(API_GET_TOKEN_ACCESS_TOKEN), civilId, jsonObject.optString("personName"), jsonObject.optString("image"), Objects.requireNonNull(jsonObject.optJSONArray("menus")).toString());
                            storeAccessToken(jsonObject.optString(API_GET_TOKEN_ACCESS_TOKEN)
                                    , civilId, null, jsonObject.optString("personName"), jsonObject.optString("image"), jsonObject.optJSONArray("menus").toString());
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.invalid_otp_msg), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    GlobalMethodsKotlin.Companion.showAlertDialog(LoginActivity.this, getResources().getString(R.string.alert_error_title), getResources().getString(R.string.wrong_msg), getResources().getString(R.string.ok), R.drawable.ic_error);
                }

                mProgressDialog.dismissDialog();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                GlobalMethodsKotlin.Companion.showAlertDialog(LoginActivity.this, getResources().getString(R.string.alert_error_title), getResources().getString(R.string.wrong_msg), getResources().getString(R.string.ok), R.drawable.ic_error);
                error.printStackTrace();
                mProgressDialog.dismissDialog();
            }
        }) {
            //
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }


        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        mQueue.add(jsonObjectRequest);
    }

    private void storeAccessToken(String accessTokenValue, String civilId, String loginId, String personName, String image, String menus) {
     getNotificationCurrentToken(accessTokenValue, civilId);
        SharedPreferences.Editor editor;


        AccessToken accessToken = AccessToken.getInstance();
        accessToken.setAccessCivilId(civilId);
        accessToken.setAccessLoginId(loginId);
        accessToken.setAccessTokenString(accessTokenValue);
        accessToken.setPersonName(personName);
        accessToken.setImage(image);


        SharedPreferences pref = getApplicationContext().getSharedPreferences("betaValidation", 0);
        editor = pref.edit();
        editor.putBoolean("beta", true);
        editor.apply();

        SharedPreferences sharedPrefAccessToken = getSharedPreferences(PREFS_API_GET_TOKEN, Context.MODE_PRIVATE);
        editor = sharedPrefAccessToken.edit();
        editor.putString(API_GET_TOKEN_ACCESS_TOKEN, accessTokenValue);
        editor.putString(PARAM_CIVIL_ID, civilId);
        editor.putString(PARAM_LOGIN_ID, loginId);
        editor.putString(PARAM_PERSON_NAME, personName);
        editor.putString(PARAM_IMAGE, image);
        editor.apply();


        AppCurrentUser currentUser = AppCurrentUser.getInstance();
        currentUser.setCivilId(civilId);
        currentUser.setIsParent(true);

        SharedPreferences sharedPrefCurrentUser = getSharedPreferences(PREFS_CURRENT_USER, Context.MODE_PRIVATE);
        editor = sharedPrefCurrentUser.edit();
        editor.putString(PARAM_CIVIL_ID, civilId);
        editor.putBoolean(PREFS_IS_PARENT, true);
        editor.apply();

        SharedPreferences sharedPrefSideMenu = getSharedPreferences(PREFS_SIDE_MENU, Context.MODE_PRIVATE);
        editor = sharedPrefSideMenu.edit();
        editor.putString(PARAM_SIDE_MENU, menus);
        editor.apply();

//        mDisclaimerDialogFragment.dismiss();
      //  moveToMainActivity();
    }

    private void moveToMainActivity() {
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private String getStoredLanguage() {
        SharedPreferences sharedPref = getSharedPreferences(LANGUAGE_PREFS, Context.MODE_PRIVATE);
        return sharedPref.getString(LANGUAGE_SELECTED, getDeviceLanguage());
    }

    private String getDeviceLanguage() {
        return Locale.getDefault().getLanguage();
    }

    private void changeLanguageTo(String language, boolean recreate) {
        currentLanguage = language;
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

    @Override
    protected void onResume() {
        super.onResume();
        changeLanguageTo(getStoredLanguage(), false);
    }

    private void resetLayout() {
        tietOTP.setText("");
        tilOTP.setVisibility(View.GONE);
        btnGetOTP.setVisibility(View.VISIBLE);
        btnLogin.setVisibility(View.INVISIBLE);
        tvResetOtp.setVisibility(View.GONE);
        tvChangeCivilID.setVisibility(View.GONE);
        ivMohLogo.setVisibility(View.VISIBLE);
    }

    // to handle Font Size
    private void adjustFontScale(Configuration configuration) {
        configuration.fontScale = (float) 1.0;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;
        getBaseContext().getResources().updateConfiguration(configuration, metrics);

    }

    private void getDisclaimerByCivilId(final String civilId) {
        mProgressDialog.showDialog();
        String fullUrl = API_PHR + "/getDisclaimerByCivilId?civilId=" + civilId;


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fullUrl, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    if (response.getInt(API_RESPONSE_CODE) == 0) {
                        Gson json = new Gson();
                        ApiDisclaimerHolder disclaimerHolder = json.fromJson(response.toString(), ApiDisclaimerHolder.class);
                        if (disclaimerHolder.getmResult() != null) {
                            if (disclaimerHolder.getmResult().getAcceptYN() != null && disclaimerHolder.getmResult().getAcceptYN().equalsIgnoreCase("y"))
                                login(civilId);
                            else
                                showDisclaimerDialog(civilId);
                        }

                    } else {
                        showDisclaimerDialog(civilId);
                    }
                } catch (JSONException e) {
                    GlobalMethodsKotlin.Companion.showAlertDialog(LoginActivity.this, getResources().getString(R.string.alert_error_title), getResources().getString(R.string.wrong_msg), getResources().getString(R.string.ok), R.drawable.ic_error);
                }
                mProgressDialog.dismissDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                GlobalMethodsKotlin.Companion.showAlertDialog(LoginActivity.this, getResources().getString(R.string.alert_error_title), getResources().getString(R.string.wrong_msg), getResources().getString(R.string.ok), R.drawable.ic_error);
                mProgressDialog.dismissDialog();
            }
        });
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        mQueue.add(jsonObjectRequest);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Clear the Activity's bundle of the subsidiary fragments' bundles.
        outState.clear();
    }

    private void validateDoctoreUser(final String civilId, String password) {
        mProgressDialog.showDialog();
        String fullUrl = API_NEHR_URL + "login/getToken";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, fullUrl, validateJSONRequestParams(password)
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt(API_RESPONSE_CODE) == 0) {
                        getUserInfo(response.optJSONObject(API_RESPONSE_RESULT).optString(API_GET_TOKEN_ACCESS_TOKEN), civilId);
                    } else {
                        GlobalMethodsKotlin.Companion.showAlertDialog(LoginActivity.this, getResources().getString(R.string.alert_error_title), getResources().getString(R.string.credentials_error), getResources().getString(R.string.ok), R.drawable.ic_error);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                mProgressDialog.dismissDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                mProgressDialog.dismissDialog();
            }
        }) {
            //
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }


        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        mQueue.add(jsonObjectRequest);
    }

    private JSONObject validateJSONRequestParams(String password) {
        Map<String, String> params = new HashMap<>();
        params.put("password", password);
        params.put("username", loginId);
        return new JSONObject(params);
    }

    private void getUserInfo(final String accessToken, final String civilId) {
        mProgressDialog.showDialog();
        String fullUrl = API_NEHR_URL + "getUserInfo";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fullUrl, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    if (response.getInt(API_RESPONSE_CODE) == 0) {
                        Gson gson = new Gson();
                        ApiGetUserInfo responseHolder = gson.fromJson(response.toString(), ApiGetUserInfo.class);
                        if (responseHolder.getResult().getLoginId() != null && responseHolder.getResult().getPerson().getPersonName() != null)
                            storeAccessToken(accessToken, civilId, loginId, responseHolder.getResult().getPerson().getPersonName(), null, null);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mProgressDialog.dismissDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("resp-UserInfo", error.toString());
                error.printStackTrace();
                mProgressDialog.dismissDialog();
            }
        }) {
            //
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", API_GET_TOKEN_BEARER + accessToken);
                return headers;
            }


        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        mQueue.add(jsonObjectRequest);
    }
   // FCM
    private void getNotificationCurrentToken(final String accessTokenValue, final String civilId) {
        // Get token
        // [START retrieve_current_token]
        if (checkPlayServices(this)) {
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "getInstanceId failed", task.getException());
                                return;
                            }
                            // Get new Instance ID token
                            String deviceId = task.getResult().getToken();
                            Log.i(TAG, "getToken:" + deviceId);
                            if (!getDeviceId().equals(deviceId)) {
                                registerDevice(deviceId, accessTokenValue, civilId);
                            }


                        }
                    });
        } else {
            new Thread() {
                @Override
                public void run() {
                    try {
                        // read from agconnect-services.json
                        String appId = AGConnectServicesConfig.fromContext(LoginActivity.this).getString("client/app_id");
                        String token = HmsInstanceId.getInstance(LoginActivity.this).getToken(appId, "HCM");
                        Log.i(TAG, "getToken:" + token);
                            if (!getDeviceId().equals(token)) {
                                registerDevice(token, accessTokenValue, civilId);
                            }
                    } catch (com.huawei.hms.common.ApiException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
// [END retrieve_current_token]
    }

    private void registerDevice(final String deviceId, final String accessTokenValue, String civilId) {
        String fullUrl = API_NEHR_URL + "v2/device/register";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, fullUrl, getJSONRequestParam(deviceId, civilId)
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG+"register", response.toString());
                try {
                    if (response.getInt(API_RESPONSE_CODE) == 0) {
                        saveRegisterDeviceDetails(deviceId);
                        moveToMainActivity();
                    } else {
                        mProgressDialog.dismissDialog();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mProgressDialog.dismissDialog();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                mProgressDialog.dismissDialog();
            }
        }) {
            //
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", API_GET_TOKEN_BEARER + accessTokenValue);
                return headers;
            }

        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);

        mQueue.add(jsonObjectRequest);
    }

    private JSONObject getJSONRequestParam( String deviceId, String civilId) {
        Map<String, Object> params = new HashMap<>();
        params.put("deviceId", deviceId);
        params.put("flag", API_ANDROID_FLAG);
        if (checkPlayServices(this))
            params.put("appShortCode", API_ANDROID_APP_CODE);
        else
            params.put("appShortCode", API_HUAWEI_APP_CODE);
        params.put("civilId", Long.parseLong(civilId));
        params.put("loginId", "");
        Log.d("registerParams", params.toString());
        return new JSONObject(params);
    }

    private void saveRegisterDeviceDetails(String deviceId) {
        SharedPreferences sharedPref = getSharedPreferences(PREFS_API_REGISTER_DEVICE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(PREFS_DEVICE_ID, deviceId);
        editor.apply();
    }
    private String getDeviceId() {
        SharedPreferences sharedPref = getSharedPreferences(PREFS_API_REGISTER_DEVICE, Context.MODE_PRIVATE);
        return sharedPref.getString(PREFS_DEVICE_ID, "");
    }
}