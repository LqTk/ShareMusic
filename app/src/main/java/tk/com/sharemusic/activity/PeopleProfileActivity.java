package tk.com.sharemusic.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import tk.com.sharemusic.R;
import tk.com.sharemusic.ShareApplication;
import tk.com.sharemusic.entity.MsgEntiti;
import tk.com.sharemusic.entity.User;
import tk.com.sharemusic.enums.Gender;
import tk.com.sharemusic.event.RefreshPartnerEvent;
import tk.com.sharemusic.myview.CircleImage;
import tk.com.sharemusic.network.BaseResult;
import tk.com.sharemusic.network.HttpMethod;
import tk.com.sharemusic.network.NetWorkService;
import tk.com.sharemusic.network.RxSchedulers;
import tk.com.sharemusic.network.response.PeopleVo;
import tk.com.sharemusic.network.rxjava.BaseObserver;

public class PeopleProfileActivity extends AppCompatActivity {

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

    private Unbinder bind;
    private NetWorkService service;
    private Context context;
    private String peopleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_profile);
        bind = ButterKnife.bind(this);
        service = HttpMethod.getInstance().create(NetWorkService.class);
        context = this;

        initData();
    }

    private void initData() {
        peopleId = getIntent().getStringExtra("peopleId");
        if (TextUtils.isEmpty(peopleId))
            return;
        service.getPeopleInfo(peopleId)
                .compose(RxSchedulers.<PeopleVo>compose(this))
                .subscribe(new BaseObserver<PeopleVo>() {
                    @Override
                    public void onSuccess(PeopleVo peopleVo) {
                        MsgEntiti data = peopleVo.getData();
                        Glide.with(context)
                                .load(TextUtils.isEmpty(data.getPeopleHead()) ? Gender.getImage(data.getPeopleSex()) : NetWorkService.homeUrl+data.getPeopleHead())
                                .apply(new RequestOptions()
                                        .centerCrop()
                                        .error(R.drawable.default_head_boy))
                                .into(ivHead);
                        tvName.setText(data.getPeopleName());
                        tvSex.setText(Gender.getName(data.getPeopleSex()));
                        tvDes.setText(data.getPeopleDes());
                    }

                    @Override
                    public void onFailed(String msg) {

                    }
                });
    }

    @OnClick({R.id.rl_publish, R.id.tv_add, R.id.tv_chat})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_publish:
                break;
            case R.id.tv_add:
                addPartner();
                break;
            case R.id.tv_chat:
                break;
        }
    }

    private void addPartner(){
        User user = ShareApplication.getUser();
        if (TextUtils.isEmpty(peopleId) || user == null)
            return;
        HashMap map = new HashMap();
        map.put("userId",user.getUserId());
        map.put("partnerId",peopleId);
        service.addPartner(map)
                .compose(RxSchedulers.<BaseResult>compose(this))
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult baseResult) {
                        Toast.makeText(PeopleProfileActivity.this,baseResult.getMsg(),Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().post(new RefreshPartnerEvent());
                        finish();
                    }

                    @Override
                    public void onFailed(String msg) {
                        Toast.makeText(PeopleProfileActivity.this,msg,Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }
}