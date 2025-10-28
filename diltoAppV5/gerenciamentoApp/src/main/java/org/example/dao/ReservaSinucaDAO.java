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
                status TEXT NOT NULL DEFAULT 'ATIVA',
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

    public ReservaSinuca inserir(ReservaSinuca reserva) throws SQLException {
        String sql = """
            INSERT INTO reservas_sinuca (numero_mesa, nome_cliente, telefone_cliente, 
                                        data_reserva, hora_inicio, hora_fim, status, 
                                        data_criacao, observacoes)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement pstmt = connectDB.getConnection().prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, reserva.getNumeroMesa());
            pstmt.setString(2, reserva.getNomeCliente());
            pstmt.setString(3, reserva.getTelefoneCliente());
            pstmt.setString(4, reserva.getDataReserva().toString());
            pstmt.setString(5, reserva.getHoraInicio().toString());
            pstmt.setString(6, reserva.getHoraFim().toString());
            pstmt.setString(7, reserva.getStatus().name());
            pstmt.setString(8, reserva.getDataCriacao().toString());
            pstmt.setString(9, reserva.getObservacoes());

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

    public List<ReservaSinuca> listarPorData(LocalDate data) throws SQLException {
        List<ReservaSinuca> reservas = new ArrayList<>();
        String sql = """
            SELECT * FROM reservas_sinuca 
            WHERE data_reserva = ? AND status = 'ATIVA'
            ORDER BY numero_mesa, hora_inicio
        """;

        try (PreparedStatement pstmt = connectDB.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, data.toString());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                reservas.add(extrairReserva(rs));
            }
        }
        return reservas;
    }

    public List<ReservaSinuca> listarPorMesa(int numeroMesa, LocalDate data) throws SQLException {
        List<ReservaSinuca> reservas = new ArrayList<>();
        String sql = """
            SELECT * FROM reservas_sinuca 
            WHERE numero_mesa = ? AND data_reserva = ? AND status = 'ATIVA'
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
            WHERE status = 'ATIVA' AND data_reserva >= date('now')
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

    public boolean cancelarReserva(Long id) throws SQLException {
        String sql = "UPDATE reservas_sinuca SET status = 'CANCELADA' WHERE id = ?";

        try (PreparedStatement pstmt = connectDB.getConnection().prepareStatement(sql)) {
            pstmt.setLong(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean cancelarReservasPorMesa(int numeroMesa, LocalDate data) throws SQLException {
        String sql = """
            UPDATE reservas_sinuca 
            SET status = 'CANCELADA' 
            WHERE numero_mesa = ? AND data_reserva = ? AND status = 'ATIVA'
        """;

        try (PreparedStatement pstmt = connectDB.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, numeroMesa);
            pstmt.setString(2, data.toString());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean cancelarReservaPorMesaEHorario(int numeroMesa, LocalDate data,
                                                  LocalTime horaInicio) throws SQLException {
        String sql = """
            UPDATE reservas_sinuca 
            SET status = 'CANCELADA' 
            WHERE numero_mesa = ? AND data_reserva = ? AND hora_inicio = ? AND status = 'ATIVA'
        """;

        try (PreparedStatement pstmt = connectDB.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, numeroMesa);
            pstmt.setString(2, data.toString());
            pstmt.setString(3, horaInicio.toString());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean existeConflito(ReservaSinuca reserva) throws SQLException {
        String sql = """
            SELECT COUNT(*) as total FROM reservas_sinuca 
            WHERE numero_mesa = ? AND data_reserva = ? AND status = 'ATIVA'
            AND ((hora_inicio < ? AND hora_fim > ?) OR 
                 (hora_inicio < ? AND hora_fim > ?) OR
                 (hora_inicio >= ? AND hora_fim <= ?))
        """;

        try (PreparedStatement pstmt = connectDB.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, reserva.getNumeroMesa());
            pstmt.setString(2, reserva.getDataReserva().toString());
            pstmt.setString(3, reserva.getHoraFim().toString());
            pstmt.setString(4, reserva.getHoraInicio().toString());
            pstmt.setString(5, reserva.getHoraInicio().toString());
            pstmt.setString(6, reserva.getHoraInicio().toString());
            pstmt.setString(7, reserva.getHoraInicio().toString());
            pstmt.setString(8, reserva.getHoraFim().toString());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
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
        reserva.setStatus(ReservaSinuca.StatusReserva.valueOf(rs.getString("status")));
        reserva.setDataCriacao(LocalDateTime.parse(rs.getString("data_criacao")));
        reserva.setObservacoes(rs.getString("observacoes"));
        return reserva;
    }

    public void fechar() {
        connectDB.closeConnection();
    }
}