package tk.com.sharemusic.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import tk.com.sharemusic.R;
import tk.com.sharemusic.network.HttpMethod;
import tk.com.sharemusic.network.NetWorkService;
import tk.com.sharemusic.network.RxSchedulers;
import tk.com.sharemusic.network.response.PeopleVo;
import tk.com.sharemusic.network.rxjava.BaseObserver;

public class ChatActivity extends AppCompatActivity {

    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.btn_profile)
    TextView btnProfile;
    @BindView(R.id.rcv_chat)
    RecyclerView rcvChat;
    @BindView(R.id.refresh_view)
    SmartRefreshLayout refreshView;
    @BindView(R.id.icon_status)
    ImageView iconStatus;
    @BindView(R.id.ll_recorder_anim)
    LinearLayout llRecorderAnim;
    @BindView(R.id.tv_sec)
    TextView tvSec;
    @BindView(R.id.layout_recording_mask)
    RelativeLayout layoutRecordingMask;
    @BindView(R.id.tv_switch)
    ImageView tvSwitch;
    @BindView(R.id.ll_voice_text)
    LinearLayout llVoiceText;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.iv_faces)
    ImageView ivFaces;
    @BindView(R.id.iv_functions)
    ImageView ivFunctions;
    @BindView(R.id.btn_send)
    TextView btnSend;
    @BindView(R.id.layout_text)
    LinearLayout layoutText;
    @BindView(R.id.btn_press_to_speak)
    TextView btnPressToSpeak;
    @BindView(R.id.layout_full)
    LinearLayout layoutFull;
    @BindView(R.id.empty)
    TextView empty;

    private Unbinder bind;
    private NetWorkService service;
    private String partnerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_chat);
        bind = ButterKnife.bind(this);
        service = HttpMethod.getInstance().create(NetWorkService.class);

        initData();
    }

    private void initData() {
        partnerId = getIntent().getStringExtra("partnerId");
        if (TextUtils.isEmpty(partnerId))
            return;
        service.getPeopleInfo(partnerId)
                .compose(RxSchedulers.<PeopleVo>compose(this))
                .subscribe(new BaseObserver<PeopleVo>() {
                    @Override
                    public void onSuccess(PeopleVo peopleVo) {
                        tvName.setText(peopleVo.getData().getPeopleName());
                    }

                    @Override
                    public void onFailed(String msg) {

                    }
                });
    }

    @OnClick({R.id.btn_back, R.id.btn_profile, R.id.refresh_view, R.id.ll_recorder_anim, R.id.ll_voice_text, R.id.btn_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                break;
            case R.id.btn_profile:
                break;
            case R.id.refresh_view:
                break;
            case R.id.ll_recorder_anim:
                break;
            case R.id.ll_voice_text:
                break;
            case R.id.btn_send:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }
}