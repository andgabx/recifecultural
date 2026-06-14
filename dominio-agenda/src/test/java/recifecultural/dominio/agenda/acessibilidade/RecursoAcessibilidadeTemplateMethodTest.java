package recifecultural.dominio.agenda.acessibilidade;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecursoAcessibilidadeTemplateMethodTest {

    @Test
    void remocaoDeRecursoEhUmaOperacaoTemplateMethod() throws Exception {
        Class<?> template = Class.forName(
                "recifecultural.dominio.agenda.acessibilidade.OperacaoRecursoAcessibilidadeTemplate");
        Class<?> remocao = Class.forName(
                "recifecultural.dominio.agenda.acessibilidade.RemocaoRecursoAcessibilidadeOperacao");

        assertTrue(template.isAssignableFrom(remocao));

        var executar = template.getDeclaredMethod("executar");
        assertTrue(Modifier.isFinal(executar.getModifiers()));
        assertFalse(Modifier.isAbstract(executar.getModifiers()));
    }
}
