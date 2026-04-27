package recifecultural.dominio.agenda.setor;

import recifecultural.dominio.agenda.espaco.Espaco;
import recifecultural.dominio.agenda.espaco.EspacoId;
import recifecultural.dominio.agenda.espaco.IEspacoRepositorio;
import recifecultural.dominio.agenda.espaco.StatusEspaco;

import java.util.ArrayList;
import java.util.List;

public class SetorServico {
    private final ISetorRepositorio setorRepositorio;
    private final IEspacoRepositorio espacoRepositorio;

    public SetorServico(ISetorRepositorio setorRepositorio, IEspacoRepositorio espacoRepositorio) {
        if (setorRepositorio == null) throw new IllegalArgumentException("Repositório de setores é obrigatório.");
        if (espacoRepositorio == null) throw new IllegalArgumentException("Repositório de espaços é obrigatório.");
        this.setorRepositorio = setorRepositorio;
        this.espacoRepositorio = espacoRepositorio;
    }

    public Setor configurarSetor(EspacoId espacoId, String nome, TipoSetor tipoSetor, int fileiras, int assentosPorFileira) {
        Espaco espaco = espacoRepositorio.obterPorId(espacoId)
                .orElseThrow(() -> new IllegalArgumentException("Espaço não encontrado."));
        
        if (espaco.getStatus() != StatusEspaco.ATIVO) {
            throw new IllegalStateException("Espaço deve estar com status ATIVO para configurar um setor.");
        }

        if (fileiras <= 0 || fileiras > 26) {
            throw new IllegalArgumentException("Número de fileiras deve ser entre 1 e 26.");
        }
        if (assentosPorFileira <= 0) {
            throw new IllegalArgumentException("Número de assentos por fileira deve ser maior que 0.");
        }

        Setor setor = new Setor(espacoId, nome, tipoSetor);
        List<Assento> assentos = new ArrayList<>();
        
        for (int i = 0; i < fileiras; i++) {
            String letraFileira = String.valueOf((char) ('A' + i));
            for (int j = 1; j <= assentosPorFileira; j++) {
                assentos.add(new Assento(letraFileira, j));
            }
        }
        
        setor.mapearAssentos(assentos);
        setorRepositorio.salvar(setor);
        return setor;
    }
}
