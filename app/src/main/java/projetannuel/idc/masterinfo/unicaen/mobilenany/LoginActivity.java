package projetannuel.idc.masterinfo.unicaen.mobilenany;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.AccessToken;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.ApiError;
import projetannuel.idc.masterinfo.unicaen.mobilenany.network.ApiService;
import projetannuel.idc.masterinfo.unicaen.mobilenany.network.RetrofitBuilder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
      @BindView(R.id.login_email)
      TextInputLayout email;

      @BindView(R.id.login_password)
      TextInputLayout password;

      ApiService service;
      TokenManager tokenManager;
      AccessToken accessToken;
      AwesomeValidation validator;
      Call<AccessToken> call;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        service = RetrofitBuilder.createService(ApiService.class);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
        setupRules();
//        if(tokenManager.getToken().getAccessToken() != null){
//            startActivity(new Intent(LoginActivity.this, MainActivity.class));
//            finish();
//        }

    }

    @OnClick(R.id.inscription)
    void inscription(){
        Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.connexion)
    void login(){

        String username = this.email.getEditText().getText().toString();
        String password = this.password.getEditText().getText().toString();

        this.email.setError(null);
        this.password.setError(null);

        validator.clear();

        if(validator.validate()) {

            call = service.login(username, password);
            call.enqueue(new Callback<AccessToken>() {
                @Override
                public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                    Log.w(TAG, "onResponse: " + response);
                    if (response.isSuccessful()) {

                        tokenManager.saveToken(response.body());
                        tokenManager.saveUserInfo(response.body());
                        //accessToken = tokenManager.getToken();
                        //if (accessToken.getRole().equals("Parent")){
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();

                    } else {
                        if (response.code() == 422) {
                            handleErrors(response.errorBody());
                        }
                        if (response.code() == 401) {
                            ApiError apiError = Utils.convertErrors(response.errorBody());
                            Toast.makeText(LoginActivity.this, apiError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<AccessToken> call, Throwable t) {
                    Log.w(TAG, "onFailure " + t.getMessage());
                }
            });
        }
    }

    private void handleErrors(ResponseBody responseBody) {
        ApiError apiError = Utils.convertErrors(responseBody);

        for (Map.Entry<String, List<String>> error : apiError.getErrors().entrySet()){

            if(error.getKey().equals("username")){
                this.email.setError(error.getValue().get(0));
            }

            if(error.getKey().equals("password")){
                this.password.setError(error.getValue().get(0));
            }
        }
    }

    public void setupRules(){

        validator.addValidation(this, R.id.login_email, Patterns.EMAIL_ADDRESS, R.string.error_email);
        validator.addValidation(this, R.id.login_password, RegexTemplate.NOT_EMPTY, R.string.error_password);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(call != null){
            call.cancel();
            call = null;
        }
    }
}
