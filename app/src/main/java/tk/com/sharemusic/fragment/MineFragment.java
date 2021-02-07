package tk.com.sharemusic.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestOptions;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import tk.com.sharemusic.R;
import tk.com.sharemusic.ShareApplication;
import tk.com.sharemusic.activity.LoginActivity;
import tk.com.sharemusic.activity.MyPublishActivity;
import tk.com.sharemusic.config.Constants;
import tk.com.sharemusic.entity.UpLoadHead;
import tk.com.sharemusic.entity.User;
import tk.com.sharemusic.enums.Gender;
import tk.com.sharemusic.event.RefreshMyInfoEvent;
import tk.com.sharemusic.myview.CircleImage;
import tk.com.sharemusic.myview.dialog.CommonDialog;
import tk.com.sharemusic.myview.dialog.ResetDialog;
import tk.com.sharemusic.network.BaseResult;
import tk.com.sharemusic.network.HttpMethod;
import tk.com.sharemusic.network.NetWorkService;
import tk.com.sharemusic.network.RxSchedulers;
import tk.com.sharemusic.network.response.UpLoadHeadVo;
import tk.com.sharemusic.network.rxjava.BaseObserver;
import tk.com.sharemusic.utils.DateUtil;
import tk.com.sharemusic.utils.GlideEngine;
import tk.com.sharemusic.utils.StringUtils;
import tk.com.sharemusic.utils.ToastUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MineFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_head)
    CircleImage ivHead;
    @BindView(R.id.rl_head)
    RelativeLayout rlHead;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.rl_name)
    RelativeLayout rlName;
    @BindView(R.id.tv_sex)
    TextView tvSex;
    @BindView(R.id.tv_age)
    TextView tvAge;
    @BindView(R.id.rl_sex)
    RelativeLayout rlSex;
    @BindView(R.id.tv_des)
    TextView tvDes;
    @BindView(R.id.rl_des)
    RelativeLayout rlDes;
    @BindView(R.id.rl_change_password)
    RelativeLayout rlChangePassword;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Unbinder bind;
    private NetWorkService service;
    private int updataType = 0;
    private User user;
    private final static String[] PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    public static final int ACTION_TYPE_ALBUM = 0;
    public static final int ACTION_TYPE_PHOTO = 1;
    public static final int PERMISSION_REQUEST_CODE = 110;
    private String protraitPath;

    public MineFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SocialPublic.
     */
    // TODO: Rename and change types and number of parameters
    public static MineFragment newInstance(String param1, String param2) {
        MineFragment fragment = new MineFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_mine_fragment, container, false);
        bind = ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        service = HttpMethod.getInstance().create(NetWorkService.class);
        initData();
    }

    private void initData() {
        user = ShareApplication.getUser();

        if (user==null){
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            ToastUtil.showShortMessage(getContext(),"请先登录");
            getActivity().finish();
            return;
        }

        if (user.getSex()==null){
            user.setSex(1);
        }

        if (user.getAge()!=null){
            tvAge.setText(user.getAge()+"");
        }

        Glide.with(getContext())
                .load(TextUtils.isEmpty(user.getHeadImg())?Gender.getImage(user.getSex()):NetWorkService.homeUrl+user.getHeadImg())
                .apply(Constants.headOptions)
                .into(ivHead);
        if (!TextUtils.isEmpty(user.getUserName())) {
            tvName.setText(user.getUserName());
        }
        if (!TextUtils.isEmpty(user.getDes())) {
            tvDes.setText(user.getDes());
        }
        tvSex.setText(Gender.getName(user.getSex()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }

    @OnClick({R.id.iv_back, R.id.rl_head, R.id.rl_name, R.id.rl_sex, R.id.rl_age, R.id.rl_des, R.id.rl_change_password,
            R.id.rl_my_public, R.id.rl_logout})
    public void onViewClicked(View view) {
        ResetDialog dialog = new ResetDialog(getContext());
        dialog.setListener(new ResetDialog.CommitListener() {
            @Override
            public void commit(String reslut) {
                if (!TextUtils.isEmpty(reslut))
                updataInfo(reslut);
                dialog.dismiss();
            }
        });
        switch (view.getId()) {
            case R.id.iv_back:

                break;
            case R.id.rl_head:
                handleSelectPicture();
                break;
            case R.id.rl_name:
                dialog.setTitle("修改用户名");
                dialog.setHint("请输入用户名");
                updataType = 1;
                dialog.show();
                break;
            case R.id.rl_sex:
                updataType = 2;
                CommonDialog commonDialog = new CommonDialog(getContext());
                commonDialog.setTitle("选择性别");
                commonDialog.setText1("男");
                commonDialog.setText2("女");
                commonDialog.setOnClick(new CommonDialog.OnClick() {
                    @Override
                    public void tv1Onclick() {
                        updataInfo("男");
                        commonDialog.dismiss();
                    }

                    @Override
                    public void tv2Onclick() {
                        updataInfo("女");
                        commonDialog.dismiss();
                    }

                    @Override
                    public void tvCancelClick() {
                        commonDialog.dismiss();
                    }
                });
                commonDialog.show();
                break;
            case R.id.rl_age:
                updataType = 4;
                selectBirthday();
                break;
            case R.id.rl_des:
                dialog.setTitle("个人描述");
                dialog.setHint("输入个人描述");
                updataType = 3;
                dialog.show();
                break;
            case R.id.rl_change_password:

                break;
            case R.id.rl_my_public:
                Intent intent = new Intent(getContext(), MyPublishActivity.class);
                intent.putExtra("userId",user.getUserId());
                startActivity(intent);
                break;
            case R.id.rl_logout:
                ShareApplication.clearActivity();
                ShareApplication.getInstance().getConfig().setString("password","");
                ShareApplication.getInstance().getConfig().setBoolean("logined",false);
                ShareApplication.getInstance().getConfig().setObject("userInfo",null);
                startActivity(new Intent(getContext(),LoginActivity.class));
                break;
        }
    }

    private void updataInfo(String str){
        Map map = new HashMap();
        if (updataType==0){
            return;
        }
        map.put("userId",user.getUserId());
        if (updataType==1){
            if (str.equals(user.getUserName()))
                return;
            map.put("name",str);
        }else if (updataType==2){
            if (user.getSex()==Gender.getCode(str))
                return;
            map.put("sex",Gender.getCode(str));
        }else if (updataType==3){
            if (user.getDes().equals(str))
                return;
            map.put("des",str);
        }else if (updataType==4){
            if (user.getBirthday().equals(str))
                return;
            String[] split = str.split("-");
            map.put("year",split[0]);
            map.put("month",split[1]);
            map.put("day",split[2]);
        }
        service.updataInfo(map)
                .compose(RxSchedulers.<BaseResult>compose(getContext()))
                .subscribe(new BaseObserver<BaseResult>(getContext()) {
                    @Override
                    public void onSuccess(BaseResult baseResult) {
                        switch (updataType){
                            case 1:
                                tvName.setText(str);
                                user.setUserName(str);
                                ShareApplication.getInstance().getConfig().setString("userName",str);
                                ShareApplication.user.setUserName(str);
                                break;
                            case 2:
                                tvSex.setText(str);
                                user.setSex(Gender.getCode(str));
                                ShareApplication.user.setSex(Gender.getCode(str));
                                break;
                            case 3:
                                tvDes.setText(str);
                                user.setDes(str);
                                ShareApplication.user.setDes(str);
                                break;
                            case 4:
                                tvAge.setText((DateUtil.getCurrentDay(DateUtil.TYPE_YEAR)-Integer.valueOf(str.split("-")[0]))+"");
                                user.setAge(DateUtil.getCurrentDay(DateUtil.TYPE_YEAR)-Integer.valueOf(str.split("-")[0]));
                                user.setBirthday(str);
                                ShareApplication.user = user;
                                break;
                        }
                        EventBus.getDefault().post(new RefreshMyInfoEvent());
                    }

                    @Override
                    public void onFailed(String msg) {
                        ToastUtil.showShortMessage(getContext(),msg);
                    }
                });
    }


    /**
     * 上传头像
     * 拍照/选择图片dialog
     */
    private void handleSelectPicture() {
        final CommonDialog commonDialog = new CommonDialog(getContext());
        commonDialog.setTitle("选择头像");
        commonDialog.setText1("相册");
        commonDialog.setText2("拍照");
        commonDialog.setOnClick(new CommonDialog.OnClick() {
            @Override
            public void tv1Onclick() {
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
                    if (!isGrantPermission(PERMISSIONS)){
                        ToastUtil.showShortMessage(getContext(),"权限不足");
                        requestPermissions(PERMISSIONS,PERMISSION_REQUEST_CODE);
                    }else {
                        goToSelectPicture(ACTION_TYPE_ALBUM);
                    }
                }else {
                    goToSelectPicture(ACTION_TYPE_ALBUM);
                }
                commonDialog.dismiss();
            }

            @Override
            public void tv2Onclick() {
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
                    if (!isGrantPermission(new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE})){
                        ToastUtil.showShortMessage(getContext(),"权限不足");
                        requestPermissions(PERMISSIONS,PERMISSION_REQUEST_CODE);
                    }else {
                        goToSelectPicture(ACTION_TYPE_PHOTO);
                    }
                }else {
                    goToSelectPicture(ACTION_TYPE_PHOTO);
                }
                commonDialog.dismiss();
            }

            @Override
            public void tvCancelClick() {
                commonDialog.dismiss();
            }
        });
        commonDialog.show();
    }

    /**
     * 选择图片/拍照
     *
     * @param position
     */
    private void goToSelectPicture(int position) {
        switch (position) {
            case ACTION_TYPE_PHOTO:
                PictureSelector.create(this)
                        .openCamera(PictureMimeType.ofImage())
                        .enableCrop(true)
                        .isDragFrame(true)// 是否可拖动裁剪框
                        .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                        .withAspectRatio(1,1)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                        .circleDimmedLayer(false)// 是否圆形裁剪 true or false
                        .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                        .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                        .forResult(PictureConfig.CHOOSE_REQUEST);
                break;
            case ACTION_TYPE_ALBUM:
                PictureSelector.create(this)
                        .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                        .loadImageEngine(GlideEngine.createGlideEngine())
//                .theme()//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
                        .maxSelectNum(1)// 最大图片选择数量 int
                        .minSelectNum(1)// 最小选择数量 int
                        .imageSpanCount(3)// 每行显示个数 int
                        .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                        .isCamera(false)// 是否显示拍照按钮 true or false
                        .enableCrop(true)
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

    @Override
    public void onActivityResult( int requestCode,int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片、视频、音频选择结果回调
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    protraitPath = selectList.get(0).getCutPath();
                    uploadAvatar();
                    break;
                case PERMISSION_REQUEST_CODE:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!isGrantPermission(PERMISSIONS)) {
                            ToastUtil.showShortMessage(getContext(),"权限不足");
                        } else {
                            handleSelectPicture();
                        }
                    }
                    break;
            }
        }
    }

    private void uploadAvatar() {
        if (TextUtils.isEmpty(protraitPath)){
            return;
        }

        User user = ShareApplication.getUser();
        if (user==null){
            return;
        }

        File file = new File(protraitPath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"),file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("img",file.getName(),requestFile);
        service.uploadHead(part, StringUtils.toRequestBody(user.getUserId()))
                .compose(RxSchedulers.<UpLoadHeadVo>compose(getContext()))
                .subscribe(new BaseObserver<UpLoadHeadVo>(getContext()) {
                    @Override
                    public void onSuccess(UpLoadHeadVo upLoadHeadVo) {
                        UpLoadHead data = upLoadHeadVo.getData();
                        if (data!=null && upLoadHeadVo.getData().isSuccess()){
                            user.setHeadImg(upLoadHeadVo.getData().getUrl());
                            ShareApplication.user = user;
                            EventBus.getDefault().post(new RefreshMyInfoEvent());
                            Glide.with(getContext())
                                    .load(NetWorkService.homeUrl+data.getUrl())
                                    .apply(Constants.headOptions)
                                    .into(ivHead);
                        }else {
                            ToastUtil.showShortMessage(getContext(),"头像上传失败");
                        }
                    }

                    @Override
                    public void onFailed(String msg) {
                        ToastUtil.showShortMessage(getContext(),msg);
                    }
                });
    }

    private void selectBirthday(){
        String[] birthdays = user.getBirthday().split("-");
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(Integer.parseInt(birthdays[0]), Integer.valueOf(birthdays[1])-1, Integer.valueOf(birthdays[2]));

        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        int nowYear = DateUtil.getCurrentDay(DateUtil.TYPE_YEAR);
        int nowMonth = DateUtil.getCurrentDay(DateUtil.TYPE_MONTH);
        int nowDay = DateUtil.getCurrentDay(DateUtil.TYPE_DAY);
        startDate.set(nowYear-100,0,1);
        endDate.set(nowYear,nowMonth-1,nowDay);

        TimePickerView timePickerView = new TimePickerBuilder(getContext(), new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                //选中事件
                updataInfo(DateUtil.birthdayDate.format(date));
            }
        })
                .setType(new boolean[]{true, true, true, false, false, false})
                .setCancelText("取消")
                .setSubmitText("确定")
                .setContentTextSize(18)
                .setTitleSize(20)
                .setTitleText("选择出生日期")
                .setOutSideCancelable(true)
                .isCyclic(false)//是否循环滚动显示
                .setTitleColor(Color.BLACK)
                .setSubmitColor(Color.BLUE)
                .setCancelColor(Color.GRAY)
                .setTitleBgColor(0xffdddddd)
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setLabel("年", "月", "日", "时", "分", "秒")
                .isCenterLabel(false)
                .isDialog(true)
                .build();
        Dialog mDialog = timePickerView.getDialog();
        if (mDialog!=null){
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM
            );
            params.leftMargin=0;
            params.rightMargin = 0;
            timePickerView.getDialogContainerLayout().setLayoutParams(params);
            Window dialogWindow = mDialog.getWindow();
            if (dialogWindow!=null){
                dialogWindow.setWindowAnimations(com.bigkoo.pickerview.R.style.picker_view_slide_anim);
                dialogWindow.setGravity(Gravity.BOTTOM);
                dialogWindow.setDimAmount(0.1f);
            }
            mDialog.show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean isGrantPermission(String[] permissions){
        for (String permission:permissions){
            if (this.getContext().checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }
}