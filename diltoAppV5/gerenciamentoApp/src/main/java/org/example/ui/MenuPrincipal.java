package org.example.ui;

import org.example.jsoncreator.HorarioJSONExportar;
import org.example.jsoncreator.ProdutoJSONExportar;
import org.example.jsoncreator.ReservaSinucaJSONExportar;
import org.example.models.Usuario;

import java.util.Scanner;

public class MenuPrincipal {
    private Scanner scanner;
    private MenuAdmin menuAdmin;
    private MenuCliente menuCliente;

    public MenuPrincipal() {
        this.scanner = new Scanner(System.in);
        this.menuAdmin = new MenuAdmin();
        this.menuCliente = new MenuCliente();
        registrarShutdownHook();
    }

    public void exibir() {
        limparTela();
        exibirCabecalho();

        boolean continuar = true;

        while (continuar) {
            try {
                exibirMenuInicial();
                int opcao = lerOpcao();

                switch (opcao) {
                    case 1 -> acessarComoAdmin();
                    case 2 -> acessarComoCliente();
                    case 0 -> {
                        continuar = false;
                        menuAdmin.fechar();
                        menuCliente.fechar();
                    }
                    default -> System.out.println("\n✗ Opção inválida!");
                }

            } catch (Exception e) {
                System.err.println("\n✗ Erro: " + e.getMessage());
                scanner.nextLine();
            }
        }

        scanner.close();
    }

    private void exibirCabecalho() {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║                                                  ║");
        System.out.println("║     🍽️  SISTEMA DE GERENCIAMENTO  🍽️             ║");
        System.out.println("║         Loja de Conveniência v2.0                ║");
        System.out.println("║                                                  ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println();
    }

    private void exibirMenuInicial() {
        System.out.println("\n" + "═".repeat(50));
        System.out.println("            🏠 BEM-VINDO - MENU INICIAL");
        System.out.println("═".repeat(50));
        System.out.println();
        System.out.println("  [1] 👨‍💼 Acessar como ADMINISTRADOR");
        System.out.println("  [2] 👤 Acessar como CLIENTE");
        System.out.println("  [0] 🚪 Sair do Sistema");
        System.out.println();
        System.out.println("═".repeat(50));
        System.out.print("Escolha uma opção: ");
    }

    private void acessarComoAdmin() {
        limparTela();
        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║            🔐 ACESSO ADMINISTRATIVO              ║");
        System.out.println("╚══════════════════════════════════════════════════╝\n");

        System.out.print("Usuário: ");
        String email = scanner.nextLine();

        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        if (email.equalsIgnoreCase("admin") && senha.equals("admin")) {
            Usuario admin = new Usuario("Administrador", "admin@loja.com", "admin", Usuario.TipoUsuario.ADMIN);
            admin.setId(1L);

            System.out.println("\n✓ Login realizado com sucesso!");
            System.out.println("Bem-vindo, " + admin.getNome() + "!");
            aguardar(1500);

            menuAdmin.exibir(admin);
        } else {
            System.out.println("\n✗ Email ou senha inválidos!");
            aguardar(2000);
        }
    }

    private void acessarComoCliente() {
        limparTela();
        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║           🛒 BEM-VINDO AO CATÁLOGO               ║");
        System.out.println("╚══════════════════════════════════════════════════╝\n");

        Usuario cliente = new Usuario("Cliente", "cliente@loja.com", "", Usuario.TipoUsuario.CLIENTE);
        cliente.setId(999L);

        System.out.println("Carregando catálogo de produtos...");
        aguardar(800);

        menuCliente.exibir(cliente);
    }

    private void registrarShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                ProdutoJSONExportar.exportarParaJSON("produtos.json");
                HorarioJSONExportar.exportarPadrao("horarios_padrao.json");
                HorarioJSONExportar.exportarEspeciais("horarios_especiais.json");
                ReservaSinucaJSONExportar.exportarParaJSON("reservas_sinuca.json");
            } catch (Exception e) {
                System.err.println("\n⚠️ Erro ao salvar: " + e.getMessage());
            }
        }));
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
}