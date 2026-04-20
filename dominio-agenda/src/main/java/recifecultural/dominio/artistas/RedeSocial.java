package recifecultural.dominio.artistas;

public final class RedeSocial {

    private final TipoRedeSocial tipo;
    private final String url;

    public RedeSocial(TipoRedeSocial tipo, String url) {
        if (tipo == null) throw new IllegalArgumentException("Tipo de rede social é obrigatório.");
        if (url == null || url.isBlank()) throw new IllegalArgumentException("URL da rede social é obrigatória.");
        this.tipo = tipo;
        this.url = url;
    }

    public TipoRedeSocial getTipo() {
        return tipo;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object outro) {
        if (this == outro) return true;
        if (!(outro instanceof RedeSocial outraRede)) return false;
        return tipo == outraRede.tipo && url.equals(outraRede.url);
    }

    @Override
    public int hashCode() {
        return 31 * tipo.hashCode() + url.hashCode();
    }
}
