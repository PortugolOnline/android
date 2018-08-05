package br.com.vinyanalista.portugol.android.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.IOException;

import br.com.vinyanalista.portugol.android.BuildConfig;
import br.com.vinyanalista.portugol.android.R;
import br.com.vinyanalista.portugol.android.activity.MainActivity;
import br.com.vinyanalista.portugol.base.lexer.LexerException;
import br.com.vinyanalista.portugol.base.parser.ParserException;
import br.com.vinyanalista.portugol.interpretador.Interpretador;
import br.com.vinyanalista.portugol.interpretador.Terminal;
import br.com.vinyanalista.portugol.interpretador.analise.ErroSemantico;
import br.com.vinyanalista.portugol.interpretador.execucao.ErroEmTempoDeExecucao;
import br.com.vinyanalista.portugol.interpretador.execucao.EscutaDeExecutor;

public class ConsoleFragment extends BaseFragment implements EscutaDeExecutor {
    static final String CODIGO_FONTE = "CODIGO_FONTE";
    static final String TAG = "TESTE";

    private MainActivity mainActivity;

    private TextView tvSaida;
    private ScrollView svSaida;
    private EditText edEntrada;
    private Button btEntrar, btEncerrar;

    private Interpretador interpretador;
    private TerminalAndroid terminal;

    public ConsoleFragment() {
    }

    public static ConsoleFragment newInstance(MainActivity mainActivity) {
        ConsoleFragment fragment = new ConsoleFragment();
        fragment.mainActivity = mainActivity;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_console, container, false);

        tvSaida = (TextView) view.findViewById(R.id.saida);
        svSaida = (ScrollView) view.findViewById(R.id.scroll_saida);
        edEntrada = (EditText) view.findViewById(R.id.entrada);
        btEntrar = (Button) view.findViewById(R.id.entrar);
        btEncerrar = (Button) view.findViewById(R.id.encerrar);

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
                terminal.entrar();
            }
        });

        btEncerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                terminal.informacao("\nA execução do programa foi interrompida pelo usuário.");
                terminal.encerrar();
            }
        });

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void aoEncerrarExecucao(ErroEmTempoDeExecucao erroEmTempoDeExecucao) {
        // TODO Esse método é realmente necessário?
        /*if (erroEmTempoDeExecucao != null) {
            tratarErro(erroEmTempoDeExecucao);
        }*/
    }

    class TerminalAndroid extends Terminal {
        @Override
        public void erro(final String mensagemDeErro) {
            Log.d(TAG, "erro(" + mensagemDeErro + ")");
            if (BuildConfig.DEBUG && isEncerrado()) {
                throw new AssertionError("Chamada a erro() com o terminal já encerrado");
            }
            /*if (isEncerrado()) {
                return;
            }*/
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adicionarASaida(mensagemDeErro, Color.RED);
                }
            });
        }

        @Override
        public synchronized void encerrar() {
            Log.d(TAG, "encerrar()");
            if (BuildConfig.DEBUG && isEncerrado()) {
                throw new AssertionError("Chamada a encerrar() com o terminal já encerrado");
            }
            super.encerrar();

            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btEntrar.setEnabled(false);
                    btEncerrar.setEnabled(false);
                    edEntrada.setEnabled(false);
                }
            });

            notify();
        }

        private synchronized void entrar() {
            Log.d(TAG, "entrar()");
            notify();
        }

        @Override
        protected void escrever(final String mensagem) {
            Log.d(TAG, "escrever(" + mensagem + ")");
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adicionarASaida(mensagem, Color.BLACK);
                }
            });
        }

        @Override
        public void informacao(final String mensagemDeInformacao) {
            Log.d(TAG, "informacao(" + mensagemDeInformacao + ")");
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adicionarASaida(mensagemDeInformacao, Color.BLUE);
                }
            });
        }

        @Override
        protected String ler() {
            Log.d(TAG, "ler()");
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btEntrar.setEnabled(true);
                    edEntrada.setEnabled(true);
                    edEntrada.requestFocus();
                }
            });
            try {
                wait();
            } catch (InterruptedException excecao) {
                return null;
            }
            if (isEncerrado()) {
                return null;
            }
            final String leitura = edEntrada.getText().toString();
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btEntrar.setEnabled(false);
                    edEntrada.setEnabled(false);
                    edEntrada.setText(R.string.vazia);
                    adicionarASaida(leitura, Color.CYAN);
                }
            });
            return leitura;
        }

        @Override
        public void limpar() {
            Log.d(TAG, "limpar()");
        }
    }

    private void adicionarASaida(String texto, int cor) {
        SpannableString textoComEstilo = new SpannableString(texto + "\n");
        textoComEstilo.setSpan(new ForegroundColorSpan(cor), 0, textoComEstilo.length(), 0);
        tvSaida.append(textoComEstilo);
    }

    public void executar(String codigoFonte) {
        terminal = new TerminalAndroid();
        interpretador = new Interpretador(terminal);
        interpretador.adicionarEscutaDeExecutor(this);
        try {
            interpretador.analisar(codigoFonte);
            interpretador.executar();
        } catch (Exception erro) {
            tratarErro(erro);
        }
    }

    private void tratarErro(Exception erro) {
        Log.d(TAG, "tratarErro()");
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
        if (!terminal.isEncerrado()) {
            terminal.encerrar();
        }
    }
}
