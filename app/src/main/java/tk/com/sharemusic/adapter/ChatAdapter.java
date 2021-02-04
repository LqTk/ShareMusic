package tk.com.sharemusic.adapter;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
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
    private Handler handler;

    public ChatAdapter(int layoutResId, @Nullable List<ChatEntity> data, String partnerHead, Handler handler) {
        super(layoutResId, data);
        this.partnerHead = partnerHead;
        this.handler = handler;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, ChatEntity msgEntity) {

        if (msgEntity.isMyContent()) {
            Glide.with(getContext())
                    .load(TextUtils.isEmpty(ShareApplication.user.getHeadImg())? Gender.getImage(ShareApplication.user.getSex()): NetWorkService.homeUrl+ShareApplication.user.getHeadImg())
                    .apply(Constants.headOptions)
                    .into((CircleImage) baseViewHolder.getView(R.id.iv_avatar));
            baseViewHolder.setVisible(R.id.layout_right, true);
            baseViewHolder.setGone(R.id.layout_left, true);
        } else {
            Glide.with(getContext())
                .load(TextUtils.isEmpty(partnerHead)? Gender.getImage(1): NetWorkService.homeUrl+partnerHead)
                .apply(Constants.headOptions)
                .into((CircleImage) baseViewHolder.getView(R.id.iv_avatar_left));
            baseViewHolder.setGone(R.id.layout_right, true);
            baseViewHolder.setVisible(R.id.layout_left, true);
        }

        if (msgEntity.msgType.equals(Constants.MODE_TEXT)) {
            baseViewHolder.setVisible(R.id.tv_content,true);
            baseViewHolder.setGone(R.id.tv_voice,true);
            baseViewHolder.setGone(R.id.rl_img,true);
            baseViewHolder.setGone(R.id.rl_video,true);

            baseViewHolder.setEnabled(R.id.tv_content,false);
            baseViewHolder.setText(R.id.tv_content,msgEntity.msgContent);
        } else if (msgEntity.msgType.equals(Constants.MODE_VOICE)) {
            baseViewHolder.setGone(R.id.tv_content,true);
            baseViewHolder.setVisible(R.id.tv_voice,true);
            baseViewHolder.setGone(R.id.rl_video,true);
            baseViewHolder.setGone(R.id.rl_img,true);

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
            baseViewHolder.setGone(R.id.rl_video,true);
            baseViewHolder.setVisible(R.id.rl_img,true);

            if (!TextUtils.isEmpty(msgEntity.msgContent)) {
                if (msgEntity.isSending()){
                    File file = new File(msgEntity.msgContent);
                    Glide.with(getContext()).load(file)
//                            .apply(Constants.picLoadOptions)
                            .centerInside()
                            .into((ImageView) baseViewHolder.getView(R.id.iv_pic));
                }else {
                    if (!TextUtils.isEmpty(msgEntity.getLocalPath())){
                        File file = new File(msgEntity.getLocalPath());
                        if (file.exists()){
                            Glide.with(getContext()).load(file)
//                                    .apply(Constants.picLoadOptions)
                                    .centerInside()
                                    .into((ImageView) baseViewHolder.getView(R.id.iv_pic));
                        }else {
                            GlideUrl url = new GlideUrl(NetWorkService.homeUrl + msgEntity.msgContent, new LazyHeaders.Builder()
                                    .build());
                            Glide.with(getContext()).load(url).
                                    apply(Constants.picLoadOptions)
                                    .fitCenter()
                                    .into((ImageView) baseViewHolder.getView(R.id.iv_pic));
                        }
                    }else {
                        GlideUrl url = new GlideUrl(NetWorkService.homeUrl + msgEntity.msgContent, new LazyHeaders.Builder()
                                .build());
                        Glide.with(getContext()).load(url).
                                apply(Constants.picLoadOptions)
                                .fitCenter()
                                .into((ImageView) baseViewHolder.getView(R.id.iv_pic));
                    }
                }
            } else {
                baseViewHolder.setImageResource(R.id.iv_pic,R.drawable.picture_icon_data_error);
            }
        }else if (msgEntity.msgType.equals(Constants.MODE_VIDEO)){
            baseViewHolder.setGone(R.id.tv_content,true);
            baseViewHolder.setGone(R.id.tv_voice,true);
            baseViewHolder.setGone(R.id.rl_img,true);
            baseViewHolder.setVisible(R.id.rl_video,true);

            ImageView video = baseViewHolder.getView(R.id.video);
            if (!TextUtils.isEmpty(msgEntity.msgContent)) {
                if (msgEntity.isSending()){
                    File file = new File(msgEntity.msgContent);
                    Glide.with(getContext())
                            .asBitmap()
                            .load(file)
                            .apply(Constants.picLoadOptions)
                            .fitCenter()
                            .into(video);
                }else {
                    if (!TextUtils.isEmpty(msgEntity.getLocalPath())){
                        File file = new File(msgEntity.getLocalPath());
                        if (file.exists()){
                            Glide.with(getContext())
                                    .asBitmap()
                                    .load(file)
                                    .apply(Constants.picLoadOptions)
                                    .fitCenter()
                                    .into(video);
                        }else {
                            GlideUrl url = new GlideUrl(NetWorkService.homeUrl + msgEntity.msgContent, new LazyHeaders.Builder()
                                    .build());
                            Glide.with(getContext())
                                    .asBitmap()
                                    .load(url).
                                    apply(Constants.picLoadOptions)
                                    .fitCenter()
                                    .into(video);
                        }
                    }else {
                        GlideUrl url = new GlideUrl(NetWorkService.homeUrl + msgEntity.msgContent, new LazyHeaders.Builder()
                                .build());
                        Glide.with(getContext())
                                .asBitmap()
                                .load(url).
                                apply(Constants.picLoadOptions)
                                .fitCenter()
                                .into(video);
                    }
                }
            } else {
                baseViewHolder.setImageResource(R.id.video,R.drawable.picture_icon_data_error);
            }
        }


        if (msgEntity.msgType.equals(Constants.MODE_TEXT)) {
            baseViewHolder.setVisible(R.id.tv_content_left,true);
            baseViewHolder.setGone(R.id.tv_voice_left,true);
            baseViewHolder.setGone(R.id.iv_pic_left,true);
            baseViewHolder.setGone(R.id.rl_video_left,true);

            baseViewHolder.setEnabled(R.id.tv_content_left,false);
            baseViewHolder.setText(R.id.tv_content_left,msgEntity.msgContent);
        } else if (msgEntity.msgType.equals(Constants.MODE_VOICE)) {
            baseViewHolder.setGone(R.id.tv_content_left,true);
            baseViewHolder.setVisible(R.id.tv_voice_left,true);
            baseViewHolder.setGone(R.id.rl_video_left,true);
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
            baseViewHolder.setGone(R.id.rl_video_left,true);
            baseViewHolder.setVisible(R.id.iv_pic_left,true);

            if (!TextUtils.isEmpty(msgEntity.msgContent)) {
                if (!TextUtils.isEmpty(msgEntity.getLocalPath())){
                    File file = new File(msgEntity.getLocalPath());
                    if (file.exists()){
                        Glide.with(getContext())
                                .load(file)
                                .apply(Constants.picLoadOptions)
                                .fitCenter()
                                .into((ImageView) baseViewHolder.getView(R.id.iv_pic_left));
                    }else {
                        GlideUrl url = new GlideUrl(NetWorkService.homeUrl+msgEntity.msgContent, new LazyHeaders.Builder()
                                .build());

                        Glide.with(getContext()).load(url)
                                .apply(Constants.picLoadOptions)
                                .fitCenter()
                                .into((ImageView) baseViewHolder.getView(R.id.iv_pic_left));
                    }
                }else {
                    GlideUrl url = new GlideUrl(NetWorkService.homeUrl + msgEntity.msgContent, new LazyHeaders.Builder()
                            .build());

                    Glide.with(getContext()).load(url)
                            .apply(Constants.picLoadOptions)
                            .fitCenter()
                            .into((ImageView) baseViewHolder.getView(R.id.iv_pic_left));
                }
            } else {
                baseViewHolder.setImageResource(R.id.iv_pic_left,R.drawable.picture_icon_data_error);
            }
        }else if (msgEntity.msgType.equals(Constants.MODE_VIDEO)){
            baseViewHolder.setGone(R.id.tv_content_left,true);
            baseViewHolder.setGone(R.id.tv_voice_left,true);
            baseViewHolder.setGone(R.id.iv_pic_left,true);
            baseViewHolder.setVisible(R.id.rl_video_left,true);

            if (!TextUtils.isEmpty(msgEntity.msgContent)){
                if (!TextUtils.isEmpty(msgEntity.getLocalPath())){
                    File file = new File(msgEntity.getLocalPath());
                    if (file.exists()){
                        Glide.with(getContext())
                                .load(file)
                                .apply(Constants.picLoadOptions)
                                .fitCenter()
                                .into((ImageView) baseViewHolder.getView(R.id.video_left));
                    }else {
                        GlideUrl url = new GlideUrl(NetWorkService.homeUrl + msgEntity.msgContent, new LazyHeaders.Builder()
                                .build());

                        Glide.with(getContext()).load(url)
                                .apply(Constants.picLoadOptions)
                                .fitCenter()
                                .into((ImageView) baseViewHolder.getView(R.id.video_left));
                    }
                }else {
                    GlideUrl url = new GlideUrl(NetWorkService.homeUrl + msgEntity.msgContent, new LazyHeaders.Builder()
                            .build());

                    Glide.with(getContext()).load(url)
                            .apply(Constants.picLoadOptions)
                            .fitCenter()
                            .into((ImageView) baseViewHolder.getView(R.id.video_left));
                }
            }else {
                baseViewHolder.setImageResource(R.id.video_left,R.drawable.picture_icon_data_error);
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

        if (msgEntity.isMyContent()){
            if (msgEntity.isSending()){
                baseViewHolder.setVisible(R.id.pb_sending,true);
                baseViewHolder.setGone(R.id.iv_send_fail,true);
            }else if (msgEntity.isSendSuccess()){
                baseViewHolder.setGone(R.id.iv_send_fail,true);
                baseViewHolder.setGone(R.id.pb_sending,true);
            }else if (!msgEntity.isSendSuccess()){
                baseViewHolder.setGone(R.id.pb_sending,true);
                baseViewHolder.setVisible(R.id.iv_send_fail,true);
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
                            .apply(Constants.headOptions)
                            .into((CircleImage) holder.getView(R.id.iv_avatar_left));
                    holder.setGone(R.id.layout_right, true);
                    holder.setVisible(R.id.layout_left, true);
                    break;
                case "state":
                    if (item.isMyContent()){
                        if (item.isSending()){
                            holder.setVisible(R.id.pb_sending,true);
                            holder.setGone(R.id.iv_send_fail,true);
                        }else if (item.isSendSuccess()){
                            holder.setGone(R.id.iv_send_fail,true);
                            holder.setGone(R.id.pb_sending,true);
                        }else if (!item.isSendSuccess()){
                            holder.setGone(R.id.pb_sending,true);
                            holder.setVisible(R.id.iv_send_fail,true);
                        }
                    }
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
