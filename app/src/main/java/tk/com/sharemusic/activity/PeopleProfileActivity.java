package tk.com.sharemusic.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.greenrobot.eventbus.EventBus;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import tk.com.sharemusic.R;
import tk.com.sharemusic.ShareApplication;
import tk.com.sharemusic.entity.MsgEntity;
import tk.com.sharemusic.entity.User;
import tk.com.sharemusic.enums.Gender;
import tk.com.sharemusic.event.RefreshPartnerEvent;
import tk.com.sharemusic.myview.CircleImage;
import tk.com.sharemusic.myview.dialog.ImgPreviewDIalog;
import tk.com.sharemusic.myview.dialog.TextDialog;
import tk.com.sharemusic.network.BaseResult;
import tk.com.sharemusic.network.HttpMethod;
import tk.com.sharemusic.network.NetWorkService;
import tk.com.sharemusic.network.RxSchedulers;
import tk.com.sharemusic.network.response.PeopleVo;
import tk.com.sharemusic.network.rxjava.BaseObserver;
import tk.com.sharemusic.utils.ToastUtil;

public class PeopleProfileActivity extends CommonActivity {

    @BindView(R.id.rl_head)
    RelativeLayout rlHead;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_sex)
    TextView tvSex;
    @BindView(R.id.ll_detail)
    LinearLayout llDetail;
    @BindView(R.id.tv_des)
    TextView tvDes;
    @BindView(R.id.iv_right3)
    ImageView ivRight3;
    @BindView(R.id.rl_publish)
    RelativeLayout rlPublish;
    @BindView(R.id.tv_add)
    TextView tvAdd;
    @BindView(R.id.tv_chat)
    TextView tvChat;
    @BindView(R.id.ll_bottom)
    LinearLayout llBottom;
    @BindView(R.id.iv_head)
    CircleImage ivHead;
    @BindView(R.id.tv_age)
    TextView tvAge;

    private Unbinder bind;
    private NetWorkService service;
    private Context context;
    private String peopleId;
    private boolean isOpenedChat = false;
    private String headUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_profile);
        bind = ButterKnife.bind(this);
        service = HttpMethod.getInstance().create(NetWorkService.class);
        context = this;

        String from = getIntent().getStringExtra("from");
        if (!TextUtils.isEmpty(from)) {
            if (from.equals("chat")) {
                isOpenedChat = true;
            } else {
                isOpenedChat = false;
            }
        }

        initData();
    }

    private void initData() {
        peopleId = getIntent().getStringExtra("peopleId");
        if (TextUtils.isEmpty(peopleId))
            return;
        Map map = new HashMap();
        map.put("userId",ShareApplication.user.getUserId());
        map.put("peopleId",peopleId);
        service.getPeopleInfo(map)
                .compose(RxSchedulers.<PeopleVo>compose(this))
                .subscribe(new BaseObserver<PeopleVo>(this) {
                    @Override
                    public void onSuccess(PeopleVo peopleVo) {
                        if (tvName==null)
                            return;
                        MsgEntity data = peopleVo.getData();
                        headUrl = data.getPeopleHead();
                        Glide.with(context)
                                .load(TextUtils.isEmpty(headUrl) ? Gender.getImage(data.getPeopleSex()) : NetWorkService.homeUrl + data.getPeopleHead())
                                .apply(new RequestOptions()
                                        .centerCrop()
                                        .error(R.drawable.default_head_boy))
                                .into(ivHead);
                        tvName.setText(data.getPeopleName());
                        tvSex.setText(Gender.getName(data.getPeopleSex()));
                        tvDes.setText(data.getPeopleDes());
                        tvAge.setText(data.getPeopleAge()+"岁");
                        if (data.isConcern()){
                            tvAdd.setText("已关注");
                        }else {
                            tvAdd.setText("关注");
                        }
                    }

                    @Override
                    public void onFailed(String msg) {

                    }
                });
    }

    @OnClick({R.id.rl_publish, R.id.tv_add, R.id.tv_chat, R.id.iv_head})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.rl_publish:
                intent = new Intent(this, MyPublishActivity.class);
                intent.putExtra("userId", peopleId);
                startActivity(intent);
                break;
            case R.id.tv_add:
                if (tvAdd.getText().toString().equals("关注")) {
                    addPartner();
                }else {
                    TextDialog dialog = new TextDialog(this);
                    dialog.setTitle1("是否取消关注？");
                    dialog.setContent("茫茫人海，可能再也见不到啦！");
                    dialog.setOnClickListener(new TextDialog.OnClickListener() {
                        @Override
                        public void commit() {
                            cancelPartner();
                            dialog.dismiss();
                        }

                        @Override
                        public void cancel() {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
                break;
            case R.id.tv_chat:
                if (!isOpenedChat) {
                    intent = new Intent(this, ChatActivity.class);
                    intent.putExtra("partnerId", peopleId);
                    startActivity(intent);
                }
                this.finish();
                break;
            case R.id.iv_head:
                if (!TextUtils.isEmpty(headUrl)) {
                    ImgPreviewDIalog dialog = new ImgPreviewDIalog(context);
                    dialog.setPhotoViewClick(new ImgPreviewDIalog.PhotoViewClick() {
                        @Override
                        public void ImgClick() {
                            dialog.dismiss();
                        }
                    });
                    dialog.setImageView(headUrl);
                    dialog.show();
                }
                break;
        }
    }

    //取消关注
    private void cancelPartner() {
        User user = ShareApplication.getUser();
        if (TextUtils.isEmpty(peopleId) || user == null)
            return;
        HashMap map = new HashMap();
        map.put("userId", user.getUserId());
        map.put("partnerId", peopleId);
        service.cancelPartner(map)
                .compose(RxSchedulers.<BaseResult>compose(this))
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult baseResult) {
                        ToastUtil.showShortMessage(PeopleProfileActivity.this, "已取消关注");
                        EventBus.getDefault().post(new RefreshPartnerEvent());
                        if (tvAdd!=null)
                        tvAdd.setText("关注");
                    }

                    @Override
                    public void onFailed(String msg) {
                        ToastUtil.showShortMessage(PeopleProfileActivity.this, msg);
                    }
                });
    }

    //添加关注
    private void addPartner() {
        User user = ShareApplication.getUser();
        if (TextUtils.isEmpty(peopleId) || user == null)
            return;
        HashMap map = new HashMap();
        map.put("userId", user.getUserId());
        map.put("partnerId", peopleId);
        service.addPartner(map)
                .compose(RxSchedulers.<BaseResult>compose(this))
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult baseResult) {
                        ToastUtil.showShortMessage(PeopleProfileActivity.this, baseResult.getMsg());
                        EventBus.getDefault().post(new RefreshPartnerEvent());
                        if (tvAdd!=null)
                        tvAdd.setText("已关注");
                    }

                    @Override
                    public void onFailed(String msg) {
                        ToastUtil.showShortMessage(PeopleProfileActivity.this, msg);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }
}