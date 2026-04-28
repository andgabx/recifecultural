package recifecultural.dominio.agenda.evento;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EventoServico {

    private final IEventoRepositorio repositorio;

    public EventoServico(IEventoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public void salvar(Evento evento) {
        repositorio.salvar(evento);
    }

    public Optional<Evento> obter(UUID id) {
        return repositorio.obter(id);
    }

    public void submeterParaAnalise(UUID id) {
        Evento evento = buscarOuLancar(id);

        List<Evento> reprovacoes = repositorio.obterReprovacoesPorPromotor(evento.getPromotorId());
        LocalDateTime noventaDiasAtras = LocalDateTime.now().minusDays(90);

        List<Evento> reprovacoesRecentes = reprovacoes.stream()
                .filter(e -> e.getDataReprovacao() != null && e.getDataReprovacao().isAfter(noventaDiasAtras))
                .toList();

        if (reprovacoesRecentes.size() >= 3) {
            LocalDateTime dataDesbloqueio = reprovacoesRecentes.stream()
                    .map(Evento::getDataReprovacao)
                    .max(Comparator.naturalOrder())
                    .orElseThrow()
                    .plusDays(30);
            throw new IllegalStateException(
                    "Promotor bloqueado por excesso de reprovações. Novas submissões permitidas a partir de " + dataDesbloqueio + "."
            );
        }

        // RN-S1: taxa histórica de aprovação inferior a 30% (mínimo 5 eventos em 12 meses) → revisão adicional
        List<Evento> finalizados = repositorio.obterEventosFinalizadosPorPromotor(evento.getPromotorId());
        LocalDateTime dozesMesesAtras = LocalDateTime.now().minusMonths(12);
        List<Evento> finalizadosRecentes = finalizados.stream()
                .filter(e -> {
                    LocalDateTime data = e.getStatus() == StatusEvento.APROVADO
                            ? e.getDataAprovacao()
                            : e.getDataReprovacao();
                    return data != null && data.isAfter(dozesMesesAtras);
                })
                .toList();
        if (finalizadosRecentes.size() >= 5) {
            long aprovados = finalizadosRecentes.stream()
                    .filter(e -> e.getStatus() == StatusEvento.APROVADO)
                    .count();
            double taxa = (double) aprovados / finalizadosRecentes.size();
            if (taxa < 0.30) {
                evento.marcarComoRequerRevisaoAdicional();
            }
        }

        evento.submeterParaAnalise();
        repositorio.atualizar(evento);
    }

    public void aprovar(UUID id) {
        Evento evento = buscarOuLancar(id);

        // RN-A1: conflito de espaço — bloqueia se outro evento APROVADO ocupa o mesmo espaço no mesmo período
        List<Evento> noEspaco = repositorio.obterPorLocalEIntervalo(
                evento.getLocalId(),
                evento.getPeriodo().getInicio(),
                evento.getPeriodo().getFim()
        );
        boolean temConflito = noEspaco.stream()
                .filter(e -> !e.getId().equals(evento.getId()))
                .anyMatch(e -> e.getStatus() == StatusEvento.APROVADO);
        if (temConflito) {
            throw new IllegalStateException(
                    "Não é possível aprovar: o espaço já possui evento aprovado no mesmo período."
            );
        }

        // RN-A2: limite de 5 eventos aprovados por promotor simultaneamente
        List<Evento> aprovadosDoPromotor = repositorio.obterEventosAprovadosPorPromotor(evento.getPromotorId());
        if (aprovadosDoPromotor.size() >= 5) {
            throw new IllegalStateException(
                    "Promotor já atingiu o limite de 5 eventos aprovados simultaneamente."
            );
        }

        evento.aprovar();
        repositorio.atualizar(evento);
    }

    public void reprovar(UUID id, FeedbackReprovacao feedback) {
        Evento evento = buscarOuLancar(id);
        evento.reprovar(feedback);
        repositorio.atualizar(evento);
    }

    public void deletar(UUID id) {
        repositorio.deletar(id);
    }

    private Evento buscarOuLancar(UUID id) {
        return repositorio.obter(id)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado: " + id));
    }
}
