package recifecultural.dominio.catraca;

import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@AllArgsConstructor
public class CatracaServico {

    private final ICatracaRepositorio repositorio;

    public String validarAcesso(String idIngresso, LocalDateTime horarioAtualDaCatraca, String identificacaoPortao) {
        IngressoCatraca ingresso = repositorio.buscarPorId(idIngresso);

        if (ingresso == null) {
            throw new IllegalArgumentException("Ingresso não reconhecido pelo sistema.");
        }

        ingresso.registrarEntrada(horarioAtualDaCatraca, identificacaoPortao);

        repositorio.salvar(ingresso);

        return "ACESSO LIBERADO. Catraca destravada com sucesso.";
    }
}