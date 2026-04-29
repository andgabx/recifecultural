package recifecultural.dominio.espaco.setor;

import recifecultural.dominio.espaco.espaco.EspacoId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Setor {

    private final SetorId id;
    private final EspacoId espacoId;
    private String nome;
    private TipoSetor tipoSetor;
    private int fileirasHorizontais;
    private int assentosPorFileiraVertical;
    private List<Assento> assentos;
    private int versao;

    public Setor(EspacoId espacoId, String nome, TipoSetor tipoSetor, int fileirasHorizontais, int assentosPorFileiraVertical) {
        if (espacoId == null) throw new IllegalArgumentException("Espaço é obrigatório.");
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome é obrigatório.");
        if (tipoSetor == null) throw new IllegalArgumentException("Tipo de setor é obrigatório.");
        if (fileirasHorizontais <= 0 || fileirasHorizontais > 26) throw new IllegalArgumentException("Número de fileiras horizontais deve ser entre 1 e 26.");
        if (assentosPorFileiraVertical <= 0) throw new IllegalArgumentException("Número de assentos por fileira vertical deve ser maior que 0.");
        this.id = SetorId.novo();
        this.espacoId = espacoId;
        this.nome = nome;
        this.tipoSetor = tipoSetor;
        this.fileirasHorizontais = fileirasHorizontais;
        this.assentosPorFileiraVertical = assentosPorFileiraVertical;
        this.assentos = new ArrayList<>();
        this.versao = 0;
    }

    public Setor(SetorId id, EspacoId espacoId, String nome, TipoSetor tipoSetor, int fileirasHorizontais, int assentosPorFileiraVertical, List<Assento> assentos, int versao) {
        this.id = id; this.espacoId = espacoId; this.nome = nome;
        this.tipoSetor = tipoSetor; 
        this.fileirasHorizontais = fileirasHorizontais;
        this.assentosPorFileiraVertical = assentosPorFileiraVertical;
        this.assentos = new ArrayList<>(assentos); 
        this.versao = versao;
    }

    public void mapearAssentos(List<Assento> novosAssentos) {
        if (novosAssentos == null || novosAssentos.isEmpty())
            throw new IllegalArgumentException("Lista de assentos não pode ser vazia.");
        boolean temDuplicata = novosAssentos.stream()
                .map(Assento::getCodigo)
                .distinct().count() < novosAssentos.size();
        if (temDuplicata) throw new IllegalArgumentException("Existem assentos com códigos duplicados.");
        this.assentos = new ArrayList<>(novosAssentos);
    }

    public Assento preReservar(UUID assentoId) {
        Assento assento = assentos.stream()
                .filter(a -> a.getId().equals(assentoId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Assento não encontrado no setor."));
        assento.marcarPreReservado();
        return assento;
    }

    public Assento liberarAssento(UUID assentoId) {
        Assento assento = encontrarAssento(assentoId);
        assento.liberar();
        return assento;
    }

    public Assento ocuparAssento(UUID assentoId) {
        Assento assento = encontrarAssento(assentoId);
        assento.ocupar();
        return assento;
    }

    public Assento bloquearAssento(UUID assentoId, MotivoIndisponibilidadeAssento motivo) {
        Assento assento = encontrarAssento(assentoId);
        assento.bloquear(motivo);
        return assento;
    }

    private Assento encontrarAssento(UUID assentoId) {
        return assentos.stream()
                .filter(a -> a.getId().equals(assentoId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Assento não encontrado."));
    }

    public int capacidade() { return assentos.size(); }

    public long contarAssentosDisponiveis() {
        return assentos.stream().filter(a -> a.getStatus() == StatusAssento.LIVRE).count();
    }

    public List<Assento> getAssentos() { return Collections.unmodifiableList(assentos); }
    public SetorId getId() { return id; }
    public EspacoId getEspacoId() { return espacoId; }
    public String getNome() { return nome; }
    public TipoSetor getTipoSetor() { return tipoSetor; }
    public int getFileirasHorizontais() { return fileirasHorizontais; }
    public int getAssentosPorFileiraVertical() { return assentosPorFileiraVertical; }
    public int getVersao() { return versao; }
}
