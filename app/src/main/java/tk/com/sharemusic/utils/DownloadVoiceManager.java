package tk.com.sharemusic.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by AdamStone on 2016/11/25.
 */

public class DownloadVoiceManager {

    private Context context;

    public DownloadVoiceManager(Context context) {
        this.context = context;
    }

    public interface OnDownloadFinishListener {
        void onDownloadSuccess(String path);

        void onDownloadFailed();
    }

    private OnDownloadFinishListener onDownloadFinishListener;

    public void setOnDownloadFinishListener(OnDownloadFinishListener onDownloadFinishListener) {
        this.onDownloadFinishListener = onDownloadFinishListener;
    }

    public void download(String host, String urlStr, String userId, String partnerId){
        String fileName = urlStr.split("/")[2];
        OutputStream output = null;
        InputStream input = null;
        int currentSize = 0;

        HttpURLConnection httpConnection = null;

        try {
            URL url = new URL(host+urlStr);
            httpConnection = (HttpURLConnection) url.openConnection();
            if (currentSize > 0) {
                httpConnection.setRequestProperty("RANGE", "bytes="
                        + currentSize + "-");
            }
            httpConnection.setConnectTimeout(10000);
            httpConnection.setReadTimeout(20000);
            if (httpConnection.getResponseCode() == 404) {
                throw new Exception("fail!");
            }
            if (!SaveFileUtil.getVoiceFile(context,userId,partnerId).exists()){
                SaveFileUtil.getVoiceFile(context,userId,partnerId).mkdirs();
            }
            input = httpConnection.getInputStream();
            File file = new File(SaveFileUtil.getVoiceFile(context,userId,partnerId),fileName);
            output = new FileOutputStream(file, false);
            byte buffer[] = new byte[1024];
            int readsize = 0;
            while ((readsize = input.read(buffer)) > 0) {
                output.write(buffer, 0, readsize);
            }

            if (onDownloadFinishListener != null) {
                onDownloadFinishListener.onDownloadSuccess(file.getAbsolutePath());
            }

        } catch (Exception e) {
            if (onDownloadFinishListener != null) {
                onDownloadFinishListener.onDownloadFailed();
            }
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Trust every server - dont check for any certificate
     */
    private static void trustAllHosts() {
        final String TAG = "trustAllHosts";
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {

            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                Log.i(TAG, "checkClientTrusted");
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                Log.i(TAG, "checkServerTrusted");
            }
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
