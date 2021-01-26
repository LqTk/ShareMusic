package tk.com.sharemusic.adapter;

import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import tk.com.sharemusic.R;
import tk.com.sharemusic.ShareApplication;
import tk.com.sharemusic.config.Constants;
import tk.com.sharemusic.entity.ChatReviewEntity;
import tk.com.sharemusic.enums.Gender;
import tk.com.sharemusic.myview.CircleImage;
import tk.com.sharemusic.network.NetWorkService;
import tk.com.sharemusic.utils.DateUtil;

public class ReviewChatAdapter extends BaseQuickAdapter<ChatReviewEntity, BaseViewHolder> {
    public ReviewChatAdapter(int layoutResId, @Nullable List<ChatReviewEntity> data) {
        super(layoutResId, data);
    }

    public ReviewChatAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, ChatReviewEntity chatReviewEntity) {
        if (chatReviewEntity.getTalkId().equals(ShareApplication.user.getUserId())){
            baseViewHolder.setText(R.id.tv_name, chatReviewEntity.getTalkName()+"(我)");
        }else {
            baseViewHolder.setText(R.id.tv_name, chatReviewEntity.getTalkName());
        }
        Glide.with(getContext())
                .load(TextUtils.isEmpty(chatReviewEntity.getTalkHead())? Gender.getImage(1): NetWorkService.homeUrl+chatReviewEntity.getTalkHead())
                .apply(Constants.headOptions)
                .into((CircleImage)baseViewHolder.getView(R.id.iv_head));
        if (chatReviewEntity.getToId().equals(ShareApplication.user.getUserId())){
            baseViewHolder.setText(R.id.tv_toName, chatReviewEntity.getToName() + "(我)：");
        }else {
            baseViewHolder.setText(R.id.tv_toName, chatReviewEntity.getToName() + "：");
        }
        baseViewHolder.setText(R.id.tv_content,chatReviewEntity.getChatText());
        baseViewHolder.setText(R.id.tv_time, DateUtil.getReviewTime(chatReviewEntity.getChatTime()));
    }
}
