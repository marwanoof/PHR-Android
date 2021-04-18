package om.gov.moh.phr.models;

public class MyConstants {
    //PARAM
    public static final String PARAM_CIVIL_ID = "PARAM_CIVIL_ID";
    public static final String PARAM_PERSON_NAME = "PARAM_PERSON_NAME";
    public static final String PARAM_IMAGE = "PARAM_IMAGE";
    public static final String PARAM_API_DEMOGRAPHICS_ITEM = "PARAM_API_DEMOGRAPHICS_ITEM";

    public static final String ACTION_CANCEL = "ACTION_CANCEL";
    public static final String ACTION_RESCHEDULE = "ACTION_RESCHEDULE";
    public static final String PARAM_APPOINTMENTS = "PARAM_APPOINTMENTS";
    public static final String PARAM_EST_CODE = "PARAM_EST_CODE";
    public static final String PARAM_RESERVATION_ID = "PARAM_RESERVATION_ID";

    public static final int GALLERY = 1, CAMERA = 2;
    public static final String PHR_IMAGES_DIRECTORY_NAME = "phr_Images";
    public static final String IMAGE_EXTENSION = "jpg";
    //api
    //public static final String API_NEHR_URL = "https://phr.moh.gov.om/nehrapi/";
    public static final String API_NEHR_URL = "https://mshifa.moh.gov.om/nehrapi/";

    //public static final String API_PHR ="https://phr.moh.gov.om/phrapi/";
    public static final String API_PHR ="https://mshifa.moh.gov.om/phrapi/";

    public static final String API_GET_TOKEN_BEARER = "Bearer ";
    public static final String API_GET_TOKEN_ACCESS_TOKEN = "access_token";
    public static final String API_RESPONSE_CODE = "code";
    public static final String API_RESPONSE_MESSAGE = "message";
    public static final String API_RESPONSE_RESULT = "result";
    public static final String API_TEXTUAL_TEMPLATE = "H";
    public static final String API_CULTURE_TEMPLATE = "C";
    public static final String API_TABULAR_TEMPLATE = "D";
    public static final int API_ANDROID_FLAG = 0;
    public static final String API_ANDROID_APP_CODE = "PHRdroid";
    public static final String API_HUAWEI_APP_CODE = "PHRhuawei";

    //prefs
    public static final String PREFS_API_GET_TOKEN = "api_auth_token";
    public static final String PREFS_API_REGISTER_DEVICE = "api_register_device";
    public static final String PREFS_CURRENT_USER = "current_user";
    public static final String PREFS_SIDE_MENU = "api_sidemenu";
    public static final String PARAM_SIDE_MENU = "PARAM_SIDE_MENU";

    public static final String PREFS_DEVICE_ID = "PREFS_DEVICE_ID";
    public static final String PREFS_IS_PARENT = "PREFS_IS_PARENT";

    //language
    public static final String LANGUAGE_ARABIC = "ar";
    public static final String LANGUAGE_ENGLISH = "en";
    public static final String LANGUAGE_PREFS = "language_prefs";
    public static final String LANGUAGE_SELECTED = "LANGUAGE_SELECTED";

    //activate userpreferences home style
    public static boolean IS_SCROLL_LIST = true;

    //activate ClinicalNotes
    public static boolean Clinical_Notes = false;

    //activate Chat
    public static String Chat_Enable = "";

    //activate appointment
    public static String appointment_Enable = "";

    //fetch age
    public static String user_age = "--";

}
