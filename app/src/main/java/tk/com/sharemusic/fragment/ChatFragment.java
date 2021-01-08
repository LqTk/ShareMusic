package tk.com.sharemusic.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
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
import tk.com.sharemusic.entity.MsgEntity;
import tk.com.sharemusic.entity.User;
import tk.com.sharemusic.enums.Gender;
import tk.com.sharemusic.event.MyChatEntityEvent;
import tk.com.sharemusic.event.RefreshChatListEvent;
import tk.com.sharemusic.myview.CircleImage;
import tk.com.sharemusic.network.HttpMethod;
import tk.com.sharemusic.network.NetWorkService;
import tk.com.sharemusic.network.RxSchedulers;
import tk.com.sharemusic.network.response.ChatListVo;
import tk.com.sharemusic.network.rxjava.BaseObserver;
import tk.com.sharemusic.utils.Config;
import tk.com.sharemusic.utils.PreferenceConfig;
import tk.com.sharemusic.utils.SaveFileUtil;

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
    private PreferenceConfig preferenceConfig;
    private View emptyView;
    private TextView tvEmptyDes;
    private ImageView ivShow;

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
        emptyView = LayoutInflater.from(getContext()).inflate(R.layout.layout_empty,null);
        tvEmptyDes = emptyView.findViewById(R.id.tv_empty_des);
        ivShow = emptyView.findViewById(R.id.iv_show);
        ivShow.setImageResource(R.drawable.chat_bg);
        tvEmptyDes.setText("还没有和好友聊天~\n快去和好友聊聊吧~");

        preferenceConfig = ShareApplication.getInstance().getConfig();
        user = ShareApplication.getUser();
        reLogin();
        if (user.getSex() == null) {
            user.setSex(0);
        }

        chatLists = preferenceConfig.getArrayList(user.getUserId(),ChatEntity.class);

        initView();
        initData();
    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcvChat.setLayoutManager(linearLayoutManager);
        chatListAdapter = new ChatListAdapter(R.layout.layout_chat_list_item, chatLists);
        rcvChat.setAdapter(chatListAdapter);
        chatListAdapter.setEmptyView(emptyView);
        chatListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("partnerId",chatLists.get(position).senderId);
                startActivity(intent);
            }
        });

        chatListAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        preferenceConfig.remove(user.getUserId()+chatLists.get(position).getSenderId()+Config.CHAT_PARTNER_LIST);
                        File file = SaveFileUtil.getVoiceFile(getContext(),user.getUserId(),chatLists.get(position).getSenderId());
                        if (file.exists()){
                            for (File file1:file.listFiles()){
                                file1.delete();
                            }
                            file.delete();
                        }
                        chatListAdapter.removeAt(position);
                        saveData();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setTitle("提示");
                builder.setMessage("是否删除与"+chatLists.get(position).senderName+"聊天记录?\n删除后将不可恢复");
                builder.show();
                return false;
            }
        });

        srf.setEnableRefresh(true);
        srf.setEnableLoadMore(true);
        srf.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadData(true,"");
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
        loadData(true,"");
    }

    private void loadData(boolean isrefresh, String talkId) {
        reLogin();
        service.getAllChat(user.getUserId())
                .compose(RxSchedulers.<ChatListVo>compose(getContext()))
                .subscribe(new BaseObserver<ChatListVo>() {
                    @Override
                    public void onSuccess(ChatListVo chatListVo) {
                        srf.finishRefresh();
                        List<ChatEntity> dataVo = chatListVo.getData();
                        for (ChatEntity chatEntity:dataVo){
                            boolean isHave = false;
                            for (int i = 0; i < chatLists.size(); i++) {
                                ChatEntity chatEntity1 = chatLists.get(i);
                                if (!TextUtils.isEmpty(talkId)){
                                    if (chatEntity1.getSenderId().equals(talkId)) {
                                        chatEntity1.setMsgContent(chatEntity.msgContent);
                                        chatEntity1.setMsgType(chatEntity.msgType);
                                        if (i != 0) {
                                            chatListAdapter.remove(chatEntity1);
                                            chatListAdapter.addData(0, chatEntity1);
                                        } else {
                                            chatListAdapter.notifyItemChanged(i, "des");
                                        }
                                        isHave = true;
                                        break;
                                    }
                                }else {
                                    if (chatEntity1.getSenderId().equals(chatEntity.getSenderId())) {
                                        chatEntity1.setMsgContent(chatEntity.msgContent);
                                        chatEntity1.setMsgType(chatEntity.msgType);
                                        if (i != 0) {
                                            chatListAdapter.remove(chatEntity1);
                                            chatListAdapter.addData(0, chatEntity1);
                                        } else {
                                            chatListAdapter.notifyItemChanged(i, "des");
                                        }
                                        isHave = true;
                                        break;
                                    }
                                }
                            }
                            if (!isHave) {
                                chatListAdapter.addData(0, chatEntity);
                            }
                        }
                        saveData();
                    }

                    @Override
                    public void onFailed(String msg) {
                        Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
                        srf.finishRefresh();
                    }
                });
    }

    private void saveData(){
        preferenceConfig.setObject(user.getUserId(),chatLists);
    }

    @Subscribe
    public void refreshData(RefreshChatListEvent event){
        if (event!=null){
            loadData(false,event.talkId);
        }
    }

    @Subscribe
    public void addMyChatEntity(MyChatEntityEvent entityEvent){
        if (entityEvent!=null){
            ChatEntity entity = entityEvent.chatEntity;
            MsgEntity partner = entityEvent.partner;
            boolean ishave = false;
            for (int i=0;i<chatLists.size();i++){
                ChatEntity chatEntity1 = chatLists.get(i);
                if (chatEntity1.getSenderId().equals(partner.getPeopleId())){
                    chatEntity1.setMsgType(entity.msgType);
                    chatEntity1.setMsgContent(entity.msgContent);
                    if (i != 0) {
                        chatListAdapter.remove(chatEntity1);
                        chatListAdapter.addData(0, chatEntity1);
                    } else {
                        chatListAdapter.notifyItemChanged(i, "des");
                    }
                    ishave = true;
                    break;
                }
            }
            if (!ishave){
                ChatEntity chatEntity = new ChatEntity(entity.chatId,entity.msgContent,entity.msgType,
                        entity.voiceTime,partner.getPeopleId(),partner.getPeopleHead(),partner.getPeopleName(),entity.chatTime);
                chatListAdapter.addData(0,chatEntity);
            }
            saveData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bind.unbind();
        EventBus.getDefault().unregister(this);
    }
}