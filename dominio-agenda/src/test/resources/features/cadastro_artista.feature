Feature: Cadastro e gestão de artistas

Scenario: Cadastrar artista com produtor ativo
Given que existe um produtor salvo no repositório com status "ATIVO"
And não existe artista com o nome "Banda Recife" para este produtor
When eu solicitar o cadastro de um artista com nome "Banda Recife", gênero musical "FORRO" e este produtor
Then o artista deve ser cadastrado com sucesso
And o status do artista deve ser "ATIVO"

Scenario: Cadastrar artista com produtor suspenso
Given que existe um produtor salvo no repositório com status "SUSPENSO"
When eu solicitar o cadastro de um artista com nome "Banda do Norte", gênero musical "MPB" e este produtor
Then o cadastro deve ser rejeitado
And a validação deve informar que o produtor deve estar ativo

Scenario: Cadastrar artista com nome duplicado para o mesmo produtor
Given que existe um produtor salvo no repositório com status "ATIVO"
And existe um artista salvo com o nome "Banda Recife" para este produtor
When eu solicitar o cadastro de um artista com nome "Banda Recife", gênero musical "FORRO" e este produtor
Then o cadastro deve ser rejeitado
And a validação deve informar que já existe artista com esse nome para o produtor

Scenario: Inativar artista sem eventos futuros confirmados
Given que existe um artista salvo no repositório com status "ATIVO"
And o serviço de consulta de eventos informa que não existem eventos futuros confirmados para este artista
When eu solicitar a inativação deste artista
Then o artista deve ser atualizado para o status "INATIVO"

Scenario: Inativar artista com eventos futuros confirmados
Given que existe um artista salvo no repositório com status "ATIVO"
And o serviço de consulta de eventos informa que existem eventos futuros confirmados para este artista
When eu solicitar a inativação deste artista
Then a operação deve ser rejeitada
And a validação deve informar que existem eventos futuros confirmados

Scenario: Atualizar rider técnico de artista ativo
Given que existe um artista salvo no repositório com status "ATIVO"
When eu solicitar a atualização do rider técnico deste artista com os itens "MICROFONE", "MESA_DE_SOM" e "ILUMINACAO_CENA"
Then o rider técnico deve ser atualizado com sucesso

Scenario: Atualizar rider técnico de artista inativo
Given que existe um artista salvo no repositório com status "INATIVO"
When eu solicitar a atualização do rider técnico deste artista com os itens "MICROFONE"
Then a operação deve ser rejeitada
And a validação deve informar que artista inativo não pode atualizar o rider técnico