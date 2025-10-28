package org.example.service;

import org.example.dao.HorarioDAO;
import org.example.models.HorarioFuncionamento;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HorarioService {
    private HorarioDAO horarioDAO;

    public HorarioService() {
        this.horarioDAO = new HorarioDAO();
    }

    public StatusFuncionamento verificarStatusAtual() {
        LocalDateTime agora = LocalDateTime.now();
        LocalDate hoje = agora.toLocalDate();
        LocalTime horaAtual = agora.toLocalTime();
        DayOfWeek diaAtual = agora.getDayOfWeek();

        try {
            HorarioFuncionamento horarioEspecial = horarioDAO.buscarHorarioEspecial(hoje);
            if (horarioEspecial != null) {
                return new StatusFuncionamento(horarioEspecial, horaAtual);
            }

            HorarioFuncionamento horarioPadrao = horarioDAO.buscarHorarioPorDia(diaAtual);
            if (horarioPadrao != null) {
                return new StatusFuncionamento(horarioPadrao, horaAtual);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao verificar status: " + e.getMessage());
        }

        return new StatusFuncionamento(null, horaAtual);
    }

    public List<HorarioFuncionamento> listarHorariosPadrao() throws SQLException {
        return horarioDAO.listarHorariosPadrao();
    }

    public List<HorarioFuncionamento> listarHorariosEspeciais() throws SQLException {
        return horarioDAO.listarHorariosEspeciais();
    }

    public void cadastrarHorarioEspecial(HorarioFuncionamento horario) throws SQLException {
        horarioDAO.inserirHorarioEspecial(horario);
    }

    public void atualizarHorarioPadrao(HorarioFuncionamento horario) throws SQLException {
        horarioDAO.atualizarHorarioPadrao(horario);
    }

    public void removerHorarioEspecial(LocalDate data) throws SQLException {
        horarioDAO.deletarHorarioEspecial(data);
    }

    public void fechar() {
        horarioDAO.fechar();
    }

    public static class StatusFuncionamento {
        private boolean aberto;
        private String mensagem;
        private String proximoEvento;
        private HorarioFuncionamento horario;

        public StatusFuncionamento(HorarioFuncionamento horario, LocalTime horaAtual) {
            this.horario = horario;

            if (horario == null || horario.isFechado()) {
                this.aberto = false;
                this.mensagem = "🔴 FECHADO";
                calcularProximaAbertura();
            } else {
                this.aberto = horario.estaAberto(horaAtual);

                if (aberto) {
                    this.mensagem = "🟢 ABERTO";
                    LocalTime fechamento = horario.getHorarioFechamento();
                    this.proximoEvento = "Fecha às " + fechamento.format(DateTimeFormatter.ofPattern("HH:mm"));
                } else {
                    LocalTime abertura = horario.getHorarioAbertura();
                    if (horaAtual.isBefore(abertura)) {
                        this.mensagem = "🔴 FECHADO";
                        this.proximoEvento = "Abre às " + abertura.format(DateTimeFormatter.ofPattern("HH:mm"));
                    } else {
                        this.mensagem = "🔴 FECHADO";
                        this.proximoEvento = "Fechado hoje";
                    }
                }
            }
        }

        private void calcularProximaAbertura() {
            LocalDate hoje = LocalDate.now();
            DayOfWeek diaAtual = hoje.getDayOfWeek();

            for (int i = 1; i <= 7; i++) {
                DayOfWeek proximoDia = diaAtual.plus(i);
                if (proximoDia != DayOfWeek.SATURDAY && proximoDia != DayOfWeek.SUNDAY) {
                    String nomeDia = getNomeDia(proximoDia);
                    this.proximoEvento = "Abre " + nomeDia + " às 08:00";
                    return;
                }
            }
            this.proximoEvento = "Consulte horários";
        }

        private String getNomeDia(DayOfWeek dia) {
            return switch (dia) {
                case MONDAY -> "segunda-feira";
                case TUESDAY -> "terça-feira";
                case WEDNESDAY -> "quarta-feira";
                case THURSDAY -> "quinta-feira";
                case FRIDAY -> "sexta-feira";
                case SATURDAY -> "sábado";
                case SUNDAY -> "domingo";
            };
        }

        public String getMensagem() {
            return mensagem;
        }

        public String getProximoEvento() {
            return proximoEvento;
        }

        public HorarioFuncionamento getHorario() {
            return horario;
        }
    }
}