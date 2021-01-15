package tk.com.sharemusic.myview.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import tk.com.sharemusic.R;

public class ProgressDialog extends Dialog {
    Context context;
    TextView textView;

    public ProgressDialog(@NonNull Context context) {
        this(context,R.style.ProgressDialog);
    }

    public ProgressDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    protected ProgressDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private void init(Context context) {
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_progress_dialog,null);
        ImageView imageView = (ImageView) view.findViewById(R.id.spinnerImageView);
        AnimationDrawable spinner = (AnimationDrawable) imageView.getBackground();
        spinner.start();
        textView = view.findViewById(R.id.message);
        setContentView(view);
    }

    public void setTextView(String msg) {
        if (textView!=null)
            textView.setText(msg);
    }
}
