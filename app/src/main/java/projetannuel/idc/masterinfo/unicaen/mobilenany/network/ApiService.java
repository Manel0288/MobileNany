package projetannuel.idc.masterinfo.unicaen.mobilenany.network;

import java.lang.reflect.Array;

import okhttp3.MultipartBody;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.AccessToken;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.Area;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.Child;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.ChildLocation;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.ListAreas;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.ListChildren;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.Photo;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {
    @POST("users/register")
    @FormUrlEncoded
    Call<AccessToken> register(@Field("nom") String nom, @Field("prenom") String prenom, @Field("email") String email,
                               @Field("password") String password, @Field("adresse") String adresse, @Field("tel") String tel);

    @Multipart
    @POST("users/add_photo")
    Call<Photo>upload(@Part MultipartBody.Part file);

    //Child photo profile
    @Multipart
    @POST("users/add_child_photo")
    Call<Photo>uploadChildPhoto(@Part MultipartBody.Part file,@Part("id") int id);

    @POST("users/login")
    @FormUrlEncoded
    Call<AccessToken> login(@Field("username") String username, @Field("password") String password);

    @POST("users/refresh")
    @FormUrlEncoded
    Call<AccessToken> refresh(@Field("refresh_token") String refreshToken);

    @GET("users/children")
    Call<ListChildren> children();

    @GET("areas/{child_id}/get_areas")
    Call<ListAreas> areas(@Path("child_id") int childId);

    @POST("users/add_child")
    @FormUrlEncoded
    Call<Child> addChild(@Field("nom") String nom, @Field("prenom") String prenom, @Field("email") String email,
                         @Field("password") String password, @Field("adresse") String adresse, @Field("tel") String tel);

    @POST("users/add_child_location")
    @FormUrlEncoded
    Call<ChildLocation> addChildLocation(@Field("longitude") String longitude, @Field("latitude") String latitude);

    @POST("areas/add_area")
    @FormUrlEncoded
    Call<Area> addArea(@Field("label") String label, @Field("adresse") String adresse, @Field("category") String category,
                       @Field("longitude") String longitude, @Field("latitude") String latitude, @Field("child_id") int childId,
                       @Field("from") String from, @Field("to") String to);

    @GET("users/child_user")
    Call<Child> getChild();

    @GET("users/{child_id}/child_last_location")
    Call<ChildLocation> getChildLastLocation(@Path("child_id") int childId);

    @GET("users/logout")
    Call<Object> logout();
}
