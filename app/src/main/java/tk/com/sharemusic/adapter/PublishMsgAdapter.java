package tk.com.sharemusic.adapter;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.List;

import tk.com.sharemusic.R;
import tk.com.sharemusic.ShareApplication;
import tk.com.sharemusic.config.Constants;
import tk.com.sharemusic.entity.PublishMsgEntity;
import tk.com.sharemusic.enums.Gender;
import tk.com.sharemusic.myview.CircleImage;
import tk.com.sharemusic.myview.MyHtmlTagHandler;
import tk.com.sharemusic.network.NetWorkService;

public class PublishMsgAdapter extends BaseQuickAdapter<PublishMsgEntity, BaseViewHolder> {
    public PublishMsgAdapter(int layoutResId, @Nullable List<PublishMsgEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, PublishMsgEntity publishMsgEntity) {
        Glide.with(getContext())
                .load(TextUtils.isEmpty(publishMsgEntity.peopleHead)? Gender.getImage(1): NetWorkService.homeUrl+publishMsgEntity.peopleHead)
                .apply(Constants.headOptions)
                .into((CircleImage)baseViewHolder.getView(R.id.iv_head));
        String html = "";
        if (publishMsgEntity.isReaded==0){
            if (publishMsgEntity.msgType.equals(Constants.MSG_GOODS)){
                html = "<b><myfont color='#dd222222'>" + publishMsgEntity.peopleName+"</myfont>  <img src='goodsd_bg'/><myfont size='29px' color='#333333'><b>赞</b></myfont>  <myfont color='#dd222222'>了您的分享</myfont>";
            }else if (publishMsgEntity.msgType.equals(Constants.MSG_REVIEW)){
                html = "<b><myfont color='#dd222222'>" + publishMsgEntity.peopleName+"</myfont>   <myfont size='29px' color='#333333'><b>评论</b></myfont>  <myfont color='#dd222222'>"+publishMsgEntity.reviewText+"</myfont>";
            }else {
                html = "<b><myfont color='#dd222222'>" + publishMsgEntity.peopleName+"</myfont>   <myfont size='29px' color='#333333'><b>回复</b></myfont>  "+publishMsgEntity.chatText;
            }
        }else {
            if (publishMsgEntity.msgType.equals(Constants.MSG_GOODS)){
                html = "<b><myfont color='#939393'>" + publishMsgEntity.peopleName+"</myfont>  <img src='goodsd_bg'/><myfont size='28px' color='#939393'><b>赞</b></myfont>  <myfont color='#939393'>了您的分享</myfont>";
            }else if (publishMsgEntity.msgType.equals(Constants.MSG_REVIEW)){
                html = "<b><myfont color='#939393'>" + publishMsgEntity.peopleName+"</myfont>   <myfont size='28px' color='#939393'><b>评论</b></myfont>  <myfont color='#939393'>"+publishMsgEntity.reviewText+"</myfont>";
            }else {
                html = "<b><myfont color='#939393'>" + publishMsgEntity.peopleName+"</myfont>   <myfont size='28px' color='#939393'><b>回复</b></myfont>  <myfont color='#939393'>"+publishMsgEntity.chatText+"</myfont>";
            }
        }
        CharSequence charSequence = Html.fromHtml(html, new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                // 获得系统资源的信息，比如图片信息
                Drawable drawable = getContext().getResources().getDrawable(
                        getResourceId(source));
                // 处理第三个图片文件安装50%的比例压缩
                if (source.equals("goodsd_bg")) {
                    drawable.setBounds(0, 0, (int) (drawable.getIntrinsicHeight() / 1.5),
                            (int) (drawable.getIntrinsicWidth() / 1.5));
                } else {
                    drawable.setBounds(0, 0, drawable.getIntrinsicHeight(),
                            drawable.getIntrinsicWidth());
                }
                return drawable;
            }
        }, new MyHtmlTagHandler("myfont"));
        baseViewHolder.setText(R.id.tv_des,charSequence);
        baseViewHolder.setText(R.id.tv_share,publishMsgEntity.publishText);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, PublishMsgEntity item, @NotNull List<?> payloads) {
        if (payloads.isEmpty()){
            convert(holder,item);
            return;
        }
        for (Object payload:payloads){
            switch (String.valueOf(payload)){
                case "updateState":
                    String html = "";
                    if (item.isReaded==0){
                        if (item.msgType.equals(Constants.MSG_GOODS)){
                            html = "<b><myfont color='#dd222222'>" + item.peopleName+"</myfont>  <img src='goodsd_bg'/><myfont size='29px' color='#333333'><b>赞</b></myfont>  <myfont color='#dd222222'>了您的分享</myfont>";
                        }else if (item.msgType.equals(Constants.MSG_REVIEW)){
                            html = "<b><myfont color='#dd222222'>" + item.peopleName+"</myfont>   <myfont size='29px' color='#333333'><b>评论</b></myfont>  <myfont color='#dd222222'>"+item.reviewText+"</myfont>";
                        }else {
                            html = "<b><myfont color='#dd222222'>" + item.peopleName+"</myfont>   <myfont size='29px' color='#333333'><b>回复</b></myfont>  "+item.chatText;
                        }
                    }else {
                        if (item.msgType.equals(Constants.MSG_GOODS)){
                            html = "<b><myfont color='#939393'>" + item.peopleName+"</myfont>  <img src='goodsd_bg'/><myfont size='28px' color='#939393'><b>赞</b></myfont>  <myfont color='#939393'>了您的分享</myfont>";
                        }else if (item.msgType.equals(Constants.MSG_REVIEW)){
                            html = "<b><myfont color='#939393'>" + item.peopleName+"</myfont>   <myfont size='28px' color='#939393'><b>评论</b></myfont>  <myfont color='#939393'>"+item.reviewText+"</myfont>";
                        }else {
                            html = "<b><myfont color='#939393'>" + item.peopleName+"</myfont>   <myfont size='28px' color='#939393'><b>回复</b></myfont>  <myfont color='#939393'>"+item.chatText+"</myfont>";
                        }
                    }
                    CharSequence charSequence = Html.fromHtml(html, new Html.ImageGetter() {
                        @Override
                        public Drawable getDrawable(String source) {
                            // 获得系统资源的信息，比如图片信息
                            Drawable drawable = getContext().getResources().getDrawable(
                                    getResourceId(source));
                            // 处理第三个图片文件安装50%的比例压缩
                            if (source.equals("goodsd_bg")) {
                                drawable.setBounds(0, 0, (int) (drawable.getIntrinsicHeight() / 1.5),
                                        (int) (drawable.getIntrinsicWidth() / 1.5));
                            } else {
                                drawable.setBounds(0, 0, drawable.getIntrinsicHeight(),
                                        drawable.getIntrinsicWidth());
                            }
                            return drawable;
                        }
                    }, new MyHtmlTagHandler("myfont"));
                    holder.setText(R.id.tv_des,charSequence);
                    break;
            }
        }
    }

    // 根据资源ID获得Field对象
    public int getResourceId(String name) {
        try {
            // 根据资源的ID的变量名获得Field的对象，使用反射机制来实现的
            Field field = R.drawable.class.getField(name);
            // 取得并返回资源ID的字段（静态变量）的值，使用反射机制
            return Integer.parseInt(field.get(null).toString());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
