package tk.com.sharemusic.myview.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.luck.picture.lib.photoview.PhotoView;
import com.luck.picture.lib.tools.MediaUtils;
import com.luck.picture.lib.widget.longimage.ImageSource;
import com.luck.picture.lib.widget.longimage.ImageViewState;
import com.luck.picture.lib.widget.longimage.SubsamplingScaleImageView;

import tk.com.sharemusic.R;
import tk.com.sharemusic.config.Constants;
import tk.com.sharemusic.network.NetWorkService;

public class ImgPreviewDialog extends Dialog {
    private PhotoView imageView;
    private PhotoViewClick photoViewClick;
    private SubsamplingScaleImageView longImageView;

    public ImgPreviewDialog(@NonNull Context context) {
        this(context, R.style.imgPreviewDialog);
    }

    public ImgPreviewDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    protected ImgPreviewDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private void init(Context context) {
        setCancelable(true);
        View v = LayoutInflater.from(context).inflate(R.layout.layout_img_pre,null);
        imageView = v.findViewById(R.id.iv_pre);
        longImageView = v.findViewById(R.id.longImageView);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photoViewClick!=null)
                    photoViewClick.ImgClick();
            }
        });
        setContentView(v);
    }

    public void setImageView(String url) {
        String lowUrl = url.toLowerCase();
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
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
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

    public void setPhotoViewClick(PhotoViewClick photoViewClick) {
        this.photoViewClick = photoViewClick;
    }

    public interface PhotoViewClick{
        void ImgClick();
    }
}
