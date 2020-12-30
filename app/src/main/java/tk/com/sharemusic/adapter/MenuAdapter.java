package tk.com.sharemusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import tk.com.sharemusic.R;
import tk.com.sharemusic.entity.HeadItem;

public class MenuAdapter extends BaseAdapter {

    List<HeadItem> list;
    Context context;

    public MenuAdapter(List<HeadItem> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.menu_item,null);
            viewHolder.imageView = convertView.findViewById(R.id.iv_head);
            viewHolder.textView = convertView.findViewById(R.id.tv_des);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        HeadItem headItem = list.get(position);
        Glide.with(context)
                .load(headItem.getResId())
                .into(viewHolder.imageView);
        viewHolder.textView.setText(headItem.getName());
        return convertView;
    }

    class ViewHolder{
        ImageView imageView;
        TextView textView;
    }
}
