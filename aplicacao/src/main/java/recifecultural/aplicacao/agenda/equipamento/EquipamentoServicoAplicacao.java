package recifecultural.aplicacao.agenda.equipamento;

import recifecultural.dominio.agenda.equipamento.AlocacaoRiderTecnicoServico;
import recifecultural.dominio.agenda.equipamento.Equipamento;
import recifecultural.dominio.agenda.equipamento.EquipamentoId;
import recifecultural.dominio.agenda.equipamento.EquipamentoServico;
import recifecultural.dominio.agenda.equipamento.IEquipamentoRepositorio;
import recifecultural.dominio.espaco.espaco.EspacoId;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.Validate.notNull;

public class EquipamentoServicoAplicacao {

    private final EquipamentoServico servico;
    private final IEquipamentoRepositorio repositorio;
    private final AlocacaoRiderTecnicoServico alocacaoRiderServico;

    public EquipamentoServicoAplicacao(EquipamentoServico servico,
                                        IEquipamentoRepositorio repositorio,
                                        AlocacaoRiderTecnicoServico alocacaoRiderServico) {
        notNull(servico, "EquipamentoServico não pode ser nulo.");
        notNull(repositorio, "IEquipamentoRepositorio não pode ser nulo.");
        notNull(alocacaoRiderServico, "AlocacaoRiderTecnicoServico não pode ser nulo.");
        this.servico = servico;
        this.repositorio = repositorio;
        this.alocacaoRiderServico = alocacaoRiderServico;
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

    public DisponibilidadeEquipamento verificarDisponibilidade(UUID espacoId, String nome, int quantidade) {
        return verificarDisponibilidade(espacoId, nome, quantidade, null, null);
    }

    public DisponibilidadeEquipamento verificarDisponibilidade(UUID espacoId, String nome, int quantidade, LocalDate inicio, LocalDate fim) {
        boolean disponivel = alocacaoRiderServico.verificarDisponibilidade(
                new EspacoId(espacoId), nome, quantidade, inicio, fim);
        List<Equipamento> disponiveis;
        if (inicio != null && fim != null) {
            disponiveis = repositorio.buscarDisponiveisPorEspacoENome(new EspacoId(espacoId), nome, quantidade, inicio, fim);
        } else {
            disponiveis = repositorio.buscarDisponiveisPorEspacoENomeSemData(new EspacoId(espacoId), nome, quantidade);
        }
        int quantidadeDisponivel = disponiveis.size();
        return new DisponibilidadeEquipamento(quantidadeDisponivel >= quantidade, quantidadeDisponivel);
    }

    private EquipamentoResumo toResumo(Equipamento e) {
        return new EquipamentoResumo(
                e.getId().valor().toString(),
                e.getEspacoId().valor().toString(),
                e.getNome(),
                e.getStatus().name(),
                e.getEventoAlocadoId() != null ? e.getEventoAlocadoId().toString() : null,
                e.getAlocacaoInicio() != null ? e.getAlocacaoInicio().toString() : null,
                e.getAlocacaoFim() != null ? e.getAlocacaoFim().toString() : null
        );
    }

    public record EquipamentoResumo(
            String id,
            String espacoId,
            String nome,
            String status,
            String eventoAlocadoId,
            String alocacaoInicio,
            String alocacaoFim
    ) {}

    public record DisponibilidadeEquipamento(boolean disponivel, int quantidadeDisponivel) {}
}
