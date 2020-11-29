package om.gov.moh.phr.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.wang.avi.AVLoadingIndicatorView
import om.gov.moh.phr.R
import om.gov.moh.phr.interfaces.MediatorInterface
import om.gov.moh.phr.models.GlobalMethodsKotlin.Companion.showAlertDialog
import om.gov.moh.phr.models.NetworkMonitorUtil
import java.util.*
import kotlin.concurrent.schedule

class Splash : AppCompatActivity() {
    lateinit var pageBackground:ImageView
    lateinit var indicatorView: AVLoadingIndicatorView


    lateinit var context:Context


    val timer = Timer("schedule", true);
    //private val networkMonitor = NetworkMonitorUtil(this)

    var isConnection:Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        pageBackground = findViewById(R.id.img_splash)
        indicatorView = findViewById(R.id.indicatorViewSplash)

        indicatorView.smoothToShow()

        if (Locale.getDefault().displayLanguage == "English"){
            pageBackground.setImageResource(R.drawable.splash_en)
        }else{
            pageBackground.setImageResource(R.drawable.splash_ar)
        }

        val intent = Intent(this, MainActivity::class.java)
        val handler = Handler()
        handler.postDelayed({
            //finish()
            startActivity(intent)
        }, 4000)



     /*   networkMonitor.result = { isAvailable, type ->
            runOnUiThread {
                when (isAvailable) {
                    true -> {
                        isConnection = true
                    }
                    false -> {
                        isConnection = false
                    }
                }
            }
            connectionResult(isConnection)
        }*/


    }

    override fun onResume() {
        super.onResume()
        //networkMonitor.register()
    }
    private fun connectionResult(result : Boolean){
        val intent = Intent(this, LoginActivity::class.java)
        if (result){
            val handler = Handler()
            handler.postDelayed({
                //finish()
                startActivity(intent)
            }, 4000)
        }else{
            val title: String = resources.getString(R.string.alert_error_title)
            val body: String = resources.getString(R.string.alert_no_connection_body_1).toString() + "\n" + resources.getString(R.string.alert_connection_body2)
            showAlertDialog(this, title, body, resources.getString(R.string.ok), R.drawable.ic_error)
        }
    }


}