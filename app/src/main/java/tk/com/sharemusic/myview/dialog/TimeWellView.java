package tk.com.sharemusic.myview.dialog;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TimeWellView extends Dialog {
    public TimeWellView(@NonNull Context context) {
        super(context);
    }

    public TimeWellView(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected TimeWellView(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
}
