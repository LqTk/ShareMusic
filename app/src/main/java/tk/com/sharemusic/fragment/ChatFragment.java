package tk.com.sharemusic.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
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
import butterknife.Unbinder;
import tk.com.sharemusic.R;
import tk.com.sharemusic.ShareApplication;
import tk.com.sharemusic.activity.ChatActivity;
import tk.com.sharemusic.activity.LoginActivity;
import tk.com.sharemusic.adapter.ChatListAdapter;
import tk.com.sharemusic.entity.ChatEntity;
import tk.com.sharemusic.entity.User;
import tk.com.sharemusic.enums.Gender;
import tk.com.sharemusic.event.RefreshChatListEvent;
import tk.com.sharemusic.myview.CircleImage;
import tk.com.sharemusic.network.HttpMethod;
import tk.com.sharemusic.network.NetWorkService;
import tk.com.sharemusic.network.RxSchedulers;
import tk.com.sharemusic.network.response.ChatListVo;
import tk.com.sharemusic.network.rxjava.BaseObserver;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.iv_user_head)
    CircleImage ivUserHead;
    @BindView(R.id.tv_user_name)
    TextView tvUserName;
    @BindView(R.id.rcv_chat)
    RecyclerView rcvChat;
    @BindView(R.id.srf)
    SmartRefreshLayout srf;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Unbinder bind;
    private NetWorkService service;
    private List<ChatEntity> chatLists = new ArrayList<>();
    private ChatListAdapter chatListAdapter;
    private RequestOptions options;
    private User user;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SocialPublic.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();
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
        View v = inflater.inflate(R.layout.layout_chat_fragment, container, false);
        bind = ButterKnife.bind(this, v);
        EventBus.getDefault().register(this);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        service = HttpMethod.getInstance().create(NetWorkService.class);
        initView();
        initData();
    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcvChat.setLayoutManager(linearLayoutManager);
        chatListAdapter = new ChatListAdapter(R.layout.layout_chat_list_item, chatLists);
        rcvChat.setAdapter(chatListAdapter);
        chatListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("partnerId",chatLists.get(position).senderId);
                startActivity(intent);
            }
        });

        srf.setEnableRefresh(true);
        srf.setEnableLoadMore(true);
        srf.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadData(true);
            }
        });
    }

    private void reLogin() {
        if (user == null) {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
            getActivity().finish();
            return;
        }
    }

    private void initData() {
        user = ShareApplication.getUser();
        reLogin();
        if (user.getSex() == null) {
            user.setSex(0);
        }

        options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.default_head_boy);

        Glide.with(getContext())
                .load(TextUtils.isEmpty(user.getHeadImg()) ? Gender.getImage(user.getSex()) : NetWorkService.homeUrl + user.getHeadImg())
                .apply(options)
                .into(ivUserHead);
        if (!TextUtils.isEmpty(user.getUserName())) {
            tvUserName.setText(user.getUserName());
        }
        loadData(true);
    }

    private void loadData(boolean isrefresh) {
        reLogin();
        service.getAllChat(user.getUserId())
                .compose(RxSchedulers.<ChatListVo>compose(getContext()))
                .subscribe(new BaseObserver<ChatListVo>() {
                    @Override
                    public void onSuccess(ChatListVo chatListVo) {
                        srf.finishRefresh();
                        List<ChatEntity> dataVo = chatListVo.getData();
                        if (isrefresh){
                            chatLists.clear();
                        }
                        chatLists.addAll(dataVo);
                        chatListAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailed(String msg) {
                        Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
                        srf.finishRefresh();
                    }
                });
    }

    @Subscribe
    public void refreshData(RefreshChatListEvent event){
        if (event!=null){
            loadData(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bind.unbind();
        EventBus.getDefault().unregister(this);
    }
}