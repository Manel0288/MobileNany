package projetannuel.idc.masterinfo.unicaen.mobilenany;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.Area;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.Child;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.ChildAndHisAreas;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.ListAreas;
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

    @BindView(R.id.dt_img_profile)
    ImageView profile;

    @BindView(R.id.r_view_lieux)
    RecyclerView recyclerViewLieux;

    @BindView(R.id.add_lieux_btn)
    MaterialButton addArea;

    View view;
    Child child;
    List<Area> areas;
    Call<ListAreas> callArea;

    Call<ListChildren> call;
    ApiService service;
    TokenManager tokenManager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_detail_enfant, container, false);

        ButterKnife.bind(this, view);

        tokenManager = TokenManager.getInstance(this.getActivity().getSharedPreferences("prefs", getContext().MODE_PRIVATE));
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class,tokenManager);

        recyclerViewLieux.setLayoutManager(new GridLayoutManager(getActivity(),2));
        recyclerViewLieux.setHasFixedSize(true);

        //hide add_area button
        if (tokenManager.getToken().getRole().equals("Enfant")){
            addArea.setVisibility(View.GONE);
        }

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            child = bundle.getParcelable("Child");

            this.nom.setText(this.nom.getText() + child.getNom());
            this.prenom.setText(this.prenom.getText() + child.getPrenom());
            this.email.setText(this.email.getText() + child.getEmail());
            this.adresse.setText(this.adresse.getText() + child.getAdresse());
            this.tel.setText(this.tel.getText() + child.getTel());
            if (child.getImageUrl() != null)
            {
                String url = Utils.getImageCompleteUrl(child.getImageUrl());

                Picasso.with(getContext())
                        .load(url)
                        .into(this.profile);
            }
            else
            {
                Picasso.with(getContext()).load(R.drawable.ic_add_child).
                        into(this.profile);
            }
        }
        this.getLieux();
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
                }

            }


            @Override
            public void onFailure(Call<ListChildren> call, Throwable t) {
                Log.w(TAG, "onFailure " + t.getMessage());
            }
        });

    }

    @OnClick(R.id.go_to_map)
    void goToMap(){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ChildMapFragment childMapFragment = new ChildMapFragment();

        Bundle bundle = new Bundle();
        Area[] areasArray = areas.toArray(new Area[areas.size()]);
        ChildAndHisAreas childAndHisAreas = new ChildAndHisAreas(child, areasArray);
        bundle.putParcelable("ChildAndAreas", childAndHisAreas);
        childMapFragment.setArguments(bundle);
        ft.replace(R.id.layout_container, childMapFragment);
        ft.commit();
    }

    private void getLieux(){

        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage("Chargement des données...");
        dialog.show();

        callArea = service.areas(child.getId());
        callArea.enqueue(new Callback<ListAreas>() {
            @Override
            public void onResponse(Call<ListAreas> callArea, Response<ListAreas> response) {
                dialog.dismiss();
                Log.w(TAG, "onResponse " + response);

                if (response.code() == 401) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().finish();

                    tokenManager.deleteToken();
                }

                if(response.isSuccessful()){
                    Log.w(TAG, "onResponse: " + response.body());

                    areas = response.body().getListLieux();
                    LieuAdapter adapter = new LieuAdapter(getContext(), areas);
                    recyclerViewLieux.setAdapter(adapter);

                }else{
                    Toast.makeText(getActivity(), "Erreur de recuperation des données", Toast.LENGTH_SHORT).show();
                }

            }


            @Override
            public void onFailure(Call<ListAreas> callArea, Throwable t) {
                Toast.makeText(getActivity(), "childId "+child.getId(), Toast.LENGTH_SHORT).show();
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
