package tk.com.sharemusic.adapter;

import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import tk.com.sharemusic.R;
import tk.com.sharemusic.ShareApplication;
import tk.com.sharemusic.config.Constants;
import tk.com.sharemusic.entity.ChatEntity;
import tk.com.sharemusic.enums.Gender;
import tk.com.sharemusic.myview.CircleImage;
import tk.com.sharemusic.network.NetWorkService;
import tk.com.sharemusic.utils.DateUtil;

public class ChatAdapter extends BaseQuickAdapter<ChatEntity, BaseViewHolder> {

    private String partnerHead;
    private RequestOptions options;

    public ChatAdapter(int layoutResId, @Nullable List<ChatEntity> data, String partnerHead) {
        super(layoutResId, data);
        this.partnerHead = partnerHead;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, ChatEntity msgEntity) {
        options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.default_head_girl);

        if (msgEntity.isMyContent()) {
            Glide.with(getContext())
                    .load(TextUtils.isEmpty(ShareApplication.user.getHeadImg())? Gender.getImage(ShareApplication.user.getSex()): NetWorkService.homeUrl+ShareApplication.user.getHeadImg())
                    .apply(options)
                    .into((CircleImage) baseViewHolder.getView(R.id.iv_avatar));
            baseViewHolder.setVisible(R.id.layout_right, true);
            baseViewHolder.setGone(R.id.layout_left, true);
        } else {
            Glide.with(getContext())
                .load(TextUtils.isEmpty(partnerHead)? Gender.getImage(1): NetWorkService.homeUrl+partnerHead)
                .apply(options)
                .into((CircleImage) baseViewHolder.getView(R.id.iv_avatar_left));
            baseViewHolder.setGone(R.id.layout_right, true);
            baseViewHolder.setVisible(R.id.layout_left, true);
        }

        if (msgEntity.msgType.equals(Constants.MODE_TEXT)) {
            baseViewHolder.setVisible(R.id.tv_content,true);
            baseViewHolder.setGone(R.id.tv_voice,true);
            baseViewHolder.setGone(R.id.iv_pic,true);

            baseViewHolder.setEnabled(R.id.tv_content,false);
            baseViewHolder.setText(R.id.tv_content,msgEntity.msgContent);
        } else if (msgEntity.msgType.equals(Constants.MODE_VOICE)) {
            baseViewHolder.setGone(R.id.tv_content,true);
            baseViewHolder.setVisible(R.id.tv_voice,true);
            baseViewHolder.setGone(R.id.iv_pic,true);

            int voiceTime = Integer.parseInt(msgEntity.voiceTime);

            StringBuffer blank = new StringBuffer();
            if (voiceTime > 20) voiceTime = 20;
            for (int i = 0 ; i < voiceTime ; i++ ) {
                blank.append("\t");
            }
            baseViewHolder.setText(R.id.tv_voice,blank.toString() + "'" + msgEntity.voiceTime);
        } else if (msgEntity.msgType.equals(Constants.MODE_IMAGE)) {
            baseViewHolder.setGone(R.id.tv_content,true);
            baseViewHolder.setGone(R.id.tv_voice,true);
            baseViewHolder.setVisible(R.id.iv_pic,true);

            GlideUrl url = new GlideUrl(NetWorkService.homeUrl+msgEntity.msgContent, new LazyHeaders.Builder()
                    .build());
            if (!TextUtils.isEmpty(msgEntity.msgContent)) {
                Glide.with(getContext()).load(url).
                        apply(options).into((ImageView) baseViewHolder.getView(R.id.iv_pic));
            } else {
                baseViewHolder.setImageResource(R.id.iv_pic,R.drawable.picture_icon_data_error);
            }
        }


        if (msgEntity.msgType.equals(Constants.MODE_TEXT)) {
            baseViewHolder.setVisible(R.id.tv_content_left,true);
            baseViewHolder.setGone(R.id.tv_voice_left,true);
            baseViewHolder.setGone(R.id.iv_pic_left,true);

            baseViewHolder.setEnabled(R.id.tv_content_left,false);
            baseViewHolder.setText(R.id.tv_content_left,msgEntity.msgContent);
        } else if (msgEntity.msgType.equals(Constants.MODE_VOICE)) {
            baseViewHolder.setGone(R.id.tv_content_left,true);
            baseViewHolder.setVisible(R.id.tv_voice_left,true);
            baseViewHolder.setGone(R.id.iv_pic_left,true);

            int voiceTime = Integer.parseInt(msgEntity.voiceTime);

            StringBuffer blank = new StringBuffer();
            if (voiceTime > 20) voiceTime = 20;
            for (int i = 0 ; i < voiceTime ; i++ ) {
                blank.append("\t");
            }
            baseViewHolder.setText(R.id.tv_voice_left,msgEntity.voiceTime + "'" + blank.toString());

        } else if (msgEntity.msgType.equals(Constants.MODE_IMAGE)) {
            baseViewHolder.setGone(R.id.tv_content_left,true);
            baseViewHolder.setGone(R.id.tv_voice_left,true);
            baseViewHolder.setVisible(R.id.iv_pic_left,true);

            GlideUrl url = new GlideUrl(NetWorkService.homeUrl+msgEntity.msgContent, new LazyHeaders.Builder()
                    .build());

            if (!TextUtils.isEmpty(msgEntity.msgContent)) {
                Glide.with(getContext()).load(url).
                        apply(options).into((ImageView) baseViewHolder.getView(R.id.iv_pic_left));

            } else {
                baseViewHolder.setImageResource(R.id.iv_pic_left,R.drawable.picture_icon_data_error);
            }
        }
        baseViewHolder.setGone(R.id.tv_name,true);
        baseViewHolder.setGone(R.id.tv_name_left,true);

        int index = getData().indexOf(msgEntity);
        boolean isShowTime = true;
        if (index>0){
            if (msgEntity.chatTime-getData().get(index-1).chatTime>10*60*1000){
                isShowTime = true;
            }else {
                isShowTime = false;
            }
        }


        if (!isShowTime) {
            baseViewHolder.setVisible(R.id.tv_time,false);
        } else {
            baseViewHolder.setVisible(R.id.tv_time,true);
        }
        baseViewHolder.setText(R.id.tv_time, DateUtil.getChatTime(msgEntity.chatTime));

        if (!isShowTime) {
            baseViewHolder.setVisible(R.id.tv_time_left,false);
        } else {
            baseViewHolder.setVisible(R.id.tv_time_left,true);
        }
        baseViewHolder.setText(R.id.tv_time_left,DateUtil.getChatTime(msgEntity.chatTime));
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, ChatEntity item, @NotNull List<?> payloads) {
        if (payloads.isEmpty()){
            convert(holder,item);
            return;
        }
        for (Object payload:payloads) {
            switch (String.valueOf(payload)) {
                case "headChange":
                    Glide.with(getContext())
                            .load(TextUtils.isEmpty(item.senderAvatar)? Gender.getImage(0): NetWorkService.homeUrl+item.senderAvatar)
                            .apply(options)
                            .into((CircleImage) holder.getView(R.id.iv_avatar_left));
                    holder.setGone(R.id.layout_right, true);
                    holder.setVisible(R.id.layout_left, true);
                    break;
            }
        }
    }

    public void setPartnerHead(String senderId, String partnerHead){
        this.partnerHead = partnerHead;
        boolean headChange = false;
        for (ChatEntity chatEntity:getData()){
            if (chatEntity.getSenderId().equals(senderId) && !chatEntity.getSenderAvatar().equals(partnerHead)){
                headChange = true;
                break;
            }
        }
        if (headChange) {
            int i=0;
            for (ChatEntity chatEntity : getData()) {
                if (chatEntity.getSenderId().equals(senderId) && !chatEntity.getSenderAvatar().equals(partnerHead)) {
                    chatEntity.setSenderAvatar(partnerHead);
                    notifyItemChanged(i,"headChange");
                }
                i++;
            }
        }
    }
}