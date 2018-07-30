package br.com.vinyanalista.portugol.android.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import br.com.vinyanalista.portugol.android.R;

public class CompartilharFragment extends DialogFragment {
    // https://developer.android.com/guide/topics/ui/dialogs

    public interface CompartilharFragmentListener {
        void compartilharComoArquivo();

        void compartilharComoTexto();
    }

    private CompartilharFragmentListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (CompartilharFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " deve implementar CompartilharFragmentListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.compartilhar_dialogo_titulo)
                .setItems(R.array.compartilhar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                listener.compartilharComoTexto();
                                break;
                            case 1:
                                listener.compartilharComoArquivo();
                                break;
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        return dialog;
    }
}
