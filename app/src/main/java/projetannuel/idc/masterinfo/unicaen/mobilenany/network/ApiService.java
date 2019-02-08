package projetannuel.idc.masterinfo.unicaen.mobilenany.network;

import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.AccessToken;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.Child;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {
    @POST("register")
    @FormUrlEncoded
    Call<AccessToken> register(@Field("nom") String nom, @Field("prenom") String prenom, @Field("email") String email,
                               @Field("password") String password, @Field("adresse") String adresse, @Field("tel") String tel);

    @POST("login")
    @FormUrlEncoded
    Call<AccessToken> login(@Field("username") String username, @Field("password") String password);

    @POST("refresh")
    @FormUrlEncoded
    Call<AccessToken> refresh(@Field("refresh_token") String refreshToken);

    @POST("add_child")
    @FormUrlEncoded
    Call<Child> addChild(@Field("nom") String nom, @Field("prenom") String prenom, @Field("email") String email,
                         @Field("password") String password, @Field("adresse") String adresse, @Field("tel") String tel);


}
