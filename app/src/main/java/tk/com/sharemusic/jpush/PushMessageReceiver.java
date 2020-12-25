package tk.com.sharemusic.jpush;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;

public class PushMessageReceiver extends JPushMessageReceiver {
    @Override
    public void onMessage(Context context, CustomMessage customMessage) {
        super.onMessage(context, customMessage);
        Log.d("pushSReceiver","onMessage message=="+customMessage);
    }

    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage notificationMessage) {
        super.onNotifyMessageOpened(context, notificationMessage);
        Log.d("pushSReceiver","onNotifyMessageOpened message=="+notificationMessage);
    }

    @Override
    public void onNotifyMessageArrived(Context context, NotificationMessage notificationMessage) {
        super.onNotifyMessageArrived(context, notificationMessage);
        Log.d("pushSReceiver","onNotifyMessageArrived message=="+notificationMessage);
    }

    @Override
    public void onMultiActionClicked(Context context, Intent intent) {
        super.onMultiActionClicked(context, intent);
        Log.d("pushSReceiver","onMultiActionClicked message==点击了通知栏");
    }
}
