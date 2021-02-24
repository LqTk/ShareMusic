package tk.com.sharemusic.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.activation.MimetypesFileTypeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import tk.com.sharemusic.R;
import tk.com.sharemusic.ShareApplication;
import tk.com.sharemusic.adapter.ChatAdapter;
import tk.com.sharemusic.config.Constants;
import tk.com.sharemusic.entity.ChatEntity;
import tk.com.sharemusic.entity.HeadItem;
import tk.com.sharemusic.entity.MsgEntity;
import tk.com.sharemusic.entity.User;
import tk.com.sharemusic.event.MsgReadEvent;
import tk.com.sharemusic.event.MyChatEntityEvent;
import tk.com.sharemusic.event.NotifyItemChatAdapterEvent;
import tk.com.sharemusic.event.RefreshPartnerMsgEvent;
import tk.com.sharemusic.myview.dialog.ImgPreviewDialog;
import tk.com.sharemusic.myview.dialog.PagerMenuGridPicker;
import tk.com.sharemusic.myview.dialog.TextDialog;
import tk.com.sharemusic.myview.dialog.VideoPreviewDialog;
import tk.com.sharemusic.network.HttpMethod;
import tk.com.sharemusic.network.NetWorkService;
import tk.com.sharemusic.network.RxSchedulers;
import tk.com.sharemusic.network.response.ChatListVo;
import tk.com.sharemusic.network.response.PeopleVo;
import tk.com.sharemusic.network.response.SendMsgVo;
import tk.com.sharemusic.network.rxjava.BaseObserver;
import tk.com.sharemusic.utils.BitmapUtil;
import tk.com.sharemusic.utils.Config;
import tk.com.sharemusic.utils.DownloadVoiceManager;
import tk.com.sharemusic.utils.PopWinUtil;
import tk.com.sharemusic.utils.PreferenceConfig;
import tk.com.sharemusic.utils.SaveFileUtil;
import tk.com.sharemusic.utils.ToastUtil;

public class ChatActivity extends CommonActivity {

    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.btn_profile)
    TextView btnProfile;
    @BindView(R.id.rcv_chat)
    RecyclerView rcvChat;
    @BindView(R.id.refresh_view)
    SmartRefreshLayout refreshView;
    @BindView(R.id.icon_status)
    ImageView iconStatus;
    @BindView(R.id.ll_recorder_anim)
    LinearLayout llRecorderAnim;
    @BindView(R.id.tv_sec)
    TextView tvSec;
    @BindView(R.id.layout_recording_mask)
    RelativeLayout layoutRecordingMask;
    @BindView(R.id.tv_switch)
    ImageView tvSwitch;
    @BindView(R.id.ll_voice_text)
    LinearLayout llVoiceText;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.iv_faces)
    ImageView ivFaces;
    @BindView(R.id.iv_functions)
    ImageView ivFunctions;
    @BindView(R.id.btn_send)
    TextView btnSend;
    @BindView(R.id.layout_text)
    LinearLayout layoutText;
    @BindView(R.id.layout_full)
    LinearLayout layoutFull;
    @BindView(R.id.ll_show)
    LinearLayout llShow;
    @BindView(R.id.btn_press_to_speak)
    TextView tvRecord;

    private Unbinder bind;
    private NetWorkService service;
    private String partnerId;
    private String mode = Constants.MODE_TEXT;

    private MediaPlayer mMediaPlayer;
    private MediaRecorder mRecorder;
    private boolean isVoicePlaying = false;
    private ChatActivity mContext;
    private AnimationDrawable animation;
    private Timer timer;
    private final String fileName = "chat_voice_";
    private String tmpFileName;

    private Handler mHandler = new Handler();
    private final Handler handler = new CountdownHandler(this);
    private int seconds = 0;
    private int tmpSec = 0;
    private File iRecAudioFile;
    private PagerMenuGridPicker functionsPicker;
    PopWinUtil uiPopWinUtil;

    private String imgPath;
    private int openType;
    private MsgEntity partnerInfo;
    private User user;
    private List<ChatEntity> localChatLists = new ArrayList<>();
    private List<ChatEntity> chatLists = new ArrayList<>();
    private ChatAdapter chatAdapter;
    private PreferenceConfig preferenceConfig;
    private String partnerHead = "";
    private InputMethodManager inputManager;
    private int reSendPos;
    private List<ChatEntity> sendEntityList = new ArrayList<>();
    private List<Integer> sending = new ArrayList<>();
    int page = 0;
    private String setName;
    private final static int CODE_TOPROFILE = 1112;

    class CountdownHandler extends Handler {
        private final WeakReference<ChatActivity> mActivity;

        public CountdownHandler(ChatActivity activity) {
            this.mActivity = new WeakReference<>(activity);//使用弱引用
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ChatActivity activity = mActivity.get();
            if (activity != null) {
                activity.handle(msg);
            }
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_chat);
        bind = ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mContext = this;
        service = HttpMethod.getInstance().create(NetWorkService.class);
        preferenceConfig = ShareApplication.getInstance().getConfig();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA
                    , Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE});
        }

        user = ShareApplication.getUser();
        reLogin();
        partnerId = getIntent().getStringExtra("partnerId");
        setName = getIntent().getStringExtra("partnerName");
        if (!TextUtils.isEmpty(setName)) {
            tvName.setText(setName);
            tvTitle.setText("");
        }
        localChatLists = preferenceConfig.getArrayList(user.getUserId()+partnerId+Config.CHAT_PARTNER_LIST,ChatEntity.class);
        if (localChatLists.size()-10>page*10){
            chatLists.addAll(0,localChatLists.subList(0, 10));
        }else {
            chatLists.addAll(0,localChatLists);
        }

        if (!ShareApplication.multiSending) {
            for (ChatEntity chatEntity : chatLists) {
                int index = 0;
                if (chatEntity.isSending()) {
                    if (!ShareApplication.multiSending) {
                        chatEntity.setSending(false);
                        chatEntity.setSendSuccess(false);
                    } else if (chatEntity.isSending()) {
                        sending.add(index);
                        sendEntityList.add(chatEntity);
                    }
                }
                index++;
            }
        }

        initView();
        initData();
        initPicker();
        loadData(true);
        initPressToSpeak();
    }

    private void loadLocalData(){
        localChatLists = preferenceConfig.getArrayList(user.getUserId()+partnerId+Config.CHAT_PARTNER_LIST,ChatEntity.class);
        if (localChatLists.size()-10>page*10){
            List<ChatEntity> chatEntities = localChatLists.subList(localChatLists.size() - page * 10, localChatLists.size());
            chatLists.addAll(0,chatEntities);
            if (chatAdapter!=null){
                chatAdapter.notifyDataSetChanged();
            }
            rcvChat.scrollToPosition(chatEntities.size()+1);
        }else {
            if (localChatLists.size()>page*10) {
                List<ChatEntity> chatEntities = localChatLists.subList(page * 10, localChatLists.size());
                chatLists.addAll(0, chatEntities);
                if (chatAdapter != null) {
                    chatAdapter.notifyDataSetChanged();
                }
                rcvChat.scrollToPosition(chatEntities.size() + 1);
            }else {
                page--;
            }
        }
        refreshView.finishRefresh();
    }

    private void loadData(boolean firstLoad) {
        reLogin();
        HashMap map = new HashMap();
        map.put("userId",user.getUserId());
        map.put("partnerId",partnerId);
        service.getPartnerChat(map)
                .compose(RxSchedulers.<ChatListVo>compose(this))
                .subscribe(new BaseObserver<ChatListVo>() {
                    @Override
                    public void onSuccess(ChatListVo chatListVo) {
                        if (refreshView==null)
                            return;
                        refreshView.finishRefresh();
                        refreshView.finishLoadMore();
                        List<ChatEntity> voData = chatListVo.getData();
                        if (voData!=null && voData.size()>0) {
                            chatLists.addAll(voData);
                            localChatLists.addAll(voData);
                            saveData();
                        }
                        EventBus.getDefault().post(new MsgReadEvent(partnerId));
                        chatAdapter.notifyDataSetChanged();
                        rcvChat.smoothScrollToPosition(chatLists.size());
                    }

                    @Override
                    public void onFailed(String msg) {
                        if (refreshView==null)
                            return;
                        ToastUtil.showShortMessage(mContext,msg);
                        refreshView.finishRefresh();
                        refreshView.finishLoadMore();
                    }
                });
    }

    private void saveData(){
        preferenceConfig.setObject(user.getUserId()+partnerId+Config.CHAT_PARTNER_LIST,localChatLists);
    }

    private void initView() {
        inputManager = (InputMethodManager) etContent.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    ivFunctions.setVisibility(View.GONE);
                    btnSend.setVisibility(View.VISIBLE);
                } else {
                    ivFunctions.setVisibility(View.VISIBLE);
                    btnSend.setVisibility(View.GONE);
                }
            }
        });

        etContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            //键盘弹出变化会调用此函数
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                //获取当前界面可视部分
                ChatActivity.this.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                //获取屏幕的高度
                int screenHeight =  ChatActivity.this.getWindow().getDecorView().getRootView().getHeight();
                //此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
                int heightDifference = screenHeight - r.bottom;
                if (heightDifference>0){
                    rcvChat.smoothScrollToPosition(chatLists.size());
                }
                Log.d("Keyboard Size", "Size: " + heightDifference);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcvChat.setLayoutManager(linearLayoutManager);

        chatAdapter = new ChatAdapter(R.layout.item_chat_content, chatLists, partnerHead,mHandler);
        chatAdapter.addChildClickViewIds(R.id.tv_voice,R.id.tv_voice_left,R.id.iv_pic,R.id.iv_pic_left, R.id.iv_send_fail, R.id.rl_video_left, R.id.rl_video);
        rcvChat.setAdapter(chatAdapter);
        chatAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                switch (view.getId()){
                    case R.id.tv_voice:
                    case R.id.tv_voice_left:
                        String url = chatLists.get(position).getMsgContent();
                        String fileName = url.split("/")[2];
                        File voiceFile =  SaveFileUtil.getVoiceFile(mContext,user.getUserId(),partnerId);
                        File file = new File(voiceFile, fileName);
                        if (file.exists()){
                            play(file.getAbsolutePath());
                        }else {
                            DownloadVoiceManager voiceManager = new DownloadVoiceManager(mContext);
                            voiceManager.setOnDownloadFinishListener(new DownloadVoiceManager.OnDownloadFinishListener() {
                                @Override
                                public void onDownloadSuccess(String path) {
                                    play(path);
                                }

                                @Override
                                public void onDownloadFailed() {
                                    ToastUtil.showShortMessage(mContext,"加载语音失败!");
                                }
                            });
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    voiceManager.download(NetWorkService.homeUrl,url,user.getUserId(),partnerId);
                                }
                            }).start();
                        }
                        break;
                    case R.id.iv_pic:
                    case R.id.iv_pic_left:
                        List<String> list = new ArrayList<>();
                        list.add(chatLists.get(position).getMsgContent());
                        ImgPreviewDialog dialog = new ImgPreviewDialog(mContext, list);
                        dialog.setPhotoViewClick(new ImgPreviewDialog.PhotoViewClick() {
                            @Override
                            public void ImgClick() {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                        break;
                    case R.id.iv_send_fail:
                        TextDialog dialog1 = new TextDialog(mContext);
                        dialog1.setTitle1("提示");
                        dialog1.setContent("是否重新发送该条消息？");
                        dialog1.setOnClickListener(new TextDialog.OnClickListener() {
                            @Override
                            public void commit() {
                                ChatEntity chatEntity = chatLists.get(position);
                                chatEntity.setSending(true);
                                chatAdapter.notifyItemChanged(position,"state");
                                reSendPos = position;
//                                send(chatEntity.msgType,chatEntity.msgContent,chatEntity.voiceTime,true);
                                sendMultiPic(chatEntity.msgType,chatEntity.getMsgContent(),position,true,chatEntity.voiceTime);
                                dialog1.dismiss();
                            }

                            @Override
                            public void cancel() {
                                dialog1.dismiss();
                            }
                        });
                        dialog1.show();
                        break;
                    case R.id.rl_video_left:
                    case R.id.rl_video:
                        VideoPreviewDialog dialog2 = new VideoPreviewDialog(mContext);
                        ChatEntity chatEntity = chatLists.get(position);
                        if (!TextUtils.isEmpty(chatEntity.localPath)){
                            File file1 = new File(chatEntity.localPath);
                            if (file1.exists()){
                                dialog2.setLocalVideo(chatEntity.localPath);
                            }else {
                                dialog2.setVideo(NetWorkService.homeUrl+chatEntity.getMsgContent());
                            }
                        }else {
                            dialog2.setVideo(NetWorkService.homeUrl + chatEntity.getMsgContent());
                        }
                        dialog2.show();
                        break;
                }
            }
        });
        rcvChat.scrollToPosition(chatLists.size());

        refreshView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                page++;
                loadLocalData();
            }
        });

        String head = getIntent().getStringExtra("head");
        chatAdapter.setPartnerHead(partnerId,head);
    }

    private void reLogin(){
        if (user==null){
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
            ToastUtil.showShortMessage(mContext,"请先登录");
            finish();
            return;
        }
    }

    private void initData() {
        uiPopWinUtil = new PopWinUtil(this);
        uiPopWinUtil.setShade(false);
        uiPopWinUtil.setBottomView(llShow);

        if (TextUtils.isEmpty(partnerId))
            return;
        Map map = new HashMap();
        map.put("userId",ShareApplication.user.getUserId());
        map.put("peopleId",partnerId);
        service.getPeopleInfo(map)
                .compose(RxSchedulers.<PeopleVo>compose(this))
                .subscribe(new BaseObserver<PeopleVo>(this) {
                    @Override
                    public void onSuccess(PeopleVo peopleVo) {
                        partnerInfo = peopleVo.getData();
                        if (partnerInfo==null){
                            ToastUtil.showShortMessage(mContext,"获取数据失败");
                            return;
                        }
                        if (tvName==null)
                            return;
                        if (TextUtils.isEmpty(setName)) {
                            tvName.setText(peopleVo.getData().getPeopleName());
                        }
                        tvTitle.setText(peopleVo.getData().getPeopleDes());
                        chatAdapter.setPartnerHead(partnerInfo.getPeopleId(),partnerInfo.getPeopleHead());
                    }

                    @Override
                    public void onFailed(String msg) {

                    }
                });
    }

    private void play(String url) {
        if (!isVoicePlaying) {
            isVoicePlaying = true;
            try {
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                    mMediaPlayer = null;
                }
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
                        mMediaPlayer.reset();
                        return false;
                    }
                });

                mMediaPlayer.reset();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mMediaPlayer.setDataSource(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mMediaPlayer.prepare();
                mMediaPlayer.start();
                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer arg0) {
                        Constants.muteAudioFocus(ChatActivity.this, true);
                        // mMediaPlayer.start();
                    }
                });
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer arg0) {
                        if (mMediaPlayer != null)
                            mMediaPlayer.release();
                        Constants.muteAudioFocus(mContext, false);
                        mMediaPlayer = null;
                        isVoicePlaying = false;
                    }
                });
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            isVoicePlaying = false;
            if (mMediaPlayer != null) {
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }
    }

    private void stopRecording() {
        if (mRecorder != null) {
            // mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    private void startRecording() {
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
        } else {
            mRecorder.reset();
        }
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        if(!SaveFileUtil.getVoiceFile(mContext,user.getUserId(),partnerId).exists()){
            SaveFileUtil.getVoiceFile(mContext,user.getUserId(),partnerId).mkdirs();
        }
        try {
            tmpFileName = user.getUserId()+System.currentTimeMillis();
            iRecAudioFile = File.createTempFile(tmpFileName, ".mp3", SaveFileUtil.getVoiceFile(mContext,user.getUserId(),partnerId));
            mRecorder.setOutputFile(iRecAudioFile.getAbsolutePath());

            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void count() {
        TimerTask task = new TimerTask() {
            public void run() {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        };
        timer = new Timer(true);
        timer.schedule(task, 1000, 1000);
    }

    private void handle(Message msg) {
        switch (msg.what) {
            case 1:
                seconds++;
                if (seconds >= 55) {
                    llRecorderAnim.setVisibility(View.GONE);
                    tvSec.setVisibility(View.VISIBLE);
                    tvSec.setText(String.valueOf(60 - seconds));
                }
                if (seconds >= 60) {
                    layoutRecordingMask.setVisibility(View.GONE);
                    llRecorderAnim.setVisibility(View.VISIBLE);
                    tvSec.setVisibility(View.GONE);
                    stopRecording();
                    tmpSec = seconds;
                    seconds = 0;

                    if (iRecAudioFile != null)
                        send(Constants.MODE_VOICE, iRecAudioFile.getAbsolutePath(), String.valueOf(tmpSec),false);
                }
                break;
        }
    }

    private void send(String msgType, String content, String voiceTime, boolean isReSend) {
        HashMap map = new HashMap();
        ChatEntity sendEntity = new ChatEntity();
        sendEntity.setSenderAvatar(ShareApplication.user.getHeadImg());
        sendEntity.setSenderName(ShareApplication.user.getUserName());
        sendEntity.setMsgType(msgType);
        sendEntity.setSenderId(ShareApplication.user.getUserId());
        sendEntity.setMsgContent(content);
        sendEntity.setSetName(tvName.getText().toString());
        map.put("talkid",user.getUserId());
        map.put("toid",partnerInfo.getPeopleId());
        map.put("msgtype",msgType);
//        MultipartBody.Part part = null;
//        Observable<SendMsgVo> baseResultObservable = null;
        switch (msgType){
            case Constants.MODE_IMAGE:
                if (TextUtils.isEmpty(content)){
                    ToastUtil.showShortMessage(mContext,"图片获取失败");
                    return;
                }
                BitmapFactory.Options options = new BitmapFactory.Options();
                Bitmap bmp = BitmapFactory.decodeFile(content, options);//这里的bitmap是个空
                options.inJustDecodeBounds = true;//这个参数设置为true才有效，
                sendEntity.height = options.outHeight;
                sendEntity.width = options.outWidth;
                sendEntity.setLocalPath(content);
//                part = getFilePart(content);
//                baseResultObservable = service.sendMsg(map,part);
                break;
            case Constants.MODE_VOICE:
                map.put("voicetime",voiceTime);
                sendEntity.setVoiceTime(voiceTime);
                if (TextUtils.isEmpty(content)){
                    ToastUtil.showShortMessage(mContext,"语音获取失败");
                    return;
                }
//                part = getFilePart(content);
//                baseResultObservable = service.sendMsg(map,part);
                break;
            case Constants.MODE_TEXT:
                map.put("msgcontent",content);
                sendEntity.setMsgContent(content);
//                baseResultObservable = service.sendMsg(map);
                break;
            case Constants.MODE_VIDEO:
                sendEntity.setLocalPath(content);
                break;
        }
        sendEntity.setSending(true);
        sendEntity.setChatTime(System.currentTimeMillis());
        if (!isReSend) {
            chatAdapter.addData(sendEntity);
            localChatLists.add(sendEntity);
            sendEntityList.add(sendEntity);

            sending.add(chatLists.size());
            saveData();
        }
        rcvChat.smoothScrollToPosition(chatLists.size());
//        EventBus.getDefault().post(new MyChatEntityEvent(sendEntity, partnerInfo));
        startSending(sending.size()-1);
        /*baseResultObservable.compose(RxSchedulers.<SendMsgVo>compose(mContext))
                .subscribe(new BaseObserver<SendMsgVo>() {
                    @Override
                    public void onSuccess(SendMsgVo baseResult) {
                        ChatEntity data = baseResult.getData();
                        if (isReSend){
                            chatLists.get(reSendPos).setSending(false);
                            chatLists.get(reSendPos).setSendSuccess(true);
                            chatLists.get(reSendPos).setMsgType(data.msgType);
                            chatLists.get(reSendPos).setMsgContent(data.msgContent);
                            chatLists.get(reSendPos).setChatTime(data.chatTime);
                            chatLists.get(reSendPos).setVoiceTime(data.voiceTime);
                            chatAdapter.notifyItemChanged(reSendPos, "state");
                        }else {
                            chatLists.get(chatLists.size() - 1).setSending(false);
                            chatLists.get(chatLists.size() - 1).setSendSuccess(true);
                            chatLists.get(chatLists.size() - 1).setMsgType(data.msgType);
                            chatLists.get(chatLists.size() - 1).setMsgContent(data.msgContent);
                            chatLists.get(chatLists.size() - 1).setChatTime(data.chatTime);
                            chatLists.get(chatLists.size() - 1).setVoiceTime(data.voiceTime);
                            chatAdapter.notifyItemChanged(chatLists.size() - 1, "state");
                        }
                        EventBus.getDefault().post(new MyChatEntityEvent(chatLists.get(chatLists.size()-1),partnerInfo));
                        saveData();
                    }

                    @Override
                    public void onFailed(String msg) {
                        chatLists.get(chatLists.size()-1).setSending(false);
                        chatLists.get(chatLists.size()-1).setSendSuccess(false);
                        chatAdapter.notifyItemChanged(chatLists.size()-1,"state");
                        EventBus.getDefault().post(new MyChatEntityEvent(chatLists.get(chatLists.size()-1),partnerInfo));
                        ToastUtil.showShortMessage(mContext,"消息发送失败");
                        saveData();
                    }
                });*/
    }

    String tempImgPath="";
    private MultipartBody.Part getFilePart(String path) {
        File tempFile = new File(tempImgPath);
        if (tempFile.exists()){
            tempFile.delete();
        }
        File file;
        if (path.equalsIgnoreCase(".gif")){
            file = new File(path);
        }else {
            tempImgPath = BitmapUtil.compressImage(path,100,1024,mContext);
            if (TextUtils.isEmpty(tempImgPath)) {
                file = new File(path);
            } else {
                file = new File(tempImgPath);
            }
        }
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        return part;
    }

    private String getFileType(String path){
        String contentType = new MimetypesFileTypeMap().getContentType(path);
        Log.d("fileType===",contentType);
        return contentType;
    }

    private void initPressToSpeak() {
        tvRecord.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        tvRecord.setText("松开结束");
                        layoutRecordingMask.setVisibility(View.VISIBLE);
                        animation = (AnimationDrawable) iconStatus.getBackground();
                        animation.start();
                        count();
                        startRecording();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        if (timer != null) {
                            timer.cancel();
                        }
                        layoutRecordingMask.setVisibility(View.GONE);
                        tvRecord.setText("按住说话");
                        if (seconds < 1) {
                            ToastUtil.showShortMessage(mContext,"语音时间太短");
                            if (mRecorder != null) {
                                mRecorder.release();
                                mRecorder = null;
                            }
                        } else {
                            stopRecording();
                            tmpSec = seconds;
                            seconds = 0;

                            if (iRecAudioFile == null)
                                return true;
                            send(Constants.MODE_VOICE, iRecAudioFile.getAbsolutePath(), String.valueOf(tmpSec),false);

                        }
                        break;
                }
                return true;
            }
        });
    }

    private void initPicker() {

        List<HeadItem> items = new ArrayList<>();
        items.add(new HeadItem(Constants.IMAGE_CHOOSE_FROM_ALBUM, R.drawable.ic_choose_from_album, "照片"));
        items.add(new HeadItem(Constants.IMAGE_TAKE_PHOTO, R.drawable.ic_take_photo, "拍摄"));
        try {
            functionsPicker = new PagerMenuGridPicker(this, items, 8, 4);
            functionsPicker.setUiHandle(uiPopWinUtil);
            functionsPicker.setAsConsole(true);
            functionsPicker.setOnPagerGridItemClickListener(new PagerMenuGridPicker.OnPagerGridItemClickListener() {
                @Override
                public void onPagerGridItemClick(HeadItem avatarItem) {
                    switch (avatarItem.getCode()) {
                        case Constants.IMAGE_TAKE_PHOTO:
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (!ShareApplication.isGrantPermission(Constants.PERMISSIONS, mContext)) {
                                    ToastUtil.showShortMessage(mContext,"权限不足");
                                    requestPermissions(Constants.PERMISSIONS, Constants.PERMISSION_REQUEST_CODE);
                                    openType = Constants.IMAGE_TAKE_PHOTO;
                                } else {
                                    ShareApplication.openTakePhoto(mContext,false);
//                                    ShareApplication.goToSelectPicture(ShareApplication.ACTION_TYPE_PHOTO, mContext, false);
                                }
                            } else {
                                ShareApplication.openTakePhoto(mContext,false);
//                                ShareApplication.goToSelectPicture(ShareApplication.ACTION_TYPE_PHOTO, mContext, false);
                            }
                            break;
                        case Constants.IMAGE_CHOOSE_FROM_ALBUM:
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (!ShareApplication.isGrantPermission(Constants.PERMISSIONS, mContext)) {
                                    ToastUtil.showShortMessage(mContext,"权限不足");
                                    requestPermissions(Constants.PERMISSIONS, Constants.PERMISSION_REQUEST_CODE);
                                    openType = Constants.IMAGE_CHOOSE_FROM_ALBUM;
                                } else {
                                    ShareApplication.openAlbumSelect(mContext,9,false);
//                                    ShareApplication.goToSelectPicture(ShareApplication.ACTION_TYPE_ALBUM, mContext, false);
                                }
                            } else {
                                ShareApplication.openAlbumSelect(mContext,9,false);
//                                ShareApplication.goToSelectPicture(ShareApplication.ACTION_TYPE_ALBUM, mContext, false);
                            }
                            break;
                    }
                }
            });
            functionsPicker.setOnConsoleButtonClickLister(new PagerMenuGridPicker.OnConsoleButtonClickLister() {
                @Override
                public void onVoiceButtonClick() {
                    if (mode == Constants.MODE_TEXT) {
                        layoutText.setVisibility(View.GONE);
                        tvRecord.setVisibility(View.VISIBLE);
                        tvSwitch.setImageResource(R.drawable.ic_keyborad);
                        mode = Constants.MODE_VOICE;
                    } else if (mode == Constants.MODE_VOICE) {
                        layoutText.setVisibility(View.VISIBLE);
                        tvRecord.setVisibility(View.GONE);
                        tvSwitch.setImageResource(R.drawable.ic_voice);
                        mode = Constants.MODE_TEXT;
                    }
                }

                @Override
                public void onContentEditClick() {
                }

                @Override
                public void onFacesButtonClick() {
                }

                @Override
                public void onImageButtonClick() {
                    uiPopWinUtil.dismissMenu();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.btn_back, R.id.btn_profile, R.id.refresh_view, R.id.ll_recorder_anim, R.id.ll_voice_text, R.id.tv_switch, R.id.iv_functions, R.id.btn_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                    mMediaPlayer = null;
                }
                finish();
                break;
            case R.id.btn_profile:
                Intent intent1 = new Intent(mContext, PeopleProfileActivity.class);
                intent1.putExtra("peopleId", partnerId);
                intent1.putExtra("from","chat");
                startActivityForResult(intent1,CODE_TOPROFILE);
                break;
            case R.id.refresh_view:
                break;
            case R.id.ll_recorder_anim:
                break;
            case R.id.ll_voice_text:
            case R.id.tv_switch:
                if (mode == Constants.MODE_TEXT) {
                    layoutText.setVisibility(View.GONE);
                    tvRecord.setVisibility(View.VISIBLE);
                    tvSwitch.setImageResource(R.drawable.ic_keyborad);
                    mode = Constants.MODE_VOICE;
                } else if (mode == Constants.MODE_VOICE) {
                    layoutText.setVisibility(View.VISIBLE);
                    tvRecord.setVisibility(View.GONE);
                    tvSwitch.setImageResource(R.drawable.ic_voice);
                    mode = Constants.MODE_TEXT;
                }
                break;
            case R.id.iv_functions:
                inputManager.hideSoftInputFromWindow(etContent.getWindowToken(), 0);
                uiPopWinUtil.dismissMenu();
                functionsPicker.setConsoleWhich(1);
                uiPopWinUtil.showPopupBottom(functionsPicker.getView(), R.id.layout_full);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        llShow.setVisibility(View.VISIBLE);
                        rcvChat.smoothScrollToPosition(chatLists.size());
                    }
                },450);
                break;
            case R.id.btn_send:
                if (TextUtils.isEmpty(etContent.getText().toString().trim())) {
                    ToastUtil.showShortMessage(mContext,"请输入聊天内容");
                } else {
                    send(Constants.MODE_TEXT, etContent.getText().toString(), "", false);
                }
                etContent.setText("");
                break;
        }
    }

    private void requestPermission(String[] strings) {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(strings)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {//用户已授予所有权限

                        } else {//用户拒绝某些权限
                            ToastUtil.showShortMessage(mContext,"应用权限未允许");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onActivityResult(final int requestCode, final int resultCode,
                                 final Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;
        switch (requestCode) {
            case PictureConfig.CHOOSE_REQUEST:
                // 图片、视频、音频选择结果回调
                List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                // 例如 LocalMedia 里面返回三种path
                // 1.media.getPath(); 为原图path
                // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                if (selectList.size()>1) {
                    sendMultiPic(selectList);
                }else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        imgPath = selectList.get(0).getAndroidQToPath();
                    } else {
                        imgPath = selectList.get(0).getPath();
                    }
                    String fileType = getFileType(imgPath);
                    if (fileType.contains("image/")) {
                        send(Constants.MODE_IMAGE, imgPath, "", false);
                    }else {
                        send(Constants.MODE_VIDEO, imgPath, "", false);
                    }
                }
                break;
            case Constants.PERMISSION_REQUEST_CODE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!ShareApplication.isGrantPermission(Constants.PERMISSIONS, mContext)) {
                        ToastUtil.showShortMessage(mContext,"权限不足");
                    } else {
                        switch (openType) {
                            case Constants.IMAGE_TAKE_PHOTO:
                                ShareApplication.openTakePhoto(mContext,false);
//                                ShareApplication.goToSelectPicture(ShareApplication.ACTION_TYPE_PHOTO, mContext, false);
                                break;
                            case Constants.IMAGE_CHOOSE_FROM_ALBUM:
                                ShareApplication.openAlbumSelect(mContext,9,false);
//                                ShareApplication.goToSelectPicture(ShareApplication.ACTION_TYPE_ALBUM, mContext, false);
                                break;
                        }
                    }
                }
                break;
            case CODE_TOPROFILE:
                tvName.setText(data.getStringExtra("name"));
                break;
        }
    }

    /**
     * 发送多张图片
     * @param selectList
     */
    private void sendMultiPic(List<LocalMedia> selectList) {
        for (LocalMedia localMedia:selectList){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                imgPath = localMedia.getAndroidQToPath();
            } else {
                imgPath = localMedia.getPath();
            }
            String fileType = getFileType(imgPath);
            ChatEntity sendEntity = new ChatEntity();
            sendEntity.setSenderAvatar(ShareApplication.user.getHeadImg());
            sendEntity.setSenderName(ShareApplication.user.getUserName());
            sendEntity.setLocalPath(imgPath);
            sendEntity.setSetName(tvName.getText().toString());
            if (fileType.contains("image/")) {
                sendEntity.setMsgType(Constants.MODE_IMAGE);
                BitmapFactory.Options options = new BitmapFactory.Options();
                Bitmap bmp = BitmapFactory.decodeFile(imgPath, options);//这里的bitmap是个空
                options.inJustDecodeBounds = true;//这个参数设置为true才有效，
                sendEntity.height = options.outHeight;
                sendEntity.width = options.outWidth;
            }else {
                sendEntity.setMsgType(Constants.MODE_VIDEO);
            }
            sendEntity.setMsgType(Constants.MODE_IMAGE);
            sendEntity.setSenderId(ShareApplication.user.getUserId());
            sendEntity.setMsgContent(imgPath);
            sendEntity.setSending(true);
            sendEntity.setChatTime(System.currentTimeMillis());
            sendEntityList.add(sendEntity);
            chatAdapter.addData(sendEntity);
            localChatLists.add(sendEntity);
            sending.add(chatLists.size());
            saveData();

            EventBus.getDefault().post(new MyChatEntityEvent(sendEntity, partnerInfo));
        }

        rcvChat.smoothScrollToPosition(chatLists.size());
        if (!ShareApplication.multiSending) {
            startSending(0);
        }
    }

    private void startSending(int pos) {
        if (sending.size()>0 && pos<sendEntityList.size()) {
            ChatEntity chatEntity = sendEntityList.get(pos);
            sendMultiPic(chatEntity.getMsgType(), chatEntity.getMsgContent(), pos, false, chatEntity.getVoiceTime());
        }
    }

    private void sendMultiPic(String msgType, String content, int pos, boolean isReSend, String voiceTime) {
        int adapterPos;
        if (isReSend){
            adapterPos = pos;
        }else {
            adapterPos = sending.get(pos) - 1;
        }
        HashMap map = new HashMap();
        map.put("talkid",user.getUserId());
        map.put("toid",partnerInfo.getPeopleId());
        map.put("msgtype",msgType);
        MultipartBody.Part part = null;
        Observable<SendMsgVo> baseResultObservable = null;
        switch (msgType){
            case Constants.MODE_IMAGE:
                if (TextUtils.isEmpty(content)){
                    ToastUtil.showShortMessage(mContext,"图片获取失败");
                    return;
                }
                part = getFilePart(content);
                baseResultObservable = service.sendMsg(map,part);
                break;
            case Constants.MODE_VOICE:
                map.put("voicetime",voiceTime);
                if (TextUtils.isEmpty(content)){
                    ToastUtil.showShortMessage(mContext,"语音获取失败");
                    return;
                }
                part = getFilePart(content);
                baseResultObservable = service.sendMsg(map,part);
                break;
            case Constants.MODE_TEXT:
                map.put("msgcontent",content);
                baseResultObservable = service.sendMsg(map);
                break;
            case Constants.MODE_VIDEO:
                if (TextUtils.isEmpty(content)){
                    ToastUtil.showShortMessage(mContext,"视频获取失败");
                    return;
                }
                part = getFilePart(content);
                baseResultObservable = service.sendMsg(map,part);
                break;
        }

        EventBus.getDefault().post(new MyChatEntityEvent(chatLists.get(adapterPos), partnerInfo));
        ShareApplication.multiSending = true;
        baseResultObservable.compose(RxSchedulers.<SendMsgVo>compose(mContext))
                .subscribe(new BaseObserver<SendMsgVo>() {
                    @Override
                    public void onSuccess(SendMsgVo baseResult) {
                        ChatEntity data = baseResult.getData();
                        chatLists.get(adapterPos).setSending(false);
                        chatLists.get(adapterPos).setSendSuccess(true);
                        chatLists.get(adapterPos).setMsgType(data.msgType);
                        chatLists.get(adapterPos).setMsgContent(data.msgContent);
                        chatLists.get(adapterPos).setChatTime(data.chatTime);
                        chatLists.get(adapterPos).setVoiceTime(data.voiceTime);
                        saveData();
                        EventBus.getDefault().post(new NotifyItemChatAdapterEvent(adapterPos,"state",true,data));
                        EventBus.getDefault().post(new MyChatEntityEvent(chatLists.get(adapterPos),partnerInfo));
                        ShareApplication.multiSending = false;
                        if (!isReSend) {
                            sending.remove(pos);
                            sendEntityList.remove(pos);
                            startSending(0);
                        }
                    }

                    @Override
                    public void onFailed(String msg) {
                        chatLists.get(adapterPos).setSending(false);
                        chatLists.get(adapterPos).setSendSuccess(false);
                        saveData();
                        EventBus.getDefault().post(new NotifyItemChatAdapterEvent(adapterPos,"state",false,chatLists.get(adapterPos)));
                        EventBus.getDefault().post(new MyChatEntityEvent(chatLists.get(adapterPos),partnerInfo));
                        ToastUtil.showShortMessage(mContext,"消息发送失败");
                        if (!isReSend) {
                            startSending(pos + 1);
                        }
                    }
                });
    }

    @Subscribe
    public void notifyAdapter(NotifyItemChatAdapterEvent event){
        if (event!=null){
            if (chatAdapter!=null){
                chatLists.get(event.pos).setSending(false);
                chatLists.get(event.pos).setSendSuccess(event.isSuccess);
                chatLists.get(event.pos).setMsgType(event.data.msgType);
                chatLists.get(event.pos).setMsgContent(event.data.msgContent);
                chatLists.get(event.pos).setChatTime(event.data.chatTime);
                chatLists.get(event.pos).setVoiceTime(event.data.voiceTime);
                chatAdapter.notifyItemChanged(event.pos, event.des);
            }
        }
    }
    @Subscribe
    public void refreshData(RefreshPartnerMsgEvent event){
        if (event!=null){
            loadData(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
        EventBus.getDefault().unregister(this);
    }

}