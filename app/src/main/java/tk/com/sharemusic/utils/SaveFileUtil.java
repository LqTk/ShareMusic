package tk.com.sharemusic.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class SaveFileUtil {

    public static File getVoiceFile(Context context, String userId, String partnerId){
        return context.getExternalFilesDir(Environment.DIRECTORY_MUSIC+File.separator+userId+File.separator+partnerId);
    }

    public static void saveFile(Context context, String dirName, String fileName){
        File filesDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(filesDir+"/"+ dirName + "/"+fileName);
        if (file.exists()){

        }
    }
}
