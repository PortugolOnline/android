package br.com.vinyanalista.portugol.android.editor;

public interface EditorListener {
    void aoModificarCodigoFonte(Editor editor);

    void aoMovimentarCursor(Editor editor);

    void aoNaoEncontrar(String localizar);
}
