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
import tk.com.sharemusic.entity.MsgEntiti;
import tk.com.sharemusic.enums.Gender;
import tk.com.sharemusic.myview.CircleImage;
import tk.com.sharemusic.network.NetWorkService;

public class PartnerAdapter extends BaseQuickAdapter<MsgEntiti, BaseViewHolder> {
    public PartnerAdapter(int layoutResId, @Nullable List<MsgEntiti> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, MsgEntiti peopleEntity) {
        baseViewHolder.setText(R.id.tv_name,peopleEntity.getPeopleName());
        baseViewHolder.setText(R.id.tv_des,peopleEntity.getPeopleDes());
        Glide.with(getContext())
                .load(TextUtils.isEmpty(peopleEntity.getPeopleHead()) ? Gender.getImage(peopleEntity.getPeopleSex()) : NetWorkService.homeUrl+peopleEntity.getPeopleHead())
                .apply(new RequestOptions()
                        .centerCrop()
                        .error(R.drawable.default_head_boy))
                .into((CircleImage) baseViewHolder.getView(R.id.iv_head));
    }
}
