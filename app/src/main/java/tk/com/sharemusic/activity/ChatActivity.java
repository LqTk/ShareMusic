package tk.com.sharemusic.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import tk.com.sharemusic.event.RefreshPartnerMsgEvent;
import tk.com.sharemusic.myview.dialog.ImgPreviewDIalog;
import tk.com.sharemusic.myview.dialog.PagerMenuGridPicker;
import tk.com.sharemusic.network.HttpMethod;
import tk.com.sharemusic.network.NetWorkService;
import tk.com.sharemusic.network.RxSchedulers;
import tk.com.sharemusic.network.response.ChatListVo;
import tk.com.sharemusic.network.response.PeopleVo;
import tk.com.sharemusic.network.response.SendMsgVo;
import tk.com.sharemusic.network.rxjava.BaseObserver;
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
    @BindView(R.id.empty)
    TextView empty;
    @BindView(R.id.btn_press_to_speak)
    TextView tvRecord;

    private Unbinder bind;
    private NetWorkService service;
    private String partnerId;
    private String mode = Constants.MODE_TEXT;
    ;

    private MediaPlayer mMediaPlayer;
    private MediaRecorder mRecorder;
    private boolean isVoicePlaying = false;
    private ChatActivity mContext;
    private AnimationDrawable animation;
    private Timer timer;
    private final String fileName = "chat_voice_";
    private String tmpFileName;

    private final Handler handler = new CountdownHandler(this);
    private int seconds = 0;
    private int tmpSec = 0;
    private File iRecAudioFile;
    private PagerMenuGridPicker functionsPicker;
    PopWinUtil uiPopWinUtil;

    private final static String[] PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private String imgPath;
    private int openType;
    private MsgEntity partnerInfo;
    private User user;
    private List<ChatEntity> chatLists = new ArrayList<>();
    private ChatAdapter chatAdapter;
    private PreferenceConfig preferenceConfig;
    private String partnerHead = "";
    private InputMethodManager inputManager;

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

        chatLists = preferenceConfig.getArrayList(user.getUserId()+partnerId+Config.CHAT_PARTNER_LIST,ChatEntity.class);

        initView();
        initData();
        initPicker();
        loadData(true);
        initPressToSpeak();
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
                        refreshView.finishRefresh();
                        refreshView.finishLoadMore();
                        List<ChatEntity> voData = chatListVo.getData();
                        if (voData!=null && voData.size()>0) {
                            chatLists.addAll(voData);
                            saveData();
                        }
                        EventBus.getDefault().post(new MsgReadEvent(partnerId));
                        chatAdapter.notifyDataSetChanged();
                        rcvChat.smoothScrollToPosition(chatLists.size());
                    }

                    @Override
                    public void onFailed(String msg) {
                        ToastUtil.showShortMessage(mContext,msg);
                        refreshView.finishRefresh();
                        refreshView.finishLoadMore();
                    }
                });
    }

    private void saveData(){
        preferenceConfig.setObject(user.getUserId()+partnerId+Config.CHAT_PARTNER_LIST,chatLists);
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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcvChat.setLayoutManager(linearLayoutManager);

        chatAdapter = new ChatAdapter(R.layout.item_chat_content, chatLists, partnerHead);
        chatAdapter.addChildClickViewIds(R.id.tv_voice,R.id.tv_voice_left,R.id.iv_pic,R.id.iv_pic_left);
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
                        ImgPreviewDIalog dialog = new ImgPreviewDIalog(mContext);
                        dialog.setPhotoViewClick(new ImgPreviewDIalog.PhotoViewClick() {
                            @Override
                            public void ImgClick() {
                                dialog.dismiss();
                            }
                        });
                        dialog.setImageView(chatLists.get(position).getMsgContent());
                        dialog.show();
                        break;
                }
            }
        });
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
                        tvName.setText(peopleVo.getData().getPeopleName());
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
                        send(Constants.MODE_VOICE, iRecAudioFile.getAbsolutePath(), String.valueOf(tmpSec));
                }
                break;
        }
    }

    private void send(String msgType, String content, String voiceTime) {
        HashMap map = new HashMap();
        map.put("talkid",user.getUserId());
        map.put("toid",partnerInfo.getPeopleId());
        map.put("msgtype",msgType);
        MultipartBody.Part part = null;
        Observable<SendMsgVo> baseResultObservable = null;
        switch (msgType){
            case Constants.MODE_IMAGE:
                if (TextUtils.isEmpty(content)){
                    ToastUtil.showShortMessage(mContext,"获取数据失败");
                    return;
                }
                part = getFilePart(content);
                baseResultObservable = service.sendMsg(map,part);
                break;
            case Constants.MODE_VOICE:
                map.put("voicetime",voiceTime);
                if (TextUtils.isEmpty(content)){
                    ToastUtil.showShortMessage(mContext,"获取数据失败");
                    return;
                }
                part = getFilePart(content);
                baseResultObservable = service.sendMsg(map,part);
                break;
            case Constants.MODE_TEXT:
                map.put("msgcontent",content);
                baseResultObservable = service.sendMsg(map);
                break;
        }
        baseResultObservable.compose(RxSchedulers.<SendMsgVo>compose(mContext))
                .subscribe(new BaseObserver<SendMsgVo>(mContext) {
                    @Override
                    public void onSuccess(SendMsgVo baseResult) {
                        ChatEntity data = baseResult.getData();
                        chatLists.add(baseResult.getData());
                        EventBus.getDefault().post(new MyChatEntityEvent(data,partnerInfo));
                        saveData();
                        chatAdapter.notifyDataSetChanged();
                        rcvChat.smoothScrollToPosition(chatLists.size());
                    }

                    @Override
                    public void onFailed(String msg) {
                        ToastUtil.showShortMessage(mContext,"消息发送失败");
                    }
                });
    }

    private MultipartBody.Part getFilePart(String path) {
        Log.d("picFile",path);
        File file = new File(path);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        return part;
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
                            send(Constants.MODE_VOICE, iRecAudioFile.getAbsolutePath(), String.valueOf(tmpSec));

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
                                if (!ShareApplication.isGrantPermission(PERMISSIONS, mContext)) {
                                    ToastUtil.showShortMessage(mContext,"权限不足");
                                    requestPermissions(PERMISSIONS, Constants.PERMISSION_REQUEST_CODE);
                                    openType = Constants.IMAGE_TAKE_PHOTO;
                                } else {
                                    ShareApplication.goToSelectPicture(ShareApplication.ACTION_TYPE_PHOTO, mContext, false);
                                }
                            } else {
                                ShareApplication.goToSelectPicture(ShareApplication.ACTION_TYPE_PHOTO, mContext, false);
                            }
                            break;
                        case Constants.IMAGE_CHOOSE_FROM_ALBUM:
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (!ShareApplication.isGrantPermission(PERMISSIONS, mContext)) {
                                    ToastUtil.showShortMessage(mContext,"权限不足");
                                    requestPermissions(PERMISSIONS, Constants.PERMISSION_REQUEST_CODE);
                                    openType = Constants.IMAGE_CHOOSE_FROM_ALBUM;
                                } else {
                                    ShareApplication.goToSelectPicture(ShareApplication.ACTION_TYPE_ALBUM, mContext, false);
                                }
                            } else {
                                ShareApplication.goToSelectPicture(ShareApplication.ACTION_TYPE_ALBUM, mContext, false);
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
                startActivity(intent1);
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
                break;
            case R.id.btn_send:
                if (TextUtils.isEmpty(etContent.getText().toString().trim())) {
                    ToastUtil.showShortMessage(mContext,"请输入聊天内容");
                } else {
                    send(Constants.MODE_TEXT, etContent.getText().toString(), "");
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
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q) {
                    imgPath = selectList.get(0).getAndroidQToPath();
                }else {
                    imgPath = selectList.get(0).getPath();
                }
                send(Constants.MODE_IMAGE, imgPath, "");
                break;
            case Constants.PERMISSION_REQUEST_CODE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!ShareApplication.isGrantPermission(PERMISSIONS, mContext)) {
                        ToastUtil.showShortMessage(mContext,"权限不足");
                    } else {
                        switch (openType) {
                            case Constants.IMAGE_TAKE_PHOTO:
                                ShareApplication.goToSelectPicture(ShareApplication.ACTION_TYPE_PHOTO, mContext, false);
                                break;
                            case Constants.IMAGE_CHOOSE_FROM_ALBUM:
                                ShareApplication.goToSelectPicture(ShareApplication.ACTION_TYPE_ALBUM, mContext, false);
                                break;
                        }
                    }
                }
                break;
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