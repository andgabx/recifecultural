package recifecultural.arquitetura;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(
        packages = "recifecultural",
        importOptions = ImportOption.DoNotIncludeTests.class
)
public class ArquiteturaTest {

    @ArchTest
    static final ArchRule bilheteria_nao_importa_catraca =
            noClasses().that().resideInAPackage("..dominio.ingressos..")
                    .should().dependOnClassesThat().resideInAPackage("..dominio.catraca..")
                    .because("Bilheteria deve integrar com Catraca via evento de domínio (Ingresso.ReembolsadoEvento), não via chamada direta.");

    @ArchTest
    static final ArchRule sorteio_nao_importa_outros_bcs_da_agenda =
            noClasses().that().resideInAPackage("..agenda.sorteio..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "..agenda.bloqueioadministrativo..",
                            "..agenda.acessibilidade..",
                            "..agenda.equipamento..",
                            "..agenda.comentario..",
                            "..agenda.prereserva..")
                    .because("SorteioContext deve ser isolado dos demais sub-contextos de dominio-agenda.");

    @ArchTest
    static final ArchRule acessibilidade_nao_importa_outros_bcs_da_agenda =
            noClasses().that().resideInAPackage("..agenda.acessibilidade..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "..agenda.bloqueioadministrativo..",
                            "..agenda.sorteio..",
                            "..agenda.equipamento..",
                            "..agenda.comentario..",
                            "..agenda.prereserva..")
                    .because("AcessibilidadeContext deve ser isolado dos demais sub-contextos de dominio-agenda.");

    @ArchTest
    static final ArchRule bloqueio_nao_importa_outros_bcs_da_agenda =
            noClasses().that().resideInAPackage("..agenda.bloqueioadministrativo..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "..agenda.sorteio..",
                            "..agenda.acessibilidade..",
                            "..agenda.equipamento..",
                            "..agenda.comentario..",
                            "..agenda.prereserva..")
                    .because("BloqueioAdministrativoContext (CORE_DOMAIN) deve ser isolado dos demais sub-contextos.");

    @ArchTest
    static final ArchRule patrocinio_nao_importa_cupom =
            noClasses().that().resideInAPackage("..dominio.patrocinio..")
                    .should().dependOnClassesThat().resideInAPackage("..dominio.cupom..")
                    .because("PatrocinioContext e CupomContext são BCs distintos no Context Map.");

    @ArchTest
    static final ArchRule cupom_nao_importa_catraca =
            noClasses().that().resideInAPackage("..dominio.cupom..")
                    .should().dependOnClassesThat().resideInAPackage("..dominio.catraca..")
                    .because("CupomContext e CatracaContext são BCs distintos.");

    @ArchTest
    static final ArchRule compartilhado_nao_depende_de_bcs_especificos =
            noClasses().that().resideInAPackage("..compartilhado.evento..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "..agenda..",
                            "..artista..",
                            "..espaco..",
                            "..financeiro..",
                            "..ingressos..",
                            "..patrocinio..")
                    .because("dominio-compartilhado é base; não pode depender dos BCs específicos.");
}
