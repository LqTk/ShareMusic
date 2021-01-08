package tk.com.sharemusic.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.ups.JPushUPSManager;
import tk.com.sharemusic.R;
import tk.com.sharemusic.ShareApplication;
import tk.com.sharemusic.network.BaseResult;
import tk.com.sharemusic.network.HttpMethod;
import tk.com.sharemusic.network.NetWorkService;
import tk.com.sharemusic.network.RxSchedulers;
import tk.com.sharemusic.network.response.LoginVo;
import tk.com.sharemusic.network.rxjava.BaseObserver;

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
                break;
        }
    }

    private void login(){
        if (TextUtils.isEmpty(etName.getText().toString()) || TextUtils.isEmpty(etPassword.getText().toString())){
            Toast.makeText(this,"请确保用户名或密码输入完整！",Toast.LENGTH_SHORT).show();
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
                            HashMap map1 = new HashMap();
                            map1.put("userId", loginVo.getData().getUserId());
                            map1.put("registerId",JPushInterface.getRegistrationID(getApplicationContext()));
                            service.updataRegisterId(map1)
                            .compose(RxSchedulers.compose(LoginActivity.this))
                            .subscribe(new BaseObserver<BaseResult>() {
                                @Override
                                public void onSuccess(BaseResult baseResult) {
                                    ShareApplication.setUser(loginVo.getData());
                                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                    LoginActivity.this.finish();
                                }

                                @Override
                                public void onFailed(String msg) {
                                    Toast.makeText(LoginActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailed(String msg) {
                        Toast.makeText(LoginActivity.this,msg,Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(this, "权限不足", Toast.LENGTH_SHORT).show();
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