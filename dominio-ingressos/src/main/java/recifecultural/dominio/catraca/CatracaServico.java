package recifecultural.dominio.catraca;

import recifecultural.dominio.catraca.validacoes.*;
import java.time.LocalDateTime;
import java.util.List;

public class CatracaServico {

    private final ICatracaRepositorio repositorio;
    private final List<ValidacaoAcessoStrategy> validacoes;

    public CatracaServico(ICatracaRepositorio repositorio) {
        this.repositorio = repositorio;
        this.validacoes = List.of(
                new ValidarEstornoStrategy(),
                new ValidarDuplaEntradaStrategy(),
                new ValidarPortaoStrategy(),
                new ValidarToleranciaAtrasoStrategy()
        );
    }

    public String validarAcesso(String idIngresso, LocalDateTime horarioAtualDaCatraca, String identificacaoPortao) {
        IngressoCatraca ingresso = repositorio.buscarPorId(idIngresso);

        if (ingresso == null) {
            throw new IllegalArgumentException("Ingresso não reconhecido pelo sistema.");
        }

        for (ValidacaoAcessoStrategy validacao : validacoes) {
            validacao.validar(ingresso, horarioAtualDaCatraca, identificacaoPortao);
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