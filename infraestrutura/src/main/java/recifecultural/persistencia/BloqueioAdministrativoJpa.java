package recifecultural.persistencia;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bloqueio_administrativo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BloqueioAdministrativoJpa {

    @Id
    private UUID id;
    private UUID idLocal;
    private String motivo;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
}