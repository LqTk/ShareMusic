package tk.com.sharemusic;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.ups.JPushUPSManager;
import cn.jpush.android.ups.TokenResult;
import cn.jpush.android.ups.UPSRegisterCallBack;
import tk.com.sharemusic.activity.LoginActivity;
import tk.com.sharemusic.entity.ChatEntity;
import tk.com.sharemusic.entity.User;
import tk.com.sharemusic.myview.dialog.ProgressDialog;
import tk.com.sharemusic.network.BaseResult;
import tk.com.sharemusic.network.HttpMethod;
import tk.com.sharemusic.network.NetWorkService;
import tk.com.sharemusic.network.RxSchedulers;
import tk.com.sharemusic.network.rxjava.BaseObserver;
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
    public static List<AppCompatActivity> activityList = new ArrayList<>();

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

    /**
     * 更新registrationId
     */
    public static void notifyRegistrationId(){
        User user = ShareApplication.getInstance().getConfig().getObject("userInfo", User.class);
        NetWorkService service = HttpMethod.getInstance().create(NetWorkService.class);
        if (user!=null) {
            ShareApplication.setUser(user);
            HashMap map1 = new HashMap();
            map1.put("userId", user.getUserId());
            map1.put("registerId", JPushInterface.getRegistrationID(mContext));
            service.updataRegisterId(map1)
                    .compose(RxSchedulers.compose(mContext))
                    .subscribe(new BaseObserver<BaseResult>() {
                        @Override
                        public void onSuccess(BaseResult baseResult) {

                        }

                        @Override
                        public void onFailed(String msg) {

                        }
                    });
        }
    }


    /**
     * 马赛克图片
     * @param bitmap
     * @param BLOCK_SIZE
     * @return
     */
    public static Bitmap BitmapMosaic(Bitmap bitmap, int BLOCK_SIZE) {

        if (bitmap == null || bitmap.getWidth() == 0 || bitmap.getHeight() == 0
                || bitmap.isRecycled()) {
            return null;
        }
        int mBitmapWidth = bitmap.getWidth();
        int mBitmapHeight = bitmap.getHeight();
        Bitmap mBitmap = Bitmap.createBitmap(mBitmapWidth, mBitmapHeight,
                Bitmap.Config.ARGB_8888);//创建画布
        int row = mBitmapWidth / BLOCK_SIZE;// 获得列的切线
        int col = mBitmapHeight / BLOCK_SIZE;// 获得行的切线
        int[] block = new int[BLOCK_SIZE * BLOCK_SIZE];
        for (int i = 0; i <=row; i++)
        {
            for (int j =0; j <= col; j++)
            {
                int length = block.length;
                int flag = 0;// 是否到边界标志
                if (i == row && j != col) {
                    length = (mBitmapWidth - i * BLOCK_SIZE) * BLOCK_SIZE;
                    if (length == 0) {
                        break;// 边界外已经没有像素
                    }
                    bitmap.getPixels(block, 0, BLOCK_SIZE, i * BLOCK_SIZE, j
                                    * BLOCK_SIZE, mBitmapWidth - i * BLOCK_SIZE,
                            BLOCK_SIZE);

                    flag = 1;
                } else if (i != row && j == col) {
                    length = (mBitmapHeight - j * BLOCK_SIZE) * BLOCK_SIZE;
                    if (length == 0) {
                        break;// 边界外已经没有像素
                    }
                    bitmap.getPixels(block, 0, BLOCK_SIZE, i * BLOCK_SIZE, j
                            * BLOCK_SIZE, BLOCK_SIZE, mBitmapHeight - j
                            * BLOCK_SIZE);
                    flag = 2;
                } else if (i == row && j == col) {
                    length = (mBitmapWidth - i * BLOCK_SIZE)
                            * (mBitmapHeight - j * BLOCK_SIZE);
                    if (length == 0) {
                        break;// 边界外已经没有像素
                    }
                    bitmap.getPixels(block, 0, BLOCK_SIZE, i * BLOCK_SIZE, j
                                    * BLOCK_SIZE, mBitmapWidth - i * BLOCK_SIZE,
                            mBitmapHeight - j * BLOCK_SIZE);

                    flag = 3;
                } else
                {
                    bitmap.getPixels(block, 0, BLOCK_SIZE, i * BLOCK_SIZE, j
                            * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);//取出像素数组
                }

                int r = 0, g = 0, b = 0, a = 0;
                for (int k = 0; k < length; k++) {
                    r += Color.red(block[k]);
                    g += Color.green(block[k]);
                    b += Color.blue(block[k]);
                    a += Color.alpha(block[k]);
                }
                int color = Color.argb(a / length, r / length, g / length, b
                        / length);//求块内所有颜色的平均值
                for (int k = 0; k < length; k++) {
                    block[k] = color;
                }
                if (flag == 1) {
                    mBitmap.setPixels(block, 0, mBitmapWidth - i * BLOCK_SIZE,
                            i * BLOCK_SIZE, j
                                    * BLOCK_SIZE, mBitmapWidth - i * BLOCK_SIZE,
                            BLOCK_SIZE);
                } else if (flag == 2) {
                    mBitmap.setPixels(block, 0, BLOCK_SIZE, i * BLOCK_SIZE, j
                            * BLOCK_SIZE, BLOCK_SIZE, mBitmapHeight - j
                            * BLOCK_SIZE);
                } else if (flag == 3) {
                    mBitmap.setPixels(block, 0, BLOCK_SIZE, i * BLOCK_SIZE, j
                                    * BLOCK_SIZE, mBitmapWidth - i * BLOCK_SIZE,
                            mBitmapHeight - j * BLOCK_SIZE);
                } else {
                    mBitmap.setPixels(block, 0, BLOCK_SIZE, i * BLOCK_SIZE, j
                            * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                }

            }
        }
        //并没有回收传进来的bitmap  原因是JAVA传值默认是引用,如果回收了之后,其他地方用到bitmap的位置可能报NULL指针异常,请根据实际情况决定是否回收.
        return mBitmap;
    }

    /**
     * 随机名称
     */
    private static String adjective[] = {
            "坦诚的", "无私的", "美丽的", "肥头大耳的", "冰清玉洁的", "善解人意的", "开心的", "冰雪聪明的", "可爱的",
            "光鲜艳丽的", "有干劲的", "姗姗来迟的", "憨厚的", "知恩图报的", "自然的", "真正的", "瑟瑟发抖的", "空中的",
            "博古通今的", "诚挚的", "吃胡萝卜的", "成熟的", "精彩的", "欢乐的", "进步的", "真诚的", "黄色的",
            "和谐的", "朴素的", "完美的", "金黄的", "聪明的", "清新的", "迷人的", "光明的", "兢兢业业的",
            "直接的", "真实的", "正气的", "用心的", "飞快的", "雪白的", "着急的", "乐观的", "能说会道的",
            "鲜艳的", "冰冷的", "细心的", "奇妙的", "勤恳的", "动人的", "动人的", "无知的", "礼貌的",
            "温柔的", "爽快的", "正常的", "平淡的", "光亮的", "文质彬彬的", "大方的", "爱吃鱼的", "刻苦的",
            "浪漫的", "专业的", "稳重的", "大气的", "知己的", "刚好的", "相对的", "平和的", "友好的",
            "坚强的", "秀丽的", "锲而不舍的", "口若悬河的", "创造困难的", "笔直的", "安定的", "知足的", "结实的",
            "水里的", "听话的", "知名的", "细致的", "不拘小节的", "仙风道骨的", "喜出望外的", "迷你的", "老实的",
            "友爱的", "貌若潘安的", "活泼可笑的", "端正的", "虚伪的", "卖火柴的", "犹豫的", "不服输的", "妙语连珠的",
            "眉清目秀的", "顽强的", "一表人才的", "多余的", "秀美的", "繁忙的", "冰凉的", "热心的", "婀娜多姿的",
            "冷清的", "公开的", "冷淡的", "天上飞的", "地上跑的", "水里游的", "地球外的", "火星上的", "太阳下的",
            "空心的", "幽默的", "开朗的", "圆溜溜的", "足智多谋的", "明眸皓齿的", "耐心的", "逞强的", "力挽狂澜澜的",
            "严于律己的", "团结的", "中用的", "学贯中西的", "倾国倾城的", "用功的", "语惊四座的", "忙乱的", "执着的",
            "谦让的", "少见的", "临危不惧的", "怕人的", "乡村里的", "幸福的", "特别的", "未来的", "伟大的",
            "城市里的", "伤心的", "实在的", "现实的", "调皮的", "忧愁的", "巨大的", "耐心的", "优秀的",
            "热情似火的", "宽容的", "严厉的", "积极的", "碰瓷的", "飞起来的"};

    private static String[] noun = {
            "狗","猫","恐龙","鸡","大象","蚊子","蛇","蚂蚁","狮子","蜘蛛","蜜蜂",
            "鲸鱼","野生动物","大熊猫","黑猩猩","蝙蝠","猪","鳄鱼","老鼠","老虎","鹦鹉",
            "马","蟑螂","海豚","猴子","鲨鱼","乌龟","牛","章鱼","兔子","苍蝇",
            "蝉","长颈鹿","狼","青蛙","北极熊","蝴蝶","蜥蜴","仓鼠","蜗牛","鸽子",
            "羊","蟒蛇","犀牛","金鱼","麻雀","猫头鹰","萤火虫","乌鸦","变色龙","龙猫",
            "猛犸象","水母","蜻蜓","龙虾","企鹅","蚕","狐狸","猩猩","果蝇","螃蟹",
            "食人鱼","熊","松鼠","蚯蚓","虎鲸","螳螂","蜈蚣","翠鸟","知了","眼镜蛇",
            "斑胸草雀","穿山甲","驯鹿","海蛞蝓","大猩猩","乌贼","骆驼","海马","袋鼠","海龟",
            "刺猬","电鳗","树懒","绵羊","河马","玳瑁","蜂鸟","斑鳖","蝎子","浣熊",
            "豹子","河豚","藏獒","野猪","蟋蟀","燕子","伊犁鼠兔","鸭子","啄木鸟","蓝鲸",
            "鱿鱼","毛虫","冬虫夏草","候鸟","朱鹮","信天翁","麋鹿","帝企鹅","鹦鹉螺","豪猪",
            "棕熊","珊瑚","牦牛","黑脉金斑蝶","阳彩臂金龟","箭毒蛙","鲎","美人鱼","秋刀鱼","金蝉",
            "斑马鱼","蟾蜍","螨虫","牡蛎","军舰鸟","寄生蜂","疣猪","海牛","火烈鸟","锤头鲨",
            "母鸡","鹰","熊猫","信鸽","老鹰","鹿","白犀牛","河狸","鲶鱼","座头鲸",
            "霸王龙","狼蛛","食草动物","海象","蛞蝓","高鼻羚羊","蝾螈","月鱼","孔雀鱼","飞蛾",
            "杜鹃","食骨蠕虫","羚羊","水蛭","帝王蝶","渡渡鸟","露脊鲸","中华锦绣龙虾","板足鲎","鹦鹉鱼",
            "抹香鲸","蜂鸟鹰蛾","墨西哥丽脂鲤","类人猿","鬣狗","马陆","沙猫","熊蜂","鲸鲨","血吸虫",
            "狨猴","砗磲","跳鼠","射水鱼","小当家","樱木花道bai","木du之本樱","小可","水冰月zhi",
            "哆啦A梦","大雄","项少羽dao","天明","石兰","塞巴斯蒂安","亚伦沃克","皮卡丘","鸣人",
            "阿冈","黑崎一护","路飞","索隆","山治","恋次","越前龙马","手冢国光","不二周助","井上",
            "露琪亚","白哉","乔巴","但丁","朋也","凉宫春日","李小狼","纳兹","格雷",
            "孙悟空（龙珠）","L","月","犬夜叉","鲁鲁修","星矢","神乐","我爱罗","Cici","奈叶",
            "小圆","小智","塞巴蒂斯.·米卡艾利斯","上条当麻","鹿丸","六道骸","NANA","冬狮郎","石田雨龙","桐人",
            "亚丝娜","武藤游戏","拓海","明日香","碓冰","雪村千鹤","蔻蔻・海克梅迪亚","娜美","蓝染","流川枫",
            "乌鲁奇奥拉","布鲁克","京乐春水","自来也","迹部","桃城武","尼禄"," 葛力姆乔虎豹杰克 ","乱菊","市丸银",
            "更木剑八","贝吉塔","游城十代","小玉","成龙","老爹","特鲁","圣主","游城十代","黑子","乌索普","弗兰奇",
            "蜜柑","真红","志波海燕","宫城良田","夜天","坂崎由莉","毛利小五郎","比克","乾贞治","留姬","18号",
            "青学王子","小小兔","腾蛇","北斗星司","真王","美铃","红发香克斯","浮竹","神原拓也","平子真子",
            "小黄","诗河","里萝莉","米娅","石田雨龙","草摩绫女","沈欺霜","琉璃仙","青柳立夏","蛮骨",
            "阿拉密斯","憨八龟","织本泉","天津饭","桓远之","建良留姬","金李","思堂","丁次","乃木流架",
            "神乐千鹤","拉达曼提斯","可伶","妖狐藏马","有希子","阿鲁迪巴","玛特","海王满","仁王雅治","凤长太郎",
            "李忆如","芥川慈郎","拉菲尔","陈聿修","本田透","艾利欧","天野银次","美堂蛮","星野琉璃","狃拉",
            "小玲","小茂","马里奥","骗人布","种村有菜","伊芙","仓伎真知","杨戬","华紫音","露娜",
            "本宫大辅","悟天克斯","艾力欧","躯","由贵瑛里","由希","葛琪琪","间桐樱","贵鬼","沙菲雅",
            "迪亚哥","村田健","日渡怜","宫本武藏","玄间","孙悟饭","启介","阿和","钢牙","艾吉",
            "多尔克","和谷","神无","樱庭时央","清田信长","火澄","焰之炼金术师","秋山辽","莎丽","安倍昌浩",
            "北大路五月","虎源太","马利克","夏莉","苏樱","丹羽大助","小泉红子"};

    public static String generateName() {
        int adjLen= adjective.length;
        int nLen= noun.length;
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        sb.append(adjective[random.nextInt(adjLen)]);
        sb.append(noun[random.nextInt(nLen)]);
        return sb.toString();
    }
}
