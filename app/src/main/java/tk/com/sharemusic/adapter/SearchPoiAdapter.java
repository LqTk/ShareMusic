package tk.com.sharemusic.adapter;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import tk.com.sharemusic.R;
import tk.com.sharemusic.entity.SearchLocationEntity;

public class SearchPoiAdapter extends BaseQuickAdapter<SearchLocationEntity, BaseViewHolder> {
    public SearchPoiAdapter(int layoutResId, @Nullable List<SearchLocationEntity> data) {
        super(layoutResId, data);
    }

    public SearchPoiAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, SearchLocationEntity searchLocationEntity) {
        baseViewHolder.setText(R.id.tv_city,searchLocationEntity.city);
        if (TextUtils.isEmpty(searchLocationEntity.address)){
            baseViewHolder.setGone(R.id.tv_address,true);
        }else {
            baseViewHolder.setVisible(R.id.tv_address,true);
            baseViewHolder.setText(R.id.tv_address,searchLocationEntity.address);
        }
        if (searchLocationEntity.isCheck){
            baseViewHolder.setVisible(R.id.iv_select,true);
        }else {
            baseViewHolder.setGone(R.id.iv_select,true);
        }
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, SearchLocationEntity item, @NotNull List<?> payloads) {
        if (payloads.isEmpty()){
            convert(holder,item);
            return;
        }
        for (Object o:payloads){
            switch (String.valueOf(o)){
                case "state":
                    if (item.isCheck){
                        holder.setVisible(R.id.iv_select,true);
                    }else {
                        holder.setGone(R.id.iv_select,true);
                    }
                    break;
            }
        }
    }
}
