package recifecultural.infraestrutura.persistencia.agenda.acessibilidade;

import jakarta.persistence.*;

import recifecultural.dominio.agenda.acessibilidade.StatusRecurso;
import recifecultural.dominio.agenda.acessibilidade.TipoRecursoAcessibilidade;

import java.util.UUID;

@Entity
@Table(name = "recurso_acessibilidade")
public class RecursoAcessibilidadeJpa {
    @Id
    UUID id;
    UUID apresentacaoId;
    UUID eventoId;
    @Enumerated(EnumType.STRING)
    TipoRecursoAcessibilidade tipo;
    @Enumerated(EnumType.STRING)
    StatusRecurso status;
    String justificativaRemocao;
}
