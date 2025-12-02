package org.example.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Parametrizados de HorarioFuncionamento")
class HorarioFuncionamentoParametrizadoTest {

    @ParameterizedTest
    @CsvSource({
        "08:00, 18:00, 09:00, true",
        "08:00, 18:00, 07:00, false",
        "08:00, 18:00, 18:00, false",
        "08:00, 18:00, 17:59, true",
        "10:00, 16:00, 12:00, true",
        "10:00, 16:00, 09:59, false",
        "10:00, 16:00, 16:00, false",
        "14:00, 22:00, 20:00, true",
        "14:00, 22:00, 13:59, false",
        "14:00, 22:00, 22:00, false",
        "06:00, 23:59, 12:30, true",
        "06:00, 23:59, 05:59, false",
        "06:00, 23:59, 23:59, false",
        "12:00, 14:00, 13:00, true"
    })
    @DisplayName("Deve validar se está aberto em diferentes horários")
    void testEstaAbertoComDiferentesHorarios(String abertura, String fechamento, String horarioTestado, boolean esperado) {
        HorarioFuncionamento h = new HorarioFuncionamento();
        h.setDiaSemana(DayOfWeek.MONDAY);
        h.setHorarioAbertura(LocalTime.parse(abertura));
        h.setHorarioFechamento(LocalTime.parse(fechamento));
        h.setFechado(false);

        LocalTime horario = LocalTime.parse(horarioTestado);
        assertEquals(esperado, h.estaAberto(horario));
    }

    @ParameterizedTest
    @CsvSource({
        "MONDAY, Segunda-feira",
        "TUESDAY, Terça-feira",
        "WEDNESDAY, Quarta-feira",
        "THURSDAY, Quinta-feira",
        "FRIDAY, Sexta-feira",
        "SATURDAY, Sábado",
        "SUNDAY, Domingo"
    })
    @DisplayName("Deve formatar dias da semana em português")
    void testFormatacaoDiasSemana(DayOfWeek dia, String esperado) {
        HorarioFuncionamento h = new HorarioFuncionamento();
        h.setDiaSemana(dia);
        assertEquals(esperado, h.getDiaSemanaFormatado());
    }
}
