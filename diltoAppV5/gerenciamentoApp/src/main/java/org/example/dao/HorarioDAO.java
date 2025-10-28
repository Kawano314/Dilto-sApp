package org.example.dao;

import org.example.models.ConnectDB;
import org.example.models.HorarioFuncionamento;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class HorarioDAO {
    private ConnectDB connectDB;

    public HorarioDAO() {
        this.connectDB = new ConnectDB();
        criarTabelas();
        inicializarHorariosPadrao();
    }

    private void criarTabelas() {
        String sql = """
            CREATE TABLE IF NOT EXISTS horarios_padrao (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                dia_semana INTEGER NOT NULL UNIQUE,
                horario_abertura TEXT,
                horario_fechamento TEXT,
                fechado BOOLEAN NOT NULL DEFAULT 0
            )
        """;

        String sqlEspeciais = """
            CREATE TABLE IF NOT EXISTS horarios_especiais (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                data_especial TEXT NOT NULL UNIQUE,
                horario_abertura TEXT,
                horario_fechamento TEXT,
                fechado BOOLEAN NOT NULL DEFAULT 0,
                observacao TEXT
            )
        """;

        try (Statement stmt = connectDB.getConnection().createStatement()) {
            stmt.execute(sql);
            stmt.execute(sqlEspeciais);
        } catch (SQLException e) {
            System.err.println("✗ Erro ao criar tabelas de horários: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void inicializarHorariosPadrao() {
        try {
            List<HorarioFuncionamento> existentes = listarHorariosPadrao();
            if (!existentes.isEmpty()) {
                return;
            }

            inserirHorarioPadrao(DayOfWeek.MONDAY, "08:00", "18:00");
            inserirHorarioPadrao(DayOfWeek.TUESDAY, "08:00", "18:00");
            inserirHorarioPadrao(DayOfWeek.WEDNESDAY, "08:00", "18:00");
            inserirHorarioPadrao(DayOfWeek.THURSDAY, "08:00", "18:00");
            inserirHorarioPadrao(DayOfWeek.FRIDAY, "08:00", "17:00");
            inserirHorarioPadrao(DayOfWeek.SATURDAY, null, null);
            inserirHorarioPadrao(DayOfWeek.SUNDAY, null, null);
        } catch (SQLException e) {
            System.err.println("✗ Erro ao inicializar horários: " + e.getMessage());
        }
    }

    private void inserirHorarioPadrao(DayOfWeek dia, String abertura, String fechamento) throws SQLException {
        String sql = """
            INSERT INTO horarios_padrao (dia_semana, horario_abertura, horario_fechamento, fechado)
            VALUES (?, ?, ?, ?)
        """;

        try (PreparedStatement pstmt = connectDB.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, dia.getValue());
            pstmt.setString(2, abertura);
            pstmt.setString(3, fechamento);
            pstmt.setBoolean(4, abertura == null);
            pstmt.executeUpdate();
        }
    }

    public List<HorarioFuncionamento> listarHorariosPadrao() throws SQLException {
        List<HorarioFuncionamento> horarios = new ArrayList<>();
        String sql = "SELECT * FROM horarios_padrao ORDER BY dia_semana";

        try (Statement stmt = connectDB.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                horarios.add(extrairHorarioPadrao(rs));
            }
        }
        return horarios;
    }

    public HorarioFuncionamento buscarHorarioPorDia(DayOfWeek dia) throws SQLException {
        String sql = "SELECT * FROM horarios_padrao WHERE dia_semana = ?";

        try (PreparedStatement pstmt = connectDB.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, dia.getValue());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extrairHorarioPadrao(rs);
            }
        }
        return null;
    }

    public HorarioFuncionamento buscarHorarioEspecial(LocalDate data) throws SQLException {
        String sql = "SELECT * FROM horarios_especiais WHERE data_especial = ?";

        try (PreparedStatement pstmt = connectDB.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, data.toString());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extrairHorarioEspecial(rs);
            }
        }
        return null;
    }

    public List<HorarioFuncionamento> listarHorariosEspeciais() throws SQLException {
        List<HorarioFuncionamento> horarios = new ArrayList<>();
        String sql = "SELECT * FROM horarios_especiais ORDER BY data_especial";

        try (Statement stmt = connectDB.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                horarios.add(extrairHorarioEspecial(rs));
            }
        }
        return horarios;
    }

    public void inserirHorarioEspecial(HorarioFuncionamento horario) throws SQLException {
        String sql = """
            INSERT OR REPLACE INTO horarios_especiais 
            (data_especial, horario_abertura, horario_fechamento, fechado, observacao)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement pstmt = connectDB.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, horario.getDataEspecial().toString());
            pstmt.setString(2, horario.isFechado() ? null : horario.getHorarioAbertura().toString());
            pstmt.setString(3, horario.isFechado() ? null : horario.getHorarioFechamento().toString());
            pstmt.setBoolean(4, horario.isFechado());
            pstmt.setString(5, horario.getObservacao());
            pstmt.executeUpdate();
        }
    }

    public boolean atualizarHorarioPadrao(HorarioFuncionamento horario) throws SQLException {
        String sql = """
            UPDATE horarios_padrao 
            SET horario_abertura = ?, horario_fechamento = ?, fechado = ?
            WHERE dia_semana = ?
        """;

        try (PreparedStatement pstmt = connectDB.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, horario.isFechado() ? null : horario.getHorarioAbertura().toString());
            pstmt.setString(2, horario.isFechado() ? null : horario.getHorarioFechamento().toString());
            pstmt.setBoolean(3, horario.isFechado());
            pstmt.setInt(4, horario.getDiaSemana().getValue());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deletarHorarioEspecial(LocalDate data) throws SQLException {
        String sql = "DELETE FROM horarios_especiais WHERE data_especial = ?";

        try (PreparedStatement pstmt = connectDB.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, data.toString());
            return pstmt.executeUpdate() > 0;
        }
    }

    private HorarioFuncionamento extrairHorarioPadrao(ResultSet rs) throws SQLException {
        HorarioFuncionamento horario = new HorarioFuncionamento();
        horario.setId(rs.getLong("id"));
        horario.setDiaSemana(DayOfWeek.of(rs.getInt("dia_semana")));
        horario.setFechado(rs.getBoolean("fechado"));

        String abertura = rs.getString("horario_abertura");
        String fechamento = rs.getString("horario_fechamento");

        if (abertura != null) {
            horario.setHorarioAbertura(LocalTime.parse(abertura));
        }
        if (fechamento != null) {
            horario.setHorarioFechamento(LocalTime.parse(fechamento));
        }

        return horario;
    }

    private HorarioFuncionamento extrairHorarioEspecial(ResultSet rs) throws SQLException {
        HorarioFuncionamento horario = new HorarioFuncionamento();
        horario.setId(rs.getLong("id"));
        horario.setDataEspecial(LocalDate.parse(rs.getString("data_especial")));
        horario.setFechado(rs.getBoolean("fechado"));
        horario.setObservacao(rs.getString("observacao"));

        String abertura = rs.getString("horario_abertura");
        String fechamento = rs.getString("horario_fechamento");

        if (abertura != null) {
            horario.setHorarioAbertura(LocalTime.parse(abertura));
        }
        if (fechamento != null) {
            horario.setHorarioFechamento(LocalTime.parse(fechamento));
        }

        return horario;
    }

    public void fechar() {
        connectDB.closeConnection();
    }
}