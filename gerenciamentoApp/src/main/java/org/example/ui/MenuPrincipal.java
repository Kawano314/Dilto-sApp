package org.example.ui;

import org.example.models.Usuario;
import org.example.service.HorarioService;

import javax.swing.*;
import java.awt.*;

public class MenuPrincipal {
    private JFrame janelaGrafica;

    public MenuPrincipal() {
        carregarFeriadosNacionais();
        registrarShutdownHook();
    }

    private void carregarFeriadosNacionais() {
        try {
            HorarioService horarioService = new HorarioService();
            int anoAtual = java.time.LocalDate.now().getYear();
            horarioService.cadastrarFeriadosNacionais(anoAtual);
            // Cadastrar também para o próximo ano, caso aplicação esteja rodando próximo ao fim do ano
            horarioService.cadastrarFeriadosNacionais(anoAtual + 1);
        } catch (Exception e) {
            System.err.println("Aviso: Erro ao carregar feriados nacionais: " + e.getMessage());
            // Não interrompe a execução da aplicação
        }
    }

    public void exibir() {
        // Criar interface gráfica
        criarInterfaceGrafica();
    }

    private void criarInterfaceGrafica() {
        janelaGrafica = new JFrame();
        janelaGrafica.setTitle("Dilto's App - Sistema de Gerenciamento");
        janelaGrafica.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janelaGrafica.setSize(550, 450);
        janelaGrafica.setLocationRelativeTo(null);
        janelaGrafica.setResizable(false);
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Painel principal
        JPanel painelPrincipal = new JPanel();
        painelPrincipal.setLayout(new BorderLayout(10, 10));
        painelPrincipal.setBackground(new Color(240, 248, 255));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Painel do título
        JPanel painelTitulo = new JPanel();
        painelTitulo.setBackground(new Color(70, 130, 180));
        painelTitulo.setPreferredSize(new Dimension(0, 100));
        
        JLabel labelTitulo = new JLabel("Dilto's App");
        labelTitulo.setFont(new Font("Arial", Font.BOLD, 36));
        labelTitulo.setForeground(Color.WHITE);
        painelTitulo.add(labelTitulo);

        // Painel de botões
        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new GridLayout(3, 1, 10, 10));
        painelBotoes.setBackground(new Color(240, 248, 255));

        JButton btnAdmin = criarBotao("Acessar como ADMINISTRADOR", new Color(34,139,34));
        JButton btnCliente = criarBotao("Acessar como CLIENTE", new Color(30,144,255));
        JButton btnSair = criarBotao("Sair", new Color(220,20,60));

        painelBotoes.add(btnAdmin);
        painelBotoes.add(btnCliente);
        painelBotoes.add(btnSair);

        // Adicionar listeners
        btnAdmin.addActionListener(e -> acessarComoAdminGrafico());
        btnCliente.addActionListener(e -> acessarComoClienteGrafico());
        btnSair.addActionListener(e -> System.exit(0));

        painelPrincipal.add(painelTitulo, BorderLayout.NORTH);
        painelPrincipal.add(painelBotoes, BorderLayout.CENTER);

        janelaGrafica.add(painelPrincipal);
        janelaGrafica.setVisible(true);
    }

    private JButton criarBotao(String texto, Color cor) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("Arial", Font.BOLD, 14));
        botao.setBackground(cor);
        botao.setForeground(Color.WHITE);
        botao.setOpaque(true);
        botao.setBorderPainted(false);
        botao.setFocusPainted(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        botao.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                botao.setBackground(cor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                botao.setBackground(cor);
            }
        });
        
        return botao;
    }

    private void acessarComoAdminGrafico() {
        JDialog dialog = new JDialog(janelaGrafica, "Login Administrativo", true);
        dialog.setSize(350, 200);
        dialog.setLocationRelativeTo(janelaGrafica);
        dialog.setResizable(false);

        JPanel painel = new JPanel();
        painel.setLayout(new GridBagLayout());
        painel.setBackground(new Color(240, 248, 255));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Usuário
        gbc.gridx = 0;
        gbc.gridy = 0;
        painel.add(new JLabel("Usuário:"), gbc);

        gbc.gridx = 1;
        JTextField txtUsuario = new JTextField(15);
        painel.add(txtUsuario, gbc);

        // Senha
        gbc.gridx = 0;
        gbc.gridy = 1;
        painel.add(new JLabel("Senha:"), gbc);

        gbc.gridx = 1;
        JPasswordField txtSenha = new JPasswordField(15);
        painel.add(txtSenha, gbc);

        // Botões
        JButton btnLogin = new JButton("Login");
        JButton btnCancelar = new JButton("Cancelar");

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JPanel painelBotoes = new JPanel();
        painelBotoes.add(btnLogin);
        painelBotoes.add(btnCancelar);
        painel.add(painelBotoes, gbc);

        btnLogin.addActionListener(e -> {
            String usuario = txtUsuario.getText();
            String senha = new String(txtSenha.getPassword());

            if (usuario.equalsIgnoreCase("admin") && senha.equals("admin")) {
                Usuario admin = new Usuario("Administrador", "admin@loja.com", "admin", Usuario.TipoUsuario.ADMIN);
                admin.setId(1L);
                dialog.dispose();
                janelaGrafica.setVisible(false);
                AdminMainWindow janelaAdmin = new AdminMainWindow(admin);
                janelaAdmin.setVisible(true);
                janelaAdmin.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                        janelaGrafica.setVisible(true);
                    }
                });
            } else {
                JOptionPane.showMessageDialog(dialog, "Usuário ou senha inválidos!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());

        dialog.add(painel);
        dialog.setVisible(true);
    }

    private void acessarComoClienteGrafico() {
        Usuario cliente = new Usuario("Cliente", "cliente@loja.com", "", Usuario.TipoUsuario.CLIENTE);
        cliente.setId(999L);
        
        janelaGrafica.setVisible(false);
        ClienteMainWindow janelaCliente = new ClienteMainWindow(cliente);
        janelaCliente.setVisible(true);
        janelaCliente.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                janelaGrafica.setVisible(true);
            }
        });
    }

    private void registrarShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Shutdown hook - pode ser usado para salvar dados se necessário
        }));
    }

}