package projetannuel.idc.masterinfo.unicaen.mobilenany;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
        case R.id.nav_deconnexion:
            //getSupportFragmentManager().beginTransaction().r
            Toast.makeText(this, "Ecran de deconnexion",Toast.LENGTH_SHORT ).show();
            break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
