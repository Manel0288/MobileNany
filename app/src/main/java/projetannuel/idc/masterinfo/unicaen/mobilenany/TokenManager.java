package projetannuel.idc.masterinfo.unicaen.mobilenany;

import android.content.SharedPreferences;

import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.AccessToken;

public class TokenManager {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private static TokenManager INSTANCE =null;

    private TokenManager(SharedPreferences prefs){
        this.prefs = prefs;
        this.editor = prefs.edit();
    }

    static synchronized TokenManager getInstance(SharedPreferences prefs){
        if(INSTANCE == null){
            INSTANCE = new TokenManager(prefs);
        }
        return INSTANCE;
    }


    public void saveToken (AccessToken token){
        editor.putString("ACCESS_TOKEN", token.getAccessToken()).commit();
        editor.putString("REFRESH_TOKEN", token.getRefreshToken()).commit();
    }

    public void saveUserInfo (AccessToken token){
        editor.putString("NOM", token.getNom()).commit();
        editor.putString("ROLE", token.getRole()).commit();
        editor.putString("IMAGE_URL", token.getImageUrl()).commit();
        editor.putString("EMAIL", token.getEmail()).commit();
    }

    public void deleteToken(){
        editor.remove("ACCESS_TOKEN").commit();
        editor.remove("REFRESH_TOKEN").commit();
        editor.remove("NOM").commit();
        editor.remove("ROLE").commit();
        editor.remove("IMAGE_URL").commit();
        editor.remove("EMAIL").commit();
    }

    public void deleteUserInfo(){
        editor.remove("NOM").commit();
        editor.remove("ROLE").commit();
        editor.remove("IMAGE_URL").commit();
        editor.remove("EMAIL").commit();
    }

    public AccessToken getToken(){
        AccessToken token =new AccessToken();
        token.setAccessToken(prefs.getString("ACCESS_TOKEN", null));
        token.setRefreshToken(prefs.getString("REFRESH_TOKEN", null));
        token.setNom(prefs.getString("NOM", null));
        token.setRole(prefs.getString("ROLE", null));
        token.setEmail(prefs.getString("EMAIL", null));
        token.setImageUrl(prefs.getString("IMAGE_URL", null));
        return token;
    }


}
