package tk.com.sharemusic.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.android.flexbox.FlexboxLayout;
import com.luck.picture.lib.tools.ScreenUtils;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import tk.com.sharemusic.R;
import tk.com.sharemusic.ShareApplication;
import tk.com.sharemusic.activity.MainActivity;
import tk.com.sharemusic.activity.PeopleProfileActivity;
import tk.com.sharemusic.config.Constants;
import tk.com.sharemusic.entity.GoodsEntity;
import tk.com.sharemusic.entity.ShareGvEntity;
import tk.com.sharemusic.entity.SocialPublicEntity;
import tk.com.sharemusic.enums.Gender;
import tk.com.sharemusic.event.ChangeFragmentEvent;
import tk.com.sharemusic.myview.CircleImage;
import tk.com.sharemusic.myview.MyGridView;
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
        Glide.with(getContext())
                .load(TextUtils.isEmpty(socialPublicEntity.getUserHead())? Gender.getImage(socialPublicEntity.getUserSex()): NetWorkService.homeUrl+socialPublicEntity.getUserHead())
                .apply(Constants.headOptions)
                .into((CircleImage) baseViewHolder.getView(R.id.iv_head));
        baseViewHolder.setText(R.id.tv_name,socialPublicEntity.getUserName());
        if (socialPublicEntity.getType().equals(Constants.SHARE_MUSIC)) {
            baseViewHolder.setVisible(R.id.ll_share_music,true);
            baseViewHolder.setGone(R.id.rl_text,true);
            baseViewHolder.setGone(R.id.ll_share_video,true);
            baseViewHolder.setGone(R.id.ll_share_pic,true);

            baseViewHolder.setText(R.id.tv_song_name, socialPublicEntity.getShareName());
            baseViewHolder.setText(R.id.tv_song_des, socialPublicEntity.getShareText());
        }else if (socialPublicEntity.getType().equals(Constants.SHARE_TEXT)){
            baseViewHolder.setGone(R.id.ll_share_music,true);
            baseViewHolder.setGone(R.id.ll_share_pic,true);
            baseViewHolder.setGone(R.id.ll_share_video,true);
            baseViewHolder.setVisible(R.id.rl_text,true);

            baseViewHolder.setText(R.id.tv_text,socialPublicEntity.getShareText());
        }else if (socialPublicEntity.getType().equals(Constants.SHARE_PIC)){
            baseViewHolder.setGone(R.id.ll_share_music,true);
            baseViewHolder.setGone(R.id.rl_text,true);
            baseViewHolder.setGone(R.id.ll_share_video,true);
            baseViewHolder.setVisible(R.id.ll_share_pic,true);

            if (TextUtils.isEmpty(socialPublicEntity.getShareText())){
                baseViewHolder.setGone(R.id.tv_text2,true);
            }else {
                baseViewHolder.setVisible(R.id.tv_text2,true);
                baseViewHolder.setText(R.id.tv_text2, socialPublicEntity.getShareText());
            }
            String[] split = socialPublicEntity.getShareUrl().split(";");
            if (split.length>1) {
                baseViewHolder.setGone(R.id.iv_img,true);
                baseViewHolder.setVisible(R.id.mgd_pic, true);
                initGridView(baseViewHolder, split);
            }else {
                baseViewHolder.setVisible(R.id.iv_img,true);
                baseViewHolder.setGone(R.id.mgd_pic, true);

                GlideUrl url = new GlideUrl(NetWorkService.homeUrl + split[0], new LazyHeaders.Builder()
                        .build());
                Glide.with(getContext())
                        .load(url)
                        .apply(Constants.picLoadOptions)
                        .into((ImageView) baseViewHolder.getView(R.id.iv_img));
            }
        }else if (socialPublicEntity.getType().equals(Constants.SHARE_VIDEO)){
            baseViewHolder.setGone(R.id.ll_share_music,true);
            baseViewHolder.setGone(R.id.rl_text,true);
            baseViewHolder.setGone(R.id.ll_share_pic,true);
            baseViewHolder.setVisible(R.id.ll_share_video,true);

            if (TextUtils.isEmpty(socialPublicEntity.getShareText())){
                baseViewHolder.setGone(R.id.tv_text3,true);
            }else {
                baseViewHolder.setVisible(R.id.tv_text3,true);
                baseViewHolder.setText(R.id.tv_text3, socialPublicEntity.getShareText());
            }

            GlideUrl url = new GlideUrl(NetWorkService.homeUrl + socialPublicEntity.getShareUrl(), new LazyHeaders.Builder()
                    .build());
            Glide.with(getContext())
                    .load(url)
                    .apply(Constants.picLoadOptions)
                    .into((ImageView) baseViewHolder.getView(R.id.iv_video));
        }
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

    private void initGridView(BaseViewHolder baseViewHolder, String[] split) {
        MyGridView gridView = baseViewHolder.getView(R.id.mgd_pic);
        List<ShareGvEntity> shareLists = new ArrayList<>();
        /*if (split.length>2){
            gridView.setNumColumns(3);
        }else{
            gridView.setNumColumns(2);
        }*/
        for (String str:split){
            shareLists.add(new ShareGvEntity(str,"net"));
        }
        SharePicAdapter adapter = new SharePicAdapter(shareLists, getContext());
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    private void addGoodsView(GoodsEntity goodsEntity, BaseViewHolder baseViewHolder, int index, int allCount) {
        TextView textView = new TextView(getContext());
        textView.setTextColor(Color.BLUE);
        textView.setBackground(getContext().getResources().getDrawable(R.drawable.text_view_pressed));
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
