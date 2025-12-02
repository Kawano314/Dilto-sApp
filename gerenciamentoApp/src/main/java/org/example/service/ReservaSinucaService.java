package org.example.service;

import org.example.dao.ReservaSinucaDAO;
import org.example.exceptions.ReservaSinucaException;
import org.example.models.ReservaSinuca;
import org.example.models.HorarioFuncionamento;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class ReservaSinucaService {
    private ReservaSinucaDAO reservaDAO;
    private HorarioService horarioService;

    public ReservaSinucaService() {
        this.reservaDAO = new ReservaSinucaDAO();
        this.horarioService = new HorarioService();
    }

    public ReservaSinuca criar(ReservaSinuca reserva) throws ReservaSinucaException {
        return criarReserva(reserva);
    }

    public void remover(long id) throws ReservaSinucaException {
        if (id <= 0) {
            throw new ReservaSinucaException("ID inválido para remoção");
        }
        try {
            boolean ok = reservaDAO.deletarPorId(id);
            if (!ok) {
                throw new ReservaSinucaException("Reserva não encontrada!");
            }
        } catch (SQLException e) {
            throw new ReservaSinucaException("Erro ao remover reserva: " + e.getMessage(), e);
        }
    }

    public List<ReservaSinuca> listar() throws ReservaSinucaException {
        return listarTodasReservasAtivas();
    }

    public int removerReservasPassadas() throws ReservaSinucaException {
        try {
            return reservaDAO.removerReservasPassadas();
        } catch (SQLException e) {
            throw new ReservaSinucaException("Erro ao remover reservas passadas: " + e.getMessage(), e);
        }
    }

    public void deletarReserva(Long id) throws ReservaSinucaException {
        try {
            boolean ok = reservaDAO.deletarPorId(id);
            if (!ok) throw new ReservaSinucaException("Falha ao deletar reserva (não encontrada)");
        } catch (SQLException e) {
            throw new ReservaSinucaException("Erro ao deletar reserva: " + e.getMessage(), e);
        }
    }

    public ReservaSinuca criarReserva(ReservaSinuca reserva) throws ReservaSinucaException {
        try {
            validarReserva(reserva);

            if (reservaDAO.existeConflito(reserva)) {
                throw new ReservaSinucaException(
                        "Já existe uma reserva para esta mesa neste horário!");
            }

            return reservaDAO.inserir(reserva);
        } catch (SQLException e) {
            throw new ReservaSinucaException("Erro ao criar reserva: " + e.getMessage(), e);
        }
    }


    public List<ReservaSinuca> listarTodasReservasAtivas() throws ReservaSinucaException {
        try {
            return reservaDAO.listarTodasAtivas();
        } catch (SQLException e) {
            throw new ReservaSinucaException("Erro ao listar reservas: " + e.getMessage(), e);
        }
    }

    public void cancelarReserva(Long id) throws ReservaSinucaException {
        try {
            ReservaSinuca reserva = reservaDAO.buscarPorId(id);
            if (reserva == null) {
                throw new ReservaSinucaException("Reserva não encontrada!");
            }
            // Deletar a reserva permanentemente
            boolean deletada = reservaDAO.deletarPorId(id);
            if (!deletada) {
                throw new ReservaSinucaException("Falha ao deletar reserva!");
            }
        } catch (SQLException e) {
            throw new ReservaSinucaException("Erro ao cancelar reserva: " + e.getMessage(), e);
        }
    }

    public void cancelarReservaPorHorario(int numeroMesa, LocalDate data, LocalTime horaInicio)
            throws ReservaSinucaException {
        try {
            validarNumeroMesa(numeroMesa);

            boolean deletada = reservaDAO.cancelarReservaPorMesaEHorario(
                numeroMesa, data, horaInicio);

            if (!deletada) {
            throw new ReservaSinucaException(
                "Nenhuma reserva encontrada para este horário!");
            }
        } catch (SQLException e) {
            throw new ReservaSinucaException(
                    "Erro ao cancelar reserva: " + e.getMessage(), e);
        }
    }

    private void validarReserva(ReservaSinuca reserva) throws ReservaSinucaException {
        if (reserva == null) {
            throw new ReservaSinucaException("Reserva não pode ser nula!");
        }

        validarNumeroMesa(reserva.getNumeroMesa());

        if (reserva.getNomeCliente() == null || reserva.getNomeCliente().trim().isEmpty()) {
            throw new ReservaSinucaException("Nome do cliente é obrigatório!");
        }

        if (reserva.getTelefoneCliente() != null && !reserva.getTelefoneCliente().isEmpty()) {
            if (!reserva.getTelefoneCliente().matches("^[0-9]{8,}$")) {
                throw new ReservaSinucaException("Telefone inválido: deve conter ao menos 8 dígitos numéricos");
            }
        }

        if (reserva.getDataReserva() == null) {
            throw new ReservaSinucaException("Data da reserva é obrigatória!");
        }

        if (reserva.getDataReserva().isBefore(LocalDate.now())) {
            throw new ReservaSinucaException("Não é possível fazer reserva para data passada!");
        }

        if (reserva.getDataReserva().equals(LocalDate.now())) {
            LocalTime agoraHora = LocalTime.now();
            if (reserva.getHoraInicio().isBefore(agoraHora) || reserva.getHoraInicio().equals(agoraHora)) {
                throw new ReservaSinucaException("Não é possível reservar para horário que já passou! Horário atual: " + agoraHora);
            }
        }

        if (reserva.getHoraInicio() == null || reserva.getHoraFim() == null) {
            throw new ReservaSinucaException("Horário de início e fim são obrigatórios!");
        }

        if (!reserva.getHoraInicio().isBefore(reserva.getHoraFim())) {
            throw new ReservaSinucaException(
                    "Horário de início deve ser anterior ao horário de fim!");
        }
        
        try {
            var horario = obterHorarioFuncionamento(reserva.getDataReserva());

            if (horario == null || horario.isFechado() || horario.getHorarioAbertura() == null || horario.getHorarioFechamento() == null) {
                throw new ReservaSinucaException("Não é possível reservar: estabelecimento fechado nesta data");
            }

            if (reserva.getHoraInicio().isBefore(horario.getHorarioAbertura()) || reserva.getHoraFim().isAfter(horario.getHorarioFechamento())) {
                throw new ReservaSinucaException("Horário inválido: fora do horário de funcionamento (" + horario.getHorarioAbertura() + " - " + horario.getHorarioFechamento() + ")");
            }
        } catch (SQLException e) {
            throw new ReservaSinucaException("Erro ao validar horário do estabelecimento: " + e.getMessage(), e);
        }
    }

    private void validarNumeroMesa(int numeroMesa) throws ReservaSinucaException {
        if (numeroMesa < 1 || numeroMesa > ReservaSinucaDAO.TOTAL_MESAS) {
            throw new ReservaSinucaException(
                    "Número de mesa inválido! Mesas disponíveis: 1 a " +
                            ReservaSinucaDAO.TOTAL_MESAS);
        }
    }

    /**
     * Valida se a reserva está dentro do horário funcionamento do estabelecimento
     * @param reserva Reserva a validar
     * @throws ReservaSinucaException Se a reserva for fora do horário funcionamento
     */
    public void validarHorarioEstabelecimento(ReservaSinuca reserva) throws ReservaSinucaException {
        try {
            if (reserva == null || reserva.getDataReserva() == null) {
                throw new ReservaSinucaException("Dados da reserva inválidos!");
            }

            HorarioFuncionamento horario = horarioService.obterHorario(reserva.getDataReserva());
            
            if (horario == null || horario.isFechado()) {
                throw new ReservaSinucaException("Estabelecimento fechado nesta data!");
            }

            LocalTime abertura = horario.getHorarioAbertura();
            LocalTime fechamento = horario.getHorarioFechamento();
            LocalTime horaInicio = reserva.getHoraInicio();
            LocalTime horaFim = reserva.getHoraFim();

            if (horaInicio.isBefore(abertura)) {
                throw new ReservaSinucaException(
                    "Horário de início (" + horaInicio + ") anterior à abertura (" + abertura + ")");
            }

            if (horaFim.isAfter(fechamento)) {
                throw new ReservaSinucaException(
                    "Horário de fim (" + horaFim + ") posterior ao fechamento (" + fechamento + ")");
            }
        } catch (SQLException e) {
            throw new ReservaSinucaException("Erro ao validar horário: " + e.getMessage(), e);
        }
    }

    /**
     * Obtém horário funcionamento para uma data específica
     * @param data Data para obter horário
     * @return HorarioFuncionamento ou null
     * @throws SQLException Se erro ao consultar BD
     */
    public HorarioFuncionamento obterHorarioFuncionamento(LocalDate data) 
            throws SQLException {
        return horarioService.obterHorario(data);
    }

    /**
     * Cancela todas as reservas de uma mesa em uma data específica
     * @param numeroMesa Número da mesa
     * @param data Data das reservas a cancelar
     * @throws ReservaSinucaException Se houver erro
     */
    public void cancelarTodasReservasDaMesa(int numeroMesa, LocalDate data) throws ReservaSinucaException {
        try {
            validarNumeroMesa(numeroMesa);
            if (data == null) {
                throw new ReservaSinucaException("Data não pode ser nula");
            }
            
            List<ReservaSinuca> reservas = reservaDAO.listarPorMesa(numeroMesa, data);
            for (ReservaSinuca reserva : reservas) {
                reservaDAO.deletarPorId(reserva.getId());
            }
        } catch (SQLException e) {
            throw new ReservaSinucaException("Erro ao cancelar reservas da mesa: " + e.getMessage(), e);
        }
    }

    /**
     * Cancela todas as reservas de uma mesa (todas as datas)
     * @param numeroMesa Número da mesa
     * @throws ReservaSinucaException Se houver erro
     */
    public void cancelarTodasReservasMesa(int numeroMesa) throws ReservaSinucaException {
        try {
            validarNumeroMesa(numeroMesa);
            List<ReservaSinuca> todasReservas = listarTodasReservasAtivas();
            for (ReservaSinuca reserva : todasReservas) {
                if (reserva.getNumeroMesa() == numeroMesa) {
                    reservaDAO.deletarPorId(reserva.getId());
                }
            }
        } catch (SQLException e) {
            throw new ReservaSinucaException("Erro ao cancelar todas as reservas da mesa: " + e.getMessage(), e);
        }
    }

    /**
     * Busca reservas por data
     * @param data Data das reservas
     * @return Lista de reservas na data especificada
     * @throws ReservaSinucaException Se houver erro
     */
    public List<ReservaSinuca> buscarPorData(LocalDate data) throws ReservaSinucaException {
        if (data == null) {
            throw new ReservaSinucaException("Data não pode ser nula");
        }
        List<ReservaSinuca> todasReservas = listarTodasReservasAtivas();
        return todasReservas.stream()
            .filter(r -> r.getDataReserva().equals(data))
            .toList();
    }

    /**
     * Busca reservas por mesa
     * @param numeroMesa Número da mesa
     * @return Lista de reservas da mesa especificada
     * @throws ReservaSinucaException Se houver erro
     */
    public List<ReservaSinuca> buscarPorMesa(int numeroMesa) throws ReservaSinucaException {
        validarNumeroMesa(numeroMesa);
        List<ReservaSinuca> todasReservas = listarTodasReservasAtivas();
        return todasReservas.stream()
            .filter(r -> r.getNumeroMesa() == numeroMesa)
            .toList();
    }

    /**
     * Busca reservas por cliente
     * @param nomeCliente Nome do cliente
     * @return Lista de reservas do cliente especificado
     * @throws ReservaSinucaException Se houver erro
     */
    public List<ReservaSinuca> buscarPorCliente(String nomeCliente) throws ReservaSinucaException {
        if (nomeCliente == null || nomeCliente.trim().isEmpty()) {
            throw new ReservaSinucaException("Nome do cliente não pode ser vazio");
        }
        List<ReservaSinuca> todasReservas = listarTodasReservasAtivas();
        return todasReservas.stream()
            .filter(r -> r.getNomeCliente() != null && 
                       r.getNomeCliente().equalsIgnoreCase(nomeCliente.trim()))
            .toList();
    }

    /**
     * Exibe mapa de mesas (mapa visual das mesas e suas reservas para uma data)
     * @param data Data para exibir mapa
     * @throws ReservaSinucaException Se houver erro
     */
    public void exibirMapaMesas(LocalDate data) throws ReservaSinucaException {
        try {
            if (data == null) {
                throw new ReservaSinucaException("Data não pode ser nula");
            }
            // Implementação básica - apenas valida a data
            List<ReservaSinuca> reservasData = buscarPorData(data);
            System.out.println("Mapa de mesas para " + data + ": " + reservasData.size() + " reservas");
        } catch (Exception e) {
            throw new ReservaSinucaException("Erro ao exibir mapa de mesas: " + e.getMessage(), e);
        }
    }

    public void fechar() {
        reservaDAO.fechar();
        horarioService.fechar();
    }
}