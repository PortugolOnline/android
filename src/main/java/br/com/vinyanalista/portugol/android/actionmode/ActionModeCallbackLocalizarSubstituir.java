package br.com.vinyanalista.portugol.android.actionmode;

import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import br.com.vinyanalista.portugol.android.R;
import br.com.vinyanalista.portugol.android.util.S;

public class ActionModeCallbackLocalizarSubstituir implements ActionMode.Callback {
    private boolean substituir = false;

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.toolbar_localizar_substituir, menu);
        menu.findItem(R.id.action_localizar_substituir_substituir).setVisible(substituir);
        menu.findItem(R.id.action_localizar_substituir_substituir_tudo).setVisible(substituir);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        S.l(this, "onActionItemClicked()");
        switch (item.getItemId()) {
            case R.id.action_localizar_substituir_anterior:
                S.l(this, "Anterior");
                return true;
            case R.id.action_localizar_substituir_proximo:
                S.l(this, "Próximo");
                return true;
            case R.id.action_localizar_substituir_substituir:
                S.l(this, "Substituir");
                return true;
            case R.id.action_localizar_substituir_substituir_tudo:
                S.l(this, "Substituir tudo");
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
    }

    public void setSubstituir(boolean substituir) {
        this.substituir = substituir;
    }
}
