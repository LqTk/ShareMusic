package tk.com.sharemusic.config;

import android.Manifest;
import android.content.Context;
import android.os.Environment;

import com.bumptech.glide.request.RequestOptions;

import java.io.File;

import tk.com.sharemusic.R;

public class Constants {
    /**
     * 聊天内容类型
     */
    public static final String MODE_TEXT = "TEXT";
    public static final String MODE_VOICE = "VOICE";
    public static final String MODE_IMAGE = "PIC";
    public static final String MODE_VIDEO = "VIDEO";

    /**
     * 分享类型
     */
    public static final String SHARE_MUSIC = "music";
    public static final String SHARE_TEXT = "text";
    public static final String SHARE_PIC = "pic";
    public static final String SHARE_VIDEO = "video";

    public static final String VOICE_DIR = "voice";

    //拍照
    public static final int IMAGE_TAKE_PHOTO = 0;
    //相册
    public static final int IMAGE_CHOOSE_FROM_ALBUM = 1;

    //权限请求返回
    public static final int PERMISSION_REQUEST_CODE = 110;

    //跳转locationActivity
    public static final int LocationRequestCode = 233;

    /**
     * 未读消息类型
     */
    public static final String MSG_REVIEW = "review";
    public static final String MSG_REVIEW_CHAT = "review_chat";
    public static final String MSG_GOODS = "goods";

    /**
     * 权限请求
     */

    public final static String[] PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public final static String[] PERMISSIONSCAMERA = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    /**
     * 分享类型 1所有人，2自己，3隐身
     */
    public static final int PEOPLE_ALL = 1;
    public static final int PEOPLE_MINE = 2;
    public static final int PEOPLE_STEALTH = 3;

    /**
     * @param bMute 值为true时为关闭背景音乐。
     */
    public static boolean muteAudioFocus(Context context, boolean bMute) {
        boolean bool = false;
        android.media.AudioManager am = (android.media.AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (bMute) {
            int result = am.requestAudioFocus(null, android.media.AudioManager.STREAM_MUSIC, android.media.AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            bool = result == android.media.AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        } else {
            int result = am.abandonAudioFocus(null);
            bool = result == android.media.AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        }
        return bool;
    }


    /**
     * glide图片加载配置
     */
    public static RequestOptions picLoadOptions = new RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.spinner)
            .error(R.drawable.picture_icon_data_error);
    public static RequestOptions headOptions = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.default_head_girl)
                .error(R.drawable.default_head_girl);

}
