package projetannuel.idc.masterinfo.unicaen.mobilenany.network;

import com.facebook.stetho.BuildConfig;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import projetannuel.idc.masterinfo.unicaen.mobilenany.TokenManager;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.CustomeAuthenticator;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class RetrofitBuilder {

    private static final String BASE_URL = "http://10.188.109.155:8005/api/";//10.188.109.155 192.168.0.17  10.224.71.150 192.168.43.138
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

    public static <T> T createServiceWithAuth(Class<T> service, TokenManager tokenManager){
        OkHttpClient newClient = client.newBuilder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                Request.Builder builder = request.newBuilder();

                if(tokenManager.getToken().getAccessToken() != null){
                    builder.addHeader("Authorization", "Bearer " + tokenManager.getToken().getAccessToken());
                }

                request = builder.build();
                return chain.proceed(request);
            }
        }).authenticator(CustomeAuthenticator.getInstance(tokenManager)).build();

        Retrofit newRetrofit = retrofit.newBuilder().client(newClient).build();
        return newRetrofit.create(service);

    }

    public static Retrofit getRetrofit() {
        return retrofit;
    }

}
