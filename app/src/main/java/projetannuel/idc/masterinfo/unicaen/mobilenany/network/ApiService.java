package projetannuel.idc.masterinfo.unicaen.mobilenany.network;

import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.AccessToken;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.Area;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.Child;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.ListChildren;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @POST("users/register")
    @FormUrlEncoded
    Call<AccessToken> register(@Field("nom") String nom, @Field("prenom") String prenom, @Field("email") String email,
                               @Field("password") String password, @Field("adresse") String adresse, @Field("tel") String tel);

    @POST("users/login")
    @FormUrlEncoded
    Call<AccessToken> login(@Field("username") String username, @Field("password") String password);

    @POST("users/refresh")
    @FormUrlEncoded
    Call<AccessToken> refresh(@Field("refresh_token") String refreshToken);

    @GET("users/children")
    Call<ListChildren> children();

    @POST("users/add_child")
    @FormUrlEncoded
    Call<Child> addChild(@Field("nom") String nom, @Field("prenom") String prenom, @Field("email") String email,
                         @Field("password") String password, @Field("adresse") String adresse, @Field("tel") String tel);

    @POST("areas/add_area")
    @FormUrlEncoded
    Call<Area> addArea(@Field("label") String label, @Field("adresse") String adresse, @Field("category") String category,
                       @Field("longitude") String longitude, @Field("latitude") String latitude, @Field("child_id") int childId);


}
