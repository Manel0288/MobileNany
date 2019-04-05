package projetannuel.idc.masterinfo.unicaen.mobilenany;

import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.AccessToken;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.Child;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.ListChildren;
import projetannuel.idc.masterinfo.unicaen.mobilenany.network.ApiService;
import projetannuel.idc.masterinfo.unicaen.mobilenany.network.RetrofitBuilder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private  NavigationView navigationView;
    private static final String TAG = "MainActivity";
    TokenManager tokenManager;
    AccessToken accessToken;

    Call<Object> call;
    ApiService service;

    Child child;
    Call<Child> callChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        accessToken = tokenManager.getToken();
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class,tokenManager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView)findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        hideItems();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open
                , R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        View headerView = navigationView.getHeaderView(0);
        TextView userNameView = (TextView)headerView.findViewById(R.id.user_name);
        TextView userEmailView = (TextView)headerView.findViewById(R.id.user_email);
        ImageView profile = (ImageView) headerView.findViewById(R.id.user_profile);
        userNameView.setText(accessToken.getNom());
        userEmailView.setText(accessToken.getEmail());

        String url = Utils.getImageCompleteUrl(accessToken.getImageUrl());
        Log.w(TAG, "----------------------- "+ url +" -------------------------------------------------------------------");
        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.BLACK)
                .borderWidthDp(3)
                .cornerRadiusDp(30)
                .oval(false)
                .build();

        Picasso.with(this)
                .load(url)
                .transform(transformation)
                .into(profile);

        if (savedInstanceState == null){
            if (accessToken.getRole().equals("Parent")) {
                getSupportFragmentManager().beginTransaction().replace(R.id.layout_container,
                        new ParentFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_parent);
            }else {
                // recuperer les coordonnées de l'enfant ici
                getChild();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_parent:
                getSupportFragmentManager().beginTransaction().replace(R.id.layout_container,
                        new ParentFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_parent);
                break;
        case R.id.nav_profile:
            getSupportFragmentManager().beginTransaction().replace(R.id.layout_container,
                    new ProfilFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_profile);
            break;
        case R.id.nav_child_info:
            goToMesInfo();
            navigationView.setCheckedItem(R.id.nav_profile);
            break;

        case R.id.nav_add_child:
                getSupportFragmentManager().beginTransaction().replace(R.id.layout_container,
                        new EnfantFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_add_child);
            break;
        case R.id.nav_deconnexion:
            logout();
            Toast.makeText(this, "Ecran de deconnexion",Toast.LENGTH_SHORT ).show();
            break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void hideItems()
    {
        Menu nav_Menu = navigationView.getMenu();
        if (accessToken.getRole().equals("Parent")) {
            nav_Menu.findItem(R.id.nav_child_info).setVisible(false);
        }else{
            nav_Menu.findItem(R.id.nav_parent).setVisible(false);
            nav_Menu.findItem(R.id.nav_profile).setVisible(false);
            nav_Menu.findItem(R.id.nav_add_child).setVisible(false);
        }
    }

    private void getChild(){

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Chargement des données...");
        dialog.show();

        callChild = service.getChild();
        callChild.enqueue(new Callback<Child>() {
            @Override
            public void onResponse(Call<Child> call, Response<Child> response) {
                dialog.dismiss();
                Log.w(TAG, "onResponse " + response);

                if (response.code() == 401) {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    MainActivity.this.finish();

                    tokenManager.deleteToken();
                }

                if(response.isSuccessful()){
                    Log.w(TAG, "onResponse: " + response.body());
                    child = response.body();
                    goToMesInfo();

                }else{
                    Toast.makeText(getApplicationContext(), "Erreur de recuperation des données", Toast.LENGTH_SHORT).show();
                }

            }


            @Override
            public void onFailure(Call<Child> call, Throwable t) {
                Log.w(TAG, "onFailure " + t.getMessage());
            }
        });
    }

    private void logout(){
        call = service.logout();
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Log.w(TAG, "onResponse " + response);

                if(response.isSuccessful()){
                    Log.w(TAG, "onResponse: " + response.body());

                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    MainActivity.this.finish();
                    tokenManager.deleteToken();

                }else{
                    Toast.makeText(getApplicationContext(), "Erreur de recuperation des données", Toast.LENGTH_SHORT).show();
                }

            }


            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.w(TAG, "onFailure " + t.getMessage());
            }
        });
    }

    private void goToMesInfo(){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        DetailEnfantFragment detailEnfantFragment = new DetailEnfantFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("Child", child);
        detailEnfantFragment.setArguments(bundle);
        ft.replace(R.id.layout_container, detailEnfantFragment);
        ft.commit();
    }

}
