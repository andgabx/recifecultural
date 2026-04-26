package recifecultural.dominio.ingressos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface IIngressoRepositorio {

    void salvar(Ingresso ingresso);

    Ingresso buscarPorId(IngressoId id);

    Ingresso buscarPorCodigoQr(String codigoQr);

    int contarAtivosPorApresentacao(UUID eventoId, LocalDateTime dataHora);

    List<Ingresso> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim);
}
