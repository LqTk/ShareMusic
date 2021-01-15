package tk.com.sharemusic.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.android.flexbox.FlexboxLayout;
import com.luck.picture.lib.tools.ScreenUtils;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import tk.com.sharemusic.R;
import tk.com.sharemusic.ShareApplication;
import tk.com.sharemusic.activity.MainActivity;
import tk.com.sharemusic.activity.PeopleProfileActivity;
import tk.com.sharemusic.config.Constants;
import tk.com.sharemusic.entity.GoodsEntity;
import tk.com.sharemusic.entity.SocialPublicEntity;
import tk.com.sharemusic.enums.Gender;
import tk.com.sharemusic.event.ChangeFragmentEvent;
import tk.com.sharemusic.myview.CircleImage;
import tk.com.sharemusic.network.NetWorkService;
import tk.com.sharemusic.utils.DateUtil;

public class PublicSocialAdapter extends BaseQuickAdapter<SocialPublicEntity, BaseViewHolder> {

    public PublicSocialAdapter(int layoutResId, @Nullable List<SocialPublicEntity> data) {
        super(layoutResId, data);
    }

    public PublicSocialAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, SocialPublicEntity socialPublicEntity) {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.default_head_boy);
        Glide.with(getContext())
                .load(TextUtils.isEmpty(socialPublicEntity.getUserHead())? Gender.getImage(socialPublicEntity.getUserSex()): NetWorkService.homeUrl+socialPublicEntity.getUserHead())
                .apply(options)
                .into((CircleImage) baseViewHolder.getView(R.id.iv_head));
        baseViewHolder.setText(R.id.tv_name,socialPublicEntity.getUserName());
        baseViewHolder.setText(R.id.tv_song_name,socialPublicEntity.getShareName());
        baseViewHolder.setText(R.id.tv_song_des,socialPublicEntity.getShareText());
        baseViewHolder.setText(R.id.tv_time,DateUtil.getPublicTime(socialPublicEntity.getCreateTime()));
        baseViewHolder.setImageResource(R.id.iv_good, R.drawable.goods_bg);
        if (socialPublicEntity.getReviewEntities().isEmpty()){
            baseViewHolder.setText(R.id.tv_review,"评论");
        }else {
            baseViewHolder.setText(R.id.tv_review,socialPublicEntity.getReviewEntities().size()+"");
        }
        if(socialPublicEntity.getGoodsList().isEmpty()){
            baseViewHolder.setText(R.id.tv_goods,"赞");
            baseViewHolder.setGone(R.id.ll_goods,true);
        }else {
            baseViewHolder.setText(R.id.tv_goods,socialPublicEntity.getGoodsList().size()+"");
            baseViewHolder.setGone(R.id.ll_goods,false);
            int index = 0;
            FlexboxLayout flexboxLayout = baseViewHolder.getView(R.id.flex_good);
            flexboxLayout.removeAllViews();
            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(R.drawable.goodsd_bg);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(ScreenUtils.dip2px(getContext(),20),ScreenUtils.dip2px(getContext(),20)));
            flexboxLayout.addView(imageView,0);
            boolean isHave = false;
            boolean finish = false;
            for (GoodsEntity goodsEntity:socialPublicEntity.getGoodsList()){
                if (!isHave) {
                    if (goodsEntity.getPeopleId().equals(ShareApplication.user.getUserId())) {
                        baseViewHolder.setImageResource(R.id.iv_good, R.drawable.goodsd_bg);
                        isHave = true;
                        if (finish)
                            break;
                    } else {
                        baseViewHolder.setImageResource(R.id.iv_good, R.drawable.goods_bg);
                    }
                }

                if (!finish) {
                    if (index < ShareApplication.showCount) {
                        addGoodsView(goodsEntity, baseViewHolder, index, socialPublicEntity.getGoodsList().size());
                    } else {
                        addDesView(baseViewHolder, index, socialPublicEntity.getGoodsList().size());
                        finish = true;
                        if (isHave)
                            break;
                    }
                    index++;
                }
            }
        }
    }

    private void addGoodsView(GoodsEntity goodsEntity, BaseViewHolder baseViewHolder, int index, int allCount) {
        TextView textView = new TextView(getContext());
        textView.setTextColor(Color.BLUE);
        if (allCount<=ShareApplication.showCount){
            if (index == allCount-1){
                textView.setText(goodsEntity.getPeopleName());
            }else {
                textView.setText(goodsEntity.getPeopleName() + "、");
            }
        }else {
            if (index == ShareApplication.showCount-1){
                textView.setText(goodsEntity.getPeopleName());
            }else {
                textView.setText(goodsEntity.getPeopleName() + "、");
            }
        }
        FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = ScreenUtils.dip2px(getContext(),6);
        int marginTop =ScreenUtils.dip2px(getContext(),5);
        int bottom =ScreenUtils.dip2px(getContext(),3);
        layoutParams.setMargins(margin, marginTop, margin, bottom);
        textView.setLayoutParams(layoutParams);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (goodsEntity.getPeopleId().equals(ShareApplication.user.getUserId())){
                    EventBus.getDefault().post(new ChangeFragmentEvent(MainActivity.PAGE_MINE));
                    return;
                }
                Intent intent1 = new Intent(getContext(), PeopleProfileActivity.class);
                intent1.putExtra("peopleId",goodsEntity.getPeopleId());
                intent1.putExtra("from","public");
                getContext().startActivity(intent1);
            }
        });
        ((FlexboxLayout)baseViewHolder.getView(R.id.flex_good)).addView(textView,index+1);
    }

    private void addDesView(BaseViewHolder baseViewHolder, int index, int allCount) {
        TextView textView = new TextView(getContext());
        textView.setTextColor(Color.BLACK);
        textView.setText("等"+allCount+"人觉得很赞");
        FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = ScreenUtils.dip2px(getContext(),6);
        int marginTop =ScreenUtils.dip2px(getContext(),5);
        int bottom =ScreenUtils.dip2px(getContext(),3);
        layoutParams.setMargins(margin, marginTop, margin, bottom);
        textView.setLayoutParams(layoutParams);
        ((FlexboxLayout)baseViewHolder.getView(R.id.flex_good)).addView(textView,index+1);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, SocialPublicEntity item, @NotNull List<?> payloads) {
        if (payloads.isEmpty()){
            convert(baseViewHolder,item);
            return;
        }
        for (Object payload:payloads){
            switch (String.valueOf(payload)){
                case "goods":
                    if(item.getGoodsList().isEmpty()){
                        baseViewHolder.setImageResource(R.id.iv_good,R.drawable.goods_bg);
                        baseViewHolder.setText(R.id.tv_goods,"赞");
                        baseViewHolder.setGone(R.id.ll_goods,true);
                    }else {
                        baseViewHolder.setText(R.id.tv_goods,item.getGoodsList().size()+"");
                        baseViewHolder.setGone(R.id.ll_goods,false);
                        int index = 0;
                        FlexboxLayout flexboxLayout = baseViewHolder.getView(R.id.flex_good);
                        flexboxLayout.removeAllViews();
                        ImageView imageView = new ImageView(getContext());
                        imageView.setImageResource(R.drawable.goodsd_bg);
                        imageView.setLayoutParams(new LinearLayout.LayoutParams(ScreenUtils.dip2px(getContext(),20),ScreenUtils.dip2px(getContext(),20)));
                        flexboxLayout.addView(imageView,0);
                        boolean isHave = false;
                        boolean finish = false;
                        for (GoodsEntity goodsEntity:item.getGoodsList()){
                            if (!isHave) {
                                if (goodsEntity.getPeopleId().equals(ShareApplication.user.getUserId())) {
                                    baseViewHolder.setImageResource(R.id.iv_good, R.drawable.goodsd_bg);
                                    isHave = true;
                                    if (finish)
                                        break;
                                } else {
                                    baseViewHolder.setImageResource(R.id.iv_good, R.drawable.goods_bg);
                                }
                            }
                            if (!finish) {
                                if (index < ShareApplication.showCount) {
                                    addGoodsView(goodsEntity, baseViewHolder, index, item.getGoodsList().size());
                                } else {
                                    addDesView(baseViewHolder, index, item.getGoodsList().size());
                                    finish = true;
                                    if (isHave)
                                        break;
                                }
                                index++;
                            }
                        }
                    }
                    break;
                case "review":
                    baseViewHolder.setText(R.id.tv_review,item.getReviewEntities().size()+"");
                    break;
            }
        }
    }
}
