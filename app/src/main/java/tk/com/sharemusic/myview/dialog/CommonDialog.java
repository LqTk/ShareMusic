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

public class CommonDialog extends Dialog {
    private View content;
    private TextView tvTitle;
    private TextView tv1;
    private TextView tv2;
    private TextView tvCancel;

    private OnClick onClick;

    public CommonDialog(@NonNull Context context) {
        this(context, R.style.commonDialog);
    }

    public CommonDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    protected CommonDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    private void init(Context context) {
        setCancelable(false);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        content = LayoutInflater.from(context).inflate(R.layout.common_dialog_layout, null);

        tvTitle = content.findViewById(R.id.tv_title);
        tv1 = content.findViewById(R.id.tv_text1);
        tv2 = content.findViewById(R.id.tv_text2);
        tvCancel = content.findViewById(R.id.tv_cancel);

        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClick!=null)
                    onClick.tv1Onclick();
            }
        });
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClick!=null)
                    onClick.tv2Onclick();
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClick!=null)
                    onClick.tvCancelClick();
            }
        });
        setContentView(content);
    }

    public void setTitle(String text1){
        if (tvTitle==null)
            return;
        tvTitle.setText(text1);
    }

    public void setOnClick(OnClick click){
        this.onClick = click;
    }

    public void setText1(String text1){
        if (tv1==null)
            return;
        tv1.setText(text1);
    }

    public void setText2(String text2){
        if (tv2==null)
            return;
        tv2.setText(text2);
    }

    public interface OnClick{
        void tv1Onclick();
        void tv2Onclick();
        void tvCancelClick();
    }
}
