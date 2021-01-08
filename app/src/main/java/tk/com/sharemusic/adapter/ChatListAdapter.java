package tk.com.sharemusic.adapter;

import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import tk.com.sharemusic.R;
import tk.com.sharemusic.config.Constants;
import tk.com.sharemusic.entity.ChatEntity;
import tk.com.sharemusic.myview.CircleImage;
import tk.com.sharemusic.network.NetWorkService;

public class ChatListAdapter extends BaseQuickAdapter<ChatEntity, BaseViewHolder> {
    public ChatListAdapter(int layoutResId, @Nullable List<ChatEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, ChatEntity chatEntity) {
        if (TextUtils.isEmpty(chatEntity.senderAvatar)){
            Glide.with(getContext())
                    .load(R.drawable.default_head_boy)
                    .into((CircleImage) baseViewHolder.getView(R.id.iv_partner_head));
        }else {
            Glide.with(getContext())
                    .load(NetWorkService.homeUrl + chatEntity.senderAvatar)
                    .apply(new RequestOptions()
                            .centerCrop()
                            .error(R.drawable.default_head_boy))
                    .into((CircleImage) baseViewHolder.getView(R.id.iv_partner_head));
        }
        baseViewHolder.setText(R.id.tv_partner_name,chatEntity.senderName);
        if (chatEntity.msgType.equals(Constants.MODE_IMAGE)) {
            baseViewHolder.setText(R.id.tv_partner_chat, "[图片]");
        }else if (chatEntity.msgType.equals(Constants.MODE_VOICE)){
            baseViewHolder.setText(R.id.tv_partner_chat, "[语音]");
        }else {
            baseViewHolder.setText(R.id.tv_partner_chat, chatEntity.msgContent);
        }
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, ChatEntity item, @NotNull List<?> payloads) {
        if (payloads.isEmpty()){
            convert(holder,item);
            return;
        }
        for (Object payload:payloads){
            switch (String.valueOf(payload)){
                case "des":
                    if (item.msgType.equals(Constants.MODE_IMAGE)) {
                        holder.setText(R.id.tv_partner_chat, "[图片]");
                    }else if (item.msgType.equals(Constants.MODE_VOICE)){
                        holder.setText(R.id.tv_partner_chat, "[语音]");
                    }else {
                        holder.setText(R.id.tv_partner_chat, item.msgContent);
                    }
                    break;
            }
        }
    }
}
