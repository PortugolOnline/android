package br.com.vinyanalista.portugol.android;

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
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import br.com.vinyanalista.portugol.android.adapter.TabsAdapter;

public class AtividadePrincipal extends AtividadeBase implements NavigationView.OnNavigationItemSelectedListener {
    private static final String ARQUIVO_SEM_NOME = "Sem nome";
    private static final String NOME_DE_ARQUIVO_PADRAO = "algoritmo.por";
    static final int REQUEST_ABRIR_ARQUIVO = 1;
    static final int REQUEST_ABRIR_EXEMPLO = 2;
    static final int REQUEST_SALVAR = 3;
    static final int REQUEST_SALVAR_COMO = 4;

    private DrawerLayout drawerLayout;
    private Menu menuToolbarPrincipal;
    private TextView tvNomeDoArquivo;
    private TabsAdapter tabsAdapter;
    private ViewPager viewPager;

    private Uri caminhoDoArquivo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atividade_principal);

        configurarToolbar();

        // Navigation Drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.nav_drawer_abrir, R.string.nav_drawer_fechar);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // https://stackoverflow.com/a/45520466/1657502
        tvNomeDoArquivo = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_nome_do_arquivo);

        // ViewPager e TabLayout
        tabsAdapter = new TabsAdapter(getSupportFragmentManager(), this);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(tabsAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                    String exemplo = data.getStringExtra(AtividadeAbrirExemplo.EXTRA_EXEMPLO_SELECIONADO);
                    abrirExemplo(exemplo);
                }
                break;
            case REQUEST_SALVAR_COMO:
                // https://developer.android.com/guide/topics/providers/document-provider
                if ((resultCode == RESULT_OK) && (data != null) && (data.getData() != null)) {
                    Uri caminhoDoArquivo = data.getData();
                    salvar(caminhoDoArquivo);
                }
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (drawerLayout != null) {
            switch (item.getItemId()) {
                case R.id.nav_drawer_abrir_arquivo:
                    abrirArquivo();
                    break;
                case R.id.nav_drawer_abrir_exemplo:
                    abrirExemplo();
                case R.id.nav_drawer_compartilhar:
                    compartilhar();
                    break;
                case R.id.nav_drawer_novo:
                    novo();
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
    protected void onStart() {
        super.onStart();
        if (caminhoDoArquivo == null) {
            novo();
        }
    }

    private void abrirArquivo() {
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
                setCodigoFonte(codigoFonte);
                limparHistoricoDesfazerRefazer();
                this.caminhoDoArquivo = caminhoDoArquivo;
                String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                atualizarNomeDoArquivo(displayName);
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
        Intent intent = new Intent(getBaseContext(), AtividadeAbrirExemplo.class);
        startActivityForResult(intent, REQUEST_ABRIR_EXEMPLO);
    }

    private void abrirExemplo(String exemplo) {
        setCodigoFonte(exemplo);
        limparHistoricoDesfazerRefazer();
    }

    private void atualizarNomeDoArquivo(String nomeDoArquivo) {
        tvNomeDoArquivo.setText(nomeDoArquivo);
    }

    private void aumentarFonte() {
        tabsAdapter.getEditorFragment().aumentarFonte();
    }

    private void compartilhar() {
        naoImplementadoAinda();
    }

    private void desfazer() {
        tabsAdapter.getEditorFragment().desfazer();
    }

    private void diminuirFonte() {
        tabsAdapter.getEditorFragment().diminuirFonte();
    }

    private void executar() {
        viewPager.setCurrentItem(TabsAdapter.TAB_CONSOLE);
        tabsAdapter.getConsoleFragment().executar(getCodigoFonte());
    }

    private String getCodigoFonte() {
        String codigoFonte = tabsAdapter.getEditorFragment().getCodigoFonte();
        return codigoFonte;
    }

    public void habilitarDesfazerRefazer(boolean habilitarDesfazer, boolean habilitarRefazer) {
        menuToolbarPrincipal.findItem(R.id.action_desfazer).setEnabled(habilitarDesfazer);
        menuToolbarPrincipal.findItem(R.id.action_refazer).setEnabled(habilitarRefazer);
    }

    private void limparHistoricoDesfazerRefazer() {
        tabsAdapter.getEditorFragment().limparHistoricoDesfazerRefazer();
    }

    private void naoImplementadoAinda() {
        Snackbar.make(viewPager, "Não implementado ainda...", Snackbar.LENGTH_SHORT).show();
    }

    private void novo() {
        if (caminhoDoArquivo != null) {
            setCodigoFonte("");
            limparHistoricoDesfazerRefazer();
        }
        caminhoDoArquivo = null;
        atualizarNomeDoArquivo(ARQUIVO_SEM_NOME);
    }

    private void refazer() {
        tabsAdapter.getEditorFragment().refazer();
    }

    private void salvar() {
        // Se o arquivo é novo, age como salvar como
        salvar(caminhoDoArquivo == null);
    }

    private void salvar(boolean selecionarArquivo) {
        if (temPermissaoParaEscreverArquivos()) {
            if (selecionarArquivo) {
                // https://developer.android.com/guide/topics/providers/document-provider
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TITLE, NOME_DE_ARQUIVO_PADRAO);
                startActivityForResult(intent, REQUEST_SALVAR_COMO);
            } else {
                salvar(caminhoDoArquivo);
            }
        } else {
            solicitarPermissaoParaEscreverArquivos(selecionarArquivo ? REQUEST_SALVAR_COMO : REQUEST_SALVAR);
        }
    }

    private void salvar(Uri caminhoDoArquivo) {
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

                this.caminhoDoArquivo = caminhoDoArquivo;
                String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                atualizarNomeDoArquivo(displayName);
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

    private void salvarComo() {
        salvar(true);
    }

    private void setCodigoFonte(String codigoFonte) {
        tabsAdapter.getEditorFragment().setCodigoFonte(codigoFonte);
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
