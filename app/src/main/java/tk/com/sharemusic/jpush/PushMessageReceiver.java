package tk.com.sharemusic.jpush;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;
import tk.com.sharemusic.event.LogoutEvent;
import tk.com.sharemusic.event.NewReviewEvent;
import tk.com.sharemusic.event.RefreshChatListEvent;
import tk.com.sharemusic.event.RefreshPartnerMsgEvent;

public class PushMessageReceiver extends JPushMessageReceiver {
    @Override
    public void onMessage(Context context, CustomMessage customMessage) {
        super.onMessage(context, customMessage);
        Log.d("pushSReceiver","onMessage message=="+customMessage+",extra = "+customMessage.extra);
        if (customMessage.message.equals("新消息")){
            String talkId = "";
            try {
                JSONObject object = new JSONObject(customMessage.extra);
                talkId = (String)object.get("talkId");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            EventBus.getDefault().post(new RefreshChatListEvent(talkId));
            EventBus.getDefault().post(new RefreshPartnerMsgEvent());
        }else if (customMessage.message.equals("新评论")){
            EventBus.getDefault().post(new NewReviewEvent());
        }else if (customMessage.message.equals("掉线")){
            EventBus.getDefault().post(new LogoutEvent());
        }
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
