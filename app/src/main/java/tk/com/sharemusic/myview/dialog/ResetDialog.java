package tk.com.sharemusic.myview.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import tk.com.sharemusic.R;

public class ResetDialog extends Dialog {

    TextView textViewTitle;
    EditText editText;
    Button button;
    CommitListener listener;

    public ResetDialog(@NonNull Context context) {
        this(context, R.style.commonDialog);
    }

    public ResetDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    protected ResetDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private void init(Context context) {
        setCancelable(true);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View v = LayoutInflater.from(context).inflate(R.layout.layout_reset,null);
        textViewTitle = v.findViewById(R.id.tv_title);
        editText = v.findViewById(R.id.et_content);
        button = v.findViewById(R.id.btn_commit);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(editText.getText().toString().trim())){
                    button.setEnabled(true);
                }else {
                    button.setEnabled(false);
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null)
                    listener.commit(editText.getText().toString().trim());
            }
        });
        setContentView(v);
    }

    public void setTitle(String title){
        if (textViewTitle!=null){
            textViewTitle.setText(title);
        }
    }

    public void setHint(String hint){
        if (editText!=null){
            editText.setHint(hint);
        }
    }

    public void setListener(CommitListener listener) {
        this.listener = listener;
    }

    public interface CommitListener{
        void commit(String reslut);
    }
}
