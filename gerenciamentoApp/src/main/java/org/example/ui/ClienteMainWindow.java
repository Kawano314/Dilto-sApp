package org.example.ui;

import org.example.models.Usuario;
import org.example.models.ReservaSinuca;
import org.example.service.ReservaSinucaService;
import org.example.service.HorarioService;
import org.example.models.HorarioFuncionamento;
import org.example.service.ProdutoService;
import org.example.models.Produto;
import org.example.exceptions.ReservaSinucaException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;

public class ClienteMainWindow extends JFrame {
    private Usuario cliente;

    public ClienteMainWindow(Usuario cliente) {
        this.cliente = cliente;
        inicializarJanela();
        criarComponentes();
    }

    private void inicializarJanela() {
        setTitle("Cliente - Dilto's App");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);
        setResizable(true);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void criarComponentes() {
        JPanel painelPrincipal = new JPanel(new BorderLayout(10,10));
        painelPrincipal.setBackground(new Color(240, 248, 255));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(70,130,180));
        header.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        JLabel titulo = new JLabel("Área do Cliente - Dilto's App");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        JLabel bemVindo = new JLabel("Olá, " + cliente.getNome());
        bemVindo.setForeground(new Color(200,220,240));
        header.add(titulo, BorderLayout.WEST);
        header.add(bemVindo, BorderLayout.EAST);

        painelPrincipal.add(header, BorderLayout.NORTH);

        // Abas do cliente
        JTabbedPane abas = new JTabbedPane();
        abas.setBackground(new Color(240,248,255));

        abas.addTab("Produtos", criarAbaProdutos());
        abas.addTab("Minhas Reservas", criarAbaReservas());
        abas.addTab("Horários", criarAbaPerfil());

        painelPrincipal.add(abas, BorderLayout.CENTER);

        // Rodapé
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(new Color(240,248,255));
        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.setBackground(new Color(158,158,158));
        btnVoltar.setForeground(Color.BLACK);
        btnVoltar.setOpaque(true);
        btnVoltar.setBorderPainted(false);
        btnVoltar.addActionListener(e -> dispose());
        footer.add(btnVoltar);
        painelPrincipal.add(footer, BorderLayout.SOUTH);

        add(painelPrincipal);
    }

    private JPanel criarAbaProdutos() {
        JPanel painel = new JPanel(new BorderLayout(8,8));
        painel.setBackground(new Color(240,248,255));
        painel.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));

        String[] colunas = {"ID", "Nome", "Preço", "Quantidade", "Descrição"};
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // view-only
            }
        };

        JTable tabela = new JTable(modelo);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modelo);
        tabela.setRowSorter(sorter);
        tabela.setFont(new Font("Arial", Font.PLAIN, 12));
        tabela.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tabela.setRowHeight(26);

        carregarProdutosTabela(modelo);
        
        // Ordenar por nome (coluna 1 no modelo)
        sorter.setSortKeys(java.util.List.of(new javax.swing.RowSorter.SortKey(1, javax.swing.SortOrder.ASCENDING)));

        // hide ID column from client view (keep in model)
        if (tabela.getColumnModel().getColumnCount() > 0) {
            tabela.removeColumn(tabela.getColumnModel().getColumn(0));
        }

        JScrollPane scroll = new JScrollPane(tabela);
        painel.add(scroll, BorderLayout.CENTER);

        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topo.setBackground(new Color(240,248,255));
        topo.add(new JLabel("Buscar:"));
        JTextField txtBusca = new JTextField(20);
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBackground(new Color(33,150,243));
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setOpaque(true);
        btnBuscar.setBorderPainted(false);
        topo.add(txtBusca);
        topo.add(btnBuscar);
        painel.add(topo, BorderLayout.NORTH);
        // Buscar action: filtrar por nome (coluna 1 no model)
        btnBuscar.addActionListener(e -> {
            String termo = txtBusca.getText().trim();
            if (termo.isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                try {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(termo), 1));
                } catch (Exception ex) {
                    sorter.setRowFilter(null);
                }
            }
        });

        return painel;
    }

    private JPanel criarAbaReservas() {
        JPanel painel = new JPanel(new BorderLayout(8,8));
        painel.setBackground(new Color(240,248,255));
        painel.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));

        String[] colunas = {"ID", "Mesa", "Data", "Hora Início", "Hora Fim"};
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0);
        JTable tabela = new JTable(modelo);
        TableRowSorter<DefaultTableModel> sorterReservas = new TableRowSorter<>(modelo);
        tabela.setRowSorter(sorterReservas);
        tabela.setFont(new Font("Arial", Font.PLAIN, 12));
        tabela.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tabela.setRowHeight(26);

        // hide ID column from client view
        if (tabela.getColumnModel().getColumnCount() > 0) {
            tabela.removeColumn(tabela.getColumnModel().getColumn(0));
        }

        JScrollPane scroll = new JScrollPane(tabela);
        painel.add(scroll, BorderLayout.CENTER);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.setBackground(new Color(240,248,255));
        JButton btnNova = new JButton("Nova Reserva");
        btnNova.setBackground(new Color(76,175,80));
        btnNova.setForeground(Color.WHITE);
        btnNova.setOpaque(true);
        btnNova.setBorderPainted(false);
        botoes.add(btnNova);
        painel.add(botoes, BorderLayout.SOUTH);

        // service to manage reservations
        ReservaSinucaService reservaService = new ReservaSinucaService();

        // Cleanup past reservations (deleted from DB) and carregar todas as reservas ativas
        try {
            try { reservaService.removerReservasPassadas(); } catch (Exception ignore) {}
            List<ReservaSinuca> todasReservas = reservaService.listar(); // listar todas as ativas
            // Ordenar por data antes de adicionar
            todasReservas.sort((r1, r2) -> {
                int cmpData = r1.getDataReserva().compareTo(r2.getDataReserva());
                if (cmpData != 0) return cmpData;
                return r1.getHoraInicio().compareTo(r2.getHoraInicio());
            });
            for (ReservaSinuca r : todasReservas) {
                modelo.addRow(new Object[]{r.getId(), r.getNumeroMesa(), r.getDataReserva().toString(), r.getHoraInicio().toString(), r.getHoraFim().toString()});
            }
        } catch (ReservaSinucaException ex) {
            // ignore, manter tabela vazia
        }

        btnNova.addActionListener(e -> {
            JDialog dialog = new JDialog(this, "Nova Reserva", true);
            dialog.setSize(420, 300);
            dialog.setLocationRelativeTo(this);
            JPanel p = new JPanel(new GridBagLayout());
            p.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6,6,6,6);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            gbc.gridx = 0; gbc.gridy = 0;
            p.add(new JLabel("Nome:"), gbc);
            gbc.gridx = 1;
            JTextField txtNome = new JTextField(20);
            txtNome.setText(cliente.getNome());
            p.add(txtNome, gbc);

            gbc.gridx = 0; gbc.gridy = 1;
            p.add(new JLabel("Telefone:"), gbc);
            gbc.gridx = 1;
            JTextField txtTel = new JTextField(15);
            p.add(txtTel, gbc);

            gbc.gridx = 0; gbc.gridy = 2;
            p.add(new JLabel("Mesa (1-" + org.example.dao.ReservaSinucaDAO.TOTAL_MESAS + "):"), gbc);
            gbc.gridx = 1;
            JComboBox<Integer> comboMesa = new JComboBox<>();
            for (int i=1;i<=org.example.dao.ReservaSinucaDAO.TOTAL_MESAS;i++) comboMesa.addItem(i);
            p.add(comboMesa, gbc);

            gbc.gridx = 0; gbc.gridy = 3;
            p.add(new JLabel("Data (DD/MM/YYYY):"), gbc);
            gbc.gridx = 1;
            JTextField txtData = new JTextField(10);
            p.add(txtData, gbc);

            gbc.gridx = 0; gbc.gridy = 4;
            p.add(new JLabel("Hora Início (HH:MM):"), gbc);
            gbc.gridx = 1;
            JTextField txtHoraIni = new JTextField(6);
            p.add(txtHoraIni, gbc);

            gbc.gridx = 0; gbc.gridy = 5;
            p.add(new JLabel("Hora Fim (HH:MM):"), gbc);
            gbc.gridx = 1;
            JTextField txtHoraFim = new JTextField(6);
            p.add(txtHoraFim, gbc);

            gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
            JPanel botoesDlg = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton btnOk = new JButton("Reservar");
            btnOk.setBackground(new Color(76,175,80)); btnOk.setForeground(Color.WHITE); btnOk.setOpaque(true); btnOk.setBorderPainted(false);
            JButton btnCanc = new JButton("Cancelar");
            btnCanc.setBackground(new Color(158,158,158)); btnCanc.setForeground(Color.BLACK); btnCanc.setOpaque(true); btnCanc.setBorderPainted(false);
            botoesDlg.add(btnOk); botoesDlg.add(btnCanc);
            p.add(botoesDlg, gbc);

            btnCanc.addActionListener(ev -> dialog.dispose());

            btnOk.addActionListener(ev -> {
                try {
                    String nomeCli = txtNome.getText().trim();
                    String tel = txtTel.getText().trim();
                    int mesa = (Integer) comboMesa.getSelectedItem();
                    LocalDate data = LocalDate.parse(txtData.getText().trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    LocalTime inicio = LocalTime.parse(txtHoraIni.getText().trim());
                    LocalTime fim = LocalTime.parse(txtHoraFim.getText().trim());

                    ReservaSinuca reserva = new ReservaSinuca(mesa, nomeCli, data, inicio, fim);
                    reserva.setTelefoneCliente(tel);

                    ReservaSinucaService svc = new ReservaSinucaService();
                    ReservaSinuca criado = svc.criarReserva(reserva);

                    // atualizar tabela
                    modelo.addRow(new Object[]{criado.getId(), criado.getNumeroMesa(), criado.getDataReserva().toString(), criado.getHoraInicio().toString(), criado.getHoraFim().toString()});

                    JOptionPane.showMessageDialog(dialog, "Reserva criada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } catch (ReservaSinucaException ex) {
                    JOptionPane.showMessageDialog(dialog, "Erro ao criar reserva: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Dados inválidos. Verifique os campos.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            });

            dialog.add(p);
            dialog.setVisible(true);
        });

        return painel;
    }

    private JPanel criarAbaPerfil() {
        // replace Perfil with Horários view
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(new Color(240,248,255));
        painel.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));

        // Painel dividido: horários padrão + horários especiais
        JPanel painelCentral = new JPanel();
        painelCentral.setLayout(new BoxLayout(painelCentral, BoxLayout.Y_AXIS));
        painelCentral.setBackground(new Color(240,248,255));

        // Tabela de horários padrão
        String[] colunas = {"Dia/Especial", "Abertura", "Fechamento"};
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable tabela = new JTable(modelo);
        tabela.setFont(new Font("Arial", Font.PLAIN, 12));
        tabela.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tabela.setRowHeight(26);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createTitledBorder("Horários Padrão da Semana"));
        scroll.setPreferredSize(new Dimension(600, 250));
        painelCentral.add(scroll);

        // Tabela de horários especiais futuros
        String[] colunasEspeciais = {"Data", "Dia", "Abertura", "Fechamento", "Observação"};
        DefaultTableModel modeloEspeciais = new DefaultTableModel(colunasEspeciais, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable tabelaEspeciais = new JTable(modeloEspeciais);
        tabelaEspeciais.setFont(new Font("Arial", Font.PLAIN, 12));
        tabelaEspeciais.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tabelaEspeciais.setRowHeight(26);

        JScrollPane scrollEspeciais = new JScrollPane(tabelaEspeciais);
        scrollEspeciais.setBorder(BorderFactory.createTitledBorder("Horários Especiais (Próximos 30 dias)"));
        scrollEspeciais.setPreferredSize(new Dimension(600, 200));
        painelCentral.add(Box.createVerticalStrut(10));
        painelCentral.add(scrollEspeciais);

        painel.add(painelCentral, BorderLayout.CENTER);

        // Status atual (aberto/fechado) exibido acima da tabela
        JLabel lblStatusAtual = new JLabel("Status indisponível");
        lblStatusAtual.setFont(new Font("Arial", Font.BOLD, 14));
        lblStatusAtual.setHorizontalAlignment(SwingConstants.LEFT);
        painel.add(lblStatusAtual, BorderLayout.NORTH);

        // carregar horários via HorarioService
        HorarioService hs = new HorarioService();
        try {
            // Preenche o rótulo de status com informações atuais
            try {
                HorarioService.StatusFuncionamento status = hs.verificarStatusAtual();
                String texto = status.getMensagem();
                if (status.getProximoEvento() != null && !status.getProximoEvento().isEmpty()) {
                    texto += " — " + status.getProximoEvento();
                }
                lblStatusAtual.setText(texto);
                if (status.getMensagem() != null && status.getMensagem().startsWith("🟢")) {
                    lblStatusAtual.setForeground(new Color(34, 139, 34));
                } else {
                    lblStatusAtual.setForeground(Color.RED);
                }
            } catch (Exception ignoreStatus) {
                lblStatusAtual.setText("Status indisponível");
                lblStatusAtual.setForeground(Color.GRAY);
            }

            LocalDate hoje = LocalDate.now();
            var padrao = hs.listarHorariosPadrao();
            for (HorarioFuncionamento h : padrao) {
                // traduz nome do dia
                String diaTraduzido = traduzirDia(h.getDiaSemana());

                // calcula a próxima data daquele dia da semana incluindo hoje (i=0)
                java.time.DayOfWeek alvo = (h.getDiaSemana() instanceof java.time.DayOfWeek) ? (java.time.DayOfWeek) h.getDiaSemana() : null;
                LocalDate proximaData = null;
                if (alvo != null) {
                    for (int i = 0; i < 7; i++) {
                        LocalDate candidato = hoje.plusDays(i);
                        if (candidato.getDayOfWeek() == alvo) {
                            proximaData = candidato;
                            break;
                        }
                    }
                }

                // Verifica se existe horário especial para a próxima ocorrência desse dia
                HorarioFuncionamento horarioParaExibir = h;
                boolean ehEspecial = false;
                if (proximaData != null) {
                    try {
                        HorarioFuncionamento obt = hs.obterHorario(proximaData);
                        if (obt != null && obt.getDataEspecial() != null) {
                            // usar o horário especial no lugar do padrão para exibição
                            horarioParaExibir = obt;
                            ehEspecial = true;
                        }
                    } catch (Exception ignored) {
                    }
                }

                String abertura = horarioParaExibir.getHorarioAbertura() != null ? horarioParaExibir.getHorarioAbertura().toString() : "-";
                String fechamento = horarioParaExibir.getHorarioFechamento() != null ? horarioParaExibir.getHorarioFechamento().toString() : "-";

                String diaLabel = diaTraduzido;
                if (ehEspecial && horarioParaExibir.getDataEspecial() != null) {
                    diaLabel = diaTraduzido + " (Especial em " + horarioParaExibir.getDataEspecial().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ")";
                }

                modelo.addRow(new Object[]{diaLabel, abertura, fechamento});
            }

            // Carregar horários especiais futuros (próximos 30 dias)
            LocalDate inicioFuturo = hoje;
            LocalDate fimFuturo = hoje.plusDays(30);
            var especiais = hs.listarHorariosEspeciais();
            for (HorarioFuncionamento h : especiais) {
                if (h.getDataEspecial() != null && 
                    !h.getDataEspecial().isBefore(inicioFuturo) && 
                    !h.getDataEspecial().isAfter(fimFuturo)) {
                    
                    String dataFormatada = h.getDataEspecial().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    String diaSemana = traduzirDia(h.getDataEspecial().getDayOfWeek());
                    String aberturaEsp = h.getHorarioAbertura() != null ? h.getHorarioAbertura().toString() : "-";
                    String fechamentoEsp = h.getHorarioFechamento() != null ? h.getHorarioFechamento().toString() : "-";
                    String obs = h.getObservacao() != null ? h.getObservacao() : "";
                    
                    modeloEspeciais.addRow(new Object[]{dataFormatada, diaSemana, aberturaEsp, fechamentoEsp, obs});
                }
            }
            
            if (modeloEspeciais.getRowCount() == 0) {
                modeloEspeciais.addRow(new Object[]{"Nenhum horário especial cadastrado", "", "", "", ""});
            }
        } catch (Exception e) {
            // em caso de erro, mostrar mensagem no painel
            modelo.addRow(new Object[]{"Erro ao carregar horários", "-", "-"});
        } finally {
            try { hs.fechar(); } catch (Exception ignored) {}
        }

        return painel;
    }

    private void carregarProdutosTabela(DefaultTableModel modelo) {
        // carregar produtos do banco via ProdutoService
        ProdutoService produtoService = new ProdutoService();
        try {
            var produtos = produtoService.listarTodosProdutos();
            for (Produto p : produtos) {
                String precoStr = p.getPreco() != null ? String.format("R$ %.2f", p.getPreco()) : "-";
                modelo.addRow(new Object[]{p.getId(), p.getNome(), precoStr, p.getQuantidadeEstoque(), p.getDescricao()});
            }
        } catch (Exception e) {
            // em caso de erro, não interrompe a interface; mostra mensagem no console
            System.err.println("Erro ao carregar produtos: " + e.getMessage());
        } finally {
            try { produtoService.fechar(); } catch (Exception ignored) {}
        }
    }

    private String traduzirDia(Object dia) {
        if (dia instanceof java.time.DayOfWeek) {
            return switch ((java.time.DayOfWeek) dia) {
                case MONDAY -> "Segunda";
                case TUESDAY -> "Terça";
                case WEDNESDAY -> "Quarta";
                case THURSDAY -> "Quinta";
                case FRIDAY -> "Sexta";
                case SATURDAY -> "Sábado";
                case SUNDAY -> "Domingo";
            };
        }
        return dia != null ? dia.toString() : "";
    }

    

    public static void main(String[] args) {
        org.example.models.Usuario cliente = new org.example.models.Usuario("Cliente", "cliente@diltos.com", "", org.example.models.Usuario.TipoUsuario.CLIENTE);
        cliente.setId(2L);
        ClienteMainWindow janela = new ClienteMainWindow(cliente);
        janela.setVisible(true);
    }
}
