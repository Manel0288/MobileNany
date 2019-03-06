package projetannuel.idc.masterinfo.unicaen.mobilenany;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.ArrayList;
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
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.ApiError;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.Area;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.Child;
import projetannuel.idc.masterinfo.unicaen.mobilenany.network.ApiService;
import projetannuel.idc.masterinfo.unicaen.mobilenany.network.RetrofitBuilder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddLieuFragment extends Fragment {

    private static final String TAG = "AddLieuFragment";
    @BindView(R.id.label)
    TextInputLayout label;

    @BindView(R.id.adresse)
    TextInputLayout adresse;

    @BindView(R.id.category)
    RadioGroup category;

    String selectedCategory;
    String latitude;
    String longitude;
    int childId;
    ArrayList<Child> children;
    RadioButton radioButton;

    @BindView(R.id.liste_enfants)
    Spinner liste_enfants;

    Call<Area> call;
    ApiService service;
    TokenManager tokenManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_lieu, container, false);
        ButterKnife.bind(this, view);

        tokenManager = TokenManager.getInstance(this.getActivity().getSharedPreferences("prefs", getContext().MODE_PRIVATE));
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class,tokenManager);

        radioButton = (RadioButton)view.findViewById(category.getCheckedRadioButtonId());

        this.addItemsOnListEnfants();
        return view;
    }

    @OnClick({R.id.radio_cat1, R.id.radio_cat2})
    void onRadioChanged(RadioButton radioButton){
        boolean checked = radioButton.isChecked();

        if (checked){
            selectedCategory = radioButton.getText().toString();
        }
    }


    private void getLocation(){
        String adr = this.adresse.getEditText().getText().toString();
        Geocoder coder = new Geocoder(getActivity());
        List<Address> address;

        try {
            // May throw an IOException
            Toast.makeText(this.getActivity(), "Exzcution de la fonction de geocoder", Toast.LENGTH_LONG).show();
            address = coder.getFromLocationName(adr, 1);
            if (address == null) {
                Log.w(TAG, "----------------geocoder not working----------------------: " + adr);
                Toast.makeText(this.getActivity(), "Votre adresse n'est pas bonne. Reésseyez!", Toast.LENGTH_LONG).show();
            }

            Address location = address.get(0);

            double lat = location.getLatitude();
            double lon = location.getLongitude();

            this.latitude = String.valueOf(lat);
            this.longitude = String.valueOf(lon);

            Toast.makeText(this.getActivity(), "longitude : " +this.longitude+" et latitude : "+ this.latitude, Toast.LENGTH_LONG).show();

        } catch (IOException ex) {
            Toast.makeText(this.getActivity(), "Erreur Goecoding ne marche pas", Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }

    }

    @OnClick(R.id.add_lieu_btn)
    void addChild(){

        String label = this.label.getEditText().getText().toString();
        String adresse =this.adresse.getEditText().getText().toString();
        String category = this.selectedCategory;
        selectedCategory = this.radioButton.getText().toString();

        //rcuperation des coordonnées ici
        this.getLocation();

        this.label.setError(null);
        this.adresse.setError(null);

        String selectedChild = liste_enfants.getSelectedItem().toString();

        for (Child child: this.children) {
            if (child.getPrenom() == selectedChild){
                childId = child.getId();
            }
        }

        Log.w(TAG, "selectedChild: "+ selectedChild);
        call = service.addArea(label,adresse,category,this.longitude,this.latitude, childId);
        call.enqueue(new Callback<Area>() {
            @Override
            public void onResponse(Call<Area> call, Response<Area> response) {
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
                    ParentFragment parentFragment = new ParentFragment();
                    ft.replace(R.id.layout_container, parentFragment);
                    ft.commit();
                }else{
                    handleErrors(response.errorBody());
                }

            }

            @Override
            public void onFailure(Call<Area> call, Throwable t) {
                Log.w(TAG, "onFailure " + t.getMessage());
            }
        });
    }


    private void handleErrors(ResponseBody responseBody) {
        ApiError apiError = Utils.convertErrors(responseBody);

        for (Map.Entry<String, List<String>> error : apiError.getErrors().entrySet()){
            if(error.getKey().equals("label")){
                this.label.setError(error.getValue().get(0));
            }

            if(error.getKey().equals("adresse")){
                this.adresse.setError(error.getValue().get(0));
            }

        }
    }

    public void addItemsOnListEnfants() {
        List<String> list = new ArrayList<String>();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
             this.children = bundle.getParcelableArrayList("Children");
            Log.w(TAG, "onChildren " + children);

            for (Child child : children) {
                String prenom = child.getPrenom();
                list.add(prenom);

            }
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        liste_enfants.setAdapter(dataAdapter);
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
