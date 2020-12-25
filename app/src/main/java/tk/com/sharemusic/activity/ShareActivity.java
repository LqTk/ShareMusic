package tk.com.sharemusic.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tk.com.sharemusic.R;
import tk.com.sharemusic.ShareApplication;
import tk.com.sharemusic.entity.SocialPublicEntity;
import tk.com.sharemusic.entity.User;
import tk.com.sharemusic.event.UpLoadSocialSuccess;
import tk.com.sharemusic.network.HttpMethod;
import tk.com.sharemusic.network.NetWorkService;
import tk.com.sharemusic.network.RxSchedulers;
import tk.com.sharemusic.network.response.GetPublicDataShareIdVo;
import tk.com.sharemusic.network.rxjava.BaseObserver;

public class ShareActivity extends AppCompatActivity {

    @BindView(R.id.et_context)
    EditText etContext;
    @BindView(R.id.btn_share)
    Button btnShare;


    private NetWorkService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        ButterKnife.bind(this);
        service = HttpMethod.getInstance().create(NetWorkService.class);
        initView();
        initData();
    }

    private void initView() {
        etContext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = etContext.getText().toString();
                if (TextUtils.isEmpty(str)){
                    btnShare.setEnabled(false);
                }else {
                    btnShare.setEnabled(true);
                }
            }
        });
    }

    private void initData() {
        String sharetext = getIntent().getStringExtra("sharetext");
        if (!TextUtils.isEmpty(sharetext)){
            etContext.setText(sharetext);
        }
    }

    @OnClick(R.id.btn_share)
    public void onViewClicked() {
        String string = etContext.getText().toString();
        if (TextUtils.isEmpty(string)){
            return;
        }
        String title = string.substring(string.indexOf("《")+1,string.indexOf("》"));
        ArrayList containedUrls = new ArrayList();
        String urlRegex = "((https?|ftp|gopher|telnet|file|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
//                    pattern = Pattern.compile("^(https?|ftp|file|http)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",Pattern.CASE_INSENSITIVE);
        Pattern pattern = Pattern.compile(urlRegex,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(string);
        while (matcher.find()) {
            containedUrls.add(string.substring(matcher.start(0),
                    matcher.end(0)));
        }
        String url = containedUrls.get(0).toString();
        Toast.makeText(this, url, Toast.LENGTH_SHORT).show();
//                    web.loadUrl(url);
        User user = ShareApplication.getUser();
        if (user==null){
            Toast.makeText(this,"分享失败",Toast.LENGTH_SHORT).show();
            return;
        }
        SocialPublicEntity socialPublicEntity = new SocialPublicEntity(user.getUserId(),user.getUserName(),
                user.getHeadImg(),user.getSex(),title,url,string,1,0,0);
        upLoadSocialPublid(socialPublicEntity);
    }


    private void upLoadSocialPublid(SocialPublicEntity entity){
        if (entity==null)
            return;
        service.pulishPublic(entity)
                .compose(RxSchedulers.<GetPublicDataShareIdVo>compose(this))
                .subscribe(new BaseObserver<GetPublicDataShareIdVo>() {
                    @Override
                    public void onSuccess(GetPublicDataShareIdVo getPublicDataShareIdVo) {
                        if (getPublicDataShareIdVo.getData()!=null){
                            EventBus.getDefault().post(new UpLoadSocialSuccess(getPublicDataShareIdVo.getData()));
                            ShareActivity.this.finish();
                        }else {
                            Toast.makeText(ShareActivity.this,"分享失败",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailed(String msg) {
                        Toast.makeText(ShareActivity.this,msg,Toast.LENGTH_SHORT).show();
                    }
                });
    }
}