package br.com.vinyanalista.portugol.android.editor;

public class ConfiguracoesDaPesquisa {
    private String localizar;
    private boolean diferenciarMaiusculas;
    private String substituirPor;

    public ConfiguracoesDaPesquisa() {
        this("", false, null);
    }

    public ConfiguracoesDaPesquisa(String localizar, boolean diferenciarMaiusculas) {
        this(localizar, diferenciarMaiusculas, null);
    }

    public ConfiguracoesDaPesquisa(String localizar, boolean diferenciarMaiusculas, String substituirPor) {
        this.localizar = localizar;
        this.diferenciarMaiusculas = diferenciarMaiusculas;
        this.substituirPor = substituirPor;
    }

    public String getLocalizar() {
        return localizar;
    }

    public boolean isDiferenciarMaiusculas() {
        return diferenciarMaiusculas;
    }

    public String getSubstituirPor() {
        return substituirPor;
    }
}
