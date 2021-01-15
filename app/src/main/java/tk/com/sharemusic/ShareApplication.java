package tk.com.sharemusic;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.ups.JPushUPSManager;
import cn.jpush.android.ups.TokenResult;
import cn.jpush.android.ups.UPSRegisterCallBack;
import tk.com.sharemusic.entity.User;
import tk.com.sharemusic.myview.dialog.ProgressDialog;
import tk.com.sharemusic.utils.GlideEngine;
import tk.com.sharemusic.utils.NetworkStatusManager;
import tk.com.sharemusic.utils.PreferenceConfig;
import tk.com.sharemusic.utils.SSLSocketFactoryUtils;

public class ShareApplication extends Application {

    public static int showCount = 3;
    public static User user;
    public static final int ACTION_TYPE_ALBUM = 0;
    public static final int ACTION_TYPE_PHOTO = 1;
    private PreferenceConfig mCurrentConfig;
    private static ShareApplication mContext;
    private static List<AppCompatActivity> activityList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
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

    public static ShareApplication getInstance(){
        return mContext;
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        ShareApplication.user = user;
    }


    public PreferenceConfig getConfig() {
        mCurrentConfig = PreferenceConfig.getPreferenceConfig(this);
        if (!mCurrentConfig.isLoadConfig()) {
            mCurrentConfig.loadConfig();
        }
        return mCurrentConfig;
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

    /**
     * 通知栏设置
     */
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

    /**
     * 选择图片/拍照
     *
     * @param position
     */
    public static void goToSelectPicture(int position, Activity context, boolean isCrop) {
        switch (position) {
            case ACTION_TYPE_PHOTO:
                PictureSelector.create(context)
                        .openCamera(PictureMimeType.ofImage())
                        .enableCrop(isCrop)
                        .isDragFrame(true)// 是否可拖动裁剪框
                        .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                        .withAspectRatio(1,1)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                        .circleDimmedLayer(false)// 是否圆形裁剪 true or false
                        .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                        .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                        .forResult(PictureConfig.CHOOSE_REQUEST);
                break;
            case ACTION_TYPE_ALBUM:
                PictureSelector.create(context)
                        .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                        .loadImageEngine(GlideEngine.createGlideEngine())
//                .theme()//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
                        .maxSelectNum(1)// 最大图片选择数量 int
                        .minSelectNum(1)// 最小选择数量 int
                        .imageSpanCount(3)// 每行显示个数 int
                        .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                        .isCamera(false)// 是否显示拍照按钮 true or false
                        .enableCrop(isCrop)
                        .isDragFrame(true)// 是否可拖动裁剪框
                        .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                        .withAspectRatio(1,1)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                        .circleDimmedLayer(false)// 是否圆形裁剪 true or false
                        .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                        .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                        .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
                break;
            default:
                break;
        }
    }

    public static void addActivity(AppCompatActivity appCompatActivity){
        activityList.add(appCompatActivity);
    }

    public static void removeActivity(AppCompatActivity appCompatActivity){
        if (activityList.contains(appCompatActivity)){
            activityList.remove(appCompatActivity);
        }
    }

    public static void clearActivity(){
        for (AppCompatActivity activity:activityList){
            activity.finish();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean isGrantPermission(String[] permissions, Context context){
        for (String permission:permissions){
            if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }
}
