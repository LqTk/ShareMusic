package tk.com.sharemusic.network.rxjava;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.STATIC,
                Modifier.TRANSIENT,
                Modifier.VOLATILE)
                .registerTypeAdapter(Date.class,
                        new JsonDeserializer<Date>() {
                            public Date deserialize(JsonElement json,
                                                    Type typeOfT,
                                                    JsonDeserializationContext context) throws JsonParseException {
                                /*SimpleDateFormat format = new SimpleDateFormat(pattern);
                                String dateStr = json.getAsString();
                                try {
                                    return format.parse(dateStr);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    Log.w("***response error***", e.getMessage());
                                }
                                return null;*/
                                return new Date(json.getAsJsonPrimitive().getAsLong());
                            }
                        })
                .create();
        String json = gson.toJson(t);
        BaseResult baseResult = gson.fromJson(json,BaseResult.class);
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
