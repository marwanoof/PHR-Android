package om.gov.moh.phr.models

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.animation.Animation
import android.view.animation.AnimationUtils

import com.example.awesomedialog.*
import com.google.android.datatransport.runtime.backends.BackendResponse.ok
import om.gov.moh.phr.R
import om.gov.moh.phr.activities.LoginActivity

class GlobalMethodsKotlin {
    companion object {

        fun showAlertDialog(context: Context, title: String, body: String, buttonText: String, iconDrawable: Int) {
            val activity = context as Activity
                AwesomeDialog.build(context)
                        .title(title)
                        .body(body)
                        .icon(iconDrawable)
                        .onPositive(buttonText, buttonBackgroundColor = R.drawable.bg_circle) {

                        }
        }

        fun showAlertErrorDialog(context: Context) {
            val activity = context as Activity
            val title = context.resources.getString(R.string.alert_error_title)
            val body = context.resources.getString(R.string.wrong_msg)
            val buttonText = context.resources.getString(R.string.ok)
            AwesomeDialog.build(context)
                    .title(title)
                    .body(body)
                    .icon(R.drawable.ic_error)
                    .onPositive(buttonText, buttonBackgroundColor = R.drawable.bg_circle) {

                    }
        }
        fun showAuthFailureAlertErrorDialog(context: Context) {
            val activity = context as Activity
            val title = context.resources.getString(R.string.alert_error_title)
            val body = context.resources.getString(R.string.auth_failure)
            val buttonText = context.resources.getString(R.string.ok)
            AwesomeDialog.build(context)
                    .title(title)
                    .body(body)
                    .icon(R.drawable.ic_error)
                    .onPositive(buttonText, buttonBackgroundColor = R.drawable.bg_circle) {
                        clearSharedPrefs(activity)
                        activity.startActivity(Intent(activity, LoginActivity::class.java))
                        activity.finish()
                    }
        }
        private fun clearSharedPrefs(activity: Activity) {
            val editor: SharedPreferences.Editor
            val sharedPref = activity.getSharedPreferences(MyConstants.PREFS_API_GET_TOKEN, Context.MODE_PRIVATE)
            editor = sharedPref.edit()
            editor.clear()
            editor.apply()

            /* sharedPref = mContext.getSharedPreferences(PREFS_API_REGISTER_DEVICE, Context.MODE_PRIVATE);
            editor = sharedPref.edit();
            editor.clear();
            editor.apply();*/
        }
        fun setAnimation(context: Context, type: Int): Animation {
            return AnimationUtils.loadAnimation(context, type)
        }

        // USED ONCE GET OTP BUTTON CLICKED
        fun showSimpleAlertDialog(context: Context, title: String, body: String, buttonText: String, iconDrawable: Int) {
            val activity = context as Activity
            AwesomeDialog.build(context)
                    //.title(title)
                    .body(body)
                    .icon(iconDrawable)
                    .onPositive(buttonText, buttonBackgroundColor = R.drawable.bg_circle) {

                    }
        }
        /* val animationfadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)

         val animationFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)

         val animationZoomIn = AnimationUtils.loadAnimation(this, R.anim.zoom_in)

         val animationZoomOut = AnimationUtils.loadAnimation(this, R.anim.zoom_out)

         val animationSlideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down)

         val animationSlideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)

         val animationBounce = AnimationUtils.loadAnimation(this, R.anim.bounce)

         val animationRotate = AnimationUtils.loadAnimation(this, R.anim.rotate)*/


    }

}