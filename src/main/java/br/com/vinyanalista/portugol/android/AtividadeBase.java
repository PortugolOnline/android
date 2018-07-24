package br.com.vinyanalista.portugol.android;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class AtividadeBase extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private boolean botaoDeVoltar = false;
    protected DrawerLayout drawerLayout;
    private Toolbar toolbar;

    protected void configurarBotaoDeVoltar() {
        botaoDeVoltar = true;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    protected void configurarNavDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.nav_drawer_abrir, R.string.nav_drawer_fechar);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    protected void configurarToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onBackPressed() {
        if ((drawerLayout != null) && (drawerLayout.isDrawerOpen(GravityCompat.START))) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (drawerLayout != null) {
            switch (item.getItemId()) {
                case R.id.nav_drawer_abrir_arquivo:
                case R.id.nav_drawer_abrir_exemplo:
                case R.id.nav_drawer_compartilhar:
                default:
                    drawerLayout.closeDrawer(GravityCompat.START);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (botaoDeVoltar && (item.getItemId() == android.R.id.home)) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
