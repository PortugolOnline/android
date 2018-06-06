package br.com.vinyanalista.portugol.android;

import android.webkit.WebSettings;
import android.webkit.WebView;

public class Editor {
    protected final String ARQUIVO_HTML = "file:///android_asset/editor.html";
    protected final String CODIFICACAO = "UTF-8";

    protected WebView webView;

    public Editor(WebView webView) {
        assert (webView != null);
        this.webView = webView;

        WebSettings webSettings = webView.getSettings();
        webSettings.setDefaultTextEncodingName(CODIFICACAO);
        webSettings.setDisplayZoomControls(false);
        webSettings.setJavaScriptEnabled(true);

        webView.loadUrl(ARQUIVO_HTML);
    }

    public String getCodigoFonte() {
        return null;
    }

    public void setCodigoFonte(String codigoFonte) {
        assert (codigoFonte != null);
        codigoFonte = codigoFonte.replace("\n", "\\n");
        webView.evaluateJavascript("setCodigoFonte('" + codigoFonte + "')", null);
    }
}