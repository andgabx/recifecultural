package recifecultural.infraestrutura.persistencia.jpa;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import recifecultural.dominio.agenda.acessibilidade.RecursoAcessibilidade;
import recifecultural.dominio.agenda.acessibilidade.TipoRecursoAcessibilidade;
import recifecultural.dominio.agenda.acessibilidade.StatusRecurso;
import recifecultural.dominio.agenda.equipamento.Equipamento;
import recifecultural.dominio.agenda.equipamento.EquipamentoId;
import recifecultural.dominio.agenda.equipamento.StatusEquipamento;
import recifecultural.dominio.artista.produtor.HistoricoStatusProdutor;
import recifecultural.dominio.artista.produtor.StatusProdutor;
import recifecultural.dominio.compartilhado.auditoria.AcaoAuditoria;
import recifecultural.dominio.compartilhado.auditoria.RegistroAuditoria;
import recifecultural.dominio.compartilhado.notificacao.Notificacao;
import recifecultural.dominio.compartilhado.notificacao.NotificacaoId;
import recifecultural.dominio.espaco.suporte.ChamadoSuporte;
import recifecultural.dominio.espaco.suporte.StatusChamado;
import recifecultural.dominio.agenda.comentario.Comentario;
import recifecultural.dominio.agenda.comentario.Nota;
import recifecultural.dominio.agenda.comentario.StatusComentario;
import recifecultural.dominio.agenda.prereserva.PreReserva;
import recifecultural.dominio.agenda.prereserva.PreReservaId;
import recifecultural.dominio.agenda.prereserva.StatusPreReserva;
import recifecultural.dominio.agenda.bloqueioadministrativo.BloqueioAdministrativo;
import recifecultural.dominio.agenda.evento.Evento;
import recifecultural.dominio.agenda.evento.Periodo;
import recifecultural.dominio.agenda.evento.Preco;
import recifecultural.dominio.agenda.sorteio.Inscricao;
import recifecultural.dominio.agenda.sorteio.Sorteio;
import recifecultural.dominio.artista.artista.Artista;
import recifecultural.dominio.artista.artista.ArtistaId;
import recifecultural.dominio.artista.produtor.Cnpj;
import recifecultural.dominio.artista.produtor.Produtor;
import recifecultural.dominio.artista.produtor.ProdutorId;
import recifecultural.dominio.catraca.IngressoCatraca;
import recifecultural.dominio.catraca.IngressoCatracaId;
import recifecultural.dominio.cupom.Cupom;
import recifecultural.dominio.cupom.CupomId;
import recifecultural.dominio.espaco.espaco.Espaco;
import recifecultural.dominio.espaco.espaco.EspacoId;
import recifecultural.dominio.espaco.setor.Assento;
import recifecultural.dominio.espaco.setor.Setor;
import recifecultural.dominio.espaco.setor.SetorId;
import recifecultural.dominio.financeiro.Despesa;
import recifecultural.dominio.financeiro.DespesaId;
import recifecultural.dominio.financeiro.OrcamentoId;
import recifecultural.dominio.financeiro.OrcamentoPeriodo;
import recifecultural.dominio.ingressos.Ingresso;
import recifecultural.dominio.ingressos.IngressoId;
import recifecultural.dominio.ingressos.MetodoPagamento;
import recifecultural.dominio.ingressos.TipoIngresso;
import recifecultural.dominio.patrocinio.EventoId;
import recifecultural.dominio.patrocinio.ModalidadeContribuicao;
import recifecultural.dominio.patrocinio.Patrocinio;
import recifecultural.dominio.patrocinio.PatrocinioId;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class JpaMapeador extends ModelMapper {

    public JpaMapeador() {
        configurarConversores();
    }

    private void configurarConversores() {
        // ── JPA → Domain ──────────────────────────────────────────────────────

        addConverter(new AbstractConverter<EventoJpa, Evento>() {
            @Override
            protected Evento convert(EventoJpa s) {
                return new Evento(
                        s.id, s.promotorId, s.localId,
                        s.titulo, s.descricaoCurta, s.descricaoLonga,
                        new Periodo(s.periodoInicio, s.periodoFim),
                        (s.precoInteira != null || s.precoSocial != null)
                                ? new Preco(s.precoInteira, s.precoMeia, s.precoSocial) : null,
                        s.categoria,
                        s.status,
                        s.datasApresentacao,
                        s.artistas,
                        s.dataAprovacao,
                        s.dataReprovacao,
                        s.requerRevisaoAdicional,
                        s.motivoCancelamento
                );
            }
        });

        addConverter(new AbstractConverter<Evento, EventoJpa>() {
            @Override
            protected EventoJpa convert(Evento s) {
                var jpa = new EventoJpa();
                jpa.id = s.getId();
                jpa.promotorId = s.getPromotorId();
                jpa.localId = s.getLocalId();
                jpa.titulo = s.getTitulo();
                jpa.descricaoCurta = s.getDescricaoCurta();
                jpa.descricaoLonga = s.getDescricaoLonga();
                if (s.getPeriodo() != null) {
                    jpa.periodoInicio = s.getPeriodo().getInicio();
                    jpa.periodoFim = s.getPeriodo().getFim();
                }
                jpa.categoria = s.getCategoria();
                jpa.status = s.getStatus();
                if (s.getPreco() != null) {
                    jpa.precoInteira = s.getPreco().getInteira();
                    jpa.precoMeia = s.getPreco().getMeia();
                    jpa.precoSocial = s.getPreco().getSocial();
                }
                jpa.dataAprovacao = s.getDataAprovacao();
                jpa.dataReprovacao = s.getDataReprovacao();
                jpa.requerRevisaoAdicional = s.isRequerRevisaoAdicional();
                jpa.motivoCancelamento = s.getMotivoCancelamento();
                jpa.datasApresentacao = new ArrayList<>(s.getDatasApresentacao());
                jpa.artistas = new ArrayList<>(s.getArtistas());
                return jpa;
            }
        });

        addConverter(new AbstractConverter<IngressoJpa, Ingresso>() {
            @Override
            protected Ingresso convert(IngressoJpa s) {
                return new Ingresso(
                        new IngressoId(s.id),
                        s.eventoId, s.assentoId, s.dataHoraApresentacao,
                        TipoIngresso.valueOf(s.tipo),
                        s.valorPago, s.codigoQr,
                        s.codigoTransacao,
                        MetodoPagamento.valueOf(s.metodoPagamento),
                        s.dataCompra, s.status, s.valorReembolsado
                );
            }
        });

        addConverter(new AbstractConverter<PatrocinioJpa, Patrocinio>() {
            @Override
            protected Patrocinio convert(PatrocinioJpa s) {
                return new Patrocinio(
                        new PatrocinioId(s.id),
                        new EventoId(s.eventoId),
                        s.patrocinadorNome, s.categoriaPatrocinio,
                        recifecultural.dominio.patrocinio.TipoPatrocinio.valueOf(s.tipo),
                        ModalidadeContribuicao.valueOf(s.modalidade),
                        s.valorContribuicao, s.dataEvento,
                        s.status, s.valorReembolsado, s.multaAplicada
                );
            }
        });

        addConverter(new AbstractConverter<Patrocinio, PatrocinioJpa>() {
            @Override
            protected PatrocinioJpa convert(Patrocinio s) {
                var jpa = new PatrocinioJpa();
                jpa.id = s.getId().getValor();
                jpa.eventoId = s.getEventoId().getValor();
                jpa.patrocinadorNome = s.getPatrocinadorNome();
                jpa.categoriaPatrocinio = s.getCategoriaPatrocinio();
                jpa.tipo = s.getTipo().name();
                jpa.modalidade = s.getModalidade().name();
                jpa.valorContribuicao = s.getValorContribuicao();
                jpa.dataEvento = s.getDataEvento();
                jpa.status = s.getStatus();
                jpa.valorReembolsado = s.getValorReembolsado();
                jpa.multaAplicada = s.getMultaAplicada();
                return jpa;
            }
        });

        addConverter(new AbstractConverter<ArtistaJpa, Artista>() {
            @Override
            protected Artista convert(ArtistaJpa s) {
                return new Artista(
                        new ArtistaId(s.id),
                        new ProdutorId(s.produtorId),
                        s.nome, null, s.status
                );
            }
        });

        addConverter(new AbstractConverter<ProdutorJpa, Produtor>() {
            @Override
            protected Produtor convert(ProdutorJpa s) {
                return new Produtor(
                        new ProdutorId(s.id),
                        s.nomeFantasia,
                        s.cnpj != null ? new Cnpj(s.cnpj) : null,
                        s.email, s.telefone, s.status
                );
            }
        });

        addConverter(new AbstractConverter<SetorJpa, Setor>() {
            @Override
            protected Setor convert(SetorJpa s) {
                List<Assento> assentos = s.assentos == null ? List.of() :
                        s.assentos.stream()
                                .map(a -> new Assento(a.id, a.codigo, a.fileira, a.numero,
                                        a.status, a.motivoIndisponibilidade, a.versao))
                                .toList();
                return new Setor(
                        new SetorId(s.id), new EspacoId(s.espacoId),
                        s.nome, s.tipoSetor,
                        s.fileirasHorizontais, s.assentosPorFileiraVertical,
                        assentos, s.versao
                );
            }
        });

        addConverter(new AbstractConverter<EspacoJpa, Espaco>() {
            @Override
            protected Espaco convert(EspacoJpa s) {
                List<String> rider = s.riderTecnico != null ? new ArrayList<>(s.riderTecnico) : List.of();
                return new Espaco(new EspacoId(s.id), s.nome, s.capacidadeMaxima, rider, s.status);
            }
        });

        addConverter(new AbstractConverter<SorteioJpa, Sorteio>() {
            @Override
            protected Sorteio convert(SorteioJpa s) {
                List<Inscricao> inscricoes = s.inscricoes == null ? List.of() :
                        s.inscricoes.stream()
                                .map(i -> new Inscricao(i.espectadorId, i.momentoInscricao, i.status))
                                .toList();
                return new Sorteio(
                        s.id, s.apresentacaoId, s.eventoId, s.vagas,
                        s.prazoInscricao, s.dataApresentacao,
                        s.status, inscricoes
                );
            }
        });

        addConverter(new AbstractConverter<CupomJpa, Cupom>() {
            @Override
            protected Cupom convert(CupomJpa s) {
                return new Cupom(
                        new CupomId(s.id), s.codigo, s.tipoDesconto,
                        s.valorDesconto, s.valorMinimoPedido,
                        s.limiteGlobal, s.limitePorCpf,
                        s.dataInicio, s.dataFim, s.categoriaPermitida
                );
            }
        });

        addConverter(new AbstractConverter<IngressoCatracaJpa, IngressoCatraca>() {
            @Override
            protected IngressoCatraca convert(IngressoCatracaJpa s) {
                return new IngressoCatraca(
                        new IngressoCatracaId(s.id),
                        s.idEvento, s.status,
                        s.horarioInicioEvento, s.tipoIngresso,
                        s.portaoAcesso
                );
            }
        });

        addConverter(new AbstractConverter<OrcamentoPeriodoJpa, OrcamentoPeriodo>() {
            @Override
            protected OrcamentoPeriodo convert(OrcamentoPeriodoJpa s) {
                return new OrcamentoPeriodo(
                        new OrcamentoId(s.id),
                        new recifecultural.dominio.financeiro.Periodo(s.periodoInicio, s.periodoFim),
                        s.valorTotal
                );
            }
        });

        addConverter(new AbstractConverter<DespesaJpa, Despesa>() {
            @Override
            protected Despesa convert(DespesaJpa s) {
                return new Despesa(
                        new DespesaId(s.id),
                        new OrcamentoId(s.orcamentoId),
                        s.descricao, s.valor, s.categoria
                );
            }
        });

        addConverter(new AbstractConverter<BloqueioAdministrativoJpa, BloqueioAdministrativo>() {
            @Override
            protected BloqueioAdministrativo convert(BloqueioAdministrativoJpa s) {
                List<UUID> eventos = (s.eventosCancelados == null || s.eventosCancelados.isBlank())
                        ? new java.util.ArrayList<>()
                        : Arrays.stream(s.eventosCancelados.split(","))
                                .map(UUID::fromString)
                                .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
                return new BloqueioAdministrativo(
                        new recifecultural.dominio.agenda.bloqueioadministrativo.BloqueioAdministrativoId(s.id),
                        new EspacoId(s.espacoId),
                        s.dataInicio, s.dataFim,
                        s.justificativa,
                        s.ativo,
                        eventos
                );
            }
        });

        // ── Domain → JPA ──────────────────────────────────────────────────────

        addConverter(new AbstractConverter<Artista, ArtistaJpa>() {
            @Override
            protected ArtistaJpa convert(Artista s) {
                var jpa = new ArtistaJpa();
                jpa.id = s.getId().valor();
                jpa.produtorId = s.getProdutorId().valor();
                jpa.nome = s.getNome();
                jpa.status = s.getStatus();
                return jpa;
            }
        });

        addConverter(new AbstractConverter<Produtor, ProdutorJpa>() {
            @Override
            protected ProdutorJpa convert(Produtor s) {
                var jpa = new ProdutorJpa();
                jpa.id = s.getId().valor();
                jpa.nomeFantasia = s.getNomeFantasia();
                jpa.cnpj = s.getCnpj() != null ? s.getCnpj().valor() : null;
                jpa.email = s.getEmail();
                jpa.telefone = s.getTelefone();
                jpa.status = s.getStatus();
                return jpa;
            }
        });

        addConverter(new AbstractConverter<Setor, SetorJpa>() {
            @Override
            protected SetorJpa convert(Setor s) {
                var jpa = new SetorJpa();
                jpa.id = s.getId().valor();
                jpa.espacoId = s.getEspacoId().valor();
                jpa.nome = s.getNome();
                jpa.tipoSetor = s.getTipoSetor();
                jpa.fileirasHorizontais = s.getFileirasHorizontais();
                jpa.assentosPorFileiraVertical = s.getAssentosPorFileiraVertical();
                jpa.versao = s.getVersao();
                jpa.assentos = s.getAssentos().stream().map(a -> {
                    var aj = new AssentoJpa();
                    aj.id = a.getId();
                    aj.codigo = a.getCodigo();
                    aj.fileira = a.getFileira();
                    aj.numero = a.getNumero();
                    aj.status = a.getStatus();
                    aj.motivoIndisponibilidade = a.getMotivoIndisponibilidade();
                    aj.versao = a.getVersao();
                    return aj;
                }).collect(java.util.stream.Collectors.toCollection(ArrayList::new));
                return jpa;
            }
        });

        addConverter(new AbstractConverter<Espaco, EspacoJpa>() {
            @Override
            protected EspacoJpa convert(Espaco s) {
                var jpa = new EspacoJpa();
                jpa.id = s.getId().valor();
                jpa.nome = s.getNome();
                jpa.capacidadeMaxima = s.getCapacidadeMaxima();
                jpa.status = s.getStatus();
                jpa.riderTecnico = new ArrayList<>(s.getRiderTecnico());
                return jpa;
            }
        });

        addConverter(new AbstractConverter<Sorteio, SorteioJpa>() {
            @Override
            protected SorteioJpa convert(Sorteio s) {
                var jpa = new SorteioJpa();
                jpa.id = s.getId();
                jpa.apresentacaoId = s.getApresentacaoId();
                jpa.eventoId = s.getEventoId();
                jpa.vagas = s.getVagas();
                jpa.prazoInscricao = s.getPrazoInscricao();
                jpa.dataApresentacao = s.getDataApresentacao();
                jpa.status = s.getStatus();
                jpa.inscricoes = s.getInscricoes().stream().map(i -> {
                    var ij = new InscricaoJpa();
                    ij.espectadorId = i.getEspectadorId();
                    ij.momentoInscricao = i.getMomentoInscricao();
                    ij.status = i.getStatus();
                    return ij;
                }).collect(java.util.stream.Collectors.toCollection(ArrayList::new));
                return jpa;
            }
        });

        addConverter(new AbstractConverter<Cupom, CupomJpa>() {
            @Override
            protected CupomJpa convert(Cupom s) {
                var jpa = new CupomJpa();
                jpa.id = s.getId().getValor();
                jpa.codigo = s.getCodigo();
                jpa.tipoDesconto = s.getTipoDesconto();
                jpa.valorDesconto = s.getValorDesconto();
                jpa.valorMinimoPedido = s.getValorMinimoPedido();
                jpa.limiteGlobal = s.getLimiteGlobal();
                jpa.usosGlobais = s.getUsosGlobais();
                jpa.limitePorCpf = s.getLimitePorCpf();
                jpa.dataInicio = s.getDataInicio();
                jpa.dataFim = s.getDataFim();
                jpa.categoriaPermitida = s.getCategoriaPermitida();
                return jpa;
            }
        });

        addConverter(new AbstractConverter<IngressoCatraca, IngressoCatracaJpa>() {
            @Override
            protected IngressoCatracaJpa convert(IngressoCatraca s) {
                var jpa = new IngressoCatracaJpa();
                jpa.id = s.getId().getValor();
                jpa.idEvento = s.getIdEvento();
                jpa.status = s.getStatus();
                jpa.horarioInicioEvento = s.getHorarioInicioEvento();
                jpa.tipoIngresso = s.getTipoIngresso();
                jpa.portaoAcesso = s.getPortaoAcesso();
                return jpa;
            }
        });

        addConverter(new AbstractConverter<OrcamentoPeriodo, OrcamentoPeriodoJpa>() {
            @Override
            protected OrcamentoPeriodoJpa convert(OrcamentoPeriodo s) {
                var jpa = new OrcamentoPeriodoJpa();
                jpa.id = s.getId().valor();
                jpa.periodoInicio = s.getPeriodo().getDataInicio();
                jpa.periodoFim = s.getPeriodo().getDataFim();
                jpa.valorTotal = s.getValorTotal();
                jpa.status = s.getStatus();
                return jpa;
            }
        });

        addConverter(new AbstractConverter<Despesa, DespesaJpa>() {
            @Override
            protected DespesaJpa convert(Despesa s) {
                var jpa = new DespesaJpa();
                jpa.id = s.getId().valor();
                jpa.orcamentoId = s.getOrcamentoId().valor();
                jpa.descricao = s.getDescricao();
                jpa.valor = s.getValor();
                jpa.categoria = s.getCategoria();
                jpa.dataRegistro = s.getDataRegistro();
                return jpa;
            }
        });

        addConverter(new AbstractConverter<BloqueioAdministrativo, BloqueioAdministrativoJpa>() {
            @Override
            protected BloqueioAdministrativoJpa convert(BloqueioAdministrativo s) {
                var jpa = new BloqueioAdministrativoJpa();
                jpa.id = s.getId().valor();
                jpa.espacoId = s.getEspacoId().valor();
                jpa.dataInicio = s.getDataInicio();
                jpa.dataFim = s.getDataFim();
                jpa.justificativa = s.getJustificativa();
                jpa.ativo = s.isAtivo();
                List<UUID> cancelados = s.getEventosCancelados();
                jpa.eventosCancelados = cancelados.isEmpty() ? null
                        : cancelados.stream().map(UUID::toString).collect(java.util.stream.Collectors.joining(","));
                return jpa;
            }
        });

        addConverter(new AbstractConverter<Ingresso, IngressoJpa>() {
            @Override
            protected IngressoJpa convert(Ingresso s) {
                var jpa = new IngressoJpa();
                jpa.id = s.getId().valor();
                jpa.eventoId = s.getEventoId();
                jpa.assentoId = s.getAssentoId();
                jpa.dataHoraApresentacao = s.getDataHoraApresentacao();
                jpa.tipo = s.getTipo().name();
                jpa.status = s.getStatus();
                jpa.valorPago = s.getValorPago();
                jpa.codigoQr = s.getCodigoQr();
                jpa.codigoTransacao = s.getCodigoTransacao();
                jpa.metodoPagamento = s.getMetodoPagamento().name();
                jpa.dataCompra = s.getDataCompra();
                jpa.valorReembolsado = s.getValorReembolsado();
                return jpa;
            }
        });

        addConverter(new AbstractConverter<ComentarioJpa, Comentario>() {
            @Override
            protected Comentario convert(ComentarioJpa s) {
                Nota nota = s.nota != null ? new Nota(s.nota) : null;
                return new Comentario(
                        s.id, s.espectadorId, s.eventoId, s.comentarioPaiId,
                        s.texto, nota, s.status, s.criadoEm, s.curtidas);
            }
        });

        addConverter(new AbstractConverter<Comentario, ComentarioJpa>() {
            @Override
            protected ComentarioJpa convert(Comentario s) {
                var jpa = new ComentarioJpa();
                jpa.id = s.getId();
                jpa.espectadorId = s.getEspectadorId();
                jpa.eventoId = s.getEventoId();
                jpa.comentarioPaiId = s.getComentarioPaiId();
                jpa.texto = s.getTexto();
                jpa.nota = s.getNota() != null ? s.getNota().getValor() : null;
                jpa.status = s.getStatus();
                jpa.criadoEm = s.getCriadoEm();
                jpa.curtidas = new java.util.HashSet<>(s.getCurtidas());
                return jpa;
            }
        });

        addConverter(new AbstractConverter<PreReservaJpa, PreReserva>() {
            @Override
            protected PreReserva convert(PreReservaJpa s) {
                return new PreReserva(
                        new PreReservaId(s.id), s.assentoId, s.setorId,
                        s.usuarioId, s.criadaEm, s.expiraEm, s.status, s.versao
                );
            }
        });

        addConverter(new AbstractConverter<PreReserva, PreReservaJpa>() {
            @Override
            protected PreReservaJpa convert(PreReserva s) {
                var jpa = new PreReservaJpa();
                jpa.id = s.getId().valor();
                jpa.assentoId = s.getAssentoId();
                jpa.setorId = s.getSetorId();
                jpa.usuarioId = s.getUsuarioId();
                jpa.criadaEm = s.getCriadaEm();
                jpa.expiraEm = s.getExpiraEm();
                jpa.status = s.getStatus();
                jpa.versao = s.getVersao();
                return jpa;
            }
        });

        addConverter(new AbstractConverter<RecursoAcessibilidadeJpa, RecursoAcessibilidade>() {
            @Override
            protected RecursoAcessibilidade convert(RecursoAcessibilidadeJpa s) {
                return new RecursoAcessibilidade(s.id, s.apresentacaoId, s.eventoId,
                        s.tipo, s.status, s.justificativaRemocao);
            }
        });

        addConverter(new AbstractConverter<RecursoAcessibilidade, RecursoAcessibilidadeJpa>() {
            @Override
            protected RecursoAcessibilidadeJpa convert(RecursoAcessibilidade s) {
                var jpa = new RecursoAcessibilidadeJpa();
                jpa.id = s.getId();
                jpa.apresentacaoId = s.getApresentacaoId();
                jpa.eventoId = s.getEventoId();
                jpa.tipo = s.getTipo();
                jpa.status = s.getStatus();
                jpa.justificativaRemocao = s.getJustificativaRemocao();
                return jpa;
            }
        });

        addConverter(new AbstractConverter<EquipamentoJpa, Equipamento>() {
            @Override
            protected Equipamento convert(EquipamentoJpa s) {
                return new Equipamento(new EquipamentoId(s.id), new EspacoId(s.espacoId),
                        s.nome, s.status, s.eventoAlocadoId);
            }
        });

        addConverter(new AbstractConverter<Equipamento, EquipamentoJpa>() {
            @Override
            protected EquipamentoJpa convert(Equipamento s) {
                var jpa = new EquipamentoJpa();
                jpa.id = s.getId().valor();
                jpa.espacoId = s.getEspacoId().valor();
                jpa.nome = s.getNome();
                jpa.status = s.getStatus();
                jpa.eventoAlocadoId = s.getEventoAlocadoId();
                return jpa;
            }
        });

        addConverter(new AbstractConverter<ChamadoSuporteJpa, ChamadoSuporte>() {
            @Override
            protected ChamadoSuporte convert(ChamadoSuporteJpa s) {
                return new ChamadoSuporte(s.id, s.assentoId, s.motivo, s.descricao, s.status, s.dataAbertura);
            }
        });

        addConverter(new AbstractConverter<ChamadoSuporte, ChamadoSuporteJpa>() {
            @Override
            protected ChamadoSuporteJpa convert(ChamadoSuporte s) {
                var jpa = new ChamadoSuporteJpa();
                jpa.id = s.getId();
                jpa.assentoId = s.getAssentoId();
                jpa.descricao = s.getDescricao();
                jpa.motivo = s.getMotivo();
                jpa.status = s.getStatus();
                jpa.dataAbertura = s.getDataAbertura();
                return jpa;
            }
        });

        addConverter(new AbstractConverter<HistoricoStatusProdutorJpa, HistoricoStatusProdutor>() {
            @Override
            protected HistoricoStatusProdutor convert(HistoricoStatusProdutorJpa s) {
                return new HistoricoStatusProdutor(
                        new recifecultural.dominio.artista.produtor.ProdutorId(s.produtorId),
                        s.statusAnterior, s.statusNovo, s.responsavel, s.motivo);
            }
        });

        addConverter(new AbstractConverter<HistoricoStatusProdutor, HistoricoStatusProdutorJpa>() {
            @Override
            protected HistoricoStatusProdutorJpa convert(HistoricoStatusProdutor s) {
                var jpa = new HistoricoStatusProdutorJpa();
                jpa.id = s.getId();
                jpa.produtorId = s.getProdutorId().valor();
                jpa.statusAnterior = s.getStatusAnterior();
                jpa.statusNovo = s.getStatusNovo();
                jpa.responsavel = s.getResponsavel();
                jpa.motivo = s.getMotivo();
                jpa.dataAlteracao = s.getDataAlteracao();
                return jpa;
            }
        });

        addConverter(new AbstractConverter<NotificacaoJpa, Notificacao>() {
            @Override
            protected Notificacao convert(NotificacaoJpa s) {
                return new Notificacao(new NotificacaoId(s.id), s.usuarioAlvo,
                        s.mensagem, s.contexto, s.idReferencia, s.foiLida, s.dataCriacao);
            }
        });

        addConverter(new AbstractConverter<Notificacao, NotificacaoJpa>() {
            @Override
            protected NotificacaoJpa convert(Notificacao s) {
                var jpa = new NotificacaoJpa();
                jpa.id = s.getId().valor();
                jpa.usuarioAlvo = s.getUsuarioAlvo();
                jpa.mensagem = s.getMensagem();
                jpa.contexto = s.getContexto();
                jpa.idReferencia = s.getIdReferencia();
                jpa.foiLida = s.isFoiLida();
                jpa.dataCriacao = s.getDataCriacao();
                return jpa;
            }
        });

        addConverter(new AbstractConverter<AuditoriaJpa, RegistroAuditoria>() {
            @Override
            protected RegistroAuditoria convert(AuditoriaJpa s) {
                return new RegistroAuditoria(
                        s.id, s.entidade, s.entidadeId, s.acao,
                        s.statusAnterior, s.statusNovo, s.descricao, s.momento);
            }
        });

        addConverter(new AbstractConverter<RegistroAuditoria, AuditoriaJpa>() {
            @Override
            protected AuditoriaJpa convert(RegistroAuditoria s) {
                var jpa = new AuditoriaJpa();
                jpa.id = s.getId();
                jpa.entidade = s.getEntidade();
                jpa.entidadeId = s.getEntidadeId();
                jpa.acao = s.getAcao();
                jpa.statusAnterior = s.getStatusAnterior();
                jpa.statusNovo = s.getStatusNovo();
                jpa.descricao = s.getDescricao();
                jpa.momento = s.getMomento();
                return jpa;
            }
        });
    }

    @Override
    public <D> D map(Object source, Class<D> destinationType) {
        return source != null ? super.map(source, destinationType) : null;
    }
}
