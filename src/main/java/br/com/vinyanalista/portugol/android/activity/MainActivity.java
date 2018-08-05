package br.com.vinyanalista.portugol.android.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import br.com.vinyanalista.portugol.android.BuildConfig;
import br.com.vinyanalista.portugol.android.editor.Editor;
import br.com.vinyanalista.portugol.android.editor.EditorListener;
import br.com.vinyanalista.portugol.android.R;
import br.com.vinyanalista.portugol.android.adapter.TabsAdapter;
import br.com.vinyanalista.portugol.android.fragment.CompartilharFragment;
import br.com.vinyanalista.portugol.android.fragment.SalvarDescartarFragment;
import br.com.vinyanalista.portugol.android.util.S;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, CompartilharFragment.CompartilharFragmentListener, EditorListener, SalvarDescartarFragment.SalvarDescartarFragmentListener {
    private static final String ARQUIVO_SEM_NOME = "Sem nome";
    private static final String NOME_DE_ARQUIVO_PADRAO = "algoritmo.por";
    static final int REQUEST_ABRIR_ARQUIVO = 1;
    static final int REQUEST_ABRIR_EXEMPLO = 2;
    static final int REQUEST_NOVO = 3;
    static final int REQUEST_SALVAR = 4;
    static final int REQUEST_SALVAR_COMO = 5;
    static final int REQUEST_SALVAR_COMO_E_ABRIR_ARQUIVO = 6;
    static final int REQUEST_SALVAR_COMO_E_ABRIR_EXEMPLO = 7;
    static final int REQUEST_SALVAR_COMO_E_NOVO = 8;

    static final String ESTADO_EDITOR_CODIGO_FONTE = "ESTADO_EDITOR_CODIGO_FONTE";
    static final String ESTADO_EDITOR_LINHA = "ESTADO_EDITOR_LINHA";
    static final String ESTADO_EDITOR_COLUNA = "ESTADO_EDITOR_COLUNA";

    private DrawerLayout drawerLayout;
    private Menu menuToolbarPrincipal;
    private NavigationView navigationView;
    private TabsAdapter tabsAdapter;
    private ViewPager viewPager;

    private boolean arquivoModificado = false;
    private Uri caminhoDoArquivo = null;
    private String nomeDoArquivo = ARQUIVO_SEM_NOME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configurarToolbar();

        // Navigation Drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.nav_drawer_abrir, R.string.nav_drawer_fechar) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                MainActivity.this.onDrawerOpened();
            }
        };
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // ViewPager e TabLayout
        tabsAdapter = new TabsAdapter(getSupportFragmentManager(), this);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(tabsAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        // https://stackoverflow.com/a/36638144/1657502
        // TODO Realmente necessário?
        if (savedInstanceState != null) {
            S.l(this, "\n\n!!! savedInstanceState != null\n\n");

            String codigoFonte = savedInstanceState.getString(ESTADO_EDITOR_CODIGO_FONTE);
            S.l(this, "codigoFonte:\n" + codigoFonte + "\n");
            int linha = savedInstanceState.getInt(ESTADO_EDITOR_LINHA);
            int coluna = savedInstanceState.getInt(ESTADO_EDITOR_COLUNA);
            S.l(this, "linha: " + linha + ", coluna: " + coluna);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        S.l(this, "onActivityResult() - requestCode: " + requestCode + ", resultCode: " + resultCode);
        switch (requestCode) {
            case REQUEST_ABRIR_ARQUIVO:
                // https://developer.android.com/guide/topics/providers/document-provider
                if ((resultCode == RESULT_OK) && (data != null) && (data.getData() != null)) {
                    Uri caminhoDoArquivo = data.getData();
                    abrirArquivo(caminhoDoArquivo);
                }
                break;
            case REQUEST_ABRIR_EXEMPLO:
                if (resultCode == RESULT_OK) {
                    String exemplo = data.getStringExtra(AbrirExemploActivity.EXTRA_EXEMPLO_SELECIONADO);
                    abrirExemplo(exemplo);
                }
                break;
            case REQUEST_SALVAR_COMO:
            case REQUEST_SALVAR_COMO_E_ABRIR_ARQUIVO:
            case REQUEST_SALVAR_COMO_E_ABRIR_EXEMPLO:
            case REQUEST_SALVAR_COMO_E_NOVO:
                // https://developer.android.com/guide/topics/providers/document-provider
                if ((resultCode == RESULT_OK) && (data != null) && (data.getData() != null)) {
                    Uri caminhoDoArquivo = data.getData();
                    salvar(caminhoDoArquivo);
                }
                break;
        }
        switch (requestCode) {
            case REQUEST_SALVAR_COMO_E_ABRIR_ARQUIVO:
                abrirArquivo();
                break;
            case REQUEST_SALVAR_COMO_E_ABRIR_EXEMPLO:
                abrirExemplo();
                break;
            case REQUEST_SALVAR_COMO_E_NOVO:
                novo();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if ((drawerLayout != null) && (drawerLayout.isDrawerOpen(GravityCompat.START))) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuToolbarPrincipal = menu;
        getMenuInflater().inflate(R.menu.toolbar_principal, menu);
        return true;
    }

    public void onDrawerOpened() {
        S.l(this, "onDrawerOpened()");
        // Esconde o teclado quando aparece o menu lateral
        // https://stackoverflow.com/a/39088728/1657502
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        // Atualiza o nome do arquivo e se foi modificado ou não
        // https://stackoverflow.com/a/35952939/1657502
        // https://stackoverflow.com/a/45520466/1657502
        View headerView = navigationView.getHeaderView(0);

        TextView tvNomeDoArquivo = (TextView) headerView.findViewById(R.id.nav_header_nome_do_arquivo);
        tvNomeDoArquivo.setText(nomeDoArquivo);

        TextView tvModificado = (TextView) headerView.findViewById(R.id.nav_header_modificado);
        tvModificado.setText(arquivoModificado ? R.string.nav_header_modificado : R.string.vazia);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        S.l(this, "onNavigationItemSelected()");
        if (drawerLayout != null) {
            switch (item.getItemId()) {
                case R.id.nav_drawer_abrir_arquivo:
                    if (arquivoModificado) {
                        confirmarDescartarAlteracoes(REQUEST_ABRIR_ARQUIVO);
                    } else {
                        abrirArquivo();
                    }
                    break;
                case R.id.nav_drawer_abrir_exemplo:
                    if (arquivoModificado) {
                        confirmarDescartarAlteracoes(REQUEST_ABRIR_EXEMPLO);
                    } else {
                        abrirExemplo();
                    }
                    break;
                case R.id.nav_drawer_compartilhar:
                    compartilhar();
                    break;
                case R.id.nav_drawer_novo:
                    if (arquivoModificado) {
                        confirmarDescartarAlteracoes(REQUEST_NOVO);
                    } else {
                        novo();
                    }
                    break;
                case R.id.nav_drawer_salvar:
                    salvar();
                    break;
                case R.id.nav_drawer_salvar_como:
                    salvarComo();
                    break;
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_aumentar_fonte:
                aumentarFonte();
                return true;
            case R.id.action_desfazer:
                desfazer();
                return true;
            case R.id.action_diminuir_fonte:
                diminuirFonte();
                return true;
            case R.id.action_executar:
                executar();
                return true;
            case R.id.action_refazer:
                refazer();
                return true;
            case R.id.action_salvar:
                salvar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // TODO Implementar
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // https://developer.android.com/training/permissions/requesting
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Ensure we update the availability of our storage provider
            getContentResolver().notifyChange(DocumentsContract.buildRootsUri(BuildConfig.DOCUMENTS_AUTHORITY), null);
            switch (requestCode) {
                case REQUEST_ABRIR_ARQUIVO:
                    abrirArquivo();
                    break;
                case REQUEST_SALVAR:
                    salvar(false);
                    break;
                case REQUEST_SALVAR_COMO:
                    salvar(true);
                    break;
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // https://stackoverflow.com/a/36638144/1657502
        // TODO Realmente necessário?
        super.onSaveInstanceState(outState);
        outState.putString(ESTADO_EDITOR_CODIGO_FONTE, getEditor().getCodigoFonte());
        outState.putInt(ESTADO_EDITOR_LINHA, getEditor().getLinha());
        outState.putInt(ESTADO_EDITOR_COLUNA, getEditor().getColuna());
    }

    private void abrirArquivo() {
        S.l(this, "abrirArquivo()");
        if (temPermissaoParaEscreverArquivos()) {
            // https://developer.android.com/guide/topics/providers/document-provider
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/plain");
            startActivityForResult(intent, REQUEST_ABRIR_ARQUIVO);
        } else {
            solicitarPermissaoParaEscreverArquivos(REQUEST_ABRIR_ARQUIVO);
        }
    }

    private void abrirArquivo(Uri caminhoDoArquivo) {
        S.l(this, "abrirArquivo(caminhoDoArquivo: " + caminhoDoArquivo + ")");
        // https://developer.android.com/guide/topics/providers/document-provider
        Cursor cursor = getContentResolver().query(caminhoDoArquivo, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(caminhoDoArquivo);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }
                inputStream.close();
                String codigoFonte = stringBuilder.toString();

                arquivoModificado = false;
                this.caminhoDoArquivo = caminhoDoArquivo;
                String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                nomeDoArquivo = displayName;
                setCodigoFonte(codigoFonte);
                getEditor().limparHistoricoDesfazerRefazer();
            } catch (FileNotFoundException excecao) {
                Snackbar.make(viewPager, "Erro: arquivo não encontrado", Snackbar.LENGTH_SHORT).show();
                return;
            } catch (IOException excecao) {
                Snackbar.make(viewPager, "Erro de entrada/saída", Snackbar.LENGTH_SHORT).show();
                return;
            } finally {
                cursor.close();
            }
        }
    }

    private void abrirExemplo() {
        S.l(this, "abrirExemplo()");
        Intent intent = new Intent(getBaseContext(), AbrirExemploActivity.class);
        startActivityForResult(intent, REQUEST_ABRIR_EXEMPLO);
    }

    private void abrirExemplo(String exemplo) {
        S.l(this, "abrirExemplo(exemplo)");
        arquivoModificado = false;
        caminhoDoArquivo = null;
        nomeDoArquivo = ARQUIVO_SEM_NOME;
        setCodigoFonte(exemplo);
        getEditor().limparHistoricoDesfazerRefazer();
    }

    @Override
    public void aoModificarCodigoFonte(Editor editor) {
        boolean habilitarDesfazer = editor.isDesfazerPossivel();
        // Se desfazer é possível, então o arquivo foi modificado
        arquivoModificado = habilitarDesfazer;
        boolean habilitarRefazer = editor.isRefazerPossivel();
        //S.l(this, "aoModificarCodigoFonte() - habilitarDesfazer: " + habilitarDesfazer + ", habilitarRefazer: " + habilitarRefazer);

        menuToolbarPrincipal.findItem(R.id.action_desfazer).setEnabled(habilitarDesfazer);
        menuToolbarPrincipal.findItem(R.id.action_refazer).setEnabled(habilitarRefazer);
    }

    @Override
    public void aoMovimentarCursor(Editor editor) {
        //S.l(this, "aoMovimentarCursor() - linha: " + editor.getLinha() + ", coluna: " + editor.getColuna());
    }

    private boolean arquivoNovo() {
        return (caminhoDoArquivo == null);
    }

    private void aumentarFonte() {
        S.l(this, "aumentarFonte()");
        getEditor().aumentarFonte();
    }

    private void compartilhar() {
        CompartilharFragment compartilhar = new CompartilharFragment();
        compartilhar.show(getSupportFragmentManager(), "CompartilharFragment");
    }

    @Override
    public void compartilharComoArquivo() {
        // https://stackoverflow.com/a/28694269/1657502
        // https://guides.codepath.com/android/Sharing-Content-with-Intents
        // https://developer.android.com/training/data-storage/files
        // https://developer.android.com/training/sharing/send#send-binary-content
        // https://developer.android.com/training/secure-file-sharing/share-file
        File compartilharPasta = new File(getCacheDir(), "compartilhar");
        if (compartilharPasta.exists()) {
            deletarRecursivamente(compartilharPasta);
        }
        compartilharPasta.mkdirs();

        String compartilharNomeDoArquivo = nomeDoArquivo;
        File compartilharArquivo = null;
        try {
            compartilharArquivo = new File(compartilharPasta, compartilharNomeDoArquivo);
            FileOutputStream fos = new FileOutputStream(compartilharArquivo);
            String codigoFonte = getCodigoFonte();
            fos.write(codigoFonte.getBytes());
            fos.close();
        } catch (FileNotFoundException excecao) {
            Snackbar.make(viewPager, "Erro: arquivo não encontrado", Snackbar.LENGTH_SHORT).show();
            return;
        } catch (IOException excecao) {
            Snackbar.make(viewPager, "Erro de entrada/saída", Snackbar.LENGTH_SHORT).show();
            return;
        }

        // https://developer.android.com/studio/build/gradle-tips#share-properties-with-the-manifest
        Uri compartilharUri = FileProvider.getUriForFile(this,
                BuildConfig.FILES_AUTHORITY, compartilharArquivo);

        Intent intentCompartilhar = new Intent(Intent.ACTION_SEND);
        intentCompartilhar.setType("text/plain");
        intentCompartilhar.putExtra(Intent.EXTRA_SUBJECT, getResources().getText(R.string.compartilhar_titulo));
        intentCompartilhar.putExtra(Intent.EXTRA_STREAM, compartilharUri);
        intentCompartilhar.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intentCompartilhar, getResources().getText(R.string.compartilhar_como_arquivo)));
    }

    @Override
    public void compartilharComoTexto() {
        // https://developer.android.com/training/sharing/send#send-text-content
        Intent intentCompartilhar = new Intent(Intent.ACTION_SEND);
        intentCompartilhar.setType("text/plain");
        intentCompartilhar.putExtra(Intent.EXTRA_SUBJECT, getResources().getText(R.string.compartilhar_titulo));
        intentCompartilhar.putExtra(Intent.EXTRA_TEXT, getCodigoFonte());
        startActivity(Intent.createChooser(intentCompartilhar, getResources().getText(R.string.compartilhar_como_texto)));
    }

    private void confirmarDescartarAlteracoes(int requestCode) {
        S.l(this, "confirmarDescartarAlteracoes(requestCode: " + requestCode + ")");
        Bundle argumentos = new Bundle();
        argumentos.putInt(SalvarDescartarFragment.KEY_REQUEST_CODE, requestCode);
        argumentos.putString(SalvarDescartarFragment.KEY_NOME_DO_ARQUIVO, nomeDoArquivo);
        SalvarDescartarFragment dialogo = new SalvarDescartarFragment();
        dialogo.setArguments(argumentos);
        dialogo.show(getSupportFragmentManager(), "SalvarDescartarFragment");
    }

    private void deletarRecursivamente(File arquivoOuPasta) {
        // https://stackoverflow.com/a/6425744/1657502
        if (arquivoOuPasta.isDirectory())
            for (File conteudo : arquivoOuPasta.listFiles())
                deletarRecursivamente(conteudo);

        arquivoOuPasta.delete();
    }

    @Override
    public void descartarAlteracoes(int requestCode) {
        S.l(this, "descartarAlteracoes(requestCode: " + requestCode + ")");
        switch (requestCode) {
            case REQUEST_ABRIR_ARQUIVO:
                abrirArquivo();
                break;
            case REQUEST_ABRIR_EXEMPLO:
                abrirExemplo();
                break;
            case REQUEST_NOVO:
                novo();
                break;
        }
    }

    private void desfazer() {
        S.l(this, "desfazer()");
        getEditor().desfazer();
    }

    private void diminuirFonte() {
        S.l(this, "diminuirFonte()");
        getEditor().diminuirFonte();
    }

    private void executar() {
        viewPager.setCurrentItem(TabsAdapter.TAB_CONSOLE);
        tabsAdapter.getConsoleFragment().executar(getCodigoFonte());
    }

    private String getCodigoFonte() {
        String codigoFonte = getEditor().getCodigoFonte();
        return codigoFonte;
    }

    private Editor getEditor() {
        return tabsAdapter.getEditorFragment().getEditor();
    }

    private void naoImplementadoAinda() {
        Snackbar.make(viewPager, "Não implementado ainda...", Snackbar.LENGTH_SHORT).show();
    }

    private void novo() {
        S.l(this, "novo()");
        arquivoModificado = false;
        caminhoDoArquivo = null;
        nomeDoArquivo = ARQUIVO_SEM_NOME;
        setCodigoFonte("");
        getEditor().limparHistoricoDesfazerRefazer();
    }

    private void refazer() {
        S.l(this, "refazer()");
        getEditor().refazer();
    }

    private void salvar() {
        S.l(this, "salvar()");
        // Se o arquivo é novo, age como salvar como
        salvar(arquivoNovo());
    }

    private void salvar(boolean selecionarArquivo) {
        S.l(this, "salvar(selecionarArquivo: " + selecionarArquivo + ")");
        salvar(selecionarArquivo, REQUEST_SALVAR_COMO);
    }

    private void salvar(boolean selecionarArquivo, int requestCode) {
        S.l(this, "salvar(selecionarArquivo: " + selecionarArquivo + ", requestCode: " + requestCode + ")");
        if (temPermissaoParaEscreverArquivos()) {
            if (selecionarArquivo) {
                // https://developer.android.com/guide/topics/providers/document-provider
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TITLE, NOME_DE_ARQUIVO_PADRAO);
                startActivityForResult(intent, requestCode);
            } else {
                salvar(caminhoDoArquivo);
            }
        } else {
            solicitarPermissaoParaEscreverArquivos(selecionarArquivo ? REQUEST_SALVAR_COMO : REQUEST_SALVAR);
        }
    }

    private void salvar(Uri caminhoDoArquivo) {
        S.l(this, "salvar(caminhoDoArquivo: " + caminhoDoArquivo + ")");
        // https://developer.android.com/guide/topics/providers/document-provider
        Cursor cursor = getContentResolver().query(caminhoDoArquivo, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            try {
                ParcelFileDescriptor pfd = this.getContentResolver().openFileDescriptor(caminhoDoArquivo, "w");
                FileOutputStream fos = new FileOutputStream(pfd.getFileDescriptor());
                String codigoFonte = getCodigoFonte();
                fos.write(codigoFonte.getBytes());
                fos.close();
                pfd.close();

                arquivoModificado = false;
                this.caminhoDoArquivo = caminhoDoArquivo;
                String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                nomeDoArquivo = displayName;
                getEditor().limparHistoricoDesfazerRefazer();
            } catch (FileNotFoundException excecao) {
                Snackbar.make(viewPager, "Erro: arquivo não encontrado", Snackbar.LENGTH_SHORT).show();
                return;
            } catch (IOException excecao) {
                Snackbar.make(viewPager, "Erro de entrada/saída", Snackbar.LENGTH_SHORT).show();
                return;
            } finally {
                cursor.close();
            }
        }
    }

    @Override
    public void salvarAlteracoes(int requestCode) {
        S.l(this, "salvarAlteracoes(requestCode: " + requestCode + ")");
        switch (requestCode) {
            case REQUEST_ABRIR_ARQUIVO:
                if (arquivoNovo()) {
                    salvar(true, REQUEST_SALVAR_COMO_E_ABRIR_ARQUIVO);
                } else {
                    salvar(false);
                    abrirArquivo();
                }
                break;
            case REQUEST_ABRIR_EXEMPLO:
                if (arquivoNovo()) {
                    salvar(true, REQUEST_SALVAR_COMO_E_ABRIR_EXEMPLO);
                } else {
                    salvar(false);
                    abrirExemplo();
                }
                break;
            case REQUEST_NOVO:
                if (arquivoNovo()) {
                    salvar(true, REQUEST_SALVAR_COMO_E_NOVO);
                } else {
                    salvar(false);
                    novo();
                }
                break;
        }
    }

    private void salvarComo() {
        salvar(true);
    }

    private void setCodigoFonte(String codigoFonte) {
        S.l(this, "setCodigoFonte(codigoFonte)");
        getEditor().setCodigoFonte(codigoFonte);
    }

    private void solicitarPermissaoParaEscreverArquivos(int requestCode) {
        // https://developer.android.com/training/permissions/requesting
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
    }

    private boolean temPermissaoParaEscreverArquivos() {
        // https://developer.android.com/training/permissions/requesting
        boolean temPermissao = (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        return temPermissao;
    }
}
