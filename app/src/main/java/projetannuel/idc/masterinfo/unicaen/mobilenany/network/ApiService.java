package projetannuel.idc.masterinfo.unicaen.mobilenany.network;

import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.AccessToken;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {
    @POST("register")
    @FormUrlEncoded
    Call<AccessToken> register(@Field("nom") String nom, @Field("prenom") String prenom, @Field("email") String email,
                               @Field("password") String password, @Field("adressse") String adresse, @Field("tel") String tel);


}
