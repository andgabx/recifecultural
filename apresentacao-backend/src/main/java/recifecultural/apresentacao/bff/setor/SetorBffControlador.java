package recifecultural.apresentacao.bff.setor;

import org.springframework.web.bind.annotation.*;
import recifecultural.aplicacao.ingressos.IngressoRepositorioAplicacao;
import recifecultural.aplicacao.ingressos.IngressoResumo;
import recifecultural.apresentacao.bff.AbstractBffControlador;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bff/setores")
public class SetorBffControlador extends AbstractBffControlador {

    private final IngressoRepositorioAplicacao ingressoRepositorio;

    public SetorBffControlador(IngressoRepositorioAplicacao ingressoRepositorio) {
        this.ingressoRepositorio = ingressoRepositorio;
    }

    // Mock simples para simular setores do espaço
    private List<SetorResumoMock> obterSetoresMock(String espacoId) {
        if ("espaco-1".equals(espacoId)) {
            return List.of(
                    new SetorResumoMock("setor-1", "Pista", false, 1000, 0, 0),
                    new SetorResumoMock("setor-2", "Camarote", true, 50, 5, 10)
            );
        }
        return List.of(new SetorResumoMock("setor-default", "Entrada Única", false, 500, 0, 0));
    }

    @GetMapping("/evento/{eventoId}/espaco/{espacoId}")
    public List<SetorComAssentos> buscarSetoresEvento(
            @PathVariable String eventoId,
            @PathVariable String espacoId) {

        List<SetorResumoMock> setores = obterSetoresMock(espacoId);

        return setores.stream().map(setor -> {
            SetorComAssentos dto = new SetorComAssentos();
            dto.setId(setor.id);
            dto.setNome(setor.nome);
            dto.setTemLugaresMarcados(setor.temLugaresMarcados);
            dto.setCapacidade(setor.capacidade);

            if (setor.temLugaresMarcados) {
                // CORREÇÃO AQUI: Buscar ingressos do setor APENAS para este evento específico
                List<IngressoResumo> ingressosDoSetorNoEvento = ingressoRepositorio.buscarPorEventoESetor(eventoId, setor.id);

                // Mapeia os assentos ocupados
                List<String> assentosOcupados = ingressosDoSetorNoEvento.stream()
                        .filter(i -> i.getAssento() != null)
                        .map(IngressoResumo::getAssento)
                        .toList();

                List<AssentoResumo> assentos = gerarAssentos(setor.filas, setor.cadeirasPorFila, assentosOcupados);
                dto.setAssentos(assentos);
            }
            return dto;
        }).toList();
    }

    private List<AssentoResumo> gerarAssentos(int filas, int cadeirasPorFila, List<String> assentosOcupados) {
        List<AssentoResumo> lista = new ArrayList<>();
        char filaChar = 'A';
        for (int i = 0; i < filas; i++) {
            for (int j = 1; j <= cadeirasPorFila; j++) {
                String codigo = String.valueOf(filaChar) + j;
                boolean ocupado = assentosOcupados.contains(codigo);
                lista.add(new AssentoResumo(codigo, !ocupado));
            }
            filaChar++;
        }
        return lista;
    }

    public static class SetorComAssentos {
        private String id;
        private String nome;
        private boolean temLugaresMarcados;
        private int capacidade;
        private List<AssentoResumo> assentos;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public boolean isTemLugaresMarcados() { return temLugaresMarcados; }
        public void setTemLugaresMarcados(boolean temLugaresMarcados) { this.temLugaresMarcados = temLugaresMarcados; }
        public int getCapacidade() { return capacidade; }
        public void setCapacidade(int capacidade) { this.capacidade = capacidade; }
        public List<AssentoResumo> getAssentos() { return assentos; }
        public void setAssentos(List<AssentoResumo> assentos) { this.assentos = assentos; }
    }

    public static class AssentoResumo {
        private String codigo;
        private boolean disponivel;

        public AssentoResumo(String codigo, boolean disponivel) {
            this.codigo = codigo;
            this.disponivel = disponivel;
        }
        public String getCodigo() { return codigo; }
        public boolean isDisponivel() { return disponivel; }
    }

    private record SetorResumoMock(String id, String nome, boolean temLugaresMarcados, int capacidade, int filas, int cadeirasPorFila) {}
}