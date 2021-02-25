package tk.com.sharemusic.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.util.ArrayList;
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
import tk.com.sharemusic.adapter.ShareGvAdapter;
import tk.com.sharemusic.config.Constants;
import tk.com.sharemusic.entity.ShareGvEntity;
import tk.com.sharemusic.entity.SocialPublicEntity;
import tk.com.sharemusic.network.BaseResult;
import tk.com.sharemusic.network.HttpMethod;
import tk.com.sharemusic.network.NetWorkService;
import tk.com.sharemusic.network.RxSchedulers;
import tk.com.sharemusic.network.response.UpLoadFileVo;
import tk.com.sharemusic.network.rxjava.BaseObserver;
import tk.com.sharemusic.utils.BitmapUtil;
import tk.com.sharemusic.utils.GlideEngine;
import tk.com.sharemusic.utils.ToastUtil;

public class ReportActivity extends CommonActivity {

    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_commit)
    TextView tvCommit;
    @BindView(R.id.tv_des)
    TextView tvDes;
    @BindView(R.id.rb_1)
    RadioButton rb1;
    @BindView(R.id.rb_2)
    RadioButton rb2;
    @BindView(R.id.rb_3)
    RadioButton rb3;
    @BindView(R.id.rb_4)
    RadioButton rb4;
    @BindView(R.id.rb_5)
    RadioButton rb5;
    @BindView(R.id.rb_6)
    RadioButton rb6;
    @BindView(R.id.et_report)
    EditText etReport;
    @BindView(R.id.gv_img)
    GridView gvImg;
    @BindView(R.id.rg_all)
    RadioGroup rgAll;

    private Unbinder bind;
    private NetWorkService service;
    private Context context;
    private String reportStr;
    private List<ShareGvEntity> shareLists = new ArrayList<>();
    private List<ShareGvEntity> upLoadPicLists = new ArrayList<>();
    private ShareGvAdapter adapter;
    private boolean isHaveAdd;
    private String shareUrl="";
    private String publishId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        bind = ButterKnife.bind(this);
        context = this;
        service = HttpMethod.getInstance().create(NetWorkService.class);

        publishId = getIntent().getStringExtra("publishId");
        shareLists.add(new ShareGvEntity(R.drawable.add_pic + "", "pic"));
        isHaveAdd = true;
        initView();
    }

    private void initView() {
        rgAll.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_1:
                        etReport.setVisibility(View.GONE);
                        reportStr = rb1.getText().toString();
                        break;
                    case R.id.rb_2:
                        etReport.setVisibility(View.GONE);
                        reportStr = rb2.getText().toString();
                        break;
                    case R.id.rb_3:
                        etReport.setVisibility(View.GONE);
                        reportStr = rb3.getText().toString();
                        break;
                    case R.id.rb_4:
                        etReport.setVisibility(View.GONE);
                        reportStr = rb4.getText().toString();
                        break;
                    case R.id.rb_5:
                        etReport.setVisibility(View.GONE);
                        reportStr = rb5.getText().toString();
                        break;
                    case R.id.rb_6:
                        etReport.setVisibility(View.VISIBLE);
                        reportStr = "";
                        break;
                }
            }
        });

        adapter = new ShareGvAdapter(shareLists, this);
        gvImg.setAdapter(adapter);
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
                        break;
                }
            }
        });
        gvImg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShareGvEntity entity = shareLists.get(position);
                if (entity.type.equals("pic")) {
                    choosePic();
                }
            }
        });
    }

    @OnClick({R.id.btn_back, R.id.tv_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                ReportActivity.this.finish();
                break;
            case R.id.tv_commit:
                if (!upLoadPicLists.isEmpty()){
                    ToastUtil.showShortMessage(context,"正在上传数据");
                    return;
                }
                if (etReport.getVisibility()==View.VISIBLE){
                    reportStr=etReport.getText().toString();
                }
                if (TextUtils.isEmpty(reportStr)){
                    ToastUtil.showShortMessage(context,"请选择举报原因");
                }
                upLoadPicLists.clear();
                shareUrl="";
                if (shareLists.size()>1) {
                    for (ShareGvEntity entity:shareLists){
                        if (entity.type.equals("file")){
                            upLoadPicLists.add(entity);
                        }
                    }
                    upLoadFile(upLoadPicLists.get(0).path);
                }else {
                    ToastUtil.showShortMessage(context,"请上传证明图片");
//                    startReport();
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
        service.reportFile(part, ShareApplication.user.getUserId())
                .compose(RxSchedulers.compose(context))
                .subscribe(new BaseObserver<UpLoadFileVo>(context) {
                    @Override
                    public void onSuccess(UpLoadFileVo upLoadFileVo) {
                        shareUrl = shareUrl+(String) upLoadFileVo.getData().get("url")+";";
                        upLoadPicLists.remove(0);
                        if (upLoadPicLists.size()>0){
                            upLoadFile(upLoadPicLists.get(0).path);
                        }else {
                            startReport();
                        }
                    }

                    @Override
                    public void onFailed(String msg) {
                        upLoadPicLists.clear();
                        ToastUtil.showShortMessage(context,msg);
                    }
                });
    }

    private void startReport() {
        Map map = new HashMap();
        map.put("publishId",publishId);
        map.put("userId",ShareApplication.user.getUserId());
        map.put("text",reportStr);
        map.put("img",shareUrl);
        service.reportPublish(map)
                .compose(RxSchedulers.compose(context))
                .subscribe(new BaseObserver<BaseResult>(context){

                    @Override
                    public void onSuccess(BaseResult baseResult) {
                        ToastUtil.showShortMessage(context,baseResult.getMsg());
                        ReportActivity.this.finish();
                    }

                    @Override
                    public void onFailed(String msg) {
                        ToastUtil.showShortMessage(context,msg);
                    }
                });
    }

    String tempImgPath = "";
    private MultipartBody.Part getFilePart(String path) {
        File tempFile = new File(tempImgPath);
        if (tempFile.exists()){
            tempFile.delete();
        }
        tempImgPath = BitmapUtil.compressImage(path,100,256,context);
        File file;
        if (TextUtils.isEmpty(tempImgPath)){
            file = new File(path);
        }else {
            file = new File(tempImgPath);
        }
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        return part;
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
        if (shareLists.size() > 3) {
            shareLists.remove(0);
            isHaveAdd = false;
        }
        if (shareLists.size() < 3 && !isHaveAdd) {
            shareLists.add(0, new ShareGvEntity(R.drawable.add_pic + "", "pic"));
            isHaveAdd = true;
        }
        adapter.notifyDataSetChanged();
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
                .maxSelectNum(3 - getSelectCount())// 最大图片选择数量 int
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
        switch (requestCode) {
            case PictureConfig.CHOOSE_REQUEST:
                // 图片、视频、音频选择结果回调
                List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                // 例如 LocalMedia 里面返回三种path
                // 1.media.getPath(); 为原图path
                // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                sendMultiPic(selectList);
                break;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }
}