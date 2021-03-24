package om.gov.moh.phr;

import android.content.Intent;
import android.util.Log;

import com.huawei.hms.push.RemoteMessage;

public class HmsMessageService extends com.huawei.hms.push.HmsMessageService {
    private static final String TAG = "PushDemoLog";

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.i(TAG, "receive token:" + token);
        sendTokenToDisplay(token);
    }

    @Override
    public void onTokenError(Exception e) {
        super.onTokenError(e);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
       Log.d("remoteMessage", remoteMessage.getData()+"...");
        if (remoteMessage.getData().length() > 0) {
            Log.i(TAG, "data-payload:" + remoteMessage.getData());
        }
        if (remoteMessage.getNotification() != null) {
            Log.i(TAG, "notification-payload" + remoteMessage.getNotification().getBody());
        }
    }

    @Override
    public void onMessageSent(String s) {
    }

    @Override
    public void onSendError(String s, Exception e) {
    }

    private void sendTokenToDisplay(String token) {
        Intent intent = new Intent("com.huawei.codelabpush.ON_NEW_TOKEN");
        intent.putExtra("token", token);
        sendBroadcast(intent);
    }
}
