package org.example.models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class ReservaSinuca {
    private Long id;
    private int numeroMesa;
    private String nomeCliente;
    private String telefoneCliente;
    private LocalDate dataReserva;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private StatusReserva status;
    private LocalDateTime dataCriacao;
    private String observacoes;

    public enum StatusReserva {
        ATIVA("Ativa"),
        CANCELADA("Cancelada"),
        CONCLUIDA("Concluída");

        private final String descricao;

        StatusReserva(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public ReservaSinuca() {
        this.dataCriacao = LocalDateTime.now();
        this.status = StatusReserva.ATIVA;
    }

    public ReservaSinuca(int numeroMesa, String nomeCliente, LocalDate dataReserva,
                         LocalTime horaInicio, LocalTime horaFim) {
        this();
        this.numeroMesa = numeroMesa;
        this.nomeCliente = nomeCliente;
        this.dataReserva = dataReserva;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNumeroMesa() {
        return numeroMesa;
    }

    public void setNumeroMesa(int numeroMesa) {
        this.numeroMesa = numeroMesa;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getTelefoneCliente() {
        return telefoneCliente;
    }

    public void setTelefoneCliente(String telefoneCliente) {
        this.telefoneCliente = telefoneCliente;
    }

    public LocalDate getDataReserva() {
        return dataReserva;
    }

    public void setDataReserva(LocalDate dataReserva) {
        this.dataReserva = dataReserva;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(LocalTime horaFim) {
        this.horaFim = horaFim;
    }

    public StatusReserva getStatus() {
        return status;
    }

    public void setStatus(StatusReserva status) {
        this.status = status;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public boolean isAtiva() {
        return status == StatusReserva.ATIVA;
    }

    public String getPeriodoFormatado() {
        return String.format("%s às %s",
                horaInicio.toString(),
                horaFim.toString());
    }

    @Override
    public String toString() {
        return String.format("Mesa %d | %s | %s-%s | %s | %s",
                numeroMesa,
                dataReserva.toString(),
                horaInicio.toString(),
                horaFim.toString(),
                nomeCliente,
                status.getDescricao());
    }
}