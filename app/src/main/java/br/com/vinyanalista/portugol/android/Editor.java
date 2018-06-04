package br.com.vinyanalista.portugol.android;

import android.webkit.WebSettings;
import android.webkit.WebView;

public class Editor {
    protected final String ARQUIVO_HTML = "file:///android_asset/editor.html";
    protected final String CODIFICACAO = "UTF-8";

    protected WebView view;

    public Editor(WebView view) {
        this.view = view;

        WebSettings webSettings = view.getSettings();
        webSettings.setDefaultTextEncodingName(CODIFICACAO);
        webSettings.setDisplayZoomControls(false);
        webSettings.setJavaScriptEnabled(true);

        view.loadUrl(ARQUIVO_HTML);
    }
}