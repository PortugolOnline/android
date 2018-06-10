package br.com.vinyanalista.portugol.android;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.IOException;

import br.com.vinyanalista.portugol.base.lexer.LexerException;
import br.com.vinyanalista.portugol.base.parser.ParserException;
import br.com.vinyanalista.portugol.interpretador.Interpretador;
import br.com.vinyanalista.portugol.interpretador.Terminal;
import br.com.vinyanalista.portugol.interpretador.analise.ErroSemantico;
import br.com.vinyanalista.portugol.interpretador.execucao.ErroEmTempoDeExecucao;

public class AtividadeConsole extends AtividadeBase {
    static final String CODIGO_FONTE = "CODIGO_FONTE";

    TextView tvSaida;
    ScrollView svSaida;
    EditText edEntrada;
    Button btEntrar, btEncerrar;

    protected Interpretador interpretador;
    protected TerminalAndroid terminal;

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
                terminal.encerrar();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Bundle argumentos = getIntent().getExtras();
        String codigoFonte = argumentos.getString(CODIGO_FONTE);
        terminal = new TerminalAndroid();
        interpretador = new Interpretador(terminal);
        try {
            interpretador.analisar(codigoFonte);
            interpretador.executar();
        } catch (Exception erro) {
            tratarErro(erro);
        }
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

    class TerminalAndroid extends Terminal {
        @Override
        public void erro(String mensagemDeErro) {
            if (BuildConfig.DEBUG && isEncerrado()) {
                throw new AssertionError("Chamada a erro() com o terminal já encerrado");
            }
            adicionarASaida(mensagemDeErro, Color.RED);
        }

        @Override
        public synchronized void encerrar() {
            if (BuildConfig.DEBUG && isEncerrado()) {
                throw new AssertionError("Chamada a encerrar() com o terminal já encerrado");
            }
            super.encerrar();
            btEntrar.setEnabled(false);
            // TODO android.util.AndroidRuntimeException: Animators may only be run on Looper threads
            //btEncerrar.setEnabled(false);
            edEntrada.setEnabled(false);
            notify();
        }

        @Override
        protected void escrever(String mensagem) {
            adicionarASaida(mensagem, Color.BLACK);
        }

        @Override
        public void informacao(String mensagemDeInformacao) {
            adicionarASaida(mensagemDeInformacao, Color.BLUE);
        }

        @Override
        protected String ler() {
            btEntrar.setEnabled(true);
            edEntrada.setEnabled(true);
            edEntrada.requestFocus();
            try {
                wait();
            } catch (InterruptedException excecao) {
                return null;
            }
            if (isEncerrado()) {
                return null;
            }
            btEntrar.setEnabled(false);
            edEntrada.setEnabled(false);
            String leitura = edEntrada.getText().toString();
            edEntrada.setText(R.string.vazio);
            adicionarASaida(leitura, Color.CYAN);
            return leitura;
        }

        @Override
        public void limpar() {

        }
    }

    private void adicionarASaida(String texto, int cor) {
        SpannableString textoComEstilo = new SpannableString(texto);
        textoComEstilo.setSpan(new ForegroundColorSpan(cor), 0, textoComEstilo.length(), 0);
        tvSaida.append(textoComEstilo);
    }

    private synchronized void entrar() {
        notify();
    }

    private void tratarErro(Exception erro) {
        int linha = -1;
        int coluna = -1;
        StringBuilder mensagemDeErro = new StringBuilder("Erro");
        if (erro instanceof LexerException) {
            LexerException erroLexico = (LexerException) erro;
            linha = erroLexico.getToken().getLine();
            coluna = erroLexico.getToken().getPos();
            mensagemDeErro.append(" léxico");
        } else if (erro instanceof ParserException) {
            ParserException erroSintatico = (ParserException) erro;
            linha = erroSintatico.getToken().getLine();
            coluna = erroSintatico.getToken().getPos();
            mensagemDeErro.append(" sintático");
        } else if (erro instanceof ErroSemantico) {
            ErroSemantico erroSemantico = (ErroSemantico) erro;
            linha = erroSemantico.getLinha();
            coluna = erroSemantico.getColuna();
            mensagemDeErro.append(" semântico");
        } else if (erro instanceof ErroEmTempoDeExecucao) {
            ErroEmTempoDeExecucao erroEmTempoDeExecucao = (ErroEmTempoDeExecucao) erro;
            linha = erroEmTempoDeExecucao.getLinha();
            coluna = erroEmTempoDeExecucao.getColuna();
            mensagemDeErro.append(" em tempo de execução");
        } else if (erro instanceof IOException) {
            mensagemDeErro.append(" de entrada/saída");
        }
        if ((linha != -1) && (coluna != -1)) {
            mensagemDeErro.append(" na linha ").append(linha).append(" coluna ").append(coluna);
        }
        mensagemDeErro.append("\n").append(erro.getLocalizedMessage());
        terminal.erro(mensagemDeErro.toString());
    }
}