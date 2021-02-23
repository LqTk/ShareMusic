package tk.com.sharemusic.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import tk.com.sharemusic.R;
import tk.com.sharemusic.ShareApplication;
import tk.com.sharemusic.activity.MainActivity;
import tk.com.sharemusic.activity.MyPublishActivity;
import tk.com.sharemusic.activity.PeopleProfileActivity;
import tk.com.sharemusic.activity.PlayerSongActivity;
import tk.com.sharemusic.activity.PublishMsgActivity;
import tk.com.sharemusic.activity.ReportActivity;
import tk.com.sharemusic.activity.ShareActivity;
import tk.com.sharemusic.activity.ShareDetailActivity;
import tk.com.sharemusic.adapter.PublicSocialAdapter;
import tk.com.sharemusic.config.Constants;
import tk.com.sharemusic.entity.ChatEntity;
import tk.com.sharemusic.entity.GoodsEntity;
import tk.com.sharemusic.entity.PublishMsgEntity;
import tk.com.sharemusic.entity.SocialPublicEntity;
import tk.com.sharemusic.event.ChangeFragmentEvent;
import tk.com.sharemusic.event.MsgCountEvent;
import tk.com.sharemusic.event.NewReviewEvent;
import tk.com.sharemusic.event.RefreshPublicData;
import tk.com.sharemusic.event.UpLoadSocialSuccess;
import tk.com.sharemusic.myview.dialog.ClickMenuView;
import tk.com.sharemusic.myview.dialog.ImgPreviewDialog;
import tk.com.sharemusic.myview.dialog.ShareDialog;
import tk.com.sharemusic.myview.dialog.VideoPreviewDialog;
import tk.com.sharemusic.network.BaseResult;
import tk.com.sharemusic.network.HttpMethod;
import tk.com.sharemusic.network.NetWorkService;
import tk.com.sharemusic.network.RxSchedulers;
import tk.com.sharemusic.network.response.GetPublicDataShareIdVo;
import tk.com.sharemusic.network.response.GetPublicDataTenVo;
import tk.com.sharemusic.network.response.GoodsResultVo;
import tk.com.sharemusic.network.response.PublicMsgVo;
import tk.com.sharemusic.network.rxjava.BaseObserver;
import tk.com.sharemusic.utils.Config;
import tk.com.sharemusic.utils.PopWinUtil;
import tk.com.sharemusic.utils.ToastUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SocialPublishFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SocialPublishFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.cycler_view)
    RecyclerView cyclerView;
    @BindView(R.id.srf)
    SmartRefreshLayout srf;
    @BindView(R.id.iv_add)
    ImageView ivAdd;
    @BindView(R.id.ll_to_top)
    LinearLayout llToTop;
    @BindView(R.id.rl_msg)
    RelativeLayout rlMsg;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Unbinder unbinder;
    private PublicSocialAdapter socialAdapter;
    private List<SocialPublicEntity> entityList = new ArrayList<>();
    private NetWorkService service;

    private boolean noMore = false;
    private int msgCount = 0;
    private View emptyView;
    private TextView tvEmptyDes;
    private ImageView ivShow;
    public final static int CODE_TODETAIL = 1000;
    public final static int CODE_TOMSG = 1001;
    private int toPos;
    private String shareId;
    private PopWinUtil uiPopWinUtil;
    private Handler mHandler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            llToTop.setVisibility(View.GONE);
            rlMsg.setVisibility(View.VISIBLE);
        }
    };
    private int page = 0;

    public SocialPublishFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SocialPublic.
     */
    // TODO: Rename and change types and number of parameters
    public static SocialPublishFragment newInstance() {
        SocialPublishFragment fragment = new SocialPublishFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_social_public, container, false);
        unbinder = ButterKnife.bind(this, v);
        EventBus.getDefault().register(this);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        service = HttpMethod.getInstance().create(NetWorkService.class);
        emptyView = LayoutInflater.from(getContext()).inflate(R.layout.layout_empty, null);
        tvEmptyDes = emptyView.findViewById(R.id.tv_empty_des);
        ivShow = emptyView.findViewById(R.id.iv_show);
        ivShow.setBackground(getResources().getDrawable(R.drawable.add_publish));
        ivShow.setImageResource(android.R.drawable.ic_input_add);
        tvEmptyDes.setText("暂无人分享~\n去分享一个吧~");
        ivShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goShare();
            }
        });
        uiPopWinUtil = new PopWinUtil(getActivity());
        uiPopWinUtil.setShade(true);
        initView();
        initRecyView();
        initData(true);
        loadMsgCount();
    }

    private void loadMsgCount() {
        if (ShareApplication.user==null)
            return;
        service.getPublicMsg(ShareApplication.user.getUserId())
                .compose(RxSchedulers.<PublicMsgVo>compose(getContext()))
                .subscribe(new BaseObserver<PublicMsgVo>() {
                    @Override
                    public void onSuccess(PublicMsgVo publicMsgVo) {
                        int count = 0;
                        for (PublishMsgEntity publishMsgEntity:publicMsgVo.getData()){
                            if(publishMsgEntity.isReaded==0){
                                count++;
                            }
                        }
//                        msgCount = publicMsgVo.getData().size();
                        msgCount = count;
                        EventBus.getDefault().post(new MsgCountEvent(MainActivity.PAGE_PUBLIC,msgCount));
                    }

                    @Override
                    public void onFailed(String msg) {

                    }
                });
    }

    private void initView() {
        srf.setEnableRefresh(true);
        srf.setEnableLoadMore(true);
        srf.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                page = 0;
                initData(true);
                loadMsgCount();
            }
        });
        srf.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (noMore) {
                    ToastUtil.showShortMessage(getContext(), "没有更多了...");
                    srf.finishLoadMore();
                    return;
                }
                page++;
                initData(false);
            }
        });
    }

    private void initData(boolean isRefresh) {
        service.getTenDatas(page)
                .compose(RxSchedulers.<GetPublicDataTenVo>compose(getContext()))
                .subscribe(new BaseObserver<GetPublicDataTenVo>() {
                    @Override
                    public void onSuccess(GetPublicDataTenVo getPublicDataTenVo) {
                        List<SocialPublicEntity> data = getPublicDataTenVo.getData();
                        if (isRefresh) {
                            entityList.clear();
                        }
                        if (data.size() < 10 || data.size()==0) {
                            page--;
                            noMore = true;
                        } else {
                            noMore = false;
                        }
                        if (!isRefresh) {
                            for (SocialPublicEntity item : data) {
                                if (entityList.contains(item)) {
                                    socialAdapter.remove(item);
                                }
                            }
                        }
                        socialAdapter.addData(data);
                        ShareApplication.getInstance().getConfig().setObject(Config.SOCIAL_PUBLISH, data);
                        if (srf==null)
                            return;
                        if (isRefresh) {
                            srf.finishRefresh();
                        } else {
                            srf.finishLoadMore();
                        }
                    }

                    @Override
                    public void onFailed(String msg) {
                        page--;
                        ToastUtil.showShortMessage(getContext(), msg);
                        if (isRefresh) {
                            srf.finishRefresh();
                        } else {
                            srf.finishLoadMore();
                        }
                    }
                });
    }

    private void initRecyView() {
        ArrayList<SocialPublicEntity> list = ShareApplication.getInstance().getConfig().getArrayList(Config.SOCIAL_PUBLISH, SocialPublicEntity.class);
        if (list!=null){
            entityList.addAll(list);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        cyclerView.setLayoutManager(linearLayoutManager);

        socialAdapter = new PublicSocialAdapter(R.layout.social_public_item_layout, entityList, service);

        cyclerView.setAdapter(socialAdapter);

        socialAdapter.setEmptyView(emptyView);
        socialAdapter.addChildClickViewIds(R.id.ll_share_music, R.id.ll_share_people, R.id.ll_good,
                R.id.iv_review, R.id.iv_share, R.id.iv_more, R.id.rl_play, R.id.iv1, R.id.iv2, R.id.iv3, R.id.iv4, R.id.iv_img);
        socialAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                switch (view.getId()) {
                    case R.id.ll_share_people:
                        if (entityList.get(position).getUserId().equals(ShareApplication.user.getUserId())) {
                            EventBus.getDefault().post(new ChangeFragmentEvent(MainActivity.PAGE_MINE));
                            return;
                        }
                        if (entityList.get(position).getIsPublic()==Constants.PEOPLE_STEALTH)
                            return;
                        Intent intent1 = new Intent(getContext(), PeopleProfileActivity.class);
                        intent1.putExtra("peopleId", entityList.get(position).getUserId());
                        intent1.putExtra("from", "public");
                        startActivity(intent1);
                        break;
                    case R.id.ll_share_music:
                        SocialPublicEntity socialPublicEntity = entityList.get(position);
                        Intent intent = new Intent(getContext(), PlayerSongActivity.class);
                        intent.putExtra("url", socialPublicEntity.getShareUrl());
                        startActivity(intent);
                        break;
                    case R.id.ll_good:
                        List<GoodsEntity> goodsList = entityList.get(position).getGoodsList();
                        boolean ishave = false;
                        for (int i = 0; i < goodsList.size(); i++) {
                            GoodsEntity goodsEntity = goodsList.get(i);
                            if (goodsEntity.getPeopleId().equals(ShareApplication.user.getUserId())) {
                                service.goodsCancel(goodsEntity.getGoodsId())
                                        .compose(RxSchedulers.<BaseResult>compose(getContext()))
                                        .subscribe(new BaseObserver<BaseResult>() {
                                            @Override
                                            public void onSuccess(BaseResult baseResult) {
                                                entityList.get(position).getGoodsList().remove(goodsEntity);
                                                adapter.notifyItemChanged(position, "goods");
                                            }

                                            @Override
                                            public void onFailed(String msg) {
                                                ToastUtil.showShortMessage(getContext(), "取消失败");
                                            }
                                        });
                                ishave = true;
                                break;
                            }
                        }
                        if (!ishave) {
                            GoodsEntity goodsEntity = new GoodsEntity();
                            goodsEntity.setPeopleId(ShareApplication.user.getUserId());
                            goodsEntity.setPeopleName(ShareApplication.user.getUserName());
                            goodsEntity.setPeopleHead(ShareApplication.user.getHeadImg());
                            goodsEntity.setPublicId(entityList.get(position).getShareId());
                            service.goodsAdd(goodsEntity)
                                    .compose(RxSchedulers.<GoodsResultVo>compose(getContext()))
                                    .subscribe(new BaseObserver<GoodsResultVo>() {
                                        @Override
                                        public void onSuccess(GoodsResultVo goodsResultVo) {
                                            entityList.get(position).getGoodsList().add(goodsResultVo.getData());
                                            adapter.notifyItemChanged(position, "goods");
//                                            ToastUtil.showShortMessage(getContext(), "点赞成功");
                                        }

                                        @Override
                                        public void onFailed(String msg) {
                                            ToastUtil.showShortMessage(getContext(), "点赞失败");
                                        }
                                    });
                        }
                        break;
                    case R.id.iv_review:
                        Intent detailIntent = new Intent(getContext(), ShareDetailActivity.class);
                        detailIntent.putExtra("shareId", entityList.get(position).getShareId());
                        detailIntent.putExtra("position", position);
                        toPos = position;
                        shareId = entityList.get(position).getShareId();
                        startActivityForResult(detailIntent, CODE_TODETAIL);
                        break;
                    case R.id.iv_share:
                        break;
                    case R.id.iv_more:
                        showChoose(entityList.get(position), position);
                        break;
                    case R.id.rl_play:
                        VideoPreviewDialog dialog2 = new VideoPreviewDialog(getContext());
                        String path = entityList.get(position).getShareUrl();
                        dialog2.setVideo(NetWorkService.homeUrl+path);
                        dialog2.show();
                        break;
                    case R.id.iv_img:
                    case R.id.iv1:
                        showPreImg(entityList.get(position),0);
                        break;
                    case R.id.iv2:
                        showPreImg(entityList.get(position),1);
                        break;
                    case R.id.iv3:
                        showPreImg(entityList.get(position),2);
                        break;
                    case R.id.iv4:
                        showPreImg(entityList.get(position),3);
                        break;
                }
            }
        });

        cyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                Log.d("滑动：", "newState=" + newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                //dy>0向下滚动 dy<0向上滚动
                if (dy < 0) {
                    mHandler.removeCallbacks(runnable);
                    mHandler.postDelayed(runnable, 1500);
                    llToTop.setVisibility(View.VISIBLE);
                    rlMsg.setVisibility(View.GONE);
                }
            }
        });
    }

    private void showPreImg(SocialPublicEntity publicEntity, int pos){
        List<String> list = new ArrayList<>();
        String[] split = publicEntity.getShareUrl().split(";");
        for (String str:split){
            list.add(str);
        }
        ImgPreviewDialog dialog = new ImgPreviewDialog(getContext(), list);
        dialog.setPhotoViewClick(new ImgPreviewDialog.PhotoViewClick() {
            @Override
            public void ImgClick() {
                dialog.dismiss();
            }
        });
        dialog.setShowPos(pos);
        dialog.show();
    }
    private void showChoose(SocialPublicEntity socialPublicEntity, int position) {
        ClickMenuView menuView = new ClickMenuView(getContext());
        if (!socialPublicEntity.getUserId().equals(ShareApplication.user.getUserId())) {
            menuView.setShowItemDelete(false);
        }
        menuView.setClickListener(new ClickMenuView.ItemClickListener() {
            @Override
            public void cancel() {
                uiPopWinUtil.dismissMenu();
            }

            @Override
            public void delete() {
                deletePublish(socialPublicEntity, position);
                uiPopWinUtil.dismissMenu();
            }

            @Override
            public void report() {
                Intent intent = new Intent(getActivity(), ReportActivity.class);
                intent.putExtra("publishId",socialPublicEntity.getShareId());
                startActivity(intent);
                uiPopWinUtil.dismissMenu();
            }

            @Override
            public void replay() {

            }
        });
        uiPopWinUtil.showPopupBottom(menuView.getView(), R.id.fl_public);
    }

    private void deletePublish(SocialPublicEntity socialPublicEntity, int itemPos) {
        service.pulishPublic(socialPublicEntity.getShareId())
                .compose(RxSchedulers.<BaseResult>compose(getContext()))
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult baseResult) {
                        if (socialAdapter==null)
                            return;
                        socialAdapter.removeAt(itemPos);
                    }

                    @Override
                    public void onFailed(String msg) {
                        ToastUtil.showShortMessage(getContext(), msg);
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case CODE_TODETAIL:
                getData(shareId);
                break;
            case CODE_TOMSG:
                loadMsgCount();
                break;
        }
    }

    private void getData(String shareId) {
        service.getByShareId(shareId)
                .compose(RxSchedulers.<GetPublicDataShareIdVo>compose(getContext()))
                .subscribe(new BaseObserver<GetPublicDataShareIdVo>() {
                    @Override
                    public void onSuccess(GetPublicDataShareIdVo getPublicDataShareIdVo) {
                        if (entityList==null)
                            return;
                        entityList.set(toPos, getPublicDataShareIdVo.getData());
                        socialAdapter.notifyItemChanged(toPos);
                    }

                    @Override
                    public void onFailed(String msg) {
                        ToastUtil.showShortMessage(getContext(), msg);
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void refreshData(RefreshPublicData refreshPublicData) {
        if (refreshPublicData != null) {
            initData(refreshPublicData.isRefresh);
        }
    }

    @Subscribe
    public void refreshSuccessData(UpLoadSocialSuccess event) {
        if (event != null) {
            socialAdapter.addData(0, event.socialPublicEntity);
        }
    }

    @OnClick({R.id.iv_add, R.id.ll_to_top, R.id.rl_msg})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_add:
                goShare();
                break;
            case R.id.ll_to_top:
                cyclerView.smoothScrollToPosition(0);
                break;
            case R.id.rl_msg:
                Intent intent = new Intent(getContext(), PublishMsgActivity.class);
                startActivityForResult(intent, CODE_TOMSG);
//                ToastUtil.showShortMessage(getContext(), "显示消息");
                break;
        }
    }

    private void goShare(){
        Intent intent1 = new Intent(getContext(), ShareActivity.class);
        ShareDialog dialog = new ShareDialog(getContext());
        dialog.setClickListener(new ShareDialog.ClickListener() {
            @Override
            public void textClick() {
                intent1.putExtra("shareType", Constants.SHARE_TEXT);
                startActivity(intent1);
                dialog.dismiss();
            }

            @Override
            public void musicClick() {
                intent1.putExtra("shareType", Constants.SHARE_MUSIC);
                startActivity(intent1);
                dialog.dismiss();
            }

            @Override
            public void cameraClick() {
                intent1.putExtra("shareType", Constants.SHARE_VIDEO);
                startActivity(intent1);
                dialog.dismiss();
            }

            @Override
            public void albumClick() {
                intent1.putExtra("shareType", Constants.SHARE_PIC);
                startActivity(intent1);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    @Subscribe
    public void newReviewMsg(NewReviewEvent event){
        if (event!=null){
            loadMsgCount();
        }
    }
}