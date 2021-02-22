package tk.com.sharemusic.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import tk.com.sharemusic.R;
import tk.com.sharemusic.ShareApplication;
import tk.com.sharemusic.config.Constants;
import tk.com.sharemusic.entity.MsgEntity;
import tk.com.sharemusic.entity.User;
import tk.com.sharemusic.enums.Gender;
import tk.com.sharemusic.event.RefreshPartnerEvent;
import tk.com.sharemusic.myview.CircleImage;
import tk.com.sharemusic.myview.dialog.ImgPreviewDialog;
import tk.com.sharemusic.myview.dialog.TextDialog;
import tk.com.sharemusic.network.BaseResult;
import tk.com.sharemusic.network.HttpMethod;
import tk.com.sharemusic.network.NetWorkService;
import tk.com.sharemusic.network.RxSchedulers;
import tk.com.sharemusic.network.response.AddPartnerVo;
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
    @BindView(R.id.tv_set_name)
    TextView tvSetName;

    private Unbinder bind;
    private NetWorkService service;
    private Context context;
    private String peopleId;
    private boolean isOpenedChat = false;
    private String headUrl;
    private final int CODE_SETNAME = 1000;//跳转到设置备注页面
    private String friendId;
    private String myId;

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
        map.put("userId", ShareApplication.user.getUserId());
        map.put("peopleId", peopleId);
        service.getPeopleInfo(map)
                .compose(RxSchedulers.<PeopleVo>compose(this))
                .subscribe(new BaseObserver<PeopleVo>(this) {
                    @Override
                    public void onSuccess(PeopleVo peopleVo) {
                        if (tvName == null)
                            return;
                        MsgEntity data = peopleVo.getData();
                        headUrl = data.getPeopleHead();
                        Glide.with(context)
                                .load(TextUtils.isEmpty(headUrl) ? Gender.getImage(data.getPeopleSex()) : NetWorkService.homeUrl + data.getPeopleHead())
                                .apply(Constants.headOptions)
                                .into(ivHead);
                        myId = data.getPeopleId();
                        tvSetName.setText(TextUtils.isEmpty(data.getSetName()) ? data.getPeopleName() : data.getSetName());
                        tvName.setText(data.getPeopleName());
                        tvSex.setText(Gender.getName(data.getPeopleSex()));
                        tvDes.setText(data.getPeopleDes());
                        tvAge.setText(data.getPeopleAge() + "岁");
                        if (data.isConcern()) {
                            friendId = data.getPartnerId();
                            tvAdd.setText("已关注");
                        } else {
                            tvAdd.setText("关注");
                        }
                    }

                    @Override
                    public void onFailed(String msg) {

                    }
                });
    }

    @OnClick({R.id.rl_publish, R.id.tv_add, R.id.tv_chat, R.id.iv_head, R.id.iv_back, R.id.tv_setting, R.id.ll_edit_name})
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
                } else {
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
                    intent.putExtra("partnerName",tvSetName.getText().toString());
                    startActivity(intent);
                }
                this.finish();
                break;
            case R.id.iv_head:
                if (!TextUtils.isEmpty(headUrl)) {
                    List<String> list = new ArrayList<>();
                    list.add(headUrl);
                    ImgPreviewDialog dialog = new ImgPreviewDialog(context, list);
                    dialog.setPhotoViewClick(new ImgPreviewDialog.PhotoViewClick() {
                        @Override
                        public void ImgClick() {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
                break;
            case R.id.iv_back:
                PeopleProfileActivity.this.finish();
                break;
            case R.id.tv_setting:
                ToastUtil.showShortMessage(context, "设置");
                break;
            case R.id.ll_edit_name:
                if (TextUtils.isEmpty(friendId)){
                    ToastUtil.showShortMessage(context,"请先关注好友");
                    return;
                }
                startActivityForResult(new Intent(this, SetNameActivity.class)
                        .putExtra("myId",myId)
                        .putExtra("id",friendId)
                        .putExtra("name",tvSetName.getText().toString()), CODE_SETNAME);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK)
            return;
        switch (requestCode) {
            case CODE_SETNAME:
                tvSetName.setText(data.getStringExtra("name"));
                setResult(Activity.RESULT_OK,data);
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
                        tvSetName.setText(tvName.getText().toString());
                        friendId = "";
                        if (tvAdd != null)
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
                .subscribe(new BaseObserver<AddPartnerVo>() {
                    @Override
                    public void onSuccess(AddPartnerVo baseResult) {
                        ToastUtil.showShortMessage(PeopleProfileActivity.this, "关注成功");
                        EventBus.getDefault().post(new RefreshPartnerEvent());
                        friendId = (String) baseResult.getData().get("friendId");
                        if (tvAdd != null)
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