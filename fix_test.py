import re

with open('dominio-agenda/src/test/java/recifecultural/dominio/agenda/bdd/PassosMapeamentoAssentos.java', 'r') as f:
    content = f.read()

content = content.replace('private SetorServico setorServico;', 'private GestaoAmbienteInternoServico setorServico;')
content = content.replace('new SetorServico(setorRepositorio, espacoRepositorio);', 'new GestaoAmbienteInternoServico(setorRepositorio, espacoRepositorio);')
content = content.replace('setorServico.configurarSetor(', 'setorServico.configurarGestaoAmbiente(')

content = content.replace(
    'assentoMock = new Assento(assentoIdMock, codigoAssento, "A", 1, StatusAssento.valueOf(status), 0);',
    'assentoMock = new Assento(assentoIdMock, codigoAssento, "A", 1, StatusAssento.valueOf(status), MotivoIndisponibilidadeAssento.OUTRO, 0);'
)
content = content.replace(
    'setorMock = new Setor(SetorId.de(setorIdMock.toString()), EspacoId.novo(), "Setor 1", TipoSetor.PLATEIA, List.of(assentoMock), 0);',
    'setorMock = new Setor(SetorId.de(setorIdMock.toString()), EspacoId.novo(), "Setor 1", TipoSetor.PLATEIA, 10, 10, List.of(assentoMock), 0);'
)

content = content.replace(
    'assentoMock = new Assento(assentoIdMock, assentoCodigo, "B", 2, StatusAssento.PRE_RESERVADO, 0);',
    'assentoMock = new Assento(assentoIdMock, assentoCodigo, "B", 2, StatusAssento.PRE_RESERVADO, MotivoIndisponibilidadeAssento.OUTRO, 0);'
)

content = content.replace(
    'setorMock.bloquearAssento(assentoIdMock);',
    'setorMock.bloquearAssento(assentoIdMock, MotivoIndisponibilidadeAssento.OUTRO);'
)

with open('dominio-agenda/src/test/java/recifecultural/dominio/agenda/bdd/PassosMapeamentoAssentos.java', 'w') as f:
    f.write(content)
