package om.gov.moh.phr.models;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import om.gov.moh.phr.R;

public class App extends Application {

    private static final String CHANNEL_ID_CHAT = "Chat";
    private static final String CHANNEL_ID_LAB = "Lab";
    private static final String CHANNEL_ID_APPOINTMENT = "Appointment";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();
            long[] pattern = {500, 500, 500, 500, 500, 500, 500, 500, 500};

            /* channel additional keys

            notificationChannel.setSound(defaultSoundUri,att);
            notificationChannel.setDescription(messageBody);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);

            */

            NotificationChannel chatChannel = new NotificationChannel(CHANNEL_ID_CHAT, getString(R.string.channel_chat), NotificationManager.IMPORTANCE_DEFAULT);
            chatChannel.setDescription(getString(R.string.channel_chat_desc));
            chatChannel.setSound(alarmSound, audioAttributes);
            chatChannel.setVibrationPattern(pattern);
            chatChannel.enableVibration(true);


            NotificationChannel labChannel = new NotificationChannel(CHANNEL_ID_LAB, getString(R.string.channel_lab), NotificationManager.IMPORTANCE_DEFAULT);
            labChannel.setDescription(getString(R.string.channel_lab_desc));
            labChannel.setDescription(getString(R.string.channel_chat_desc));
            labChannel.setSound(alarmSound, audioAttributes);
            labChannel.setVibrationPattern(pattern);
            labChannel.enableVibration(true);

            NotificationChannel appointmentChannel = new NotificationChannel(CHANNEL_ID_APPOINTMENT, getString(R.string.channel_appointment), NotificationManager.IMPORTANCE_HIGH);
            appointmentChannel.setDescription(getString(R.string.channel_appointment_desc));
            appointmentChannel.setDescription(getString(R.string.channel_chat_desc));
            appointmentChannel.setSound(alarmSound, audioAttributes);
            appointmentChannel.setVibrationPattern(pattern);
            appointmentChannel.enableVibration(true);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(chatChannel);
            notificationManager.createNotificationChannel(labChannel);
            notificationManager.createNotificationChannel(appointmentChannel);

        }
    }
}
