package recifecultural.dominio.ingressos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface IIngressoRepositorio {

    void salvar(Ingresso ingresso);

    Ingresso buscarPorId(IngressoId id);

    Ingresso buscarPorCodigoQr(String codigoQr);

    int contarAtivosPorApresentacao(UUID eventoId, LocalDateTime dataHora);

    List<Ingresso> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim);

    Set<UUID> buscarAssentosOcupadosPorEvento(UUID eventoId);

    /**
     * Retorna a maior quantidade de ingressos ATIVO vendidos em qualquer apresentação
     * futura de qualquer evento agendado no espaço informado.
     * <p>
     * Usado para validar a redução de capacidade server-side: a nova capacidade não pode
     * ser menor que esse valor, pois existem ingressos já emitidos para apresentações futuras.
     *
     * @param espacoId ID do espaço (localId no evento)
     * @param agora    instante de referência; apenas apresentações após este instante são consideradas
     * @return maior contagem de ingressos ATIVO por apresentação futura, ou 0 se não houver nenhum
     */
    int maiorCargaAtivosPorEspaco(UUID espacoId, LocalDateTime agora);
}
