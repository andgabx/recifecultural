package recifecultural.dominio.agenda.setor;

import recifecultural.dominio.agenda.espaco.EspacoId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Setor {

    private final SetorId id;
    private final EspacoId espacoId;
    private String nome;
    private TipoSetor tipoSetor;
    private List<Assento> assentos;
    private int versao;

    public Setor(EspacoId espacoId, String nome, TipoSetor tipoSetor) {
        if (espacoId == null) throw new IllegalArgumentException("Espaço é obrigatório.");
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome é obrigatório.");
        if (tipoSetor == null) throw new IllegalArgumentException("Tipo de setor é obrigatório.");
        this.id = SetorId.novo();
        this.espacoId = espacoId;
        this.nome = nome;
        this.tipoSetor = tipoSetor;
        this.assentos = new ArrayList<>();
        this.versao = 0;
    }

    public Setor(SetorId id, EspacoId espacoId, String nome, TipoSetor tipoSetor,
                 List<Assento> assentos, int versao) {
        this.id = id; this.espacoId = espacoId; this.nome = nome;
        this.tipoSetor = tipoSetor; this.assentos = new ArrayList<>(assentos); this.versao = versao;
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

    /**
     * Pré-reserva um assento. Chamado pelo PreReservaServico.
     * O versionamento do Assento é controlado pelo JPA via @Version na entidade de infraestrutura.
     */
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

    public Assento bloquearAssento(UUID assentoId) {
        Assento assento = encontrarAssento(assentoId);
        assento.bloquear();
        return assento;
    }

    private Assento encontrarAssento(UUID assentoId) {
        return assentos.stream()
                .filter(a -> a.getId().equals(assentoId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Assento não encontrado."));
    }

    public int capacidade() { return assentos.size(); }

    public List<Assento> getAssentos() { return Collections.unmodifiableList(assentos); }
    public SetorId getId() { return id; }
    public EspacoId getEspacoId() { return espacoId; }
    public String getNome() { return nome; }
    public TipoSetor getTipoSetor() { return tipoSetor; }
    public int getVersao() { return versao; }
}