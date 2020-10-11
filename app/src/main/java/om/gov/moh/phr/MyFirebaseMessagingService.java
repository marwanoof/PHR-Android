package om.gov.moh.phr;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import om.gov.moh.phr.activities.MainActivity;
import om.gov.moh.phr.apimodels.Notification;
import om.gov.moh.phr.models.DBHelper;

import static om.gov.moh.phr.models.MyConstants.API_ANDROID_APP_CODE;
import static om.gov.moh.phr.models.MyConstants.API_ANDROID_FLAG;
import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_MESSAGE;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_PREFS;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_SELECTED;

/**
 * NOTE: There can only be one service in each app that receives FCM messages. If multiple
 * are declared in the Manifest then the first one will be chosen.
 * <p>
 * In order to make this Java sample functional, you must remove the following from the Kotlin messaging
 * service in the AndroidManifest.xml:
 * <p>
 * <intent-filter>
 * <action android:name="com.google.firebase.MESSAGING_EVENT" />
 * </intent-filter>
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private LocalBroadcastManager broadcaster;
    private DBHelper dbHelper;
    int chatCount = 0;
    int appointmentCount = 0;
    int notificationCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        broadcaster = LocalBroadcastManager.getInstance(this);
        dbHelper = new DBHelper(this);
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

/*

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (*/
        /* Check if data needs to be processed by long running job *//*
 true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }

        }
*/


        // Check if message contains a notification payload.

        createChannelToShowNotifications(remoteMessage.getData().get("title"), remoteMessage.getData().get("body"), remoteMessage.getData().get("type"), remoteMessage.getData());
        Log.d(TAG, "Message Notification Body: " + remoteMessage.getData().get("body"));


        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        if (remoteMessage.getData().get("type").equals("2")) {
            Notification notification = new Notification();
            notification.setKeyId(remoteMessage.getData().get("keyId"));
            notification.setTitle(remoteMessage.getData().get("title"));
            notification.setBody(remoteMessage.getData().get("body"));
            notification.setCreatedDate(remoteMessage.getData().get("createdDate"));
            notification.setPnsType(remoteMessage.getData().get("pnsType"));
            notification.setLabType(remoteMessage.getData().get("labType"));
            dbHelper.insertDate(notification);
        }
        handelDataExtras(remoteMessage);
    }
    // [END receive_message]


    // [START on_new_token]

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }
    // [END on_new_token]


    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        Intent intent = new Intent("MyNewToken");
        intent.putExtra("token", token);
        broadcaster.sendBroadcast(intent);
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param title FCM message title received.
     * @param desc  FCM message body received.
     * @param data
     */
    //FCM
    private void createChannelToShowNotifications(String title, String desc, String type, Map<String, String> data) {
        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[]{notifyIntent}, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                        // .setSmallIcon(R.drawable.appointments)
                        .setContentTitle(title)
                        .setContentText(desc)
                        .setAutoCancel(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            builder.setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .setContentIntent(pendingIntent);
        switch (type) {
            case "1":
                builder.setSmallIcon(R.drawable.appointments);
                break;
            case "2":
                builder.setSmallIcon(R.drawable.notification);
                break;
            case "3":
                builder.setSmallIcon(R.drawable.chat);
                sendBroadcast(new Intent(getBody(data.get("body"), data.get("senderId"))));
                break;
        }
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);
        builder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        //notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(
                        getResources().getString(R.string.default_notification_channel_id), getResources().getString(R.string.default_notification_channel_name), importance);
                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(mChannel);
                }
                builder.setChannelId(getResources().getString(R.string.default_notification_channel_id));
            }
        }
        notificationManager.notify(m, builder.build());

        sendBroadcast(new Intent(addBadge(type)));
    }

    //FCM
    private void handelDataExtras(RemoteMessage remoteMessage) {
        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]
        if (remoteMessage != null) {
            for (String key : remoteMessage.getData().keySet()) {
                Object value = remoteMessage.getData().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);

            }
        }
// [END handle_data_extras]
    }

    private String addBadge(String type) {
        SharedPreferences sharedPref = getSharedPreferences("Counting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (type.equals("1")) {

            int updatedAppointmentCount = sharedPref.getInt("appointmentCount", 0);
            if (updatedAppointmentCount != 0)
                appointmentCount = updatedAppointmentCount + 1;
            else
                appointmentCount = appointmentCount + 1;
            editor.putInt("appointmentCount", appointmentCount);
            editor.apply();
        }
        if (type.equals("2")) {
            int updatedNotificationCount = sharedPref.getInt("notificationCount", 0);
            if (updatedNotificationCount != 0)
                notificationCount = updatedNotificationCount + 1;
            else
                notificationCount = notificationCount + 1;

            editor.putInt("notificationCount", notificationCount);
            editor.apply();
        }
        if (type.equals("3")) {
            int updatedChatCount = sharedPref.getInt("chatCount", 0);
            if (updatedChatCount != 0)
                chatCount = updatedChatCount + 1;
            else
                chatCount = chatCount + 1;

            editor.putInt("chatCount", chatCount);
            editor.apply();
        }
        return "TEST";
    }

    private String getBody(String body, String senderID) {
        SharedPreferences sharedPref = getSharedPreferences("CHAT-BODY", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("MESSAGE-BODY", body);
        editor.putString("MESSAGE-SENDER", senderID);
        editor.apply();
        return "BODY";
    }
}