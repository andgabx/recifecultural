package recifecultural.infraestrutura.persistencia.agenda.prereserva;

import org.springframework.stereotype.Repository;

import recifecultural.dominio.agenda.prereserva.IPreReservaRepositorio;
import recifecultural.dominio.agenda.prereserva.PreReserva;
import recifecultural.dominio.agenda.prereserva.PreReservaId;
import recifecultural.infraestrutura.persistencia.jpa.JpaMapeador;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PreReservaRepositorioImpl implements IPreReservaRepositorio {

    private final PreReservaJpaRepository jpa;
    private final JpaMapeador mapeador;

    public PreReservaRepositorioImpl(PreReservaJpaRepository jpa, JpaMapeador mapeador) {
        this.jpa = jpa;
        this.mapeador = mapeador;
    }

    @Override
    public void salvar(PreReserva preReserva) {
        jpa.save(mapeador.map(preReserva, PreReservaJpa.class));
    }

    @Override
    public void atualizar(PreReserva preReserva) {
        jpa.save(mapeador.map(preReserva, PreReservaJpa.class));
    }

    @Override
    public Optional<PreReserva> obterPorId(PreReservaId id) {
        return jpa.findById(id.valor()).map(p -> mapeador.map(p, PreReserva.class));
    }

    @Override
    public List<PreReserva> listarAtivasPorAssento(UUID assentoId) {
        return jpa.findAtivasPorAssento(assentoId).stream()
                .map(p -> mapeador.map(p, PreReserva.class))
                .toList();
    }

    @Override
    public List<PreReserva> listarAtivasExpiradas(LocalDateTime agora) {
        return jpa.findAtivasExpiradas(agora).stream()
                .map(p -> mapeador.map(p, PreReserva.class))
                .toList();
    }
}
