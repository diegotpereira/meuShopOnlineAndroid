package br.java.meushoponlineandroid.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import br.java.meushoponlineandroid.R;
import br.java.meushoponlineandroid.fragment.ComentarioFragmento;
import br.java.meushoponlineandroid.fragment.HomeFragmento;
import br.java.meushoponlineandroid.fragment.PerfilFragmento;
import br.java.meushoponlineandroid.fragment.SobreFragmento;
import br.java.meushoponlineandroid.fragment.VenderFragmento;

public class DrawerActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        getSupportFragmentManager().beginTransaction().replace(R.id.frag_container,
                new HomeFragmento()).commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.nav_principal:
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.frag_container, new HomeFragmento()).commit();
                break;

            case R.id.nav_meu_perfil:
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.frag_container , new PerfilFragmento()).commit();
                break;

            case R.id.nav_vender:
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.frag_container , new VenderFragmento()).commit();
                break;

            case R.id.nav_sair:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(this, EntrarActivity.class));
                break;

            case R.id.nav_sobre:
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.frag_container , new SobreFragmento()).commit();
                break;

            case R.id.nav_comentario:
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.frag_container , new ComentarioFragmento()).commit();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}