package recifecultural.infraestrutura.persistencia.espaco.espaco;

import org.springframework.stereotype.Repository;

import recifecultural.dominio.espaco.espaco.Espaco;
import recifecultural.dominio.espaco.espaco.EspacoId;
import recifecultural.dominio.espaco.espaco.IEspacoRepositorio;
import recifecultural.dominio.espaco.espaco.Ocupacao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class EspacoRepositorioImpl implements IEspacoRepositorio {

    private final EspacoJpaRepository jpa;

    public EspacoRepositorioImpl(EspacoJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void salvar(Espaco espaco) {
        jpa.save(toJpa(espaco));
    }

    @Override
    public void atualizar(Espaco espaco) {
        jpa.save(toJpa(espaco));
    }

    @Override
    public Optional<Espaco> obterPorId(EspacoId id) {
        return jpa.findById(id.valor()).map(this::toDomain);
    }

    @Override
    public List<Espaco> listarTodos() {
        return jpa.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Ocupacao> buscarOcupacoesPorPeriodo(EspacoId id, LocalDateTime inicio, LocalDateTime fim) {
        return jpa.findById(id.valor())
                .map(e -> e.ocupacoes.stream()
                        .filter(o -> o.inicio != null && o.fim != null)
                        .map(o -> new Ocupacao(o.inicio, o.fim, o.minutosMontagem, o.minutosDesmontagem, o.bufferExtra))
                        .filter(o -> o.inicioEfetivo().isBefore(fim) && o.fimEfetivo().isAfter(inicio))
                        .toList())
                .orElse(List.of());
    }

    @Override
    public void salvarOcupacao(EspacoId id, Ocupacao ocupacao) {
        jpa.findById(id.valor()).ifPresent(e -> {
            var jpaOcupacao = new OcupacaoJpa();
            jpaOcupacao.inicio = ocupacao.inicio();
            jpaOcupacao.fim = ocupacao.fim();
            jpaOcupacao.minutosMontagem = ocupacao.minutosMontagem();
            jpaOcupacao.minutosDesmontagem = ocupacao.minutosDesmontagem();
            jpaOcupacao.bufferExtra = ocupacao.bufferExtra();
            e.ocupacoes.add(jpaOcupacao);
            jpa.save(e);
        });
    }

    private EspacoJpa toJpa(Espaco espaco) {
        var e = new EspacoJpa();
        e.id = espaco.getId().valor();
        e.nome = espaco.getNome();
        e.capacidadeMaxima = espaco.getCapacidadeMaxima();
        e.status = espaco.getStatus();
        e.riderTecnico = new ArrayList<>(espaco.getRiderTecnico());
        return e;
    }

    private Espaco toDomain(EspacoJpa e) {
        List<String> rider = e.riderTecnico != null ? new ArrayList<>(e.riderTecnico) : List.of();
        return new Espaco(new EspacoId(e.id), e.nome, e.capacidadeMaxima, rider, e.status);
    }
}
