package tk.com.sharemusic.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import tk.com.sharemusic.R;

/**
 * 避免Toast消息提示按照队列来重复提示
 * 
 */
public class ToastUtil {
    private static Handler handler = new Handler(Looper.getMainLooper());
    
    private static Toast toast = null;
    
    private static Object synObj = new Object();
    
    /**
     * @param ctx
     *            使用时的上下文
     * @param msg
     *            提示文字
     */
    public static void showShortMessage(final Context ctx,
                                   final String msg) {
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                handler.post(new Runnable() {
                    
                    @Override
                    public void run() {
                        synchronized (synObj) {
                            if (toast != null) {
                                toast.setText(msg);
                                toast.setDuration(Toast.LENGTH_SHORT);
                            }
                            else {
                                toast = Toast.makeText(ctx, msg, Toast.LENGTH_SHORT);
                            }
                            toast.setGravity(Gravity.BOTTOM, 0, 100);
                            toast.show();
                        }
                    }
                });
            }
        }).start();
    }
    
    /**
     * @param ctx
     *            使用时的上下文
     * @param msg
     *            提示文字
     */
    public static void showLongMessage(final Context ctx,
                                   final String msg) {
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                handler.post(new Runnable() {
                    
                    @Override
                    public void run() {
                        synchronized (synObj) {
                            if (toast != null) {
                                toast.setText(msg);
                                toast.setDuration(Toast.LENGTH_LONG);
                            }
                            else {
                                toast = Toast.makeText(ctx, msg, Toast.LENGTH_LONG);
                            }
                            toast.setGravity(Gravity.BOTTOM, 0, 100);
                            toast.show();
                        }
                    }
                });
            }
        }).start();
    }
    
    
	/**
	 * 吐出指定的视图，使其显示较长的时间
	 * @param context
	 * @param view
	 */
	public static final void toastL(Context context, View view){
		Toast toast = new Toast(context);
		toast.setView(view);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.show();
	}
	
	/**
	 * 吐出指定的视图，使其显示较短的时间
	 * @param context
	 * @param view
	 */
	public static final void toastS(Context context, View view){
		Toast toast = new Toast(context);
		toast.setView(view);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.show();
	}
	
	/**
	 * 吐出一个显示时间较长的提示
	 * @param context 上下文对象
	 * @param resId 显示内容资源ID
	 */
	public static final void toastL(Context context, int resId){
		Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
	}

	/**
	 * 吐出一个显示时间较短的提示
	 * @param context 上下文对象
	 * @param resId 显示内容资源ID
	 */
	public static final void toastS(Context context, int resId){
		Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 吐出一个显示时间较长的提示
	 * @param context 上下文对象
	 * @param content 显示内容
	 */
	public static final void toastL(Context context, String content){
		Toast.makeText(context, content, Toast.LENGTH_LONG).show();
	}

	/**
	 * 吐出一个显示时间较短的提示
	 * @param context 上下文对象
	 * @param content 显示内容
	 */
	public static final void toastS(Context context, String content){
		Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 吐出一个显示时间较长的提示
	 * @param context 上下文对象
	 * @param formatResId 被格式化的字符串资源的ID
	 * @param args 参数数组
	 */
	public static final void toastL(Context context, int formatResId, Object... args){
		Toast.makeText(context, String.format(context.getString(formatResId), args), Toast.LENGTH_LONG).show();
	}

	/**
	 * 吐出一个显示时间较短的提示
	 * @param context 上下文对象
	 * @param formatResId 被格式化的字符串资源的ID
	 * @param args 参数数组
	 */
	public static final void toastS(Context context, int formatResId, Object... args){
		Toast.makeText(context, String.format(context.getString(formatResId), args), Toast.LENGTH_SHORT).show();
	}

	/**
	 * 吐出一个显示时间较长的提示
	 * @param context 上下文对象
	 * @param format 被格式化的字符串
	 * @param args 参数数组
	 */
	public static final void toastL(Context context, String format, Object... args){
		Toast.makeText(context, String.format(format, args), Toast.LENGTH_LONG).show();
	}

	/**
	 * 吐出一个显示时间较短的提示
	 * @param context 上下文对象
	 * @param format 被格式化的字符串
	 * @param args 参数数组
	 */
	public static final void toastS(Context context, String format, Object... args){
		Toast.makeText(context, String.format(format, args), Toast.LENGTH_SHORT).show();
	}

    /**
     * 距离顶部height高度显示TOAST
     * @param context
     * @param msg
     * @param height
     */
    public static final void showToastOnTopS(final Context context, final String msg, final int height){
        new Thread(new Runnable() {

            @Override
            public void run() {
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        synchronized (synObj) {
                            if (toast != null) {
                                toast.setText(msg);
                                toast.setDuration(Toast.LENGTH_SHORT);
                            }
                            else {
                                toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
                            }
                            toast.setGravity(Gravity.TOP, 0, height);
                            toast.show();
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * 居中显示TOAST
     * @param context
     * @param msg
     */
    public static final void showToastInCenter(final Context context, final String msg){
        new Thread(new Runnable() {

            @Override
            public void run() {
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        synchronized (synObj) {
                            if (toast != null) {
                                toast.setText(msg);
                                toast.setDuration(Toast.LENGTH_SHORT);
                            }
                            else {
                                toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
                            }
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * 居中显示自定义Toast
     * @param context
     * @param imgId
     * @param des
     */
    public static final void showToastMyView(Context context, int imgId, String des){
        View toastView = LayoutInflater.from(context).inflate(R.layout.toast_image_layout, null);
        ImageView iv = toastView.findViewById(R.id.iv_show);
        TextView tv = toastView.findViewById(R.id.tv_message);
        iv.setImageResource(imgId);
        tv.setText(des);
        Toast toast = new Toast(context);   //上下文
        toast.setGravity(Gravity.CENTER, 0, 0);   //位置居中
        toast.setDuration(Toast.LENGTH_LONG);  //设置短暂提示
        toast.setView(toastView);   //把定义好的View布局设置到Toast里面
        toast.show();
    }

}
