package org.example.testutils;

import org.example.models.HorarioFuncionamento;
import org.example.models.Produto;
import org.example.models.ReservaSinuca;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public class TestDataFactory {
    public static Produto produtoValido() {
        Produto p = new Produto();
        p.setNome("Refrigerante");
        p.setPreco(new BigDecimal("8.50"));
        p.setQuantidadeEstoque(50);
        p.setCategoria("Bebidas");
        return p;
    }

    public static HorarioFuncionamento horarioPadrao(DayOfWeek dia, String abertura, String fechamento) {
        HorarioFuncionamento h = new HorarioFuncionamento();
        h.setDiaSemana(dia);
        h.setHorarioAbertura(LocalTime.parse(abertura));
        h.setHorarioFechamento(LocalTime.parse(fechamento));
        h.setFechado(false);
        return h;
    }

    public static HorarioFuncionamento horarioEspecial(LocalDate data, String abertura, String fechamento, boolean fechado) {
        HorarioFuncionamento h = new HorarioFuncionamento();
        h.setDataEspecial(data);
        h.setFechado(fechado);
        if (!fechado) {
            h.setHorarioAbertura(LocalTime.parse(abertura));
            h.setHorarioFechamento(LocalTime.parse(fechamento));
        }
        h.setObservacao("Feriado/Especial");
        return h;
    }

    public static ReservaSinuca reservaValida(int mesa) {
        return reservaValida(mesa, 1);
    }

    public static ReservaSinuca reservaValida(int mesa, int duracao) {
        LocalTime inicio = LocalTime.of(14, 0);
        return reservaValida(mesa, inicio, duracao);
    }

    public static ReservaSinuca reservaValida(int mesa, LocalTime horaInicio, int duracao) {
        LocalTime horaFim = duracao == 1 ? 
            horaInicio.plusMinutes(30) : 
            horaInicio.plusHours(1);
        
        return new ReservaSinuca(
            mesa,
            "João Silva",
            LocalDate.now().plusDays(1),
            horaInicio,
            horaFim
        );
    }
}
