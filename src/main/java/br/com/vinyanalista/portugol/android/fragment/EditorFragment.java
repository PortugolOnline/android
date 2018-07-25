package br.com.vinyanalista.portugol.android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import br.com.vinyanalista.portugol.android.AtividadePrincipal;
import br.com.vinyanalista.portugol.android.Editor;
import br.com.vinyanalista.portugol.android.EditorListener;
import br.com.vinyanalista.portugol.android.R;

public class EditorFragment extends BaseFragment implements EditorListener {
    private AtividadePrincipal atividadePrincipal;
    private Editor editor;

    public EditorFragment() {
    }

    public static EditorFragment newInstance(AtividadePrincipal atividadePrincipal) {
        EditorFragment fragment = new EditorFragment();
        fragment.atividadePrincipal = atividadePrincipal;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editor, container, false);
        WebView webView = (WebView) view;
        editor = new Editor(webView);
        editor.adicionarListener(this);
        return view;
    }

    @Override
    public void aoAtualizarCodigoFonte(Editor editor) {
    }

    @Override
    public void aoAtualizarDesfazerRefazer(Editor editor) {
        atividadePrincipal.habilitarDesfazerRefazer(editor.isDesfazerPossivel(), editor.isRefazerPossivel());
    }

    public void aumentarFonte() {
        editor.aumentarFonte();
    }

    public void desfazer() {
        editor.desfazer();
    }

    public void diminuirFonte() {
        editor.diminuirFonte();
    }

    public String getCodigoFonte() {
        return editor.getCodigoFonte();
    }

    public void limparHistoricoDesfazerRefazer() {
        editor.limparHistoricoDesfazerRefazer();
    }

    public void refazer() {
        editor.refazer();
    }

    public void setCodigoFonte(String codigoFonte) {
        editor.setCodigoFonte(codigoFonte);
    }
}
