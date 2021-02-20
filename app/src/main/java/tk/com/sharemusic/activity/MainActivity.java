package tk.com.sharemusic.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.next.easynavigation.view.EasyNavigationBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import tk.com.sharemusic.R;
import tk.com.sharemusic.ShareApplication;
import tk.com.sharemusic.config.Constants;
import tk.com.sharemusic.event.ChangeFragmentEvent;
import tk.com.sharemusic.event.LogoutEvent;
import tk.com.sharemusic.event.MsgCountEvent;
import tk.com.sharemusic.fragment.ChatFragment;
import tk.com.sharemusic.fragment.FriendsFragment;
import tk.com.sharemusic.fragment.MineFragment;
import tk.com.sharemusic.fragment.SocialPublishFragment;
import tk.com.sharemusic.network.HttpMethod;
import tk.com.sharemusic.network.NetWorkService;
import tk.com.sharemusic.utils.ToastUtil;

public class MainActivity extends CommonActivity {

    public static int PAGE_PARTNER = 0;
    public static int PAGE_PUBLIC = 1;
    public static int PAGE_MESSAGE = 2;
    public static int PAGE_MINE = 3;
    private List<Fragment> fragments = new ArrayList<>();
    private String[] navTitle = {"好友","公场","消息","我的"};

    @BindView(R.id.bottom_bar)
    EasyNavigationBar bottomBar;
    private Unbinder bind;
    private NetWorkService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bind = ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        service = HttpMethod.getInstance().create(NetWorkService.class);
        if(!ShareApplication.showLocation){
            ShareApplication.initLocation();
        }
        getIntentData();
        initView();
    }

    private void initView() {
        fragments.add(FriendsFragment.newInstance());
        fragments.add(SocialPublishFragment.newInstance());
        fragments.add(ChatFragment.newInstance());
        fragments.add(MineFragment.newInstance("",""));
        bottomBar.titleItems(navTitle)
                .fragmentList(fragments)
                .fragmentManager(getSupportFragmentManager())
                .selectTextColor(Color.parseColor("#1296DB"))
                .normalTextColor(Color.parseColor("#8a8a8a"))
                .msgPointLeft(-5)
                .msgPointTop(-5)
                .setOnTabLoadListener(new EasyNavigationBar.OnTabLoadListener() { //Tab加载完毕回调
                    @Override
                    public void onTabLoadCompleteEvent() {
                        bottomBar.selectTab(1,false);
                    }
                })
                .build();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
        EventBus.getDefault().unregister(this);
        if (ShareApplication.mLocationClient!=null) {
            ShareApplication.mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
        }
    }

    @Subscribe
    public void changePage(ChangeFragmentEvent event){
        if (event!=null){
            bottomBar.selectTab(event.page,true);
        }
    }

    @Subscribe
    public void logOut(LogoutEvent event){
        if (event!=null){
            ShareApplication.clearActivity();
            ShareApplication.getInstance().getConfig().setString("password","");
            ShareApplication.getInstance().getConfig().setBoolean("logined",false);
            ShareApplication.getInstance().getConfig().setObject("userInfo",null);
            startActivity(new Intent(MainActivity.this,LoginActivity.class).putExtra(
                    "logout","您的账号在其它设备登录，如果不是您本人操作，请及时修改密码。"));
        }
    }

    @Subscribe
    public void msgCount(MsgCountEvent event){
        if (event!=null){
            bottomBar.setMsgPointCount(event.pos,event.count);
        }
    }

    private void getIntentData(){

        Intent intent = getIntent();
        if (intent!=null) {
            String action = intent.getAction();
            String type = intent.getType();
            if (Intent.ACTION_SEND.equals(action) && type!=null){
                Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                Bundle extras = intent.getExtras();
                if ("audio/".equals(type)) {
                    // 处理发送来音频
                    ToastUtil.showShortMessage(this,"audio");
                } else if (type.startsWith("video/")) {
                    // 处理发送来的视频
                    ToastUtil.showShortMessage(this,"video");
                } else if (type.startsWith("*/")) {
                    //处理发送过来的其他文件
                    ToastUtil.showShortMessage(this,"other");
                }else if (type.startsWith("text/")){
                    String string = extras.getString("android.intent.extra.TEXT");
                    Intent intent1 = new Intent(this,ShareActivity.class);
                    intent1.putExtra("sharetext",string);
                    intent1.putExtra("shareType", Constants.SHARE_MUSIC);
                    startActivity(intent1);
                }
            } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
                ArrayList<Uri> arrayList = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
                if (type.startsWith("audio/")) {
                    // 处理发送来的多个音频
                    ToastUtil.showShortMessage(this,"audio");
                } else if (type.startsWith("video/")) {
                    //处理发送过来的多个视频
                    ToastUtil.showShortMessage(this,"video");
                } else if (type.startsWith("*/")) {
                    //处理发送过来的多个文件
                    ToastUtil.showShortMessage(this,"other");
                }else if (type.startsWith("text/")){
                    ToastUtil.showShortMessage(this,"text");
                }
            }
            setIntent(null);
        }
    }

    @Override
    public void onBackPressed() {
        new android.app.AlertDialog.Builder(this).setTitle("温馨提示")
                .setMessage("是否退出分享生活")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }
}