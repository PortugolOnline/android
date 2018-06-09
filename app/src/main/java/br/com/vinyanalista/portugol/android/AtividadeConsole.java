package br.com.vinyanalista.portugol.android;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

public class AtividadeConsole extends AtividadeBase {
    TextView tvSaida;
    ScrollView svSaida;
    EditText edEntrada;
    Button btEntrar, btEncerrar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atividade_console);

        configurarToolbar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvSaida = (TextView) findViewById(R.id.saida);
        svSaida = (ScrollView) findViewById(R.id.scroll_saida);
        edEntrada = (EditText) findViewById(R.id.entrada);
        btEntrar = (Button) findViewById(R.id.entrar);
        btEncerrar = (Button) findViewById(R.id.encerrar);

        // Rola a TextView para baixo sempre que texto é adicionado
        // https://stackoverflow.com/a/19860791/1657502
        tvSaida.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                svSaida.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

        btEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entrar();
            }
        });

        btEncerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                encerrar();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void adicionarTexto() {
        // TODO Apenas teste, remover
        String texto = "adicionarTexto()\n";
        SpannableString textoComEstilo = new SpannableString(texto);
        textoComEstilo.setSpan(new ForegroundColorSpan(Color.GREEN), 0, textoComEstilo.length(), 0);
        tvSaida.append(textoComEstilo);
    }

    public void adicionarTexto2() {
        // TODO Apenas teste, remover
        String texto = "adicionarTexto2()\n";
        SpannableString textoComEstilo = new SpannableString(texto);
        textoComEstilo.setSpan(new ForegroundColorSpan(Color.RED), 0, textoComEstilo.length(), 0);
        tvSaida.append(textoComEstilo);
    }

    public void entrar() {
        adicionarTexto();
    }

    public void encerrar() {
        adicionarTexto2();
    }
}