package projetannuel.idc.masterinfo.unicaen.mobilenany;

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
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    @BindView(R.id.nom)
    TextInputLayout nom;

    @BindView(R.id.prenom)
    TextInputLayout prenom;

    @BindView(R.id.adresse)
    TextInputLayout adresse;

    @BindView(R.id.telephone)
    TextInputLayout telephone;

    @BindView(R.id.email)
    TextInputLayout email;

    @BindView(R.id.password)
    TextInputLayout password;

    Call<AccessToken> call;
    ApiService service;
    AwesomeValidation validator;
    TokenManager tokenManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);
        service = RetrofitBuilder.createService(ApiService.class);
        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        setupRules();
        if(tokenManager.getToken().getAccessToken() != null){
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
        }
    }

    @OnClick(R.id.register_btn)
    void register(){

        String nom = this.nom.getEditText().getText().toString();
        String prenom =this.prenom.getEditText().getText().toString();
        String adresse =this.adresse.getEditText().getText().toString();
        String tel =this.telephone.getEditText().getText().toString();
        String email =this.email.getEditText().getText().toString();
        String password =this.password.getEditText().getText().toString();

        this.nom.setError(null);
        this.prenom.setError(null);
        this.email.setError(null);
        this.adresse.setError(null);
        this.password.setError(null);
        this.telephone.setError(null);

        validator.clear();

        if (validator.validate()){
            call = service.register(nom, prenom, email, password, adresse, tel);
            call.enqueue(new Callback<AccessToken>() {
                @Override
                public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {

                    Log.w(TAG, "onResponse " + response);
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    finish();

                    if(response.isSuccessful()){
                        Log.w(TAG, "onResponse: " + response.body());
                       tokenManager.saveToken(response.body());
                    }else{
                        handleErrors(response.errorBody());
                    }

                }

                @Override
                public void onFailure(Call<AccessToken> call, Throwable t) {

                    Log.w(TAG, "onFailure " + t.getMessage());

                }
            });
        }

    }

    public void setupRules(){

        validator.addValidation(this, R.id.nom, RegexTemplate.NOT_EMPTY, R.string.error_nom);
        validator.addValidation(this, R.id.prenom, RegexTemplate.NOT_EMPTY, R.string.error_prenom);
        validator.addValidation(this, R.id.email, Patterns.EMAIL_ADDRESS, R.string.error_email);
        validator.addValidation(this, R.id.adresse, RegexTemplate.NOT_EMPTY, R.string.error_adresse);
        validator.addValidation(this, R.id.password, "[a-zA-Z0-9]{6,}", R.string.error_password);
        validator.addValidation(this, R.id.telephone, RegexTemplate.NOT_EMPTY, R.string.error_telephone);
    }

    private void handleErrors(ResponseBody responseBody) {
        ApiError apiError = Utils.convertErrors(responseBody);

        for (Map.Entry<String, List<String>> error : apiError.getErrors().entrySet()){
            if(error.getKey().equals("nom")){
                this.nom.setError(error.getValue().get(0));
            }

            if(error.getKey().equals("prenom")){
                this.prenom.setError(error.getValue().get(0));
            }

            if(error.getKey().equals("email")){
                this.email.setError(error.getValue().get(0));
            }

            if(error.getKey().equals("adresse")){
                this.adresse.setError(error.getValue().get(0));
            }

            if(error.getKey().equals("password")){
                this.password.setError(error.getValue().get(0));
            }

            if(error.getKey().equals("tel")){
                this.telephone.setError(error.getValue().get(0));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (call != null){
            call.cancel();
            call = null;
        }
    }
}
