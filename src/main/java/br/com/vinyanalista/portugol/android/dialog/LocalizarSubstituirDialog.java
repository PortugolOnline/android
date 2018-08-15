package br.com.vinyanalista.portugol.android.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;

import br.com.vinyanalista.portugol.android.R;
import br.com.vinyanalista.portugol.android.editor.ConfiguracoesDaPesquisa;

public class LocalizarSubstituirDialog extends DialogFragment {
    public static final String ARGUMENTO_LOCALIZAR = "localizar";
    public static final String ARGUMENTO_DIFERENCIAR_MAIUSCULAS = "diferenciarMaiusculas";
    public static final String ARGUMENTO_SUBSTITUIR_POR = "substituirPor";

    public interface Listener {
        void localizar(String localizar, boolean diferenciarMaiusculas);

        void substituir(String localizar, boolean diferenciarMaiusculas, String substituirPor);
    }

    private Listener listener;

    private EditText edLocalizar, edSubstituirPor;
    private CheckBox cbDiferenciar, cbSubstituir;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (Listener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " deve implementar LocalizarSubstituirDialog.Listener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ConfiguracoesDaPesquisa configuracoesDaPesquisa;

        if (getArguments() == null) {
            configuracoesDaPesquisa = new ConfiguracoesDaPesquisa();
        } else {
            configuracoesDaPesquisa = new ConfiguracoesDaPesquisa(
                    getArguments().getString(ARGUMENTO_LOCALIZAR),
                    getArguments().getBoolean(ARGUMENTO_DIFERENCIAR_MAIUSCULAS),
                    getArguments().getString(ARGUMENTO_SUBSTITUIR_POR)
            );
        }

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_localizar_substituir, null);

        edLocalizar = (EditText) view.findViewById(R.id.edit_text_localizar);
        edLocalizar.setText(configuracoesDaPesquisa.getLocalizar());

        edSubstituirPor = (EditText) view.findViewById(R.id.edit_text_substituir_por);
        edSubstituirPor.setText(configuracoesDaPesquisa.getSubstituirPor());

        cbDiferenciar = (CheckBox) view.findViewById(R.id.checkbox_diferenciar);
        cbDiferenciar.setChecked(configuracoesDaPesquisa.isDiferenciarMaiusculas());

        cbSubstituir = (CheckBox) view.findViewById(R.id.checkbox_substituir);
        cbSubstituir.setChecked(configuracoesDaPesquisa.getSubstituirPor() != null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_localizar_substituir_titulo).setView(view)
                .setPositiveButton(R.string.dialog_localizar_substituir_button_localizar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Esconde o teclado
                        // https://stackoverflow.com/a/5106399/1657502
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(edLocalizar.getWindowToken(), 0);

                        if (cbSubstituir.isChecked()) {
                            listener.substituir(edLocalizar.getText().toString(), cbDiferenciar.isChecked(), edSubstituirPor.getText().toString());
                        } else {
                            listener.localizar(edLocalizar.getText().toString(), cbDiferenciar.isChecked());
                        }
                    }
                })
                .setNegativeButton(R.string.dialog_localizar_substituir_button_cancelar, null);
        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                edLocalizar.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        // https://stackoverflow.com/a/8240314/1657502
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(!TextUtils.isEmpty(editable));
                    }
                });

                cbSubstituir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean marcado = ((CheckBox) view).isChecked();
                        edSubstituirPor.setVisibility(marcado ? View.VISIBLE : View.GONE);
                    }
                });

                edSubstituirPor.setVisibility(cbSubstituir.isChecked() ? View.VISIBLE : View.GONE);

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(!TextUtils.isEmpty(edLocalizar.getEditableText()));

                // Foca no campo Localizar e mostra o teclado
                // https://stackoverflow.com/a/5106399/1657502
                edLocalizar.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edLocalizar, InputMethodManager.SHOW_IMPLICIT);
                // Move o cursor para o final do campo
                // https://stackoverflow.com/a/44428987/1657502
                edLocalizar.setSelection(edLocalizar.getText().length());
            }
        });

        return dialog;
    }
}
