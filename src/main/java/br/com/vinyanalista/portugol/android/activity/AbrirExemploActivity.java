package br.com.vinyanalista.portugol.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.vinyanalista.portugol.android.R;
import br.com.vinyanalista.portugol.interpretador.Exemplo;

public class AbrirExemploActivity extends BaseActivity {
    static final String EXTRA_EXEMPLO_SELECIONADO = "EXEMPLO_SELECIONADO";

    private Intent resultadoDaActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abrir_exemplo);

        configurarToolbar();
        configurarBotaoDeVoltar();

        ListView lvExemplos = (ListView) findViewById(R.id.list_view_exemplos);

        setResult(Activity.RESULT_CANCELED, null);
        resultadoDaActivity = new Intent();

        List<String> listaDeExemplos = new ArrayList<String>();
        for (int e = 0; e < Exemplo.values().length; e++) {
            Exemplo exemplo = Exemplo.values()[e];
            String nomeDoExemplo = (e + 1) + " - " + exemplo.getNome();
            listaDeExemplos.add(nomeDoExemplo);
        }

        lvExemplos.setAdapter(new AdapterPersonalizado(getBaseContext(), R.layout.listview_item_exemplo,
                R.id.listview_item_nome, listaDeExemplos));
        lvExemplos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position,
                                    long rowId) {
                Exemplo exemploSelecionado = Exemplo.values()[position];
                resultadoDaActivity.putExtra(EXTRA_EXEMPLO_SELECIONADO, exemploSelecionado.getProgramaFonte());
                AbrirExemploActivity.this.setResult(RESULT_OK, resultadoDaActivity);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private class AdapterPersonalizado extends ArrayAdapter<String> {
        AdapterPersonalizado(Context context, int resource, int textViewResourceId, List<String> item) {
            super(context, resource, textViewResourceId, item);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View item = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                    R.layout.listview_item_exemplo, null);
            TextView nome = (TextView) item.findViewById(R.id.listview_item_nome);
            nome.setText(getItem(position));
            ImageView icone = (ImageView) item.findViewById(R.id.listview_item_icone);
            icone.setImageResource(R.mipmap.ic_launcher);
            return item;
        }
    }
}
