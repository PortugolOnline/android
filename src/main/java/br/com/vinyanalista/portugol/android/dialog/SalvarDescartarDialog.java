package br.com.vinyanalista.portugol.android.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;

import br.com.vinyanalista.portugol.android.R;

public class SalvarDescartarDialog extends DialogFragment {
    public static final String ARGUMENTO_NOME_DO_ARQUIVO = "nomeDoArquivo";
    public static final String ARGUMENTO_REQUEST_CODE = "requestCode";

    public interface Listener {
        void descartarAlteracoes(int requestCode);

        void salvarAlteracoes(int requestCode);
    }

    private Listener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (Listener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " deve implementar SalvarDescartarDialog.Listener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String nomeDoArquivo = getArguments().getString(ARGUMENTO_NOME_DO_ARQUIVO);
        // https://stackoverflow.com/a/20887690/1657502
        // https://stackoverflow.com/a/13425284/1657502
        // http://developer.android.com/guide/topics/resources/string-resource.html#FormattingAndStyling
        String mensagem = getResources().getString(R.string.salvar_descartar_dialogo_mensagem, nomeDoArquivo);
        CharSequence mensagemComFormatacao = Html.fromHtml(mensagem);
        final int requestCode = getArguments().getInt(ARGUMENTO_REQUEST_CODE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.salvar_descartar_dialogo_titulo)
                .setMessage(mensagemComFormatacao)
                .setNegativeButton(R.string.salvar_descartar_dialogo_opcao_descartar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.descartarAlteracoes(requestCode);
                    }
                })
                .setNeutralButton(R.string.salvar_descartar_dialogo_opcao_cancelar, null)
                .setPositiveButton(R.string.salvar_descartar_dialogo_opcao_salvar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.salvarAlteracoes(requestCode);
                    }
                });
        AlertDialog dialog = builder.create();
        return dialog;
    }
}
