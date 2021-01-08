package tk.com.sharemusic.activity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import tk.com.sharemusic.network.HttpMethod;
import tk.com.sharemusic.network.NetWorkService;
import tk.com.sharemusic.network.RxSchedulers;
import tk.com.sharemusic.network.response.GetPublicDataTenVo;
import tk.com.sharemusic.network.rxjava.BaseObserver;

public class MyPublishActivity extends CommonActivity {

    @BindView(R.id.iv_add)
    ImageView ivAdd;
    @BindView(R.id.cycler_view)
    RecyclerView cyclerView;
    @BindView(R.id.srf)
    SmartRefreshLayout srf;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    private PublicSocialAdapter socialAdapter;
    private List<SocialPublicEntity> entityList = new ArrayList<>();
    private NetWorkService service;
    private boolean noMore = false;
    private int page = 0;
    private View emptyView;
    private TextView tvEmptyDes;
    private ImageView ivShow;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_publish);
        ButterKnife.bind(this);
        service = HttpMethod.getInstance().create(NetWorkService.class);
        emptyView = LayoutInflater.from(this).inflate(R.layout.layout_empty,null);
        tvEmptyDes = emptyView.findViewById(R.id.tv_empty_des);
        ivShow = emptyView.findViewById(R.id.iv_show);
        ivShow.setBackground(getResources().getDrawable(R.drawable.add_publish));
        ivShow.setImageResource(android.R.drawable.ic_input_add);

        initView();
        initRecyView();
        initData(true, page);
    }

    private void initView() {
        srf.setEnableRefresh(true);
        srf.setEnableLoadMore(true);
        srf.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                page = 0;
                noMore = false;
                initData(true, 0);
            }
        });
        srf.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (noMore) {
                    Toast.makeText(MyPublishActivity.this, "没有更多了...", Toast.LENGTH_SHORT).show();
                    srf.finishLoadMore();
                    return;
                }
                page++;
                initData(false, page);
            }
        });
    }


    private void initData(boolean isRefresh, int page) {
        userId = getIntent().getStringExtra("userId");
        if (TextUtils.isEmpty(userId))
            return;

        if (userId.equals(ShareApplication.user.getUserId())) {
            tvTitle.setText("我的发布");
            ivAdd.setVisibility(View.VISIBLE);
            tvEmptyDes.setText("您还没分享内容\n点击分享一个");
            ivShow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = new Intent(MyPublishActivity.this, ShareActivity.class);
                    intent1.putExtra("sharetext","");
                    startActivity(intent1);
                }
            });

        } else {
            tvTitle.setText("TA的发布");
            ivAdd.setVisibility(View.GONE);
            tvEmptyDes.setText("TA没分享内容");
        }

        loadData(isRefresh,page);
    }

    private void loadData(boolean isRefresh, int page){
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("page", page);
        service.getMyPublish(map)
                .compose(RxSchedulers.<GetPublicDataTenVo>compose(this))
                .subscribe(new BaseObserver<GetPublicDataTenVo>() {
                    @Override
                    public void onSuccess(GetPublicDataTenVo getPublicDataTenVo) {
                        if (getPublicDataTenVo.getData() != null) {
                            if (isRefresh) {
                                entityList.clear();
                                entityList.addAll(getPublicDataTenVo.getData());
                                socialAdapter.notifyDataSetChanged();
                                srf.finishRefresh();
                            } else {
                                if (getPublicDataTenVo.getData().size() == 0) {
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
                        if (isRefresh) {
                            srf.finishRefresh();
                        } else {
                            srf.finishLoadMore();
                        }
                        Toast.makeText(MyPublishActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initRecyView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        cyclerView.setLayoutManager(linearLayoutManager);

        socialAdapter = new PublicSocialAdapter(R.layout.social_public_item_layout, entityList);

        cyclerView.setAdapter(socialAdapter);

        socialAdapter.setEmptyView(emptyView);
        socialAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                SocialPublicEntity socialPublicEntity = entityList.get(position);
                Intent intent = new Intent(MyPublishActivity.this, PlayerSongActivity.class);
                intent.putExtra("url", socialPublicEntity.getShareUrl());
                startActivity(intent);
            }
        });
    }

    @OnClick({R.id.btn_back, R.id.iv_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                this.finish();
                break;
            case R.id.iv_add:
                Intent intent1 = new Intent(this, ShareActivity.class);
                intent1.putExtra("sharetext", "");
                startActivity(intent1);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(userId)){
            loadData(true,0);
        }
    }
}