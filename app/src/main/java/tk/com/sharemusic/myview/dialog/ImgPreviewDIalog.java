package tk.com.sharemusic.myview.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.luck.picture.lib.photoview.PhotoView;

import tk.com.sharemusic.R;
import tk.com.sharemusic.config.Constants;
import tk.com.sharemusic.network.NetWorkService;

public class ImgPreviewDIalog extends Dialog {
    private PhotoView imageView;
    private PhotoViewClick photoViewClick;

    public ImgPreviewDIalog(@NonNull Context context) {
        this(context, R.style.imgPreviewDialog);
    }

    public ImgPreviewDIalog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    protected ImgPreviewDIalog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private void init(Context context) {
        setCancelable(true);
        View v = LayoutInflater.from(context).inflate(R.layout.layout_img_pre,null);
        imageView = v.findViewById(R.id.iv_pre);
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
        Glide.with(getContext()).load(NetWorkService.homeUrl+url).apply(Constants.picLoadOptions)
                .into(imageView);
    }

    public void setPhotoViewClick(PhotoViewClick photoViewClick) {
        this.photoViewClick = photoViewClick;
    }

    public interface PhotoViewClick{
        void ImgClick();
    }
}
