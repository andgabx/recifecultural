package recifecultural.dominio.agenda.acessibilidade;

import recifecultural.dominio.compartilhado.notificacao.INotificacaoServico;

import java.util.UUID;

/** Operação de remoção de um recurso anunciado: aplica a regra e avisa o público do evento. */
public class RemocaoRecursoAcessibilidadeOperacao extends OperacaoRecursoAcessibilidadeTemplate {

    private final String justificativa;

    public RemocaoRecursoAcessibilidadeOperacao(IRecursoAcessibilidadeRepositorio repositorio,
                                                INotificacaoServico notificacaoServico,
                                                UUID recursoId,
                                                String justificativa) {
        super(repositorio, notificacaoServico, recursoId);
        this.justificativa = justificativa;
    }

    @Override
    protected void aplicarRegra(RecursoAcessibilidade recurso) {
        recurso.remover(justificativa);
    }

    @Override
    protected void notificar(RecursoAcessibilidade recurso) {
        String mensagem = String.format(
                "O recurso de acessibilidade %s foi removido do evento %s. Justificativa: %s",
                recurso.getTipo(), recurso.getEventoId(), recurso.getJustificativaRemocao());
        notificacaoServico.enviarBroadcast(mensagem, "ACESSIBILIDADE_REMOVIDA", recurso.getEventoId());
    }
}
