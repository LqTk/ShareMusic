package tk.com.sharemusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

import java.io.File;
import java.util.List;

import tk.com.sharemusic.R;
import tk.com.sharemusic.config.Constants;
import tk.com.sharemusic.entity.ShareGvEntity;
import tk.com.sharemusic.network.NetWorkService;

public class SharePicAdapter extends BaseAdapter {

    List<ShareGvEntity> datas;
    Context context;
    OnItemChildClickListener itemClickListener;

    public SharePicAdapter(List<ShareGvEntity> datas, Context context) {
        this.datas = datas;
        this.context = context;
    }

    public void setItemChildClickListener(OnItemChildClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView==null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_share_pic,parent,false);
            viewHolder.ivPic = convertView.findViewById(R.id.iv_pic);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ShareGvEntity shareEntity = datas.get(position);

        if (shareEntity.type.equals("net")){
            GlideUrl url = new GlideUrl(NetWorkService.homeUrl + shareEntity.path, new LazyHeaders.Builder()
                    .build());
            Glide.with(context)
                    .load(url)
                    .apply(Constants.picLoadOptions)
                    .into(viewHolder.ivPic);
        }

        return convertView;
    }

    class ViewHolder{
        ImageView ivPic;
    }

    public interface OnItemChildClickListener {
        void onItemClick(View view, int pos, Object obj);
    }
}
