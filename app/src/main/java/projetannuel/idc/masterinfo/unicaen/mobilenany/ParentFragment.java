package projetannuel.idc.masterinfo.unicaen.mobilenany;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.Child;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.ListChildren;
import projetannuel.idc.masterinfo.unicaen.mobilenany.network.ApiService;
import projetannuel.idc.masterinfo.unicaen.mobilenany.network.RetrofitBuilder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ParentFragment extends Fragment {
    private static final String TAG = "ParentFragment";
    View view;

    RecyclerView recyclerView;

    List<Child> children;
    Call<ListChildren> call;
    ApiService service;
    TokenManager tokenManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_parent, container, false);

        //ButterKnife.bind(this, view);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        children = new ArrayList<>();

        tokenManager = TokenManager.getInstance(this.getActivity().getSharedPreferences("prefs", getContext().MODE_PRIVATE));
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class,tokenManager);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        this.getChildren();

        return view;
    }



    private void getChildren(){

        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage("Chargement des données...");
        dialog.show();

        call = service.children();
        call.enqueue(new Callback<ListChildren>() {
            @Override
            public void onResponse(Call<ListChildren> call, Response<ListChildren> response) {
                dialog.dismiss();
                Log.w(TAG, "onResponse " + response);

                if (response.code() == 401) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().finish();

                    tokenManager.deleteToken();
                }

                if(response.isSuccessful()){
                    Log.w(TAG, "onResponse: " + response.body());

                    children = response.body().getListEnfant();
                    RecyclerViewAdapter adapter = new RecyclerViewAdapter(getContext(), children);
                    recyclerView.setAdapter(adapter);

                }else{
                    Toast.makeText(getActivity(), "Erreur de recuperation des données", Toast.LENGTH_SHORT).show();
                }

            }


            @Override
            public void onFailure(Call<ListChildren> call, Throwable t) {
                Log.w(TAG, "onFailure " + t.getMessage());
            }
        });
    }
}
