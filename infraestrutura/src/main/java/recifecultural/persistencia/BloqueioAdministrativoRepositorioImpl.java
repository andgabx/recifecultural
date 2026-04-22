package recifecultural.persistencia;

import org.springframework.stereotype.Component;
import recifecultural.dominio.agenda.bloqueioadministrativo.BloqueioAdministrativo;
import recifecultural.dominio.agenda.bloqueioadministrativo.BloqueioAdministrativoId;
import recifecultural.dominio.agenda.bloqueioadministrativo.IBloqueioAdministrativoRepositorio;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BloqueioAdministrativoRepositorioImpl implements IBloqueioAdministrativoRepositorio {

    private final BloqueioAdministrativoJpaRepositorio repositorio;

    public BloqueioAdministrativoRepositorioImpl(BloqueioAdministrativoJpaRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public boolean salvar(BloqueioAdministrativo bloqueioAdministrativo) {
        BloqueioAdministrativoJpa jpa = new BloqueioAdministrativoJpa(
            bloqueioAdministrativo.getId().valor(),
            bloqueioAdministrativo.getIdEspaco(),
            bloqueioAdministrativo.getMotivo(),
            bloqueioAdministrativo.getDataInicio(),
            bloqueioAdministrativo.getDataFim()
        );
        repositorio.save(jpa);
        return true;
    }

    @Override
    public BloqueioAdministrativo obter(BloqueioAdministrativoId id) {
        return repositorio.findById(id.valor()).map(this::toDomain).orElse(null);
    }

    @Override
    public List<BloqueioAdministrativo> obterTodos() {
        return repositorio.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    private BloqueioAdministrativo toDomain(BloqueioAdministrativoJpa jpa) {
        return new BloqueioAdministrativo(
            new BloqueioAdministrativoId(jpa.getId()),
            jpa.getIdLocal(),
            jpa.getMotivo(),
            jpa.getDataInicio(),
            jpa.getDataFim()
        );
    }
}