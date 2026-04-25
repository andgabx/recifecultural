package recifecultural.dominio.artista.produtor;

public record Cnpj(String valor) {

    public Cnpj {
        if (valor == null || valor.isBlank()) throw new IllegalArgumentException("CNPJ é obrigatório.");
        String normalizado = valor.replaceAll("[.\\-/]", "");
        if (!normalizado.matches("\\d{14}")) throw new IllegalArgumentException("CNPJ inválido: " + valor);
        if (!digitosValidos(normalizado)) throw new IllegalArgumentException("CNPJ com dígitos verificadores inválidos.");
        valor = normalizado;
    }

    private static boolean digitosValidos(String cnpj) {
        int[] pesos1 = {5,4,3,2,9,8,7,6,5,4,3,2};
        int[] pesos2 = {6,5,4,3,2,9,8,7,6,5,4,3,2};
        return calcularDigito(cnpj, pesos1) == Character.getNumericValue(cnpj.charAt(12))
                && calcularDigito(cnpj, pesos2) == Character.getNumericValue(cnpj.charAt(13));
    }

    private static int calcularDigito(String cnpj, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < pesos.length; i++) soma += Character.getNumericValue(cnpj.charAt(i)) * pesos[i];
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    public String formatado() {
        return valor.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
    }
}