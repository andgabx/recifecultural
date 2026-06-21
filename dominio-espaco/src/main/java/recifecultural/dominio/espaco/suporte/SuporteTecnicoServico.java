package recifecultural.dominio.espaco.suporte;

import recifecultural.dominio.compartilhado.notificacao.INotificacaoServico;
import recifecultural.dominio.espaco.setor.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class SuporteTecnicoServico {

    private static final String CONTEXTO_SUPORTE = "SUPORTE_TECNICO";

    private final IChamadoSuporteRepositorio chamadoRepositorio;
    private final ISetorRepositorio setorRepositorio;
    private final INotificacaoServico notificacaoServico;

    public SuporteTecnicoServico(IChamadoSuporteRepositorio chamadoRepositorio,
                                  ISetorRepositorio setorRepositorio,
                                  INotificacaoServico notificacaoServico) {
        this.chamadoRepositorio = chamadoRepositorio;
        this.setorRepositorio = setorRepositorio;
        this.notificacaoServico = notificacaoServico;
    }

    public ChamadoSuporte abrirChamado(SetorId setorId, UUID assentoId, TipoChamado tipo,
                                       MotivoIndisponibilidade motivo, String descricao) {
        Setor setor = setorRepositorio.obterPorId(setorId)
                .orElseThrow(() -> new IllegalArgumentException("Setor não encontrado."));

        setor.bloquearAssento(assentoId, motivo);
        setorRepositorio.atualizar(setor);

        ChamadoSuporte chamado = new ChamadoSuporte(assentoId, tipo, motivo, descricao);
        chamadoRepositorio.salvar(chamado);

        String mensagem = String.format(
                "Novo chamado aberto — assento %s, motivo: %s. Descrição: %s",
                assentoId, motivo, descricao);
        notificacaoServico.enviarBroadcast(mensagem, CONTEXTO_SUPORTE, chamado.getId());

        return chamado;
    }

    public void escalarVencidos(LocalDateTime agora, long slaHoras) {
        LocalDateTime limite = agora.minusHours(slaHoras);
        List<ChamadoSuporte> vencidos = chamadoRepositorio.listarAbertosAntesDe(limite);
        for (ChamadoSuporte chamado : vencidos) {
            chamado.escalar();
            chamadoRepositorio.atualizar(chamado);
            String mensagem = String.format(
                    "Chamado %s escalado automaticamente — sem resolução após %d horas.",
                    chamado.getId(), slaHoras);
            notificacaoServico.enviarBroadcast(mensagem, CONTEXTO_SUPORTE, chamado.getId());
        }
    }

    public void aceitarChamado(UUID chamadoId, SetorId setorId, UUID assentoId, String tecnico) {
        ChamadoSuporte chamado = chamadoRepositorio.obterPorId(chamadoId).orElseThrow(() -> new IllegalArgumentException("Chamado não encontrado"));
        Setor setor = setorRepositorio.obterPorId(setorId).orElseThrow(() -> new IllegalArgumentException("Setor não encontrado"));
        Assento assento = setor.obterAssento(assentoId).orElseThrow(() -> new IllegalArgumentException("Assento não encontrado"));

        chamado.aceitarChamado(tecnico);
        assento.iniciarManutencao();

        setorRepositorio.atualizar(setor);
        chamadoRepositorio.atualizar(chamado);
    }

    public void resolverChamado(UUID chamadoId, SetorId setorId, UUID assentoId, String solucao) {
        ChamadoSuporte chamado = chamadoRepositorio.obterPorId(chamadoId).orElseThrow(() -> new IllegalArgumentException("Chamado não encontrado"));
        Setor setor = setorRepositorio.obterPorId(setorId).orElseThrow(() -> new IllegalArgumentException("Setor não encontrado"));
        Assento assento = setor.obterAssento(assentoId).orElseThrow(() -> new IllegalArgumentException("Assento não encontrado"));

        chamado.resolver();
        assento.resolverManutencao(); 

        setorRepositorio.atualizar(setor);
        chamadoRepositorio.atualizar(chamado);
    }
}
