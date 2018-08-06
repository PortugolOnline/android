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
import android.widget.CheckBox;
import android.widget.EditText;

import br.com.vinyanalista.portugol.android.R;

public class LocalizarSubstituirFragment extends DialogFragment {
    public interface LocalizarSubstituirFragmentListener {
        void localizar(String localizar, boolean diferenciar);

        void substituir(String localizar, boolean diferenciar, String substituirPor);
    }

    private LocalizarSubstituirFragmentListener listener;

    private EditText edLocalizar, edSubstituirPor;
    private CheckBox cbDiferenciar, cbSubstituir;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (LocalizarSubstituirFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " deve implementar LocalizarSubstituirFragmentListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_localizar_substituir, null);

        edLocalizar = (EditText) view.findViewById(R.id.edit_text_localizar);
        edSubstituirPor = (EditText) view.findViewById(R.id.edit_text_substituir_por);
        cbDiferenciar = (CheckBox) view.findViewById(R.id.checkbox_diferenciar);
        cbSubstituir = (CheckBox) view.findViewById(R.id.checkbox_substituir);

        cbSubstituir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean marcado = ((CheckBox) view).isChecked();
                edSubstituirPor.setVisibility(marcado ? View.VISIBLE : View.GONE);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setPositiveButton(R.string.dialog_localizar_substituir_button_localizar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Localizar
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
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

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
            }
        });

        return dialog;
    }
}
