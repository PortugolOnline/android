package br.com.vinyanalista.portugol.android;

import android.webkit.*;

import java.util.ArrayList;
import java.util.List;

public class Editor {
    protected final static String ARQUIVO_HTML = "file:///android_asset/editor.html";
    protected final static String CODIFICACAO = "UTF-8";

    private boolean desfazerPossivel;
    protected final List<EditorListener> listeners = new ArrayList<EditorListener>();
    private boolean refazerPossivel;
    protected WebView webView;


    public Editor(WebView webView) {
        assert (webView != null);
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
    public void atualizarDesfazerRefazer(boolean desfazerPossivel, boolean refazerPossivel) {
        this.desfazerPossivel = desfazerPossivel;
        this.refazerPossivel = refazerPossivel;
        for (EditorListener listener : listeners) {
            listener.aoAtualizarDesfazerRefazer(this);
        }
    }

    public void desfazer() {
        webView.evaluateJavascript("desfazer()", null);
    }

    public String getCodigoFonte() {
        return null;
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

    public void refazer() {
        webView.evaluateJavascript("refazer()", null);
    }

    public void removerListener(EditorListener listener) {
        listeners.remove(listener);
    }

    public void setCodigoFonte(String codigoFonte) {
        assert (codigoFonte != null);
        codigoFonte = codigoFonte.replace("\n", "\\n");
        webView.evaluateJavascript("setCodigoFonte('" + codigoFonte + "')", null);
    }
}