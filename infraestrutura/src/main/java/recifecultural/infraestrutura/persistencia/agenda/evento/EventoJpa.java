package recifecultural.infraestrutura.persistencia.agenda.evento;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import recifecultural.dominio.agenda.evento.StatusEvento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "evento")
public class EventoJpa {
    @Id
    UUID id;
    UUID promotorId;
    UUID localId;
    String titulo;
    String descricaoCurta;
    @Column(columnDefinition = "TEXT")
    String descricaoLonga;
    LocalDateTime periodoInicio;
    LocalDateTime periodoFim;
    String categoria;
    @Enumerated(EnumType.STRING)
    StatusEvento status;
    BigDecimal precoInteira;
    BigDecimal precoMeia;
    BigDecimal precoSocial;
    LocalDateTime dataAprovacao;
    LocalDateTime dataReprovacao;
    boolean requerRevisaoAdicional;
    String motivoCancelamento;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "evento_apresentacao", joinColumns = @JoinColumn(name = "evento_id"))
    @Column(name = "data_hora")
    List<LocalDateTime> datasApresentacao = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "evento_artista", joinColumns = @JoinColumn(name = "evento_id"))
    @Column(name = "artista_id")
    List<UUID> artistas = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "evento_rider_items", joinColumns = @JoinColumn(name = "evento_id"))
    List<RiderItemJpa> riderItems = new ArrayList<>();

    public List<RiderItemJpa> getRiderItems() {
        return riderItems;
    }

    public void setRiderItems(List<RiderItemJpa> riderItems) {
        this.riderItems = riderItems;
    }

    @Embeddable
    public static class RiderItemJpa {
        @Column(name = "nome_equipamento")
        private String nomeEquipamento;

        @Column(name = "quantidade")
        private int quantidade;

        public RiderItemJpa() {}

        public RiderItemJpa(String nomeEquipamento, int quantidade) {
            this.nomeEquipamento = nomeEquipamento;
            this.quantidade = quantidade;
        }

        public String getNomeEquipamento() {
            return nomeEquipamento;
        }

        public void setNomeEquipamento(String nomeEquipamento) {
            this.nomeEquipamento = nomeEquipamento;
        }

        public int getQuantidade() {
            return quantidade;
        }

        public void setQuantidade(int quantidade) {
            this.quantidade = quantidade;
        }
    }
}

interface EventoJpaRepository extends JpaRepository<EventoJpa, UUID> {
    List<EventoJpa> findByStatus(StatusEvento status);

    @Query("SELECT e FROM EventoJpa e WHERE e.localId = :localId AND e.periodoInicio < :fim AND e.periodoFim > :inicio")
    List<EventoJpa> findByLocalEIntervalo(UUID localId, LocalDateTime inicio, LocalDateTime fim);

    List<EventoJpa> findByPromotorId(UUID promotorId);

    @Query("SELECT e FROM EventoJpa e WHERE e.promotorId = :promotorId AND e.status = 'REPROVADO'")
    List<EventoJpa> findReprovacoesPorPromotor(UUID promotorId);

    @Query("SELECT e FROM EventoJpa e WHERE e.promotorId = :promotorId AND e.status IN ('APROVADO', 'REPROVADO')")
    List<EventoJpa> findFinalizadosPorPromotor(UUID promotorId);

    @Query("SELECT e FROM EventoJpa e WHERE e.promotorId = :promotorId AND e.status = 'APROVADO'")
    List<EventoJpa> findAprovadosPorPromotor(UUID promotorId);
}
