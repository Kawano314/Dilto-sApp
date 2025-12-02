#  Trabalho 3

## Como executar

- Requisitos: Java 17+, Maven 3.9+
- Executar testes:
```
mvn clean test
```
- Executar aplicação (exemplo):
Rodar a partir da classe Main

## Estrutura principal
- pasta models: modelos de domínio (Produto, HorarioFuncionamento, ReservaSinuca, etc.)
- pasta dao: persistência via SQLite embutido
- pasta service: regras de negócio (ProdutoService, HorarioService, ReservaSinucaService)
- pasta report: interface e exportador de relatórios (TXT)
- pasta ui: janelas (Admin, Cliente, Menu)
- Testes em `src/test/java/org/example/...`

## Novas funcionalidades via TDD (3)
1) Feriados automáticos (Horário Especial)
- Implementação: `HorarioService.cadastrarFeriadosNacionais(int ano)`
- Testes: `HorarioServiceTest.FeriadosAutomaticos` validam criação, fechamento e não duplicação.
- Cenário: datas fixas nacionais cadastradas como "fechado" com observação.

2) Interface Gráfica em Java Swing
- Implementação: Três telas principais em `pasta ui`:
  - `MenuPrincipal`: Tela de login e seleção de tipo de usuário (Admin/Cliente)
  - `AdminMainWindow`: Interface administrativa com abas para Produtos, Horários e Mesas de Sinuca
  - `ClienteMainWindow`: Interface de cliente com abas para Produtos, Minhas Reservas e Horários
- Testes: `MenuPrincipalTest` (4 testes), `AdminMainWindowTest` (7 testes), `ClienteMainWindowTest` (8 testes) — 19 testes no total
- Cenários testados:
  - Instanciação sem erros das três janelas principais
  - Configurações corretas de tamanho (AdminMainWindow: 1000x700, ClienteMainWindow: 900x650), título e operação de fechamento
  - Associação correta com usuário logado (admin/cliente)
  - Presença das abas esperadas em cada interface
  - Responsividade da interface (resizable)
  - Integração funcional com services de Produtos, Horários e Reservas

3) Exportação TXT de produtos com estoque baixo
- Implementação: Atalho na service: `ProdutoService.exportarRelatorioEstoqueBaixoTxt(int limite, Path destino)`
- Testes: `RelatorioEstoqueBaixoTxtExporterTest` (criação do arquivo, cabeçalho, filtragem correta e ordenação).
- Formato do arquivo:
  - Cabeçalho: `Relatório de Produtos com Estoque Baixo (<= LIMITE) - YYYY-MM-DD HH:mm`
  - Colunas: `ID | Nome | Categoria | Preço | Estoque`

## Testes de funcionalidades do T2

- `ProdutoServiceTest.java`
- `HorarioServiceTest.java`
- `ReservaSinucaServiceTest.java`
- `T2IntegrationNewFeaturesTest.java`
- `T2FunctionalitiesTest.java`

## Testes Novos do T3

- `MenuPrincipalTest.java`
- `AdminMainWindowTest.java`
- `ClienteMainWindowTest.java`
- `T2FunctionalitiesTest.java`
- `HorarioFuncionamentoTest.java`
- `HorarioFuncionamentoParametrizadoTest.java`
- `ProdutoTest.java`
- `ReservaSinucaTest.java`
- `ExcecoesTest.java`

## Manutenção

### 1. Correção de bugs das funcionalidades do Trabalho 2
Bugs corrigidos e validações implementadas:
- Validação de dias fechados: Sistema agora bloqueia criação de reservas em dias que o estabelecimento está fechado (feriados, domingos/sábados fechados, horários especiais marcados como "fechado").
  - Implementado em: `ReservaSinucaService.validarReserva()` - verifica `horario.isFechado()` e rejeita com exceção clara.
  - Testes: `ReservaSinucaServiceTest` e `T2IntegrationNewFeaturesTest` validam bloqueio em feriados.
  
- Validação de horários de funcionamento: Reservas não podem extrapolar o horário de fechamento do estabelecimento.
  - Exemplo: se fecha às 18:00, não é possível cadastrar reserva que termine às 20:00.
  - Implementado em: `ReservaSinucaService.validarReserva()` - valida `horaFim.isAfter(horarioFechamento)`.
  - Exceção lançada: `"Horário inválido: fora do horário de funcionamento (08:00 - 18:00)"`.
  - Testes: `ReservaSinucaServiceTest.LimitesFuncionamento` valida limites de abertura/fechamento.

### 2. TDD (refatoração) de novas funcionalidades
Refatorações aplicadas com TDD:
- Interface `RelatorioExporter<T>`: Criada para desacoplar formato de exportação das regras de negócio.
  - Permite adicionar CSV, PDF, JSON sem alterar `ProdutoService`.
  - Testes: `RelatorioEstoqueBaixoTxtExporterTest` valida interface + implementação TXT.
- Método `ProdutoService.exportarRelatorioEstoqueBaixoTxt()`: Refatorado para usar a interface, facilitando manutenção e evolução.
- Ordenação consistente: Relatórios ordenam produtos por estoque crescente (menor estoque primeiro = maior prioridade de reabastecimento).
- Validações robustas: Criação automática de diretórios, validação de dados nulos/vazios, mensagens de erro claras.

### 3. Integração de funcionalidades do Trabalho 2 + novas
Integração implementada:
- T2IntegrationNewFeaturesTest: Teste end-to-end integrando:
  1. Cadastro de feriado nacional (nova funcionalidade T3)
  2. Bloqueio de reserva no feriado (integração T2 validação + T3 horário especial)
  3. Reserva permitida no dia seguinte ao feriado (T2 funcionalidade original)
  4. Cadastro de produtos com estoque baixo (T2)
  5. Geração de relatório TXT dos produtos (nova funcionalidade T3)
- T2FunctionalitiesTest: Validação de funcionalidades originais (produtos, reservas, horários) continua funcionando após integração das novas features.

### 4. Refatorações gerais no código completo
Refatorações estruturais:
- Pacote `org.example.service.report`: Novo pacote criado para isolar responsabilidade de geração de relatórios (Single Responsibility Principle).
- Constantes extraídas: `ReservaSinucaDAO.TOTAL_MESAS` centraliza número de mesas.
- Mensagens de erro padronizadas: Todas as exceptions agora incluem contexto detalhado (ex.: "Horário inválido: fora do horário de funcionamento (08:00 - 18:00)").
- Código duplicado eliminado: Métodos de validação centralizados (ex.: `validarNumeroMesa()`, `validarHorario()`).
- Tratamento de edge cases: Reservas "back-to-back" (fim de uma = início da outra) agora permitidas; horários especiais fechados sem necessidade de definir horas de abertura/fechamento.


## Como gerar o relatório TXT de estoque baixo
Exemplo de uso direto pela Service:
```java
var service = new org.example.service.ProdutoService();
var caminho = java.nio.file.Path.of("target", "relatorios", "estoque-baixo.txt");
service.exportarRelatorioEstoqueBaixoTxt(5, caminho);
service.fechar();
```
O arquivo será criado/atualizado no caminho informado.

## Observações
- O banco é criado automaticamente via `ProdutoDAO`/`ConnectDB` para facilitar testes locais.
- Os testes manipulam dados isoladamente e limpam registros quando necessário.
- A exportação foi desenhada para ser estendida (ex.: adicionar `CsvExporter`).

## Cenários de Testes Importantes

### Gerenciamento de Produtos (T2)

Cenário: Cadastrar e listar produtos válidos
- Pré-condição: Sistema inicializado
- Passos:
  1. Cadastrar produto "Taco Premium" com preço R$ 150.00 e estoque 5
  2. Cadastrar produto "Bola Branca" com preço R$ 25.50 e estoque 20
  3. Listar todos os produtos
- Resultado esperado: Ambos produtos aparecem na lista; IDs são distintos
- Arquivo de teste: `ProdutoServiceTest.java` → `CadastrarProduto`, `ListarProdutos`

Cenário: Validar preço negativo
- Pré-condição: Produto em criação
- Passos: Tentar cadastrar produto com preço R$ -10.00
- Resultado esperado: Exceção lançada; produto não é criado
- Arquivo de teste: `ProdutoServiceTest.java` → `BuscarProduto`

Cenário: Buscar por faixa de preço
- Pré-condição: Múltiplos produtos com preços variados (R$ 10, R$ 50, R$ 100, R$ 200)
- Passos: Buscar produtos entre R$ 40 e R$ 150
- Resultado esperado: Retorna 2 produtos (R$ 50 e R$ 100)
- Arquivo de teste: `ProdutoServiceTest.java` → `BuscarPorFaixaPreco`

### Gerenciamento de Horários (T2)

Cenário: Verificar horário especial fechado (Feriado)
- Pré-condição: Feriado nacional (ex: Natal) cadastrado como fechado
- Passos: Consultar horário de 25/12/2024
- Resultado esperado: `isFechado() == true`; observação inclui "Natal"
- Arquivo de teste: `HorarioServiceTest.java` → `FeriadosAutomaticos`

Cenário: Alterar horário padrão de funcionamento
- Pré-condição: Sistema com horários padrão
- Passos: Alterar segunda-feira para abrir às 10:00 (em vez de 08:00)
- Resultado esperado: Próximas segundas-feiras abrem às 10:00; reservas às 09:00 são rejeitadas
- Arquivo de teste: `HorarioServiceTest.java` → `AlterarHorarioPadrao`

### Reservas de Sinuca (T2)

Cenário: Criar reserva com horários válidos
- Pré-condição: Mesa 1 disponível para 15/12/2026 de 14:00 a 15:00
- Passos: Criar reserva para João (telefone 999999999)
- Resultado esperado: Reserva criada com ID; pode ser recuperada por ID
- Arquivo de teste: `ReservaSinucaServiceTest.java` → `NovaReserva.testCriarReservaValida`

Cenário: Rejeitar reserva fora do funcionamento (T3 - Manutenção)
- Pré-condição: Estabelecimento funciona 08:00-18:00
- Passos: Tentar criar reserva de 17:30 a 19:00
- Resultado esperado: `ReservaSinucaException` lançada com mensagem clara
- Arquivo de teste: `ReservaSinucaServiceTest.java` → `LimitesFuncionamento.testFimAposOFechamento`

Cenário: Detectar conflito de horários na mesma mesa
- Pré-condição: Reserva existente mesa 1 de 14:00-15:00
- Passos: Tentar criar outra reserva na mesma mesa com sobreposição (14:30-15:30)
- Resultado esperado: Exceção de conflito; segunda reserva não é criada
- Arquivo de teste: `ReservaSinucaServiceTest.java` → `NovaReserva.testConflitoHorarios`

### Relatório de Estoque Baixo (T3)

Cenário: Exportar produtos com estoque ≤ 5
- Pré-condição: 
  - Produto A (estoque 3)
  - Produto B (estoque 8)
  - Produto C (estoque 5)
- Passos: Exportar relatório com limite 5 para arquivo TXT
- Resultado esperado: 
  - Arquivo criado com cabeçalho e data/hora
  - Produtos A e C listados (estoque 3 e 5)
  - Produto B não aparece
  - Ordenados por estoque crescente
- Arquivo de teste: `RelatorioEstoqueBaixoTxtExporterTest.java` → `deveExportarTxtOrdenado`

### Integração T2 + T3

Cenário: Feriado bloqueia reserva
- Pré-condição: Feriado nacional de Natal (25/12) cadastrado
- Passos:
  1. Tentar criar reserva para 25/12/2024
  2. Criar reserva para 26/12/2024 (dia seguinte)
- Resultado esperado: 
  - Passo 1 falha (dia fechado)
  - Passo 2 sucede (dia aberto)
- Arquivo de teste: `T2IntegrationNewFeaturesTest.java`

---

**Executar testes específicos:**
```bash
# Testes de um arquivo específico
mvn test -Dtest=ProdutoServiceTest

# Testes de uma classe aninhada
mvn test -Dtest=ReservaSinucaServiceTest$NovaReserva

# Todos os testes
mvn clean test
```

## Relatório de Testes de funcionalidades do T2

### Produtos (ProdutoServiceTest)

| Teste | Descrição | Status |
| `CadastrarProduto.testCadastrarValido` | Cadastra produto com dados válidos (nome, preço > 0, estoque ≥ 0) |
| `CadastrarProduto.testRejectarSemNome` | Rejeita cadastro sem nome (exceção lançada) |
| `CadastrarProduto.testRejectarPrecoNegativo` | Rejeita preço ≤ 0 |
| `BuscarProduto.testBuscarPorId` | Recupera produto pelo ID único |
| `BuscarProduto.testBuscarPorCodigo` | Localiza produto por código único |
| `ListarProdutos.testListarTodos` | Retorna lista completa de produtos |
| `BuscarPorFaixaPreco.testFaixaValida` | Filtra produtos por intervalo de preço |
| `AtualizarProduto.testAlterarCampos` | Modifica campos de produto existente |
| `DeletarProduto.testRemoverExistente` | Deleta produto; busca subsequente falha |
| `RelatorioEstoqueBaixo.testFiltrarEstoqueBaixo` | Lista produtos com estoque ≤ limite |

Total: 22 testes | Resultado: 22 Passando

### Horários de Funcionamento (HorarioServiceTest)

| Teste | Descrição | Status |
| `VerificarStatus.testStatusAberto` | Verifica se estabelecimento está aberto (horário atual dentro do funcionamento) |
| `VerificarStatus.testStatusFechado` | Detecta quando está fechado (fora do horário) |
| `ListarHorarios.testListarPadrao` | Retorna horários padrão de seg a dom |
| `ListarHorarios.testListarEspeciais` | Lista datas especiais com observações |
| `AlterarHorarioPadrao.testMudarAberturaFechamento` | Altera horários de abertura/fechamento de um dia da semana |
| `CadastrarHorarioEspecial.testAberto` | Cadastra data especial com horário customizado (aberto) |
| `CadastrarHorarioEspecial.testFechado` | Cadastra data especial como "fechado" (ex: feriado) |
| `RemoverHorarioEspecial.testRemover` | Remove especial; volta a vigorar padrão |
| `FeriadosAutomaticos.testCadastrarFeriados` | Cadastra feriados nacionais de um ano |
| `FeriadosAutomaticos.testNaoDuplicar` | Validação contra duplicação de feriados |

Total: 30+ testes | Resultado: 30+ Passando

### Reservas de Sinuca (ReservaSinucaServiceTest)

| Teste | Descrição | Status |
| `NovaReserva.testCriarReservaValida` | Cria reserva com dados válidos; recebe ID único |
| `NovaReserva.testValidarNumeroMesa` | Rejeita mesa fora do intervalo (1-3) |
| `NovaReserva.testValidarDataFutura` | Rejeita data passada |
| `NovaReserva.testConflitoHorarios` | Detecta sobreposição com reserva existente na mesma mesa |
| `NovaReserva.testAceitarHorariosDiferentes` | Permite múltiplas reservas na mesma mesa em horários distintos |
| `ObterMapaReservas.testMapaSimples` | Retorna mapa de ocupação/disponibilidade por mesa |
| `ListarReservas.testListarPorMesa` | Filtra reservas de uma mesa específica |
| `ListarReservas.testListarTodas` | Retorna todas as reservas ativas |
| `RemoverReserva.testRemoverExistente` | Cancela reserva pelo ID; não aparece mais em buscas |
| `RemoverReserva.testRemoverMultiplas` | Cancela múltiplas reservas independentemente |
| `CancelarTodasReservasMesa.testLimparMesa` | Remove todas as reservas de uma mesa |
| `LimitesFuncionamento.testFimAposOFechamento` | Rejeita reserva que termina depois do fechamento |
| `LimitesFuncionamento.testFimExatamenteNoFechamento` | Permite reserva que termina exatamente no fechamento |

Total: 36 testes | Resultado: 36 Passando

### Exportação de Relatórios (RelatorioEstoqueBaixoTxtExporterTest)

| Teste | Descrição | Status |
| `testCriarArquivoTxt` | Cria arquivo TXT com nome e caminho corretos |
| `deveExportarTxtOrdenado` | Filtra corretamente e ordena por estoque crescente; cabeçalho inclui data/hora |

Total: 2 testes | Resultado: 2 Passando*

### Integração T2 + T3 (T2IntegrationNewFeaturesTest)

| Teste | Descrição | Status |
| `testIntegracaoCompleta` | Fluxo end-to-end: feriado → bloqueio → permitido no próximo dia → relatório exportado |

Total: 1 teste | **Resultado: 1 Passando

### Resumo de Testes T2

| Categoria | Testes | Status |
| Produtos  | 22 | 22/22 |
| Horários  | 30+| 30+/30+ |
| Reserva   | 36 | 36/36 |
| Relatórios| 2  | 2/2 |
| Integração| 1  | 1/1 |
| TOTAL T2  | 91 | 91/91 |

Conclusão: Todas as funcionalidades originais do T2 continuam operacionais após integração com T3. Nenhuma regressão foi introduzida.
