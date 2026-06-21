package recifecultural.dominio.espaco.suporte;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IChamadoSuporteRepositorio {
    void salvar(ChamadoSuporte chamado);
    void atualizar(ChamadoSuporte chamado);
    Optional<ChamadoSuporte> obterPorId(UUID id);
    List<ChamadoSuporte> listarAbertosAntesDe(LocalDateTime limite);
}