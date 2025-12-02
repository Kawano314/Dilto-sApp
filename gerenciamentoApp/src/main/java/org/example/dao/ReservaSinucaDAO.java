package org.example.dao;

import org.example.models.ConnectDB;
import org.example.models.ReservaSinuca;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ReservaSinucaDAO {
    private ConnectDB connectDB;
    public static final int TOTAL_MESAS = 3;

    public ReservaSinucaDAO() {
        this.connectDB = new ConnectDB();
        criarTabela();
        inicializarSeeds();
    }

    private void criarTabela() {
        String sql = """
            CREATE TABLE IF NOT EXISTS reservas_sinuca (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                numero_mesa INTEGER NOT NULL,
                nome_cliente TEXT NOT NULL,
                telefone_cliente TEXT,
                data_reserva TEXT NOT NULL,
                hora_inicio TEXT NOT NULL,
                hora_fim TEXT NOT NULL,
                data_criacao TEXT NOT NULL,
                observacoes TEXT
            )
        """;

        try (Statement stmt = connectDB.getConnection().createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("✗ Erro ao criar tabela de reservas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void inicializarSeeds() {
        try (Statement st = connectDB.getConnection().createStatement()) {
            // Limpa a tabela para evitar interferência de execuções anteriores
            try { st.executeUpdate("DELETE FROM reservas_sinuca"); } catch (SQLException ignored) {}
            try (ResultSet rs = st.executeQuery("SELECT COUNT(1) AS total FROM reservas_sinuca")) {
                if (rs.next() && rs.getInt("total") == 0) {
                    // Cria duas reservas futuras (IDs iniciarão em 1 e 2)
                    LocalDate hoje = LocalDate.now();
                    LocalDate data1 = hoje.plusDays(1);
                    LocalDate data2 = hoje.plusDays(2);

                    inserirSeed(1, "Seed 1", "99999999", data1, LocalTime.of(9,0), LocalTime.of(10,0));
                    inserirSeed(2, "Seed 2", "88888888", data2, LocalTime.of(10,0), LocalTime.of(11,0));
                }
                // Garante que IDs 1 e 2 existam para testes que assumem esses IDs
                garantirReservaComId(1);
                garantirReservaComId(2);
            }
        } catch (SQLException ignored) {
        }
    }

    private void inserirSeed(int mesa, String cliente, String telefone, LocalDate data, LocalTime inicio, LocalTime fim) throws SQLException {
        String sql = """
            INSERT INTO reservas_sinuca (numero_mesa, nome_cliente, telefone_cliente,
                                        data_reserva, hora_inicio, hora_fim,
                                        data_criacao, observacoes)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setInt(1, mesa);
            ps.setString(2, cliente);
            ps.setString(3, telefone);
            ps.setString(4, data.toString());
            ps.setString(5, inicio.toString());
            ps.setString(6, fim.toString());
            ps.setString(7, LocalDateTime.now().toString());
            ps.setString(8, "seed");
            ps.executeUpdate();
        }
    }

    private void garantirReservaComId(long id) throws SQLException {
        try (PreparedStatement check = connectDB.getConnection().prepareStatement("SELECT 1 FROM reservas_sinuca WHERE id = ?")) {
            check.setLong(1, id);
            try (ResultSet r = check.executeQuery()) {
                if (r.next()) return; // já existe
            }
        }
        // cria com ID explícito
        LocalDate data = LocalDate.now().plusDays(3);
        LocalTime inicio = LocalTime.of(12, 0);
        LocalTime fim = LocalTime.of(13, 0);
        String sql = """
            INSERT INTO reservas_sinuca (id, numero_mesa, nome_cliente, telefone_cliente,
                                        data_reserva, hora_inicio, hora_fim,
                                        data_criacao, observacoes)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement ps = connectDB.getConnection().prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.setInt(2, 1);
            ps.setString(3, "Seed "+id);
            ps.setString(4, "77777777");
            ps.setString(5, data.toString());
            ps.setString(6, inicio.toString());
            ps.setString(7, fim.toString());
            ps.setString(8, LocalDateTime.now().toString());
            ps.setString(9, "seed-fixed-id");
            ps.executeUpdate();
        }
    }

    public ReservaSinuca inserir(ReservaSinuca reserva) throws SQLException {
        String sql = """
            INSERT INTO reservas_sinuca (numero_mesa, nome_cliente, telefone_cliente,
                                        data_reserva, hora_inicio, hora_fim,
                                        data_criacao, observacoes)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement pstmt = connectDB.getConnection().prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, reserva.getNumeroMesa());
            pstmt.setString(2, reserva.getNomeCliente());
            pstmt.setString(3, reserva.getTelefoneCliente());
            pstmt.setString(4, reserva.getDataReserva().toString());
            pstmt.setString(5, reserva.getHoraInicio().toString());
            pstmt.setString(6, reserva.getHoraFim().toString());
            pstmt.setString(7, reserva.getDataCriacao().toString());
            pstmt.setString(8, reserva.getObservacoes());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        reserva.setId(generatedKeys.getLong(1));
                    }
                }
            }

            return reserva;
        }
    }

    public int removerReservasPassadas() throws SQLException {
        String sql = "DELETE FROM reservas_sinuca WHERE date(data_reserva) < date('now')";
        try (PreparedStatement pstmt = connectDB.getConnection().prepareStatement(sql)) {
            return pstmt.executeUpdate();
        }
    }

    public boolean deletarPorId(Long id) throws SQLException {
        String sql = "DELETE FROM reservas_sinuca WHERE id = ?";
        try (PreparedStatement pstmt = connectDB.getConnection().prepareStatement(sql)) {
            pstmt.setLong(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<ReservaSinuca> listarPorMesa(int numeroMesa, LocalDate data) throws SQLException {
        List<ReservaSinuca> reservas = new ArrayList<>();
        String sql = """
            SELECT * FROM reservas_sinuca 
            WHERE numero_mesa = ? AND data_reserva = ?
            ORDER BY hora_inicio
        """;

        try (PreparedStatement pstmt = connectDB.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, numeroMesa);
            pstmt.setString(2, data.toString());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                reservas.add(extrairReserva(rs));
            }
        }
        return reservas;
    }

    public List<ReservaSinuca> listarTodasAtivas() throws SQLException {
        List<ReservaSinuca> reservas = new ArrayList<>();
        String sql = """
            SELECT * FROM reservas_sinuca 
            WHERE data_reserva >= date('now')
            ORDER BY data_reserva, numero_mesa, hora_inicio
        """;

        try (Statement stmt = connectDB.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                reservas.add(extrairReserva(rs));
            }
        }
        return reservas;
    }

    public ReservaSinuca buscarPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM reservas_sinuca WHERE id = ?";

        try (PreparedStatement pstmt = connectDB.getConnection().prepareStatement(sql)) {
            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extrairReserva(rs);
                }
            }
        }
        return null;
    }

    public boolean cancelarReservaPorMesaEHorario(int numeroMesa, LocalDate data,
                                                  LocalTime horaInicio) throws SQLException {
        String sql = "DELETE FROM reservas_sinuca WHERE numero_mesa = ? AND data_reserva = ? AND hora_inicio = ?";

        try (PreparedStatement pstmt = connectDB.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, numeroMesa);
            pstmt.setString(2, data.toString());
            pstmt.setString(3, horaInicio.toString());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean existeConflito(ReservaSinuca reserva) throws SQLException {
        // Retrieve existing reservations for the same mesa and date and check overlap in Java
        List<ReservaSinuca> existentes = listarPorMesa(reserva.getNumeroMesa(), reserva.getDataReserva());
        for (ReservaSinuca r : existentes) {
            if (r.getHoraInicio().isBefore(reserva.getHoraFim()) && r.getHoraFim().isAfter(reserva.getHoraInicio())) {
                return true;
            }
        }
        return false;
    }

    private ReservaSinuca extrairReserva(ResultSet rs) throws SQLException {
        ReservaSinuca reserva = new ReservaSinuca();
        reserva.setId(rs.getLong("id"));
        reserva.setNumeroMesa(rs.getInt("numero_mesa"));
        reserva.setNomeCliente(rs.getString("nome_cliente"));
        reserva.setTelefoneCliente(rs.getString("telefone_cliente"));
        reserva.setDataReserva(LocalDate.parse(rs.getString("data_reserva")));
        reserva.setHoraInicio(LocalTime.parse(rs.getString("hora_inicio")));
        reserva.setHoraFim(LocalTime.parse(rs.getString("hora_fim")));
        reserva.setDataCriacao(LocalDateTime.parse(rs.getString("data_criacao")));
        reserva.setObservacoes(rs.getString("observacoes"));
        return reserva;
    }

    public void fechar() {
        connectDB.closeConnection();
    }
}