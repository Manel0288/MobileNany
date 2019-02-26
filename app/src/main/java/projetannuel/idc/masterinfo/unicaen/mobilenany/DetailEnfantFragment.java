package projetannuel.idc.masterinfo.unicaen.mobilenany;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.Area;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.Child;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.ListChildren;
import projetannuel.idc.masterinfo.unicaen.mobilenany.network.ApiService;
import projetannuel.idc.masterinfo.unicaen.mobilenany.network.RetrofitBuilder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailEnfantFragment extends Fragment {

    private static final String TAG = "DetailEnfantFragment";

    @BindView(R.id.dt_nom)
    TextView nom;

    @BindView(R.id.dt_prenom)
    TextView prenom;

    @BindView(R.id.dt_email)
    TextView email;

    @BindView(R.id.dt_adresse)
    TextView adresse;

    @BindView(R.id.dt_tel)
    TextView tel;

    Call<ListChildren> call;
    ApiService service;
    TokenManager tokenManager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_enfant, container, false);

        ButterKnife.bind(this, view);

        tokenManager = TokenManager.getInstance(this.getActivity().getSharedPreferences("prefs", getContext().MODE_PRIVATE));
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class,tokenManager);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            Child child = bundle.getParcelable("Child");

            this.nom.setText(this.nom.getText() + child.getNom());
            this.prenom.setText(this.prenom.getText() + child.getPrenom());
            this.email.setText(this.email.getText() + child.getEmail());
            this.adresse.setText(this.adresse.getText() + child.getAdresse());
            this.tel.setText(this.tel.getText() + child.getTel());
        }
        return view;
    }

    @OnClick(R.id.add_lieux_btn)
    void addLieu(){
        call = service.children();
        call.enqueue(new Callback<ListChildren>() {
            @Override
            public void onResponse(Call<ListChildren> call, Response<ListChildren> response) {
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
                    AddLieuFragment addLieuFragment = new AddLieuFragment();

                    List<Child> children = response.body().getListEnfant();
                    Bundle bundle = new Bundle();
                    ArrayList<Child> newChildren = new ArrayList<>(children);
                    bundle.putParcelableArrayList("Children", newChildren);
                    addLieuFragment.setArguments(bundle);
                    ft.replace(R.id.layout_container, addLieuFragment);
                    ft.commit();
                }else{
                    //erreur
                }

            }


            @Override
            public void onFailure(Call<ListChildren> call, Throwable t) {
                Log.w(TAG, "onFailure " + t.getMessage());
            }
        });

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
