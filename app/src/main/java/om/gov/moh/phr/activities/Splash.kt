package om.gov.moh.phr.activities

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.wang.avi.AVLoadingIndicatorView
import om.gov.moh.phr.R
import om.gov.moh.phr.models.MyConstants
import om.gov.moh.phr.models.MyConstants.*
import java.util.*

class Splash : AppCompatActivity() {
    lateinit var pageBackground: ImageView
    lateinit var indicatorView: AVLoadingIndicatorView


    lateinit var context: Context


    val timer = Timer("schedule", true);
    //private val networkMonitor = NetworkMonitorUtil(this)

    var isConnection: Boolean = true
    private var currentLanguage: String = getDeviceLanguage()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeLanguageTo(getStoredLanguage(), false)
        setContentView(R.layout.activity_splash)

        pageBackground = findViewById(R.id.img_splash)
        indicatorView = findViewById(R.id.indicatorViewSplash)

        indicatorView.smoothToShow()

        if (getStoredLanguage().equals(LANGUAGE_ENGLISH))
            pageBackground.setImageResource(R.drawable.splash_en)
        else
            pageBackground.setImageResource(R.drawable.splash_ar)
        val intent: Intent
        if (getSharedPreferences(PREFS_API_GET_TOKEN, Context.MODE_PRIVATE).contains(API_GET_TOKEN_ACCESS_TOKEN))
            intent = Intent(this, MainActivity::class.java)
        else
            intent = Intent(this, LoginActivity::class.java)
        val handler = Handler()
        handler.postDelayed({
            finish()
            startActivity(intent)
        }, 4000)
    }

    override fun onResume() {
        super.onResume()
        changeLanguageTo(getStoredLanguage(), false)
    }

    private fun changeLanguageTo(language: String, recreate: Boolean) {
        currentLanguage = language
        val locale = Locale(language)
        Locale.setDefault(locale)
        val configuration = Configuration()
        configuration.locale = locale
        configuration.setLocale(locale)
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics())
        if (recreate) {
            val intent = intent
            finish()
            startActivity(intent)
        }
    }

    private fun getDeviceLanguage(): String {
        return Locale.getDefault().language
    }

    private fun getStoredLanguage(): String {
        val sharedPref = getSharedPreferences(MyConstants.LANGUAGE_PREFS, MODE_PRIVATE)
        return sharedPref.getString(MyConstants.LANGUAGE_SELECTED, getDeviceLanguage()).toString()
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //Clear the Activity's bundle of the subsidiary fragments' bundles.
        outState.clear()
    }
}