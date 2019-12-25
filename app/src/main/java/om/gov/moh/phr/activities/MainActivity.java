package om.gov.moh.phr.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.AccessToken;
import om.gov.moh.phr.fragments.HomeFragment;
import om.gov.moh.phr.fragments.LoginFragment;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.AppCurrentUser;
import om.gov.moh.phr.models.AppLanguage;
import om.gov.moh.phr.models.ComponentConstants;
import om.gov.moh.phr.models.HomePagerAdapter;
import om.gov.moh.phr.models.MyProgressDialog;
import om.gov.moh.phr.models.NetworkUtility;
import om.gov.moh.phr.models.ViewPagerCustomDuration;

import static om.gov.moh.phr.models.MyConstants.API_ANDROID_APP_CODE;
import static om.gov.moh.phr.models.MyConstants.API_ANDROID_FLAG;
import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_ACCESS_TOKEN;
import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_MESSAGE;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_ARABIC;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_ENGLISH;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_PREFS;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_SELECTED;
import static om.gov.moh.phr.models.MyConstants.PARAM_CIVIL_ID;
import static om.gov.moh.phr.models.MyConstants.PREFS_API_GET_TOKEN;
import static om.gov.moh.phr.models.MyConstants.PREFS_API_REGISTER_DEVICE;
import static om.gov.moh.phr.models.MyConstants.PREFS_CURRENT_USER;
import static om.gov.moh.phr.models.MyConstants.PREFS_DEVICE_ID;
import static om.gov.moh.phr.models.MyConstants.PREFS_IS_PARENT;


/**
 * PHR-DOC : MainActivity | JIRA board : http://10.99.2.38:8080/browse/PHR-41
 * <p>
 * MainActivity : is the second Activity of PHR. this Activity handel many transactions.
 * <p>
 * #Requirements :
 * 1. the status bar should be white with dark icons.
 * 2. MainActivity should have side menu and button navigation view.
 * 3. Button navigation view should be linked with view pager
 * 4. Side menu  and its icon should be visible and accessible only if home screen is active.
 * 5. MainActivity should have 2 containers. 1# container that contains view pager. #2 container contains sub fragments.
 * 6. 1# container Visibility should be View.VISIBLE and 2# container Visibility should be View.GONE
 * <p>
 * ##Structure :
 * <p>
 * 1. initialize {mHomePagerAdapter} that is variable of type {@link HomePagerAdapter}. HomePagerAdapter class would handel loading fragments into the view pager
 */
public class MainActivity extends AppCompatActivity implements MediatorInterface, ToolbarControllerInterface, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private ViewPagerCustomDuration mViewPager;
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigation;
    private MyProgressDialog mProgressDialog;
    private RequestQueue mQueue;
    private Context mContext = MainActivity.this;
    String settingsLanguage = Locale.getDefault().getDisplayLanguage();
    //BottomNavigationView :  link bottom nav bar with view pager
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//enable viewpagerContainer to be visible if it is clicked from inner fragments!
            changeFragmentContainerVisibility(View.GONE, View.VISIBLE);

            switch (item.getItemId()) {
                case R.id.navigation_home: {
                    //code here ...
//                    changeButtonNavVisibilityTo(View.VISIBLE, View.VISIBLE);
                    mViewPager.setCurrentItem(0);

                    return true;
                }

                case R.id.navigation_appointment: {
                    //code here ...
                    mViewPager.setCurrentItem(1);
                    return true;
                }
                case R.id.navigation_notifications: {
                    //code here ...
                    mViewPager.setCurrentItem(2);
                    return true;
                }

                case R.id.navigation_chat: {
                    //code here ...
                    mViewPager.setCurrentItem(3);
                    return true;
                }
            }
            return false;
        }
    };


    //FCM
    public static boolean checkPlayServices(Activity activity) {
        final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activity, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
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
        androidx.fragment.app.FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        if(settingsLanguage.contains("العربية"))
            changeLanguageTo(LANGUAGE_ARABIC);
        else
            changeLanguageTo(LANGUAGE_ENGLISH);
        setContentView(R.layout.side_menu_main_page);

        requestNotificationPermission();

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


        //TODO: delete this to remove the toolbar
        //*************************************

        toolbar = findViewById(R.id.toolbar);
        setTitle("");
        //TODO: delete this to remove the toolbar
        //*************************************

        mDrawer = findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
        //*************************************


        mProgressDialog = new MyProgressDialog(mContext);// initializes progress dialog
        mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, getSocketFactory())); // initializes mQueue : we need to use  Volley.newRequestQueue(this, new HurlStack(null, getSocketFactory())) because we need to connect the app to secure server "https".


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //BottomNavigationView

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        if (getSharedPreferences(PREFS_API_GET_TOKEN, Context.MODE_PRIVATE).contains(API_GET_TOKEN_ACCESS_TOKEN)) {

            setupMainActivity();

        } else {
            setupLoginFragment();

        }
       if(settingsLanguage.contains("العربية")) {
           storeLanguage(LANGUAGE_ARABIC);
           setAppLanguage(LANGUAGE_ARABIC);
       }else {
           storeLanguage(LANGUAGE_ENGLISH);
           setAppLanguage(LANGUAGE_ENGLISH);
       }
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
                            Log.println(Log.ASSERT, "ERROR HERE: ", "3");
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
        mViewPager = findViewById(R.id.swipe_container);
        HomePagerAdapter mHomePagerAdapter =
                new HomePagerAdapter(getSupportFragmentManager(), this);
        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mHomePagerAdapter);


        // to link view pager with bottom nav bar
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                //code here
                BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigation.getChildAt(0);
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                bottomNavigation.setSelectedItemId(item.getId());

                if (i > 0) {
                    changeSideMenuToolBarVisibility(View.GONE);
                    shouldLockDrawer(true);
                } else {
                    changeSideMenuToolBarVisibility(View.VISIBLE);
                    shouldLockDrawer(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });


        if (checkPlayServices(this)) {
            // FCM
//            createChannelToShowNotifications();
            // FCM
            handelDataExtras();
            // FCM
            getNotificationCurrentToken();
        }
    }

    @Override
    public SSLSocketFactory getSocketFactory() {

        CertificateFactory cf = null;
        try {

            cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = getResources().openRawResource(R.raw.cert);
            Certificate ca;
            try {

                ca = cf.generateCertificate(caInput);
                Log.e("CERT", "ca=" + ((X509Certificate) ca).getSubjectDN());


            } finally {
                caInput.close();
            }


            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);


            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);


            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {

                    Log.e("CipherUsed", session.getCipherSuite());
                    return hostname.compareTo("5.162.223.156") == 0; //The Hostname of your server.

                }
            };


            HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
            SSLContext context = null;
            context = SSLContext.getInstance("TLS");

            context.init(null, tmf.getTrustManagers(), null);
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
    public void changeFragmentContainerVisibility(int fragmentContainerVisibility,
                                                  int viewPagerVisibility) {

        findViewById(R.id.fl_main_container).setVisibility(fragmentContainerVisibility);
        findViewById(R.id.swipe_container).setVisibility(viewPagerVisibility);
        if (fragmentContainerVisibility == View.VISIBLE) {
            shouldLockDrawer(true);
        } else {
            shouldLockDrawer(false);
        }

    }

    @Override
    public void changeBottomNavVisibility(int bottomNavVisibility) {
        changeSideMenuToolBarVisibility(bottomNavVisibility);
        findViewById(R.id.bottom_navigation).setVisibility(bottomNavVisibility);
        if (bottomNavVisibility == View.VISIBLE) {
            setupMainActivity();
        }
    }

    @Override
    public void slideTo(int index) {
        mViewPager.setCurrentItem(index, true);
    }


    @Override
    public void customToolbarBackButtonClicked() {
        onBackPressed();
    }

    @Override
    public void changeSideMenuToolBarVisibility(int visibility) {
        toolbar.setVisibility(visibility);
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
        // Handle navigation view item clicks here.
        int id = item.getItemId();
//        changeButtonNavVisibilityTo(View.GONE, View.GONE);

        if (id == R.id.nav_camera) {
            // Handle the camera action
          //  changeFragmentTo(VitalInfoFragment.newInstance("Home Side Menu"), VitalInfoFragment.class.getSimpleName());

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_logout) {
            logout();
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
                    .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.fl_main_container, fragmentToLoad, fragmentTag)
                    .addToBackStack(fragmentTag)
                    .commit();

        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.fl_main_container, fragmentToLoad, fragmentTag)
                    .commit();
        }
    }

    private void logout() {
        unRegisterDeviceId();
        clearSharedPrefs();
        clearBackStack();
        setupLoginFragment();
    }

    private void unRegisterDeviceId() {
        String fullUrl = "http://10.99.9.36:9000/nehrapi/device/unRegister?deviceId=" + getDeviceId() + "&flag=" + API_ANDROID_FLAG + "&appShortCode=" + API_ANDROID_APP_CODE + "&civilId=" + getCurrentUser().getCivilId();
        Log.d("unRegisterDevice-url", fullUrl);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fullUrl, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt(API_RESPONSE_CODE) == 0) {

                        Log.d("unRegisterDevice-result", response.getString(API_RESPONSE_MESSAGE));

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
                Log.d("unRegisterDevice", error.toString());
                error.printStackTrace();
                mProgressDialog.dismissDialog();
            }
        }) {
            //
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
//                headers.put("Accept", "application/json");
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
    protected void onResume() {
        super.onResume();
        if (getSharedPreferences(PREFS_API_GET_TOKEN, Context.MODE_PRIVATE).contains(API_GET_TOKEN_ACCESS_TOKEN)) {
            if (checkPlayServices(this)) {
                // FCM
//            createChannelToShowNotifications();
                // FCM
                handelDataExtras();
                // FCM
                getNotificationCurrentToken();
            }

            if (getCurrentFragment() != null) {
                //if screen rotated retain Fragment
                if (getCurrentFragment() instanceof HomeFragment) {
                    changeFragmentContainerVisibility(View.GONE, View.VISIBLE);
                    changeSideMenuToolBarVisibility(View.VISIBLE);
                } else {
                    changeFragmentContainerVisibility(View.VISIBLE, View.GONE);
                    changeSideMenuToolBarVisibility(View.GONE);
                    changeFragmentTo(getCurrentFragment(), getCurrentFragment().getTag());
                }

            } else {
                //set Home/Main/default fragment
                changeFragmentContainerVisibility(View.GONE, View.VISIBLE);
                changeSideMenuToolBarVisibility(View.VISIBLE);
                changeFragmentTo(HomeFragment.newInstance(), HomeFragment.class.getSimpleName());
            }
        } else {
            setupLoginFragment();
        }
    }

    private void setupLoginFragment() {
        changeFragmentContainerVisibility(View.VISIBLE, View.GONE);
        changeSideMenuToolBarVisibility(View.GONE);
        changeBottomNavVisibility(View.GONE);
        changeFragmentTo(LoginFragment.newInstance(), LoginFragment.class.getSimpleName());
    }


    //FCM
    private void handelDataExtras() {
        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG + "-intent", "Key: " + key + " Value: " + value);
            }
        }
// [END handle_data_extras]
    }

    // FCM
    private void getNotificationCurrentToken() {
        // Get token
        // [START retrieve_current_token]
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

                        registerDevice(deviceId);

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, deviceId);
                        Log.d(TAG + "-token", msg);
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();

                    }
                });
// [END retrieve_current_token]
    }

    private void registerDevice(final String deviceId) {

        String fullUrl = "http://10.99.9.36:9000/nehrapi/device/register?deviceId=" + deviceId + "&flag=" + API_ANDROID_FLAG + "&appShortCode=" + API_ANDROID_APP_CODE + "&civilId=" + getCurrentUser().getCivilId();
        Log.d("registerDevice-url", fullUrl);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fullUrl, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt(API_RESPONSE_CODE) == 0) {

                        Log.d("registerDevice-result", response.getString(API_RESPONSE_MESSAGE));

                       /* Gson gson = new Gson();
                        ApiDependentsHolder responseHolder = gson.fromJson(response.toString(), ApiDependentsHolder.class);
                        Log.d("resp-dependants", response.getJSONArray("result").toString());
                        prepareDependantsCards(responseHolder, container, inflater, llDependentsContainer);*/

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
                Log.d("registerDevice", error.toString());
                error.printStackTrace();
                mProgressDialog.dismissDialog();
            }
        }) {
            //
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
//                headers.put("Accept", "application/json");
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

    private void saveRegisterDeviceDetails(String deviceId) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(PREFS_API_REGISTER_DEVICE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(PREFS_DEVICE_ID, deviceId);
        editor.apply();
    }

    public void logoutUser(View view) {
        logout();
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
                ((InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
    private void setAppLanguage(String language) {
        AppLanguage appLanguage = AppLanguage.getInstance();
        appLanguage.setSelectedLanguage(language);
    }

    private void changeLanguageTo(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
    }


    private void storeLanguage(String language) {
        SharedPreferences sharedPref = getSharedPreferences(LANGUAGE_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(LANGUAGE_SELECTED, language);
        editor.apply();
    }
}

//main Activity