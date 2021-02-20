package tk.com.sharemusic.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import tk.com.sharemusic.R;
import tk.com.sharemusic.ShareApplication;
import tk.com.sharemusic.network.BaseResult;
import tk.com.sharemusic.network.HttpMethod;
import tk.com.sharemusic.network.NetWorkService;
import tk.com.sharemusic.network.RxSchedulers;
import tk.com.sharemusic.network.rxjava.BaseObserver;
import tk.com.sharemusic.utils.ToastUtil;

public class ResetPasswordActivity extends CommonActivity {

    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.et_old_password)
    EditText etOldPassword;
    @BindView(R.id.et_new_password1)
    EditText etNewPassword1;
    @BindView(R.id.et_new_password2)
    EditText etNewPassword2;
    @BindView(R.id.btn_commit)
    Button btnCommit;


    private Unbinder bind;
    private NetWorkService service;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        bind = ButterKnife.bind(this);
        context = this;
        service = HttpMethod.getInstance().create(NetWorkService.class);

        initView();
    }

    private void initView() {
        etOldPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString().trim())
                        && !TextUtils.isEmpty(etNewPassword1.getText().toString().trim())
                        && !TextUtils.isEmpty(etNewPassword2.getText().toString().trim()) ){
                    btnCommit.setEnabled(true);
                }else {
                    btnCommit.setEnabled(false);
                }
            }
        });
        etNewPassword1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString().trim())
                        && !TextUtils.isEmpty(etOldPassword.getText().toString().trim())
                        && !TextUtils.isEmpty(etNewPassword2.getText().toString().trim()) ){
                    btnCommit.setEnabled(true);
                }else {
                    btnCommit.setEnabled(false);
                }
            }
        });
        etNewPassword2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString().trim())
                        && !TextUtils.isEmpty(etOldPassword.getText().toString().trim())
                        && !TextUtils.isEmpty(etNewPassword1.getText().toString().trim()) ){
                    btnCommit.setEnabled(true);
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
                ResetPasswordActivity.this.finish();
                break;
            case R.id.btn_commit:
                if (TextUtils.isEmpty(etOldPassword.getText().toString().trim())){
                    ToastUtil.showShortMessage(context,"请输入旧密码");
                    return;
                }
                if (TextUtils.isEmpty(etNewPassword1.getText().toString().trim())){
                    ToastUtil.showShortMessage(context,"请输入新密码");
                    return;
                }
                if (!etNewPassword1.getText().toString().trim().equals(etNewPassword2.getText().toString().trim())){
                    ToastUtil.showShortMessage(context,"两次新密码不一致");
                    return;
                }
                commit();
                break;
        }
    }

    private void commit() {
        Map map = new HashMap();
        map.put("oldPassword",etOldPassword.getText().toString().trim());
        map.put("newPassword",etNewPassword1.getText().toString().trim());
        map.put("userId", ShareApplication.user.getUserId());
        service.changePassword(map)
                .compose(RxSchedulers.compose(context))
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult baseResult) {
                        ToastUtil.showShortMessage(context,baseResult.getMsg());
                        ShareApplication.clearActivity();
                        ShareApplication.getInstance().getConfig().setString("password","");
                        ShareApplication.getInstance().getConfig().setBoolean("logined",false);
                        ShareApplication.getInstance().getConfig().setObject("userInfo",null);
                        startActivity(new Intent(context,LoginActivity.class));
                    }

                    @Override
                    public void onFailed(String msg) {
                        ToastUtil.showShortMessage(context,msg);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }
}