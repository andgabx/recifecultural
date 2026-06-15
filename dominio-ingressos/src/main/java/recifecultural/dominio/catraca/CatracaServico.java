package recifecultural.dominio.catraca;

import recifecultural.dominio.catraca.validacoes.*;
import java.time.LocalDateTime;

public class CatracaServico {

    private final ICatracaRepositorio repositorio;
    private final ValidadorAcesso validadorPipeline;

    public CatracaServico(ICatracaRepositorio repositorio) {
        this.repositorio = repositorio;

        // Padrão Decorator: Envolvendo objetos dentro de objetos!
        // A validação passa de fora para dentro até chegar na Base.
        this.validadorPipeline =
                new ValidarEstornoDecorator(
                        new ValidarDuplaEntradaDecorator(
                                new ValidarPortaoDecorator(
                                        new ValidarToleranciaAtrasoDecorator(
                                                new ValidadorAcessoBase()
                                        )
                                )
                        )
                );
    }

    public String validarAcesso(String idIngresso, LocalDateTime horarioAtualDaCatraca, String identificacaoPortao) {
        if (idIngresso == null || idIngresso.isBlank()) {
            throw new IllegalArgumentException("ID do ingresso é obrigatório.");
        }

        IngressoCatraca ingresso = repositorio.buscarPorId(idIngresso);

        if (ingresso == null) {
            throw new IllegalArgumentException("Ingresso não reconhecido pelo sistema.");
        }

        validadorPipeline.validar(ingresso, horarioAtualDaCatraca, identificacaoPortao);

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