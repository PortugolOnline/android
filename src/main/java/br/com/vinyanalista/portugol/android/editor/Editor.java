package br.com.vinyanalista.portugol.android.editor;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewParent;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.List;

import br.com.vinyanalista.portugol.android.R;
import br.com.vinyanalista.portugol.android.util.S;

public class Editor extends WebView {
    protected final static String ARQUIVO_HTML = "file:///android_asset/editor.html";
    protected final static String CODIFICACAO = "UTF-8";

    public interface Listener {
        void aoModificarCodigoFonte(Editor editor);

        void aoMovimentarCursor(Editor editor);

        void aoNaoEncontrar(String localizar);
    }

    private ActionMode actionMode;
    private String codigoFonte = "";
    private ConfiguracoesDaPesquisa configuracoesDaPesquisa = null;
    private int coluna = -1;
    private boolean desfazerPossivel = false;
    private int linha = -1;
    protected final List<Listener> listeners = new ArrayList<Listener>();
    private boolean refazerPossivel = false;

    public Editor(Context context, AttributeSet attrs) {
        super(context, attrs);

        WebSettings webSettings = getSettings();
        webSettings.setDefaultTextEncodingName(CODIFICACAO);
        webSettings.setDisplayZoomControls(false);
        webSettings.setJavaScriptEnabled(true);

        loadUrl(ARQUIVO_HTML);

        addJavascriptInterface(new JavascriptApi(), "javascriptInterface");

        setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                S.l(Editor.this, consoleMessage.sourceId() + ":" + consoleMessage.lineNumber() + " " + consoleMessage.message());
                return true;
            }
        });
    }

    private class JavascriptApi {
        @JavascriptInterface
        public void aoModificarCodigoFonte(String codigoFonte, boolean desfazerPossivel, boolean refazerPossivel) {
            Editor.this.codigoFonte = codigoFonte;
            Editor.this.desfazerPossivel = desfazerPossivel;
            Editor.this.refazerPossivel = refazerPossivel;
            for (Listener listener : listeners) {
                listener.aoModificarCodigoFonte(Editor.this);
            }
        }

        @JavascriptInterface
        public void aoMovimentarCursor(int linha, int coluna) {
            Editor.this.linha = linha;
            Editor.this.coluna = coluna;
            for (Listener listener : listeners) {
                listener.aoMovimentarCursor(Editor.this);
            }
        }

        @JavascriptInterface
        public void aoNaoEncontrar(String localizar) {
            for (Listener listener : listeners) {
                listener.aoNaoEncontrar(localizar);
            }
        }

        @JavascriptInterface
        public void copiar(String textoSelecionado) {
            // https://stackoverflow.com/a/29353049/1657502
            ClipboardManager clipboard = (ClipboardManager) Editor.this.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(getResources().getString(R.string.app_name), textoSelecionado);
            clipboard.setPrimaryClip(clip);
            S.l(Editor.this, "copiar() - texto: " + textoSelecionado);
        }

        @JavascriptInterface
        public void compartilharTextoSelecionado(String textoSelecionado) {
            S.l(Editor.this, "compartilharTextoSelecionado() - textoSelecionado: " + textoSelecionado);
            // https://developer.android.com/training/sharing/send#send-text-content
            Intent intentCompartilhar = new Intent(Intent.ACTION_SEND);
            intentCompartilhar.setType("text/plain");
            intentCompartilhar.putExtra(Intent.EXTRA_SUBJECT, getResources().getText(R.string.compartilhar_titulo));
            intentCompartilhar.putExtra(Intent.EXTRA_TEXT, textoSelecionado);
            getContext().startActivity(Intent.createChooser(intentCompartilhar, getResources().getText(R.string.action_selecao_compartilhar)));
        }
    }

    private ActionMode.Callback actionModeSelecao = new ActionMode.Callback() {
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            S.l(Editor.this, "onActionItemClicked()");
            switch (item.getItemId()) {
                case R.id.action_selecao_colar:
                    colar();
                    mode.finish();
                    return true;
                case R.id.action_selecao_compartilhar:
                    compartilharTextoSelecionado();
                    mode.finish();
                    return true;
                case R.id.action_selecao_copiar:
                    copiar();
                    mode.finish();
                    return true;
                case R.id.action_selecao_recortar:
                    recortar();
                    mode.finish();
                    return true;
                case R.id.action_selecao_selecionar_tudo:
                    selecionarTudo();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            S.l(Editor.this, "onCreateActionMode()");
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.toolbar_selecao, menu);
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            S.l(Editor.this, "onDestroyActionMode()");
            actionMode = null;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            S.l(Editor.this, "onPrepareActionMode()");
            return false;
        }
    };

    // Personalização da Contextual Action Bar (CAB) pela WebView
    // https://issuetracker.google.com/issues/36939404
    // https://bugs.chromium.org/p/chromium/issues/detail?id=476536
    // https://phabricator.wikimedia.org/T33484
    // https://github.com/brion/WebViewTest
    // https://stackoverflow.com/a/22391169/1657502
    // https://stackoverflow.com/a/29353049/1657502

    @Override
    public ActionMode startActionMode(ActionMode.Callback callback) {
        ViewParent parent = getParent();
        if (parent == null) {
            return null;
        }
        return parent.startActionModeForChild(this, actionModeSelecao);
    }

    public void adicionarListener(Listener listener) {
        assert (listener != null);

        listeners.add(listener);
    }

    public void aumentarFonte() {
        evaluateJavascript("aumentarFonte()", null);
    }

    public void colar() {
        S.l(this, "colar()");
        // https://stackoverflow.com/a/43131290/1657502
        ClipboardManager clipboard = (ClipboardManager) Editor.this.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard == null) return;
        ClipData clip = clipboard.getPrimaryClip();
        if (clip == null) return;
        ClipData.Item item = clip.getItemAt(0);
        if (item == null) return;
        CharSequence textToPaste = item.getText();
        if (textToPaste == null) return;
        colar(textToPaste.toString());
    }

    public void colar(String texto) {
        S.l(this, "colar() - texto: " + texto);
        evaluateJavascript("colar(" + texto + ")", null);
    }

    private void compartilharTextoSelecionado() {
        S.l(this, "compartilharTextoSelecionado()");
    }

    public void copiar() {
        S.l(this, "copiar()");
        // https://stackoverflow.com/a/29353049/1657502
        evaluateJavascript("copiar()", null);
    }

    private void configurarPesquisa(String localizar, boolean diferenciarMaiusculas, String substituirPor) {
        evaluateJavascript("configurarPesquisa(\"" + localizar + "\", " + diferenciarMaiusculas + ", " + (substituirPor == null ? "null" : "\"" + substituirPor + "\"") + ")", null);
    }

    public void desfazer() {
        evaluateJavascript("desfazer()", null);
    }

    public void destacarLinhaComErro(int linha, int coluna) {
        S.l(this, "destacarLinhaComErro() - linha: " + linha + ", coluna: " + coluna);
        evaluateJavascript("destacarLinhaComErro(" + linha + ", " + coluna + ")", null);
    }

    public void diminuirFonte() {
        evaluateJavascript("diminuirFonte()", null);
    }

    public String getCodigoFonte() {
        return codigoFonte;
    }

    public int getColuna() {
        return coluna;
    }

    public ConfiguracoesDaPesquisa getConfiguracoesDaPesquisa() {
        return configuracoesDaPesquisa;
    }

    public int getLinha() {
        return linha;
    }

    public boolean isDesfazerPossivel() {
        return desfazerPossivel;
    }

    public boolean isRefazerPossivel() {
        return refazerPossivel;
    }

    public void limparHistoricoDesfazerRefazer() {
        evaluateJavascript("limparHistoricoDesfazerRefazer()", null);
    }

    public void localizarAnterior() {
        evaluateJavascript("localizarAnterior()", null);
    }

    public void localizarProximo() {
        evaluateJavascript("localizarProximo()", null);
    }

    public void posicionarCursor(int linha, int coluna) {
        evaluateJavascript("posicionarCursor(" + linha + ", " + coluna + ")", null);
    }

    public void recortar() {
        S.l(this, "recortar()");
        evaluateJavascript("recortar()", null);
    }

    public void refazer() {
        evaluateJavascript("refazer()", null);
    }

    public void removerListener(Listener listener) {
        assert (listener != null);

        listeners.remove(listener);
    }

    public void selecionarTudo() {
        S.l(this, "selecionarTudo()");
        evaluateJavascript("selecionarTudo()", null);
    }

    public void setCodigoFonte(String codigoFonte) {
        assert (codigoFonte != null);

        this.codigoFonte = codigoFonte;
        codigoFonte = codigoFonte.replace("\n", "\\n");
        evaluateJavascript("setCodigoFonte('" + codigoFonte + "')", null);
    }

    public void setConfiguracoesDaPesquisa(ConfiguracoesDaPesquisa configuracoesDaPesquisa) {
        this.configuracoesDaPesquisa = configuracoesDaPesquisa;
        configurarPesquisa(configuracoesDaPesquisa.getLocalizar(), configuracoesDaPesquisa.isDiferenciarMaiusculas(), configuracoesDaPesquisa.getSubstituirPor());
    }

    public void substituir() {
        evaluateJavascript("substituir()", null);
    }

    public void substituirTudo() {
        evaluateJavascript("substituirTudo()", null);
    }
}
