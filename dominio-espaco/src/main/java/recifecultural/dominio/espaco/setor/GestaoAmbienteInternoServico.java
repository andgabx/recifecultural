package recifecultural.dominio.espaco.setor;

import recifecultural.dominio.espaco.espaco.Espaco;
import recifecultural.dominio.espaco.espaco.EspacoId;
import recifecultural.dominio.espaco.espaco.IEspacoRepositorio;
import recifecultural.dominio.espaco.espaco.StatusEspaco;

import java.util.ArrayList;
import java.util.List;

public class GestaoAmbienteInternoServico {
    private final ISetorRepositorio setorRepositorio;
    private final IEspacoRepositorio espacoRepositorio;

    public GestaoAmbienteInternoServico(ISetorRepositorio setorRepositorio, IEspacoRepositorio espacoRepositorio) {
        if (setorRepositorio == null) throw new IllegalArgumentException("Repositório de setores é obrigatório.");
        if (espacoRepositorio == null) throw new IllegalArgumentException("Repositório de espaços é obrigatório.");
        this.setorRepositorio = setorRepositorio;
        this.espacoRepositorio = espacoRepositorio;
    }

    public Setor configurarGestaoAmbiente(EspacoId espacoId, String nome, TipoSetor tipoSetor, int fileirasHorizontais, int assentosPorFileiraVertical) {
        Espaco espaco = espacoRepositorio.obterPorId(espacoId)
                .orElseThrow(() -> new IllegalArgumentException("Espaço não encontrado."));
        
        if (espaco.getStatus() != StatusEspaco.ATIVO) {
            throw new IllegalStateException("Espaço deve estar com status ATIVO para configurar o ambiente interno.");
        }

        if (fileirasHorizontais <= 0 || fileirasHorizontais > 26) {
            throw new IllegalArgumentException("Número de fileiras horizontais deve ser entre 1 e 26.");
        }
        if (assentosPorFileiraVertical <= 0) {
            throw new IllegalArgumentException("Número de assentos por fileira vertical deve ser maior que 0.");
        }

        Setor setor = new Setor(espacoId, nome, tipoSetor, fileirasHorizontais, assentosPorFileiraVertical);
        List<Assento> assentos = new ArrayList<>();
        
        for (int i = 0; i < fileirasHorizontais; i++) {
            String letraFileira = String.valueOf((char) ('A' + i));
            for (int j = 1; j <= assentosPorFileiraVertical; j++) {
                assentos.add(new Assento(letraFileira, j));
            }
        }
        
        setor.mapearAssentos(assentos);
        setorRepositorio.salvar(setor);
        return setor;
    }

    public long contarAssentosDisponiveisPorEspaco(EspacoId espacoId) {
        List<Setor> setores = setorRepositorio.listarPorEspaco(espacoId);
        return setores.stream()
                .mapToLong(Setor::contarAssentosDisponiveis)
                .sum();
    }
}
