package recifecultural.dominio.ingressos;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class IngressoRepositorioEmMemoria implements IIngressoRepositorio {

    private final Map<IngressoId, Ingresso> ingressos = new HashMap<>();

    @Override
    public void salvar(Ingresso ingresso) {
        ingressos.put(ingresso.getId(), ingresso);
    }

    @Override
    public Ingresso buscarPorId(IngressoId id) {
        return ingressos.get(id);
    }

    @Override
    public Ingresso buscarPorCodigoQr(String codigoQr) {
        return ingressos.values().stream()
                .filter(i -> i.getCodigoQr().equals(codigoQr))
                .findFirst()
                .orElse(null);
    }

    @Override
    public int contarAtivosPorApresentacao(UUID eventoId, LocalDateTime dataHora) {
        return (int) ingressos.values().stream()
                .filter(i -> i.getEventoId().equals(eventoId)
                        && i.getDataHoraApresentacao().equals(dataHora)
                        && i.getStatus() == StatusIngresso.ATIVO)
                .count();
    }

    @Override
    public List<Ingresso> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return ingressos.values().stream()
                .filter(i -> !i.getDataCompra().isBefore(inicio)
                        && !i.getDataCompra().isAfter(fim))
                .toList();
    }
}
