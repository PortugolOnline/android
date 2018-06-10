package br.com.vinyanalista.portugol.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import br.com.vinyanalista.portugol.interpretador.Exemplo;

public class AtividadePrincipal extends AtividadeBase implements EditorListener {
    protected Editor editor;
    protected Menu menuToolbarPrincipal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atividade_principal);

        configurarToolbar();

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
        editor.setCodigoFonte(Exemplo.ESTRUTURA_SEQUENCIAL.getProgramaFonte());

        Intent intent = new Intent(getBaseContext(), AtividadeConsole.class);
        Bundle argumentos = new Bundle();
        argumentos.putString(AtividadeConsole.CODIGO_FONTE, editor.getCodigoFonte());
        intent.putExtras(argumentos);
        startActivity(intent);
    }

    protected void refazer() {
        editor.refazer();
    }
}