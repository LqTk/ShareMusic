package tk.com.sharemusic.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.ups.JPushUPSManager;
import tk.com.sharemusic.R;
import tk.com.sharemusic.ShareApplication;
import tk.com.sharemusic.entity.User;
import tk.com.sharemusic.network.BaseResult;
import tk.com.sharemusic.network.HttpMethod;
import tk.com.sharemusic.network.NetWorkService;
import tk.com.sharemusic.network.RxSchedulers;
import tk.com.sharemusic.network.response.LoginVo;
import tk.com.sharemusic.network.rxjava.BaseObserver;
import tk.com.sharemusic.utils.ToastUtil;

public class LoginActivity extends CommonActivity {

    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.bt_login)
    Button btLogin;
    @BindView(R.id.tv_register)
    TextView tvRegister;
    @BindView(R.id.tv_forget)
    TextView tvForget;

    NetWorkService service;
    private Unbinder bind;
    private String[] PERMISSIONS = new String[]{Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int PERMISSION_REQUEST_CODE = 110;
    private String shareText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bind = ButterKnife.bind(this);
        service = HttpMethod.getInstance().create(NetWorkService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isGrantPermission(PERMISSIONS)){
                requestPermissions(PERMISSIONS,111);
            }
        }
        shareText = getIntent().getStringExtra("shareText");
        String name = ShareApplication.getInstance().getConfig().getString("userName", "");
        etName.setText(name);
        String password = ShareApplication.getInstance().getConfig().getString("password", "");
        etPassword.setText(password);
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(password)) {
            login();
        }
        String logout = getIntent().getStringExtra("logout");
        if (!TextUtils.isEmpty(logout)){
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage(logout)
                    .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            dialog.show();
        }
        if (!isNotificationEnabled(this)) {
            new android.app.AlertDialog.Builder(this).setTitle("温馨提示")
                    .setMessage("你还未开启系统通知，将影响消息的接收，要去开启吗？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            gotoSet();//去设置开启通知
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private boolean isNotificationEnabled(Context context) {
        boolean isOpened = false;
        try {
            isOpened = NotificationManagerCompat.from(context).areNotificationsEnabled();
        } catch (Exception e) {
            e.printStackTrace();
            isOpened = false;
        }
        return isOpened;
    }

    /**
     * 通知栏设置
     */
    private void gotoSet() {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= 26) {
            // android 8.0引导
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());
        } else if (Build.VERSION.SDK_INT >=  Build.VERSION_CODES.LOLLIPOP) {
            // android 5.0-7.0
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", getPackageName());
            intent.putExtra("app_uid", getApplicationInfo().uid);
        } else {
            // 其他
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", getPackageName(), null));
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick({R.id.bt_login, R.id.tv_register, R.id.tv_forget})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_login:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!isGrantPermission(PERMISSIONS)){
                        requestPermissions(PERMISSIONS,PERMISSION_REQUEST_CODE);
                    }else {
                        login();
                    }
                }else {
                    login();
                }
                break;
            case R.id.tv_register:
                Intent intent = new Intent(this,RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_forget:
                startActivity(new Intent(this,ForgetPassWordActivity.class));
                break;
        }
    }

    private void login(){
        if (TextUtils.isEmpty(etName.getText().toString()) || TextUtils.isEmpty(etPassword.getText().toString())){
            ToastUtil.showShortMessage(this,"请确保用户名或密码输入完整");
            return;
        }
        HashMap map = new HashMap();
        map.put("name", etName.getText().toString());
        map.put("password",etPassword.getText().toString());
        service.login(map)
                .compose(RxSchedulers.compose(this))
                .subscribe(new BaseObserver<LoginVo>(this) {
                    @Override
                    public void onSuccess(LoginVo loginVo) {
                        if (loginVo.getStatus()==0){
                            ShareApplication.getInstance().getConfig().setBoolean("logined",true);
                            ShareApplication.getInstance().getConfig().setObject("userInfo",loginVo.getData());
                            ShareApplication.notifyRegistrationId();
                            ShareApplication.setUser(loginVo.getData());
                            ShareApplication.getInstance().getConfig().setString("userName", etName.getText().toString());
                            ShareApplication.getInstance().getConfig().setString("password", etPassword.getText().toString());
                            if (TextUtils.isEmpty(shareText)) {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            }else {
                                setResult(Activity.RESULT_OK);
                            }
                            LoginActivity.this.finish();
                            /*HashMap map1 = new HashMap();
                            map1.put("userId", loginVo.getData().getUserId());
                            map1.put("registerId",JPushInterface.getRegistrationID(getApplicationContext()));
                            service.updataRegisterId(map1)
                            .compose(RxSchedulers.compose(LoginActivity.this))
                            .subscribe(new BaseObserver<BaseResult>() {
                                @Override
                                public void onSuccess(BaseResult baseResult) {
                                    if (etName==null)
                                        return;
                                    ShareApplication.setUser(loginVo.getData());
                                    ShareApplication.getInstance().getConfig().setString("userName",etName.getText().toString());
                                    ShareApplication.getInstance().getConfig().setString("password",etPassword.getText().toString());
                                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                    LoginActivity.this.finish();
                                }

                                @Override
                                public void onFailed(String msg) {
                                    ToastUtil.showShortMessage(LoginActivity.this,"登录失败");
                                }
                            });*/
                        }
                    }

                    @Override
                    public void onFailed(String msg) {
                        ToastUtil.showShortMessage(LoginActivity.this,msg);
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean isGrantPermission(String[] permissions){
        for (String permission:permissions){
            if (this.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode== Activity.RESULT_OK){
            switch (requestCode){
                case PERMISSION_REQUEST_CODE:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!isGrantPermission(PERMISSIONS)) {
                            ToastUtil.showShortMessage(LoginActivity.this,"权限不足");
                        } else {
                            login();
                        }
                    }
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }
}