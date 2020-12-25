package tk.com.sharemusic.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestOptions;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.util.HashMap;
import java.util.List;

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
import tk.com.sharemusic.entity.UpLoadHead;
import tk.com.sharemusic.entity.User;
import tk.com.sharemusic.enums.Gender;
import tk.com.sharemusic.myview.CircleImage;
import tk.com.sharemusic.myview.dialog.CommonDialog;
import tk.com.sharemusic.network.HttpMethod;
import tk.com.sharemusic.network.NetWorkService;
import tk.com.sharemusic.network.RxSchedulers;
import tk.com.sharemusic.network.response.UpLoadHeadVo;
import tk.com.sharemusic.network.rxjava.BaseObserver;
import tk.com.sharemusic.utils.GlideEngine;
import tk.com.sharemusic.utils.StringUtils;

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
    private User user;
    private final static String[] PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    public static final int ACTION_TYPE_ALBUM = 0;
    public static final int ACTION_TYPE_PHOTO = 1;
    public static final int PERMISSION_REQUEST_CODE = 110;
    private String protraitPath;
    private RequestOptions options;

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
            Toast.makeText(getContext(),"请先登录",Toast.LENGTH_SHORT).show();
            getActivity().finish();
            return;
        }

        options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.default_head_boy);

        if (user.getSex()==null){
            user.setSex(0);
        }

        Glide.with(getContext())
                .load(TextUtils.isEmpty(user.getHeadImg())?Gender.getImage(user.getSex()):NetWorkService.homeUrl+user.getHeadImg())
                .apply(options)
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

    @OnClick({R.id.iv_back, R.id.rl_head, R.id.rl_name, R.id.rl_sex, R.id.rl_des, R.id.rl_change_password, R.id.rl_my_public})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:

                break;
            case R.id.rl_head:
                handleSelectPicture();
                break;
            case R.id.rl_name:
                break;
            case R.id.rl_sex:
                break;
            case R.id.rl_des:
                break;
            case R.id.rl_change_password:
                break;
            case R.id.rl_my_public:
                Intent intent = new Intent(getContext(), MyPublishActivity.class);
                startActivity(intent);
                break;
        }
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
                        Toast.makeText(getContext(),"权限不足",Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getContext(),"权限不足",Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getContext(), "权限不足", Toast.LENGTH_SHORT).show();
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
                .subscribe(new BaseObserver<UpLoadHeadVo>() {
                    @Override
                    public void onSuccess(UpLoadHeadVo upLoadHeadVo) {
                        UpLoadHead data = upLoadHeadVo.getData();
                        if (data!=null && upLoadHeadVo.getData().isSuccess()){
                            user.setHeadImg(upLoadHeadVo.getData().getUrl());
                            ShareApplication.setUser(user);
                            Glide.with(getContext())
                                    .load(NetWorkService.homeUrl+data.getUrl())
                                    .apply(options)
                                    .into(ivHead);
                        }else {
                            Toast.makeText(getContext(),"头像上传失败",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailed(String msg) {
                        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                    }
                });
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