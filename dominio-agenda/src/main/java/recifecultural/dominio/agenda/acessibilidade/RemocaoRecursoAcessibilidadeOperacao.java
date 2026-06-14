package recifecultural.dominio.agenda.acessibilidade;

import recifecultural.dominio.compartilhado.notificacao.INotificacaoServico;

import java.util.UUID;

public class RemocaoRecursoAcessibilidadeOperacao extends OperacaoRecursoAcessibilidadeTemplate {

    private final UUID recursoId;
    private final String justificativa;

    public RemocaoRecursoAcessibilidadeOperacao(IRecursoAcessibilidadeRepositorio repositorio,
                                                INotificacaoServico notificacaoServico,
                                                UUID recursoId,
                                                String justificativa) {
        super(repositorio, notificacaoServico);
        this.recursoId = recursoId;
        this.justificativa = justificativa;
    }

    @Override
    protected RecursoAcessibilidade buscarRecurso() {
        return repositorio().obter(recursoId)
                .orElseThrow(() -> new IllegalArgumentException("Recurso não encontrado: " + recursoId));
    }

    @Override
    protected RecursoAcessibilidade.RecursoEvento aplicarRegraDeDominio(RecursoAcessibilidade recurso) {
        return recurso.remover(justificativa);
    }

    @Override
    protected void notificar(RecursoAcessibilidade.RecursoEvento evento) {
        RecursoAcessibilidade recurso = evento.getRecurso();
        String mensagem = String.format(
                "O recurso de acessibilidade %s foi removido do evento %s. Justificativa: %s",
                recurso.getTipo(), recurso.getEventoId(), recurso.getJustificativaRemocao());
        notificacaoServico().enviarBroadcast(mensagem, "ACESSIBILIDADE_REMOVIDA", recurso.getEventoId());
    }
}
