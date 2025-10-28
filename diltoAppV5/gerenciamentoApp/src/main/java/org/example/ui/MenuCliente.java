package org.example.ui;

import org.example.exceptions.ProdutoException;
import org.example.exceptions.ReservaSinucaException;
import org.example.jsoncreator.HorarioJSONExportar;
import org.example.jsoncreator.ProdutoJSONExportar;
import org.example.jsoncreator.ReservaSinucaJSONExportar;
import org.example.models.HorarioFuncionamento;
import org.example.models.Produto;
import org.example.models.ReservaSinuca;
import org.example.models.Usuario;
import org.example.service.HorarioService;
import org.example.service.ProdutoService;
import org.example.service.ReservaSinucaService;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MenuCliente {
    private Scanner scanner;
    private ProdutoService produtoService;
    private HorarioService horarioService;
    private ReservaSinucaService reservaSinucaService;

    public MenuCliente() {
        this.scanner = new Scanner(System.in);
        this.produtoService = new ProdutoService();
        this.horarioService = new HorarioService();
        this.reservaSinucaService = new ReservaSinucaService();
    }

    public void exibir(Usuario cliente) {
        boolean continuar = true;

        while (continuar) {
            try {
                limparTela();
                exibirMenuCliente();
                int opcao = lerOpcao();

                switch (opcao) {
                    case 1 -> buscarProduto();
                    case 2 -> visualizarHorarios();
                    case 3 -> visualizarMesasSinuca();
                    case 4 -> novaReserva();
                    case 0 -> {
                        continuar = false;
                        System.out.println("\n✓ Voltando ao menu principal...");
                        aguardar(1000);
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

    private void exibirMenuCliente() {
        System.out.println("═".repeat(50));
        System.out.println("              📋 MENU DO CLIENTE");
        System.out.println("═".repeat(50));
        System.out.println();
        System.out.println("  [1] 🔍 Buscar Produto");
        System.out.println("  [2] 🕒 Horários de Funcionamento");
        System.out.println("  [3] 🎱 Visualizar Mesas de Sinuca");
        System.out.println("  [4] ➕ Nova Reserva de Mesa");
        System.out.println("  [0] ⬅️ Voltar");
        System.out.println();
        System.out.println("═".repeat(50));
        System.out.print("Escolha uma opção: ");
    }

    private void buscarProduto() {
        System.out.println("\n--- 🔍 BUSCAR PRODUTO ---");
        System.out.println("1. Buscar por Nome");
        System.out.println("2. Buscar por Categoria");
        System.out.print("Escolha: ");

        try {
            int opcao = Integer.parseInt(scanner.nextLine());

            switch (opcao) {
                case 1 -> {
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
                case 2 -> {
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

    private void visualizarHorarios() {
        System.out.println("\n--- 📋 HORÁRIOS DE FUNCIONAMENTO ---\n");

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

    private void visualizarMesasSinuca() {
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
            System.err.println("⚠️ Aviso: Erro ao salvar dados - " + e.getMessage());
        }
    }

}