package recifecultural.infraestrutura.padroes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import recifecultural.dominio.agenda.evento.Evento;
import recifecultural.dominio.agenda.evento.IEventoRepositorio;
import recifecultural.dominio.agenda.evento.StatusEvento;
import recifecultural.dominio.compartilhado.auditoria.AcaoAuditoria;
import recifecultural.dominio.compartilhado.auditoria.IAuditoriaRepositorio;
import recifecultural.dominio.compartilhado.auditoria.RegistroAuditoria;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/*
 * Padrão Decorator (Par 4): decora IEventoRepositorio adicionando trilha de
 * auditoria em mudanças de status (aprovação, reprovação, cancelamento).
 * O serviço de aprovação (F4.1) continua a chamar repositorio.atualizar(...)
 * sem saber que está sendo auditado.
 *
 * Os registros vão tanto para o log (slf4j) quanto para o `IAuditoriaRepositorio`,
 * que persiste em banco e alimenta a tela `/gestor/auditoria`.
 *
 * Atomicidade: para que a operação no domínio e o registro de auditoria
 * compartilhem a mesma transação, o método do serviço chamador deve ser
 * anotado com @Transactional. O decorator em si não gerencia transações.
 */
public class EventoRepositorioComAuditoria implements IEventoRepositorio {

    private static final String ENTIDADE = "EVENTO";
    private static final Logger log = LoggerFactory.getLogger(EventoRepositorioComAuditoria.class);

    private final IEventoRepositorio delegado;
    private final IAuditoriaRepositorio auditoria;

    public EventoRepositorioComAuditoria(IEventoRepositorio delegado,
                                          IAuditoriaRepositorio auditoria) {
        if (delegado == null) throw new IllegalArgumentException("Delegado não pode ser nulo.");
        if (auditoria == null) throw new IllegalArgumentException("Auditoria não pode ser nula.");
        this.delegado = delegado;
        this.auditoria = auditoria;
    }

    @Override
    public void salvar(Evento evento) {
        // Bug 5: salva primeiro — só loga e registra após confirmação do delegate
        delegado.salvar(evento);
        log.info("[AUDITORIA EVENTO] criado id={} promotor={} titulo='{}' status={}",
                evento.getId(), evento.getPromotorId(), evento.getTitulo(), evento.getStatus());
        registrarAuditoria(new RegistroAuditoria(
                ENTIDADE, evento.getId(), AcaoAuditoria.CRIADO,
                null, evento.getStatus().name(),
                String.format("Evento '%s' criado pelo promotor %s",
                        evento.getTitulo(), evento.getPromotorId())));
    }

    @Override
    public void atualizar(Evento evento) {
        StatusEvento statusAtual = evento.getStatus();
        Optional<Evento> anterior = delegado.obter(evento.getId());
        StatusEvento statusAnterior = anterior.map(Evento::getStatus).orElse(null);
        delegado.atualizar(evento);
        if (statusAnterior != statusAtual) {
            log.info("[AUDITORIA EVENTO] transição id={} {} → {} titulo='{}'",
                    evento.getId(), statusAnterior, statusAtual, evento.getTitulo());
            registrarAuditoria(new RegistroAuditoria(
                    ENTIDADE, evento.getId(), AcaoAuditoria.TRANSICAO_STATUS,
                    statusAnterior != null ? statusAnterior.name() : null,
                    statusAtual.name(),
                    String.format("Evento '%s' transicionou de %s para %s",
                            evento.getTitulo(), statusAnterior, statusAtual)));
        }
    }

    @Override
    public void deletar(UUID id) {
        Optional<Evento> anterior = delegado.obter(id);
        String titulo = anterior.map(Evento::getTitulo).orElse("(desconhecido)");
        delegado.deletar(id);
        log.info("[AUDITORIA EVENTO] removido id={}", id);
        registrarAuditoria(new RegistroAuditoria(
                ENTIDADE, id, AcaoAuditoria.REMOVIDO,
                anterior.map(e -> e.getStatus().name()).orElse(null), null,
                String.format("Evento '%s' removido", titulo)));
    }

    // Bug 4: isola a chamada ao repositório de auditoria para que uma falha aqui
    // não reverta a operação já persistida no domínio (que é o comportamento correto).
    // A garantia de atomicidade plena (rollback de ambos) depende de @Transactional
    // no método do serviço chamador.
    private void registrarAuditoria(RegistroAuditoria registro) {
        try {
            auditoria.registrar(registro);
        } catch (RuntimeException e) {
            log.error("[AUDITORIA EVENTO] falha ao persistir registro de auditoria para entidade={} id={}: {}",
                    registro.getEntidade(), registro.getEntidadeId(), e.getMessage(), e);
        }
    }

    @Override
    public Optional<Evento> obter(UUID id) {
        return delegado.obter(id);
    }

    @Override
    public List<Evento> obterTodos() {
        return delegado.obterTodos();
    }

    @Override
    public List<Evento> obterPorLocalEIntervalo(UUID localId, LocalDateTime inicio, LocalDateTime fim) {
        return delegado.obterPorLocalEIntervalo(localId, inicio, fim);
    }

    @Override
    public List<Evento> obterReprovacoesPorPromotor(UUID promotorId) {
        return delegado.obterReprovacoesPorPromotor(promotorId);
    }

    @Override
    public List<Evento> obterEventosAprovadosPorPromotor(UUID promotorId) {
        return delegado.obterEventosAprovadosPorPromotor(promotorId);
    }

    @Override
    public List<Evento> obterEventosFinalizadosPorPromotor(UUID promotorId) {
        return delegado.obterEventosFinalizadosPorPromotor(promotorId);
    }
}
