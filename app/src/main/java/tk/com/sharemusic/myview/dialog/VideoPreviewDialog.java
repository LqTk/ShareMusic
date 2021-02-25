package tk.com.sharemusic.myview.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.danikula.videocache.HttpProxyCacheServer;

import tk.com.sharemusic.R;
import tk.com.sharemusic.ShareApplication;
import tk.com.sharemusic.utils.ToastUtil;

public class VideoPreviewDialog extends Dialog {

    private VideoView videoView;
    private ImageView ivLoading;
    private TextView tvError;
    private boolean isLoading = true;
    private HttpProxyCacheServer proxy;
    private int setTime = 0;
    private DismissListener dismissListener;

    public VideoPreviewDialog(@NonNull Context context) {
        this(context, R.style.imgPreviewDialog);
    }

    public VideoPreviewDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    protected VideoPreviewDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private void init(Context context) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        proxy = ShareApplication.getProxy(context);
        View v = LayoutInflater.from(context).inflate(R.layout.layout_video_pre,null);
        videoView = v.findViewById(R.id.video_view);
        videoView.setZOrderOnTop(true);
        ivLoading = v.findViewById(R.id.iv_loading);
        tvError = v.findViewById(R.id.tv_error);
        tvError.setVisibility(View.GONE);
        AnimationDrawable spinner = (AnimationDrawable) ivLoading.getBackground();
        spinner.start();
        if (isLoading){
            ivLoading.setVisibility(View.VISIBLE);
        }
        videoView.setMediaController(new MediaController(context));
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mp, int percent) {
                        Log.d("videoPercent==",percent+",current=="+videoView.getCurrentPosition()*100/videoView.getDuration());
                    }
                });
                ivLoading.setVisibility(View.GONE);
                tvError.setVisibility(View.GONE);
                if (setTime>0){
                    videoView.seekTo(setTime);
                }
                videoView.start();
            }
        });
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                ToastUtil.showShortMessage(context,"视频播放失败");
                tvError.setVisibility(View.VISIBLE);
                ivLoading.setVisibility(View.GONE);
                videoView.setVisibility(View.GONE);
                return false;
            }
        });
        setContentView(v);
    }

    public void setVideo(String url, int time){
        isLoading = true;
        if (videoView!=null){
            tvError.setVisibility(View.GONE);
            String proxyUrl = proxy.getProxyUrl(url);
            videoView.setVideoPath(proxyUrl);
            setTime = time;
//            videoView.setVideoURI(Uri.parse(url));
        }
    }

    public void setVideo(String url){
        isLoading = true;
        setTime = 0;
        if (videoView!=null){
            tvError.setVisibility(View.GONE);
            String proxyUrl = proxy.getProxyUrl(url);
            videoView.setVideoPath(proxyUrl);
//            videoView.setVideoURI(Uri.parse(url));
        }
    }

    public void setDismissListener(DismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }

    public void setLocalVideo(String path){
        isLoading = true;
        if (videoView!=null){
            tvError.setVisibility(View.GONE);
            videoView.setVideoPath(path);
        }
    }

    @Override
    public void setOnDismissListener(@Nullable OnDismissListener listener) {
        super.setOnDismissListener(listener);
    }

    @Override
    public void dismiss() {
        if (dismissListener!=null){
            dismissListener.OnDismiss(videoView.getCurrentPosition());
        }
        super.dismiss();
    }

    public interface DismissListener{
        void OnDismiss(int pos);
    }
}
