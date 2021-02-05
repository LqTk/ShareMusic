package tk.com.sharemusic.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bumptech.glide.Glide;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import tk.com.sharemusic.R;
import tk.com.sharemusic.ShareApplication;
import tk.com.sharemusic.adapter.ShareGvAdapter;
import tk.com.sharemusic.config.Constants;
import tk.com.sharemusic.entity.ShareGvEntity;
import tk.com.sharemusic.entity.SocialPublicEntity;
import tk.com.sharemusic.entity.User;
import tk.com.sharemusic.event.UpLoadSocialSuccess;
import tk.com.sharemusic.myview.MyGridView;
import tk.com.sharemusic.myview.dialog.BottomChoose;
import tk.com.sharemusic.myview.dialog.VideoPreviewDialog;
import tk.com.sharemusic.network.HttpMethod;
import tk.com.sharemusic.network.NetWorkService;
import tk.com.sharemusic.network.RxSchedulers;
import tk.com.sharemusic.network.response.GetPublicDataShareIdVo;
import tk.com.sharemusic.network.response.UpLoadFileVo;
import tk.com.sharemusic.network.rxjava.BaseObserver;
import tk.com.sharemusic.utils.GlideEngine;
import tk.com.sharemusic.utils.PopWinUtil;
import tk.com.sharemusic.utils.ToastUtil;

public class ShareActivity extends CommonActivity {

    @BindView(R.id.et_context)
    EditText etContext;
    @BindView(R.id.btn_share)
    TextView btnShare;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.rl_head)
    RelativeLayout rlHead;
    @BindView(R.id.gv_share)
    MyGridView gvShare;
    @BindView(R.id.iv_location)
    ImageView ivLocation;
    @BindView(R.id.rl_location)
    RelativeLayout rlLocation;
    @BindView(R.id.iv_people)
    ImageView ivPeople;
    @BindView(R.id.rl_people)
    RelativeLayout rlPeople;
    @BindView(R.id.iv_video)
    ImageView ivVideo;
    @BindView(R.id.iv_delete)
    ImageView ivDelete;
    @BindView(R.id.iv_play)
    ImageView ivPlay;
    @BindView(R.id.rl_video)
    RelativeLayout rlVideo;
    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.tv_people_type)
    TextView tvPeopleType;
    @BindView(R.id.tv_people_see)
    TextView tvPeopleSee;

    private NetWorkService service;
    private String shareType = Constants.SHARE_MUSIC;
    private List<ShareGvEntity> shareLists = new ArrayList<>();
    private List<ShareGvEntity> upLoadPicLists = new ArrayList<>();
    private Context mContext;
    private ShareGvAdapter adapter;
    private PopWinUtil uiPopWinUtil;
    private boolean isHaveAdd = true;//图片选择加号是否显示
    private BottomChoose bottomChoose;
    private boolean videoLoad = false;//视频是否加载成功
    private String videoPath = "";//视频地址
    private int publishType = Constants.PEOPLE_ALL;
    private boolean showLocation = ShareApplication.showLocation;
    private double latitude;
    private double longitude;
    private String city="";
    private String locationStr;
    private String shareUrl="";

    //定位
    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    private AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    showLocation = true;
                    //可在其中解析amapLocation获取相应内容。
                    locationStr = aMapLocation.getProvince()+aMapLocation.getCity()+aMapLocation.getDistrict()+aMapLocation.getStreet();
                    latitude = aMapLocation.getLatitude();//获取纬度
                    longitude = aMapLocation.getLongitude();//获取经度
                    tvLocation.setTextColor(getResources().getColor(R.color.greenBg));
                    ShareApplication.cityCode = aMapLocation.getCityCode();
                    if (!TextUtils.isEmpty(aMapLocation.getPoiName())){
                        tvLocation.setText(aMapLocation.getPoiName().trim());
                        city = aMapLocation.getPoiName().trim();
                    }else {
                        tvLocation.setText(aMapLocation.getAoiName().trim());//获取当前定位点的AOI信息
                        city = aMapLocation.getAoiName().trim();
                    }
                    ivLocation.setImageResource(R.drawable.location_ok);

                    /*aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    aMapLocation.getAccuracy();//获取精度信息
                    aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                    aMapLocation.getCountry();//国家信息
                    aMapLocation.getProvince();//省信息
                    aMapLocation.getCity();//城市信息
                    aMapLocation.getDistrict();//城区信息
                    aMapLocation.getStreet();//街道信息
                    aMapLocation.getStreetNum();//街道门牌号信息
                    aMapLocation.getCityCode();//城市编码
                    aMapLocation.getAdCode();//地区编码
                    aMapLocation.getBuildingId();//获取当前室内定位的建筑物Id
                    aMapLocation.getFloor();//获取当前室内定位的楼层
                    aMapLocation.getGpsAccuracyStatus();//获取GPS的当前状态
                    //获取定位时间
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(aMapLocation.getTime());
                    df.format(date);*/

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
    public AMapLocationClientOption mLocationOption = null;
    private Unbinder bind;
    private static final int TYPECODE = 234;//跳转选择发布类型activity
    private String shareContent="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        bind = ButterKnife.bind(this);
        mContext = this;
        service = HttpMethod.getInstance().create(NetWorkService.class);
        uiPopWinUtil = new PopWinUtil(this);
        uiPopWinUtil.setShade(true);
        initData();
        if (!ShareApplication.showLocation) {
            initLocation();
        }else {
            locationStr = ShareApplication.locationStr;
            latitude = ShareApplication.latitude;//获取纬度
            longitude = ShareApplication.longitude;//获取经度
            tvLocation.setTextColor(getResources().getColor(R.color.greenBg));
            city = ShareApplication.city;
            tvLocation.setText(city);
            ivLocation.setImageResource(R.drawable.location_ok);
        }
        initBottomChoose();
        initView();
    }

    private void initLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
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
     * 初始化底部弹框
     */
    private void initBottomChoose() {
        bottomChoose = new BottomChoose(mContext);
        bottomChoose.setClickListener(new BottomChoose.ClickListener() {
            @Override
            public void album() {
                if (shareType.equals(Constants.SHARE_PIC)) {
                    choosePic();
                } else if (shareType.equals(Constants.SHARE_VIDEO)) {
                    chooseVideo();
                }
            }

            @Override
            public void camera() {
                if (shareType.equals(Constants.SHARE_PIC)) {
                    openCameraPic();
                } else if (shareType.equals(Constants.SHARE_VIDEO)) {
                    openCameraVideo();
                }
            }
        });
        bottomChoose.setUiHandle(uiPopWinUtil);
    }

    /**
     * 初始化View
     */
    private void initView() {
        adapter = new ShareGvAdapter(shareLists, this);
        gvShare.setAdapter(adapter);
        adapter.setItemChildClickListener(new ShareGvAdapter.OnItemChildClickListener() {
            @Override
            public void onItemClick(View view, int pos, Object obj) {
                switch (view.getId()) {
                    case R.id.iv_delete:
                        shareLists.remove(pos);
                        adapter.notifyDataSetChanged();
                        if (!isHaveAdd) {
                            shareLists.add(0, new ShareGvEntity(R.drawable.add_pic + "", "pic"));
                            isHaveAdd = true;
                        }
                        if (shareLists.size() > 1) {
                            setEnable(true);
                        } else {
                            setEnable(false);
                        }
                        break;
                }
            }
        });
        gvShare.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShareGvEntity entity = shareLists.get(position);
                if (entity.type.equals("pic")) {
                    uiPopWinUtil.showPopupBottom(bottomChoose.getView(), R.id.ll_share_content);
                }
            }
        });

        switch (shareType) {
            case Constants.SHARE_MUSIC:
                gvShare.setVisibility(View.GONE);
                rlVideo.setVisibility(View.GONE);
                break;
            case Constants.SHARE_VIDEO:
                gvShare.setVisibility(View.GONE);
                rlVideo.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!ShareApplication.isGrantPermission(Constants.PERMISSIONS, mContext)) {
                        ToastUtil.showShortMessage(mContext, "权限不足");
                        requestPermissions(Constants.PERMISSIONS, Constants.PERMISSION_REQUEST_CODE);
                    } else {
                        chooseVideo();
                    }
                } else {
                    chooseVideo();
                }
                break;
            case Constants.SHARE_PIC:
                gvShare.setVisibility(View.VISIBLE);
                rlVideo.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!ShareApplication.isGrantPermission(Constants.PERMISSIONS, mContext)) {
                        ToastUtil.showShortMessage(mContext, "权限不足");
                        requestPermissions(Constants.PERMISSIONS, Constants.PERMISSION_REQUEST_CODE);
                    } else {
                        choosePic();
                    }
                } else {
                    choosePic();
                }
                break;
            case Constants.SHARE_TEXT:
                gvShare.setVisibility(View.GONE);
                rlVideo.setVisibility(View.GONE);
                break;
            default:
                break;
        }

        etContext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                shareContent = s.toString();
                if (shareType.equals(Constants.SHARE_TEXT)) {
                    String str = etContext.getText().toString();
                    setEnable(!TextUtils.isEmpty(str));
                }
            }
        });
    }

    /**
     * 设置发布是否可点击
     */
    private void setEnable(boolean enable) {
        if (enable) {
            btnShare.setEnabled(true);
            btnShare.setBackground(getResources().getDrawable(R.drawable.green_bg));
            btnShare.setTextColor(getResources().getColor(R.color.white));
        } else {
            btnShare.setEnabled(false);
            btnShare.setBackground(getResources().getDrawable(R.drawable.grey_bg));
            btnShare.setTextColor(getResources().getColor(R.color.textColorGrey));
        }
    }

    /**
     * 初始化数据类型
     */
    private void initData() {
        String sharetext = getIntent().getStringExtra("sharetext");
        shareType = getIntent().getStringExtra("shareType");
        if (!TextUtils.isEmpty(sharetext)) {
            etContext.setText(sharetext);
        }
        shareLists.add(new ShareGvEntity(R.drawable.add_pic + "", "pic"));
        isHaveAdd = true;
    }

    public void musicShare() {
        String string = etContext.getText().toString();
        if (TextUtils.isEmpty(string)) {
            return;
        }
        String title = string.substring(string.indexOf("《") + 1, string.indexOf("》"));
        ArrayList containedUrls = new ArrayList();
        String urlRegex = "((https?|ftp|gopher|telnet|file|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
//                    pattern = Pattern.compile("^(https?|ftp|file|http)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",Pattern.CASE_INSENSITIVE);
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(string);
        while (matcher.find()) {
            containedUrls.add(string.substring(matcher.start(0),
                    matcher.end(0)));
        }
        String url = containedUrls.get(0).toString();
        ToastUtil.showShortMessage(this, url);
//                    web.loadUrl(url);
        User user = ShareApplication.getUser();
        if (user == null) {
            ToastUtil.showShortMessage(this, "分享失败");
            return;
        }
        SocialPublicEntity socialPublicEntity = new SocialPublicEntity(user.getUserId(), user.getUserName(),
                user.getHeadImg(), user.getSex(), title, url, string, publishType, shareType,
                showLocation?city:"", showLocation?latitude:null, showLocation?longitude:null, new ArrayList<>(), new ArrayList<>());
        upLoadSocialPublid(socialPublicEntity);
    }


    private void upLoadSocialPublid(SocialPublicEntity entity) {
        if (entity == null)
            return;
        service.pulishPublic(entity)
                .compose(RxSchedulers.<GetPublicDataShareIdVo>compose(this))
                .subscribe(new BaseObserver<GetPublicDataShareIdVo>() {
                    @Override
                    public void onSuccess(GetPublicDataShareIdVo getPublicDataShareIdVo) {
                        shareUrl = "";
                        if (getPublicDataShareIdVo.getData() != null) {
                            EventBus.getDefault().post(new UpLoadSocialSuccess(getPublicDataShareIdVo.getData()));
                            ShareActivity.this.finish();
                        } else {
                            ToastUtil.showShortMessage(ShareActivity.this, "分享失败");
                        }
                    }

                    @Override
                    public void onFailed(String msg) {
                        shareUrl = "";
                        ToastUtil.showShortMessage(ShareActivity.this, msg);
                    }
                });
    }

    @OnClick({R.id.iv_back, R.id.rl_location, R.id.rl_people, R.id.btn_share, R.id.iv_delete, R.id.rl_video})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                ShareActivity.this.finish();
                break;
            case R.id.rl_location:
                Intent locationIntent = new Intent(ShareActivity.this,LocationActivity.class);
                locationIntent.putExtra("locationed",showLocation);
                locationIntent.putExtra("city",tvLocation.getText().toString());
                locationIntent.putExtra("address",locationStr);
                locationIntent.putExtra("latitude",latitude);
                locationIntent.putExtra("longitude",longitude);
                startActivityForResult(locationIntent,Constants.LocationRequestCode);
                break;
            case R.id.rl_people:
                Intent peopleIntent = new Intent(ShareActivity.this,PublishTypeActivity.class);
                peopleIntent.putExtra("type",tvPeopleType.getText().toString());
                startActivityForResult(peopleIntent,TYPECODE);
                break;
            case R.id.btn_share:
                if (shareType.equals(Constants.SHARE_MUSIC)) {
                    musicShare();
                } else if (shareType.equals(Constants.SHARE_TEXT)) {
                    SocialPublicEntity socialPublicEntity = new SocialPublicEntity(ShareApplication.user.getUserId(),
                            ShareApplication.user.getUserName(),ShareApplication.user.getHeadImg(),
                            ShareApplication.user.getSex(), "", "", shareContent,
                            publishType, shareType, showLocation?city:"", showLocation?latitude:null, showLocation?longitude:null,new ArrayList<>(), new ArrayList<>());
                    upLoadSocialPublid(socialPublicEntity);
                } else if (shareType.equals(Constants.SHARE_VIDEO)) {
                    upLoadFile(videoPath);
                } else if (shareType.equals(Constants.SHARE_PIC)) {
                    upLoadPicLists.clear();
                    for (ShareGvEntity entity:shareLists){
                        if (entity.type.equals("file")){
                            upLoadPicLists.add(entity);
                        }
                    }
                    upLoadFile(upLoadPicLists.get(0).path);
                }
                ShareActivity.this.finish();
                break;
            case R.id.iv_delete:
                if (videoLoad) {
                    videoPath = "";
                    videoLoad = false;
                    ivPlay.setVisibility(View.GONE);
                    ivDelete.setVisibility(View.GONE);
                    Glide.with(mContext)
                            .load(R.drawable.add_pic)
                            .apply(Constants.picLoadOptions)
                            .into(ivVideo);
                    setEnable(false);
                }
                break;
            case R.id.rl_video:
                if (videoLoad) {
                    if (!TextUtils.isEmpty(videoPath)) {
                        File file1 = new File(videoPath);
                        if (file1.exists()) {
                            VideoPreviewDialog dialog2 = new VideoPreviewDialog(mContext);
                            dialog2.setLocalVideo(videoPath);
                            dialog2.show();
                        } else {
                            ToastUtil.showShortMessage(mContext, "视频加载失败，请检查视频是否被删除");
                        }
                    } else {
                        ToastUtil.showShortMessage(mContext, "视频加载失败");
                    }
                } else {
                    uiPopWinUtil.showPopupBottom(bottomChoose.getView(), R.id.ll_share_content);
                }
                break;
        }
    }

    /**
     * 上传文件
     */
    private void upLoadFile(String path) {
        if (TextUtils.isEmpty(path))
            return;
        MultipartBody.Part part = null;
        part = getFilePart(path);
        service.upLoadFile(part)
                .compose(RxSchedulers.compose(mContext))
                .subscribe(new BaseObserver<UpLoadFileVo>() {
                    @Override
                    public void onSuccess(UpLoadFileVo upLoadFileVo) {
                        if (shareType.equals(Constants.SHARE_VIDEO)){
                            shareUrl = (String) upLoadFileVo.getData().get("url");
                            SocialPublicEntity socialPublicEntity = new SocialPublicEntity(ShareApplication.user.getUserId(),
                                    ShareApplication.user.getUserName(),ShareApplication.user.getHeadImg(),
                                    ShareApplication.user.getSex(), "", shareUrl, shareContent,
                                    publishType, shareType, showLocation?city:"", showLocation?latitude:null, showLocation?longitude:null,new ArrayList<>(), new ArrayList<>());
                            upLoadSocialPublid(socialPublicEntity);
                        }else if (shareType.equals(Constants.SHARE_PIC)){
                            shareUrl = shareUrl+(String) upLoadFileVo.getData().get("url")+";";
                            upLoadPicLists.remove(0);
                            if (upLoadPicLists.size()>0){
                                upLoadFile(upLoadPicLists.get(0).path);
                            }else {
                                SocialPublicEntity socialPublicEntity = new SocialPublicEntity(ShareApplication.user.getUserId(),
                                        ShareApplication.user.getUserName(),ShareApplication.user.getHeadImg(),
                                        ShareApplication.user.getSex(), "", shareUrl, shareContent,
                                        publishType, shareType, showLocation?city:"", showLocation?latitude:null, showLocation?longitude:null,new ArrayList<>(), new ArrayList<>());
                                upLoadSocialPublid(socialPublicEntity);
                            }
                        }
                    }

                    @Override
                    public void onFailed(String msg) {
                        ToastUtil.showShortMessage(mContext,msg);
                    }
                });
    }

    private MultipartBody.Part getFilePart(String path) {
        File file = new File(path);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        return part;
    }


    /**
     * 选择图片
     */
    private void choosePic() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .isGif(true)
                .loadImageEngine(GlideEngine.createGlideEngine())
//                .theme()//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
                .maxSelectNum(9 - getSelectCount())// 最大图片选择数量 int
//                .minSelectNum(1)// 最小选择数量 int
                .imageSpanCount(3)// 每行显示个数 int
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
//                .isCamera(true)// 是否显示拍照按钮 true or false
                .isDragFrame(true)// 是否可拖动裁剪框
                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                .withAspectRatio(1, 1)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .circleDimmedLayer(false)// 是否圆形裁剪 true or false
                .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    /**
     * 选择图片数量
     */
    private int getSelectCount() {
        int count = 0;
        for (ShareGvEntity entity : shareLists) {
            if (entity.type.equals("file")) {
                count++;
            }
        }
        return count;
    }

    /**
     * 选择视频
     */
    private void chooseVideo() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofVideo())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .isWithVideoImage(true)
                .isGif(true)
                .loadImageEngine(GlideEngine.createGlideEngine())
//                .theme()//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
                .maxSelectNum(1)// 最大图片选择数量 int
//                .minSelectNum(1)// 最小选择数量 int
                .imageSpanCount(3)// 每行显示个数 int
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .isCamera(true)// 是否显示拍照按钮 true or false
                .isDragFrame(true)// 是否可拖动裁剪框
                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                .withAspectRatio(1, 1)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .circleDimmedLayer(false)// 是否圆形裁剪 true or false
                .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    /**
     * 选择拍照
     */
    private void openCameraPic() {
        PictureSelector.create(this)
                .openCamera(PictureMimeType.ofImage())
                .enableCrop(true)
                .videoQuality(1)// 视频录制质量 0 or 1 int
                .videoMaxSecond(20)// 显示多少秒以内的视频or音频也可适用 int
                //.videoMinSecond(10)// 显示多少秒以内的视频or音频也可适用 int
                .recordVideoSecond(20)//视频秒数录制 默认60s int
                .previewVideo(true)//是否预览视频
                .freeStyleCropEnabled(true)//是否可播放音频 true or false
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    /**
     * 选择拍摄视频
     */
    private void openCameraVideo() {
        PictureSelector.create(this)
                .openCamera(PictureMimeType.ofVideo())
                .enableCrop(true)
                .videoQuality(1)// 视频录制质量 0 or 1 int
                .videoMaxSecond(20)// 显示多少秒以内的视频or音频也可适用 int
                //.videoMinSecond(10)// 显示多少秒以内的视频or音频也可适用 int
                .recordVideoSecond(20)//视频秒数录制 默认60s int
                .previewVideo(true)//是否预览视频
                .freeStyleCropEnabled(true)//是否可播放音频 true or false
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    /**
     * 图片选择返回结果和权限请求结果返回
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @SuppressLint("MissingSuperCall")
    @Override
    public void onActivityResult(final int requestCode, final int resultCode,
                                 final Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;
        switch (requestCode) {
            case PictureConfig.CHOOSE_REQUEST:
                // 图片、视频、音频选择结果回调
                List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                if (selectList != null && selectList.size() > 0) {
                    setEnable(true);
                }
                // 例如 LocalMedia 里面返回三种path
                // 1.media.getPath(); 为原图path
                // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                if (shareType.equals(Constants.SHARE_PIC)) {
                    sendMultiPic(selectList);
                } else if (shareType.equals(Constants.SHARE_VIDEO)) {
                    loadVideoData(selectList);
                }
                break;
            case Constants.PERMISSION_REQUEST_CODE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!ShareApplication.isGrantPermission(Constants.PERMISSIONSCAMERA, mContext)) {
                        ToastUtil.showShortMessage(mContext, "权限不足");
                    } else {
                        switch (shareType) {
                            case Constants.SHARE_PIC:
                                openCameraPic();
                                break;
                            case Constants.SHARE_VIDEO:
                                openCameraVideo();
                                break;
                        }
                    }
                }
                break;
            case Constants.LocationRequestCode:
                if (data!=null){
                    Bundle bundle = data.getBundleExtra("data");
                    city = bundle.getString("city");
                    locationStr = bundle.getString("address");
                    latitude = bundle.getDouble("latitude");
                    longitude = bundle.getDouble("longitude");
                    if (city.equals("不显示位置")){
                        showLocation = false;
                        tvLocation.setTextColor(getResources().getColor(R.color.textColorBlack));
                        tvLocation.setText(city);
                        ivLocation.setImageResource(R.drawable.location_no);
                    }else {
                        showLocation = true;
                        tvLocation.setTextColor(getResources().getColor(R.color.greenBg));
                        tvLocation.setText(city);
                        ivLocation.setImageResource(R.drawable.location_ok);
                    }
                }
                break;
            case TYPECODE:
                if (data!=null){
                    Bundle bundle = data.getBundleExtra("data");
                    String type = bundle.getString("type");
                    switch (type){
                        case "公开":
                            ivPeople.setImageResource(R.drawable.people);
                            tvPeopleSee.setTextColor(getResources().getColor(R.color.textColorBlack));
                            tvPeopleType.setTextColor(getResources().getColor(R.color.textColorGrey));
                            publishType = Constants.PEOPLE_ALL;
                            break;
                        case "私密":
                            ivPeople.setImageResource(R.drawable.people_ok);
                            tvPeopleSee.setTextColor(getResources().getColor(R.color.greenBg));
                            tvPeopleType.setTextColor(getResources().getColor(R.color.greenBg));
                            publishType = Constants.PEOPLE_MINE;
                            break;
                        case "隐身":
                            ivPeople.setImageResource(R.drawable.people_ok);
                            tvPeopleSee.setTextColor(getResources().getColor(R.color.greenBg));
                            tvPeopleType.setTextColor(getResources().getColor(R.color.greenBg));
                            publishType = Constants.PEOPLE_STEALTH;
                            break;
                    }
                    tvPeopleType.setText(type);
                }
                break;
        }
    }

    /**
     * 设置Video数据
     *
     * @param selectList
     */
    private void loadVideoData(List<LocalMedia> selectList) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            videoPath = selectList.get(0).getAndroidQToPath();
        } else {
            videoPath = selectList.get(0).getPath();
        }
        File file = new File(videoPath);
        Glide.with(mContext)
                .load(file)
                .apply(Constants.picLoadOptions)
                .into(ivVideo);
        if (file.exists()) {
            videoLoad = true;
            setEnable(true);
            ivPlay.setVisibility(View.VISIBLE);
            ivDelete.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 更新adapter数据
     *
     * @param selectList
     */
    private void sendMultiPic(List<LocalMedia> selectList) {
        for (LocalMedia localMedia : selectList) {
            String imgPath = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                imgPath = localMedia.getAndroidQToPath();
            } else {
                imgPath = localMedia.getPath();
            }
            shareLists.add(new ShareGvEntity(imgPath, "file"));
        }
        if (shareLists.size() > 9) {
            shareLists.remove(0);
            isHaveAdd = false;
        }
        if (shareLists.size() < 9 && !isHaveAdd) {
            shareLists.add(0, new ShareGvEntity(R.drawable.add_pic + "", "pic"));
            isHaveAdd = true;
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
        if (mLocationClient!=null) {
            mLocationClient.onDestroy();
        }//销毁定位客户端，同时销毁本地定位服务。
    }
}