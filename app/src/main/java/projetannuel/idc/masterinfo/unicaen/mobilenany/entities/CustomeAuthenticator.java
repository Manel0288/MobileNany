package projetannuel.idc.masterinfo.unicaen.mobilenany.entities;

import java.io.IOException;

import javax.annotation.Nullable;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import projetannuel.idc.masterinfo.unicaen.mobilenany.TokenManager;
import projetannuel.idc.masterinfo.unicaen.mobilenany.network.ApiService;
import projetannuel.idc.masterinfo.unicaen.mobilenany.network.RetrofitBuilder;
import retrofit2.Call;

public class CustomeAuthenticator implements Authenticator {

    private TokenManager tokenManager;
    private static CustomeAuthenticator INSTANCE;

    private CustomeAuthenticator(TokenManager tokenManager){
        this.tokenManager = tokenManager;
    }

    public static synchronized CustomeAuthenticator getInstance(TokenManager tokenManager){
        if(INSTANCE == null){
            INSTANCE = new CustomeAuthenticator(tokenManager);
        }
        return INSTANCE;
    }
    @Nullable
    @Override
    public Request authenticate(Route route, Response response) throws IOException {

        if(responseCount(response) >= 2){
            return null;
        }
        AccessToken token = tokenManager.getToken();

        ApiService service = RetrofitBuilder.createService(ApiService.class);
        Call<AccessToken> call =service.refresh(token.getRefreshToken());
        retrofit2.Response<AccessToken> res = call.execute();

        if (res.isSuccessful()){
            AccessToken newToken = res.body();
            tokenManager.saveToken(newToken);

            return response.request().newBuilder().header("Authorization", "Bearer " + res.body().getAccessToken()).build();
        }else {

            return null;
        }

    }

    private int responseCount(Response response){
        int result = 1;
        while ((response = response.priorResponse()) != null){

            result++;
        }

        return result;
    }
}
