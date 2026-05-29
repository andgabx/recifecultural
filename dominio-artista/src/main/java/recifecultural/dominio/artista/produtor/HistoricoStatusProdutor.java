package recifecultural.dominio.artista.produtor;

import java.time.LocalDateTime;
import java.util.UUID;

public class HistoricoStatusProdutor {

    private final UUID id;
    private final ProdutorId produtorId;
    private final StatusProdutor statusAnterior;
    private final StatusProdutor statusNovo;
    private final String responsavel;
    private final String motivo;
    private final LocalDateTime dataAlteracao;

    public HistoricoStatusProdutor(ProdutorId produtorId,
                                   StatusProdutor statusAnterior,
                                   StatusProdutor statusNovo,
                                   String responsavel,
                                   String motivo) {
        if (produtorId == null) throw new IllegalArgumentException("ID do produtor é obrigatório.");
        if (statusNovo == null) throw new IllegalArgumentException("Status novo é obrigatório.");
        if (responsavel == null || responsavel.isBlank())
            throw new IllegalArgumentException("Responsável pela ação é obrigatório.");
        this.id = UUID.randomUUID();
        this.produtorId = produtorId;
        this.statusAnterior = statusAnterior;
        this.statusNovo = statusNovo;
        this.responsavel = responsavel;
        this.motivo = motivo;
        this.dataAlteracao = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public ProdutorId getProdutorId() { return produtorId; }
    public StatusProdutor getStatusAnterior() { return statusAnterior; }
    public StatusProdutor getStatusNovo() { return statusNovo; }
    public String getResponsavel() { return responsavel; }
    public String getMotivo() { return motivo; }
    public LocalDateTime getDataAlteracao() { return dataAlteracao; }
}
