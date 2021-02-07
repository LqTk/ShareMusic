package tk.com.sharemusic.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.danikula.videocache.HttpProxyCacheServer;
import com.google.android.flexbox.FlexboxLayout;
import com.luck.picture.lib.tools.ScreenUtils;
import com.shehuan.niv.NiceImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tk.com.sharemusic.R;
import tk.com.sharemusic.ShareApplication;
import tk.com.sharemusic.adapter.ReviewAdapter;
import tk.com.sharemusic.adapter.SharePicAdapter;
import tk.com.sharemusic.config.Constants;
import tk.com.sharemusic.entity.ChatReviewEntity;
import tk.com.sharemusic.entity.GoodsEntity;
import tk.com.sharemusic.entity.ReviewEntity;
import tk.com.sharemusic.entity.ShareGvEntity;
import tk.com.sharemusic.entity.SocialPublicEntity;
import tk.com.sharemusic.enums.Gender;
import tk.com.sharemusic.event.ChangeFragmentEvent;
import tk.com.sharemusic.event.ChatReviewDeleteEvent;
import tk.com.sharemusic.event.ChatReviewEvent;
import tk.com.sharemusic.myview.CircleImage;
import tk.com.sharemusic.myview.MyGridView;
import tk.com.sharemusic.myview.dialog.ClickMenuView;
import tk.com.sharemusic.myview.dialog.ImgPreviewDialog;
import tk.com.sharemusic.myview.dialog.VideoPreviewDialog;
import tk.com.sharemusic.network.BaseResult;
import tk.com.sharemusic.network.HttpMethod;
import tk.com.sharemusic.network.NetWorkService;
import tk.com.sharemusic.network.RxSchedulers;
import tk.com.sharemusic.network.response.AddReviewVo;
import tk.com.sharemusic.network.response.ChatReviewVo;
import tk.com.sharemusic.network.response.GetPublicDataShareIdVo;
import tk.com.sharemusic.network.response.GoodsResultVo;
import tk.com.sharemusic.network.rxjava.BaseObserver;
import tk.com.sharemusic.utils.DateUtil;
import tk.com.sharemusic.utils.PopWinUtil;
import tk.com.sharemusic.utils.ToastUtil;

public class ShareDetailActivity extends CommonActivity {

    @BindView(R.id.iv_head)
    CircleImage ivHead;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.ll_share_people)
    LinearLayout llSharePeople;
    @BindView(R.id.tv_song_name)
    TextView tvSongName;
    @BindView(R.id.tv_song_des)
    TextView tvSongDes;
    @BindView(R.id.ll_share_music)
    LinearLayout llShareMusic;
    @BindView(R.id.iv_good)
    ImageView ivGood;
    @BindView(R.id.tv_goods)
    TextView tvGoods;
    @BindView(R.id.tv_review)
    TextView tvReview;
    @BindView(R.id.iv_review)
    LinearLayout ivReview;
    @BindView(R.id.iv_share)
    ImageView ivShare;
    @BindView(R.id.flex_good)
    FlexboxLayout flexGood;
    @BindView(R.id.ll_goods)
    LinearLayout llGoods;
    @BindView(R.id.rcy_review)
    RecyclerView rcyReview;
    @BindView(R.id.et_review)
    EditText etReview;
    @BindView(R.id.tv_send)
    TextView tvSend;
    @BindView(R.id.tv_text)
    TextView tvText;
    @BindView(R.id.rl_text)
    RelativeLayout rlText;
    @BindView(R.id.tv_text2)
    TextView tvText2;
    @BindView(R.id.iv_img)
    NiceImageView ivImg;
    @BindView(R.id.mgd_pic)
    MyGridView mgdPic;
    @BindView(R.id.ll_share_pic)
    LinearLayout llSharePic;
    @BindView(R.id.tv_text3)
    TextView tvText3;
    @BindView(R.id.iv_video)
    ImageView ivVideo;
    @BindView(R.id.iv_play)
    ImageView ivPlay;
    @BindView(R.id.rl_play)
    RelativeLayout rlPlay;
    @BindView(R.id.ll_share_video)
    LinearLayout llShareVideo;
    @BindView(R.id.rl_video_ready)
    RelativeLayout rlVideoReady;
    @BindView(R.id.video_view)
    VideoView videoView;
    @BindView(R.id.pb_load)
    ProgressBar pbLoad;
    @BindView(R.id.rl_video_play)
    RelativeLayout rlVideoPlay;
    @BindView(R.id.iv1)
    NiceImageView iv1;
    @BindView(R.id.iv2)
    NiceImageView iv2;
    @BindView(R.id.iv3)
    NiceImageView iv3;
    @BindView(R.id.iv4)
    NiceImageView iv4;
    @BindView(R.id.ll_iv4)
    LinearLayout llIv4;
    @BindView(R.id.tv_where)
    TextView tvWhere;
    @BindView(R.id.tv_distance)
    TextView tvDistance;
    @BindView(R.id.rl_location)
    LinearLayout rlLocation;

    private SocialPublicEntity publicEntity = new SocialPublicEntity();
    private Context mContext;
    private NetWorkService service;
    private String shareId;
    private List<ReviewEntity> reviewEntities = new ArrayList<>();
    private ReviewAdapter reviewAdapter;
    private List<GoodsEntity> goodsList = new ArrayList<>();
    private InputMethodManager inputManager;
    private boolean isReviewChat = false;
    private ReviewEntity review;
    private int reviewPos;
    private boolean isReviewChatItem = false;
    private ChatReviewEntity chatReviewEntity;
    private PopWinUtil uiPopWinUtil;
    private Handler mHandler = new Handler();
    private boolean fromMsg = false;
    private String msgId = "";
    private HttpProxyCacheServer proxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_detail);
        ButterKnife.bind(this);
        mContext = this;
        service = HttpMethod.getInstance().create(NetWorkService.class);
        EventBus.getDefault().register(this);
        ivGood.setImageResource(R.drawable.goods_bg);
        uiPopWinUtil = new PopWinUtil(this);
        uiPopWinUtil.setShade(true);
        inputManager = (InputMethodManager) etReview.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        String from = getIntent().getStringExtra("from");
        if (!TextUtils.isEmpty(from) && from.equals("msgActivity")) {
            fromMsg = true;
            msgId = getIntent().getStringExtra("msgId");
        }
        initView();
        initRcyView();
        initData();
    }

    private void showInputTips() {
        etReview.setFocusable(true);
        etReview.setFocusableInTouchMode(true);
        etReview.requestFocus();
        inputManager.showSoftInput(etReview, InputMethodManager.SHOW_FORCED);
    }

    private void initView() {
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                pbLoad.setVisibility(View.GONE);
                videoView.start();
            }
        });
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                rlVideoPlay.setVisibility(View.GONE);
                rlVideoReady.setVisibility(View.VISIBLE);
                ToastUtil.showShortMessage(mContext, "视频播放出错");
                return false;
            }
        });
        etReview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(etReview.getText().toString().trim())) {
                    tvSend.setVisibility(View.GONE);
                } else {
                    tvSend.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initRcyView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcyReview.setLayoutManager(linearLayoutManager);

        reviewAdapter = new ReviewAdapter(R.layout.layout_review, reviewEntities);
        rcyReview.setAdapter(reviewAdapter);
        reviewAdapter.addChildClickViewIds(R.id.tv_name, R.id.iv_head);
        reviewAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                switch (view.getId()) {
                    case R.id.iv_head:
                    case R.id.tv_name:
                        String peopleId = reviewEntities.get(position).getPeopleId();
                        if (peopleId.equals(ShareApplication.user.getUserId())) {
                            return;
                        }
                        Intent intent1 = new Intent(mContext, PeopleProfileActivity.class);
                        intent1.putExtra("peopleId", peopleId);
                        intent1.putExtra("from", "public");
                        startActivity(intent1);
                        break;
                }
            }
        });
        reviewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                review = reviewEntities.get(position);
                if (review.getPeopleId().equals(ShareApplication.user.getUserId())) {
                    inputManager.hideSoftInputFromWindow(etReview.getWindowToken(), 0);
                    ClickMenuView menuView = new ClickMenuView(mContext);
                    menuView.setClickListener(new ClickMenuView.ItemClickListener() {
                        @Override
                        public void cancel() {
                            uiPopWinUtil.dismissMenu();
                        }

                        @Override
                        public void delete() {
                            deleteReView(review, position);
                            uiPopWinUtil.dismissMenu();
                        }

                        @Override
                        public void report() {
                            uiPopWinUtil.dismissMenu();
                        }

                        @Override
                        public void replay() {

                        }
                    });
                    uiPopWinUtil.showPopupBottom(menuView.getView(), R.id.rl_detail_public);
                } else {
                    if (publicEntity.getUserId().equals(ShareApplication.user.getUserId())) {
                        ClickMenuView menuView = new ClickMenuView(mContext);
                        menuView.showReplay(View.VISIBLE);
                        menuView.setClickListener(new ClickMenuView.ItemClickListener() {
                            @Override
                            public void cancel() {
                                uiPopWinUtil.dismissMenu();
                            }

                            @Override
                            public void delete() {
                                deleteReView(review, position);
                                uiPopWinUtil.dismissMenu();
                            }

                            @Override
                            public void report() {
                                uiPopWinUtil.dismissMenu();
                            }

                            @Override
                            public void replay() {
                                int pos = position + 1;
                                reviewPos = position;
                                etReview.setText("");
                                etReview.setHint("回复" + pos + "楼 " + reviewEntities.get(position).getPeopleName());
                                isReviewChat = true;
                                isReviewChatItem = false;
                                uiPopWinUtil.dismissMenu();
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        showInputTips();
                                    }
                                }, 500);
                            }
                        });
                        uiPopWinUtil.showPopupBottom(menuView.getView(), R.id.rl_detail_public);
                    } else {
                        int pos = position + 1;
                        reviewPos = position;
                        etReview.setText("");
                        etReview.setHint("回复" + pos + "楼 " + reviewEntities.get(position).getPeopleName());
                        isReviewChat = true;
                        isReviewChatItem = false;
                        showInputTips();
                    }

                }
            }
        });
    }

    private void initData() {
        proxy = ShareApplication.getProxy(this);
        shareId = getIntent().getStringExtra("shareId");
        if (TextUtils.isEmpty(shareId)) {
            ToastUtil.showLongMessage(mContext, "获取失败");
            finish();
        }
        if (!fromMsg) {
            getData();
        } else {
            getMsgData();
        }
    }

    private void getMsgData() {
        service.getShareMsg(shareId, msgId)
                .compose(RxSchedulers.compose(mContext))
                .subscribe(new BaseObserver<GetPublicDataShareIdVo>() {
                    @Override
                    public void onSuccess(GetPublicDataShareIdVo getPublicDataShareIdVo) {
                        publicEntity = getPublicDataShareIdVo.getData();
                        loadData();
                    }

                    @Override
                    public void onFailed(String msg) {
                        ToastUtil.showShortMessage(mContext, msg);
                    }
                });
    }

    private void getData() {
        service.getByShareId(shareId)
                .compose(RxSchedulers.<GetPublicDataShareIdVo>compose(mContext))
                .subscribe(new BaseObserver<GetPublicDataShareIdVo>(mContext) {
                    @Override
                    public void onSuccess(GetPublicDataShareIdVo getPublicDataShareIdVo) {
                        publicEntity = getPublicDataShareIdVo.getData();
                        loadData();
                    }

                    @Override
                    public void onFailed(String msg) {
                        ToastUtil.showShortMessage(mContext, msg);
                    }
                });
    }

    private void loadData() {
        if (tvName == null)
            return;
        Glide.with(mContext)
                .load(TextUtils.isEmpty(publicEntity.getUserHead()) ? Gender.getImage(publicEntity.getUserSex()) : NetWorkService.homeUrl + publicEntity.getUserHead())
                .apply(Constants.headOptions)
                .into(ivHead);
        tvName.setText(publicEntity.getUserName());
        tvTime.setText(DateUtil.getPublicTime(publicEntity.getCreateTime()));
        if (publicEntity.getType().equals(Constants.SHARE_MUSIC)) {
            llShareMusic.setVisibility(View.VISIBLE);
            rlText.setVisibility(View.GONE);
            llShareVideo.setVisibility(View.GONE);
            llSharePic.setVisibility(View.GONE);

            tvSongName.setText(publicEntity.getShareName());
            tvSongDes.setText(publicEntity.getShareText());
        } else if (publicEntity.getType().equals(Constants.SHARE_TEXT)) {
            llShareMusic.setVisibility(View.GONE);
            rlText.setVisibility(View.VISIBLE);
            llShareVideo.setVisibility(View.GONE);
            llSharePic.setVisibility(View.GONE);

            tvText.setText(publicEntity.getShareText());
        } else if (publicEntity.getType().equals(Constants.SHARE_PIC)) {
            llShareMusic.setVisibility(View.GONE);
            rlText.setVisibility(View.GONE);
            llShareVideo.setVisibility(View.GONE);
            llSharePic.setVisibility(View.VISIBLE);

            if (TextUtils.isEmpty(publicEntity.getShareText())) {
                tvText2.setVisibility(View.GONE);
            } else {
                tvText2.setVisibility(View.VISIBLE);
                tvText2.setText(publicEntity.getShareText());
            }
            String[] split = publicEntity.getShareUrl().split(";");
            if (split.length == 1) {
                ivImg.setVisibility(View.VISIBLE);
                mgdPic.setVisibility(View.GONE);
                llIv4.setVisibility(View.GONE);

                GlideUrl url = new GlideUrl(NetWorkService.homeUrl + split[0], new LazyHeaders.Builder()
                        .build());
                Glide.with(this)
                        .load(url)
                        .apply(Constants.picLoadOptions)
                        .into(ivImg);
            } else if (split.length == 4) {
                ivImg.setVisibility(View.GONE);
                mgdPic.setVisibility(View.GONE);
                llIv4.setVisibility(View.VISIBLE);

                GlideUrl url1 = new GlideUrl(NetWorkService.homeUrl + split[0], new LazyHeaders.Builder()
                        .build());
                Glide.with(mContext)
                        .load(url1)
                        .apply(Constants.picLoadOptions)
                        .into(iv1);

                GlideUrl url2 = new GlideUrl(NetWorkService.homeUrl + split[1], new LazyHeaders.Builder()
                        .build());
                Glide.with(mContext)
                        .load(url2)
                        .apply(Constants.picLoadOptions)
                        .into(iv2);

                GlideUrl url3 = new GlideUrl(NetWorkService.homeUrl + split[2], new LazyHeaders.Builder()
                        .build());
                Glide.with(mContext)
                        .load(url3)
                        .apply(Constants.picLoadOptions)
                        .into(iv3);

                GlideUrl url4 = new GlideUrl(NetWorkService.homeUrl + split[3], new LazyHeaders.Builder()
                        .build());
                Glide.with(mContext)
                        .load(url4)
                        .apply(Constants.picLoadOptions)
                        .into(iv4);
            } else {
                ivImg.setVisibility(View.GONE);
                mgdPic.setVisibility(View.VISIBLE);
                llIv4.setVisibility(View.GONE);
                initGridView(split);
            }
        } else if (publicEntity.getType().equals(Constants.SHARE_VIDEO)) {
            llShareMusic.setVisibility(View.GONE);
            rlText.setVisibility(View.GONE);
            llShareVideo.setVisibility(View.VISIBLE);
            llSharePic.setVisibility(View.GONE);

            if (TextUtils.isEmpty(publicEntity.getShareText())) {
                tvText3.setVisibility(View.GONE);
            } else {
                tvText3.setVisibility(View.VISIBLE);
                tvText3.setText(publicEntity.getShareText());
            }

            GlideUrl url = new GlideUrl(NetWorkService.homeUrl + publicEntity.getShareUrl(), new LazyHeaders.Builder()
                    .build());
            Glide.with(this)
                    .load(url)
                    .apply(Constants.picLoadOptions)
                    .into(ivVideo);
        }

        if (publicEntity.getLongitude() != null && publicEntity.getLatitude() != null) {
            if (publicEntity.getLatitude() != 0.0 || publicEntity.getLongitude() != 0.0 || !TextUtils.isEmpty(publicEntity.getLocation())) {
                rlLocation.setVisibility(View.VISIBLE);
                tvWhere.setText(publicEntity.getLocation());
                if (ShareApplication.showLocation) {
                    tvDistance.setVisibility(View.VISIBLE);
                    tvDistance.setText(ShareApplication.getDistance(ShareApplication.latitude,
                            ShareApplication.longitude, publicEntity.getLatitude(), publicEntity.getLongitude()));
                } else {
                    tvDistance.setVisibility(View.GONE);
                }
            } else {
                rlLocation.setVisibility(View.GONE);
            }
        } else {
            rlLocation.setVisibility(View.GONE);
        }

        List<ReviewEntity> reviewEntities = publicEntity.getReviewEntities();
        if (reviewEntities.isEmpty()) {
            tvReview.setText("评论");
        } else {
            tvReview.setText(reviewEntities.size() + "");
            reviewAdapter.addData(reviewEntities);
        }

        rlVideoReady.setVisibility(View.GONE);
        rlVideoPlay.setVisibility(View.VISIBLE);
        playVideo();
        goodsList.addAll(publicEntity.getGoodsList());
        if (goodsList.isEmpty()) {
            tvGoods.setText("赞");
            llGoods.setVisibility(View.GONE);
        } else {
            tvGoods.setText(goodsList.size() + "");
            llGoods.setVisibility(View.VISIBLE);
            showGoodsView();
        }
        if (!publicEntity.getUserId().equals(ShareApplication.user.getUserId())) {
            showInputTips();
        }
    }

    private void initGridView(String[] split) {
        List<ShareGvEntity> shareLists = new ArrayList<>();
        for (String str : split) {
            shareLists.add(new ShareGvEntity(str, "net"));
        }
        SharePicAdapter adapter = new SharePicAdapter(shareLists, this);
        mgdPic.setAdapter(adapter);
        mgdPic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<String> list = new ArrayList<>();
                String[] split = publicEntity.getShareUrl().split(";");
                for (String str : split) {
                    list.add(str);
                }
                ImgPreviewDialog dialog = new ImgPreviewDialog(mContext, list);
                dialog.setPhotoViewClick(new ImgPreviewDialog.PhotoViewClick() {
                    @Override
                    public void ImgClick() {
                        dialog.dismiss();
                    }
                });
                dialog.setShowPos(position);
                dialog.show();
            }
        });
    }

    private void showGoodsView() {
        flexGood.removeAllViews();
        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(R.drawable.goodsd_bg);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(ScreenUtils.dip2px(mContext, 20), ScreenUtils.dip2px(mContext, 20)));
        flexGood.addView(imageView, 0);

        boolean isHave = false;
        boolean finish = false;
        int index = 0;
        for (GoodsEntity goodsEntity : goodsList) {
            if (!isHave) {
                if (goodsEntity.getPeopleId().equals(ShareApplication.user.getUserId())) {
                    ivGood.setImageResource(R.drawable.goodsd_bg);
                    isHave = true;
                    if (finish)
                        break;
                } else {
                    ivGood.setImageResource(R.drawable.goods_bg);
                }
            }

            if (!finish) {
                if (index < ShareApplication.showCount) {
                    addGoodsView(goodsEntity, index, goodsList.size());
                } else {
                    addDesView(index, goodsList.size());
                    finish = true;
                    if (isHave)
                        break;
                }
                index++;
            }
        }
    }

    private void addGoodsView(GoodsEntity goodsEntity, int index, int allCount) {
        TextView textView = new TextView(mContext);
        textView.setTextColor(Color.BLUE);
        textView.setBackground(getResources().getDrawable(R.drawable.text_view_pressed));
        if (allCount <= ShareApplication.showCount) {
            if (index == allCount - 1) {
                textView.setText(goodsEntity.getPeopleName());
            } else {
                textView.setText(goodsEntity.getPeopleName() + "、");
            }
        } else {
            if (index == ShareApplication.showCount - 1) {
                textView.setText(goodsEntity.getPeopleName());
            } else {
                textView.setText(goodsEntity.getPeopleName() + "、");
            }
        }
        FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = ScreenUtils.dip2px(mContext, 6);
        int marginTop = ScreenUtils.dip2px(mContext, 5);
        int bottom = ScreenUtils.dip2px(mContext, 3);
        layoutParams.setMargins(margin, marginTop, margin, bottom);
        textView.setLayoutParams(layoutParams);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (goodsEntity.getPeopleId().equals(ShareApplication.user.getUserId())) {
                    EventBus.getDefault().post(new ChangeFragmentEvent(MainActivity.PAGE_MINE));
                    return;
                }
                Intent intent1 = new Intent(mContext, PeopleProfileActivity.class);
                intent1.putExtra("peopleId", goodsEntity.getPeopleId());
                intent1.putExtra("from", "public");
                mContext.startActivity(intent1);
            }
        });
        flexGood.addView(textView, index + 1);
    }

    private void addDesView(int index, int allCount) {
        TextView textView = new TextView(mContext);
        textView.setTextColor(Color.BLACK);
        textView.setText("等" + allCount + "人觉得很赞");
        FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = ScreenUtils.dip2px(mContext, 6);
        int marginTop = ScreenUtils.dip2px(mContext, 5);
        int bottom = ScreenUtils.dip2px(mContext, 3);
        layoutParams.setMargins(margin, marginTop, margin, bottom);
        textView.setLayoutParams(layoutParams);
        flexGood.addView(textView, index + 1);
    }

    @OnClick({R.id.btn_back, R.id.ll_share_people, R.id.ll_share_music, R.id.ll_good, R.id.iv_review,
            R.id.rcy_review, R.id.tv_send, R.id.rl_play, R.id.iv_img, R.id.iv1, R.id.iv2, R.id.iv3, R.id.iv4})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                ShareDetailActivity.this.finish();
                break;
            case R.id.ll_share_people:
                if (publicEntity.getUserId().equals(ShareApplication.user.getUserId())) {
                    return;
                }
                Intent intent1 = new Intent(mContext, PeopleProfileActivity.class);
                intent1.putExtra("peopleId", publicEntity.getUserId());
                intent1.putExtra("from", "public");
                startActivity(intent1);
                break;
            case R.id.ll_share_music:
                Intent intent = new Intent(mContext, PlayerSongActivity.class);
                intent.putExtra("url", publicEntity.getShareUrl());
                startActivity(intent);
                break;
            case R.id.ll_good:
                addGoods();
                break;
            case R.id.iv_review:
                isReviewChat = false;
                isReviewChatItem = false;
                etReview.setText("");
                etReview.setHint("评论");
                showInputTips();
                break;
            case R.id.rcy_review:
                break;
            case R.id.tv_send:
                inputManager.hideSoftInputFromWindow(etReview.getWindowToken(), 0);
                if (!isReviewChat) {
                    sendReview();
                } else {
                    HashMap map = new HashMap();
                    if (isReviewChatItem) {
                        if (chatReviewEntity == null)
                            return;
                        map.put("reviewId", chatReviewEntity.getReviewId());
                        map.put("talkId", ShareApplication.user.getUserId());
                        map.put("toId", chatReviewEntity.getTalkId());
                    } else {
                        if (review == null)
                            return;
                        map.put("reviewId", review.getReviewId());
                        map.put("talkId", ShareApplication.user.getUserId());
                        map.put("toId", review.getPeopleId());
                    }
                    map.put("chatText", etReview.getText().toString());
                    sendReviewChat(map);
                }
                break;
            case R.id.rl_play:
                VideoPreviewDialog dialog2 = new VideoPreviewDialog(this);
                String path = publicEntity.getShareUrl();
                dialog2.setVideo(NetWorkService.homeUrl + path);
                dialog2.show();
                break;
            case R.id.iv_img:
                List<String> list = new ArrayList<>();
                list.add(publicEntity.getShareUrl());
                ImgPreviewDialog dialog = new ImgPreviewDialog(mContext, list);
                dialog.setPhotoViewClick(new ImgPreviewDialog.PhotoViewClick() {
                    @Override
                    public void ImgClick() {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
            case R.id.iv1:
                showPreImg(0);
                break;
            case R.id.iv2:
                showPreImg(1);
                break;
            case R.id.iv3:
                showPreImg(2);
                break;
            case R.id.iv4:
                showPreImg(3);
                break;
        }
    }

    private void showPreImg(int pos) {
        List<String> list = new ArrayList<>();
        String[] split = publicEntity.getShareUrl().split(";");
        for (String str : split) {
            list.add(str);
        }
        ImgPreviewDialog dialog = new ImgPreviewDialog(mContext, list);
        dialog.setPhotoViewClick(new ImgPreviewDialog.PhotoViewClick() {
            @Override
            public void ImgClick() {
                dialog.dismiss();
            }
        });
        dialog.setShowPos(pos);
        dialog.show();
    }

    private void playVideo() {
        String proxyUrl = proxy.getProxyUrl(NetWorkService.homeUrl + publicEntity.getShareUrl());
        videoView.setVideoPath(proxyUrl);
    }

    private void sendReviewChat(HashMap map) {
        service.addReviewChat(map)
                .compose(RxSchedulers.<ChatReviewVo>compose(mContext))
                .subscribe(new BaseObserver<ChatReviewVo>(mContext) {
                    @Override
                    public void onSuccess(ChatReviewVo chatReviewVo) {
                        if (reviewEntities == null)
                            return;
                        if (etReview == null)
                            return;
                        isReviewChat = false;
                        isReviewChatItem = false;
                        etReview.setText("");
                        etReview.setHint("评论");

                        reviewEntities.get(reviewPos).getChatReviewList().add(chatReviewVo.getData());
                        reviewAdapter.notifyItemChanged(reviewPos);
                    }

                    @Override
                    public void onFailed(String msg) {
                        ToastUtil.showShortMessage(mContext, msg);
                    }
                });
    }

    private void addGoods() {
        boolean ishave = false;
        for (int i = 0; i < goodsList.size(); i++) {
            GoodsEntity goodsEntity = goodsList.get(i);
            if (goodsEntity.getPeopleId().equals(ShareApplication.user.getUserId())) {
                int finalI = i;
                service.goodsCancel(goodsEntity.getGoodsId())
                        .compose(RxSchedulers.<BaseResult>compose(mContext))
                        .subscribe(new BaseObserver<BaseResult>() {
                            @Override
                            public void onSuccess(BaseResult baseResult) {
                                if (goodsList == null)
                                    return;
                                goodsList.remove(goodsEntity);
                                ivGood.setImageResource(R.drawable.goods_bg);
                                if (goodsList.isEmpty()) {
                                    tvGoods.setText("赞");
                                    llGoods.setVisibility(View.GONE);
                                } else {
                                    llGoods.setVisibility(View.VISIBLE);
                                    tvGoods.setText(goodsList.size() + "");
                                }
                                showGoodsView();
                            }

                            @Override
                            public void onFailed(String msg) {
                                ToastUtil.showShortMessage(mContext, "取消失败");
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
            goodsEntity.setPublicId(publicEntity.getShareId());
            service.goodsAdd(goodsEntity)
                    .compose(RxSchedulers.<GoodsResultVo>compose(mContext))
                    .subscribe(new BaseObserver<GoodsResultVo>() {
                        @Override
                        public void onSuccess(GoodsResultVo goodsResultVo) {
                            if (llGoods == null)
                                return;
                            llGoods.setVisibility(View.VISIBLE);
                            goodsList.add(goodsResultVo.getData());
                            tvGoods.setText(goodsList.size() + "");
                            ivGood.setImageResource(R.drawable.goodsd_bg);
                            showGoodsView();
                            ToastUtil.showShortMessage(mContext, "点赞成功");
                        }

                        @Override
                        public void onFailed(String msg) {
                            ToastUtil.showShortMessage(mContext, "点赞失败");
                        }
                    });
        }
    }

    private void sendReview() {
        HashMap map = new HashMap();
        map.put("peopleId", ShareApplication.getUser().getUserId());
        map.put("publicId", publicEntity.getShareId());
        map.put("reviewText", etReview.getText().toString());
        service.addReview(map)
                .compose(RxSchedulers.<AddReviewVo>compose(mContext))
                .subscribe(new BaseObserver<AddReviewVo>(mContext) {
                    @Override
                    public void onSuccess(AddReviewVo addReviewVo) {
                        if (etReview == null)
                            return;
                        etReview.setText("");
                        reviewAdapter.addData(reviewEntities.size(), addReviewVo.getData());
                        if (reviewEntities.isEmpty()) {
                            tvReview.setText("评论");
                        } else {
                            tvReview.setText(reviewEntities.size() + "");
                        }
                    }

                    @Override
                    public void onFailed(String msg) {
                        ToastUtil.showShortMessage(mContext, "评论失败");
                    }
                });
    }

    @Subscribe
    public void refreshChatView(ChatReviewEvent event) {
        if (event != null) {
            isReviewChat = false;
            isReviewChatItem = true;
            chatReviewEntity = event.chatReviewEntity;
            showInputTips();
            reviewPos = event.pos;
            etReview.setText("");
            etReview.setHint("评论" + chatReviewEntity.getTalkName());
            isReviewChat = true;
        }
    }

    @Subscribe
    public void deleteChatView(ChatReviewDeleteEvent event) {
        if (event != null) {
            ChatReviewEntity chatReviewEntity = event.chatReviewEntity;
            inputManager.hideSoftInputFromWindow(etReview.getWindowToken(), 0);
            ClickMenuView menuView = new ClickMenuView(mContext);
            menuView.setClickListener(new ClickMenuView.ItemClickListener() {
                @Override
                public void cancel() {
                    uiPopWinUtil.dismissMenu();
                }

                @Override
                public void delete() {
                    deleteChatView(chatReviewEntity, event.itemPos, event.chatPos);
                    uiPopWinUtil.dismissMenu();
                }

                @Override
                public void report() {
                    uiPopWinUtil.dismissMenu();
                }

                @Override
                public void replay() {

                }
            });
            uiPopWinUtil.showPopupBottom(menuView.getView(), R.id.rl_detail_public);
        }
    }

    private void deleteReView(ReviewEntity entity, int itemPos) {
        service.deleteReview(entity.getReviewId())
                .compose(RxSchedulers.<BaseResult>compose(mContext))
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult baseResult) {
                        ToastUtil.showShortMessage(mContext, "评论已删除");
                        if (reviewAdapter == null)
                            return;
                        reviewAdapter.removeAt(itemPos);
                        if (reviewEntities.isEmpty()) {
                            tvReview.setText("评论");
                        } else {
                            tvReview.setText(reviewEntities.size() + "");
                        }
                    }

                    @Override
                    public void onFailed(String msg) {
                        ToastUtil.showShortMessage(mContext, msg);
                    }
                });
    }

    private void deleteChatView(ChatReviewEntity entity, int itemPos, int chatPos) {
        service.deleteReviewChat(entity.getReviewChatId())
                .compose(RxSchedulers.<BaseResult>compose(mContext))
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult baseResult) {
                        if (reviewEntities == null)
                            return;
                        reviewEntities.get(itemPos).getChatReviewList().remove(chatPos);
                        reviewAdapter.notifyItemChanged(itemPos);
                    }

                    @Override
                    public void onFailed(String msg) {
                        ToastUtil.showShortMessage(mContext, msg);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}