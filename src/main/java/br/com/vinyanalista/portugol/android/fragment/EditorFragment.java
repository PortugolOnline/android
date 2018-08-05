package br.com.vinyanalista.portugol.android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import br.com.vinyanalista.portugol.android.editor.Editor;
import br.com.vinyanalista.portugol.android.editor.EditorListener;
import br.com.vinyanalista.portugol.android.R;

public class EditorFragment extends BaseFragment {
    private Editor editor;
    private EditorListener listener;

    public static EditorFragment newInstance(EditorListener listener) {
        EditorFragment fragment = new EditorFragment();
        fragment.listener = listener;
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
        editor.adicionarListener(listener);
        return view;
    }

    public Editor getEditor() {
        return editor;
    }
}
