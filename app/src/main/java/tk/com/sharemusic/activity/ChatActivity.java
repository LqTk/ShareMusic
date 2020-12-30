package tk.com.sharemusic.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import tk.com.sharemusic.config.Constants;
import tk.com.sharemusic.entity.HeadItem;
import tk.com.sharemusic.entity.MsgEntiti;
import tk.com.sharemusic.entity.MsgEntity;
import tk.com.sharemusic.entity.User;
import tk.com.sharemusic.myview.dialog.PagerMenuGridPicker;
import tk.com.sharemusic.network.BaseResult;
import tk.com.sharemusic.network.HttpMethod;
import tk.com.sharemusic.network.NetWorkService;
import tk.com.sharemusic.network.RxSchedulers;
import tk.com.sharemusic.network.response.PeopleVo;
import tk.com.sharemusic.network.rxjava.BaseObserver;
import tk.com.sharemusic.utils.PopWinUtil;

public class ChatActivity extends AppCompatActivity {

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
    private MsgEntiti partnerInfo;
    private User user;

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
        service = HttpMethod.getInstance().create(NetWorkService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA
                    , Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE});
        }

        initView();
        initData();
        initPicker();
        initPressToSpeak();
    }

    private void initView() {
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
    }

    private void initData() {
        mContext = this;
        uiPopWinUtil = new PopWinUtil(this);
        uiPopWinUtil.setShade(false);

        user = ShareApplication.getUser();
        if (user==null){
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
            Toast.makeText(mContext,"请先登录",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        partnerId = getIntent().getStringExtra("partnerId");
        if (TextUtils.isEmpty(partnerId))
            return;
        service.getPeopleInfo(partnerId)
                .compose(RxSchedulers.<PeopleVo>compose(this))
                .subscribe(new BaseObserver<PeopleVo>() {
                    @Override
                    public void onSuccess(PeopleVo peopleVo) {
                        partnerInfo = peopleVo.getData();
                        if (partnerInfo==null){
                            Toast.makeText(mContext,"获取数据失败",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        tvName.setText(peopleVo.getData().getPeopleName());
                        tvTitle.setText(peopleVo.getData().getPeopleDes());
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
               /* try {
                    mMediaPlayer.setDataSource(mContext, uri, header);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
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
        if (!new File(Constants.DEFAULT_SAVE_FILE_PATH).exists()) {
            new File(Constants.DEFAULT_SAVE_FILE_PATH).mkdirs();
        }
        try {
            tmpFileName = fileName + System.currentTimeMillis();
            iRecAudioFile = File.createTempFile(tmpFileName, ".mp3", new File(Constants.DEFAULT_SAVE_FILE_PATH));
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
        Observable<BaseResult> baseResultObservable = null;
        switch (msgType){
            case Constants.MODE_IMAGE:
                part = getFilePart(content);
                baseResultObservable = service.sendMsg(map,part);
                break;
            case Constants.MODE_VOICE:
                map.put("voicetime",voiceTime);
                part = getFilePart(content);
                baseResultObservable = service.sendMsg(map,part);
                break;
            case Constants.MODE_TEXT:
                map.put("msgcontent",content);
                baseResultObservable = service.sendMsg(map);
                break;
        }
        baseResultObservable.compose(RxSchedulers.<BaseResult>compose(mContext))
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult baseResult) {
                        Toast.makeText(mContext,"消息发送成功",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(String msg) {
                        Toast.makeText(mContext,"消息发送失败",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private MultipartBody.Part getFilePart(String path) {
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
                            Toast.makeText(mContext, "语音时间太短!", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(mContext, "权限不足", Toast.LENGTH_SHORT).show();
                                    requestPermissions(PERMISSIONS, Constants.PERMISSION_REQUEST_CODE);
                                    openType = Constants.IMAGE_TAKE_PHOTO;
                                } else {
                                    ShareApplication.goToSelectPicture(ShareApplication.ACTION_TYPE_PHOTO, mContext);
                                }
                            } else {
                                ShareApplication.goToSelectPicture(ShareApplication.ACTION_TYPE_PHOTO, mContext);
                            }
                            break;
                        case Constants.IMAGE_CHOOSE_FROM_ALBUM:
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (!ShareApplication.isGrantPermission(PERMISSIONS, mContext)) {
                                    Toast.makeText(mContext, "权限不足", Toast.LENGTH_SHORT).show();
                                    requestPermissions(PERMISSIONS, Constants.PERMISSION_REQUEST_CODE);
                                    openType = Constants.IMAGE_CHOOSE_FROM_ALBUM;
                                } else {
                                    ShareApplication.goToSelectPicture(ShareApplication.ACTION_TYPE_ALBUM, mContext);
                                }
                            } else {
                                ShareApplication.goToSelectPicture(ShareApplication.ACTION_TYPE_ALBUM, mContext);
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
                uiPopWinUtil.dismissMenu();
                functionsPicker.setConsoleWhich(1);
                uiPopWinUtil.showPopupMenu(functionsPicker.getView(), R.id.layout_full);
                break;
            case R.id.btn_send:
                if (TextUtils.isEmpty(etContent.getText().toString().trim())) {
                    Toast.makeText(mContext,"请输入聊天内容！",Toast.LENGTH_SHORT).show();
                } else {
                    send(Constants.MODE_TEXT, etContent.getText().toString(), "");
                }
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
                            Toast.makeText(mContext, "应用权限未允许", Toast.LENGTH_SHORT).show();
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
                imgPath = selectList.get(0).getCutPath();
                send(Constants.MODE_IMAGE, imgPath, "");
                break;
            case Constants.PERMISSION_REQUEST_CODE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!ShareApplication.isGrantPermission(PERMISSIONS, mContext)) {
                        Toast.makeText(mContext, "权限不足", Toast.LENGTH_SHORT).show();
                    } else {
                        switch (openType) {
                            case Constants.IMAGE_TAKE_PHOTO:
                                ShareApplication.goToSelectPicture(ShareApplication.ACTION_TYPE_PHOTO, mContext);
                                break;
                            case Constants.IMAGE_CHOOSE_FROM_ALBUM:
                                ShareApplication.goToSelectPicture(ShareApplication.ACTION_TYPE_ALBUM, mContext);
                                break;
                        }
                    }
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }
}