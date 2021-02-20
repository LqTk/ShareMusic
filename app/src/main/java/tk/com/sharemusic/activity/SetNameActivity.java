package tk.com.sharemusic.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import tk.com.sharemusic.R;
import tk.com.sharemusic.ShareApplication;
import tk.com.sharemusic.event.RefreshPartnerEvent;
import tk.com.sharemusic.network.BaseResult;
import tk.com.sharemusic.network.HttpMethod;
import tk.com.sharemusic.network.NetWorkService;
import tk.com.sharemusic.network.RxSchedulers;
import tk.com.sharemusic.network.rxjava.BaseObserver;
import tk.com.sharemusic.utils.ToastUtil;

public class SetNameActivity extends CommonActivity {

    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_commit)
    TextView tvCommit;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.tv_name_length)
    TextView tvNameLength;
    private Unbinder bind;
    private String friendId;
    private NetWorkService service;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_name);
        bind = ButterKnife.bind(this);
        context = this;
        service = HttpMethod.getInstance().create(NetWorkService.class);

        friendId = getIntent().getStringExtra("id");
        String name = getIntent().getStringExtra("name");
        initView();
        etName.setText(name);
    }

    private void initView() {
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int i = 12 - s.toString().length();
                tvNameLength.setText(i+"");
            }
        });
    }

    @OnClick({R.id.btn_back, R.id.tv_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                SetNameActivity.this.finish();
                break;
            case R.id.tv_commit:
                if (TextUtils.isEmpty(etName.getText().toString().trim())){
                    return;
                }
                updateNote();
                break;
        }
    }

    private void updateNote() {
        Map map = new HashMap();
        map.put("id",friendId);
        map.put("name",etName.getText().toString().trim());
        service.setNote(map)
                .compose(RxSchedulers.<BaseResult>compose(this))
                .subscribe(new BaseObserver<BaseResult>(){

                    @Override
                    public void onSuccess(BaseResult baseResult) {
                        Intent intent = new Intent();
                        intent.putExtra("name",etName.getText().toString().trim());
                        EventBus.getDefault().post(new RefreshPartnerEvent());
                        setResult(Activity.RESULT_OK,intent);
                        SetNameActivity.this.finish();
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