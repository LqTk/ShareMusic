package tk.com.sharemusic.network.rxjava;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import tk.com.sharemusic.network.BaseResult;
import tk.com.sharemusic.utils.NetworkStatusManager;

public abstract class BaseObserver<T> implements Observer<T> {
    private Context context;

    public BaseObserver() {
    }

    public BaseObserver(Context context) {
        this.context = context;
    }

    @Override
    public void onSubscribe(Disposable d) {
        if (context!=null){
            if (NetworkStatusManager.getInstance().detectNetwork(context) == NetworkStatusManager.NETWORK_CLASS_UNKNOWN){
                onFailed("请检查网络");
                d.dispose();
                return;
            }
        }
    }

    @Override
    public void onNext(T t) {
        String json = new Gson().toJson(t);
        BaseResult baseResult = new Gson().fromJson(json,BaseResult.class);
        if (baseResult.getStatus()==0){
            onSuccess(t);
        }else {
            onFailed(baseResult.getMsg());
        }
    }

    @Override
    public void onError(Throwable e) {
        if (context != null && !(e instanceof ConnectException) && !(e instanceof SocketTimeoutException)){
            Toast.makeText(context.getApplicationContext(), e.getMessage(),Toast.LENGTH_LONG).show();
        }

        onFailed("网络错误");
    }

    @Override
    public void onComplete() {

    }

    public abstract void onSuccess(T t);

    public abstract void onFailed(String msg);
}
