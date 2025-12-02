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

    public void cadastrarHorarioEspecial(HorarioFuncionamento horario) throws SQLException, org.example.exceptions.HorarioException {
        if (horario == null) {
            throw new org.example.exceptions.HorarioException("Horário não pode ser nulo");
        }

        if (horario.getDataEspecial() == null) {
            throw new org.example.exceptions.HorarioException("Data do horário especial é obrigatória");
        }

        try {
            validarDataEspecial(horario.getDataEspecial());
        } catch (IllegalArgumentException e) {
            throw new org.example.exceptions.HorarioException(e.getMessage(), e);
        }

        try {
            validarHorario(horario.getHorarioAbertura(), horario.getHorarioFechamento(), horario.isFechado());
        } catch (IllegalArgumentException e) {
            throw new org.example.exceptions.HorarioException(e.getMessage(), e);
        }

        HorarioFuncionamento existente = horarioDAO.buscarHorarioEspecial(horario.getDataEspecial());
        if (existente != null) {
            throw new org.example.exceptions.HorarioException("Já existe horário especial cadastrado para esta data: " + horario.getDataEspecial());
        }

        horarioDAO.inserirHorarioEspecial(horario);
    }

    public void atualizarHorarioPadrao(HorarioFuncionamento horario) throws SQLException, org.example.exceptions.HorarioException {
        if (horario == null) {
            throw new org.example.exceptions.HorarioException("Horário não pode ser nulo");
        }

        try {
            validarHorario(horario.getHorarioAbertura(), horario.getHorarioFechamento(), horario.isFechado());
        } catch (IllegalArgumentException e) {
            throw new org.example.exceptions.HorarioException(e.getMessage(), e);
        }

        horarioDAO.atualizarHorarioPadrao(horario);
    }

    public void removerHorarioEspecial(LocalDate data) throws SQLException {
        horarioDAO.deletarHorarioEspecial(data);
    }

    /**
     * Valida horário (abertura antes de fechamento)
     * @param abertura Hora de abertura
     * @param fechamento Hora de fechamento
     * @param ehFechado Se o dia está marcado como fechado
     * @throws IllegalArgumentException Se a abertura não for antes do fechamento
     */
    public void validarHorario(LocalTime abertura, LocalTime fechamento, boolean ehFechado) {
        if (!ehFechado && abertura != null && fechamento != null) {
            if (!abertura.isBefore(fechamento)) {
                throw new IllegalArgumentException("Horário de abertura deve ser antes do fechamento");
            }
        }
    }

    /**
     * Valida data especial (não pode ser no passado)
     * @param data Data a validar
     * @throws IllegalArgumentException Se a data for passada
     */
    public void validarDataEspecial(LocalDate data) {
        if (data != null && data.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Data não pode ser no passado");
        }
    }

    /**
     * Obtém horário funcionamento para uma data específica
     * @param data Data para obter horário
     * @return HorarioFuncionamento para a data ou null
     */
    public HorarioFuncionamento obterHorario(LocalDate data) throws SQLException {
        if (data == null) {
            return null;
        }
        
        HorarioFuncionamento especial = horarioDAO.buscarHorarioEspecial(data);
        if (especial != null) {
            return especial;
        }
        
        return horarioDAO.buscarHorarioPorDia(data.getDayOfWeek());
    }

    public void fechar() {
        horarioDAO.fechar();
    }

    /**
     * Cadastra feriados nacionais brasileiros automaticamente para um ano específico
     * @param ano Ano para cadastrar os feriados
     * @throws SQLException Em caso de erro no banco
     * @throws org.example.exceptions.HorarioException Se houver problema na validação
     */
    public void cadastrarFeriadosNacionais(int ano) throws SQLException, org.example.exceptions.HorarioException {
        cadastrarFeriadoSeNaoExistir(LocalDate.of(ano, 1, 1), "Feriado Nacional - Ano Novo");
        cadastrarFeriadoSeNaoExistir(LocalDate.of(ano, 4, 21), "Feriado Nacional - Tiradentes");
        cadastrarFeriadoSeNaoExistir(LocalDate.of(ano, 5, 1), "Feriado Nacional - Dia do Trabalho");
        cadastrarFeriadoSeNaoExistir(LocalDate.of(ano, 9, 7), "Feriado Nacional - Independência do Brasil");
        cadastrarFeriadoSeNaoExistir(LocalDate.of(ano, 10, 12), "Feriado Nacional - Nossa Senhora Aparecida");
        cadastrarFeriadoSeNaoExistir(LocalDate.of(ano, 11, 2), "Feriado Nacional - Finados");
        cadastrarFeriadoSeNaoExistir(LocalDate.of(ano, 11, 15), "Feriado Nacional - Proclamação da República");
        cadastrarFeriadoSeNaoExistir(LocalDate.of(ano, 12, 25), "Feriado Nacional - Natal");
    }
    
    /**
     * Cadastra feriado somente se não existir horário especial para aquela data
     */
    private void cadastrarFeriadoSeNaoExistir(LocalDate data, String observacao) throws SQLException {
        HorarioFuncionamento existente = horarioDAO.buscarHorarioEspecial(data);
        if (existente == null) {
            HorarioFuncionamento feriado = new HorarioFuncionamento();
            feriado.setDataEspecial(data);
            feriado.setFechado(true);
            feriado.setObservacao(observacao);
            horarioDAO.inserirHorarioEspecial(feriado);
        }
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
            HorarioService service = new HorarioService();

            for (int i = 1; i <= 7; i++) {
                DayOfWeek proximoDia = diaAtual.plus(i);
                try {
                    HorarioFuncionamento h = service.horarioDAO.buscarHorarioPorDia(proximoDia);
                    if (h != null && !h.isFechado()) {
                        String nomeDia = getNomeDia(proximoDia);
                        LocalTime abertura = h.getHorarioAbertura();
                        this.proximoEvento = "Abre " + nomeDia + " às " + 
                            abertura.format(DateTimeFormatter.ofPattern("HH:mm"));
                        service.fechar();
                        return;
                    }
                } catch (Exception e) {
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