package recifecultural.infraestrutura.persistencia.compartilhado.auditoria;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import recifecultural.dominio.compartilhado.auditoria.IAuditoriaRepositorio;
import recifecultural.dominio.compartilhado.auditoria.RegistroAuditoria;

import java.util.List;

@Repository
public class AuditoriaRepositorioImpl implements IAuditoriaRepositorio {

    private final AuditoriaJpaRepository jpa;
    private final ModelMapper mapeador;

    public AuditoriaRepositorioImpl(AuditoriaJpaRepository jpa, ModelMapper mapeador) {
        this.jpa = jpa;
        this.mapeador = mapeador;
    }

    @Override
    public void registrar(RegistroAuditoria registro) {
        jpa.save(mapeador.map(registro, AuditoriaJpa.class));
    }

    @Override
    public List<RegistroAuditoria> listarRecentes(int limite) {
        return jpa.findAllByOrderByMomentoDesc(PageRequest.of(0, Math.max(1, limite))).stream()
                .map(j -> mapeador.map(j, RegistroAuditoria.class))
                .toList();
    }

    @Override
    public List<RegistroAuditoria> listarPorEntidade(String entidade, int limite) {
        return jpa.findByEntidadeOrderByMomentoDesc(entidade, PageRequest.of(0, Math.max(1, limite))).stream()
                .map(j -> mapeador.map(j, RegistroAuditoria.class))
                .toList();
    }
}
