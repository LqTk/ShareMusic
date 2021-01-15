package tk.com.sharemusic.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.google.android.flexbox.FlexboxLayout;
import com.luck.picture.lib.tools.ScreenUtils;

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
import tk.com.sharemusic.entity.ChatReviewEntity;
import tk.com.sharemusic.entity.GoodsEntity;
import tk.com.sharemusic.entity.ReviewEntity;
import tk.com.sharemusic.entity.SocialPublicEntity;
import tk.com.sharemusic.enums.Gender;
import tk.com.sharemusic.event.ChangeFragmentEvent;
import tk.com.sharemusic.event.ChatReviewDeleteEvent;
import tk.com.sharemusic.event.ChatReviewEvent;
import tk.com.sharemusic.myview.CircleImage;
import tk.com.sharemusic.myview.dialog.ClickMenuView;
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

public class ShareDetailActivity extends AppCompatActivity {

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
    @BindView(R.id.ll_share_content)
    LinearLayout llShareContent;
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

        initView();
        initRcyView();
        initData();
        showInputTips(etReview);
    }

    private void showInputTips(EditText et_text) {
        et_text.setFocusable(true);
        et_text.setFocusableInTouchMode(true);
        et_text.requestFocus();
        inputManager = (InputMethodManager) et_text.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(et_text, 0);
    }

    private void initView() {
        etReview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(etReview.getText().toString().trim())){
                    tvSend.setVisibility(View.GONE);
                }else {
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
        reviewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                review = reviewEntities.get(position);
                if (review.getPeopleId().equals(ShareApplication.user.getUserId())){
                    inputManager.hideSoftInputFromWindow(etReview.getWindowToken(), 0);
                    ClickMenuView menuView = new ClickMenuView(mContext);
                    menuView.setClickListener(new ClickMenuView.ItemClickListener() {
                        @Override
                        public void cancel() {
                            uiPopWinUtil.dismissMenu();
                        }

                        @Override
                        public void delete() {
                            deleteReView(review,position);
                            uiPopWinUtil.dismissMenu();
                        }

                        @Override
                        public void report() {
                            uiPopWinUtil.dismissMenu();
                        }
                    });
                    uiPopWinUtil.showPopupBottom(menuView.getView(),R.id.rl_detail_public);
                }else {
                    showInputTips(etReview);
                    int pos = position + 1;
                    reviewPos = position;
                    etReview.setText("");
                    etReview.setHint("回复" + pos + "楼 " + reviewEntities.get(position).getPeopleName());
                    isReviewChat = true;
                    isReviewChatItem = false;
                }
            }
        });
    }

    private void initData() {
        shareId = getIntent().getStringExtra("shareId");
        if (TextUtils.isEmpty(shareId)) {
            ToastUtil.showLongMessage(mContext, "获取失败");
            finish();
        }
        getData();
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
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.default_head_boy);
        Glide.with(mContext)
                .load(TextUtils.isEmpty(publicEntity.getUserHead()) ? Gender.getImage(publicEntity.getUserSex()) : NetWorkService.homeUrl + publicEntity.getUserHead())
                .apply(options)
                .into(ivHead);
        tvName.setText(publicEntity.getUserName());
        tvTime.setText(DateUtil.getPublicTime(publicEntity.getCreateTime()));
        tvSongName.setText(publicEntity.getShareName());
        tvSongDes.setText(publicEntity.getShareText());
        List<ReviewEntity> reviewEntities = publicEntity.getReviewEntities();
        if (reviewEntities.isEmpty()) {
            tvReview.setText("评论");
        } else {
            tvReview.setText(reviewEntities.size() + "");
            reviewAdapter.addData(reviewEntities);
        }

        goodsList.addAll(publicEntity.getGoodsList());
        if (goodsList.isEmpty()) {
            tvGoods.setText("赞");
            llGoods.setVisibility(View.GONE);
        } else {
            tvGoods.setText(goodsList.size() + "");
            llGoods.setVisibility(View.VISIBLE);
            showGoodsView();
        }
    }

    private void showGoodsView(){
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

    @OnClick({R.id.iv_head, R.id.ll_share_content, R.id.iv_good, R.id.iv_review, R.id.rcy_review, R.id.tv_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_head:
                break;
            case R.id.ll_share_content:
                break;
            case R.id.iv_good:
                addGoods();
                break;
            case R.id.iv_review:
                isReviewChat = false;
                isReviewChatItem = false;
                etReview.setText("");
                etReview.setHint("评论");
                break;
            case R.id.rcy_review:
                break;
            case R.id.tv_send:
                inputManager.hideSoftInputFromWindow(etReview.getWindowToken(), 0);
                if (!isReviewChat) {
                    sendReview();
                }else {
                    HashMap map = new HashMap();
                    if (isReviewChatItem){
                        if (chatReviewEntity==null)
                            return;
                        map.put("reviewId", chatReviewEntity.getReviewId());
                        map.put("talkId", ShareApplication.user.getUserId());
                        map.put("toId", chatReviewEntity.getTalkId());
                    }else {
                        if (review==null)
                            return;
                        map.put("reviewId", review.getReviewId());
                        map.put("talkId", ShareApplication.user.getUserId());
                        map.put("toId", review.getPeopleId());
                    }
                    map.put("chatText", etReview.getText().toString());
                    sendReviewChat(map);
                }
                break;
        }
    }

    private void sendReviewChat(HashMap map) {
        service.addReviewChat(map)
                .compose(RxSchedulers.<ChatReviewVo>compose(mContext))
                .subscribe(new BaseObserver<ChatReviewVo>(mContext) {
                    @Override
                    public void onSuccess(ChatReviewVo chatReviewVo) {
                        reviewEntities.get(reviewPos).getChatReviewList().add(chatReviewVo.getData());
                        reviewAdapter.notifyItemChanged(reviewPos);
                    }

                    @Override
                    public void onFailed(String msg) {
                        ToastUtil.showShortMessage(mContext,msg);
                    }
                });
    }

    private void addGoods() {
        boolean ishave = false;
        for (int i=0; i<goodsList.size();i++){
            GoodsEntity goodsEntity = goodsList.get(i);
            if (goodsEntity.getPeopleId().equals(ShareApplication.user.getUserId())){
                int finalI = i;
                service.goodsCancel(goodsEntity.getGoodsId())
                        .compose(RxSchedulers.<BaseResult>compose(mContext))
                        .subscribe(new BaseObserver<BaseResult>() {
                            @Override
                            public void onSuccess(BaseResult baseResult) {
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
                                ToastUtil.showShortMessage(mContext,"取消失败");
                            }
                        });
                ishave = true;
                break;
            }
        }
        if (!ishave){
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
                            llGoods.setVisibility(View.VISIBLE);
                            goodsList.add(goodsResultVo.getData());
                            tvGoods.setText(goodsList.size() + "");
                            ivGood.setImageResource(R.drawable.goodsd_bg);
                            showGoodsView();
                            ToastUtil.showShortMessage(mContext,"点赞成功");
                        }

                        @Override
                        public void onFailed(String msg) {
                            ToastUtil.showShortMessage(mContext,"点赞失败");
                        }
                    });
        }
    }

    private void sendReview() {
        HashMap map = new HashMap();
        map.put("peopleId",ShareApplication.getUser().getUserId());
        map.put("publicId",publicEntity.getShareId());
        map.put("reviewText",etReview.getText().toString());
        service.addReview(map)
                .compose(RxSchedulers.<AddReviewVo>compose(mContext))
                .subscribe(new BaseObserver<AddReviewVo>(mContext) {
                    @Override
                    public void onSuccess(AddReviewVo addReviewVo) {
                        etReview.setText("");
                        reviewAdapter.addData(reviewEntities.size(),addReviewVo.getData());
                        if (reviewEntities.isEmpty()){
                            tvReview.setText("评论");
                        }else {
                            tvReview.setText(reviewEntities.size()+"");
                        }
                    }

                    @Override
                    public void onFailed(String msg) {
                        ToastUtil.showShortMessage(mContext,"评论失败");
                    }
                });
    }

    @Subscribe
    public void refreshChatView(ChatReviewEvent event){
        if (event!=null){
            isReviewChat = false;
            isReviewChatItem = true;
            chatReviewEntity = event.chatReviewEntity;
            showInputTips(etReview);
            reviewPos = event.pos;
            etReview.setText("");
            etReview.setHint("评论"+chatReviewEntity.getTalkName());
            isReviewChat = true;
        }
    }

    @Subscribe
    public void deleteChatView(ChatReviewDeleteEvent event){
        if (event!=null){
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
                    deleteChatView(chatReviewEntity,event.itemPos,event.chatPos);
                    uiPopWinUtil.dismissMenu();
                }

                @Override
                public void report() {
                    uiPopWinUtil.dismissMenu();
                }
            });
            uiPopWinUtil.showPopupBottom(menuView.getView(),R.id.rl_detail_public);
        }
    }

    private void deleteReView(ReviewEntity entity, int itemPos){
        service.deleteReview(entity.getReviewId())
                .compose(RxSchedulers.<BaseResult>compose(mContext))
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult baseResult) {
                        ToastUtil.showShortMessage(mContext,"评论已删除");
                        reviewAdapter.removeAt(itemPos);
                        if (reviewEntities.isEmpty()) {
                            tvReview.setText("评论");
                        } else {
                            tvReview.setText(reviewEntities.size() + "");
                        }
                    }

                    @Override
                    public void onFailed(String msg) {
                        ToastUtil.showShortMessage(mContext,msg);
                    }
                });
    }

    private void deleteChatView(ChatReviewEntity entity, int itemPos, int chatPos){
        service.deleteReviewChat(entity.getReviewChatId())
                .compose(RxSchedulers.<BaseResult>compose(mContext))
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult baseResult) {
                        reviewEntities.get(itemPos).getChatReviewList().remove(chatPos);
                        reviewAdapter.notifyItemChanged(itemPos);
                    }

                    @Override
                    public void onFailed(String msg) {
                        ToastUtil.showShortMessage(mContext,msg);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}