package tk.com.sharemusic.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import tk.com.sharemusic.R;
import tk.com.sharemusic.ShareApplication;
import tk.com.sharemusic.activity.ChatActivity;
import tk.com.sharemusic.adapter.PartnerAdapter;
import tk.com.sharemusic.entity.MsgEntity;
import tk.com.sharemusic.entity.User;
import tk.com.sharemusic.event.NotifyPartInfoEvent;
import tk.com.sharemusic.event.RefreshPartnerEvent;
import tk.com.sharemusic.network.HttpMethod;
import tk.com.sharemusic.network.NetWorkService;
import tk.com.sharemusic.network.RxSchedulers;
import tk.com.sharemusic.network.response.PartnerVo;
import tk.com.sharemusic.network.response.PeopleVo;
import tk.com.sharemusic.network.rxjava.BaseObserver;
import tk.com.sharemusic.utils.Config;
import tk.com.sharemusic.utils.ToastUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PartnerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PartnerFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.rcv_part)
    RecyclerView rcvPart;
    @BindView(R.id.srf_content)
    SmartRefreshLayout srfContent;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Unbinder bind;
    private NetWorkService service;
    private PartnerAdapter adapter;
    private View emptyView;
    private List<MsgEntity> peopleEntities = new ArrayList<>();
    private TextView tvEmptyDes;
    private ImageView ivShow;

    public PartnerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PartnerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PartnerFragment newInstance() {
        PartnerFragment fragment = new PartnerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
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
        View v = inflater.inflate(R.layout.fragment_partner, container, false);
        bind = ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        service = HttpMethod.getInstance().create(NetWorkService.class);
        emptyView = LayoutInflater.from(getContext()).inflate(R.layout.layout_empty, null);
        tvEmptyDes = emptyView.findViewById(R.id.tv_empty_des);
        ivShow = emptyView.findViewById(R.id.iv_show);
        ivShow.setImageResource(R.drawable.partner_bg);
        tvEmptyDes.setText("暂时没有好友哟~\n快去添加吧");

        EventBus.getDefault().register(this);
        initView();
        initData();
    }

    private void initData() {
        User user = ShareApplication.getUser();
        if (user == null) {
            ToastUtil.showShortMessage(getContext(), "请先登录");
            return;
        }
        service.getPartnerInfo(user.getUserId())
                .compose(RxSchedulers.<PartnerVo>compose(getContext()))
                .subscribe(new BaseObserver<PartnerVo>() {
                    @Override
                    public void onSuccess(PartnerVo peopleVo) {
                        List<MsgEntity> data = peopleVo.getData();
                        if (data != null) {
                            peopleEntities.clear();
                            adapter.addData(data);
                        }
                        savePartner();
                    }

                    @Override
                    public void onFailed(String msg) {
                        ToastUtil.showShortMessage(getContext(), msg);
                    }
                });
    }

    private void initView() {
        srfContent.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                initData();
                srfContent.finishRefresh();
            }
        });


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rcvPart.setLayoutManager(linearLayoutManager);

        peopleEntities = ShareApplication.getInstance().getConfig().getArrayList(Config.PARTNER_DATA,MsgEntity.class);
        if (peopleEntities==null){
            peopleEntities = new ArrayList<>();
        }
        adapter = new PartnerAdapter(R.layout.partner_item_layout, peopleEntities);
        rcvPart.setAdapter(adapter);

        adapter.setEmptyView(emptyView);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("partnerId", peopleEntities.get(position).getPeopleId());
                intent.putExtra("partnerName", TextUtils.isEmpty(peopleEntities.get(position).getSetName())?peopleEntities.get(position).getPeopleName():peopleEntities.get(position).getSetName());
                intent.putExtra("head",peopleEntities.get(position).getPeopleHead());
                startActivity(intent);
            }
        });
    }

    @Subscribe
    public void refreshData(RefreshPartnerEvent refreshPartnerEvent) {
        if (refreshPartnerEvent != null) {
            initData();
        }
    }

    @Subscribe
    public void notifyPartnerInfo(NotifyPartInfoEvent event) {
        if (event != null) {
            int i = 0;
            for (MsgEntity msgEntity : peopleEntities) {
                if (msgEntity.getPeopleId().equals(event.partnerId)) {
                    int finalI = i;
                    Map map = new HashMap();
                    map.put("userId",ShareApplication.user.getUserId());
                    map.put("peopleId",event.partnerId);
                    service.getPeopleInfo(map)
                            .compose(RxSchedulers.<PeopleVo>compose(getContext()))
                            .subscribe(new BaseObserver<PeopleVo>(getContext()) {
                                @Override
                                public void onSuccess(PeopleVo peopleVo) {
                                    adapter.setData(finalI, peopleVo.getData());
                                    savePartner();
                                }

                                @Override
                                public void onFailed(String msg) {

                                }
                            });
                }
                i++;
            }
        }
    }

    private void savePartner(){
        ShareApplication.getInstance().getConfig().setObject(Config.PARTNER_DATA,peopleEntities);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bind.unbind();
        EventBus.getDefault().unregister(this);
    }
}