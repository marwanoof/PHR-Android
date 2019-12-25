package om.gov.moh.phr.interfaces;

import android.content.Context;

import androidx.fragment.app.Fragment;

import javax.net.ssl.SSLSocketFactory;

import om.gov.moh.phr.apimodels.AccessToken;
import om.gov.moh.phr.models.AppCurrentUser;

/**
 * Implementation of the functions is in {@link om.gov.moh.phr.activities.MainActivity}
 */
public interface MediatorInterface {
    /**
     * this function will allow us to change the fragments dynamically
     *
     * @param fragmentToLoad : dataType > v4.app.Fragment :: the object of the fragment you want to load in form of MyFragment() or MyFragment().newInstance()
     * @param fragmentTag    :  dataType > String :: a String which identify the "tag" of the fragment in form of "FRAGMENT_TAG_MY_FRAGMENT",
     */
    void changeFragmentTo(Fragment fragmentToLoad, String fragmentTag);

    /**
     * this function used to handling communication with https web serves
     *
     * @return SSLSocketFactory
     */
    SSLSocketFactory getSocketFactory();

    /**
     * @return accessToken
     */
    AccessToken getAccessToken();

    AppCurrentUser getCurrentUser();


    void displayAlertDialog(Context context, String message);

    boolean isConnected();

//    void changeButtonNavVisibilityTo(int sideMenuToolbarVisibility, int bottomNabBarVisibilit);

    void changeFragmentContainerVisibility(int fragmentContainerVisibility, int viewPagerVisibility);

    void slideTo(int index);

    void changeBottomNavVisibility(int bottomNavVisibility);


}


