Feature: Ativar e Encerrar Patrocínio

  Scenario: Ativar patrocínio em PROPOSTA com sucesso
    Given que existe um patrocínio de SUBSIDIO_INGRESSO_SOCIAL em PROPOSTA com valor 500.00
    When ativo o patrocínio
    Then o status do patrocínio é "ATIVO"

  Scenario: Ativar patrocínio já ATIVO lança erro
    Given que existe um patrocínio de SUBSIDIO_INGRESSO_SOCIAL em ATIVO com valor 500.00
    When tento ativar o patrocínio novamente
    Then o sistema rejeita com a mensagem "Apenas patrocínios com status PROPOSTA podem ser ativados."

  Scenario: Ativar patrocínio ENCERRADO lança erro
    Given que existe um patrocínio de SUBSIDIO_INGRESSO_SOCIAL em ENCERRADO com valor 500.00
    When tento ativar o patrocínio novamente
    Then o sistema rejeita com a mensagem "Apenas patrocínios com status PROPOSTA podem ser ativados."

  Scenario: Encerrar patrocínio ATIVO com sucesso
    Given que existe um patrocínio de SUBSIDIO_INGRESSO_SOCIAL em ATIVO com valor 500.00
    When encerro o patrocínio
    Then o status do patrocínio é "ENCERRADO"

  Scenario: Encerrar patrocínio não ATIVO lança erro
    Given que existe um patrocínio de SUBSIDIO_INGRESSO_SOCIAL em PROPOSTA com valor 500.00
    When tento encerrar o patrocínio
    Then o sistema rejeita com a mensagem "Apenas patrocínios com status ATIVO podem ser encerrados."
