Feature: Cadastrar artista
  Como artista
  Quero submeter meu cadastro na plataforma
  Para que um promotor possa me aprovar e eu possa ser vinculado a eventos

  Scenario: Cadastro com dados válidos
    When submeto meu cadastro com nome "Alceu Valença", bio "Cantor e compositor pernambucano", email "alceu@exemplo.com" e telefone "81999990000"
    Then meu cadastro é criado com status "PENDENTE"

  Scenario: Cadastro sem nome é rejeitado
    When submeto meu cadastro com nome "", bio "Bio qualquer", email "artista@exemplo.com" e telefone ""
    Then o sistema rejeita o cadastro com a mensagem "Nome do artista é obrigatório."

  Scenario: Cadastro sem bio é rejeitado
    When submeto meu cadastro com nome "Nação Zumbi", bio "", email "nacao@exemplo.com" e telefone ""
    Then o sistema rejeita o cadastro com a mensagem "Bio do artista é obrigatória."

  Scenario: Cadastro com email inválido é rejeitado
    When submeto meu cadastro com nome "Lenine", bio "Cantor e compositor", email "lenine-semArroba" e telefone ""
    Then o sistema rejeita o cadastro com a mensagem "E-mail do artista é inválido."

  Scenario: Cadastro com nome já existente é rejeitado
    Given que já existe um artista cadastrado com o nome "Alceu Valença"
    When submeto meu cadastro com nome "Alceu Valença", bio "Outra bio", email "outro@exemplo.com" e telefone ""
    Then o sistema rejeita o cadastro com a mensagem "Já existe um artista cadastrado com o nome: Alceu Valença"
