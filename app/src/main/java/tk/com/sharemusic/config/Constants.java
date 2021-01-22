package tk.com.sharemusic.config;

import android.content.Context;
import android.os.Environment;

import com.bumptech.glide.request.RequestOptions;

import java.io.File;

import tk.com.sharemusic.R;

public class Constants {
    public static final String MODE_TEXT = "TEXT";
    public static final String MODE_VOICE = "VOICE";
    public static final String MODE_IMAGE = "PIC";


    public static final String VOICE_DIR = "voice";

    public static final int IMAGE_TAKE_PHOTO = 0;
    public static final int IMAGE_CHOOSE_FROM_ALBUM = 1;

    public static final int PERMISSION_REQUEST_CODE = 110;

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


    public static RequestOptions picLoadOptions = new RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.spinner)
            .error(R.drawable.picture_icon_data_error);
    public static RequestOptions headOptions = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.default_head_girl)
                .error(R.drawable.default_head_girl);

}
