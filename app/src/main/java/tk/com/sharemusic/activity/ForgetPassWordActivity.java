package tk.com.sharemusic.activity;

import android.content.Context;
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
import tk.com.sharemusic.network.BaseResult;
import tk.com.sharemusic.network.HttpMethod;
import tk.com.sharemusic.network.NetWorkService;
import tk.com.sharemusic.network.RxSchedulers;
import tk.com.sharemusic.network.rxjava.BaseObserver;
import tk.com.sharemusic.utils.ToastUtil;

public class ForgetPassWordActivity extends CommonActivity {

    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.btn_commit)
    Button btnCommit;

    private Unbinder bind;
    private NetWorkService service;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass_word);
        bind = ButterKnife.bind(this);
        context = this;
        service = HttpMethod.getInstance().create(NetWorkService.class);

        initView();
    }

    private void initView() {
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString().trim()) && !TextUtils.isEmpty(etPassword.getText().toString().trim())){
                    btnCommit.setEnabled(true);
                }else {
                    btnCommit.setEnabled(false);
                }
            }
        });
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString().trim()) && !TextUtils.isEmpty(etPhone.getText().toString().trim())){
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
                ForgetPassWordActivity.this.finish();
                break;
            case R.id.btn_commit:
                if (TextUtils.isEmpty(etPhone.getText().toString().trim())){
                    ToastUtil.showShortMessage(context,"请输入电话号码");
                    return;
                }
                if (TextUtils.isEmpty(etPassword.getText().toString().trim())){
                    ToastUtil.showShortMessage(context,"请输入密码");
                    return;
                }
                commit();
                break;
        }
    }

    private void commit() {
        Map map = new HashMap();
        map.put("phone",etPhone.getText().toString().trim());
        map.put("password",etPassword.getText().toString().trim());
        service.resetPassword(map)
                .compose(RxSchedulers.compose(context))
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult baseResult) {
                        ToastUtil.showShortMessage(context,baseResult.getMsg());
                        ForgetPassWordActivity.this.finish();
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