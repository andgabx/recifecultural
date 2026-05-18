package recifecultural.dominio.agenda.sorteio;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

public class Sorteio {
    private final UUID id;
    private final UUID apresentacaoId;
    private final UUID eventoId;
    private final int vagas;
    private final LocalDateTime prazoInscricao;
    private final LocalDateTime dataApresentacao;
    private StatusSorteio status;
    private final List<Inscricao> inscricoes;
    private final Random random;

    public Sorteio(UUID apresentacaoId, UUID eventoId, int vagas,
                   LocalDateTime prazoInscricao, LocalDateTime dataApresentacao) {
        this(apresentacaoId, eventoId, vagas, prazoInscricao, dataApresentacao, new Random());
    }

    public Sorteio(UUID apresentacaoId, UUID eventoId, int vagas,
                   LocalDateTime prazoInscricao, LocalDateTime dataApresentacao, Random random) {
        if (apresentacaoId == null) throw new IllegalArgumentException("Apresentação é obrigatória.");
        if (eventoId == null) throw new IllegalArgumentException("Evento é obrigatório.");
        if (vagas <= 0) throw new IllegalArgumentException("Número de vagas deve ser positivo.");
        if (prazoInscricao == null) throw new IllegalArgumentException("Prazo de inscrição é obrigatório.");
        if (dataApresentacao == null) throw new IllegalArgumentException("Data da apresentação é obrigatória.");
        if (!prazoInscricao.isBefore(dataApresentacao))
            throw new IllegalArgumentException("Prazo de inscrição deve ser anterior à data da apresentação.");

        this.id = UUID.randomUUID();
        this.apresentacaoId = apresentacaoId;
        this.eventoId = eventoId;
        this.vagas = vagas;
        this.prazoInscricao = prazoInscricao;
        this.dataApresentacao = dataApresentacao;
        this.status = StatusSorteio.INSCRICOES_ABERTAS;
        this.inscricoes = new ArrayList<>();
        this.random = random;
    }

    /** Reconstruction constructor — preserva ID, status e inscricoes ao recarregar do banco. */
    public Sorteio(UUID id, UUID apresentacaoId, UUID eventoId, int vagas,
                   LocalDateTime prazoInscricao, LocalDateTime dataApresentacao,
                   StatusSorteio status, List<Inscricao> inscricoes) {
        if (id == null) throw new IllegalArgumentException("ID é obrigatório.");
        if (apresentacaoId == null) throw new IllegalArgumentException("Apresentação é obrigatória.");
        if (eventoId == null) throw new IllegalArgumentException("Evento é obrigatório.");

        this.id = id;
        this.apresentacaoId = apresentacaoId;
        this.eventoId = eventoId;
        this.vagas = vagas;
        this.prazoInscricao = prazoInscricao;
        this.dataApresentacao = dataApresentacao;
        this.status = status != null ? status : StatusSorteio.INSCRICOES_ABERTAS;
        this.inscricoes = inscricoes != null ? new ArrayList<>(inscricoes) : new ArrayList<>();
        this.random = new Random();
    }

    public void inscrever(UUID espectadorId) {
        if (status != StatusSorteio.INSCRICOES_ABERTAS)
            throw new IllegalStateException("Sorteio não está com inscrições abertas.");
        if (jaInscrito(espectadorId))
            throw new IllegalStateException("Espectador já inscrito neste sorteio.");
        inscricoes.add(new Inscricao(espectadorId, LocalDateTime.now()));
    }

    public ConcluidoEvento apurar() {
        if (status != StatusSorteio.INSCRICOES_ABERTAS)
            throw new IllegalStateException("Sorteio não pode ser apurado no estado " + status + ".");

        status = StatusSorteio.EM_APURACAO;

        List<Inscricao> embaralhadas = new ArrayList<>(inscricoes);
        Collections.shuffle(embaralhadas, random);
        for (int i = 0; i < embaralhadas.size(); i++) {
            embaralhadas.get(i).marcarComo(i < vagas ? StatusInscricao.GANHADOR : StatusInscricao.SUPLENTE);
        }

        status = StatusSorteio.CONCLUIDO;
        return new ConcluidoEvento(this);
    }

    public Optional<SuplentePromovidoEvento> desistir(UUID espectadorId) {
        if (status != StatusSorteio.CONCLUIDO)
            throw new IllegalStateException("Só é possível desistir após a apuração.");

        Inscricao alvo = inscricoes.stream()
                .filter(i -> i.getEspectadorId().equals(espectadorId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Espectador não inscrito neste sorteio."));

        if (alvo.getStatus() != StatusInscricao.GANHADOR)
            throw new IllegalStateException("Apenas ganhadores podem desistir.");

        alvo.marcarComo(StatusInscricao.DESISTENTE);

        return inscricoes.stream()
                .filter(i -> i.getStatus() == StatusInscricao.SUPLENTE)
                .findFirst()
                .map(suplente -> {
                    suplente.marcarComo(StatusInscricao.GANHADOR);
                    return new SuplentePromovidoEvento(this, suplente.getEspectadorId(), espectadorId);
                });
    }

    public CanceladoEvento cancelar() {
        if (status == StatusSorteio.CANCELADO) {
            return new CanceladoEvento(this);
        }
        status = StatusSorteio.CANCELADO;
        inscricoes.forEach(i -> i.marcarComo(StatusInscricao.CANCELADA));
        return new CanceladoEvento(this);
    }

    public long contarPorStatus(StatusInscricao statusInscricao) {
        return inscricoes.stream().filter(i -> i.getStatus() == statusInscricao).count();
    }

    private boolean jaInscrito(UUID espectadorId) {
        return inscricoes.stream().anyMatch(i -> i.getEspectadorId().equals(espectadorId));
    }

    public UUID getId() { return id; }
    public UUID getApresentacaoId() { return apresentacaoId; }
    public UUID getEventoId() { return eventoId; }
    public int getVagas() { return vagas; }
    public LocalDateTime getPrazoInscricao() { return prazoInscricao; }
    public LocalDateTime getDataApresentacao() { return dataApresentacao; }
    public StatusSorteio getStatus() { return status; }
    public List<Inscricao> getInscricoes() { return Collections.unmodifiableList(inscricoes); }

    public static class SorteioEvento {
        private final Sorteio sorteio;

        private SorteioEvento(Sorteio sorteio) {
            this.sorteio = sorteio;
        }

        public Sorteio getSorteio() {
            return sorteio;
        }
    }

    public static class ConcluidoEvento extends SorteioEvento {
        private ConcluidoEvento(Sorteio sorteio) {
            super(sorteio);
        }
    }

    public static class CanceladoEvento extends SorteioEvento {
        private CanceladoEvento(Sorteio sorteio) {
            super(sorteio);
        }
    }

    public static class SuplentePromovidoEvento extends SorteioEvento {
        private final UUID espectadorPromovidoId;
        private final UUID espectadorDesistenteId;

        private SuplentePromovidoEvento(Sorteio sorteio, UUID promovidoId, UUID desistenteId) {
            super(sorteio);
            this.espectadorPromovidoId = promovidoId;
            this.espectadorDesistenteId = desistenteId;
        }

        public UUID getEspectadorPromovidoId() {
            return espectadorPromovidoId;
        }

        public UUID getEspectadorDesistenteId() {
            return espectadorDesistenteId;
        }
    }
}
