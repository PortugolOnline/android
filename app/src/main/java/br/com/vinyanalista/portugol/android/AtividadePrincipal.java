package br.com.vinyanalista.portugol.android;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

public class AtividadePrincipal extends AppCompatActivity {
    protected Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atividade_principal);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        WebView webView = (WebView) findViewById(R.id.editor);

        editor = new Editor(webView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_executar:
                executar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void executar() {
        editor.setCodigoFonte("algoritmo\nescreva \"Olá, Android!\"\nfim_algoritmo.");
    }
}