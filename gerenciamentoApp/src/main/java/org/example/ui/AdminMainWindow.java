package org.example.ui;

import org.example.models.Usuario;
import org.example.service.ReservaSinucaService;
import org.example.models.ReservaSinuca;
import org.example.service.ProdutoService;
import org.example.models.Produto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.util.regex.Pattern;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;

import org.example.service.HorarioService;
import org.example.models.HorarioFuncionamento;

public class AdminMainWindow extends JFrame {
    private Usuario admin;
    private JTabbedPane abas;
    private JLabel labelUsuario;

    public AdminMainWindow(Usuario admin) {
        this.admin = admin;

        inicializarJanela();
        criarComponentes();
    }

    private void inicializarJanela() {
        setTitle("Administração - Dilto's App");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setResizable(true);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void criarComponentes() {
        JPanel painelPrincipal = new JPanel();
        painelPrincipal.setLayout(new BorderLayout(10, 10));
        painelPrincipal.setBackground(new Color(240, 248, 255));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Painel Superior - Header
        JPanel painelHeader = criarPainelHeader();
        painelPrincipal.add(painelHeader, BorderLayout.NORTH);

        // Painel Central - Abas
        abas = new JTabbedPane(JTabbedPane.TOP);
        abas.setBackground(new Color(240, 248, 255));
        abas.setFont(new Font("Arial", Font.BOLD, 12));

        abas.addTab("Produtos", criarAbaProdutos());
        abas.addTab("Horários", criarAbaHorarios());
        abas.addTab("Mesas de Sinuca", criarAbaSinuca());

        painelPrincipal.add(abas, BorderLayout.CENTER);

        // Painel Inferior - Rodapé
        JPanel painelRodape = criarPainelRodape();
        painelPrincipal.add(painelRodape, BorderLayout.SOUTH);

        add(painelPrincipal);
    }

    private JPanel criarPainelHeader() {
        JPanel painel = new JPanel();
        painel.setLayout(new BorderLayout(20, 0));
        painel.setBackground(new Color(70, 130, 180));
        painel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel labelTitulo = new JLabel("PAINEL ADMINISTRATIVO");
        labelTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        labelTitulo.setForeground(Color.WHITE);

        labelUsuario = new JLabel("Administrador: " + admin.getNome());
        labelUsuario.setFont(new Font("Arial", Font.PLAIN, 12));
        labelUsuario.setForeground(new Color(200, 220, 240));
        labelUsuario.setHorizontalAlignment(SwingConstants.RIGHT);

        painel.add(labelTitulo, BorderLayout.WEST);
        painel.add(labelUsuario, BorderLayout.EAST);

        return painel;
    }

    private JPanel criarAbaProdutos() {
        JPanel painel = new JPanel();
        painel.setLayout(new BorderLayout(10, 10));
        painel.setBackground(new Color(240, 248, 255));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Painel de entrada
        JPanel painelEntrada = new JPanel();
        painelEntrada.setLayout(new GridBagLayout());
        painelEntrada.setBackground(Color.WHITE);
        painelEntrada.setBorder(BorderFactory.createTitledBorder("Adicionar Novo Produto"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtNome = new JTextField(25);
        JTextField txtPreco = new JTextField(12);
        JSpinner spinnerQtd = new JSpinner(new SpinnerNumberModel(1, 0, 10000, 1));
        JTextArea txtDescricao = new JTextArea(2, 35);
        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);

        gbc.gridx = 0; gbc.gridy = 0;
        painelEntrada.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1;
        painelEntrada.add(txtNome, gbc);

        gbc.gridx = 2;
        painelEntrada.add(new JLabel("Preço (R$):"), gbc);
        gbc.gridx = 3;
        painelEntrada.add(txtPreco, gbc);

        gbc.gridx = 4;
        painelEntrada.add(new JLabel("Quantidade:"), gbc);
        gbc.gridx = 5;
        painelEntrada.add(spinnerQtd, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        painelEntrada.add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 2; gbc.gridwidth = 4;
        painelEntrada.add(new JScrollPane(txtDescricao), gbc);

        JButton btnAdicionar = new JButton("Adicionar Produto");
        btnAdicionar.setBackground(new Color(76, 175, 80));
        btnAdicionar.setForeground(Color.WHITE);
        btnAdicionar.setOpaque(true);
        btnAdicionar.setBorderPainted(false);
        btnAdicionar.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 6;
        painelEntrada.add(btnAdicionar, gbc);

        // criar um painel superior que empilha o formulário e a barra de busca
        JPanel norte = new JPanel();
        norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));
        norte.setOpaque(false);
        norte.add(painelEntrada);
        norte.add(Box.createVerticalStrut(8));
        // painel de busca (entre adicionar e tabela)
        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelBusca.setBackground(new Color(250, 250, 250));
        painelBusca.setBorder(BorderFactory.createTitledBorder("Buscar Produtos"));
        painelBusca.add(new JLabel("Buscar:"));
        JTextField txtBuscaAdmin = new JTextField(30);
        JButton btnBuscarAdmin = new JButton("Buscar");
        btnBuscarAdmin.setBackground(new Color(33,150,243)); btnBuscarAdmin.setForeground(Color.WHITE); btnBuscarAdmin.setOpaque(true); btnBuscarAdmin.setBorderPainted(false);
        painelBusca.add(txtBuscaAdmin);
        painelBusca.add(btnBuscarAdmin);

        // controles para verificar estoque baixo
        painelBusca.add(Box.createHorizontalStrut(12));
        painelBusca.add(new JLabel("Estoque abaixo de:"));
        JSpinner spinnerLimite = new JSpinner(new SpinnerNumberModel(5, 0, Integer.MAX_VALUE, 1));
        painelBusca.add(spinnerLimite);
        JButton btnVerificarEstoque = new JButton("Verificar");
        btnVerificarEstoque.setBackground(new Color(255, 193, 7)); btnVerificarEstoque.setOpaque(true); btnVerificarEstoque.setBorderPainted(false);
        painelBusca.add(btnVerificarEstoque);
        JLabel lblResultadoEstoque = new JLabel(" ");
        lblResultadoEstoque.setFont(new Font("Arial", Font.BOLD, 12));
        painelBusca.add(lblResultadoEstoque);
        norte.add(painelBusca);
        painel.add(norte, BorderLayout.NORTH);

        // Painel da tabela
        String[] colunas = {"ID", "Nome", "Preço", "Quantidade", "Descrição"};
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0);
        JTable tabela = new JTable(modelo);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modelo);
        tabela.setRowSorter(sorter);
        tabela.setFont(new Font("Arial", Font.PLAIN, 11));
        tabela.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tabela.setRowHeight(25);

        carregarProdutosTabela(modelo);

        JScrollPane scrollProdutos = new JScrollPane(tabela);
        scrollProdutos.setBorder(BorderFactory.createTitledBorder("Produtos Cadastrados"));
        painel.add(scrollProdutos, BorderLayout.CENTER);

        // Botões de ação
        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        painelBotoes.setBackground(new Color(240, 248, 255));

        JButton btnEditar = new JButton("Editar");
        btnEditar.setBackground(new Color(33, 150, 243));
        btnEditar.setForeground(Color.WHITE);
        btnEditar.setOpaque(true);
        btnEditar.setBorderPainted(false);

        JButton btnRemover = new JButton("Remover");
        btnRemover.setBackground(new Color(244, 67, 54));
        btnRemover.setForeground(Color.WHITE);
        btnRemover.setOpaque(true);
        btnRemover.setBorderPainted(false);

        painelBotoes.add(btnEditar);
        painelBotoes.add(btnRemover);

        painel.add(painelBotoes, BorderLayout.SOUTH);

        // Listeners
        btnAdicionar.addActionListener(e -> {
            ProdutoService svc = new ProdutoService();
            try {
                String nome = txtNome.getText().trim();
                String precoTxt = txtPreco.getText().trim();
                int quantidade = (Integer) spinnerQtd.getValue();
                String descricao = txtDescricao.getText();

                if (nome.isEmpty() || precoTxt.isEmpty()) {
                    JOptionPane.showMessageDialog(painel, "Nome e preço são obrigatórios!", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                java.math.BigDecimal preco;
                try {
                    preco = new java.math.BigDecimal(precoTxt.replace(',', '.'));
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(painel, "Preço inválido! Use formato numérico.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Check if we're editing an existing product
                Object editingIdObj = btnAdicionar.getClientProperty("editingId");
                if (editingIdObj != null) {
                    Long editingId = editingIdObj instanceof Number ? ((Number) editingIdObj).longValue() : Long.parseLong(editingIdObj.toString());
                    try {
                        Produto p = new Produto();
                        p.setId(editingId);
                        p.setNome(nome);
                        p.setPreco(preco);
                        p.setQuantidadeEstoque(quantidade);
                        p.setDescricao(descricao);

                        svc.atualizarProduto(p);
                        // refresh table
                        modelo.setRowCount(0);
                        carregarProdutosTabela(modelo);

                        // reset form and editing state
                        txtNome.setText("");
                        txtPreco.setText("");
                        spinnerQtd.setValue(1);
                        txtDescricao.setText("");
                        btnAdicionar.putClientProperty("editingId", null);
                        btnAdicionar.setText("Adicionar Produto");

                        JOptionPane.showMessageDialog(painel, "Produto atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    } catch (org.example.exceptions.ProdutoException ex) {
                        JOptionPane.showMessageDialog(painel, "Erro ao atualizar produto: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    try {
                        Produto p = new Produto();
                        p.setNome(nome);
                        p.setPreco(preco);
                        p.setQuantidadeEstoque(quantidade);
                        p.setDescricao(descricao);

                        Produto criado = svc.cadastrarProduto(p);
                        // refresh table
                        modelo.setRowCount(0);
                        carregarProdutosTabela(modelo);

                        txtNome.setText("");
                        txtPreco.setText("");
                        spinnerQtd.setValue(1);
                        txtDescricao.setText("");

                        JOptionPane.showMessageDialog(painel, "Produto adicionado com sucesso! (ID: " + criado.getId() + ")", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    } catch (org.example.exceptions.ProdutoException ex) {
                        JOptionPane.showMessageDialog(painel, "Erro ao cadastrar produto: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } finally {
                try { svc.fechar(); } catch (Exception ignored) {}
            }
        });

        // Edit button behavior: populate form and switch add button to save mode
        btnEditar.addActionListener(ev -> {
            int linhaView = tabela.getSelectedRow();
            if (linhaView == -1) {
                JOptionPane.showMessageDialog(painel, "Selecione um produto para editar!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int linha = tabela.convertRowIndexToModel(linhaView);

            Object idObj = modelo.getValueAt(linha, 0);
            Long id = idObj instanceof Number ? ((Number) idObj).longValue() : Long.parseLong(idObj.toString());
            ProdutoService svc2 = new ProdutoService();
            try {
                Produto p = svc2.buscarProdutoPorId(id);
                if (p == null) {
                    JOptionPane.showMessageDialog(painel, "Produto não encontrado no banco de dados.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // populate fields
                txtNome.setText(p.getNome() != null ? p.getNome() : "");
                txtPreco.setText(p.getPreco() != null ? p.getPreco().toString() : "");
                spinnerQtd.setValue(p.getQuantidadeEstoque() != null ? p.getQuantidadeEstoque() : 0);
                txtDescricao.setText(p.getDescricao() != null ? p.getDescricao() : "");

                // set editing marker
                btnAdicionar.putClientProperty("editingId", id);
                btnAdicionar.setText("Salvar Alterações");
            } catch (org.example.exceptions.ProdutoException ex) {
                JOptionPane.showMessageDialog(painel, "Erro ao carregar produto: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            } finally {
                try { svc2.fechar(); } catch (Exception ignored) {}
            }
        });

        btnRemover.addActionListener(e -> {
            int linhaView = tabela.getSelectedRow();
            if (linhaView == -1) {
                JOptionPane.showMessageDialog(painel, "Selecione um produto para remover!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirmacao = JOptionPane.showConfirmDialog(painel, "Remover este produto do banco de dados? Esta ação é irreversível.", "Confirmação", JOptionPane.YES_NO_OPTION);
            if (confirmacao != JOptionPane.YES_OPTION) return;

            ProdutoService svc = new ProdutoService();
            try {
                int linha = tabela.convertRowIndexToModel(linhaView);
                Object idObj = modelo.getValueAt(linha, 0);
                Long id = idObj instanceof Number ? ((Number) idObj).longValue() : Long.parseLong(idObj.toString());
                try {
                    svc.deletarProduto(id);
                    modelo.removeRow(linha);
                    JOptionPane.showMessageDialog(painel, "Produto removido do banco de dados!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } catch (org.example.exceptions.ProdutoException ex) {
                    JOptionPane.showMessageDialog(painel, "Erro ao remover produto: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } finally {
                try { svc.fechar(); } catch (Exception ignored) {}
            }
        });

        // ação de busca: filtrar pela coluna Nome (índice 1 no modelo)
        btnBuscarAdmin.addActionListener(ev -> {
            String termo = txtBuscaAdmin.getText().trim();
            if (termo.isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                try { sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(termo), 1)); } catch (Exception ex) { sorter.setRowFilter(null); }
            }
        });
        txtBuscaAdmin.addActionListener(ev -> btnBuscarAdmin.doClick());

        // ação de verificar estoque baixo
        btnVerificarEstoque.addActionListener(ev -> {
            int limite = (Integer) spinnerLimite.getValue();
            ProdutoService svcEst = new ProdutoService();
            try {
                try {
                    java.util.List<org.example.models.Produto> abaixo = svcEst.gerarRelatorioEstoqueBaixo(limite);
                    int qtd = abaixo != null ? abaixo.size() : 0;
                    lblResultadoEstoque.setText(qtd + " produtos abaixo de " + limite);
                    
                    // Gerar relatório TXT automaticamente
                    try {
                        java.nio.file.Path caminhoRelatorio = java.nio.file.Path.of("relatorios", "estoque-baixo.txt");
                        svcEst.exportarRelatorioEstoqueBaixoTxt(limite, caminhoRelatorio);
                        System.out.println("✓ Relatório TXT gerado: " + caminhoRelatorio.toAbsolutePath());
                    } catch (Exception exRelatorio) {
                        System.err.println("Aviso: Não foi possível gerar relatório TXT: " + exRelatorio.getMessage());
                    }
                    
                    // também mostrar detalhe em dialog se houverem produtos
                    if (qtd > 0) {
                        StringBuilder sb = new StringBuilder();
                        for (org.example.models.Produto p : abaixo) {
                            sb.append(p.getNome()).append(" (" + (p.getQuantidadeEstoque()!=null?p.getQuantidadeEstoque():0) + ")\n");
                        }
                        JTextArea ta = new JTextArea(sb.toString());
                        ta.setEditable(false);
                        ta.setRows(Math.min(10, qtd));
                        JScrollPane sp = new JScrollPane(ta);
                        sp.setPreferredSize(new Dimension(400, Math.min(200, qtd*20 + 20)));
                        JOptionPane.showMessageDialog(painel, sp, "Produtos com estoque abaixo de " + limite + "\n(Relatório salvo em: relatorios/estoque-baixo.txt)", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (org.example.exceptions.ProdutoException ex) {
                    JOptionPane.showMessageDialog(painel, "Erro ao verificar estoque: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } finally {
                try { svcEst.fechar(); } catch (Exception ignored) {}
            }
        });

        return painel;
    }

    private JPanel criarAbaHorarios() {
        JPanel painel = new JPanel();
        painel.setLayout(new BorderLayout(10, 10));
        painel.setBackground(new Color(240, 248, 255));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Painel de entrada - reorganizado para evitar sobreposição
        JPanel painelEntrada = new JPanel(new GridBagLayout());
        painelEntrada.setBackground(Color.WHITE);
        painelEntrada.setBorder(BorderFactory.createTitledBorder("Configurar Horário"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] dias = {"Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado", "Domingo"};
        JComboBox<String> comboDia = new JComboBox<>(dias);
        JComboBox<String> comboTipo = new JComboBox<>(new String[]{"Padrão", "Especial"});
        JTextField txtDataEspecial = new JTextField(10);
        txtDataEspecial.setToolTipText("DD/MM/YYYY");
        JTextField txtObservacao = new JTextField(30);
        JTextField txtAbertura = new JTextField(8);
        JTextField txtFechamento = new JTextField(8);
        JCheckBox checkAberto = new JCheckBox("Aberto");
        checkAberto.setSelected(true);
        JButton btnSalvarHorario = new JButton("Salvar Horário");
        btnSalvarHorario.setBackground(new Color(76, 175, 80));
        btnSalvarHorario.setForeground(Color.WHITE);
        btnSalvarHorario.setOpaque(true);
        btnSalvarHorario.setBorderPainted(false);

        // Primeira linha: Tipo | Dia | DataEspecial (visível só para Especial)
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        painelEntrada.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.2;
        painelEntrada.add(comboTipo, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        painelEntrada.add(new JLabel("Dia:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.3;
        painelEntrada.add(comboDia, gbc);

        gbc.gridx = 4; gbc.weightx = 0;
        JLabel lblDataEspecial = new JLabel("Data Especial:");
        painelEntrada.add(lblDataEspecial, gbc);
        gbc.gridx = 5; gbc.weightx = 0.4;
        painelEntrada.add(txtDataEspecial, gbc);

        // Segunda linha: Abertura | Fechamento | Aberto checkbox
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        painelEntrada.add(new JLabel("Abertura (HH:MM):"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.3;
        painelEntrada.add(txtAbertura, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        painelEntrada.add(new JLabel("Fechamento (HH:MM):"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.3;
        painelEntrada.add(txtFechamento, gbc);

        gbc.gridx = 4; gbc.weightx = 0;
        painelEntrada.add(checkAberto, gbc);

        // Terceira linha: Observação (ocupa várias colunas)
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 6; gbc.weightx = 1.0;
        JLabel lblObservacao = new JLabel("Observação (opcional):");
        painelEntrada.add(lblObservacao, gbc);
        gbc.gridy = 3;
        painelEntrada.add(txtObservacao, gbc);

        // Listener único para esconder/mostrar campos baseado no tipo
        comboTipo.addActionListener(ev -> {
            boolean ehEspecial = "Especial".equals(comboTipo.getSelectedItem());
            txtDataEspecial.setVisible(ehEspecial);
            lblDataEspecial.setVisible(ehEspecial);
            txtObservacao.setVisible(ehEspecial);
            lblObservacao.setVisible(ehEspecial);
            painelEntrada.revalidate();
            painelEntrada.repaint();
        });
        // Inicializar escondido (Padrão é default)
        txtDataEspecial.setVisible(false);
        lblDataEspecial.setVisible(false);
        txtObservacao.setVisible(false);
        lblObservacao.setVisible(false);
        
        // Quarta linha: botão salvar
        gbc.gridy = 4; gbc.gridwidth = 6; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER;
        painelEntrada.add(btnSalvarHorario, gbc);

        painel.add(painelEntrada, BorderLayout.NORTH);

        // Tabela de horários padrão (sem coluna Status)
        String[] colunas = {"Dia", "Abertura", "Fechamento"};
        DefaultTableModel modeloPadrao = new DefaultTableModel(colunas, 0);
        JTable tabelaPadrao = new JTable(modeloPadrao);
        tabelaPadrao.setFont(new Font("Arial", Font.PLAIN, 11));
        tabelaPadrao.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tabelaPadrao.setRowHeight(25);

        // Tabela de horários especiais (lado direito)
        String[] colEspec = {"Data", "Abertura", "Fechamento", "Observação"};
        DefaultTableModel modeloEspec = new DefaultTableModel(colEspec, 0);
        JTable tabelaEspec = new JTable(modeloEspec);
        tabelaEspec.setFont(new Font("Arial", Font.PLAIN, 11));
        tabelaEspec.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tabelaEspec.setRowHeight(22);

        // painel central com as duas tabelas lado a lado
        JScrollPane scrollPadrao = new JScrollPane(tabelaPadrao);
        scrollPadrao.setBorder(BorderFactory.createTitledBorder("Horários Padrão"));

        JScrollPane scrollEspec = new JScrollPane(tabelaEspec);
        scrollEspec.setBorder(BorderFactory.createTitledBorder("Horários Especiais"));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPadrao, scrollEspec);
        split.setResizeWeight(0.5);
        split.setContinuousLayout(true);
        split.setOneTouchExpandable(true);
        // set proportional divider after layout (use mid by default)
        split.setDividerLocation(0.5);
        split.setBorder(null);

        // ensure each side has a reasonable minimum size
        scrollPadrao.setMinimumSize(new Dimension(300, 200));
        scrollEspec.setMinimumSize(new Dimension(300, 200));

        painel.add(split, BorderLayout.CENTER);

        // carregar dados iniciais
        carregarHorariosTabela(modeloPadrao);
        carregarHorariosEspeciaisTabela(modeloEspec);

        // Botões
        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new FlowLayout(FlowLayout.RIGHT));
        painelBotoes.setBackground(new Color(240, 248, 255));

        JButton btnRemoverHorario = new JButton("Remover Especial");
        btnRemoverHorario.setBackground(new Color(255, 152, 0));
        btnRemoverHorario.setForeground(Color.WHITE);
        btnRemoverHorario.setOpaque(true);
        btnRemoverHorario.setBorderPainted(false);

        painelBotoes.add(btnRemoverHorario);

        painel.add(painelBotoes, BorderLayout.SOUTH);

        btnSalvarHorario.addActionListener(e -> {
            String tipo = (String) comboTipo.getSelectedItem();
            String abertura = txtAbertura.getText();
            String fechamento = txtFechamento.getText();
            boolean abertoFlag = checkAberto.isSelected();

            HorarioService hs = new HorarioService();
            try {
                if ("Especial".equals(tipo)) {
                    String dataTxt = txtDataEspecial.getText().trim();
                    if (dataTxt.isEmpty()) {
                        JOptionPane.showMessageDialog(painel, "Data especial é obrigatória para horário especial.", "Erro", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    LocalDate data;
                    try {
                        data = LocalDate.parse(dataTxt, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(painel, "Data inválida. Use DD/MM/YYYY", "Erro", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    HorarioFuncionamento hEspecial = new HorarioFuncionamento();
                    hEspecial.setDataEspecial(data);
                    hEspecial.setObservacao(txtObservacao.getText().trim());
                    if (!abertoFlag) {
                        hEspecial.setFechado(true);
                    } else {
                        if (!validarHora(abertura) || !validarHora(fechamento)) {
                            JOptionPane.showMessageDialog(painel, "Formato de hora inválido! Use HH:MM", "Erro", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        hEspecial.setHorarioAbertura(LocalTime.parse(abertura));
                        hEspecial.setHorarioFechamento(LocalTime.parse(fechamento));
                        hEspecial.setFechado(false);
                    }

                    try {
                        hs.cadastrarHorarioEspecial(hEspecial);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(painel, "Erro ao cadastrar horário especial: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                } else {
                    // padrão
                    String dia = (String) comboDia.getSelectedItem();
                    DayOfWeek diaSemana = switch (dia) {
                        case "Segunda" -> DayOfWeek.MONDAY;
                        case "Terça" -> DayOfWeek.TUESDAY;
                        case "Quarta" -> DayOfWeek.WEDNESDAY;
                        case "Quinta" -> DayOfWeek.THURSDAY;
                        case "Sexta" -> DayOfWeek.FRIDAY;
                        case "Sábado" -> DayOfWeek.SATURDAY;
                        default -> DayOfWeek.SUNDAY;
                    };

                    HorarioFuncionamento hPadrao = new HorarioFuncionamento();
                    hPadrao.setDiaSemana(diaSemana);
                    if (!abertoFlag) {
                        hPadrao.setFechado(true);
                    } else {
                        if (!validarHora(abertura) || !validarHora(fechamento)) {
                            JOptionPane.showMessageDialog(painel, "Formato de hora inválido! Use HH:MM", "Erro", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        hPadrao.setHorarioAbertura(LocalTime.parse(abertura));
                        hPadrao.setHorarioFechamento(LocalTime.parse(fechamento));
                        hPadrao.setFechado(false);
                    }

                    try {
                        hs.atualizarHorarioPadrao(hPadrao);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(painel, "Erro ao atualizar horário padrão: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                // refresh
                modeloPadrao.setRowCount(0);
                modeloEspec.setRowCount(0);
                carregarHorariosTabela(modeloPadrao);
                carregarHorariosEspeciaisTabela(modeloEspec);

                JOptionPane.showMessageDialog(painel, "Horário salvo!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } finally {
                try { hs.fechar(); } catch (Exception ignored) {}
            }
        });

        // remover horário especial (não marca padrão como fechado)
        btnRemoverHorario.addActionListener(ev -> {
            HorarioService hs = new HorarioService();
            try {
                int linhaEspec = tabelaEspec.getSelectedRow();
                if (linhaEspec != -1) {
                    Object dataObj = modeloEspec.getValueAt(linhaEspec, 0);
                    if (dataObj != null) {
                        try {
                            LocalDate dt = LocalDate.parse(dataObj.toString(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                            int conf = JOptionPane.showConfirmDialog(painel, "Remover horário especial de " + dataObj + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
                            if (conf == JOptionPane.YES_OPTION) {
                                hs.removerHorarioEspecial(dt);
                                modeloEspec.removeRow(linhaEspec);
                                JOptionPane.showMessageDialog(painel, "Horário especial removido.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                            }
                            return;
                        } catch (Exception ex) {
                            // parse failed, continue
                        }
                    }
                }

                // não mais marca o dia padrão como fechado — apenas indicar instrução ao usuário
                JOptionPane.showMessageDialog(painel, "Selecione um horário especial para remover.\nPara marcar um dia padrão como fechado, use a opção apropriada no formulário de edição de horários.", "Aviso", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(painel, "Erro ao processar ação: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            } finally {
                try { hs.fechar(); } catch (Exception ignored) {}
            }
        });

        return painel;
    }

    private JPanel criarAbaSinuca() {
        JPanel painel = new JPanel(new BorderLayout(10,10));
        painel.setBackground(new Color(240,248,255));
        painel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        String[] colunas = {"ID", "Cliente", "Data", "Hora Início", "Hora Fim"};
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0);
        JTable tabela = new JTable(modelo);
        tabela.setFont(new Font("Arial", Font.PLAIN, 11));
        tabela.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tabela.setRowHeight(25);

        // carregar do banco
        ReservaSinucaService svc = new ReservaSinucaService();
        try {
            try { svc.removerReservasPassadas(); } catch (Exception ignore) {}
            List<ReservaSinuca> reservas = svc.listar();
            // Ordenar por data e hora
            reservas.sort((r1, r2) -> {
                int cmpData = r1.getDataReserva().compareTo(r2.getDataReserva());
                if (cmpData != 0) return cmpData;
                return r1.getHoraInicio().compareTo(r2.getHoraInicio());
            });
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (ReservaSinuca r : reservas) {
                String data = r.getDataReserva() != null ? r.getDataReserva().format(dtf) : "";
                modelo.addRow(new Object[]{r.getId(), r.getNomeCliente(), data, r.getHoraInicio().toString(), r.getHoraFim().toString()});
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar reservas: " + e.getMessage());
        } finally {
            try { svc.fechar(); } catch (Exception ignored) {}
        }

        JScrollPane scrollReservas = new JScrollPane(tabela);
        scrollReservas.setBorder(BorderFactory.createTitledBorder("Reservas de Mesas"));
        painel.add(scrollReservas, BorderLayout.CENTER);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.setBackground(new Color(240,248,255));
        JButton btnRemover = new JButton("Remover (DB)");
        btnRemover.setBackground(new Color(244,67,54));
        btnRemover.setForeground(Color.WHITE);
        btnRemover.setOpaque(true);
        btnRemover.setBorderPainted(false);
        botoes.add(btnRemover);
        painel.add(botoes, BorderLayout.SOUTH);

        btnRemover.addActionListener(e -> {
            int linha = tabela.getSelectedRow();
            if (linha == -1) {
                JOptionPane.showMessageDialog(painel, "Selecione uma reserva!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int conf = JOptionPane.showConfirmDialog(painel, "Remover esta reserva do banco de dados? Esta ação é irreversível.", "Confirmação", JOptionPane.YES_NO_OPTION);
            if (conf == JOptionPane.YES_OPTION) {
                try {
                    Object idObj = modelo.getValueAt(linha, 0);
                    long id = idObj instanceof Number ? ((Number)idObj).longValue() : Long.parseLong(idObj.toString());
                    ReservaSinucaService s2 = new ReservaSinucaService();
                    s2.deletarReserva(id);
                    s2.fechar();
                    modelo.removeRow(linha);
                    JOptionPane.showMessageDialog(painel, "Reserva removida do banco de dados!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(painel, "Erro ao remover reserva: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return painel;
    }

    private JPanel criarPainelRodape() {
        JPanel painel = new JPanel();
        painel.setLayout(new BorderLayout(10, 0));
        painel.setBackground(new Color(240, 248, 255));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel labelHora = new JLabel();
        atualizarReloge(labelHora);

        JButton btnVoltar = new JButton("Voltar ao Menu Principal");
        btnVoltar.setBackground(new Color(158, 158, 158));
        btnVoltar.setForeground(Color.BLACK);
        btnVoltar.setOpaque(true);
        btnVoltar.setBorderPainted(false);
        btnVoltar.setFont(new Font("Arial", Font.BOLD, 12));

        btnVoltar.addActionListener(e -> dispose());

        painel.add(labelHora, BorderLayout.WEST);
        painel.add(btnVoltar, BorderLayout.EAST);

        return painel;
    }

    private void atualizarReloge(JLabel label) {
        Timer timer = new Timer(1000, e -> {
            LocalDateTime agora = LocalDateTime.now();
            label.setText(agora.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        });
        timer.start();
    }

    private void carregarProdutosTabela(DefaultTableModel modelo) {
        // Carrega produtos do banco via ProdutoService
        ProdutoService produtoService = new ProdutoService();
        try {
            var produtos = produtoService.listarTodosProdutos();
            for (Produto p : produtos) {
                String precoStr = p.getPreco() != null ? String.format("R$ %.2f", p.getPreco()) : "-";
                modelo.addRow(new Object[]{p.getId(), p.getNome(), precoStr, p.getQuantidadeEstoque(), p.getDescricao()});
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar produtos (admin): " + e.getMessage());
        } finally {
            try { produtoService.fechar(); } catch (Exception ignored) {}
        }
    }

    private void carregarHorariosTabela(DefaultTableModel modelo) {
        HorarioService hs = new HorarioService();
        try {
            var padrao = hs.listarHorariosPadrao();
            for (HorarioFuncionamento h : padrao) {
                // traduzir para português curto
                String diaPt = switch (h.getDiaSemana()) {
                    case MONDAY -> "Segunda";
                    case TUESDAY -> "Terça";
                    case WEDNESDAY -> "Quarta";
                    case THURSDAY -> "Quinta";
                    case FRIDAY -> "Sexta";
                    case SATURDAY -> "Sábado";
                    default -> "Domingo";
                };

                String abertura = h.getHorarioAbertura() != null ? h.getHorarioAbertura().toString() : "-";
                String fechamento = h.getHorarioFechamento() != null ? h.getHorarioFechamento().toString() : "-";
                modelo.addRow(new Object[]{diaPt, abertura, fechamento});
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar horários padrão: " + e.getMessage());
        } finally {
            try { hs.fechar(); } catch (Exception ignored) {}
        }
    }

    private void carregarHorariosEspeciaisTabela(DefaultTableModel modelo) {
        HorarioService hs = new HorarioService();
        try {
            var esp = hs.listarHorariosEspeciais();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (HorarioFuncionamento h : esp) {
                String data = h.getDataEspecial() != null ? h.getDataEspecial().format(dtf) : "";
                String abertura = h.getHorarioAbertura() != null ? h.getHorarioAbertura().toString() : "-";
                String fechamento = h.getHorarioFechamento() != null ? h.getHorarioFechamento().toString() : "-";
                String obs = h.getObservacao() != null ? h.getObservacao() : "";
                modelo.addRow(new Object[]{data, abertura, fechamento, obs});
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar horários especiais: " + e.getMessage());
        } finally {
            try { hs.fechar(); } catch (Exception ignored) {}
        }
    }

    private boolean validarHora(String hora) {
        return hora.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$");
    }

    public static void main(String[] args) {
        Usuario admin = new Usuario("Admin", "admin@diltos.com", "admin", Usuario.TipoUsuario.ADMIN);
        admin.setId(1L);
        AdminMainWindow janela = new AdminMainWindow(admin);
        janela.setVisible(true);
    }
}
