package projetannuel.idc.masterinfo.unicaen.mobilenany;

import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open
                , R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.layout_container,
                    new ParentFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_parent);
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
                break;
        case R.id.nav_profile:
            getSupportFragmentManager().beginTransaction().replace(R.id.layout_container,
                    new ProfilFragment()).commit();
            break;
        case R.id.nav_ch_last_position:
            getSupportFragmentManager().beginTransaction().replace(R.id.layout_container,
                    new LieuxFragment()).commit();
            break;

        case R.id.nav_add_child:
                getSupportFragmentManager().beginTransaction().replace(R.id.layout_container,
                        new EnfantFragment()).commit();
            break;
        case R.id.nav_deconnexion:
            //getSupportFragmentManager().beginTransaction().r
            Toast.makeText(this, "Ecran de deconnexion",Toast.LENGTH_SHORT ).show();
            break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == 0) {
//            ChildMapFragment childMapFragment = new ChildMapFragment();
//            childMapFragment.onRequestPermissionsResult(requestCode,permissions,grantResults);
//        }
//    }

}
