package br.com.vinyanalista.portugol.android;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class AtividadeBase extends AppCompatActivity {
    protected void configurarToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
}
