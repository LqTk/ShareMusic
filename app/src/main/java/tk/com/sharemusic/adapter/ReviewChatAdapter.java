package tk.com.sharemusic.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import tk.com.sharemusic.R;
import tk.com.sharemusic.entity.ChatReviewEntity;

public class ReviewChatAdapter extends BaseQuickAdapter<ChatReviewEntity, BaseViewHolder> {
    public ReviewChatAdapter(int layoutResId, @Nullable List<ChatReviewEntity> data) {
        super(layoutResId, data);
    }

    public ReviewChatAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, ChatReviewEntity chatReviewEntity) {
        baseViewHolder.setText(R.id.tv_name,chatReviewEntity.getTalkName());
        baseViewHolder.setText(R.id.tv_toName,chatReviewEntity.getToName()+"：");
        baseViewHolder.setText(R.id.tv_content,chatReviewEntity.getChatText());
        if (getData().size()>1){
            baseViewHolder.setVisible(R.id.v_line,true);
            if (chatReviewEntity.equals(getData().get(getData().size()-1))){
                baseViewHolder.setGone(R.id.v_line, true);
            }
        }else {
            baseViewHolder.setGone(R.id.v_line,true);
        }
    }
}
