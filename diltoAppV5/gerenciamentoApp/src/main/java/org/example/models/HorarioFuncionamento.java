package org.example.models;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public class HorarioFuncionamento {
    private Long id;
    private DayOfWeek diaSemana;
    private LocalTime horarioAbertura;
    private LocalTime horarioFechamento;
    private boolean fechado;
    private LocalDate dataEspecial;
    private String observacao;

    public HorarioFuncionamento() {
        this.fechado = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DayOfWeek getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(DayOfWeek diaSemana) {
        this.diaSemana = diaSemana;
    }

    public LocalTime getHorarioAbertura() {
        return horarioAbertura;
    }

    public void setHorarioAbertura(LocalTime horarioAbertura) {
        this.horarioAbertura = horarioAbertura;
    }

    public LocalTime getHorarioFechamento() {
        return horarioFechamento;
    }

    public void setHorarioFechamento(LocalTime horarioFechamento) {
        this.horarioFechamento = horarioFechamento;
    }

    public boolean isFechado() {
        return fechado;
    }

    public void setFechado(boolean fechado) {
        this.fechado = fechado;
    }

    public LocalDate getDataEspecial() {
        return dataEspecial;
    }

    public void setDataEspecial(LocalDate dataEspecial) {
        this.dataEspecial = dataEspecial;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
    
    public boolean isHorarioEspecial() {
        return dataEspecial != null;
    }

    public String getDiaSemanaFormatado() {
        if (diaSemana == null) return "";

        return switch (diaSemana) {
            case MONDAY -> "Segunda-feira";
            case TUESDAY -> "Terça-feira";
            case WEDNESDAY -> "Quarta-feira";
            case THURSDAY -> "Quinta-feira";
            case FRIDAY -> "Sexta-feira";
            case SATURDAY -> "Sábado";
            case SUNDAY -> "Domingo";
        };
    }

    public String getHorarioFormatado() {
        if (fechado) {
            return "Fechado";
        }
        return String.format("%s–%s",
                horarioAbertura.toString(),
                horarioFechamento.toString());
    }

    public boolean estaAberto(LocalTime horarioAtual) {
        if (fechado || horarioAbertura == null || horarioFechamento == null) {
            return false;
        }
        return !horarioAtual.isBefore(horarioAbertura) && horarioAtual.isBefore(horarioFechamento);
    }

    @Override
    public String toString() {
        if (isHorarioEspecial()) {
            return String.format("%s - %s %s",
                    dataEspecial.toString(),
                    getHorarioFormatado(),
                    observacao != null ? "(" + observacao + ")" : "");
        }
        return String.format("%s: %s", getDiaSemanaFormatado(), getHorarioFormatado());
    }
}