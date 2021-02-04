package tk.com.sharemusic.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.luck.picture.lib.photoview.PhotoView;
import com.luck.picture.lib.tools.MediaUtils;
import com.luck.picture.lib.widget.longimage.ImageSource;
import com.luck.picture.lib.widget.longimage.ImageViewState;
import com.luck.picture.lib.widget.longimage.SubsamplingScaleImageView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import tk.com.sharemusic.R;
import tk.com.sharemusic.config.Constants;
import tk.com.sharemusic.network.NetWorkService;

public class PreImgAdapter extends PagerAdapter {

    private List<String> data;
    private Context context;
    private ClickListener clickListener;

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public PreImgAdapter(List<String> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View v = (View) object;

        container.removeView(v);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View v = LayoutInflater.from(context).inflate(R.layout.layout_img_pre,null);
        PhotoView imageView = v.findViewById(R.id.iv_pre);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener!=null)
                    clickListener.picClick();
            }
        });
        SubsamplingScaleImageView longImageView = v.findViewById(R.id.longImageView);
        longImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener!=null)
                    clickListener.picClick();
            }
        });
        String url = data.get(position);
        String lowUrl = url.toLowerCase();
        if (lowUrl.toLowerCase().endsWith(".gif")){
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            Glide.with(context)
                    .load(NetWorkService.homeUrl+url)
                    .apply(new RequestOptions()
                            .centerCrop()
                            .placeholder(R.drawable.spinner)
                            .override(1000,1000)
                            .error(R.drawable.picture_icon_data_error))
                    .fitCenter()
                    .into(imageView);
        }else {
            Glide.with(context)
                    .load(NetWorkService.homeUrl + url)
                    .apply(new RequestOptions()
                            .centerCrop()
                            .placeholder(R.drawable.spinner)
                            .override(1000,1000)
                            .error(R.drawable.picture_icon_data_error))
                    .fitCenter()
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @androidx.annotation.Nullable Transition<? super Drawable> transition) {
                            try {
                                BitmapDrawable bd = (BitmapDrawable) resource;
                                Bitmap bitmap = bd.getBitmap();
                                if (resource != null) {
                                    boolean eqLongImage = MediaUtils.isLongImg(bitmap.getWidth(),
                                            bitmap.getHeight());
                                    longImageView.setVisibility(eqLongImage ? View.VISIBLE : View.GONE);
                                    imageView.setVisibility(eqLongImage ? View.GONE : View.VISIBLE);
                                    if (eqLongImage) {
                                        // 加载长图
                                        longImageView.setQuickScaleEnabled(true);
                                        longImageView.setZoomEnabled(true);
                                        longImageView.setPanEnabled(true);
                                        longImageView.setDoubleTapZoomDuration(100);
                                        longImageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
                                        longImageView.setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);
                                        longImageView.setImage(ImageSource.bitmap(bitmap),
                                                new ImageViewState(0, new PointF(0, 0), 0));
                                    } else {
                                        // 普通图片
                                        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                        imageView.setImageBitmap(bitmap);
                                    }
                                }
                            }catch (Exception e){
                                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                imageView.setImageResource(R.drawable.picture_icon_data_error);
                            }

                        }
                    });
        }
        container.addView(v,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);

        return v;
    }

    public List<String> getData(){
        return data;
    }

    public interface ClickListener{
        void picClick();
    }
    /*
    public PreImgAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    public PreImgAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, String url) {
        String lowUrl = url.toLowerCase();
        ImageView imageView = baseViewHolder.getView(R.id.iv_pre);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        SubsamplingScaleImageView longImageView = baseViewHolder.getView(R.id.longImageView);
        if (lowUrl.toLowerCase().endsWith(".gif")){
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            Glide.with(getContext())
                    .load(NetWorkService.homeUrl+url)
                    .apply(Constants.picLoadOptions)
                    .fitCenter()
                    .into(imageView);
        }else {
            Glide.with(getContext())
                    .load(NetWorkService.homeUrl + url)
                    .apply(Constants.picLoadOptions)
                    .fitCenter()
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @androidx.annotation.Nullable Transition<? super Drawable> transition) {
                            try {
                                BitmapDrawable bd = (BitmapDrawable) resource;
                                Bitmap bitmap = bd.getBitmap();
                                if (resource != null) {
                                    boolean eqLongImage = MediaUtils.isLongImg(bitmap.getWidth(),
                                            bitmap.getHeight());
                                    longImageView.setVisibility(eqLongImage ? View.VISIBLE : View.GONE);
                                    imageView.setVisibility(eqLongImage ? View.GONE : View.VISIBLE);
                                    if (eqLongImage) {
                                        // 加载长图
                                        longImageView.setQuickScaleEnabled(true);
                                        longImageView.setZoomEnabled(true);
                                        longImageView.setPanEnabled(true);
                                        longImageView.setDoubleTapZoomDuration(100);
                                        longImageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
                                        longImageView.setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);
                                        longImageView.setImage(ImageSource.bitmap(bitmap),
                                                new ImageViewState(0, new PointF(0, 0), 0));
                                    } else {
                                        // 普通图片
                                        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                        imageView.setImageBitmap(bitmap);
                                    }
                                }
                            }catch (Exception e){
                                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                imageView.setImageResource(R.drawable.picture_icon_data_error);
                            }

                        }
                    });
        }
    }
*/
}
