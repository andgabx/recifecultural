package recifecultural.dominio.espaco.setor;

import recifecultural.dominio.espaco.espaco.Espaco;
import recifecultural.dominio.espaco.espaco.EspacoId;
import recifecultural.dominio.espaco.espaco.IEspacoRepositorio;
import recifecultural.dominio.espaco.espaco.StatusEspaco;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

        int totalNovo = fileirasHorizontais * assentosPorFileiraVertical;
        int totalExistente = setorRepositorio.listarPorEspaco(espacoId).stream()
                .mapToInt(Setor::capacidade).sum();
        if (totalExistente + totalNovo > espaco.getCapacidadeMaxima()) {
            throw new IllegalStateException(
                    "Capacidade do espaço excedida. Já cadastrados " + totalExistente
                            + " assentos; novo setor adiciona " + totalNovo
                            + "; capacidade máxima é " + espaco.getCapacidadeMaxima() + "."
            );
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

    public Setor editarSetor(SetorId setorId, String nome, TipoSetor tipoSetor,
                             int fileirasHorizontais, int assentosPorFileiraVertical) {
        Setor setor = setorRepositorio.obterPorId(setorId)
                .orElseThrow(() -> new IllegalArgumentException("Setor não encontrado."));

        Espaco espaco = espacoRepositorio.obterPorId(setor.getEspacoId())
                .orElseThrow(() -> new IllegalArgumentException("Espaço do setor não encontrado."));

        // Valida capacidade total considerando o novo tamanho deste setor
        int totalNovo = fileirasHorizontais * assentosPorFileiraVertical;
        int totalOutros = setorRepositorio.listarPorEspaco(setor.getEspacoId()).stream()
                .filter(s -> !s.getId().equals(setorId))
                .mapToInt(Setor::capacidade).sum();
        if (totalOutros + totalNovo > espaco.getCapacidadeMaxima()) {
            throw new IllegalStateException(
                    "Capacidade do espaço excedida. Outros setores totalizam " + totalOutros
                            + " assentos; este setor passaria a ter " + totalNovo
                            + "; capacidade máxima é " + espaco.getCapacidadeMaxima() + "."
            );
        }

        boolean dimensoesMudaram = setor.editar(nome, tipoSetor, fileirasHorizontais, assentosPorFileiraVertical);

        if (dimensoesMudaram) {
            List<Assento> novosAssentos = new ArrayList<>();
            for (int i = 0; i < fileirasHorizontais; i++) {
                String letraFileira = String.valueOf((char) ('A' + i));
                for (int j = 1; j <= assentosPorFileiraVertical; j++) {
                    novosAssentos.add(new Assento(letraFileira, j));
                }
            }
            setor.mapearAssentos(novosAssentos);
        }

        setorRepositorio.atualizar(setor);
        return setor;
    }

    public long contarAssentosDisponiveisPorEspaco(EspacoId espacoId) {
        List<Setor> setores = setorRepositorio.listarPorEspaco(espacoId);
        return setores.stream()
                .mapToLong(Setor::contarAssentosDisponiveis)
                .sum();
    }

    public List<Setor> listarPorEspaco(EspacoId espacoId) {
        return setorRepositorio.listarPorEspaco(espacoId);
    }

    public void bloquearAssento(SetorId setorId, UUID assentoId, MotivoIndisponibilidade motivo, String observacao) {
        Setor setor = setorRepositorio.obterPorId(setorId).orElseThrow();
        Assento assento = setor.obterAssento(assentoId)
                .orElseThrow(() -> new IllegalArgumentException("Cadeira não encontrada no setor."));
        
        assento.bloquearAdministrativamente(motivo, observacao);
        setorRepositorio.atualizar(setor);
    }

    public void desbloquearAssento(SetorId setorId, UUID assentoId) {
        Setor setor = setorRepositorio.obterPorId(setorId).orElseThrow();
        Assento assento = setor.obterAssento(assentoId).orElseThrow();
        
        assento.desbloquearAdministrativamente();
        setorRepositorio.atualizar(setor);
    }
}
