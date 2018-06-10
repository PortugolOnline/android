package br.com.vinyanalista.portugol.android;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class AtividadeBase extends AppCompatActivity {
    private boolean botaoDeVoltar = false;

    protected void configurarBotaoDeVoltar() {
        botaoDeVoltar = true;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    protected void configurarToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
