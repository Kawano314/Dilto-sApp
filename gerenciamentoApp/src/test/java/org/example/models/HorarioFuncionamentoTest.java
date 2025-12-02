package org.example.models;

import org.example.testutils.TestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do Modelo HorarioFuncionamento")
class HorarioFuncionamentoTest {

    @Test
    @DisplayName("Deve criar horário padrão válido")
    void testCriarHorarioPadrao() {
        HorarioFuncionamento h = TestDataFactory.horarioPadrao(DayOfWeek.MONDAY, "08:00", "18:00");
        assertNotNull(h);
        assertEquals(DayOfWeek.MONDAY, h.getDiaSemana());
        assertFalse(h.isFechado());
    }

    @Test
    @DisplayName("Deve criar horário especial válido")
    void testCriarHorarioEspecial() {
        LocalDate data = LocalDate.now();
        HorarioFuncionamento h = TestDataFactory.horarioEspecial(data, "10:00", "14:00", false);
        assertNotNull(h);
        assertEquals(data, h.getDataEspecial());
        assertTrue(h.isHorarioEspecial());
    }

    @Test
    @DisplayName("Deve detectar horário especial")
    void testIsHorarioEspecial() {
        HorarioFuncionamento h = new HorarioFuncionamento();
        h.setDataEspecial(LocalDate.now());
        assertTrue(h.isHorarioEspecial());
    }

    @Test
    @DisplayName("Deve formatar dia da semana em português")
    void testGetDiaSemanaFormatado() {
        HorarioFuncionamento h = TestDataFactory.horarioPadrao(DayOfWeek.MONDAY, "08:00", "18:00");
        assertEquals("Segunda-feira", h.getDiaSemanaFormatado());
    }

    @Test
    @DisplayName("Deve retornar 'Fechado' quando fechado")
    void testGetHorarioFormatadoFechado() {
        HorarioFuncionamento h = new HorarioFuncionamento();
        h.setFechado(true);
        assertEquals("Fechado", h.getHorarioFormatado());
    }

    @Test
    @DisplayName("Deve formatar horário corretamente")
    void testGetHorarioFormatado() {
        HorarioFuncionamento h = TestDataFactory.horarioPadrao(DayOfWeek.MONDAY, "08:00", "18:00");
        assertTrue(h.getHorarioFormatado().contains("08:00"));
        assertTrue(h.getHorarioFormatado().contains("18:00"));
    }

    @Test
    @DisplayName("Deve validar se estabelecimento está aberto")
    void testEstaAberto() {
        HorarioFuncionamento h = TestDataFactory.horarioPadrao(DayOfWeek.MONDAY, "08:00", "18:00");
        LocalTime horarioAberto = LocalTime.of(12, 0);
        assertTrue(h.estaAberto(horarioAberto));
    }

    @Test
    @DisplayName("Deve validar se estabelecimento está fechado - antes da abertura")
    void testEstaFechadoAntes() {
        HorarioFuncionamento h = TestDataFactory.horarioPadrao(DayOfWeek.MONDAY, "08:00", "18:00");
        LocalTime horarioAntes = LocalTime.of(7, 0);
        assertFalse(h.estaAberto(horarioAntes));
    }

    @Test
    @DisplayName("Deve validar se estabelecimento está fechado - após fechamento")
    void testEstaFechadoDepois() {
        HorarioFuncionamento h = TestDataFactory.horarioPadrao(DayOfWeek.MONDAY, "08:00", "18:00");
        LocalTime horarioDepois = LocalTime.of(19, 0);
        assertFalse(h.estaAberto(horarioDepois));
    }

    @Test
    @DisplayName("Deve retornar false se horário fechado")
    void testEstaAbertoQuandoFechado() {
        HorarioFuncionamento h = new HorarioFuncionamento();
        h.setFechado(true);
        assertFalse(h.estaAberto(LocalTime.of(12, 0)));
    }

    @Test
    @DisplayName("Deve retornar false se horários não estão definidos")
    void testEstaAbertoSemHorario() {
        HorarioFuncionamento h = new HorarioFuncionamento();
        h.setFechado(false);
        assertFalse(h.estaAberto(LocalTime.of(12, 0)));
    }

    @Test
    @DisplayName("Deve formatar toString de horário padrão")
    void testToStringPadrao() {
        HorarioFuncionamento h = TestDataFactory.horarioPadrao(DayOfWeek.MONDAY, "08:00", "18:00");
        String str = h.toString();
        assertTrue(str.contains("Segunda-feira"));
    }

    @Test
    @DisplayName("Deve formatar toString de horário especial")
    void testToStringEspecial() {
        HorarioFuncionamento h = TestDataFactory.horarioEspecial(LocalDate.now(), "10:00", "14:00", false);
        String str = h.toString();
        assertTrue(str.contains(LocalDate.now().toString()));
    }
}
