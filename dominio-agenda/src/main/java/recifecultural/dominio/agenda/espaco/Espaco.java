package recifecultural.dominio.agenda.espaco;


import java.util.ArrayList;
import java.util.List;
import recifecultural.dominio.agenda.espaco.EspacoId;

public class Espaco {
    private final EspacoId id;
    private String nome;
    private int capacidadeMaxima;
    private List<String> riderTecnico;
    private StatusEspaco status;

    public Espaco(String nome, int capacidadeMaxima, List<String> riderTecnico) {
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome é obrigatório.");
        if (capacidadeMaxima <= 0) throw new IllegalArgumentException("A capacidade deve ser maior que zero.");

        this.id = EspacoId.novo();
        this.nome = nome;
        this.capacidadeMaxima = capacidadeMaxima;
        this.riderTecnico = riderTecnico != null ? new ArrayList<>(riderTecnico) : new ArrayList<>();
        this.status = StatusEspaco.ATIVO;

    }

    public EspacoId getId() {
        return id;
    }

    public void reduzirCapacidade(int novaCapacidade, int maiorCargaIngressosVendidosFuturo) {
        if (novaCapacidade <= 0) {
            throw new IllegalArgumentException("A capacidade não pode ser zero ou negativa.");
        }
        if (novaCapacidade < maiorCargaIngressosVendidosFuturo) {
            throw new IllegalStateException(
                    "Conflito Crítico: A nova capacidade (" + novaCapacidade +
                            ") é menor que a carga de ingressos já vendidos (" + maiorCargaIngressosVendidosFuturo +
                            ") para eventos futuros. Realize o remanejamento antes de atualizar."
            );
        }
        this.capacidadeMaxima = novaCapacidade;
    }

    public void interditar() {
        if (this.status == StatusEspaco.INTERDITADO) {
            throw new IllegalStateException("O espaço já está interditado.");
        }
        this.status = StatusEspaco.INTERDITADO;
    }

    private List<Ocupacao> ocupacoesExistentes = new ArrayList<>();

    public void validarDisponibilidade(Ocupacao novaOcupacao, List<Ocupacao> ocupacoesNoPeriodo) {
        if (this.status == StatusEspaco.INTERDITADO) {
            throw new IllegalStateException("Espaço interditado não aceita novas pautas.");
        }

        for (Ocupacao existente : ocupacoesNoPeriodo) {
            if (novaOcupacao.sobrepoe(existente)) {
                throw new IllegalStateException(
                        String.format("Conflito de Horário: O intervalo efetivo (%s - %s) sobrepõe uma pauta existente.",
                                novaOcupacao.inicioEfetivo(), novaOcupacao.fimEfetivo())
                );
            }
        }
    }

    public String getNome() { return nome; }
    public int getCapacidadeMaxima() { return capacidadeMaxima; }
    public StatusEspaco getStatus() { return status; }
}
