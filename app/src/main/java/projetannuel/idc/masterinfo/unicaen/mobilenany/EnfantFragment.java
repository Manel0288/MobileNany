package projetannuel.idc.masterinfo.unicaen.mobilenany;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.AccessToken;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.ApiError;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.Child;
import projetannuel.idc.masterinfo.unicaen.mobilenany.network.ApiService;
import projetannuel.idc.masterinfo.unicaen.mobilenany.network.RetrofitBuilder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnfantFragment extends Fragment {

    private static final String TAG = "EnfantFragment";
    @BindView(R.id.child_nom)
    TextInputLayout nom;

    @BindView(R.id.child_prenom)
    TextInputLayout prenom;

    @BindView(R.id.child_adresse)
    TextInputLayout adresse;

    @BindView(R.id.child_tel)
    TextInputLayout telephone;

    @BindView(R.id.child_email)
    TextInputLayout email;

    @BindView(R.id.child_password)
    TextInputLayout password;

    Call<Child> call;
    ApiService service;
    TokenManager tokenManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_enfant, container, false);
        ButterKnife.bind(this, view);

        tokenManager = TokenManager.getInstance(this.getActivity().getSharedPreferences("prefs", getContext().MODE_PRIVATE));
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class,tokenManager);

        return view;
    }

    @OnClick(R.id.add_btn)
    void addChild(){
        Toast.makeText(getActivity(), "tester le bouton add child", Toast.LENGTH_LONG).show();

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

        call = service.addChild(nom,prenom,email,password,adresse,tel);
        call.enqueue(new Callback<Child>() {
            @Override
            public void onResponse(Call<Child> call, Response<Child> response) {
                Log.w(TAG, "onResponse " + response);

                if (response.code() == 401) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().finish();

                    tokenManager.deleteToken();
                }

                if(response.isSuccessful()){
                    Log.w(TAG, "onResponse: " + response.body());
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    AddChildPhotoFragment childPhotoFragment = new AddChildPhotoFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("Child", response.body());
                    childPhotoFragment.setArguments(bundle);
                    ft.replace(R.id.layout_container, childPhotoFragment);
                    ft.commit();
                }else{
                    handleErrors(response.errorBody());
                }

            }

            @Override
            public void onFailure(Call<Child> call, Throwable t) {
                Log.w(TAG, "onFailure " + t.getMessage());
            }
        });
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
    public void onDestroy() {
        super.onDestroy();
        if (call != null){
            call.cancel();
            call = null;
        }
    }
}
