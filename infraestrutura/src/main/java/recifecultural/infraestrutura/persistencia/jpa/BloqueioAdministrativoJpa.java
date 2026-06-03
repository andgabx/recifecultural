package recifecultural.infraestrutura.persistencia.jpa;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import recifecultural.aplicacao.agenda.bloqueioadministrativo.BloqueioAdministrativoRepositorioAplicacao;
import recifecultural.aplicacao.agenda.bloqueioadministrativo.BloqueioAdministrativoResumo;
import recifecultural.dominio.agenda.bloqueioadministrativo.BloqueioAdministrativo;
import recifecultural.dominio.agenda.bloqueioadministrativo.BloqueioAdministrativoId;
import recifecultural.dominio.agenda.bloqueioadministrativo.IBloqueioAdministrativoRepositorio;
import recifecultural.dominio.espaco.espaco.EspacoId;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "bloqueio_administrativo")
class BloqueioAdministrativoJpa {
    @Id
    UUID id;
    UUID espacoId;
    LocalDate dataInicio;
    LocalDate dataFim;
    String justificativa;
    boolean ativo;

    @Column(name = "eventos_cancelados", columnDefinition = "text")
    String eventosCancelados; // comma-separated UUIDs, nullable
}

interface BloqueioAdministrativoJpaRepository extends JpaRepository<BloqueioAdministrativoJpa, UUID> {
    List<BloqueioAdministrativoJpa> findByEspacoId(UUID espacoId);

    @Query("SELECT b FROM BloqueioAdministrativoJpa b WHERE b.ativo = true")
    List<BloqueioAdministrativoJpa> findAtivos();
}

@Repository
class BloqueioAdministrativoRepositorioImpl
        implements IBloqueioAdministrativoRepositorio, BloqueioAdministrativoRepositorioAplicacao {

    private final BloqueioAdministrativoJpaRepository jpa;
    private final JpaMapeador mapeador;

    BloqueioAdministrativoRepositorioImpl(BloqueioAdministrativoJpaRepository jpa, JpaMapeador mapeador) {
        this.jpa = jpa;
        this.mapeador = mapeador;
    }

    @Override
    public void salvar(BloqueioAdministrativo bloqueio) {
        jpa.save(mapeador.map(bloqueio, BloqueioAdministrativoJpa.class));
    }

    @Override
    public BloqueioAdministrativo obter(BloqueioAdministrativoId id) {
        return jpa.findById(id.valor()).map(b -> mapeador.map(b, BloqueioAdministrativo.class)).orElse(null);
    }

    @Override
    public void atualizar(BloqueioAdministrativo bloqueio) {
        jpa.save(mapeador.map(bloqueio, BloqueioAdministrativoJpa.class));
    }

    @Override
    public void deletar(BloqueioAdministrativoId id) {
        jpa.deleteById(id.valor());
    }

    @Override
    public List<BloqueioAdministrativo> obterTodos() {
        return jpa.findAll().stream().map(b -> mapeador.map(b, BloqueioAdministrativo.class)).toList();
    }

    @Override
    public List<BloqueioAdministrativo> buscarPorEspaco(EspacoId espacoId) {
        return jpa.findByEspacoId(espacoId.valor())
                .stream().map(b -> mapeador.map(b, BloqueioAdministrativo.class)).toList();
    }

    private static List<String> parseEventos(String raw) {
        if (raw == null || raw.isBlank()) return List.of();
        return Arrays.asList(raw.split(","));
    }

    @Override
    public List<BloqueioAdministrativoResumo> pesquisarAtivos() {
        return jpa.findAtivos().stream()
                .<BloqueioAdministrativoResumo>map(b -> new BloqueioAdministrativoResumoJpa(
                        b.id.toString(),
                        b.espacoId != null ? b.espacoId.toString() : null,
                        b.dataInicio != null ? b.dataInicio.toString() : null,
                        b.dataFim != null ? b.dataFim.toString() : null,
                        b.justificativa,
                        b.ativo,
                        parseEventos(b.eventosCancelados)))
                .toList();
    }

    @Override
    public List<BloqueioAdministrativoResumo> pesquisarTodos() {
        return jpa.findAll().stream()
                .<BloqueioAdministrativoResumo>map(b -> new BloqueioAdministrativoResumoJpa(
                        b.id.toString(),
                        b.espacoId != null ? b.espacoId.toString() : null,
                        b.dataInicio != null ? b.dataInicio.toString() : null,
                        b.dataFim != null ? b.dataFim.toString() : null,
                        b.justificativa,
                        b.ativo,
                        parseEventos(b.eventosCancelados)))
                .toList();
    }

    record BloqueioAdministrativoResumoJpa(
            String id, String espacoId, String dataInicio, String dataFim,
            String justificativa, boolean ativo, List<String> eventosCancelados)
            implements BloqueioAdministrativoResumo {
        public String getId() { return id; }
        public String getEspacoId() { return espacoId; }
        public String getDataInicio() { return dataInicio; }
        public String getDataFim() { return dataFim; }
        public String getJustificativa() { return justificativa; }
        public boolean isAtivo() { return ativo; }
        public List<String> getEventosCancelados() { return eventosCancelados; }
    }
}
