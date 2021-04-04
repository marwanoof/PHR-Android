package om.gov.moh.phr.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.aaid.HmsInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.AccessToken;
import om.gov.moh.phr.fragments.ChatMessagesFragment;
import om.gov.moh.phr.fragments.DemographicsFragment;
import om.gov.moh.phr.fragments.FeedbackFragment;
import om.gov.moh.phr.fragments.HomeFragment;
import om.gov.moh.phr.fragments.WebSideMenuFragment;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.AppCurrentUser;
import om.gov.moh.phr.models.AppLanguage;
import om.gov.moh.phr.models.ComponentConstants;
import om.gov.moh.phr.models.CustomTypefaceSpan;
import om.gov.moh.phr.models.HomePagerAdapter;
import om.gov.moh.phr.models.MyProgressDialog;
import om.gov.moh.phr.models.NetworkUtility;
import om.gov.moh.phr.models.ViewPagerCustomDuration;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static om.gov.moh.phr.models.MyConstants.API_ANDROID_APP_CODE;
import static om.gov.moh.phr.models.MyConstants.API_ANDROID_FLAG;
import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_ACCESS_TOKEN;
import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_HUAWEI_APP_CODE;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_MESSAGE;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_ARABIC;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_ENGLISH;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_PREFS;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_SELECTED;
import static om.gov.moh.phr.models.MyConstants.PARAM_CIVIL_ID;
import static om.gov.moh.phr.models.MyConstants.PARAM_IMAGE;
import static om.gov.moh.phr.models.MyConstants.PARAM_PERSON_NAME;
import static om.gov.moh.phr.models.MyConstants.PARAM_SIDE_MENU;
import static om.gov.moh.phr.models.MyConstants.PREFS_API_GET_TOKEN;
import static om.gov.moh.phr.models.MyConstants.PREFS_API_REGISTER_DEVICE;
import static om.gov.moh.phr.models.MyConstants.PREFS_CURRENT_USER;
import static om.gov.moh.phr.models.MyConstants.PREFS_DEVICE_ID;
import static om.gov.moh.phr.models.MyConstants.PREFS_IS_PARENT;
import static om.gov.moh.phr.models.MyConstants.PREFS_SIDE_MENU;



public class MainActivity extends AppCompatActivity implements MediatorInterface, ToolbarControllerInterface, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private static final String DEPENDENT_CIVILID = "DependentCivilID";
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private MyProgressDialog mProgressDialog;
    private RequestQueue mQueue;
    private Context mContext = MainActivity.this;
    private String currentLanguage = getDeviceLanguage();
    private DataUpdateReceiver dataUpdateReceiver;
    private String menus;
    final int callbackId = 42;

    //FCM
    public static boolean checkPlayServices(Activity activity) {
        final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                //apiAvailability.getErrorDialog(activity, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.d(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }


    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id
                .fl_main_container);
    }

    private static boolean isCallable(Context context, Intent intent) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeLanguageTo(getStoredLanguage(), false);
        adjustFontScale(getResources().getConfiguration());
        setContentView(R.layout.side_menu_main_page);
        storeLanguage(currentLanguage);
        setAppLanguage(currentLanguage);
        requestNotificationPermission();
        checkPermission(callbackId, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR);
        toolbar = findViewById(R.id.toolbar);

        //steps to change status bar color << start >>
        Window window = getWindow();
        //clear FLAG_TRANSLUCENT_STATUS flag :
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window]
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //change color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorStatusBar));
        //steps to change status bar color << end >>

        //set toolbar dark icons
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        ImageView ivLogout = findViewById(R.id.iv_logout);


        mDrawer = findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
        //*************************************


        mProgressDialog = new MyProgressDialog(mContext);// initializes progress dialog
        mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, getSocketFactory())); // initializes mQueue : we need to use  Volley.newRequestQueue(this, new HurlStack(null, getSocketFactory())) because we need to connect the app to secure server "https".
        Intent intent = getIntent();
        if (intent.getStringExtra(DEPENDENT_CIVILID) != null) {
            ivLogout.setBackground(getResources().getDrawable(R.drawable.ic_close));
            String civilId = intent.getStringExtra(DEPENDENT_CIVILID);
            SharedPreferences.Editor editor;
            boolean isParent = getAccessToken().getAccessCivilId().equalsIgnoreCase(civilId);

            SharedPreferences sharedPrefCurrentUser = mContext.getSharedPreferences(PREFS_CURRENT_USER, Context.MODE_PRIVATE);
            editor = sharedPrefCurrentUser.edit();
            editor.putString(PARAM_CIVIL_ID, civilId);
            editor.putBoolean(PREFS_IS_PARENT, isParent);
            editor.apply();


            AppCurrentUser appCurrentUser = AppCurrentUser.getInstance();
            appCurrentUser.setIsParent(isParent);
            appCurrentUser.setCivilId(civilId);
        } else {
            SharedPreferences.Editor editor;

            SharedPreferences sharedPrefCurrentUser = mContext.getSharedPreferences(PREFS_CURRENT_USER, Context.MODE_PRIVATE);
            editor = sharedPrefCurrentUser.edit();
            editor.putString(PARAM_CIVIL_ID, getCurrentUser().getCivilId());
            editor.putBoolean(PREFS_IS_PARENT, true);
            editor.apply();

            AppCurrentUser appCurrentUser = AppCurrentUser.getInstance();
            appCurrentUser.setIsParent(true);
            appCurrentUser.setCivilId(getAccessToken().getAccessCivilId());
        }
        setupMainActivity();
    /*    if (getSharedPreferences(PREFS_API_GET_TOKEN, Context.MODE_PRIVATE).contains(API_GET_TOKEN_ACCESS_TOKEN)) {
            setupMainActivity();
        } else {
            setupLoginFragment();
        }*/

    }

    private void checkPermission(int callbackId, String... permissionsId) {
        boolean permissions = true;
        for (String p : permissionsId) {
            permissions = permissions && ContextCompat.checkSelfPermission(this, p) == PERMISSION_GRANTED;
        }

        if (!permissions)
            ActivityCompat.requestPermissions(this, permissionsId, callbackId);
    }

    private void setUpNavigationView() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();

        SharedPreferences sharedPrefSideMenu = mContext.getSharedPreferences(PREFS_SIDE_MENU, Context.MODE_PRIVATE);
        menus = sharedPrefSideMenu.getString(PARAM_SIDE_MENU, "");
        String arabicMenuItem = getResources().getString(R.string.arabic);
        menu.findItem(R.id.nav_arabic).setTitle(applyFontToMenuItem(arabicMenuItem));

        String englishMenuItem = getResources().getString(R.string.english);
        menu.findItem(R.id.nav_english).setTitle(applyFontToMenuItem(englishMenuItem));

        String aboutMenuItem = getResources().getString(R.string.about_the_app);
        menu.findItem(R.id.nav_about).setTitle(applyFontToMenuItem(aboutMenuItem));

        String feedbackMenuItem = getResources().getString(R.string.phr_feedback);
        menu.findItem(R.id.nav_feedback).setTitle(applyFontToMenuItem(feedbackMenuItem));

        String logoutMenuItem = getResources().getString(R.string.title_logout);
        menu.findItem(R.id.nav_logout).setTitle(applyFontToMenuItem(logoutMenuItem));
        try {
            JSONArray array = new JSONArray(menus);
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObj = array.getJSONObject(i);
                int nav_id = jsonObj.getInt("menuId");
                String nav_name = jsonObj.getString("menuName");
                String nav_name_ar = jsonObj.getString("menuNameNls");
                String nav_icon = jsonObj.getString("iconClass");
                String nav_Url = jsonObj.getString("templateUrl");
                String nav_Url_ar = jsonObj.getString("templateUrlNls");
                if (nav_icon.contains("sm_bmi")) {  // the resource exists...
                    menu.add(Menu.NONE, nav_id, Menu.NONE, applyFontToMenuItem(getResources().getString(R.string.bmi_calculator))).setIcon(R.drawable.bmi_ic);
                } else if (nav_icon.contains("sm_righ")) {  // the resource exists...
                    menu.add(Menu.NONE, nav_id, Menu.NONE, applyFontToMenuItem(getResources().getString(R.string.patients_rights))).setIcon(R.drawable.terms_of_use_ic);
                } else if (nav_icon.contains("sm_faci")) {  // the resource exists...
                    menu.add(Menu.NONE, nav_id, Menu.NONE, applyFontToMenuItem(getResources().getString(R.string.heath_facilities))).setIcon(R.drawable.svg_ic_institute);
                } else if (nav_icon.contains("sm_phar")) {  // the resource exists...
                    menu.add(Menu.NONE, nav_id, Menu.NONE, applyFontToMenuItem(getResources().getString(R.string.find_pharmacy))).setIcon(R.drawable.search_pharmacy);
                } else if (nav_icon.contains("sm_edd")) {  // the resource exists...
                    menu.add(Menu.NONE, nav_id, Menu.NONE, applyFontToMenuItem(getResources().getString(R.string.edd_calculator))).setIcon(R.drawable.edd_ic);
                } else if (nav_icon.contains("sm_form")) {  // the resource exists...
                    menu.add(Menu.NONE, nav_id, Menu.NONE, applyFontToMenuItem(getResources().getString(R.string.forms))).setIcon(R.drawable.forms);
                } else if (nav_icon.contains("sm_ask")) {  // the resource exists...
                    menu.add(Menu.NONE, nav_id, Menu.NONE, applyFontToMenuItem(getResources().getString(R.string.ask_doctor))).setIcon(R.drawable.ask_doctor);
                } else if (nav_icon.contains("sm_educ")) {  // the resource exists...
                    menu.add(Menu.NONE, nav_id, Menu.NONE, applyFontToMenuItem(getResources().getString(R.string.health_education))).setIcon(R.drawable.health_education);
                } else {  // checkExistence == 0  // the resource does NOT exist!!
                    if (getStoredLanguage().equals(LANGUAGE_ENGLISH))
                        menu.add(Menu.NONE, nav_id, Menu.NONE, applyFontToMenuItem(nav_name)).setIcon(R.drawable.terms_of_use_ic);
                    else
                        menu.add(Menu.NONE, nav_id, Menu.NONE, applyFontToMenuItem(nav_name_ar)).setIcon(R.drawable.terms_of_use_ic);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (getStoredLanguage().equals(LANGUAGE_ARABIC))
            menu.findItem(R.id.nav_arabic).setVisible(false);
        else
            menu.findItem(R.id.nav_english).setVisible(false);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void requestNotificationPermission() {

        SharedPreferences settings = mContext.getSharedPreferences("ProtectedApps", Context.MODE_PRIVATE);
        boolean skipMessage = settings.getBoolean("skipProtectedAppCheck", false);
        if (!skipMessage) {
            final SharedPreferences.Editor editor = settings.edit();
            boolean foundCorrectIntent = false;
            for (final Intent intent : ComponentConstants.POWERMANAGER_INTENTS) {

                if (isCallable(mContext, intent)) {
                    foundCorrectIntent = true;

                    final AppCompatCheckBox dontShowAgain = new AppCompatCheckBox(mContext);
                    dontShowAgain.setText(getString(R.string.alert_dont_show_again));
                    dontShowAgain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            editor.putBoolean("skipProtectedAppCheck", isChecked);
                            editor.apply();
                        }
                    });
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle(Build.MANUFACTURER + " Protected Apps")
                            .setMessage(String.format("%s requires to be enabled in 'Protected Apps' to function properly.%n", mContext.getString(R.string.app_name)))
                            .setView(dontShowAgain)
                            .setPositiveButton("Go to settings", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    mContext.startActivity(intent);
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, null);

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    break;
                }
            }
            if (!foundCorrectIntent) {
                editor.putBoolean("skipProtectedAppCheck", true);
                editor.apply();
            }
        }
    }

    private void setupMainActivity() {
        setUpNavigationView();
        //to add the badge of notifications
        if (AppCurrentUser.getInstance().getIsParent())
            checkNotificationsCounter();
        if (checkPlayServices(this)) {
            getNotificationCurrentToken();
        }

    }

    private void checkNotificationsCounter() {
        SharedPreferences sharedPref = mContext.getSharedPreferences("Counting", Context.MODE_PRIVATE);
        int NoOfAppointmentsNotifications = sharedPref.getInt("appointmentCount", 0);
        int NoOfNotifications = sharedPref.getInt("notificationCount", 0);
        int NoOfChatNotifications = sharedPref.getInt("chatCount", 0);
        if (NoOfChatNotifications > 0) {
            if (getCurrentFragment() != null && getCurrentFragment() instanceof ChatMessagesFragment) {
                clearNotificationSharedPrefs(3);
            }

        }
    }

    // client cert to be appended each time will make http request
    //server certificate to be added into TrustManager
    @Override
    public SSLSocketFactory getSocketFactory() {
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

    @Override
    public AccessToken getAccessToken() {
        if (!TextUtils.isEmpty(AccessToken.getInstance().getAccessTokenString())) {
            return AccessToken.getInstance();

        } else {
            SharedPreferences sharedPref = getSharedPreferences(PREFS_API_GET_TOKEN, Context.MODE_PRIVATE);
            AccessToken accessToken = AccessToken.getInstance();
            accessToken.setAccessTokenString(sharedPref.getString(API_GET_TOKEN_ACCESS_TOKEN, ""));
            accessToken.setAccessCivilId(sharedPref.getString(PARAM_CIVIL_ID, ""));
            accessToken.setPersonName(sharedPref.getString(PARAM_PERSON_NAME, ""));
            accessToken.setImage(sharedPref.getString(PARAM_IMAGE, ""));
            return accessToken;
        }
    }

    @Override
    public AppCurrentUser getCurrentUser() {
        if (!TextUtils.isEmpty(AccessToken.getInstance().getAccessTokenString())) {
            return AppCurrentUser.getInstance();

        } else {
            SharedPreferences sharedPref = getSharedPreferences(PREFS_CURRENT_USER, Context.MODE_PRIVATE);
            AppCurrentUser appCurrentUser = AppCurrentUser.getInstance();
            appCurrentUser.setIsParent(sharedPref.getBoolean(PREFS_IS_PARENT, true));
            appCurrentUser.setCivilId(sharedPref.getString(PARAM_CIVIL_ID, ""));
            return appCurrentUser;
        }
    }

    private String getDeviceId() {
        SharedPreferences sharedPref = mContext.getSharedPreferences(PREFS_API_REGISTER_DEVICE, Context.MODE_PRIVATE);
        return sharedPref.getString(PREFS_DEVICE_ID, "");
    }

    @Override
    public void displayAlertDialog(Context context, String message) {

    }

    @Override
    public boolean isConnected() {
        return NetworkUtility.isConnected(this);
    }

    @Override
    public void customToolbarBackButtonClicked() {
        onBackPressed();
    }

    @Override
    public void changeSideMenuToolBarVisibility(int visibility) {
        if (toolbar != null)
            toolbar.setVisibility(visibility);
        if (mDrawer != null)
            shouldLockDrawer(visibility != View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (getCurrentFragment() instanceof HomeFragment) {
            moveTaskToBack(true);
        } else {
            super.onBackPressed();
        }
    }

    //side menu
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        changeSideMenuToolBarVisibility(View.GONE);
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        String title = item.getTitle().toString();
        if (menus != null && menus.length() > 0) {
            try {
                JSONArray array = new JSONArray(menus);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = array.getJSONObject(i);
                        int nav_id = jsonObject.getInt("menuId");
                        String nav_name = jsonObject.getString("menuName");
                        String nav_name_ar = jsonObject.getString("menuNameNls");
                        String nav_icon = jsonObject.getString("iconClass");
                        String nav_Url = jsonObject.getString("templateUrl");
                        String nav_Url_ar = jsonObject.getString("templateUrlNls");
                        if (nav_id == 250) {
                            nav_Url = "https://docs.google.com/gview?embedded=true&url=" + nav_Url;
                            nav_Url_ar = "https://docs.google.com/gview?embedded=true&url=" + nav_Url_ar;
                        }
                        if (id == nav_id) {
                            if (getStoredLanguage().equals(LANGUAGE_ENGLISH))
                                changeFragmentTo(WebSideMenuFragment.newInstance(nav_Url, nav_name), WebSideMenuFragment.class.getSimpleName());
                            else
                                changeFragmentTo(WebSideMenuFragment.newInstance(nav_Url_ar, nav_name_ar), WebSideMenuFragment.class.getSimpleName());
                            break;
                        } else if (id == R.id.nav_english) {
                            storeLanguage(LANGUAGE_ENGLISH);
                            changeLanguageTo(LANGUAGE_ENGLISH, true);
                            break;
                        } else if (id == R.id.nav_arabic) {
                            storeLanguage(LANGUAGE_ARABIC);
                            changeLanguageTo(LANGUAGE_ARABIC, true);
                            break;
                        } else if (id == R.id.nav_about) {
                            displayAboutAppDialog();
                            break;
                        } else if (id == R.id.nav_feedback) {
                            changeFragmentTo(FeedbackFragment.newInstance(), FeedbackFragment.class.getSimpleName());
                            break;
                        } else if (title.equals(getResources().getString(R.string.patients_rights))) {
                            //   changeFragmentTo(WebSideMenuFragment.newInstance(8), WebSideMenuFragment.class.getSimpleName());
                        } else if (id == R.id.nav_logout) {
                            changeSideMenuToolBarVisibility(View.VISIBLE);
                            if (AppCurrentUser.getInstance().getIsParent())
                                logout();
                            else {
                                finish();
                                Intent ParentHome = new Intent(MainActivity.this, MainActivity.class);
                                startActivity(ParentHome);
                            }
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }// else {
        if (id == R.id.nav_english) {
            storeLanguage(LANGUAGE_ENGLISH);
            changeLanguageTo(LANGUAGE_ENGLISH, true);
        } else if (id == R.id.nav_arabic) {
            storeLanguage(LANGUAGE_ARABIC);
            changeLanguageTo(LANGUAGE_ARABIC, true);
        } else if (id == R.id.nav_about) {

            displayAboutAppDialog();
        } else if (id == R.id.nav_feedback) {
            changeFragmentTo(FeedbackFragment.newInstance(), FeedbackFragment.class.getSimpleName());
        }/* else if (title.equals(getResources().getString(R.string.patients_rights))) {
        //   changeFragmentTo(WebSideMenuFragment.newInstance(8), WebSideMenuFragment.class.getSimpleName());
    }*/ else if (id == R.id.nav_logout) {
            changeSideMenuToolBarVisibility(View.VISIBLE);
            if (AppCurrentUser.getInstance().getIsParent())
                logout();
            else {
                finish();
                Intent ParentHome = new Intent(MainActivity.this, MainActivity.class);
                startActivity(ParentHome);
            }
            //     }
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //BottomNavigationView

    @Override
    public void changeFragmentTo(Fragment fragmentToLoad, String fragmentTag) {
        if (getSupportFragmentManager().findFragmentByTag(fragmentTag) == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exist_right_to_left, R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                    .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.fl_main_container, fragmentToLoad, fragmentTag)
                    .addToBackStack(fragmentTag)
                    .commit();

        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exist_right_to_left, R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                    .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.fl_main_container, fragmentToLoad, fragmentTag)
                    .commit();
        }
    }

    private SpannableString applyFontToMenuItem(String mi) {
        Typeface font = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            font = getResources().getFont(R.font.sky);
        }
        SpannableString mNewTitle = new SpannableString(mi);
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return mNewTitle;
    }

    private void logout() {
        displayLogoutDialog();
    }

    private void displayLogoutDialog() {
        android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(mContext);
        builder1.setMessage(getResources().getString(R.string.confirm_logout_msg));
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                getResources().getString(R.string.yes_dialog),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        unRegisterDeviceId();
                        clearSharedPrefs();
                        clearBackStack();
                        setupLoginFragment();
                        clearNotificationSharedPrefs(9);
                    }
                });

        builder1.setNegativeButton(
                getResources().getString(R.string.no_dialog),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        android.app.AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void displayAboutAppDialog() {
        changeSideMenuToolBarVisibility(View.VISIBLE);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
        builder.setMessage(getResources().getString(R.string.about_app_info));
        builder.setCancelable(true);
        builder.setPositiveButton(
                getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private void unRegisterDeviceId() {
        String fullUrl = API_NEHR_URL + "device/unRegister?deviceId=" + getDeviceId() + "&flag=" + API_ANDROID_FLAG + "&appShortCode=" + API_ANDROID_APP_CODE + "&civilId=" + getCurrentUser().getCivilId();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fullUrl, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt(API_RESPONSE_CODE) == 0) {
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
                headers.put("Authorization", API_GET_TOKEN_BEARER + getAccessToken().getAccessTokenString());
                return headers;
            }

        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);

        mQueue.add(jsonObjectRequest);

    }


    private void clearSharedPrefs() {
        SharedPreferences sharedPref;
        SharedPreferences.Editor editor;

        sharedPref = mContext.getSharedPreferences(PREFS_API_GET_TOKEN, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.clear();
        editor.apply();

        sharedPref = mContext.getSharedPreferences(PREFS_CURRENT_USER, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.clear();
        editor.apply();


        sharedPref = mContext.getSharedPreferences(PREFS_API_REGISTER_DEVICE, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.clear();
        editor.apply();
    }

    private void clearBackStack() {
        androidx.fragment.app.FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            androidx.fragment.app.FragmentManager.BackStackEntry entry = fragmentManager.getBackStackEntryAt(0);
            fragmentManager.popBackStack(entry.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public void shouldLockDrawer(boolean shouldLock) {
        if (shouldLock) {
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver),
                new IntentFilter("MyNewToken")
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mProgressDialog.dismissDialog();
            String newToken = Objects.requireNonNull(intent.getExtras()).getString("token");
            unRegisterDeviceId();
            clearSharedPrefs();
            registerDevice(newToken);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        changeLanguageTo(getStoredLanguage(), false);
        if (checkPlayServices(this)) {

            if (dataUpdateReceiver == null) dataUpdateReceiver = new DataUpdateReceiver();
            IntentFilter intentFilter = new IntentFilter("TEST");
            registerReceiver(dataUpdateReceiver, intentFilter);
            // FCM
            getNotificationCurrentToken();
        }

        if (getCurrentFragment() != null) {
            //if screen rotated retain Fragment
            if (getCurrentFragment() instanceof HomeFragment) {
                changeSideMenuToolBarVisibility(View.VISIBLE);
            } else {
                changeSideMenuToolBarVisibility(View.GONE);
                changeFragmentTo(getCurrentFragment(), getCurrentFragment().getTag());
            }

        } else {
            //set Home/Main/default fragment
            changeSideMenuToolBarVisibility(View.VISIBLE);
            changeFragmentTo(HomeFragment.newInstance(), HomeFragment.class.getSimpleName());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dataUpdateReceiver != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(dataUpdateReceiver);
    }

    private void setupLoginFragment() {
        finish();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    // FCM
    private void getNotificationCurrentToken() {
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
                            if (!getDeviceId().equals(deviceId)) {
                                registerDevice(deviceId);
                            }


                        }
                    });
        } else {
            new Thread() {
                @Override
                public void run() {
                    try {
                        // read from agconnect-services.json
                        String appId = AGConnectServicesConfig.fromContext(mContext).getString("client/app_id");
                        String token = HmsInstanceId.getInstance(mContext).getToken(appId, "HCM");
                        Log.i(TAG, "get token:" + token);
                        if (!TextUtils.isEmpty(token)) {
                            if (!getDeviceId().equals(token)) {
                                registerDevice(token);
                            }
                        }
                    } catch (com.huawei.hms.common.ApiException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
// [END retrieve_current_token]
    }

    private void registerDevice(final String deviceId) {
        String fullUrl = API_NEHR_URL + "v2/device/register";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, fullUrl, getJSONRequestParam(deviceId)
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt(API_RESPONSE_CODE) == 0) {
                        saveRegisterDeviceDetails(deviceId);
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
                headers.put("Authorization", API_GET_TOKEN_BEARER + getAccessToken().getAccessTokenString());
                return headers;
            }

        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);

        mQueue.add(jsonObjectRequest);
    }

    private JSONObject getJSONRequestParam(String deviceId) {
        Map<String, Object> params = new HashMap<>();
        params.put("deviceId", deviceId);
        params.put("flag", API_ANDROID_FLAG);
        if (checkPlayServices(this))
            params.put("appShortCode", API_ANDROID_APP_CODE);
        else
            params.put("appShortCode", API_HUAWEI_APP_CODE);
        params.put("civilId", Long.parseLong(getCurrentUser().getCivilId()));
        params.put("loginId", "");
        return new JSONObject(params);
    }

    private void saveRegisterDeviceDetails(String deviceId) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(PREFS_API_REGISTER_DEVICE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(PREFS_DEVICE_ID, deviceId);
        editor.apply();
    }

    public void logoutUser(View view) {
        if (AppCurrentUser.getInstance().getIsParent())
            logout();
        else {
            finish();
            Intent ParentHome = new Intent(MainActivity.this, MainActivity.class);
            ParentHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(ParentHome);
        }
    }

    // to prevent appearing the keyboard on undesired Screens
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int[] scrcoords = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //handelDataExtras();
    }

    private void clearNotificationSharedPrefs(int notificationType) {
        SharedPreferences sharedPref;
        SharedPreferences.Editor editor;

        sharedPref = mContext.getSharedPreferences("Counting", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        switch (notificationType) {
            case 1:
                editor.remove("appointmentCount");
                editor.apply();
                break;
            case 2:
                editor.remove("notificationCount");
                editor.apply();
                break;
            case 3:
                editor.remove("chatCount");
                editor.apply();
                break;
            default:
                editor.remove("appointmentCount");
                editor.remove("notificationCount");
                editor.remove("chatCount");
                editor.apply();
        }
    }

    private void setAppLanguage(String language) {
        AppLanguage appLanguage = AppLanguage.getInstance();
        appLanguage.setSelectedLanguage(language);
    }

    private void changeLanguageTo(String language, boolean recreate) {
        currentLanguage = language;
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        configuration.setLocale(locale);
        mContext.getResources().updateConfiguration(configuration, mContext.getResources().getDisplayMetrics());
        if (recreate) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }


    private void storeLanguage(String language) {
        SharedPreferences sharedPref = getSharedPreferences(LANGUAGE_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(LANGUAGE_SELECTED, language);
        editor.apply();
    }

    private String getStoredLanguage() {
        SharedPreferences sharedPref = getSharedPreferences(LANGUAGE_PREFS, Context.MODE_PRIVATE);
        return sharedPref.getString(LANGUAGE_SELECTED, getDeviceLanguage());
    }

    private String getDeviceLanguage() {
        return Locale.getDefault().getLanguage();
    }

    private class DataUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("TEST")) {
                if (AppCurrentUser.getInstance().getIsParent())
                    checkNotificationsCounter();
            }
        }
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
}

//main Activity