Feature: Aprovar e rejeitar artista
  Como promotor
  Quero aprovar ou rejeitar cadastros de artistas pendentes
  Para controlar quem pode ser vinculado aos eventos do teatro

  Scenario: Aprovar artista pendente
    Given que existe um artista com status "PENDENTE"
    When o promotor aprova o artista
    Then o artista passa para o status "APROVADO"

  Scenario: Rejeitar artista pendente com motivo
    Given que existe um artista com status "PENDENTE"
    When o promotor rejeita o artista com o motivo "Portfólio insuficiente"
    Then o artista passa para o status "REJEITADO"
    And o motivo de rejeição é "Portfólio insuficiente"

  Scenario: Rejeitar artista sem motivo é rejeitado
    Given que existe um artista com status "PENDENTE"
    When o promotor rejeita o artista com o motivo ""
    Then o sistema rejeita a operação com a mensagem "Motivo de rejeição é obrigatório."

  Scenario: Aprovar artista que não está pendente é rejeitado
    Given que existe um artista com status "APROVADO"
    When o promotor aprova o artista
    Then o sistema rejeita a operação com a mensagem "Apenas artistas com status PENDENTE podem ser aprovados."

  Scenario: Rejeitar artista que não está pendente é rejeitado
    Given que existe um artista com status "REJEITADO"
    When o promotor rejeita o artista com o motivo "Qualquer motivo"
    Then o sistema rejeita a operação com a mensagem "Apenas artistas com status PENDENTE podem ser rejeitados."

  Scenario: Resubmeter artista rejeitado
    Given que existe um artista com status "REJEITADO"
    When o artista resubmete o cadastro
    Then o artista passa para o status "PENDENTE"
    And o motivo de rejeição é removido

  Scenario: Resubmeter artista que não está rejeitado é rejeitado
    Given que existe um artista com status "PENDENTE"
    When o artista resubmete o cadastro
    Then o sistema rejeita a operação com a mensagem "Apenas artistas com status REJEITADO podem resubmeter o cadastro."
