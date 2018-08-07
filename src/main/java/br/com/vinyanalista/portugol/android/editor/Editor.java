package br.com.vinyanalista.portugol.android.editor;

import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.List;

public class Editor {
    protected final static String ARQUIVO_HTML = "file:///android_asset/editor.html";
    protected final static String CODIFICACAO = "UTF-8";

    private String codigoFonte;
    private ConfiguracoesDaPesquisa configuracoesDaPesquisa;
    private int coluna;
    private boolean desfazerPossivel;
    private int linha;
    protected final List<EditorListener> listeners = new ArrayList<EditorListener>();
    private boolean refazerPossivel;
    protected WebView webView;

    public Editor(WebView webView) {
        assert (webView != null);

        codigoFonte = "";
        configuracoesDaPesquisa = null;
        desfazerPossivel = false;
        refazerPossivel = false;
        this.webView = webView;

        WebSettings webSettings = webView.getSettings();
        webSettings.setDefaultTextEncodingName(CODIFICACAO);
        webSettings.setDisplayZoomControls(false);
        webSettings.setJavaScriptEnabled(true);

        webView.loadUrl(ARQUIVO_HTML);

        webView.addJavascriptInterface(this, "javascriptInterface");
    }

    public void adicionarListener(EditorListener listener) {
        assert (listener != null);

        listeners.add(listener);
    }

    @JavascriptInterface
    public void aoModificarCodigoFonte(String codigoFonte, boolean desfazerPossivel, boolean refazerPossivel) {
        this.codigoFonte = codigoFonte;
        this.desfazerPossivel = desfazerPossivel;
        this.refazerPossivel = refazerPossivel;
        for (EditorListener listener : listeners) {
            listener.aoModificarCodigoFonte(this);
        }
    }

    @JavascriptInterface
    public void aoMovimentarCursor(int linha, int coluna) {
        this.linha = linha;
        this.coluna = coluna;
        for (EditorListener listener : listeners) {
            listener.aoMovimentarCursor(this);
        }
    }

    @JavascriptInterface
    public void aoNaoEncontrar(String localizar) {
        for (EditorListener listener : listeners) {
            listener.aoNaoEncontrar(localizar);
        }
    }

    public void aumentarFonte() {
        webView.evaluateJavascript("aumentarFonte()", null);
    }

    private void configurarPesquisa(String localizar, boolean diferenciarMaiusculas, String substituirPor) {
        webView.evaluateJavascript("configurarPesquisa(\"" + localizar + "\", " + diferenciarMaiusculas + ", " + (substituirPor == null ? "null" : "\"" + substituirPor + "\"") + ")", null);
    }

    public void desfazer() {
        webView.evaluateJavascript("desfazer()", null);
    }

    public void destacarLinhaComErro(int linha, int coluna) {
        webView.evaluateJavascript("destacarLinhaComErro(" + linha + ", " + coluna + ")", null);
    }

    public void diminuirFonte() {
        webView.evaluateJavascript("diminuirFonte()", null);
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
        webView.evaluateJavascript("limparHistoricoDesfazerRefazer()", null);
    }

    public void localizarAnterior() {
        webView.evaluateJavascript("localizarAnterior()", null);
    }

    public void localizarProximo() {
        webView.evaluateJavascript("localizarProximo()", null);
    }

    public void posicionarCursor(int linha, int coluna) {
        webView.evaluateJavascript("posicionarCursor(" + linha + ", " + coluna + ")", null);
    }

    public void refazer() {
        webView.evaluateJavascript("refazer()", null);
    }

    public void removerListener(EditorListener listener) {
        assert (listener != null);

        listeners.remove(listener);
    }

    public void setCodigoFonte(String codigoFonte) {
        assert (codigoFonte != null);

        this.codigoFonte = codigoFonte;
        codigoFonte = codigoFonte.replace("\n", "\\n");
        webView.evaluateJavascript("setCodigoFonte('" + codigoFonte + "')", null);
    }

    public void setConfiguracoesDaPesquisa(ConfiguracoesDaPesquisa configuracoesDaPesquisa) {
        this.configuracoesDaPesquisa = configuracoesDaPesquisa;
        configurarPesquisa(configuracoesDaPesquisa.getLocalizar(), configuracoesDaPesquisa.isDiferenciarMaiusculas(), configuracoesDaPesquisa.getSubstituirPor());
    }

    public void substituir() {
        webView.evaluateJavascript("substituir()", null);
    }

    public void substituirTudo() {
        webView.evaluateJavascript("substituirTudo()", null);
    }
}
