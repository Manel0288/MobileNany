package projetannuel.idc.masterinfo.unicaen.mobilenany.entities;

import com.squareup.moshi.Json;

public class AccessToken {

    @Json(name = "token_type")
    String tokenType;
    @Json(name = "expires_in")
    int expiresIn;
    @Json(name = "access_token")
    String accessToken;
    @Json(name ="refresh_token")
    String refreshToken;

    @Json(name ="nom")
    String nom;

    @Json(name ="role")
    String role;

    @Json(name ="email")
    String email;

    @Json(name ="image_url")
    String imageUrl;


    public String getTokenType() {
        return tokenType;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getNom() {
        return nom;
    }

    public String getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setImageUrl(String url) {
        this.imageUrl = url;
    }
}
