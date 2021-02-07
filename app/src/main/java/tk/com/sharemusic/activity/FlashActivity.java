package tk.com.sharemusic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import tk.com.sharemusic.R;
import tk.com.sharemusic.ShareApplication;

public class FlashActivity extends CommonActivity {

    @BindView(R.id.iv_flash)
    ImageView ivFlash;
    @BindView(R.id.tv_go)
    TextView tvGo;
    @BindView(R.id.tv_des)
    TextView tvDes;

    private int seconds = 5;
    private Handler mHandler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            seconds--;
            if (tvGo!=null) {
                tvGo.setText("跳过 " + seconds);
            }
            if (seconds==0){
                if (ShareApplication.getInstance().getConfig().getBoolean("logined",false)){
                    ShareApplication.notifyRegistrationId();
                    startActivity(new Intent(FlashActivity.this,MainActivity.class));
                    FlashActivity.this.finish();
                }else {
                    startActivity(new Intent(FlashActivity.this,LoginActivity.class));
                    FlashActivity.this.finish();
                }
            }else {
                mHandler.postDelayed(runnable, 1000);
            }
        }
    };
    private Unbinder bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);
        bind = ButterKnife.bind(this);
        mHandler.postDelayed(runnable,1000);
    }

    @OnClick({R.id.tv_go})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_go:
                mHandler.removeCallbacks(runnable);
                if (ShareApplication.getInstance().getConfig().getBoolean("logined",false)){
                    ShareApplication.notifyRegistrationId();
                    startActivity(new Intent(FlashActivity.this,MainActivity.class));
                    FlashActivity.this.finish();
                }else {
                    startActivity(new Intent(FlashActivity.this,LoginActivity.class));
                    FlashActivity.this.finish();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }
}