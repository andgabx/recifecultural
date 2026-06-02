package recifecultural.infraestrutura.persistencia.jpa;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import recifecultural.aplicacao.patrocinio.PatrocinioRepositorioAplicacao;
import recifecultural.aplicacao.patrocinio.PatrocinioResumo;
import recifecultural.dominio.patrocinio.EventoId;
import recifecultural.dominio.patrocinio.IPatrocinioRepositorio;
import recifecultural.dominio.patrocinio.Patrocinio;
import recifecultural.dominio.patrocinio.PatrocinioId;
import recifecultural.dominio.patrocinio.StatusPatrocinio;
import recifecultural.dominio.patrocinio.TipoPatrocinio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "patrocinio")
class PatrocinioJpa {
    @Id
    UUID id;
    UUID eventoId;
    String patrocinadorNome;
    String categoriaPatrocinio;
    String tipo;
    String modalidade;
    BigDecimal valorContribuicao;
    LocalDateTime dataEvento;
    @Enumerated(EnumType.STRING)
    StatusPatrocinio status;
    BigDecimal valorReembolsado;
    BigDecimal multaAplicada;
}

interface PatrocinioJpaRepository extends JpaRepository<PatrocinioJpa, UUID> {
    Optional<PatrocinioJpa> findByEventoIdAndTipo(UUID eventoId, String tipo);
    Optional<PatrocinioJpa> findByEventoIdAndCategoriaPatrocinio(UUID eventoId, String categoria);
    List<PatrocinioJpa> findByEventoId(UUID eventoId);
}

@Repository
class PatrocinioRepositorioImpl implements IPatrocinioRepositorio, PatrocinioRepositorioAplicacao {

    private final PatrocinioJpaRepository jpa;
    private final JpaMapeador mapeador;

    PatrocinioRepositorioImpl(PatrocinioJpaRepository jpa, JpaMapeador mapeador) {
        this.jpa = jpa;
        this.mapeador = mapeador;
    }

    @Override
    public void salvar(Patrocinio p) {
        jpa.save(mapeador.map(p, PatrocinioJpa.class));
    }

    @Override
    public Patrocinio buscarPorId(PatrocinioId id) {
        return jpa.findById(id.getValor())
                .map(p -> mapeador.map(p, Patrocinio.class))
                .orElse(null);
    }

    @Override
    public List<Patrocinio> buscarPorEvento(EventoId eventoId) {
        return jpa.findByEventoId(eventoId.getValor())
                .stream().map(p -> mapeador.map(p, Patrocinio.class)).toList();
    }

    @Override
    public Optional<Patrocinio> buscarMasterPorEvento(EventoId eventoId) {
        return jpa.findByEventoIdAndTipo(eventoId.getValor(), TipoPatrocinio.MASTER.name())
                .map(p -> mapeador.map(p, Patrocinio.class));
    }

    @Override
    public Optional<Patrocinio> buscarPorEventoECategoria(EventoId eventoId, String categoria) {
        return jpa.findByEventoIdAndCategoriaPatrocinio(eventoId.getValor(), categoria)
                .map(p -> mapeador.map(p, Patrocinio.class));
    }

    @Override
    public void atualizar(Patrocinio p) {
        jpa.save(mapeador.map(p, PatrocinioJpa.class));
    }

    @Override
    public List<PatrocinioResumo> pesquisarPorEvento(UUID eventoId) {
        return jpa.findByEventoId(eventoId).stream()
                .<PatrocinioResumo>map(p -> new PatrocinioResumoJpa(
                        p.id.toString(), p.eventoId.toString(),
                        p.patrocinadorNome, p.categoriaPatrocinio,
                        p.tipo, p.modalidade,
                        p.valorContribuicao != null ? p.valorContribuicao.toPlainString() : null,
                        p.dataEvento != null ? p.dataEvento.toString() : null,
                        p.status != null ? p.status.name() : null,
                        p.valorReembolsado != null ? p.valorReembolsado.toPlainString() : null,
                        p.multaAplicada != null ? p.multaAplicada.toPlainString() : null))
                .toList();
    }

    record PatrocinioResumoJpa(String id, String eventoId, String patrocinadorNome,
                                String categoriaPatrocinio, String tipo, String modalidade,
                                String valorContribuicao, String dataEvento, String status,
                                String valorReembolsado, String multaAplicada)
            implements PatrocinioResumo {
        public String getId() { return id; }
        public String getEventoId() { return eventoId; }
        public String getPatrocinadorNome() { return patrocinadorNome; }
        public String getCategoriaPatrocinio() { return categoriaPatrocinio; }
        public String getTipo() { return tipo; }
        public String getModalidade() { return modalidade; }
        public String getValorContribuicao() { return valorContribuicao; }
        public String getDataEvento() { return dataEvento; }
        public String getStatus() { return status; }
        public String getValorReembolsado() { return valorReembolsado; }
        public String getMultaAplicada() { return multaAplicada; }
    }
}
