package tk.com.sharemusic.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import tk.com.sharemusic.R;
import tk.com.sharemusic.ShareApplication;
import tk.com.sharemusic.adapter.PublishMsgAdapter;
import tk.com.sharemusic.entity.PublishMsgEntity;
import tk.com.sharemusic.event.ChangeFragmentEvent;
import tk.com.sharemusic.event.MsgCountEvent;
import tk.com.sharemusic.network.BaseResult;
import tk.com.sharemusic.network.HttpMethod;
import tk.com.sharemusic.network.NetWorkService;
import tk.com.sharemusic.network.RxSchedulers;
import tk.com.sharemusic.network.response.PublicMsgVo;
import tk.com.sharemusic.network.rxjava.BaseObserver;
import tk.com.sharemusic.utils.ToastUtil;

public class PublishMsgActivity extends CommonActivity {

    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.rcy_msg)
    RecyclerView rcyMsg;


    private Context context;
    private Unbinder bind;
    private NetWorkService service;
    private List<PublishMsgEntity> msgEntities = new ArrayList<>();
    private PublishMsgAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_msg);
        bind = ButterKnife.bind(this);
        context = this;
        service = HttpMethod.getInstance().create(NetWorkService.class);

        initView();
        loadData();
    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcyMsg.setLayoutManager(linearLayoutManager);

        adapter = new PublishMsgAdapter(R.layout.layout_publish_msg_item, msgEntities);
        rcyMsg.setAdapter(adapter);

        adapter.addChildClickViewIds(R.id.iv_head);
        adapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                switch (view.getId()){
                    case R.id.iv_head:
                        PublishMsgEntity publishMsgEntity = msgEntities.get(position);
                        if (publishMsgEntity.peopleId.equals(ShareApplication.user.getUserId())) {
                            EventBus.getDefault().post(new ChangeFragmentEvent(MainActivity.PAGE_MINE));
                            return;
                        }
                        Intent intent1 = new Intent(context, PeopleProfileActivity.class);
                        intent1.putExtra("peopleId", publishMsgEntity.peopleId);
                        intent1.putExtra("from", "public");
                        startActivity(intent1);
                        break;
                }
            }
        });
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                PublishMsgEntity publishMsgEntity = msgEntities.get(position);
                Intent detailIntent = new Intent(context, ShareDetailActivity.class);
                detailIntent.putExtra("shareId", publishMsgEntity.publishId);
                detailIntent.putExtra("from","msgActivity");
                detailIntent.putExtra("msgId",publishMsgEntity.msgId);
                startActivity(detailIntent);
                updateReadState(position);
            }
        });
    }

    private void updateReadState(int position) {
        service.updateReadState(msgEntities.get(position).msgId)
                .compose(RxSchedulers.compose(context))
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult baseResult) {
                        adapter.getData().get(position).setIsReaded(1);
                        adapter.notifyItemChanged(position,"updateState");
                    }

                    @Override
                    public void onFailed(String msg) {

                    }
                });
    }

    private void loadData() {
        if (ShareApplication.user==null)
            return;
        service.getPublicMsg(ShareApplication.user.getUserId())
                .compose(RxSchedulers.<PublicMsgVo>compose(context))
                .subscribe(new BaseObserver<PublicMsgVo>() {
                    @Override
                    public void onSuccess(PublicMsgVo publicMsgVo) {
                        adapter.addData(publicMsgVo.getData());
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