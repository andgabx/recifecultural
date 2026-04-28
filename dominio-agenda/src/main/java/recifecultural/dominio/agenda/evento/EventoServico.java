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

        evento.submeterParaAnalise();
        repositorio.atualizar(evento);
    }

    public void aprovar(UUID id) {
        Evento evento = buscarOuLancar(id);
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
