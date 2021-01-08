package tk.com.sharemusic.activity;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import tk.com.sharemusic.ShareApplication;

public class CommonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShareApplication.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ShareApplication.removeActivity(this);
    }
}
