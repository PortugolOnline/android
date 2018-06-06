package br.com.vinyanalista.portugol.android;

import android.util.Log;
import android.webkit.*;

import java.util.ArrayList;
import java.util.List;

public class Editor {
    protected final static String ARQUIVO_HTML = "file:///android_asset/editor.html";
    protected final static String CODIFICACAO = "UTF-8";

    private String codigoFonte;
    private boolean desfazerPossivel;
    protected final List<EditorListener> listeners = new ArrayList<EditorListener>();
    private boolean refazerPossivel;
    protected WebView webView;


    public Editor(WebView webView) {
        assert (webView != null);

        codigoFonte = "";
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
        listeners.add(listener);
    }

    @JavascriptInterface
    public void atualizarCodigoFonte(String codigoFonte) {
        this.codigoFonte = codigoFonte;
        for (EditorListener listener : listeners) {
            listener.aoAtualizarCodigoFonte(this);
        }
    }

    @JavascriptInterface
    public void atualizarDesfazerRefazer(boolean desfazerPossivel, boolean refazerPossivel) {
        this.desfazerPossivel = desfazerPossivel;
        this.refazerPossivel = refazerPossivel;
        for (EditorListener listener : listeners) {
            listener.aoAtualizarDesfazerRefazer(this);
        }
    }

    public void aumentarFonte() {
        webView.evaluateJavascript("aumentarFonte()", null);
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

    public boolean isDesfazerPossivel() {
        return desfazerPossivel;
    }

    public boolean isRefazerPossivel() {
        return refazerPossivel;
    }

    public void limparHistoricoDesfazerRefazer() {
        webView.evaluateJavascript("limparHistoricoDesfazerRefazer()", null);
    }

    public void posicionarCursor(int linha, int coluna) {
        webView.evaluateJavascript("posicionarCursor(" + linha + ", " + coluna + ")", null);
    }

    public void refazer() {
        webView.evaluateJavascript("refazer()", null);
    }

    public void removerListener(EditorListener listener) {
        listeners.remove(listener);
    }

    public void setCodigoFonte(String codigoFonte) {
        assert (codigoFonte != null);
        this.codigoFonte = codigoFonte;
        codigoFonte = codigoFonte.replace("\n", "\\n");
        webView.evaluateJavascript("setCodigoFonte('" + codigoFonte + "')", null);
        for (EditorListener listener : listeners) {
            listener.aoAtualizarCodigoFonte(this);
        }
    }
}