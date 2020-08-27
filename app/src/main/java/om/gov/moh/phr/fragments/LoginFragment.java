package om.gov.moh.phr.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.AccessToken;
import om.gov.moh.phr.dialogfragments.CancelAppointmentDialogFragment;
import om.gov.moh.phr.dialogfragments.DisclaimerDialogFragment;
import om.gov.moh.phr.interfaces.DialogFragmentInterface;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.AppCurrentUser;
import om.gov.moh.phr.models.MyProgressDialog;

import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_ACCESS_TOKEN;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_MESSAGE;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_RESULT;
import static om.gov.moh.phr.models.MyConstants.API_PHR;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_ARABIC;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_PREFS;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_SELECTED;
import static om.gov.moh.phr.models.MyConstants.PARAM_CIVIL_ID;
import static om.gov.moh.phr.models.MyConstants.PARAM_IMAGE;
import static om.gov.moh.phr.models.MyConstants.PARAM_PERSON_NAME;
import static om.gov.moh.phr.models.MyConstants.PARAM_SIDE_MENU;
import static om.gov.moh.phr.models.MyConstants.PREFS_API_GET_TOKEN;
import static om.gov.moh.phr.models.MyConstants.PREFS_CURRENT_USER;
import static om.gov.moh.phr.models.MyConstants.PREFS_IS_PARENT;
import static om.gov.moh.phr.models.MyConstants.PREFS_SIDE_MENU;

/**
 * PHR-DOC : LoginFragment | JIRA board : http://10.99.2.38:8080/browse/PHR-1
 * <p>
 * LoginFragment : is the first Activity of PHR
 * <p>
 * #Requirements :
 * 1. User should be allowed to to enter civil ID only at first.
 * 2. Once the User enters civil id, then he/she should clicks "Get OTP". Otherwise user would asked to enter the civil id.
 * 3. After clicking on "Get OTP" you should call : https://5.162.223.156/phrapi/getOtp/<user-civil-id>. eg: https://5.162.223.156/phrapi/getOtp/62163078. This api will verify user civilId if it is valid it will send sms. Once the Api response shows that civilId is valid then only display the OTP input.
 * 4. User  should receive OTP as sms on his/her registered mobile. #TODO : not yet implemented.
 * 5. User Should enter OTP on OTP column.
 * 6. User should click on Login, if both civilId & OTP are available the only display  disclaimer dialog.
 * 7. Disclaimer dialog should allow user to accept/deny. if user accepts the Disclaimer then only call : https://5.162.223.156/phrapi/validateOtp/<user-civil-id>/<otp>. This api will verify user civilId & otp  if they are valid. Once the Api response shows that they are valid then login the user.
 * <p>
 * <p>
 * ##Structure :
 * <p>
 * 1. Clicking on otp Button {@link #btnGetOTP} would call {@link #getOTP(String civilId)}, but first you would need to check if there is internet connection using { NetworkUtility.isConnected() }. Also, check civilId on {@link #tietCivilId}.
 * 2. Clicking on login Button {@link #btnLogin} would call {@link #showDisclaimerDialog()}, but first you would need to check if there is civilId on {@link #tietCivilId} and  otp on {@link #tietOTP}.
 * 3. Disclaimer Dialog has 2 buttons "Accept" and "Decline". "Accept" will would call {@link #onAccept()}. "Decline" would call {@link #onDecline()}. Note : {@link #onAccept()} and {@link #onDecline()} are called invoiced because  LoginFragment implements {@link DialogFragmentInterface}
 */

public class LoginFragment extends Fragment {

    private static final String API_URL_GET_OTP = API_PHR + "getOtp/";
    private static final String API_URL_VALIDATE_OTP = API_PHR + "validateOtp/";
    private RequestQueue mQueue;
    private MyProgressDialog mProgressDialog;
    private TextInputEditText tietCivilId;
    private TextInputLayout tilOTP;
    private TextInputEditText tietOTP;
    private Button btnGetOTP;
    private Button btnLogin;
    private DisclaimerDialogFragment mDisclaimerDialogFragment;
    private Context mContext;
    private MediatorInterface mMediatorCallback;
    private ToolbarControllerInterface mToolbarCallback;
    private TextView tvCancel;
    private ConstraintLayout constraintLayout;
// Implement this interface in your Activity.

    public interface OnCallbackReceived {
        void Update(JSONArray menus);
    }

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mMediatorCallback = (MediatorInterface) context;
        mToolbarCallback = (ToolbarControllerInterface) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      /*  if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }*/
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View parentView = inflater.inflate(R.layout.fragment_login, container, false);

        mProgressDialog = new MyProgressDialog(mContext);// initializes progress dialog
        mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, getSocketFactory())); // initializes mQueue : we need to use  Volley.newRequestQueue(this, new HurlStack(null, getSocketFactory())) because we need to connect the app to secure server "https".

        constraintLayout = parentView.findViewById(R.id.constraintLayout_login);
        tietCivilId = parentView.findViewById(R.id.tiet_civil_id);
        tilOTP = parentView.findViewById(R.id.til_otp);
        tietOTP = parentView.findViewById(R.id.tiet_otp);
        btnGetOTP = parentView.findViewById(R.id.btn_get_otp);
        btnLogin = parentView.findViewById(R.id.btn_login);
        ImageView ivLogo = parentView.findViewById(R.id.imageView);
        tvCancel = parentView.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tietOTP.setText("");
                tietCivilId.setText("");
                tilOTP.setVisibility(View.GONE);
                btnGetOTP.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.INVISIBLE);

            }
        });
        if (getStoredLanguage().equals(LANGUAGE_ARABIC))
            ivLogo.setBackground(getResources().getDrawable(R.drawable.phr_logo_ar));


/**
 *  if SharedPreferences exist and it contains API_GET_TOKEN_ACCESS_TOKEN then user already logged in before. thus, open MainActivity
 */
        /*  if (mContext.getSharedPreferences(PREFS_API_GET_TOKEN, Context.MODE_PRIVATE).contains(API_GET_TOKEN_ACCESS_TOKEN)) {
         *//*  SharedPreferences prefs = getSharedPreferences(PREFS_API_GET_TOKEN, Context.MODE_PRIVATE);
            tietCivilId.setText(prefs.getString(PARAM_CIVIL_ID, ""));*//*
            moveToMainActivity();
        } else*/
        {
            tietOTP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(tietCivilId.getText().toString())) {
                        tietCivilId.setError(getString(R.string.alert_empty_field));
                    }
                }
            });

            btnGetOTP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(tietCivilId.getText().toString())) {
                        tietCivilId.setError(getString(R.string.alert_empty_field));

                    } else {
                        getOTP(tietCivilId.getText().toString());

                    }
                }
            });

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean shouldLogin = true;

                    if (TextUtils.isEmpty(tietCivilId.getText().toString())) {
                        tietCivilId.setError(getString(R.string.alert_empty_field));
                        shouldLogin = false;
                    }

                    if (TextUtils.isEmpty(tietOTP.getText().toString())) {
                        tietOTP.setError(getString(R.string.alert_empty_field));
                        shouldLogin = false;
                    }

                    if (shouldLogin) {//NetworkUtility.isConnected(mContext) &
                        showDisclaimerDialog();
                    }
                }
            });
        }
        return parentView;
    }


    private void getOTP(String civilId) {
        mProgressDialog.showDialog();
        String fullUrl = API_PHR + "v2/getOtp";

        // getOtp api is using HTTP.GET inorder to get data, that's why we need Request.Method.GET
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, fullUrl, getJSONRequestParams(civilId)
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("api-getOtp", response.toString());

                /**
                 * all apis returns json response. if you need to parse or get certain object from json the you need try{} catch(JSONException e){}
                 */
                try {

                    /**
                     *  all apis return API_RESPONSE_CODE. If and only if API_RESPONSE_CODE == 0 then the api call was successful.
                     *  all apis return API_RESPONSE_RESULT which is string message that describe the result of calling an api. can be very useful if API_RESPONSE_CODE != 0 which would tell what went wrong.
                     */
                    if (response.getInt(API_RESPONSE_CODE) == 0
                            && response.getString(API_RESPONSE_RESULT) != null
                            && response.getString(API_RESPONSE_RESULT).equalsIgnoreCase("true")) {
                        displayLoginForm();
                    } else {
                        Toast.makeText(mContext, response.getString(API_RESPONSE_MESSAGE), Toast.LENGTH_SHORT).show();
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
//                Log.d("enc", error.toString());
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
//                headers.put("Authorization", auth);
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
        Map<String, Long> params = new HashMap<>();
        params.put("civilId", Long.parseLong(civilId));
        params.put("otp", Long.parseLong(otp));
        return new JSONObject(params);
    }
    /**
     * call displayLoginForm() to display login form : otp input + login button.
     */
    private void displayLoginForm() {
        /* if (NetworkUtility.isConnected(mContext))*/
        {
            tilOTP.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.VISIBLE);
            btnGetOTP.setVisibility(View.INVISIBLE);

            //change constraint of cancel
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.connect(R.id.tv_cancel,ConstraintSet.TOP,R.id.btn_login,ConstraintSet.BOTTOM,0);
            constraintSet.applyTo(constraintLayout);
        }

    }

    private void showDisclaimerDialog() {
        mDisclaimerDialogFragment = DisclaimerDialogFragment.newInstance();
        mDisclaimerDialogFragment.setDialogFragmentListener(new DialogFragmentInterface() {

            /**
             * onAccept(), onAccept(int position) and onDecline() are overridden functions of {@link DialogFragmentInterface}
             */
            @Override
            public void onAccept() {
                if (mContext.getSharedPreferences(PREFS_API_GET_TOKEN, Context.MODE_PRIVATE).contains(API_GET_TOKEN_ACCESS_TOKEN)) {
                    localLogin();
                } /*else if (NetworkUtility.isConnected(mContext))*/
                {
                    //TODO : below line is the orginal behaviour
                    login(tietCivilId.getText().toString(), tietOTP.getText().toString());
                }
            }

            @Override
            public void onAccept(int position) {

            }

            @Override
            public void onDecline() {
                tietOTP.setText("");
                tilOTP.setVisibility(View.GONE);
                btnGetOTP.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.INVISIBLE);
                mDisclaimerDialogFragment.dismiss();
            }


        });
        mDisclaimerDialogFragment.show(getChildFragmentManager(), CancelAppointmentDialogFragment.class.getSimpleName());
    }

    private void login(final String civilId, String otp) {
        String fullUrl = API_PHR+"v2/validateOtp";
        Log.d("resp-login_URl", fullUrl);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, fullUrl, getValidateJSONRequestParams(civilId, otp)
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("resp-login", response.toString());
                try {
                    if (response.getInt(API_RESPONSE_CODE) == 0
                            && response.getString(API_RESPONSE_RESULT) != null) {
                        JSONObject jsonObject = response.getJSONObject(API_RESPONSE_RESULT);
                        storeAccessToken(jsonObject.optString(API_GET_TOKEN_ACCESS_TOKEN)
                                , civilId, jsonObject.optString("personName"), jsonObject.optString("image"), jsonObject.getJSONArray("menus").toString());
                    Log.d("sideMenu", response.toString());

                    } else {
                        Toast.makeText(mContext, response.getString(API_RESPONSE_MESSAGE), Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismissDialog();
                    }
                } catch (JSONException e) {
//                    Log.d("enc", e.getMessage());

                    e.printStackTrace();
                }

                mProgressDialog.dismissDialog();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.d("enc", error.toString());
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
//                headers.put("Authorization", auth);
                return headers;
            }


        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        mQueue.add(jsonObjectRequest);
    }


    /**
     * storeAccessToken() will store both civilId and accessToken in SharedPreferences, so we can use them in the future.
     *  @param accessTokenValue
     * @param civilId
     * @param menus
     */
    private void storeAccessToken(String accessTokenValue, String civilId, String personName, String image, String menus) {
        SharedPreferences.Editor editor;

        AccessToken accessToken = AccessToken.getInstance();
        accessToken.setAccessCivilId(civilId);
        accessToken.setAccessTokenString(accessTokenValue);
        accessToken.setPersonName(personName);
        accessToken.setImage(image);

        SharedPreferences sharedPrefAccessToken = mContext.getSharedPreferences(PREFS_API_GET_TOKEN, Context.MODE_PRIVATE);
        editor = sharedPrefAccessToken.edit();
        editor.putString(API_GET_TOKEN_ACCESS_TOKEN, accessTokenValue);
        editor.putString(PARAM_CIVIL_ID, civilId);
        editor.putString(PARAM_PERSON_NAME, personName);
        editor.putString(PARAM_IMAGE, image);
        editor.apply();


        AppCurrentUser currentUser = AppCurrentUser.getInstance();
        currentUser.setCivilId(civilId);
        currentUser.setIsParent(true);

        SharedPreferences sharedPrefCurrentUser = mContext.getSharedPreferences(PREFS_CURRENT_USER, Context.MODE_PRIVATE);
        editor = sharedPrefCurrentUser.edit();
        editor.putString(PARAM_CIVIL_ID, civilId);
        editor.putBoolean(PREFS_IS_PARENT, true);
        editor.apply();

        SharedPreferences sharedPrefSideMenu = mContext.getSharedPreferences(PREFS_SIDE_MENU, Context.MODE_PRIVATE);
        editor = sharedPrefSideMenu.edit();
        editor.putString(PARAM_SIDE_MENU, menus);
        editor.apply();


        mDisclaimerDialogFragment.dismiss();
        moveToMainActivity();
    }

    /**
     * call moveToMainActivity() inorder to move to mainActivity
     */
    private void moveToMainActivity() {
        androidx.fragment.app.FragmentManager fm = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    /**
     * getSocketFactory() will help us to create/start communicate with servers on https domain
     * you would need to store the certificate in the project under raw folder. certificate : {@link om.gov.moh.phr.R.raw#cert}
     *
     * @return SSLSocketFactory to
     */
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

    /**
     * login using SharedPreferences
     */
    private void localLogin() {
        mDisclaimerDialogFragment.dismiss();
        moveToMainActivity();
    }

    private String getStoredLanguage() {
        SharedPreferences sharedPref = mContext.getSharedPreferences(LANGUAGE_PREFS, Context.MODE_PRIVATE);
        return sharedPref.getString(LANGUAGE_SELECTED, getDeviceLanguage());
    }

    private String getDeviceLanguage() {
        return Locale.getDefault().getLanguage();
    }
}
