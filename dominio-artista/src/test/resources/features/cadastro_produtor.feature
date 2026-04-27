Feature: Cadastro e gestão de produtores culturais

Scenario: Cadastrar produtor com CNPJ válido e único
Given que não existe produtor cadastrado com o CNPJ "12.345.678/0001-95"
When eu solicitar o cadastro de um produtor com nome fantasia "Produtora Recife Viva", CNPJ "12.345.678/0001-95", e-mail "contato@recifeviva.com.br" e telefone "(81) 99999-0000"
Then o produtor deve ser cadastrado com sucesso
And o status do produtor deve ser "ATIVO"

Scenario: Cadastrar produtor com CNPJ já existente
Given que existe um produtor cadastrado com o CNPJ "12.345.678/0001-95"
When eu solicitar o cadastro de um produtor com nome fantasia "Nova Produtora", CNPJ "12.345.678/0001-95", e-mail "novo@produtora.com.br" e telefone "(81) 98888-1111"
Then o cadastro deve ser rejeitado
And a validação deve informar que o CNPJ já existe

Scenario: Cadastrar produtor com CNPJ inválido
Given que não existe produtor cadastrado com o CNPJ "12.345.678/0001-00"
When eu solicitar o cadastro de um produtor com nome fantasia "Produtora Inválida", CNPJ "12.345.678/0001-00", e-mail "invalida@produtora.com.br" e telefone "(81) 97777-2222"
Then o cadastro deve ser rejeitado
And a validação deve informar que o CNPJ é inválido

Scenario: Suspender produtor ativo
Given que existe um produtor salvo no repositório com status "ATIVO"
When eu solicitar a suspensão deste produtor
Then o produtor deve ser atualizado para o status "SUSPENSO"

Scenario: Reativar produtor suspenso
Given que existe um produtor salvo no repositório com status "SUSPENSO"
When eu solicitar a reativação deste produtor
Then o produtor deve ser atualizado para o status "ATIVO"

Scenario: Reativar produtor inativo
Given que existe um produtor salvo no repositório com status "INATIVO"
When eu solicitar a reativação deste produtor
Then a operação deve ser rejeitada
And a validação deve informar que produtor inativo não pode ser reativado

Scenario: Inativar produtor sem artistas ativos
Given que existe um produtor salvo no repositório com status "ATIVO"
And o produtor não possui artistas ativos vinculados
When eu solicitar a inativação deste produtor
Then o produtor deve ser atualizado para o status "INATIVO"
And os dados de e-mail e telefone devem ser anonimizados

Scenario: Inativar produtor com artistas ativos
Given que existe um produtor salvo no repositório com status "ATIVO"
And o produtor possui ao menos um artista com status "ATIVO"
When eu solicitar a inativação deste produtor
Then a operação deve ser rejeitada
And a validação deve informar que existem artistas ativos vinculados