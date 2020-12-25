package tk.com.sharemusic.utils;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class StringUtils {

    /**
     * 字符串转换成requestBOdy 用于图文上传
     * @param value
     * @return
     */
    public static RequestBody toRequestBody(String value){
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"),value);
        return requestBody;
    }
}
