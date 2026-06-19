package recifecultural.aplicacao.auditoria;

import recifecultural.dominio.compartilhado.auditoria.IAuditoriaRepositorio;
import recifecultural.dominio.compartilhado.auditoria.RegistroAuditoria;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import static org.apache.commons.lang3.Validate.notNull;

@Transactional(readOnly = true)
public class AuditoriaServicoAplicacao {

    private final IAuditoriaRepositorio repositorio;

    public AuditoriaServicoAplicacao(IAuditoriaRepositorio repositorio) {
        notNull(repositorio, "IAuditoriaRepositorio não pode ser nulo.");
        this.repositorio = repositorio;
    }

    public List<RegistroResumo> listarRecentes(int limite) {
        return repositorio.listarRecentes(limite).stream().map(this::toResumo).toList();
    }

    public List<RegistroResumo> listarPorEntidade(String entidade, int limite) {
        return repositorio.listarPorEntidade(entidade, limite).stream().map(this::toResumo).toList();
    }

    private RegistroResumo toResumo(RegistroAuditoria r) {
        return new RegistroResumo(
                r.getId().toString(),
                r.getEntidade(),
                r.getEntidadeId().toString(),
                r.getAcao().name(),
                r.getStatusAnterior(),
                r.getStatusNovo(),
                r.getDescricao(),
                r.getMomento()
        );
    }

    public record RegistroResumo(
            String id,
            String entidade,
            String entidadeId,
            String acao,
            String statusAnterior,
            String statusNovo,
            String descricao,
            LocalDateTime momento
    ) {}
}
