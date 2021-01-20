package tk.com.sharemusic.activity;

import android.content.Context;
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
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tk.com.sharemusic.R;
import tk.com.sharemusic.ShareApplication;
import tk.com.sharemusic.adapter.PublicSocialAdapter;
import tk.com.sharemusic.entity.GoodsEntity;
import tk.com.sharemusic.entity.SocialPublicEntity;
import tk.com.sharemusic.event.ChangeFragmentEvent;
import tk.com.sharemusic.myview.dialog.ClickMenuView;
import tk.com.sharemusic.network.BaseResult;
import tk.com.sharemusic.network.HttpMethod;
import tk.com.sharemusic.network.NetWorkService;
import tk.com.sharemusic.network.RxSchedulers;
import tk.com.sharemusic.network.response.GetPublicDataShareIdVo;
import tk.com.sharemusic.network.response.GetPublicDataTenVo;
import tk.com.sharemusic.network.response.GoodsResultVo;
import tk.com.sharemusic.network.rxjava.BaseObserver;
import tk.com.sharemusic.utils.PopWinUtil;
import tk.com.sharemusic.utils.ToastUtil;

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
    private Context mContext;
    private final static int code_detail=1100;
    private int toPos;
    private String shareId;
    private PopWinUtil uiPopWinUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_publish);
        ButterKnife.bind(this);
        mContext = this;
        service = HttpMethod.getInstance().create(NetWorkService.class);
        emptyView = LayoutInflater.from(this).inflate(R.layout.layout_empty,null);
        tvEmptyDes = emptyView.findViewById(R.id.tv_empty_des);
        ivShow = emptyView.findViewById(R.id.iv_show);
        ivShow.setVisibility(View.VISIBLE);
        ivShow.setBackground(getResources().getDrawable(R.drawable.add_publish));
        ivShow.setImageResource(android.R.drawable.ic_input_add);
        uiPopWinUtil = new PopWinUtil(this);
        uiPopWinUtil.setShade(true);

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
                    ToastUtil.showShortMessage(MyPublishActivity.this,"没有更多了...");
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
            tvEmptyDes.setText("TA还没分享内容");
            ivShow.setVisibility(View.GONE);
        }

        loadData(isRefresh,page);
    }

    private void loadData(boolean isRefresh, int page){
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("page", page);
        service.getMyPublish(map)
                .compose(RxSchedulers.<GetPublicDataTenVo>compose(this))
                .subscribe(new BaseObserver<GetPublicDataTenVo>(this) {
                    @Override
                    public void onSuccess(GetPublicDataTenVo getPublicDataTenVo) {
                        if (getPublicDataTenVo.getData() != null) {
                            if (entityList==null)
                                return;
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
                        ToastUtil.showShortMessage(MyPublishActivity.this,msg);
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
        socialAdapter.addChildClickViewIds(R.id.ll_share_content,R.id.iv_good,R.id.iv_review,R.id.iv_share,R.id.iv_more);
        socialAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                switch (view.getId()){
                    case R.id.ll_share_content:
                        SocialPublicEntity socialPublicEntity = entityList.get(position);
                        Intent intent = new Intent(mContext, PlayerSongActivity.class);
                        intent.putExtra("url", socialPublicEntity.getShareUrl());
                        startActivity(intent);
                        break;
                    case R.id.iv_good:
                        List<GoodsEntity> goodsList = entityList.get(position).getGoodsList();
                        boolean ishave = false;
                        for (int i=0; i<goodsList.size();i++){
                            GoodsEntity goodsEntity = goodsList.get(i);
                            if (goodsEntity.getPeopleId().equals(ShareApplication.user.getUserId())){
                                service.goodsCancel(goodsEntity.getGoodsId())
                                        .compose(RxSchedulers.<BaseResult>compose(mContext))
                                        .subscribe(new BaseObserver<BaseResult>() {
                                            @Override
                                            public void onSuccess(BaseResult baseResult) {
                                                if (entityList==null)
                                                    return;
                                                entityList.get(position).getGoodsList().remove(goodsEntity);
                                                adapter.notifyItemChanged(position,"goods");
                                            }

                                            @Override
                                            public void onFailed(String msg) {
                                                ToastUtil.showShortMessage(mContext,msg);
                                            }
                                        });
                                ishave = true;
                                break;
                            }
                        }
                        if (!ishave){
                            GoodsEntity goodsEntity = new GoodsEntity();
                            goodsEntity.setPeopleId(ShareApplication.user.getUserId());
                            goodsEntity.setPeopleName(ShareApplication.user.getUserName());
                            goodsEntity.setPeopleHead(ShareApplication.user.getHeadImg());
                            goodsEntity.setPublicId(entityList.get(position).getShareId());
                            service.goodsAdd(goodsEntity)
                                    .compose(RxSchedulers.<GoodsResultVo>compose(mContext))
                                    .subscribe(new BaseObserver<GoodsResultVo>() {
                                        @Override
                                        public void onSuccess(GoodsResultVo goodsResultVo) {
                                            if (entityList==null)
                                                return;
                                            entityList.get(position).getGoodsList().add(goodsResultVo.getData());
                                            adapter.notifyItemChanged(position,"goods");
                                            ToastUtil.showShortMessage(mContext,"点赞成功");
                                        }

                                        @Override
                                        public void onFailed(String msg) {
                                            ToastUtil.showShortMessage(mContext,"点赞失败");
                                        }
                                    });
                        }
                        break;
                    case R.id.iv_review:
                        Intent detailIntent = new Intent(mContext, ShareDetailActivity.class);
                        detailIntent.putExtra("shareId",entityList.get(position).getShareId());
                        detailIntent.putExtra("position",position);
                        toPos = position;
                        shareId = entityList.get(position).getShareId();
                        startActivityForResult(detailIntent,code_detail);
                        break;
                    case R.id.iv_more:
                        showChoose(entityList.get(position),position);
                        break;
                }
            }
        });
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

    private void showChoose(SocialPublicEntity socialPublicEntity, int position){
        ClickMenuView menuView = new ClickMenuView(mContext);
        if (!socialPublicEntity.getUserId().equals(ShareApplication.user.getUserId())){
            menuView.setShowItemDelete(false);
        }
        menuView.setClickListener(new ClickMenuView.ItemClickListener() {
            @Override
            public void cancel() {
                uiPopWinUtil.dismissMenu();
            }

            @Override
            public void delete() {
                deletePublish(socialPublicEntity,position);
                uiPopWinUtil.dismissMenu();
            }

            @Override
            public void report() {
                uiPopWinUtil.dismissMenu();
            }
        });
        uiPopWinUtil.showPopupBottom(menuView.getView(),R.id.cst_my_publish);
    }

    private void deletePublish(SocialPublicEntity socialPublicEntity, int itemPos){
        service.pulishPublic(socialPublicEntity.getShareId())
                .compose(RxSchedulers.<BaseResult>compose(mContext))
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult baseResult) {
                        if (socialAdapter==null)
                            return;
                        socialAdapter.removeAt(itemPos);
                    }

                    @Override
                    public void onFailed(String msg) {
                        ToastUtil.showShortMessage(mContext,msg);
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case code_detail:
                getData(shareId);
                break;
        }
    }

    private void getData(String shareId) {
        service.getByShareId(shareId)
                .compose(RxSchedulers.<GetPublicDataShareIdVo>compose(mContext))
                .subscribe(new BaseObserver<GetPublicDataShareIdVo>() {
                    @Override
                    public void onSuccess(GetPublicDataShareIdVo getPublicDataShareIdVo) {
                        entityList.set(toPos,getPublicDataShareIdVo.getData());
                        if (socialAdapter==null)
                            return;
                        socialAdapter.notifyItemChanged(toPos);
                    }

                    @Override
                    public void onFailed(String msg) {
                        ToastUtil.showShortMessage(mContext, msg);
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