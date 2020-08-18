package om.gov.moh.phr.interfaces;

/**
 * this interface allows the "recycler view adapter"  to communicate with ...
 * the "fragment" which holds the recycler view, that fragment must Implements this interface ...
 * ... in order for communication to take place
 */
public interface AdapterToFragmentConnectorInterface {


    /**
     * onMyListItemClicked() get called it if an item of recycler view was clicked
     *
     * @param dataToPass : dataType > any >> put data you want to pass here , accept any data type: String, int, boolean ..., etc
     * @param dataTitle  : dataType > String :: a String that make it easy for you to identify what data is sent
     */
    <T> void onMyListItemClicked(T dataToPass, String dataTitle);

    <T> void onMyListItemClicked(T dataToPass, String dataTitle, int position);


}
