package recifecultural.dominio.espaco.suporte;

import recifecultural.dominio.compartilhado.notificacao.NotificacaoServico;
import recifecultural.dominio.espaco.setor.ISetorRepositorio;
import recifecultural.dominio.espaco.setor.MotivoIndisponibilidadeAssento;
import recifecultural.dominio.espaco.setor.Setor;
import recifecultural.dominio.espaco.setor.SetorId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class SuporteTecnicoServico {

    private static final String CONTEXTO_SUPORTE = "SUPORTE_TECNICO";

    private final IChamadoSuporteRepositorio chamadoRepositorio;
    private final ISetorRepositorio setorRepositorio;
    private final NotificacaoServico notificacaoServico;

    public SuporteTecnicoServico(IChamadoSuporteRepositorio chamadoRepositorio,
                                  ISetorRepositorio setorRepositorio,
                                  NotificacaoServico notificacaoServico) {
        this.chamadoRepositorio = chamadoRepositorio;
        this.setorRepositorio = setorRepositorio;
        this.notificacaoServico = notificacaoServico;
    }

    public ChamadoSuporte abrirChamado(SetorId setorId, UUID assentoId,
                                        MotivoIndisponibilidadeAssento motivo, String descricao) {
        Setor setor = setorRepositorio.obterPorId(setorId)
                .orElseThrow(() -> new IllegalArgumentException("Setor não encontrado."));

        setor.bloquearAssento(assentoId, motivo);
        setorRepositorio.atualizar(setor);

        ChamadoSuporte chamado = new ChamadoSuporte(assentoId, motivo, descricao);
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
}
