package org.example.ui;

import org.example.jsoncreator.HorarioJSONExportar;
import org.example.jsoncreator.ProdutoJSONExportar;
import org.example.exceptions.ProdutoException;
import org.example.exceptions.ReservaSinucaException;
import org.example.jsoncreator.ReservaSinucaJSONExportar;
import org.example.models.HorarioFuncionamento;
import org.example.models.Produto;
import org.example.models.ReservaSinuca;
import org.example.models.Usuario;
import org.example.service.HorarioService;
import org.example.service.ProdutoService;
import org.example.service.ReservaSinucaService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MenuAdmin {
    private Scanner scanner;
    private ProdutoService produtoService;
    private HorarioService horarioService;
    private ReservaSinucaService reservaSinucaService;

    public MenuAdmin() {
        this.scanner = new Scanner(System.in);
        this.produtoService = new ProdutoService();
        this.horarioService = new HorarioService();
        this.reservaSinucaService = new ReservaSinucaService();
    }

    public void exibir(Usuario admin) {
        boolean continuar = true;

        while (continuar) {
            try {
                limparTela();
                exibirMenuAdmin();
                int opcao = lerOpcao();

                switch (opcao) {
                    case 1 -> gerenciarProdutos();
                    case 2 -> gerenciarHorarios();
                    case 3 -> gerenciarSinuca();
                    case 0 -> {
                        continuar = false;
                        System.out.println("\n✓ Voltando ao menu principal...");
                        aguardar(1000);
                        limparTela();
                    }
                    default -> System.out.println("\n✗ Opção inválida!");
                }

                if (continuar && opcao != 0) {
                    System.out.println("\nPressione ENTER para continuar...");
                    scanner.nextLine();
                }

            } catch (Exception e) {
                System.err.println("\n✗ Erro: " + e.getMessage());
                scanner.nextLine();
            }
        }
    }

    private void exibirMenuAdmin() {
        System.out.println("═".repeat(50));
        System.out.println("              📊 MENU ADMINISTRATIVO");
        System.out.println("═".repeat(50));
        System.out.println();
        System.out.println("  [1] 📦 Gerenciar Produtos");
        System.out.println("  [2] 🕐 Gerenciar Horários");
        System.out.println("  [3] 🎱 Gerenciar Mesas de Sinuca");
        System.out.println("  [0] ⬅️ Voltar");
        System.out.println();
        System.out.println("═".repeat(50));
        System.out.print("Escolha uma opção: ");
    }

    private void gerenciarProdutos() {
        boolean continuar = true;

        while (continuar) {
            try {
                limparTela();
                System.out.println("\n╔══════════════════════════════════════════════════╗");
                System.out.println("║            📦 GERENCIAMENTO DE PRODUTOS          ║");
                System.out.println("╚══════════════════════════════════════════════════╝\n");

                exibirMenuProdutos();
                int opcao = lerOpcao();

                switch (opcao) {
                    case 1 -> cadastrarProduto();
                    case 2 -> listarTodosProdutos();
                    case 3 -> buscarProduto();
                    case 4 -> atualizarProduto();
                    case 5 -> deletarProduto();
                    case 6 -> gerenciarEstoque();
                    case 7 -> listarEstoqueBaixo();
                    case 0 -> {
                        continuar = false;
                        System.out.println("\n✓ Voltando...");
                        aguardar(500);
                        limparTela();
                    }
                    default -> System.out.println("\n✗ Opção inválida!");
                }

                if (continuar && opcao != 0) {
                    System.out.println("\nPressione ENTER para continuar...");
                    scanner.nextLine();
                }

            } catch (Exception e) {
                System.err.println("\n✗ Erro: " + e.getMessage());
                scanner.nextLine();
            }
        }
    }

    private void exibirMenuProdutos() {
        System.out.println("  [1] ➕ Cadastrar Produto");
        System.out.println("  [2] 📋 Listar Todos os Produtos");
        System.out.println("  [3] 🔍 Buscar Produto");
        System.out.println("  [4] ✏️ Atualizar Produto");
        System.out.println("  [5] 🗑️ Deletar Produto");
        System.out.println("  [6] 📊 Gerenciar Estoque");
        System.out.println("  [7] ⚠️ Produtos com Estoque Baixo");
        System.out.println("  [0] ⬅️ Voltar");
        System.out.println();
        System.out.print("Escolha uma opção: ");
    }

    private void cadastrarProduto() {
        System.out.println("\n--- ➕ CADASTRAR NOVO PRODUTO ---\n");

        try {
            System.out.print("Nome: ");
            String nome = scanner.nextLine();

            System.out.print("Código: ");
            String codigo = scanner.nextLine();

            System.out.print("Descrição: ");
            String descricao = scanner.nextLine();

            System.out.print("Preço (R$): ");
            BigDecimal preco = new BigDecimal(scanner.nextLine());

            System.out.print("Quantidade em Estoque: ");
            int quantidade = Integer.parseInt(scanner.nextLine());

            System.out.print("Categoria: ");
            String categoria = scanner.nextLine();

            Produto produto = new Produto(nome, codigo, preco, quantidade);
            produto.setDescricao(descricao);
            produto.setCategoria(categoria);

            produto = produtoService.cadastrarProduto(produto);
            System.out.println("\n✓ Produto cadastrado com sucesso! ID: " + produto.getId());

            ProdutoJSONExportar.exportarParaJSON("produtos.json");
        } catch (ProdutoException e) {
            System.err.println("\n✗ Erro: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("\n✗ Erro: Valor numérico inválido");
        }
    }

    private void listarTodosProdutos() {
        System.out.println("\n--- 📋 LISTA DE PRODUTOS ---\n");

        try {
            List<Produto> produtos = produtoService.listarTodosProdutos();

            if (produtos.isEmpty()) {
                System.out.println("Nenhum produto cadastrado.");
                return;
            }

            System.out.println("Total de produtos: " + produtos.size());
            System.out.println("-".repeat(100));

            for (Produto p : produtos) {
                System.out.println(p);
            }

        } catch (ProdutoException e) {
            System.err.println("\n✗ Erro: " + e.getMessage());
        }
    }

    private void buscarProduto() {
        System.out.println("\n--- 🔍 BUSCAR PRODUTO ---");
        System.out.println("1. Buscar por ID");
        System.out.println("2. Buscar por Código");
        System.out.println("3. Buscar por Nome");
        System.out.println("4. Buscar por Categoria");
        System.out.print("Escolha: ");

        try {
            int opcao = Integer.parseInt(scanner.nextLine());

            switch (opcao) {
                case 1 -> {
                    System.out.print("Digite o ID: ");
                    Long id = Long.parseLong(scanner.nextLine());
                    Produto produto = produtoService.buscarProdutoPorId(id);
                    System.out.println("\n" + produto);
                }
                case 2 -> {
                    System.out.print("Digite o Código: ");
                    String codigo = scanner.nextLine();
                    Produto produto = produtoService.buscarProdutoPorCodigo(codigo);
                    System.out.println("\n" + produto);
                }
                case 3 -> {
                    System.out.print("Digite o Nome (ou parte): ");
                    String nome = scanner.nextLine();
                    List<Produto> produtos = produtoService.buscarProdutosPorNome(nome);

                    if (produtos.isEmpty()) {
                        System.out.println("Nenhum produto encontrado.");
                    } else {
                        System.out.println("\nProdutos encontrados: " + produtos.size());
                        produtos.forEach(System.out::println);
                    }
                }
                case 4 -> {
                    try {
                        System.out.print("Digite a categoria: ");
                        String categoria = scanner.nextLine();

                        List<Produto> produtos = produtoService.buscarProdutosPorCategoria(categoria);

                        if (produtos.isEmpty()) {
                            System.out.println("\nNenhum produto encontrado na categoria: " + categoria);
                        } else {
                            System.out.println("\nProdutos na categoria '" + categoria + "': " + produtos.size());
                            System.out.println("-".repeat(100));
                            produtos.forEach(System.out::println);
                        }

                    } catch (ProdutoException e) {
                        System.err.println("\n✗ " + e.getMessage());
                    }
                }
                default -> System.out.println("Opção inválida!");
            }

        } catch (ProdutoException e) {
            System.err.println("\n✗ " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("\n✗ Erro: Valor numérico inválido");
        }
    }

    private void atualizarProduto() {
        System.out.println("\n--- ✏️ ATUALIZAR PRODUTO ---\n");

        try {
            System.out.print("Digite o ID do produto: ");
            Long id = Long.parseLong(scanner.nextLine());

            Produto produto = produtoService.buscarProdutoPorId(id);
            System.out.println("\nProduto atual: " + produto);

            System.out.print("\nNovo Nome (Enter para manter): ");
            String nome = scanner.nextLine();
            if (!nome.trim().isEmpty()) {
                produto.setNome(nome);
            }

            System.out.print("Nova Descrição (Enter para manter): ");
            String descricao = scanner.nextLine();
            if (!descricao.trim().isEmpty()) {
                produto.setDescricao(descricao);
            }

            System.out.print("Novo Preço (Enter para manter): ");
            String precoStr = scanner.nextLine();
            if (!precoStr.trim().isEmpty()) {
                produto.setPreco(new BigDecimal(precoStr));
            }

            System.out.print("Nova Quantidade (Enter para manter): ");
            String qtdStr = scanner.nextLine();
            if (!qtdStr.trim().isEmpty()) {
                produto.setQuantidadeEstoque(Integer.parseInt(qtdStr));
            }

            System.out.print("Nova Categoria (Enter para manter): ");
            String categoria = scanner.nextLine();
            if (!categoria.trim().isEmpty()) {
                produto.setCategoria(categoria);
            }

            produtoService.atualizarProduto(produto);
            System.out.println("\n✓ Produto atualizado com sucesso!");

            ProdutoJSONExportar.exportarParaJSON("produtos.json");

        } catch (ProdutoException e) {
            System.err.println("\n✗ " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("\n✗ Erro: Valor numérico inválido");
        }
    }

    private void deletarProduto() {
        System.out.println("\n--- 🗑️ DELETAR PRODUTO ---\n");

        try {
            System.out.print("Digite o ID do produto: ");
            Long id = Long.parseLong(scanner.nextLine());

            Produto produto = produtoService.buscarProdutoPorId(id);
            System.out.println("\nProduto: " + produto);

            System.out.print("\nConfirma exclusão? (S/N): ");
            String confirmacao = scanner.nextLine();

            if (confirmacao.equalsIgnoreCase("S")) {
                produtoService.deletarProduto(id);
                System.out.println("\n✓ Produto deletado com sucesso!");

                ProdutoJSONExportar.exportarParaJSON("produtos.json");
            } else {
                System.out.println("\n✗ Operação cancelada.");
            }

        } catch (ProdutoException e) {
            System.err.println("\n✗ " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("\n✗ Erro: Valor numérico inválido");
        }
    }

    private void gerenciarEstoque() {
        System.out.println("\n--- 📊 GERENCIAR ESTOQUE ---");
        System.out.println("1. Adicionar Estoque (Entrada)");
        System.out.println("2. Remover Estoque (Saída/Venda)");
        System.out.print("Escolha: ");

        try {
            int opcao = Integer.parseInt(scanner.nextLine());

            System.out.print("Digite o ID do produto: ");
            Long id = Long.parseLong(scanner.nextLine());

            Produto produto = produtoService.buscarProdutoPorId(id);
            System.out.println("\nProduto: " + produto);
            System.out.println("Estoque atual: " + produto.getQuantidadeEstoque());

            System.out.print("\nQuantidade: ");
            int quantidade = Integer.parseInt(scanner.nextLine());

            switch (opcao) {
                case 1 -> {
                    produtoService.adicionarEstoque(id, quantidade);
                    System.out.println("\n✓ Estoque adicionado com sucesso!");
                    ProdutoJSONExportar.exportarParaJSON("produtos.json");
                    Produto atualizado = produtoService.buscarProdutoPorId(id);
                    System.out.println("Novo estoque: " + atualizado.getQuantidadeEstoque());
                }
                case 2 -> {
                    produtoService.removerEstoque(id, quantidade);
                    System.out.println("\n✓ Estoque removido com sucesso!");
                    ProdutoJSONExportar.exportarParaJSON("produtos.json");
                    Produto atualizado = produtoService.buscarProdutoPorId(id);
                    System.out.println("Novo estoque: " + atualizado.getQuantidadeEstoque());
                }
                default -> System.out.println("Opção inválida!");
            }

        } catch (ProdutoException e) {
            System.err.println("\n✗ " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("\n✗ Erro: Valor numérico inválido");
        }
    }

    private void listarEstoqueBaixo() {
        System.out.println("\n--- ⚠️ PRODUTOS COM ESTOQUE BAIXO ---\n");

        try {
            System.out.print("Limite mínimo de estoque: ");
            int limite = Integer.parseInt(scanner.nextLine());

            List<Produto> produtos = produtoService.listarProdutosEstoqueBaixo(limite);

            if (produtos.isEmpty()) {
                System.out.println("\n✓ Nenhum produto com estoque baixo.");
            } else {
                System.out.println("\n⚠️  Produtos com estoque <= " + limite + ":");
                System.out.println("-".repeat(100));
                produtos.forEach(System.out::println);
            }

        } catch (ProdutoException e) {
            System.err.println("\n✗ " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("\n✗ Erro: Valor numérico inválido");
        }
    }

    private void gerenciarHorarios() {
        boolean continuar = true;

        while (continuar) {
            try {
                limparTela();
                System.out.println("\n╔══════════════════════════════════════════════════╗");
                System.out.println("║          🕐 GERENCIAMENTO DE HORÁRIOS            ║");
                System.out.println("╚══════════════════════════════════════════════════╝\n");

                System.out.println("  [1] 📋 Ver Horários Atuais");
                System.out.println("  [2] ✏️ Alterar Horário Padrão da Semana");
                System.out.println("  [3] ➕ Adicionar Horário Especial (Feriado)");
                System.out.println("  [4] 🗑️ Remover Horário Especial");
                System.out.println("  [0] ⬅️ Voltar");
                System.out.println();
                System.out.print("Escolha: ");

                int opcao = lerOpcao();

                switch (opcao) {
                    case 1 -> visualizarHorariosAdmin();
                    case 2 -> alterarHorarioPadrao();
                    case 3 -> adicionarHorarioEspecial();
                    case 4 -> removerHorarioEspecial();
                    case 0 -> {
                        continuar = false;
                        System.out.println("\n✓ Voltando...");
                        aguardar(500);
                    }
                    default -> System.out.println("\n✗ Opção inválida!");
                }

                if (continuar && opcao != 0) {
                    System.out.println("\nPressione ENTER para continuar...");
                    scanner.nextLine();
                }

            } catch (Exception e) {
                System.err.println("\n✗ Erro: " + e.getMessage());
                scanner.nextLine();
            }
        }
    }

    private void visualizarHorariosAdmin() {
        System.out.println("\n--- 📋 HORÁRIOS CADASTRADOS ---\n");

        try {
            HorarioService.StatusFuncionamento status = horarioService.verificarStatusAtual();
            System.out.println("Status atual: " + status.getMensagem());
            System.out.println(status.getProximoEvento());
            System.out.println();

            System.out.println("HORÁRIOS PADRÃO:");
            System.out.println("-".repeat(50));
            List<HorarioFuncionamento> horarios = horarioService.listarHorariosPadrao();
            horarios.forEach(System.out::println);

            System.out.println("\nHORÁRIOS ESPECIAIS:");
            System.out.println("-".repeat(50));
            List<HorarioFuncionamento> especiais = horarioService.listarHorariosEspeciais();
            if (especiais.isEmpty()) {
                System.out.println("Nenhum horário especial cadastrado.");
            } else {
                especiais.forEach(System.out::println);
            }

        } catch (SQLException e) {
            System.err.println("\n✗ Erro ao carregar horários: " + e.getMessage());
        }
    }

    private void alterarHorarioPadrao() {
        System.out.println("\n--- ✏️ ALTERAR HORÁRIO PADRÃO ---\n");

        System.out.println("Selecione o dia da semana:");
        System.out.println("1. Segunda-feira");
        System.out.println("2. Terça-feira");
        System.out.println("3. Quarta-feira");
        System.out.println("4. Quinta-feira");
        System.out.println("5. Sexta-feira");
        System.out.println("6. Sábado");
        System.out.println("7. Domingo");
        System.out.print("\nDia: ");

        try {
            int dia = Integer.parseInt(scanner.nextLine());
            if (dia < 1 || dia > 7) {
                System.out.println("\n✗ Dia inválido!");
                return;
            }

            DayOfWeek diaSemana = DayOfWeek.of(dia);

            System.out.print("\nA loja ficará fechada neste dia? (S/N): ");
            String fechado = scanner.nextLine();

            HorarioFuncionamento horario = new HorarioFuncionamento();
            horario.setDiaSemana(diaSemana);

            if (fechado.equalsIgnoreCase("S")) {
                horario.setFechado(true);
            } else {
                System.out.print("Horário de abertura (HH:mm): ");
                String abertura = scanner.nextLine();

                System.out.print("Horário de fechamento (HH:mm): ");
                String fechamento = scanner.nextLine();

                horario.setHorarioAbertura(LocalTime.parse(abertura));
                horario.setHorarioFechamento(LocalTime.parse(fechamento));
            }

            horarioService.atualizarHorarioPadrao(horario);
            System.out.println("\n✓ Horário atualizado com sucesso!");
            HorarioJSONExportar.exportarPadrao("horarios_padrao.json");

        } catch (NumberFormatException e) {
            System.err.println("\n✗ Número inválido!");
        } catch (DateTimeParseException e) {
            System.err.println("\n✗ Formato de hora inválido! Use HH:mm (ex: 08:00)");
        } catch (SQLException e) {
            System.err.println("\n✗ Erro ao atualizar horário: " + e.getMessage());
        }
    }

    private void adicionarHorarioEspecial() {
        System.out.println("\n--- ➕ ADICIONAR HORÁRIO ESPECIAL ---\n");
        System.out.println("(Para feriados, eventos especiais, etc.)\n");

        try {
            System.out.print("Data (dd/MM/yyyy): ");
            String dataStr = scanner.nextLine();
            LocalDate data = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            System.out.print("Observação (ex: Natal, Feriado): ");
            String observacao = scanner.nextLine();

            System.out.print("A loja ficará fechada neste dia? (S/N): ");
            String fechado = scanner.nextLine();

            HorarioFuncionamento horario = new HorarioFuncionamento();
            horario.setDataEspecial(data);
            horario.setObservacao(observacao);

            if (fechado.equalsIgnoreCase("S")) {
                horario.setFechado(true);
            } else {
                System.out.print("Horário de abertura (HH:mm): ");
                String abertura = scanner.nextLine();

                System.out.print("Horário de fechamento (HH:mm): ");
                String fechamento = scanner.nextLine();

                horario.setHorarioAbertura(LocalTime.parse(abertura));
                horario.setHorarioFechamento(LocalTime.parse(fechamento));
            }

            horarioService.cadastrarHorarioEspecial(horario);
            System.out.println("\n✓ Horário especial cadastrado com sucesso!");
            HorarioJSONExportar.exportarEspeciais("horarios_especiais.json");

        } catch (DateTimeParseException e) {
            System.err.println("\n✗ Formato inválido! Use dd/MM/yyyy para data e HH:mm para hora");
        } catch (SQLException e) {
            System.err.println("\n✗ Erro ao cadastrar horário: " + e.getMessage());
        }
    }

    private void removerHorarioEspecial() {
        System.out.println("\n--- 🗑️ REMOVER HORÁRIO ESPECIAL ---\n");

        try {
            List<HorarioFuncionamento> especiais = horarioService.listarHorariosEspeciais();

            if (especiais.isEmpty()) {
                System.out.println("Nenhum horário especial cadastrado.");
                return;
            }

            System.out.println("Horários especiais cadastrados:");
            especiais.forEach(System.out::println);

            System.out.print("\nData para remover (dd/MM/yyyy): ");
            String dataStr = scanner.nextLine();
            LocalDate data = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            System.out.print("Confirma remoção? (S/N): ");
            String confirmacao = scanner.nextLine();

            if (confirmacao.equalsIgnoreCase("S")) {
                horarioService.removerHorarioEspecial(data);
                System.out.println("\n✓ Horário especial removido com sucesso!");
                HorarioJSONExportar.exportarEspeciais("horarios_especiais.json");
            } else {
                System.out.println("\n✗ Operação cancelada.");
            }

        } catch (DateTimeParseException e) {
            System.err.println("\n✗ Formato inválido! Use dd/MM/yyyy");
        } catch (SQLException e) {
            System.err.println("\n✗ Erro ao remover horário: " + e.getMessage());
        }
    }

    private void gerenciarSinuca() {
        boolean continuar = true;

        while (continuar) {
            try {
                limparTela();
                System.out.println("\n╔══════════════════════════════════════════════════╗");
                System.out.println("║        🎱 GERENCIAMENTO DE MESAS DE SINUCA       ║");
                System.out.println("╚══════════════════════════════════════════════════╝\n");

                System.out.println("  [1] 📋 Visualizar Mapa de Reservas");
                System.out.println("  [2] ➕ Nova Reserva");
                System.out.println("  [3] 🗑️ Remover Reserva Específica");
                System.out.println("  [4] ⚠️ Cancelar Todas Reservas de uma Mesa");
                System.out.println("  [5] 🔍 Buscar Reservas");
                System.out.println("  [0] ⬅️ Voltar");
                System.out.println();
                System.out.print("Escolha: ");

                int opcao = lerOpcao();

                switch (opcao) {
                    case 1 -> visualizarMapaReservas();
                    case 2 -> novaReserva();
                    case 3 -> removerReservaEspecifica();
                    case 4 -> cancelarTodasReservasMesa();
                    case 5 -> buscarReservas();
                    case 0 -> {
                        continuar = false;
                        System.out.println("\n✓ Voltando...");
                        aguardar(500);
                    }
                    default -> System.out.println("\n✗ Opção inválida!");
                }

                if (continuar && opcao != 0) {
                    System.out.println("\nPressione ENTER para continuar...");
                    scanner.nextLine();
                }

            } catch (Exception e) {
                System.err.println("\n✗ Erro: " + e.getMessage());
                scanner.nextLine();
            }
        }
    }

    private void visualizarMapaReservas() {
        System.out.println("\n=== MAPA DE RESERVAS DAS MESAS ===\n");

        try {
            LocalDate data = LocalDate.now();
            String dataFormatada = data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String diaSemana = getDiaSemanaPortugues(data.getDayOfWeek());

            System.out.printf("📅 Data: %s (%s)%n%n", dataFormatada, diaSemana);

            Map<Integer, List<ReservaSinuca>> mapaReservas =
                    reservaSinucaService.obterMapaReservasPorData(data);

            for (int mesa = 1; mesa <= 3; mesa++) {
                System.out.println("--------------------------------------------------");
                System.out.println("🎱 Mesa " + mesa + ":");

                List<ReservaSinuca> reservas = mapaReservas.get(mesa);

                if (reservas == null || reservas.isEmpty()) {
                    System.out.println("   ✅ Livre o dia todo");
                } else {
                    for (ReservaSinuca r : reservas) {
                        System.out.printf("   🔸 %s - %s (Tel: %s)%n",
                                r.getPeriodoFormatado(),
                                r.getNomeCliente(),
                                r.getTelefoneCliente() != null ? r.getTelefoneCliente() : "N/A");
                    }
                }

                System.out.println();
            }

            System.out.println("--------------------------------------------------");

        } catch (DateTimeParseException e) {
            System.err.println("\n✗ Formato de data inválido! Use dd/MM/yyyy");
        } catch (ReservaSinucaException e) {
            System.err.println("\n✗ " + e.getMessage());
        }
    }


    private void novaReserva() {
        System.out.println("\n--- ➕ NOVA RESERVA ---\n");

        try {
            System.out.print("Número da mesa (1-3): ");
            int mesa = Integer.parseInt(scanner.nextLine());

            System.out.print("Nome do cliente: ");
            String nome = scanner.nextLine();

            System.out.print("Telefone do cliente: ");
            String telefone = scanner.nextLine();

            LocalDate data = LocalDate.now();

            System.out.print("Horário de início (HH:mm): ");
            LocalTime horaInicio = LocalTime.parse(scanner.nextLine());

            System.out.print("1 - 30 minutos" +
                    "\n2 - 1 hora" +
                    "\nDuração da reserva: ");
            int opcao = Integer.parseInt(scanner.nextLine());

            LocalTime horaFim;
            if (opcao == 1) {
                horaFim = horaInicio.plusMinutes(30);
            } else if (opcao == 2) {
                horaFim = horaInicio.plusHours(1);
            } else {
                System.err.println("\n✗ Opção inválida! Usando 30 minutos como padrão.");
                horaFim = horaInicio.plusMinutes(30);
            }

            System.out.print("Observações (opcional): ");
            String obs = scanner.nextLine();

            ReservaSinuca reserva = new ReservaSinuca(mesa, nome, data, horaInicio, horaFim);
            reserva.setTelefoneCliente(telefone);
            if (!obs.trim().isEmpty()) {
                reserva.setObservacoes(obs);
            }

            reserva = reservaSinucaService.criarReserva(reserva);
            System.out.println("\n✓ Reserva criada com sucesso! ID: " + reserva.getId());
            System.out.println("Mesa " + mesa + " reservada para " + nome);
            System.out.println("Período: " + reserva.getPeriodoFormatado());

            ReservaSinucaJSONExportar.exportarParaJSON("reservas_sinuca.json");

        } catch (NumberFormatException e) {
            System.err.println("\n✗ Número inválido!");
        } catch (DateTimeParseException e) {
            System.err.println("\n✗ Formato inválido! Use HH:mm para hora.");
        } catch (ReservaSinucaException e) {
            System.err.println("\n✗ " + e.getMessage());
        }
    }

    private void removerReservaEspecifica() {
        System.out.println("\n--- 🗑️ REMOVER RESERVA ESPECÍFICA ---\n");

        try {
            System.out.println("Escolha o método:");
            System.out.println("1. Por ID da reserva");
            System.out.println("2. Por Mesa e Horário");
            System.out.print("\nEscolha: ");

            int metodo = Integer.parseInt(scanner.nextLine());

            if (metodo == 1) {
                List<ReservaSinuca> reservas = reservaSinucaService.listarTodasReservasAtivas();

                if (reservas.isEmpty()) {
                    System.out.println("\nNenhuma reserva ativa encontrada.");
                    return;
                }

                System.out.println("\nReservas ativas:");
                System.out.println("-".repeat(80));
                for (ReservaSinuca r : reservas) {
                    System.out.printf("ID: %d | %s%n", r.getId(), r);
                }
                System.out.println("-".repeat(80));

                System.out.print("\nID da reserva para cancelar: ");
                Long id = Long.parseLong(scanner.nextLine());

                System.out.print("Confirma cancelamento? (S/N): ");
                if (scanner.nextLine().equalsIgnoreCase("S")) {
                    reservaSinucaService.cancelarReserva(id);
                    System.out.println("\n✓ Reserva cancelada com sucesso!");

                    ReservaSinucaJSONExportar.exportarParaJSON("reservas_sinuca.json");
                }

            } else if (metodo == 2) {
                System.out.print("Número da mesa (1-3): ");
                int mesa = Integer.parseInt(scanner.nextLine());

                LocalDate data = LocalDate.now();

                List<ReservaSinuca> reservas =
                        reservaSinucaService.listarReservasPorMesa(mesa, data);

                if (reservas.isEmpty()) {
                    System.out.println("\nNenhuma reserva encontrada para esta mesa nesta data.");
                    return;
                }

                System.out.println("\nReservas da Mesa " + mesa + " em " +
                        data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ":");
                System.out.println("-".repeat(80));
                for (ReservaSinuca r : reservas) {
                    System.out.printf("%s | %s | Tel: %s%n",
                            r.getPeriodoFormatado(),
                            r.getNomeCliente(),
                            r.getTelefoneCliente());
                }
                System.out.println("-".repeat(80));

                System.out.print("\nHorário de início da reserva para cancelar (HH:mm): ");
                LocalTime hora = LocalTime.parse(scanner.nextLine());

                System.out.print("Confirma cancelamento? (S/N): ");
                if (scanner.nextLine().equalsIgnoreCase("S")) {
                    reservaSinucaService.cancelarReservaPorHorario(mesa, data, hora);
                    System.out.println("\n✓ Reserva cancelada com sucesso!");

                    ReservaSinucaJSONExportar.exportarParaJSON("reservas_sinuca.json");
                }
            }

        } catch (NumberFormatException e) {
            System.err.println("\n✗ Número inválido!");
        } catch (DateTimeParseException e) {
            System.err.println("\n✗ Formato inválido!");
        } catch (ReservaSinucaException e) {
            System.err.println("\n✗ " + e.getMessage());
        }
    }

    private void cancelarTodasReservasMesa() {
        System.out.println("\n--- ⚠️ CANCELAR TODAS RESERVAS DE UMA MESA ---\n");
        System.out.println("Use esta opção quando uma mesa quebrar ou ficar indisponível.\n");

        try {
            System.out.print("Número da mesa (1-3): ");
            int mesa = Integer.parseInt(scanner.nextLine());

            LocalDate data = LocalDate.now();

            List<ReservaSinuca> reservas =
                    reservaSinucaService.listarReservasPorMesa(mesa, data);

            if (reservas.isEmpty()) {
                System.out.println("\nNenhuma reserva encontrada para esta mesa nesta data.");
                return;
            }

            System.out.println("\nReservas que serão canceladas:");
            System.out.println("-".repeat(80));
            for (ReservaSinuca r : reservas) {
                System.out.println(r);
            }
            System.out.println("-".repeat(80));

            System.out.print("\n⚠️  Confirma cancelamento de TODAS as reservas acima? (S/N): ");
            if (scanner.nextLine().equalsIgnoreCase("S")) {
                int total = reservaSinucaService.cancelarTodasReservasDaMesa(mesa, data);
                System.out.println("\n✓ " + total + " reserva(s) cancelada(s) com sucesso!");
                System.out.println("💡 Dica: Entre em contato com os clientes para reagendar.");

                ReservaSinucaJSONExportar.exportarParaJSON("reservas_sinuca.json");
            } else {
                System.out.println("\n✗ Operação cancelada.");
            }

        } catch (NumberFormatException e) {
            System.err.println("\n✗ Número inválido!");
        } catch (DateTimeParseException e) {
            System.err.println("\n✗ Formato de data inválido! Use dd/MM/yyyy");
        } catch (ReservaSinucaException e) {
            System.err.println("\n✗ " + e.getMessage());
        }
    }

    private void buscarReservas() {
        System.out.println("\n--- 🔍 BUSCAR RESERVAS ---\n");

        try {
            System.out.println("1. Todas as reservas ativas");
            System.out.println("2. Reservas de uma mesa específica");
            System.out.print("\nEscolha: ");

            int opcao = Integer.parseInt(scanner.nextLine());

            if (opcao == 1) {
                List<ReservaSinuca> reservas = reservaSinucaService.listarTodasReservasAtivas();

                if (reservas.isEmpty()) {
                    System.out.println("\nNenhuma reserva ativa encontrada.");
                } else {
                    System.out.println("\n📋 Total de reservas ativas: " + reservas.size());
                    System.out.println("=".repeat(80));
                    for (ReservaSinuca r : reservas) {
                        System.out.printf("ID: %d | %s%n", r.getId(), r);
                    }
                }

            } else if (opcao == 2) {
                System.out.print("Número da mesa (1-3): ");
                int mesa = Integer.parseInt(scanner.nextLine());

                LocalDate data = LocalDate.now();

                List<ReservaSinuca> reservas =
                        reservaSinucaService.listarReservasPorMesa(mesa, data);

                if (reservas.isEmpty()) {
                    System.out.println("\nNenhuma reserva encontrada.");
                } else {
                    System.out.println("\n📋 Mesa " + mesa + " - " +
                            data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    System.out.println("=".repeat(80));
                    for (ReservaSinuca r : reservas) {
                        System.out.println(r);
                    }
                }
            }

        } catch (NumberFormatException e) {
            System.err.println("\n✗ Número inválido!");
        } catch (DateTimeParseException e) {
            System.err.println("\n✗ Formato inválido!");
        } catch (ReservaSinucaException e) {
            System.err.println("\n✗ " + e.getMessage());
        }
    }

    private String getDiaSemanaPortugues(DayOfWeek dia) {
        return switch (dia) {
            case MONDAY -> "Segunda-feira";
            case TUESDAY -> "Terça-feira";
            case WEDNESDAY -> "Quarta-feira";
            case THURSDAY -> "Quinta-feira";
            case FRIDAY -> "Sexta-feira";
            case SATURDAY -> "Sábado";
            case SUNDAY -> "Domingo";
        };
    }

    private int lerOpcao() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void limparTela() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    private void aguardar(int milissegundos) {
        try {
            Thread.sleep(milissegundos);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void fechar() {
        try {
            ProdutoJSONExportar.exportarParaJSON("produtos.json");
            HorarioJSONExportar.exportarPadrao("horarios_padrao.json");
            HorarioJSONExportar.exportarEspeciais("horarios_especiais.json");
            ReservaSinucaJSONExportar.exportarParaJSON("reservas_sinuca.json");

            produtoService.fechar();
            horarioService.fechar();
            reservaSinucaService.fechar();
        } catch (Exception e) {
            System.err.println("\n⚠️ Aviso: Erro ao salvar dados - " + e.getMessage());
            e.printStackTrace();
        }
    }
}