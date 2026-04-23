package recifecultural.dominio.ingressos;

import java.time.LocalDateTime;
import java.util.UUID;

public interface IIngressoRepositorio {

    void salvar(Ingresso ingresso);

    Ingresso buscarPorId(IngressoId id);

    Ingresso buscarPorCodigoQr(String codigoQr);

    int contarAtivosPorApresentacao(UUID eventoId, LocalDateTime dataHora);
}
