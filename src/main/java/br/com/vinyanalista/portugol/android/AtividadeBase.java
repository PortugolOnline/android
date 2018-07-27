package br.com.vinyanalista.portugol.android;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import br.com.vinyanalista.portugol.android.util.S;

public class AtividadeBase extends AppCompatActivity {
    private boolean botaoDeVoltar = false;
    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        S.l(this, "onCreate()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        S.l(this, "onDestroy()");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (botaoDeVoltar && (item.getItemId() == android.R.id.home)) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        S.l(this, "onPause()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        S.l(this, "onRestart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        S.l(this, "onResume()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        S.l(this, "onStart()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        S.l(this, "onStop()");
    }

    protected void configurarBotaoDeVoltar() {
        botaoDeVoltar = true;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    protected void configurarToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
}
