package tk.com.sharemusic;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;

import javax.net.ssl.HttpsURLConnection;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.ups.JPushUPSManager;
import cn.jpush.android.ups.TokenResult;
import cn.jpush.android.ups.UPSRegisterCallBack;
import tk.com.sharemusic.entity.User;
import tk.com.sharemusic.utils.NetworkStatusManager;
import tk.com.sharemusic.utils.SSLSocketFactoryUtils;

public class ShareApplication extends Application {

    private static User user;

    @Override
    public void onCreate() {
        super.onCreate();
        NetworkStatusManager.init(this);
        if (!isNotificationEnabled(this)) {
            gotoSet();//去设置开启通知
        }
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        String registrationID = JPushInterface.getRegistrationID(this);
        Log.d("registtationId==",registrationID);
        /*JPushUPSManager.registerToken(getApplicationContext(), "46e1c21ea828ba30dd8e951e", null, null, new UPSRegisterCallBack() {
            @Override
            public void onResult(TokenResult tokenResult) {
                Log.d("pushSReceiver","注册状态"+tokenResult.getReturnCode()+",string=="+tokenResult.toString());
            }
        });*/
        trustAllSSL();
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        ShareApplication.user = user;
    }

    private void trustAllSSL(){
        HttpsURLConnection.setDefaultSSLSocketFactory(SSLSocketFactoryUtils.createSSLSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(new SSLSocketFactoryUtils.TrustAllHostnameVerifier());
    }

    private boolean isNotificationEnabled(Context context) {
        boolean isOpened = false;
        try {
            isOpened = NotificationManagerCompat.from(context).areNotificationsEnabled();
        } catch (Exception e) {
            e.printStackTrace();
            isOpened = false;
        }
        return isOpened;

    }

    private void gotoSet() {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= 26) {
            // android 8.0引导
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());
        } else if (Build.VERSION.SDK_INT >= 21) {
            // android 5.0-7.0
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", getPackageName());
            intent.putExtra("app_uid", getApplicationInfo().uid);
        } else {
            // 其他
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", getPackageName(), null));
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


}
