package recifecultural.dominio.espaco.suporte;

import recifecultural.dominio.espaco.setor.ISetorRepositorio;
import recifecultural.dominio.espaco.setor.Setor;
import recifecultural.dominio.espaco.setor.MotivoIndisponibilidadeAssento;
import recifecultural.dominio.espaco.setor.SetorId;
import java.util.UUID;

public class SuporteTecnicoServico {

    private final IChamadoSuporteRepositorio chamadoRepositorio;
    private final ISetorRepositorio setorRepositorio;

    public SuporteTecnicoServico(IChamadoSuporteRepositorio chamadoRepositorio, ISetorRepositorio setorRepositorio) {
        this.chamadoRepositorio = chamadoRepositorio;
        this.setorRepositorio = setorRepositorio;
    }

    public ChamadoSuporte abrirChamado(SetorId setorId, UUID assentoId, MotivoIndisponibilidadeAssento motivo, String descricao) {
        Setor setor = setorRepositorio.obterPorId(setorId)
                .orElseThrow(() -> new IllegalArgumentException("Setor não encontrado."));

        setor.bloquearAssento(assentoId, motivo);
        setorRepositorio.atualizar(setor);

        ChamadoSuporte chamado = new ChamadoSuporte(assentoId, motivo, descricao);
        chamadoRepositorio.salvar(chamado);
        
        return chamado;
    }
}
