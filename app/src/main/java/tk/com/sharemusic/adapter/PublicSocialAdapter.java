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
import tk.com.sharemusic.entity.SocialPublicEntity;
import tk.com.sharemusic.enums.Gender;
import tk.com.sharemusic.myview.CircleImage;
import tk.com.sharemusic.network.NetWorkService;

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
                .load(TextUtils.isEmpty(socialPublicEntity.getUserhead())? Gender.getImage(socialPublicEntity.getUsersex()): NetWorkService.homeUrl+socialPublicEntity.getUserhead())
                .apply(options)
                .into((CircleImage) baseViewHolder.getView(R.id.iv_head));
        baseViewHolder.setText(R.id.tv_name,socialPublicEntity.getUsername());
        baseViewHolder.setText(R.id.tv_song_name,socialPublicEntity.getSharename());
        baseViewHolder.setText(R.id.tv_song_des,socialPublicEntity.getSharetext());
    }
}
