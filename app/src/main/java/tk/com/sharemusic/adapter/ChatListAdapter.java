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
import tk.com.sharemusic.utils.DateUtil;

public class ChatListAdapter extends BaseQuickAdapter<ChatEntity, BaseViewHolder> {
    public ChatListAdapter(int layoutResId, @Nullable List<ChatEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, ChatEntity chatEntity) {
        if (TextUtils.isEmpty(chatEntity.senderAvatar)){
            Glide.with(getContext())
                    .load(R.drawable.default_head_girl)
                    .into((CircleImage) baseViewHolder.getView(R.id.iv_partner_head));
        }else {
            Glide.with(getContext())
                    .load(NetWorkService.homeUrl + chatEntity.senderAvatar)
                    .apply(new RequestOptions()
                            .centerCrop()
                            .error(R.drawable.default_head_girl))
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
        baseViewHolder.setText(R.id.tv_chat_time, DateUtil.getChatTime(chatEntity.chatTime));
        if (chatEntity.count>0){
            baseViewHolder.setVisible(R.id.tv_count,true);
        }else {
            baseViewHolder.setGone(R.id.tv_count,true);
        }
        if (chatEntity.count>99) {
            baseViewHolder.setText(R.id.tv_count, "99+");
        }else {
            baseViewHolder.setText(R.id.tv_count, chatEntity.count + "");
        }
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, ChatEntity item, @NotNull List<?> payloads) {
        if (payloads.isEmpty()){
            convert(holder,item);
            return;
        }
        holder.setText(R.id.tv_chat_time, DateUtil.getChatTime(item.chatTime));
        if (item.count>0){
            holder.setVisible(R.id.tv_count,true);
        }else {
            holder.setGone(R.id.tv_count,true);
        }
        if (item.count>99) {
            holder.setText(R.id.tv_count, "99+");
        }else {
            holder.setText(R.id.tv_count, item.count + "");
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
                case "desName":
                    if (item.msgType.equals(Constants.MODE_IMAGE)) {
                        holder.setText(R.id.tv_partner_chat, "[图片]");
                    }else if (item.msgType.equals(Constants.MODE_VOICE)){
                        holder.setText(R.id.tv_partner_chat, "[语音]");
                    }else {
                        holder.setText(R.id.tv_partner_chat, item.msgContent);
                    }
                    holder.setText(R.id.tv_partner_name,item.senderName);
                    break;
                case "desAvr":
                    if (item.msgType.equals(Constants.MODE_IMAGE)) {
                        holder.setText(R.id.tv_partner_chat, "[图片]");
                    }else if (item.msgType.equals(Constants.MODE_VOICE)){
                        holder.setText(R.id.tv_partner_chat, "[语音]");
                    }else {
                        holder.setText(R.id.tv_partner_chat, item.msgContent);
                    }
                    if (TextUtils.isEmpty(item.senderAvatar)){
                        Glide.with(getContext())
                                .load(R.drawable.default_head_girl)
                                .into((CircleImage) holder.getView(R.id.iv_partner_head));
                    }else {
                        Glide.with(getContext())
                                .load(NetWorkService.homeUrl + item.senderAvatar)
                                .apply(new RequestOptions()
                                        .centerCrop()
                                        .error(R.drawable.default_head_girl))
                                .into((CircleImage) holder.getView(R.id.iv_partner_head));
                    }
                    break;
            }
        }
    }

    public int getAllMsgCount(){
        int count = 0;
        for (ChatEntity chatEntity:getData()){
            count = count + chatEntity.count;
        }
        return count;
    }

    public void clearMsgCount(){
        for (ChatEntity chatEntity:getData()){
            chatEntity.count = 0;
        }
    }
}
