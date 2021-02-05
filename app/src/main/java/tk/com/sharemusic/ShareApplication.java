package tk.com.sharemusic;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.danikula.videocache.HttpProxyCacheServer;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.ups.JPushUPSManager;
import cn.jpush.android.ups.TokenResult;
import cn.jpush.android.ups.UPSRegisterCallBack;
import tk.com.sharemusic.entity.ChatEntity;
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
    public static boolean multiSending = false;
    private PreferenceConfig mCurrentConfig;
    private static ShareApplication mContext;
    private static List<AppCompatActivity> activityList = new ArrayList<>();

    /**
     * 定位信息
     */
    public static boolean showLocation = false;
    public static String locationStr = "";
    public static double latitude;
    public static double longitude;
    public static String cityCode;
    public static String city;

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
        initLocation();
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
                        .openGallery(PictureMimeType.ofAll())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                        .isWithVideoImage(true)
                        .maxVideoSelectNum(1)
                        .isGif(true)
                        .loadImageEngine(GlideEngine.createGlideEngine())
//                .theme()//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
                        .maxSelectNum(1)// 最大图片选择数量 int
                        .minSelectNum(1)// 最小选择数量 int
                        .imageSpanCount(3)// 每行显示个数 int
                        .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                        .isCamera(true)// 是否显示拍照按钮 true or false
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

    /**
     * 拍视频
     * @param activity
     * @param isCrop 是否可以裁剪
     */
    public static void openTakePhoto(Activity activity, boolean isCrop){
        PictureSelector.create(activity)
                .openCamera(PictureMimeType.ofVideo())
                .enableCrop(isCrop)
                .videoQuality(1)// 视频录制质量 0 or 1 int
                .videoMaxSecond(20)// 显示多少秒以内的视频or音频也可适用 int
                //.videoMinSecond(10)// 显示多少秒以内的视频or音频也可适用 int
                .recordVideoSecond(20)//视频秒数录制 默认60s int
                .previewVideo(true)//是否预览视频
                .freeStyleCropEnabled(true)//是否可播放音频 true or false
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    /**
     * 相册选择照片
     * @param activity
     * @param count 可选择数量
     * @param isCrop 是否裁剪
     */
    public static void openAlbumSelect(Activity activity,int count, boolean isCrop){
        PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofAll())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .isWithVideoImage(true)
                .maxVideoSelectNum(count)
                .isGif(true)
                .loadImageEngine(GlideEngine.createGlideEngine())
//                .theme()//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
                .maxSelectNum(count)// 最大图片选择数量 int
//                .minSelectNum(1)// 最小选择数量 int
                .imageSpanCount(3)// 每行显示个数 int
                .selectionMode(count>1?PictureConfig.MULTIPLE:PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .isCamera(true)// 是否显示拍照按钮 true or false
                .enableCrop(isCrop)
                .isDragFrame(true)// 是否可拖动裁剪框
                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                .withAspectRatio(1,1)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .circleDimmedLayer(false)// 是否圆形裁剪 true or false
                .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    /**
     * activity管理
     * @param appCompatActivity
     */
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

    /**
     * 判断权限是否允许
     * @param permissions
     * @param context
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean isGrantPermission(String[] permissions, Context context){
        for (String permission:permissions){
            if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    private HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy(Context context) {
        ShareApplication app = (ShareApplication) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
                .maxCacheSize(1024 * 1024 * 1024)       // 1 Gb for cache
                .build();
    }

    /**
     * 计算两点之间的距离
     */
    private static double EARTH_RADIUS = 6378.137;//地球半径

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 计算两个经纬度之间的距离
     *
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return km
     */
    public static String getDistance(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        //s单位km，如果需要其他单位请记得换算
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        if (s>1) {
            DecimalFormat df = new DecimalFormat("#.0");
            s = Double.parseDouble(df.format(s));
            return s+"km";
        }
        double dis = s*1000;//米
        if (dis>100){
            return dis%1000+"米";
        }

        return "<100米";
    }

    /**
     * 定位
     */

    //定位
    //声明AMapLocationClient类对象
    public static AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    private static AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    ShareApplication.showLocation = true;
                    //可在其中解析amapLocation获取相应内容。
                    ShareApplication.locationStr = aMapLocation.getProvince()+aMapLocation.getCity()+aMapLocation.getDistrict()+aMapLocation.getStreet();
                    ShareApplication.latitude = aMapLocation.getLatitude();//获取纬度
                    ShareApplication.longitude = aMapLocation.getLongitude();//获取经度
                    ShareApplication.cityCode = aMapLocation.getCityCode();
                    if (!TextUtils.isEmpty(aMapLocation.getPoiName())){
                        ShareApplication.city = aMapLocation.getPoiName().trim();
                    }else {
                        ShareApplication.city = aMapLocation.getAoiName().trim();
                    }
                    mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
                }else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError","location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }
        }
    };

    //声明AMapLocationClientOption对象
    public static AMapLocationClientOption mLocationOption = null;

    public static void initLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(mContext);
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);

        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(20000);
        //关闭缓存机制
        mLocationOption.setLocationCacheEnable(false);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }
}
