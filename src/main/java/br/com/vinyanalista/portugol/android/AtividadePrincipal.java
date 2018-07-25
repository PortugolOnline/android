package br.com.vinyanalista.portugol.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import br.com.vinyanalista.portugol.android.adapter.TabsAdapter;

public class AtividadePrincipal extends AtividadeBase implements NavigationView.OnNavigationItemSelectedListener {
    static final int REQUEST_ABRIR_EXEMPLO = 1;

    private DrawerLayout drawerLayout;
    private Menu menuToolbarPrincipal;
    private TabsAdapter tabsAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atividade_principal);

        configurarToolbar();

        // Navigation Drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.nav_drawer_abrir, R.string.nav_drawer_fechar);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // ViewPager e TabLayout
        tabsAdapter = new TabsAdapter(getSupportFragmentManager(), this);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(tabsAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ABRIR_EXEMPLO:
                if (resultCode == RESULT_OK) {
                    String exemplo = data.getStringExtra(AtividadeAbrirExemplo.EXTRA_EXEMPLO_SELECIONADO);
                    abrirExemplo(exemplo);
                }
                break;
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        menuToolbarPrincipal = menu;
        getMenuInflater().inflate(R.menu.toolbar_principal, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (drawerLayout != null) {
            switch (item.getItemId()) {
                case R.id.nav_drawer_abrir_arquivo:
                    break;
                case R.id.nav_drawer_abrir_exemplo:
                    abrirExemplo();
                case R.id.nav_drawer_compartilhar:
                    break;
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_aumentar_fonte:
                aumentarFonte();
                return true;
            case R.id.action_desfazer:
                desfazer();
                return true;
            case R.id.action_diminuir_fonte:
                diminuirFonte();
                return true;
            case R.id.action_executar:
                executar();
                return true;
            case R.id.action_refazer:
                refazer();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void abrirExemplo() {
        Intent intent = new Intent(getBaseContext(), AtividadeAbrirExemplo.class);
        startActivityForResult(intent, REQUEST_ABRIR_EXEMPLO);
    }

    private void abrirExemplo(String exemplo) {
        tabsAdapter.getEditorFragment().setCodigoFonte(exemplo);
        tabsAdapter.getEditorFragment().limparHistoricoDesfazerRefazer();
    }

    private void aumentarFonte() {
        tabsAdapter.getEditorFragment().aumentarFonte();
    }

    private void desfazer() {
        tabsAdapter.getEditorFragment().desfazer();
    }

    private void diminuirFonte() {
        tabsAdapter.getEditorFragment().diminuirFonte();
    }

    private void executar() {
        String getCodigoFonte = tabsAdapter.getEditorFragment().getCodigoFonte();
        tabsAdapter.getConsoleFragment().executar(getCodigoFonte);
        viewPager.setCurrentItem(TabsAdapter.TAB_CONSOLE);
    }

    public void habilitarDesfazerRefazer(boolean habilitarDesfazer, boolean habilitarRefazer) {
        menuToolbarPrincipal.findItem(R.id.action_desfazer).setEnabled(habilitarDesfazer);
        menuToolbarPrincipal.findItem(R.id.action_refazer).setEnabled(habilitarRefazer);
    }

    private void refazer() {
        tabsAdapter.getEditorFragment().refazer();
    }
}
