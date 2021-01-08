package tk.com.sharemusic.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tk.com.sharemusic.R;
import tk.com.sharemusic.entity.User;
import tk.com.sharemusic.network.BaseResult;
import tk.com.sharemusic.network.HttpMethod;
import tk.com.sharemusic.network.NetWorkService;
import tk.com.sharemusic.network.RxSchedulers;
import tk.com.sharemusic.network.rxjava.BaseObserver;

public class RegisterActivity extends CommonActivity {

    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_password1)
    EditText etPassword1;
    @BindView(R.id.et_password2)
    EditText etPassword2;
    @BindView(R.id.btn_commit)
    Button btnCommit;
    private NetWorkService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        service = HttpMethod.getInstance().create(NetWorkService.class);

        initView();
    }

    private void initView() {
        etPassword1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(etName.getText().toString().trim()) || !TextUtils.isEmpty(etPhone.getText().toString().trim())){
                    if (etPassword1.getText().toString().trim().equals(etPassword2.getText().toString().trim())){
                        btnCommit.setEnabled(true);
                    }else {
                        btnCommit.setEnabled(false);
                    }
                }else {
                    btnCommit.setEnabled(false);
                }
            }
        });

        etPassword2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(etName.getText().toString().trim()) || !TextUtils.isEmpty(etPhone.getText().toString().trim())){
                    if (etPassword1.getText().toString().trim().equals(etPassword2.getText().toString().trim())){
                        btnCommit.setEnabled(true);
                    }else {
                        btnCommit.setEnabled(false);
                    }
                }else {
                    btnCommit.setEnabled(false);
                }
            }
        });
    }

    @OnClick({R.id.btn_back, R.id.btn_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                this.finish();
                break;
            case R.id.btn_commit:
                if (TextUtils.isEmpty(etName.getText().toString().trim()) && TextUtils.isEmpty(etPhone.getText().toString().trim())){
                    Toast.makeText(this,"请输入用户名或电话号码",Toast.LENGTH_LONG).show();
                    return;
                }else if (TextUtils.isEmpty(etPassword1.getText().toString().trim())){
                    Toast.makeText(this,"请输入密码",Toast.LENGTH_LONG).show();
                    return;
                }else if (TextUtils.isEmpty(etPassword2.getText().toString().trim())){
                    Toast.makeText(this,"请输入确认密码",Toast.LENGTH_LONG).show();
                    return;
                }else if (!etPassword1.getText().toString().trim().equals(etPassword2.getText().toString().trim())){
                    Toast.makeText(this,"两次输入密码不一致",Toast.LENGTH_LONG).show();
                    return;
                }else {
                    register();
                }
                break;
        }
    }

    private void register(){
        Map map = new HashMap();
        map.put("name",etName.getText().toString().trim());
        map.put("phone",etPhone.getText().toString().trim());
        map.put("password",etPassword1.getText().toString().trim());
        service.register(map)
                .compose(RxSchedulers.<BaseResult>compose(this))
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult baseResult) {
                        if (baseResult.getStatus()==0){
                            Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_LONG).show();
                            RegisterActivity.this.finish();
                        }
                    }

                    @Override
                    public void onFailed(String msg) {
                        Toast.makeText(RegisterActivity.this,"注册失败",Toast.LENGTH_LONG).show();
                    }
                });
    }
}