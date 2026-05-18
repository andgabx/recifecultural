package recifecultural.apresentacao.bff.setor;

import java.util.UUID;

public class SetorTelas {
    public record ConfigurarSetorRequisicao(
            UUID espacoId,
            String nome,
            String tipoSetor,
            int fileirasHorizontais,
            int assentosPorFileiraVertical) {}

    public record EditarSetorRequisicao(
            String nome,
            String tipoSetor,
            int fileirasHorizontais,
            int assentosPorFileiraVertical) {}
}
