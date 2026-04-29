package recifecultural.dominio.catraca;

import java.time.LocalDateTime;

public class CatracaServico {

    private final ICatracaRepositorio repositorio;

    public CatracaServico(ICatracaRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public String validarAcesso(String idIngresso, LocalDateTime horarioAtualDaCatraca, String identificacaoPortao) {
        IngressoCatraca ingresso = repositorio.buscarPorId(idIngresso);

        if (ingresso == null) {
            throw new IllegalArgumentException("Ingresso não reconhecido pelo sistema.");
        }

        ingresso.registrarEntrada(horarioAtualDaCatraca, identificacaoPortao);

        repositorio.salvar(ingresso);

        return "ACESSO LIBERADO. Catraca destravada com sucesso.";
    }
    public void inativarIngresso(String idIngresso) {
        IngressoCatraca ingresso = repositorio.buscarPorId(idIngresso);

        if (ingresso != null) {
            ingresso.cancelarAcesso();
            repositorio.salvar(ingresso);
        }
    }
}
