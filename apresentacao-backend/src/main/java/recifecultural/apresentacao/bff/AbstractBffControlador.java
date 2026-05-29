package recifecultural.apresentacao.bff;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

/**
 * Padrão Template Method: define o esqueleto de uma resposta BFF padronizada.
 * Subclasses herdam o tratamento de erros e o formato de resposta sem redefinir a estrutura.
 */
public abstract class AbstractBffControlador {

    protected <T> ResponseEntity<T> responder(T corpo) {
        if (corpo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(corpo);
    }

    protected ResponseEntity<Map<String, String>> responderCriado(String id) {
        return ResponseEntity.status(201).body(Map.of("id", id));
    }

    protected ResponseEntity<Map<String, String>> responderSemConteudo() {
        return ResponseEntity.noContent().build();
    }

    // Template Method: subclasses podem sobrescrever o tratamento de erros de negócio
    @ExceptionHandler(IllegalStateException.class)
    protected ResponseEntity<Map<String, String>> tratarErroNegocio(IllegalStateException e) {
        return erroNegocio(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<Map<String, String>> tratarErroValidacao(IllegalArgumentException e) {
        return erroDadosInvalidos(e.getMessage());
    }

    protected ResponseEntity<Map<String, String>> erroNegocio(String mensagem) {
        return ResponseEntity.unprocessableEntity().body(Map.of("erro", mensagem));
    }

    protected ResponseEntity<Map<String, String>> erroDadosInvalidos(String mensagem) {
        return ResponseEntity.badRequest().body(Map.of("erro", mensagem));
    }
}
