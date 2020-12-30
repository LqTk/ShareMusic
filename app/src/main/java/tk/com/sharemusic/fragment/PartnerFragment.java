package tk.com.sharemusic.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import tk.com.sharemusic.R;
import tk.com.sharemusic.ShareApplication;
import tk.com.sharemusic.activity.ChatActivity;
import tk.com.sharemusic.adapter.PartnerAdapter;
import tk.com.sharemusic.entity.MsgEntiti;
import tk.com.sharemusic.entity.User;
import tk.com.sharemusic.event.RefreshPartnerEvent;
import tk.com.sharemusic.network.HttpMethod;
import tk.com.sharemusic.network.NetWorkService;
import tk.com.sharemusic.network.RxSchedulers;
import tk.com.sharemusic.network.response.PartnerVo;
import tk.com.sharemusic.network.rxjava.BaseObserver;

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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Unbinder bind;
    private NetWorkService service;
    private PartnerAdapter adapter;
    private List<MsgEntiti> peopleEntities = new ArrayList<>();

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

        EventBus.getDefault().register(this);
        initView();
        initData();
    }

    private void initData() {
        User user = ShareApplication.getUser();
        if (user==null){
            Toast.makeText(getContext(),"请先登录",Toast.LENGTH_SHORT).show();
            return;
        }
        service.getPartnerInfo(user.getUserId())
                .compose(RxSchedulers.<PartnerVo>compose(getContext()))
                .subscribe(new BaseObserver<PartnerVo>() {
                    @Override
                    public void onSuccess(PartnerVo peopleVo) {
                        List<MsgEntiti> data = peopleVo.getData();
                        if (data!=null) {
                            peopleEntities.clear();
                            peopleEntities.addAll(data);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailed(String msg) {
                        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rcvPart.setLayoutManager(linearLayoutManager);

        adapter = new PartnerAdapter(R.layout.partner_item_layout,peopleEntities);
        rcvPart.setAdapter(adapter);

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("partnerId",peopleEntities.get(position).getPeopleId());
                startActivity(intent);
            }
        });
    }

    @Subscribe
    public void refreshData(RefreshPartnerEvent refreshPartnerEvent){
        if (refreshPartnerEvent!=null){
            initData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bind.unbind();
        EventBus.getDefault().unregister(this);
    }
}