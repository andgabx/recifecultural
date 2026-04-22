package recifecultural.persistencia;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bloqueio_administrativo")
public class BloqueioAdministrativoJpa {

    @Id
    private UUID id;
    private UUID idLocal;
    private String motivo;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;

    // Construtor vazio exigido pelo JPA
    public BloqueioAdministrativoJpa() {
    }

    // Construtor com todos os argumentos
    public BloqueioAdministrativoJpa(UUID id, UUID idLocal, String motivo, LocalDateTime dataInicio, LocalDateTime dataFim) {
        this.id = id;
        this.idLocal = idLocal;
        this.motivo = motivo;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }

    // Getters e Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(UUID idLocal) {
        this.idLocal = idLocal;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDateTime getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDateTime dataFim) {
        this.dataFim = dataFim;
    }
}