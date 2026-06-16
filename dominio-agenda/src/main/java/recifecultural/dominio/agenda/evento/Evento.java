package recifecultural.dominio.agenda.evento;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import recifecultural.dominio.agenda.equipamento.RiderItem;

public class Evento {
    private final UUID id;
    private final UUID promotorId;
    private UUID localId;

    private String titulo;
    private String descricaoCurta;
    private String descricaoLonga;

    private Periodo periodo;
    private final List<LocalDateTime> datasApresentacao;
    private final List<UUID> artistas;
    private String categoria;
    private URI enderecoIngresso;

    private Preco preco;

    private List<RiderItem> riderItems = new ArrayList<>();

    private StatusEvento status;
    private FeedbackReprovacao feedbackReprovacao;
    private LocalDateTime dataReprovacao;
    private LocalDateTime dataAprovacao;
    private boolean requerRevisaoAdicional = false;
    private String motivoCancelamento;

    public UUID getId() { return id; }

    public UUID getPromotorId() { return promotorId; }

    public UUID getLocalId() {
        return localId;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricaoCurta() {
        return descricaoCurta;
    }

    public String getDescricaoLonga() {
        return descricaoLonga;
    }

    public Periodo getPeriodo() {
        return periodo;
    }

    public URI getEnderecoIngresso() {
        return enderecoIngresso;
    }

    public Preco getPreco() {
        return preco;
    }

    public StatusEvento getStatus() {
        return status;
    }

    public FeedbackReprovacao getFeedbackReprovacao() {
        return feedbackReprovacao;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public LocalDateTime getDataReprovacao() {
        return dataReprovacao;
    }

    public LocalDateTime getDataAprovacao() {
        return dataAprovacao;
    }

    public boolean isRequerRevisaoAdicional() {
        return requerRevisaoAdicional;
    }

    public void marcarComoRequerRevisaoAdicional() {
        this.requerRevisaoAdicional = true;
    }

    public List<LocalDateTime> getDatasApresentacao() {
        return Collections.unmodifiableList(datasApresentacao);
    }

    public List<UUID> getArtistas() {
        return Collections.unmodifiableList(artistas);
    }

    public String getCategoria() {
        return categoria;
    }

    private void setTitulo(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("Título é obrigatório.");
        }
        this.titulo = titulo;
    }

    public Evento(
            UUID id,
            UUID promotorId,
            UUID localId,
            String titulo,
            String descricaoCurta,
            String descricaoLonga,
            Periodo periodo,
            URI enderecoIngresso,
            Preco preco
    ) {
        this.id = id;
        this.promotorId = promotorId;
        this.localId = localId;

        setTitulo(titulo);
        this.descricaoCurta = descricaoCurta;
        this.descricaoLonga = descricaoLonga;

        this.periodo = periodo;
        this.datasApresentacao = new ArrayList<>();
        this.artistas = new ArrayList<>();
        this.enderecoIngresso = enderecoIngresso;
        this.preco = preco;

        this.status = StatusEvento.RASCUNHO;
    }

    /** Reconstruction constructor — preserva todo o estado ao recarregar do banco. */
    public Evento(
            UUID id,
            UUID promotorId,
            UUID localId,
            String titulo,
            String descricaoCurta,
            String descricaoLonga,
            Periodo periodo,
            Preco preco,
            String categoria,
            StatusEvento status,
            List<LocalDateTime> datasApresentacao,
            List<UUID> artistas,
            LocalDateTime dataAprovacao,
            LocalDateTime dataReprovacao,
            boolean requerRevisaoAdicional,
            String motivoCancelamento
    ) {
        this.id = id;
        this.promotorId = promotorId;
        this.localId = localId;
        setTitulo(titulo);
        this.descricaoCurta = descricaoCurta;
        this.descricaoLonga = descricaoLonga;
        this.periodo = periodo;
        this.preco = preco;
        this.categoria = categoria;
        this.status = status != null ? status : StatusEvento.RASCUNHO;
        this.datasApresentacao = datasApresentacao != null ? new ArrayList<>(datasApresentacao) : new ArrayList<>();
        this.artistas = artistas != null ? new ArrayList<>(artistas) : new ArrayList<>();
        this.dataAprovacao = dataAprovacao;
        this.dataReprovacao = dataReprovacao;
        this.requerRevisaoAdicional = requerRevisaoAdicional;
        this.motivoCancelamento = motivoCancelamento;
    }

    public void adicionarArtista(UUID artistaId) {
        if (artistaId == null) throw new IllegalArgumentException("ID do artista é obrigatório.");
        this.artistas.add(artistaId);
    }

    public void definirCategoria(String categoria) {
        if (categoria == null || categoria.isBlank())
            throw new IllegalArgumentException("Categoria é obrigatória.");
        this.categoria = categoria;
    }

    /**
     * Edita as informações do evento. Permitido apenas enquanto estiver em RASCUNHO.
     * Substitui (não acumula) as listas de artistas e datas de apresentação.
     */
    public void editarInformacoes(String novoTitulo,
                                   String novaDescricaoCurta,
                                   String novaDescricaoLonga,
                                   Periodo novoPeriodo,
                                   Preco novoPreco,
                                   String novaCategoria,
                                   UUID novoLocalId,
                                   List<UUID> novosArtistas,
                                   List<LocalDateTime> novasDatasApresentacao) {
        if (this.status != StatusEvento.RASCUNHO)
            throw new IllegalStateException("Apenas eventos em RASCUNHO podem ser editados.");
        if (novoPeriodo == null)
            throw new IllegalArgumentException("Período é obrigatório.");

        setTitulo(novoTitulo);
        this.descricaoCurta = novaDescricaoCurta;
        this.descricaoLonga = novaDescricaoLonga;
        this.periodo = novoPeriodo;
        this.preco = novoPreco;
        if (novaCategoria != null && !novaCategoria.isBlank()) this.categoria = novaCategoria;
        if (novoLocalId != null) this.localId = novoLocalId;

        this.artistas.clear();
        if (novosArtistas != null) {
            for (UUID a : novosArtistas) {
                if (a == null) throw new IllegalArgumentException("ID do artista é obrigatório.");
                this.artistas.add(a);
            }
        }

        this.datasApresentacao.clear();
        if (novasDatasApresentacao != null) {
            for (LocalDateTime d : novasDatasApresentacao) {
                if (d == null) throw new IllegalArgumentException("Data de apresentação é obrigatória.");
                this.datasApresentacao.add(d);
            }
        }
    }

    public void submeterParaAnalise() {
        if (datasApresentacao.isEmpty())
            throw new IllegalStateException("O evento deve ter pelo menos uma data de apresentação para ser submetido à análise.");
        if (artistas.isEmpty())
            throw new IllegalStateException("O evento deve ter pelo menos um artista incluído para ser submetido à análise.");
        if (categoria == null || categoria.isBlank())
            throw new IllegalStateException("O evento deve ter uma categoria definida para ser submetido à análise.");
        if (localId == null)
            throw new IllegalStateException("O evento deve ter um espaço definido para ser submetido à análise.");
        this.status = StatusEvento.EM_ANALISE;
    }

    public AprovadoEvento aprovar() {
        exigirStatusEmAnalise("aprovar");
        this.status = StatusEvento.APROVADO;
        this.dataAprovacao = LocalDateTime.now();
        return new AprovadoEvento(this);
    }

    public ReprovadoEvento reprovar(FeedbackReprovacao feedback) {
        exigirStatusEmAnalise("reprovar");
        this.feedbackReprovacao = feedback;
        this.dataReprovacao = LocalDateTime.now();
        this.status = StatusEvento.REPROVADO;
        return new ReprovadoEvento(this);
    }

    public CanceladoEvento cancelar(String motivo) {
        if (this.status == StatusEvento.REALIZADO) {
            throw new IllegalStateException("Não é possível cancelar um evento que já foi realizado.");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("Motivo do cancelamento é obrigatório.");
        }
        this.status = StatusEvento.CANCELADO;
        this.motivoCancelamento = motivo;
        return new CanceladoEvento(this);
    }

    public void restaurar() {
        if (this.status != StatusEvento.CANCELADO)
            throw new IllegalStateException("Só é possível restaurar eventos cancelados.");
        this.status = StatusEvento.APROVADO;
        this.motivoCancelamento = null;
    }

    private void exigirStatusEmAnalise(String acao) {
        if (this.status != StatusEvento.EM_ANALISE)
            throw new IllegalStateException("Não é possível " + acao + " um evento com status " + this.status + ".");
    }

    public void verificarAprovado() {
        if (this.status != StatusEvento.APROVADO) {
            throw new IllegalStateException("O evento deve estar APROVADO para esta operação.");
        }
    }

    public void programarApresentacao(LocalDateTime dataHora) {
        if (dataHora == null) throw new IllegalArgumentException("Data nula.");
        if (!periodo.contemData(dataHora))
            throw new IllegalArgumentException("Apresentação fora do período do evento.");
        this.datasApresentacao.add(dataHora);
    }

    /**
     * Atualiza o preço operacionalmente — usado por patrocínios de subsídio.
     * Não requer status RASCUNHO pois é um ajuste posterior à aprovação.
     */
    public void aplicarSubsidioNoPreco(BigDecimal novoPrecoSocial) {
        if (novoPrecoSocial == null) throw new IllegalArgumentException("Novo preço social é obrigatório.");
        this.preco = new Preco(
                this.preco != null ? this.preco.getInteira() : null,
                this.preco != null ? this.preco.getMeia() : null,
                novoPrecoSocial);
    }

    public void adicionarRiderItem(java.util.UUID equipamentoId, int qtd) {
        this.riderItems.add(new RiderItem(equipamentoId, qtd));
    }

    public void substituirRiderItems(List<RiderItem> novosItens) {
        this.riderItems.clear();
        if (novosItens != null) {
            this.riderItems.addAll(novosItens);
        }
    }

    public List<RiderItem> getRiderItems() {
        return Collections.unmodifiableList(riderItems);
    }

    public static class EventoEvento {
        private final Evento evento;

        private EventoEvento(Evento evento) {
            this.evento = evento;
        }

        public Evento getEvento() {
            return evento;
        }
    }

    public static class AprovadoEvento extends EventoEvento {
        private AprovadoEvento(Evento evento) {
            super(evento);
        }
    }

    public static class ReprovadoEvento extends EventoEvento {
        private ReprovadoEvento(Evento evento) {
            super(evento);
        }
    }

    public static class CanceladoEvento extends EventoEvento {
        private CanceladoEvento(Evento evento) {
            super(evento);
        }
    }
}
