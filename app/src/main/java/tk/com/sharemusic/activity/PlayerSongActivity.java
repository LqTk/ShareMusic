package tk.com.sharemusic.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import tk.com.sharemusic.R;

public class PlayerSongActivity extends AppCompatActivity {

    @BindView(R.id.web)
    WebView web;
    private Unbinder bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_song);
        bind = ButterKnife.bind(this);

        initWebView();
    }

    private void initWebView() {
        String url = getIntent().getStringExtra("url");
        web.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        WebSettings webSetting = web.getSettings();

        webSetting.setDatabaseEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setSupportZoom(true);
        webSetting.setDisplayZoomControls(false);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setBuiltInZoomControls(true);

        webSetting.setSupportMultipleWindows(true);
        webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSetting.setAppCacheEnabled(true);

        webSetting.setUseWideViewPort(true);

        webSetting.setDomStorageEnabled(true);
        webSetting.setGeolocationEnabled(true);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            webSetting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            webSetting.setBlockNetworkImage(false);
        }
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        }else {
            webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }

        web.setWebChromeClient(new WebChromeClient());
        web.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url == null) return false;

                try {
                    if (!url.startsWith("http") && !url.startsWith("https")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                        return true;
                    }
                }catch (Exception e){
                    return true;
                }
                view.loadUrl(url);
                return true;
            }
        });
        web.loadUrl(url);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }
}