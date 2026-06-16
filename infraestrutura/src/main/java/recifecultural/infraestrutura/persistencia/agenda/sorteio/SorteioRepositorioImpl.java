package recifecultural.infraestrutura.persistencia.agenda.sorteio;

import org.springframework.stereotype.Repository;

import recifecultural.aplicacao.agenda.sorteio.SorteioInscritoResumo;
import recifecultural.aplicacao.agenda.sorteio.SorteioRepositorioAplicacao;
import recifecultural.aplicacao.agenda.sorteio.SorteioResumo;
import recifecultural.dominio.agenda.sorteio.ISorteioRepositorio;
import recifecultural.dominio.agenda.sorteio.Sorteio;
import recifecultural.dominio.agenda.sorteio.StatusSorteio;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class SorteioRepositorioImpl implements ISorteioRepositorio, SorteioRepositorioAplicacao {

    private final SorteioJpaRepository jpa;
    private final SorteioMapeador mapeador;

    public SorteioRepositorioImpl(SorteioJpaRepository jpa, SorteioMapeador mapeador) {
        this.jpa = jpa;
        this.mapeador = mapeador;
    }

    @Override
    public void salvar(Sorteio sorteio) {
        jpa.save(mapeador.toJpa(sorteio));
    }

    @Override
    public Optional<Sorteio> obter(UUID id) {
        return jpa.findById(id).map(mapeador::toDomain);
    }

    @Override
    public void atualizar(Sorteio sorteio) {
        jpa.save(mapeador.toJpa(sorteio));
    }

    @Override
    public void deletar(UUID id) {
        jpa.deleteById(id);
    }

    @Override
    public List<SorteioResumo> pesquisarAbertos() {
        return jpa.findByStatus(StatusSorteio.INSCRICOES_ABERTAS).stream()
                .<SorteioResumo>map(s -> new SorteioResumoJpa(
                        s.id.toString(), s.eventoId.toString(),
                        s.apresentacaoId != null ? s.apresentacaoId.toString() : null,
                        s.vagas,
                        s.status != null ? s.status.name() : null,
                        s.prazoInscricao != null ? s.prazoInscricao.toString() : null,
                        s.dataApresentacao != null ? s.dataApresentacao.toString() : null))
                .toList();
    }

    @Override
    public List<SorteioResumo> pesquisarPorEvento(UUID eventoId) {
        return jpa.findByEventoId(eventoId).stream()
                .<SorteioResumo>map(s -> new SorteioResumoJpa(
                        s.id.toString(), s.eventoId.toString(),
                        s.apresentacaoId != null ? s.apresentacaoId.toString() : null,
                        s.vagas,
                        s.status != null ? s.status.name() : null,
                        s.prazoInscricao != null ? s.prazoInscricao.toString() : null,
                        s.dataApresentacao != null ? s.dataApresentacao.toString() : null))
                .toList();
    }

    @Override
    public List<SorteioInscritoResumo> pesquisarPorEspectador(UUID espectadorId) {
        return jpa.findByEspectadorId(espectadorId).stream()
                .<SorteioInscritoResumo>map(s -> {
                    InscricaoJpa minhaInscricao = s.inscricoes.stream()
                            .filter(i -> espectadorId.equals(i.espectadorId))
                            .findFirst()
                            .orElse(null);
                    if (minhaInscricao == null) return null;
                    Integer posicao = null;
                    if (minhaInscricao.status != null) {
                        List<InscricaoJpa> mesmoStatus = s.inscricoes.stream()
                                .filter(i -> i.status == minhaInscricao.status)
                                .sorted(Comparator.comparing(i -> i.momentoInscricao))
                                .toList();
                        int idx = 0;
                        for (InscricaoJpa i : mesmoStatus) {
                            idx++;
                            if (espectadorId.equals(i.espectadorId)) {
                                posicao = idx;
                                break;
                            }
                        }
                    }
                    return new SorteioInscritoResumoJpa(
                            s.id.toString(), s.eventoId.toString(),
                            s.apresentacaoId != null ? s.apresentacaoId.toString() : null, s.vagas,
                            s.status != null ? s.status.name() : null,
                            s.prazoInscricao != null ? s.prazoInscricao.toString() : null,
                            s.dataApresentacao != null ? s.dataApresentacao.toString() : null,
                            minhaInscricao.status != null ? minhaInscricao.status.name() : null,
                            posicao,
                            s.inscricoes.size()
                    );
                })
                .filter(r -> r != null)
                .toList();
    }

    record SorteioResumoJpa(String id, String eventoId, String apresentacaoId,
                             int vagas, String status, String prazoInscricao,
                             String dataApresentacao)
            implements SorteioResumo {
        public String getId() { return id; }
        public String getEventoId() { return eventoId; }
        public String getApresentacaoId() { return apresentacaoId; }
        public int getVagas() { return vagas; }
        public String getStatus() { return status; }
        public String getPrazoInscricao() { return prazoInscricao; }
        public String getDataApresentacao() { return dataApresentacao; }
    }

    record SorteioInscritoResumoJpa(
            String sorteioId, String eventoId, String apresentacaoId,
            int vagas, String statusSorteio,
            String prazoInscricao, String dataApresentacao,
            String statusInscricao, Integer posicao, int totalInscritos)
            implements SorteioInscritoResumo {
        public String getSorteioId() { return sorteioId; }
        public String getEventoId() { return eventoId; }
        public String getApresentacaoId() { return apresentacaoId; }
        public int getVagas() { return vagas; }
        public String getStatusSorteio() { return statusSorteio; }
        public String getPrazoInscricao() { return prazoInscricao; }
        public String getDataApresentacao() { return dataApresentacao; }
        public String getStatusInscricao() { return statusInscricao; }
        public Integer getPosicao() { return posicao; }
        public int getTotalInscritos() { return totalInscritos; }
    }
}
