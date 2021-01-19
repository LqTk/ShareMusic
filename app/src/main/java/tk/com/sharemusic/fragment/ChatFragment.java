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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import tk.com.sharemusic.R;
import tk.com.sharemusic.ShareApplication;
import tk.com.sharemusic.activity.ChatActivity;
import tk.com.sharemusic.activity.LoginActivity;
import tk.com.sharemusic.activity.MainActivity;
import tk.com.sharemusic.adapter.ChatListAdapter;
import tk.com.sharemusic.entity.ChatEntity;
import tk.com.sharemusic.entity.MsgEntity;
import tk.com.sharemusic.entity.User;
import tk.com.sharemusic.enums.Gender;
import tk.com.sharemusic.event.MsgCountEvent;
import tk.com.sharemusic.event.MsgReadEvent;
import tk.com.sharemusic.event.MyChatEntityEvent;
import tk.com.sharemusic.event.NotifyPartInfoEvent;
import tk.com.sharemusic.event.RefreshChatListEvent;
import tk.com.sharemusic.event.RefreshMyInfoEvent;
import tk.com.sharemusic.myview.CircleImage;
import tk.com.sharemusic.network.HttpMethod;
import tk.com.sharemusic.network.NetWorkService;
import tk.com.sharemusic.network.RxSchedulers;
import tk.com.sharemusic.network.response.ChatListVo;
import tk.com.sharemusic.network.response.PeopleVo;
import tk.com.sharemusic.network.rxjava.BaseObserver;
import tk.com.sharemusic.utils.Config;
import tk.com.sharemusic.utils.PreferenceConfig;
import tk.com.sharemusic.utils.SaveFileUtil;
import tk.com.sharemusic.utils.ToastUtil;

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
        if (user==null){
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            ToastUtil.showShortMessage(getContext(),"请先登录");
            getActivity().finish();
            return;
        }
        if (user.getSex() == null) {
            user.setSex(0);
        }

        chatLists = preferenceConfig.getArrayList(user.getUserId(),ChatEntity.class);

        if (chatLists!=null && chatLists.size()>0){
            notifyChatLists();
        }

        initView();
        initData();
    }

    private void notifyChatLists() {
        for (int i=0;i<chatLists.size();i++) {
            ChatEntity chatEntity = chatLists.get(i);
            int finalI = i;

            Map map = new HashMap();
            map.put("userId",ShareApplication.user.getUserId());
            map.put("peopleId",chatEntity.senderId);
            service.getPeopleInfo(map)
                    .compose(RxSchedulers.<PeopleVo>compose(getContext()))
                    .subscribe(new BaseObserver<PeopleVo>() {
                        @Override
                        public void onSuccess(PeopleVo peopleVo) {
                            boolean change = false;
                            if (!peopleVo.getData().getPeopleName().equals(chatEntity.senderName)){
                                chatEntity.setSenderName(peopleVo.getData().getPeopleName());
                                change = true;
                            }
                            if (!peopleVo.getData().getPeopleHead().equals(chatEntity.senderAvatar)){
                                chatEntity.setSenderAvatar(peopleVo.getData().getPeopleHead());
                                change = true;
                            }
                            if (change) {
                                chatListAdapter.notifyItemChanged(finalI);
                                EventBus.getDefault().post(new NotifyPartInfoEvent(peopleVo.getData().getPeopleId()));
                            }
                        }

                        @Override
                        public void onFailed(String msg) {

                        }
                    });
        }
    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcvChat.setLayoutManager(linearLayoutManager);
        chatListAdapter = new ChatListAdapter(R.layout.layout_chat_list_item, chatLists);
        EventBus.getDefault().post(new MsgCountEvent(MainActivity.PAGE_MESSAGE,chatListAdapter.getAllMsgCount()));
        rcvChat.setAdapter(chatListAdapter);
        chatListAdapter.setEmptyView(emptyView);
        chatListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                chatListAdapter.getItem(position).count=0;
                chatListAdapter.notifyItemChanged(position,"count");
                EventBus.getDefault().post(new MsgCountEvent(MainActivity.PAGE_MESSAGE,chatListAdapter.getAllMsgCount()));
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
                        //如果未读消息大于0表示服务器有未读消息 通过读取指定好友的具体聊天信息来删除未读消息
                        if (chatLists.get(position).count>0) {
                            deleteMsg(chatLists.get(position).senderId);
                        }
                        preferenceConfig.remove(user.getUserId()+chatLists.get(position).getSenderId()+Config.CHAT_PARTNER_LIST);
                        File file = SaveFileUtil.getVoiceFile(getContext(),user.getUserId(),chatLists.get(position).getSenderId());
                        if (file.exists()){
                            for (File file1:file.listFiles()){
                                file1.delete();
                            }
                            file.delete();
                        }
                        chatListAdapter.removeAt(position);
                        EventBus.getDefault().post(new MsgCountEvent(MainActivity.PAGE_MESSAGE,chatListAdapter.getAllMsgCount()));
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
                builder.setMessage("是否删除与"+chatLists.get(position).senderName+"的聊天记录?\n删除后将不可恢复");
                builder.show();
                return false;
            }
        });

        srf.setEnableRefresh(true);
        srf.setEnableLoadMore(true);
        srf.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                notifyChatLists();
                loadData();
            }
        });
    }

    private void deleteMsg(String partnerId) {
        Map map = new HashMap();
        map.put("userId",ShareApplication.user.getUserId());
        map.put("partnerId",partnerId);
        service.getPartnerChat(map)
                .compose(RxSchedulers.<ChatListVo>compose(getContext()))
                .subscribe(new BaseObserver<ChatListVo>() {
                    @Override
                    public void onSuccess(ChatListVo chatListVo) {

                    }

                    @Override
                    public void onFailed(String msg) {
                    }
                });
    }

    private void reLogin() {
        if (user == null) {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            ToastUtil.showShortMessage(getContext(),"请先登录");
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
        loadData();
    }

    /**
     * 加载所有聊天消息
     */
    private void loadData() {
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
                                if (chatEntity1.getSenderId().equals(chatEntity.getSenderId())) {
                                    isHave = true;
                                    boolean nameChange = false;
                                    boolean headChange = false;
                                    boolean contentChange = false;
                                    if (!chatEntity1.getSenderName().equals(chatEntity.senderName)) {
                                        chatEntity1.setSenderName(chatEntity.senderName);
                                        nameChange = true;
                                    }
                                    if (!chatEntity1.getSenderAvatar().equals(chatEntity.senderAvatar)) {
                                        chatEntity1.setSenderAvatar(chatEntity.senderAvatar);
                                        headChange = true;
                                    }
                                    //如果获取的聊天时间大于已获得的聊天时间，表示有新的消息，刷新列表
                                    if (chatEntity.chatTime>chatEntity1.chatTime) {
                                        contentChange = true;
                                        chatEntity1.setMsgContent(chatEntity.msgContent);
                                        chatEntity1.setChatTime(chatEntity.chatTime);
                                        chatEntity1.setCount(chatEntity1.count + 1);
                                        chatEntity1.setMsgType(chatEntity.msgType);
                                    }
                                    if (nameChange||headChange){
                                        EventBus.getDefault().post(new NotifyPartInfoEvent(chatEntity.senderId));
                                    }
                                    if (nameChange || headChange || contentChange){
                                        if (i != 0) {
                                            chatListAdapter.remove(chatEntity1);
                                            chatListAdapter.addData(0, chatEntity1);
                                        } else {
                                            if (nameChange && headChange){
                                                chatListAdapter.notifyItemChanged(i);
                                            }else if (nameChange){
                                                chatListAdapter.notifyItemChanged(i, "desName");
                                            }else if (headChange){
                                                chatListAdapter.notifyItemChanged(i, "desAvr");
                                            }else {
                                                chatListAdapter.notifyItemChanged(i, "des");
                                            }
                                        }
                                    }
                                    break;
                                }
                            }
                            if (!isHave) {
                                chatEntity.setCount(chatEntity.count+1);
                                chatListAdapter.addData(0, chatEntity);
                            }
                        }
                        EventBus.getDefault().post(new MsgCountEvent(MainActivity.PAGE_MESSAGE,chatListAdapter.getAllMsgCount()));
                        saveData();
                    }

                    @Override
                    public void onFailed(String msg) {
                        ToastUtil.showShortMessage(getContext(),msg);
                        srf.finishRefresh();
                    }
                });
    }

    /**
     * 保存消息列表
     */
    private void saveData(){
        preferenceConfig.setObject(user.getUserId(),chatLists);
    }

    /**
     * 接收到新消息推送，然后加载未读消息
     * @param event
     */
    @Subscribe
    public void refreshData(RefreshChatListEvent event){
        if (event!=null){
            loadPartData(event.talkId);
        }
    }

    /**
     * 加载指定好友的未读聊天信息
     * @param talkId
     */
    private void loadPartData(String talkId) {
        HashMap map = new HashMap();
        map.put("userId",user.getUserId());
        map.put("partnerId",talkId);
        service.getSelectAllChat(map)
                .compose(RxSchedulers.<ChatListVo>compose(getContext()))
                .subscribe(new BaseObserver<ChatListVo>() {
                    @Override
                    public void onSuccess(ChatListVo chatListVo) {
                        List<ChatEntity> voData = chatListVo.getData();
                        int count = voData.size();
                        if (count<=0)
                            return;
                        ChatEntity chatEntity = voData.get(voData.size() - 1);
                        chatEntity.setCount(count);
                        int pos = isHavePos(talkId);
                        if (pos>-1){
                            ChatEntity chatEntity1 = chatLists.get(pos);
                            boolean nameChange = false;
                            boolean headChange = false;
                            if (!chatEntity1.getSenderName().equals(chatEntity.senderName)) {
                                chatEntity1.setSenderName(chatEntity.senderName);
                                nameChange = true;
                            }
                            if (!chatEntity1.getSenderAvatar().equals(chatEntity.senderAvatar)) {
                                chatEntity1.setSenderAvatar(chatEntity.senderAvatar);
                                headChange = true;
                            }
                            chatEntity1.setMsgContent(chatEntity.msgContent);
                            chatEntity1.setMsgType(chatEntity.msgType);
                            chatEntity1.setChatTime(chatEntity.chatTime);
                            chatEntity1.setCount(chatEntity1.count+1);
                            if (nameChange||headChange){
                                EventBus.getDefault().post(new NotifyPartInfoEvent(chatEntity.senderId));
                            }

                            if (pos != 0) {
                                chatListAdapter.remove(chatEntity1);
                                chatListAdapter.addData(0, chatEntity1);
                            } else {
                                if (nameChange && headChange){
                                    chatListAdapter.notifyItemChanged(pos);
                                }else if (nameChange){
                                    chatListAdapter.notifyItemChanged(pos, "desName");
                                }else if (headChange){
                                    chatListAdapter.notifyItemChanged(pos, "desAvr");
                                }else {
                                    chatListAdapter.notifyItemChanged(pos, "des");
                                }
                            }
                        }else {
                            chatListAdapter.addData(0,chatEntity);
                        }
                        EventBus.getDefault().post(new MsgCountEvent(MainActivity.PAGE_MESSAGE,chatListAdapter.getAllMsgCount()));
                        saveData();
                    }

                    @Override
                    public void onFailed(String msg) {
                        ToastUtil.showShortMessage(getContext(),msg);
                    }
                });
    }

    /**
     * 判断chatLists中是否已经有了好友聊天记录 没有返回-1 有返回对应的位置
     * @param id
     * @return
     */
    private int isHavePos(String id){
        for (ChatEntity chatEntity:chatLists){
            if (chatEntity.getSenderId().equals(id)) {
                return chatLists.indexOf(chatEntity);
            }
        }
        return -1;
    }

    /**
     * 将用户自己发送的消息添加到消息列表
     * @param entityEvent
     */
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
                    chatEntity1.setCount(0);
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

    /**
     * 更新用户头像和姓名
     * @param event
     */
    @Subscribe
    public void refreshInfo(RefreshMyInfoEvent event){
        if (event!=null){
            user = ShareApplication.user;
            Glide.with(getContext())
                    .load(TextUtils.isEmpty(user.getHeadImg()) ? Gender.getImage(user.getSex()) : NetWorkService.homeUrl + user.getHeadImg())
                    .apply(options)
                    .into(ivUserHead);
            if (!TextUtils.isEmpty(user.getUserName())) {
                tvUserName.setText(user.getUserName());
            }
        }
    }

    /**
     * 更新已读消息小红点
     * @param event
     */
    @Subscribe
    public void refreshReadMsg(MsgReadEvent event){
        if (event!=null){
            int havePos = isHavePos(event.partnerId);
            if (havePos>-1){
                chatLists.get(havePos).count=0;
                chatListAdapter.notifyItemChanged(havePos);
                EventBus.getDefault().post(new MsgCountEvent(MainActivity.PAGE_MESSAGE,chatListAdapter.getAllMsgCount()));
            }
        }
    }
}