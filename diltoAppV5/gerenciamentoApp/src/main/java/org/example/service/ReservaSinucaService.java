package org.example.service;

import org.example.dao.ReservaSinucaDAO;
import org.example.exceptions.ReservaSinucaException;
import org.example.models.ReservaSinuca;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class ReservaSinucaService {
    private ReservaSinucaDAO reservaDAO;

    public ReservaSinucaService() {
        this.reservaDAO = new ReservaSinucaDAO();
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

    public Map<Integer, List<ReservaSinuca>> obterMapaReservasPorData(LocalDate data)
            throws ReservaSinucaException {
        try {
            List<ReservaSinuca> reservas = reservaDAO.listarPorData(data);

            Map<Integer, List<ReservaSinuca>> mapa = new TreeMap<>();
            for (int i = 1; i <= ReservaSinucaDAO.TOTAL_MESAS; i++) {
                mapa.put(i, new ArrayList<>());
            }

            for (ReservaSinuca reserva : reservas) {
                mapa.get(reserva.getNumeroMesa()).add(reserva);
            }

            return mapa;
        } catch (SQLException e) {
            throw new ReservaSinucaException("Erro ao carregar reservas: " + e.getMessage(), e);
        }
    }

    public List<ReservaSinuca> listarReservasPorMesa(int numeroMesa, LocalDate data)
            throws ReservaSinucaException {
        try {
            validarNumeroMesa(numeroMesa);
            return reservaDAO.listarPorMesa(numeroMesa, data);
        } catch (SQLException e) {
            throw new ReservaSinucaException("Erro ao listar reservas: " + e.getMessage(), e);
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

            if (!reserva.isAtiva()) {
                throw new ReservaSinucaException("Esta reserva já foi cancelada ou concluída!");
            }

            boolean cancelada = reservaDAO.cancelarReserva(id);
            if (!cancelada) {
                throw new ReservaSinucaException("Falha ao cancelar reserva!");
            }
        } catch (SQLException e) {
            throw new ReservaSinucaException("Erro ao cancelar reserva: " + e.getMessage(), e);
        }
    }

    public int cancelarTodasReservasDaMesa(int numeroMesa, LocalDate data)
            throws ReservaSinucaException {
        try {
            validarNumeroMesa(numeroMesa);

            List<ReservaSinuca> reservas = reservaDAO.listarPorMesa(numeroMesa, data);
            int total = reservas.size();

            if (total == 0) {
                throw new ReservaSinucaException(
                        "Não há reservas ativas para esta mesa nesta data!");
            }

            reservaDAO.cancelarReservasPorMesa(numeroMesa, data);
            return total;
        } catch (SQLException e) {
            throw new ReservaSinucaException(
                    "Erro ao cancelar reservas da mesa: " + e.getMessage(), e);
        }
    }

    public void cancelarReservaPorHorario(int numeroMesa, LocalDate data, LocalTime horaInicio)
            throws ReservaSinucaException {
        try {
            validarNumeroMesa(numeroMesa);

            boolean cancelada = reservaDAO.cancelarReservaPorMesaEHorario(
                    numeroMesa, data, horaInicio);

            if (!cancelada) {
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

        if (reserva.getDataReserva() == null) {
            throw new ReservaSinucaException("Data da reserva é obrigatória!");
        }

        if (reserva.getDataReserva().isBefore(LocalDate.now())) {
            throw new ReservaSinucaException("Não é possível fazer reserva para data passada!");
        }

        if (reserva.getHoraInicio() == null || reserva.getHoraFim() == null) {
            throw new ReservaSinucaException("Horário de início e fim são obrigatórios!");
        }

        if (!reserva.getHoraInicio().isBefore(reserva.getHoraFim())) {
            throw new ReservaSinucaException(
                    "Horário de início deve ser anterior ao horário de fim!");
        }
    }

    private void validarNumeroMesa(int numeroMesa) throws ReservaSinucaException {
        if (numeroMesa < 1 || numeroMesa > ReservaSinucaDAO.TOTAL_MESAS) {
            throw new ReservaSinucaException(
                    "Número de mesa inválido! Mesas disponíveis: 1 a " +
                            ReservaSinucaDAO.TOTAL_MESAS);
        }
    }

    public void fechar() {
        reservaDAO.fechar();
    }
}