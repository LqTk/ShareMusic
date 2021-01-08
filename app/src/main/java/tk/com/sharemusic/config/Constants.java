package tk.com.sharemusic.config;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class Constants {
    public static final String MODE_TEXT = "TEXT";
    public static final String MODE_VOICE = "VOICE";
    public static final String MODE_IMAGE = "PIC";


    public static final String VOICE_DIR = "voice";


    /*// 默认存放文件下载的路径
    public final static String DEFAULT_SAVE_FILE_PATH = Environment
            .getExternalStorageDirectory()
            + File.separator
            + "ShareMusic"
            + File.separator + "download" + File.separator;*/

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
}
