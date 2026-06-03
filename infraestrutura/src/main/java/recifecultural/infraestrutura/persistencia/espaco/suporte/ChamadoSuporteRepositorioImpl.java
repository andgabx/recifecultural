package recifecultural.infraestrutura.persistencia.espaco.suporte;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Repository;

import recifecultural.dominio.espaco.suporte.ChamadoSuporte;
import recifecultural.dominio.espaco.suporte.IChamadoSuporteRepositorio;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ChamadoSuporteRepositorioImpl implements IChamadoSuporteRepositorio {

    private final ChamadoSuporteJpaRepository jpa;
    private final ModelMapper mapeador;

    public ChamadoSuporteRepositorioImpl(ChamadoSuporteJpaRepository jpa, ModelMapper mapeador) {
        this.jpa = jpa;
        this.mapeador = mapeador;
    }

    @Override
    public void salvar(ChamadoSuporte chamado) {
        jpa.save(mapeador.map(chamado, ChamadoSuporteJpa.class));
    }

    @Override
    public void atualizar(ChamadoSuporte chamado) {
        jpa.save(mapeador.map(chamado, ChamadoSuporteJpa.class));
    }

    @Override
    public Optional<ChamadoSuporte> obterPorId(UUID id) {
        return jpa.findById(id).map(c -> mapeador.map(c, ChamadoSuporte.class));
    }

    @Override
    public List<ChamadoSuporte> listarAbertosAntesDe(LocalDateTime limite) {
        return jpa.findAbertosAntesDe(limite).stream()
                .map(c -> mapeador.map(c, ChamadoSuporte.class))
                .toList();
    }
}
