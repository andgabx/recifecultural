package recifecultural.aplicacao.agenda.equipamento;

import recifecultural.dominio.agenda.equipamento.Equipamento;
import recifecultural.dominio.agenda.equipamento.EquipamentoId;
import recifecultural.dominio.agenda.equipamento.EquipamentoServico;
import recifecultural.dominio.agenda.equipamento.IEquipamentoRepositorio;
import recifecultural.dominio.espaco.espaco.EspacoId;

import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.Validate.notNull;

public class EquipamentoServicoAplicacao {

    private final EquipamentoServico servico;
    private final IEquipamentoRepositorio repositorio;

    public EquipamentoServicoAplicacao(EquipamentoServico servico,
                                        IEquipamentoRepositorio repositorio) {
        notNull(servico, "EquipamentoServico não pode ser nulo.");
        notNull(repositorio, "IEquipamentoRepositorio não pode ser nulo.");
        this.servico = servico;
        this.repositorio = repositorio;
    }

    public List<EquipamentoResumo> listarPorEspaco(UUID espacoId) {
        return repositorio.listarPorEspaco(new EspacoId(espacoId)).stream()
                .map(this::toResumo)
                .toList();
    }

    public String adquirir(UUID espacoId, String nome) {
        return servico.adquirirEquipamento(new EspacoId(espacoId), nome).valor().toString();
    }

    public void marcarManutencao(UUID id) {
        servico.reportarManutencao(new EquipamentoId(id));
    }

    public void liberar(UUID id) {
        servico.liberar(new EquipamentoId(id));
    }

    public void remover(UUID id) {
        servico.removerEquipamento(new EquipamentoId(id));
    }

    private EquipamentoResumo toResumo(Equipamento e) {
        return new EquipamentoResumo(
                e.getId().valor().toString(),
                e.getEspacoId().valor().toString(),
                e.getNome(),
                e.getStatus().name(),
                e.getEventoAlocadoId() != null ? e.getEventoAlocadoId().toString() : null
        );
    }

    public record EquipamentoResumo(
            String id,
            String espacoId,
            String nome,
            String status,
            String eventoAlocadoId
    ) {}
}
