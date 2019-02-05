package projetannuel.idc.masterinfo.unicaen.mobilenany.network;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import projetannuel.idc.masterinfo.unicaen.mobilenany.BuildConfig;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class RetrofitBuilder {

    private static final String BASE_URL = "http://192.168.0.17:8005/api/users/";
    private static final OkHttpClient client = buildClient();

    private static final Retrofit retrofit = buildRetrofit(client);

    private  static OkHttpClient buildClient(){

        OkHttpClient.Builder builder = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                Request.Builder builder = request.newBuilder()
                        .addHeader("Accept", "application/json")
                        .addHeader("Connection", "close");

                request = builder.build();

                return chain.proceed(request);
            }
        });

        if (BuildConfig.DEBUG){

            builder.addNetworkInterceptor(new StethoInterceptor());
        }

        return builder.build();
    }

    private static Retrofit buildRetrofit(OkHttpClient client){

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build();
    }

    public static <T> T createService(Class<T> service){
        return retrofit.create(service);
    }

    public static Retrofit getRetrofit() {
        return retrofit;
    }

}
