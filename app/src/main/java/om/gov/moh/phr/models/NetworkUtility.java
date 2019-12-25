package om.gov.moh.phr.models;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtility {

    public static boolean isConnected(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo network = connectivityManager.getActiveNetworkInfo();
        if (network != null && network.isConnectedOrConnecting()) {
            NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobile = connectivityManager.getNetworkInfo
                    (ConnectivityManager.TYPE_MOBILE);

            return (mobile != null) && (mobile.isConnectedOrConnecting()) || (wifi != null) && (wifi.isConnectedOrConnecting());


        } else return false;
    }

}