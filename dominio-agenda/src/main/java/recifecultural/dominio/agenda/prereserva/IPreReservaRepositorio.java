package recifecultural.dominio.agenda.prereserva;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IPreReservaRepositorio {
    void salvar(PreReserva preReserva);
    void atualizar(PreReserva preReserva);
    Optional<PreReserva> obterPorId(PreReservaId id);
    List<PreReserva> listarAtivasPorAssento(UUID assentoId);
    List<PreReserva> listarAtivasExpiradas(LocalDateTime agora);
}