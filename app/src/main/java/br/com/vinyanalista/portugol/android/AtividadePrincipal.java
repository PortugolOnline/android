package br.com.vinyanalista.portugol.android;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

public class AtividadePrincipal extends AppCompatActivity implements EditorListener {
    protected Editor editor;
    protected Menu menuToolbarPrincipal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atividade_principal);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        WebView webView = (WebView) findViewById(R.id.editor);

        editor = new Editor(webView);
        editor.adicionarListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuToolbarPrincipal = menu;
        getMenuInflater().inflate(R.menu.toolbar_principal, menu);
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

    @Override
    public void aoAtualizarCodigoFonte(Editor editor) {
        // TODO Apenas teste, remover
        Log.d("teste", editor.getCodigoFonte() + "\n\n");
    }

    @Override
    public void aoAtualizarDesfazerRefazer(Editor editor) {
        menuToolbarPrincipal.findItem(R.id.action_desfazer).setEnabled(editor.isDesfazerPossivel());
        menuToolbarPrincipal.findItem(R.id.action_refazer).setEnabled(editor.isRefazerPossivel());
    }

    public void aumentarFonte() {
        editor.aumentarFonte();
    }

    protected void desfazer() {
        editor.desfazer();
    }

    protected void diminuirFonte() {
        editor.diminuirFonte();
    }

    protected void executar() {
        // TODO Apenas teste, remover
        Log.d("teste", editor.getCodigoFonte() + "\n\n");
    }

    protected void refazer() {
        editor.refazer();
    }
}