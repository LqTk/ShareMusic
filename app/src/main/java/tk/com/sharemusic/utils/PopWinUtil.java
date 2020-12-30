package tk.com.sharemusic.utils;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import tk.com.sharemusic.R;

public class PopWinUtil {
    private Activity mActivity;
    private PopupWindow popupWindow = null;
    private boolean shade = true;
    private OnShowListener onShowListener;

    public PopWinUtil(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void setShade(boolean shade) {
        this.shade = shade;
    }

    public void showPopupMenu(View view, int containerId){
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(R.style.popuStyle);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
                lp.alpha = 1f;
                mActivity.getWindow().setAttributes(lp);
            }
        });

        if (popupWindow!=null && !popupWindow.isShowing()){
            popupWindow.showAtLocation(mActivity.findViewById(containerId), Gravity.BOTTOM,0,0);
        }
        if (onShowListener!=null){
            onShowListener.onShow();
        }
        if (shade){
            ColorDrawable cd = new ColorDrawable(0x000000);
            popupWindow.setBackgroundDrawable(cd);
            WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
            lp.alpha = 0.6f;
            mActivity.getWindow().setAttributes(lp);
        }
    }
    /**
     */
    public void dismissMenu() {
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 1f;
        mActivity.getWindow().setAttributes(lp);

        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }

        if (onShowListener != null) {
            onShowListener.onDismiss();
        }
    }

    public interface OnShowListener{
        public void onShow();
        public void onDismiss();
    }
}
