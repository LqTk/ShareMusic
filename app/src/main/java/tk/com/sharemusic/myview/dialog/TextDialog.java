package tk.com.sharemusic.myview.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import tk.com.sharemusic.R;

public class TextDialog extends Dialog {

    TextView title1;
    TextView title2;
    TextView content;
    TextView tvCancel;
    TextView tvCommit;
    private OnClickListener onClickListener;

    public TextDialog(@NonNull Context context) {
        this(context, R.style.commonDialog);
    }

    public TextDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    protected TextDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private void init(Context context) {
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View v = LayoutInflater.from(context).inflate(R.layout.layout_common_dialog,null);
        title1 = v.findViewById(R.id.tv_title1);
        title2 = v.findViewById(R.id.tv_title2);
        content = v.findViewById(R.id.content);
        tvCancel = v.findViewById(R.id.tv_cancel);
        tvCommit = v.findViewById(R.id.tv_commit);
        tvCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener!=null)
                    onClickListener.commit();
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener!=null)
                    onClickListener.cancel();
            }
        });
        setContentView(v);
    }

    public void setTitle1(String str){
        if (title1!=null) {
            title1.setVisibility(View.VISIBLE);
            title1.setText(str);
        }
    }
    public void setTitle2(String str){
        if (title2!=null){
            title2.setVisibility(View.VISIBLE);
            title2.setText(str);
        }
    }
    public void setContent(String str){
        if (content!=null)
            content.setText(str);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener{
        void commit();
        void cancel();
    }
}
