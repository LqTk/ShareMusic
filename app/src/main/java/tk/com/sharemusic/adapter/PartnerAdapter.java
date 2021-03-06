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
import tk.com.sharemusic.entity.MsgEntity;
import tk.com.sharemusic.enums.Gender;
import tk.com.sharemusic.myview.CircleImage;
import tk.com.sharemusic.network.NetWorkService;

public class PartnerAdapter extends BaseQuickAdapter<MsgEntity, BaseViewHolder> {
    public PartnerAdapter(int layoutResId, @Nullable List<MsgEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, MsgEntity peopleEntity) {
        baseViewHolder.setText(R.id.tv_name,TextUtils.isEmpty(peopleEntity.getSetName())?peopleEntity.getPeopleName():peopleEntity.getSetName());
        baseViewHolder.setText(R.id.tv_des,peopleEntity.getPeopleDes());
        Glide.with(getContext())
                .load(TextUtils.isEmpty(peopleEntity.getPeopleHead()) ? Gender.getImage(peopleEntity.getPeopleSex()) : NetWorkService.homeUrl+peopleEntity.getPeopleHead())
                .apply(Constants.headOptions)
                .into((CircleImage) baseViewHolder.getView(R.id.iv_head));
    }
}
