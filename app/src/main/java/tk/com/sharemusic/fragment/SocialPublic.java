package tk.com.sharemusic.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import tk.com.sharemusic.R;
import tk.com.sharemusic.activity.PeopleProfileActivity;
import tk.com.sharemusic.activity.PlayerSongActivity;
import tk.com.sharemusic.activity.ShareActivity;
import tk.com.sharemusic.adapter.PublicSocialAdapter;
import tk.com.sharemusic.entity.SocialPublicEntity;
import tk.com.sharemusic.event.RefreshPublicData;
import tk.com.sharemusic.event.UpLoadSocialSuccess;
import tk.com.sharemusic.network.HttpMethod;
import tk.com.sharemusic.network.NetWorkService;
import tk.com.sharemusic.network.RxSchedulers;
import tk.com.sharemusic.network.response.GetPublicDataTenVo;
import tk.com.sharemusic.network.rxjava.BaseObserver;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SocialPublic#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SocialPublic extends Fragment {

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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Unbinder unbinder;
    private PublicSocialAdapter socialAdapter;
    private List<SocialPublicEntity> entityList = new ArrayList<>();
    private NetWorkService service;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0:
                    socialAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    private boolean noMore = false;

    public SocialPublic() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SocialPublic.
     */
    // TODO: Rename and change types and number of parameters
    public static SocialPublic newInstance() {
        SocialPublic fragment = new SocialPublic();
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
        initView();
        initRecyView();
        initData(true);
    }

    private void initView() {
        srf.setEnableRefresh(true);
        srf.setEnableLoadMore(true);
        srf.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                initData(true);
            }
        });
        srf.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (noMore){
                    Toast.makeText(getContext(),"没有更多了...",Toast.LENGTH_SHORT).show();
                    srf.finishLoadMore();
                    return;
                }
                initData(false);
            }
        });
    }

    private void initData(boolean isRefresh) {
        service.getTenDatas()
                .compose(RxSchedulers.<GetPublicDataTenVo>compose(getContext()))
                .subscribe(new BaseObserver<GetPublicDataTenVo>() {
                    @Override
                    public void onSuccess(GetPublicDataTenVo getPublicDataTenVo) {
                        List<SocialPublicEntity> data = getPublicDataTenVo.getData();
                        if (isRefresh) {
                            entityList.clear();
                        }
                        if (data.size()<10){
                            noMore = true;
                        }else {
                            noMore = false;
                        }
                        entityList.addAll(data);
                        if (isRefresh) {
                            srf.finishRefresh();
                        } else {
                            srf.finishLoadMore();
                        }
                        mHandler.sendEmptyMessage(0);
                    }

                    @Override
                    public void onFailed(String msg) {
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                        if (isRefresh) {
                            srf.finishRefresh();
                        } else {
                            srf.finishLoadMore();
                        }
                    }
                });
    }

    private void initRecyView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        cyclerView.setLayoutManager(linearLayoutManager);

        socialAdapter = new PublicSocialAdapter(R.layout.social_public_item_layout, entityList);

        cyclerView.setAdapter(socialAdapter);

        socialAdapter.addChildClickViewIds(R.id.ll_share_content,R.id.ll_share_people);
        socialAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                switch (view.getId()){
                    case R.id.ll_share_people:
                        Intent intent1 = new Intent(getContext(), PeopleProfileActivity.class);
                        intent1.putExtra("peopleId", entityList.get(position).getUserid());
                        startActivity(intent1);
                        break;
                    case R.id.ll_share_content:
                        SocialPublicEntity socialPublicEntity = entityList.get(position);
                        Intent intent = new Intent(getContext(), PlayerSongActivity.class);
                        intent.putExtra("url", socialPublicEntity.getShareurl());
                        startActivity(intent);
                        break;
                }
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
            entityList.add(event.socialPublicEntity);
            mHandler.sendEmptyMessage(0);
        }
    }

    @OnClick(R.id.iv_add)
    public void onViewClicked() {
        Intent intent1 = new Intent(getContext(), ShareActivity.class);
        intent1.putExtra("sharetext","");
        startActivity(intent1);
    }
}