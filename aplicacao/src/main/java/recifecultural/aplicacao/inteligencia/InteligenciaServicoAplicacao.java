package recifecultural.aplicacao.inteligencia;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// Assuma que esta interface existe no seu domínio para buscar o evento
import recifecultural.dominio.agenda.evento.Evento;
import recifecultural.dominio.agenda.evento.IEventoRepositorio;

@Service
public class InteligenciaServicoAplicacao {

    private final IEventoRepositorio eventoRepositorio;
    private final VisitacaoConsulta visitacaoConsulta;

    private OrtEnvironment env;
    private OrtSession receitaSession;
    private OrtSession noShowSession;

    public InteligenciaServicoAplicacao(IEventoRepositorio eventoRepositorio,
                                        VisitacaoConsulta visitacaoConsulta) {
        this.eventoRepositorio = eventoRepositorio;
        this.visitacaoConsulta = visitacaoConsulta;
    }

    /**
     * Visitação histórica agregada por espaço (teatro) e mês.
     * Soma ingressos ATIVO ou UTILIZADO de todos os anos disponíveis no banco.
     * Retorna lista vazia se não houver ingressos — controlador decide fallback.
     */
    public List<VisitacaoMensal> listarVisitacao() {
        return visitacaoConsulta.agregarPorEspacoEMes();
    }

    /**
     * Inicializa o ambiente ONNX e carrega os modelos em memória
     * logo após o Spring Boot instanciar este serviço.
     *
     * Carrega via byte[] (não path) porque o JAR fat-packaged do Spring Boot
     * resolve recursos via URL nested:/...!/models/*.onnx — o ONNX Runtime
     * nativo não entende esse esquema e falha com ORT_NO_SUCHFILE.
     */
    @PostConstruct
    public void init() throws OrtException, IOException {
        this.env = OrtEnvironment.getEnvironment();

        byte[] receitaBytes = lerRecurso("/models/receita_model.onnx");
        byte[] noShowBytes = lerRecurso("/models/noshow_model.onnx");

        this.receitaSession = env.createSession(receitaBytes, new OrtSession.SessionOptions());
        this.noShowSession = env.createSession(noShowBytes, new OrtSession.SessionOptions());
    }

    private byte[] lerRecurso(String caminho) throws IOException {
        try (InputStream in = getClass().getResourceAsStream(caminho)) {
            if (in == null) {
                throw new IllegalStateException("Modelo ONNX não encontrado: " + caminho);
            }
            return in.readAllBytes();
        }
    }

    /**
     * Libera recursos nativos C++ do ONNX Runtime ao desligar a aplicação.
     */
    @PreDestroy
    public void close() throws OrtException {
        if (receitaSession != null) receitaSession.close();
        if (noShowSession != null) noShowSession.close();
        if (env != null) env.close();
    }

    /**
     * Predição Real de Receita utilizando Regressão Linear via ONNX.
     */
    public PrevisaoReceitaResposta preverReceita(double orcamentoMarketing, double patrocinio) {
        try {
            // 1. Preparar os dados de entrada (Tensor)
            float[][] features = new float[][]{
                    { (float) orcamentoMarketing, (float) patrocinio }
            };

            OnnxTensor inputTensor = OnnxTensor.createTensor(env, features);
            String inputName = receitaSession.getInputNames().iterator().next();
            Map<String, OnnxTensor> inputs = Collections.singletonMap(inputName, inputTensor);

            // 2. Executar a inferência
            OrtSession.Result result = receitaSession.run(inputs);

            // 3. Extrair o resultado do array (Regressão retorna Float 2D: [N_Amostras][N_Outputs])
            float[][] output = (float[][]) result.get(0).getValue();
            double receitaEstimada = output[0][0];

            double investimentoTotal = orcamentoMarketing + patrocinio;
            double receitaFormatada = Math.round(receitaEstimada * 100.0) / 100.0;

            // Fechar tensores para evitar vazamento de memória nativa
            inputTensor.close();
            result.close();

            return new PrevisaoReceitaResposta(investimentoTotal, receitaFormatada);

        } catch (OrtException e) {
            throw new RuntimeException("Erro ao executar inferência de Receita via ONNX", e);
        }
    }

    /**
     * Predição Real de No-Show utilizando Random Forest via ONNX.
     */
    public PrevisaoNoShowResposta preverNoShow(UUID eventoId) {
        if (eventoId == null) throw new IllegalArgumentException("eventoId é obrigatório.");

        // 1. Buscar evento real no banco
        Evento evento = eventoRepositorio.obter(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado."));

        // 2. Feature Engineering: Extrair e transformar os dados conforme o treino em Python
        float precoInteira = evento.getPreco() != null && evento.getPreco().getInteira() != null 
                ? evento.getPreco().getInteira().floatValue() : 0.0f;
                
        int diaDaSemana = 1; // Default
        if (!evento.getDatasApresentacao().isEmpty()) {
            LocalDateTime primeiraData = evento.getDatasApresentacao().get(0);
            diaDaSemana = primeiraData.getDayOfWeek().getValue(); // 1=Segunda, 7=Domingo
        }
        
        float isFimDeSemana = (diaDaSemana == 6 || diaDaSemana == 7) ? 1.0f : 0.0f;

        try {
            // 3. Montar Tensor com as 3 features exatas que a IA espera
            float[][] features = new float[][]{
                    { precoInteira, (float) diaDaSemana, isFimDeSemana }
            };

            OnnxTensor inputTensor = OnnxTensor.createTensor(env, features);
            String inputName = noShowSession.getInputNames().iterator().next();
            Map<String, OnnxTensor> inputs = Collections.singletonMap(inputName, inputTensor);

            // 4. Executar inferência
            OrtSession.Result result = noShowSession.run(inputs);

            // 5. Extrair predição
            float[][] output = (float[][]) result.get(0).getValue();
            double probabilidadeNoShow = output[0][0];
            double probabilidadeFormatada = Math.round(probabilidadeNoShow * 100.0) / 100.0;

            boolean alertaAltoRisco = probabilidadeFormatada > 20.0;

            inputTensor.close();
            result.close();

            return new PrevisaoNoShowResposta(eventoId, probabilidadeFormatada, alertaAltoRisco);

        } catch (OrtException e) {
            throw new RuntimeException("Erro ao executar inferência de No-Show via ONNX", e);
        }
    }

    /**
     * Integra a predição de risco da IA com métricas complementares simuladas 
     * (Ocupação e Público-Alvo).
     */
    public AnaliseEventoResposta analisarEvento(UUID eventoId) {
        if (eventoId == null) throw new IllegalArgumentException("eventoId é obrigatório.");

        // Usa os modelos reais para descobrir Risco
        PrevisaoNoShowResposta noShowResposta = preverNoShow(eventoId);
        
        // Simulação base para métricas não modeladas (público e ocupação)
        long seed = Math.abs(eventoId.getMostSignificantBits() ^ eventoId.getLeastSignificantBits());
        
        // A receita na análise pode ser uma média predefinida baseada no risco 
        // ou você pode adicionar os dados financeiros do evento à chamada
        double receitaProjetada = Math.round((25_000.0 + (seed % 75_000)) * 100.0) / 100.0;
        
        // Ocupação inversamente proporcional à probabilidade de No-Show
        double taxaOcupacaoEsperada = Math.max(0.0, 100.0 - noShowResposta.getProbabilidadeNoShow() - (seed % 10));

        String[] publicos = { "Jovens 18-25", "Adultos 26-35", "Famílias", "Profissionais 30-45", "Estudantes" };
        String publicoAlvo = publicos[(int) (seed % publicos.length)];

        AnaliseEventoResposta.NivelRisco risco;
        if (noShowResposta.isAlertaAltoRisco()) {
            risco = AnaliseEventoResposta.NivelRisco.ALTO;
        } else if (noShowResposta.getProbabilidadeNoShow() > 10.0) {
            risco = AnaliseEventoResposta.NivelRisco.MEDIO;
        } else {
            risco = AnaliseEventoResposta.NivelRisco.BAIXO;
        }

        return new AnaliseEventoResposta(receitaProjetada, taxaOcupacaoEsperada, publicoAlvo, risco);
    }
}