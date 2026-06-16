package recifecultural.infraestrutura.persistencia.agenda.evento;

import org.springframework.stereotype.Repository;

import recifecultural.aplicacao.agenda.evento.ApresentacaoIdHelper;
import recifecultural.aplicacao.agenda.evento.ApresentacaoResumo;
import recifecultural.aplicacao.agenda.evento.EventoRepositorioAplicacao;
import recifecultural.aplicacao.agenda.evento.EventoResumo;
import recifecultural.aplicacao.agenda.evento.EventoResumoExpandido;
import recifecultural.aplicacao.agenda.evento.RiderItemResposta;
import recifecultural.dominio.agenda.evento.Evento;
import recifecultural.dominio.agenda.evento.IEventoRepositorio;
import recifecultural.infraestrutura.persistencia.jpa.JpaMapeador;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public class EventoRepositorioImpl implements IEventoRepositorio, EventoRepositorioAplicacao {

    private final EventoJpaRepository jpa;
    private final JpaMapeador mapeador;

    public EventoRepositorioImpl(EventoJpaRepository jpa, JpaMapeador mapeador) {
        this.jpa = jpa;
        this.mapeador = mapeador;
    }

    @Override
    public void salvar(Evento evento) {
        jpa.save(mapeador.map(evento, EventoJpa.class));
    }

    @Override
    public void atualizar(Evento evento) {
        jpa.save(mapeador.map(evento, EventoJpa.class));
    }

    @Override
    public void deletar(UUID id) {
        jpa.deleteById(id);
    }

    @Override
    public Optional<Evento> obter(UUID id) {
        return jpa.findById(id).map(e -> mapeador.map(e, Evento.class));
    }

    @Override
    public List<Evento> obterTodos() {
        return jpa.findAll().stream().map(e -> mapeador.map(e, Evento.class)).toList();
    }

    @Override
    public List<Evento> obterPorLocalEIntervalo(UUID localId, LocalDateTime inicio, LocalDateTime fim) {
        return jpa.findByLocalEIntervalo(localId, inicio, fim)
                .stream().map(e -> mapeador.map(e, Evento.class)).toList();
    }

    @Override
    public List<Evento> obterReprovacoesPorPromotor(UUID promotorId) {
        return jpa.findReprovacoesPorPromotor(promotorId)
                .stream().map(e -> mapeador.map(e, Evento.class)).toList();
    }

    @Override
    public List<Evento> obterEventosFinalizadosPorPromotor(UUID promotorId) {
        return jpa.findFinalizadosPorPromotor(promotorId)
                .stream().map(e -> mapeador.map(e, Evento.class)).toList();
    }

    @Override
    public List<Evento> obterEventosAprovadosPorPromotor(UUID promotorId) {
        return jpa.findAprovadosPorPromotor(promotorId)
                .stream().map(e -> mapeador.map(e, Evento.class)).toList();
    }

    @Override
    public List<EventoResumo> pesquisarResumos() {
        return jpa.findAll().stream()
                .<EventoResumo>map(e -> new EventoResumoJpa(
                        e.id.toString(), e.titulo, e.categoria,
                        e.status != null ? e.status.name() : null,
                        e.periodoInicio != null ? e.periodoInicio.toString() : null,
                        e.periodoFim != null ? e.periodoFim.toString() : null))
                .toList();
    }

    @Override
    public List<EventoResumo> listarPorPromotor(UUID promotorId) {
        return jpa.findByPromotorId(promotorId).stream()
                .<EventoResumo>map(e -> new EventoResumoJpa(
                        e.id.toString(), e.titulo, e.categoria,
                        e.status != null ? e.status.name() : null,
                        e.periodoInicio != null ? e.periodoInicio.toString() : null,
                        e.periodoFim != null ? e.periodoFim.toString() : null))
                .toList();
    }

    @Override
    public EventoResumoExpandido buscarResumoExpandido(UUID id) {
        return jpa.findById(id)
                .map(e -> {
                    UUID eventoId = e.id;
                    List<ApresentacaoResumo> apresentacoes = e.datasApresentacao == null
                            ? List.of()
                            : e.datasApresentacao.stream()
                                .map(d -> (ApresentacaoResumo) new ApresentacaoResumoJpa(
                                        ApresentacaoIdHelper.gerar(eventoId, d.toString()).toString(),
                                        eventoId.toString(),
                                        d.toString()))
                                .toList();
                    List<RiderItemResposta> riderItems = e.riderItems == null
                            ? List.of()
                            : e.riderItems.stream()
                                .map(item -> (RiderItemResposta) new RiderItemRespostaJpa(
                                        item.getNomeEquipamento(), item.getQuantidade()))
                                .toList();
                    return (EventoResumoExpandido) new EventoResumoExpandidoJpa(
                            e.id.toString(), e.titulo, e.categoria,
                            e.status != null ? e.status.name() : null,
                            e.descricaoCurta,
                            e.descricaoLonga,
                            e.promotorId != null ? e.promotorId.toString() : null,
                            e.localId != null ? e.localId.toString() : null,
                            e.periodoInicio != null ? e.periodoInicio.toString() : null,
                            e.periodoFim != null ? e.periodoFim.toString() : null,
                            e.precoInteira != null ? e.precoInteira.toPlainString() : null,
                            e.precoMeia != null ? e.precoMeia.toPlainString() : null,
                            e.precoSocial != null ? e.precoSocial.toPlainString() : null,
                            e.artistas != null ? e.artistas.stream().map(UUID::toString).toList() : List.of(),
                            apresentacoes,
                            riderItems);
                })
                .orElse(null);
    }

    record EventoResumoJpa(String id, String titulo, String categoria, String status,
                           String periodoInicio, String periodoFim)
            implements EventoResumo {
        public String getId() { return id; }
        public String getTitulo() { return titulo; }
        public String getCategoria() { return categoria; }
        public String getStatus() { return status; }
        public String getPeriodoInicio() { return periodoInicio; }
        public String getPeriodoFim() { return periodoFim; }
    }

    record EventoResumoExpandidoJpa(
            String id, String titulo, String categoria, String status,
            String descricaoCurta, String descricaoLonga,
            String promotorId, String localId,
            String periodoInicio, String periodoFim,
            String precoInteira, String precoMeia, String precoSocial,
            List<String> artistas,
            List<ApresentacaoResumo> apresentacoes,
            List<RiderItemResposta> riderItems)
            implements EventoResumoExpandido {
        public String getId() { return id; }
        public String getTitulo() { return titulo; }
        public String getCategoria() { return categoria; }
        public String getStatus() { return status; }
        public String getDescricaoCurta() { return descricaoCurta; }
        public String getDescricaoLonga() { return descricaoLonga; }
        public String getPromotorId() { return promotorId; }
        public String getLocalId() { return localId; }
        public String getPeriodoInicio() { return periodoInicio; }
        public String getPeriodoFim() { return periodoFim; }
        public String getPrecoInteira() { return precoInteira; }
        public String getPrecoMeia() { return precoMeia; }
        public String getPrecoSocial() { return precoSocial; }
        public List<String> getArtistas() { return artistas; }
        public List<ApresentacaoResumo> getApresentacoes() { return apresentacoes; }
        public List<RiderItemResposta> getRiderItems() { return riderItems; }
    }

    record RiderItemRespostaJpa(String nomeEquipamento, int quantidade)
            implements RiderItemResposta {
        public String getNomeEquipamento() { return nomeEquipamento; }
        public int getQuantidade() { return quantidade; }
    }

    record ApresentacaoResumoJpa(String id, String eventoId, String dataHora)
            implements ApresentacaoResumo {
        public String getId() { return id; }
        public String getEventoId() { return eventoId; }
        public String getDataHora() { return dataHora; }
    }
}
