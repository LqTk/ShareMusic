package tk.com.sharemusic.network;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import tk.com.sharemusic.utils.SSLSocketFactoryUtils;

public class HttpMethod {
    private static volatile OkHttpClient client;
    private static volatile Retrofit retrofit;

    public static OkHttpClient getClient(){
        if (client==null){
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            client = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @NotNull
                        @Override
                        public Response intercept(@NotNull Chain chain) throws IOException {
                            Request request = chain.request()
                                    .newBuilder()
                                    .addHeader("contentType", "application/json")
                                    .build();
                            return chain.proceed(request);
                        }
                    })
                    .addInterceptor(loggingInterceptor)
                    .sslSocketFactory(SSLSocketFactoryUtils.createSSLSocketFactory(),SSLSocketFactoryUtils.createTrustAllManager())
                    .hostnameVerifier(new SSLSocketFactoryUtils.TrustAllHostnameVerifier())
                    .readTimeout(5, TimeUnit.MINUTES)
                    .writeTimeout(5,TimeUnit.MINUTES)
                    .connectTimeout(60,TimeUnit.SECONDS)
                    .build();
        }
        return client;
    }

    public static Retrofit getInstance(){
        if (client==null){
            synchronized (HttpMethod.class){
                getClient();
            }
        }
        retrofit = new Retrofit.Builder()
                .baseUrl(NetWorkService.BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();
        return retrofit;
    }
}
