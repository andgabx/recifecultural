package recifecultural.aplicacao.ingressos;

import recifecultural.dominio.cupom.Cupom;
import recifecultural.dominio.cupom.CupomId;
import recifecultural.dominio.cupom.ICupomRepositorio;
import recifecultural.dominio.cupom.TipoDesconto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import static org.apache.commons.lang3.Validate.notNull;

@Transactional(readOnly = true)
public class CupomServicoAplicacao {

    private final ICupomRepositorio repositorio;

    public CupomServicoAplicacao(ICupomRepositorio repositorio) {
        notNull(repositorio, "ICupomRepositorio não pode ser nulo.");
        this.repositorio = repositorio;
    }

    public List<CupomResumo> listar() {
        return repositorio.listarTodos().stream().map(this::toResumo).toList();
    }

    @Transactional
    public CupomResumo criar(CriarCupomComando cmd) {
        notNull(cmd, "Comando é obrigatório.");
        CupomId id = new CupomId(UUID.randomUUID().toString());
        Cupom cupom = new Cupom(
                id,
                cmd.codigo(),
                cmd.tipoDesconto(),
                cmd.valorDesconto(),
                cmd.valorMinimoPedido() != null ? cmd.valorMinimoPedido() : BigDecimal.ZERO,
                cmd.limiteGlobal(),
                cmd.limitePorCpf(),
                cmd.dataInicio(),
                cmd.dataFim(),
                cmd.categoriaPermitida()
        );
        repositorio.salvar(cupom);
        return toResumo(cupom);
    }

    @Transactional
    public void deletar(String id) {
        repositorio.deletar(new CupomId(id));
    }

    private CupomResumo toResumo(Cupom c) {
        return new CupomResumo(
                c.getId().getValor(),
                c.getCodigo(),
                c.getTipoDesconto().name(),
                c.getValorDesconto(),
                c.getValorMinimoPedido(),
                c.getLimiteGlobal(),
                c.getUsosGlobais(),
                c.getLimitePorCpf(),
                c.getDataInicio(),
                c.getDataFim(),
                c.getCategoriaPermitida()
        );
    }

    public record CriarCupomComando(
            String codigo,
            TipoDesconto tipoDesconto,
            BigDecimal valorDesconto,
            BigDecimal valorMinimoPedido,
            int limiteGlobal,
            int limitePorCpf,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            String categoriaPermitida
    ) {}

    public record CupomResumo(
            String id,
            String codigo,
            String tipoDesconto,
            BigDecimal valorDesconto,
            BigDecimal valorMinimoPedido,
            int limiteGlobal,
            int usosGlobais,
            int limitePorCpf,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            String categoriaPermitida
    ) {}
}
