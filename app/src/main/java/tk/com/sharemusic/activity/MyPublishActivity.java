package tk.com.sharemusic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tk.com.sharemusic.R;
import tk.com.sharemusic.ShareApplication;
import tk.com.sharemusic.adapter.PublicSocialAdapter;
import tk.com.sharemusic.entity.SocialPublicEntity;
import tk.com.sharemusic.entity.User;
import tk.com.sharemusic.network.HttpMethod;
import tk.com.sharemusic.network.NetWorkService;
import tk.com.sharemusic.network.RxSchedulers;
import tk.com.sharemusic.network.response.GetPublicDataTenVo;
import tk.com.sharemusic.network.rxjava.BaseObserver;

public class MyPublishActivity extends AppCompatActivity {

    @BindView(R.id.iv_add)
    ImageView ivAdd;
    @BindView(R.id.cycler_view)
    RecyclerView cyclerView;
    @BindView(R.id.srf)
    SmartRefreshLayout srf;

    private PublicSocialAdapter socialAdapter;
    private List<SocialPublicEntity> entityList = new ArrayList<>();
    private NetWorkService service;
    private boolean noMore = false;
    private int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_publish);
        ButterKnife.bind(this);
        service = HttpMethod.getInstance().create(NetWorkService.class);

        initView();
        initRecyView();
        initData(true,page);
    }

    private void initView() {
        srf.setEnableRefresh(true);
        srf.setEnableLoadMore(true);
        srf.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                page = 0;
                noMore = false;
                initData(true,0);
            }
        });
        srf.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (noMore){
                    Toast.makeText(MyPublishActivity.this,"没有更多了...",Toast.LENGTH_SHORT).show();
                    srf.finishLoadMore();
                    return;
                }
                page++;
                initData(false,page);
            }
        });
    }


    private void initData(boolean isRefresh, int page) {
        User user = ShareApplication.getUser();
        if (user==null){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            Toast.makeText(this,"请先登录",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        HashMap<String,Object> map = new HashMap<>();
        map.put("userId",user.getUserId());
        map.put("page",page);
        service.getMyPublish(map)
                .compose(RxSchedulers.<GetPublicDataTenVo>compose(this))
                .subscribe(new BaseObserver<GetPublicDataTenVo>() {
                    @Override
                    public void onSuccess(GetPublicDataTenVo getPublicDataTenVo) {
                        if (getPublicDataTenVo.getData()!=null){
                            if (isRefresh){
                                entityList.clear();
                                entityList.addAll(getPublicDataTenVo.getData());
                                socialAdapter.notifyDataSetChanged();
                                srf.finishRefresh();
                            }else {
                                if (getPublicDataTenVo.getData().size()==0){
                                    noMore = true;
                                }
                                entityList.addAll(getPublicDataTenVo.getData());
                                socialAdapter.notifyDataSetChanged();
                                srf.finishLoadMore();
                            }
                        }
                    }

                    @Override
                    public void onFailed(String msg) {
                        if (isRefresh){
                            srf.finishRefresh();
                        }else {
                            srf.finishLoadMore();
                        }
                        Toast.makeText(MyPublishActivity.this,msg,Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initRecyView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        cyclerView.setLayoutManager(linearLayoutManager);

        socialAdapter = new PublicSocialAdapter(R.layout.social_public_item_layout, entityList);

        cyclerView.setAdapter(socialAdapter);

        socialAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                SocialPublicEntity socialPublicEntity = entityList.get(position);
                Intent intent = new Intent(MyPublishActivity.this, PlayerSongActivity.class);
                intent.putExtra("url", socialPublicEntity.getShareurl());
                startActivity(intent);
            }
        });
    }

    @OnClick(R.id.iv_add)
    public void onViewClicked() {
        Intent intent1 = new Intent(this, ShareActivity.class);
        intent1.putExtra("sharetext","");
        startActivity(intent1);
    }

}