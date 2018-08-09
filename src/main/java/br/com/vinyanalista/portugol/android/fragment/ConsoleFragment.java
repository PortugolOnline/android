package br.com.vinyanalista.portugol.android.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.IOException;

import br.com.vinyanalista.portugol.android.BuildConfig;
import br.com.vinyanalista.portugol.android.R;
import br.com.vinyanalista.portugol.android.activity.MainActivity;
import br.com.vinyanalista.portugol.android.util.S;
import br.com.vinyanalista.portugol.base.lexer.LexerException;
import br.com.vinyanalista.portugol.base.parser.ParserException;
import br.com.vinyanalista.portugol.interpretador.Interpretador;
import br.com.vinyanalista.portugol.interpretador.Terminal;
import br.com.vinyanalista.portugol.interpretador.analise.ErroSemantico;
import br.com.vinyanalista.portugol.interpretador.execucao.ErroEmTempoDeExecucao;
import br.com.vinyanalista.portugol.interpretador.execucao.EscutaDeExecutor;

public class ConsoleFragment extends BaseFragment implements EscutaDeExecutor {
    static final String CODIGO_FONTE = "CODIGO_FONTE";

    public interface Listener {
        void destacarLinhaComErro(int linha, int coluna);
    }

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
        // TODO Refatorar tratamento de exceção
        if (erroEmTempoDeExecucao != null) {
            tratarErro(erroEmTempoDeExecucao);
        }
    }

    class TerminalAndroid extends Terminal {
        @Override
        public void erro(final String mensagemDeErro) {
            S.l(this, "erro() - mensagemDeErro: " + mensagemDeErro);
            if (BuildConfig.DEBUG && isEncerrado()) {
                throw new AssertionError("Chamada a erro() com o terminal já encerrado");
            }
            // TODO Refatorar tratamento de exceção
            /*if (isEncerrado()) {
                return;
            }*/
            /*mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adicionarASaida(mensagemDeErro, Color.RED);
                }
            });*/
            //tratarErro();
        }

        @Override
        public synchronized void encerrar() {
            S.l(this, "encerrar()");
            if (BuildConfig.DEBUG && isEncerrado()) {
                throw new AssertionError("Chamada a encerrar() com o terminal já encerrado");
            }
            super.encerrar();

            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    edEntrada.setEnabled(false);
                    btEntrar.setEnabled(false);
                    btEncerrar.setEnabled(false);
                }
            });

            notify();
        }

        private synchronized void entrar() {
            S.l(this, "entrar()");
            notify();
        }

        @Override
        protected void escrever(final String mensagem) {
            S.l(this, "escrever() - mensagem: " + mensagem);
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adicionarASaida(mensagem, Color.BLACK);
                }
            });
        }

        @Override
        public void informacao(final String mensagemDeInformacao) {
            S.l(this, "informacao() - mensagemDeInformacao: " + mensagemDeInformacao);
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adicionarASaida(mensagemDeInformacao, Color.BLUE);
                }
            });
        }

        @Override
        protected String ler() {
            S.l(this, "ler()");
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    edEntrada.setEnabled(true);
                    btEntrar.setEnabled(true);

                    // Foca no campo Entrada e mostra o teclado
                    // https://stackoverflow.com/a/5106399/1657502
                    edEntrada.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(edEntrada, InputMethodManager.SHOW_IMPLICIT);
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
            S.l(this, "limpar()");
            tvSaida.setText(R.string.vazia);
        }
    }

    private void adicionarASaida(String texto, int cor) {
        SpannableString textoComEstilo = new SpannableString(texto + "\n");
        textoComEstilo.setSpan(new ForegroundColorSpan(cor), 0, textoComEstilo.length(), 0);
        tvSaida.append(textoComEstilo);
        // Rola a TextView para baixo sempre que texto é adicionado
        // https://stackoverflow.com/a/28403308/1657502
        svSaida.fullScroll(View.FOCUS_DOWN);
    }

    public void executar(String codigoFonte) {
        tvSaida.setText(R.string.vazia);
        terminal = new TerminalAndroid();
        interpretador = new Interpretador(terminal);
        interpretador.adicionarEscutaDeExecutor(this);
        try {
            interpretador.analisar(codigoFonte);
            interpretador.executar();
            btEncerrar.setEnabled(true);
        } catch (Exception erro) {
            // TODO Refatorar tratamento de exceção
            tratarErro(erro);
        }
    }

    private void tratarErro(Exception erro) {
        // TODO Refatorar tratamento de exceção
        S.l(this, "tratarErro(erro)");
        int linha = -1;
        int coluna = -1;
        final StringBuilder mensagemDeErro = new StringBuilder("Erro");
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
            final int linhaFinal = linha, colunaFinal = coluna;
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.destacarLinhaComErro(linhaFinal, colunaFinal);
                }
            });
        }
        mensagemDeErro.append("\n").append(erro.getLocalizedMessage());
        //terminal.erro(mensagemDeErro.toString());
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adicionarASaida(mensagemDeErro.toString(), Color.RED);
            }
        });
        if (!terminal.isEncerrado()) {
            terminal.encerrar();
        }
    }
}
