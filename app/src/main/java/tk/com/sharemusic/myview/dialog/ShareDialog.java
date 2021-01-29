package tk.com.sharemusic.myview.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tk.com.sharemusic.R;

public class ShareDialog extends Dialog {

    View view;
    TextView tvText;
    TextView tvMusic;
    LinearLayout llCamera;
    TextView tvAlbum;

    ClickListener clickListener;

    public ShareDialog(@NonNull Context context) {
        this(context, R.style.commonDialog);
    }

    public ShareDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        initView(context);
    }

    protected ShareDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private void initView(Context context) {
        setCancelable(true);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(true);

        view = LayoutInflater.from(context).inflate(R.layout.layout_share_dialog, null);
        tvText = view.findViewById(R.id.tv_text);
        tvMusic = view.findViewById(R.id.tv_music);
        tvAlbum = view.findViewById(R.id.tv_album);
        llCamera = view.findViewById(R.id.ll_camera);
        tvText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener!=null)
                    clickListener.textClick();
            }
        });
        tvMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener!=null)
                    clickListener.musicClick();
            }
        });
        tvAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener!=null)
                    clickListener.albumClick();
            }
        });
        llCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener!=null)
                    clickListener.cameraClick();
            }
        });

        setContentView(view);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener{
        void textClick();
        void musicClick();
        void cameraClick();
        void albumClick();
    }
}
