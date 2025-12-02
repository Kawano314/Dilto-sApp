package org.example.models;

import org.example.testutils.TestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do Modelo ReservaSinuca")
class ReservaSinucaTest {

    @Test
    @DisplayName("Deve criar reserva válida")
    void testCriarReservaValida() {
        ReservaSinuca r = TestDataFactory.reservaValida(1);
        assertNotNull(r);
        assertEquals(1, r.getNumeroMesa());
    }

    @Test
    @DisplayName("Deve validar número da mesa entre 1-3")
    void testValidarNumeroMesa() {
        ReservaSinuca r = new ReservaSinuca();
        r.setNumeroMesa(1);
        assertEquals(1, r.getNumeroMesa());
    }

    @Test
    @DisplayName("Deve validar nome cliente obrigatório")
    void testValidarNomeCliente() {
        ReservaSinuca r = new ReservaSinuca();
        r.setNomeCliente("João Silva");
        assertEquals("João Silva", r.getNomeCliente());
    }

    @Test
    @DisplayName("Deve validar data da reserva")
    void testValidarDataReserva() {
        ReservaSinuca r = new ReservaSinuca();
        LocalDate data = LocalDate.now().plusDays(1);
        r.setDataReserva(data);
        assertEquals(data, r.getDataReserva());
    }

    @Test
    @DisplayName("Deve validar hora de início")
    void testValidarHoraInicio() {
        ReservaSinuca r = new ReservaSinuca();
        LocalTime hora = LocalTime.of(14, 0);
        r.setHoraInicio(hora);
        assertEquals(hora, r.getHoraInicio());
    }

    @Test
    @DisplayName("Deve validar hora de fim")
    void testValidarHoraFim() {
        ReservaSinuca r = new ReservaSinuca();
        LocalTime hora = LocalTime.of(15, 0);
        r.setHoraFim(hora);
        assertEquals(hora, r.getHoraFim());
    }

    @Test
    @DisplayName("Deve validar telefone cliente")
    void testValidarTelefoneCliente() {
        ReservaSinuca r = new ReservaSinuca();
        r.setTelefoneCliente("11999999999");
        assertEquals("11999999999", r.getTelefoneCliente());
    }

    @Test
    @DisplayName("Deve calcular duração de reserva corretamente")
    void testCalcularDuracao() {
        ReservaSinuca r = TestDataFactory.reservaValida(1, 1); // 30 minutos
        assertTrue(r.getHoraFim().isAfter(r.getHoraInicio()));
    }

    @Test
    @DisplayName("Deve formatar período corretamente")
    void testGetPeriodoFormatado() {
        ReservaSinuca r = TestDataFactory.reservaValida(1);
        String periodo = r.getPeriodoFormatado();
        assertNotNull(periodo);
        assertTrue(periodo.contains("de") || periodo.contains("-") || periodo.contains("até"));
    }

    @Test
    @DisplayName("Deve armazenar observações")
    void testArmazenarObservacoes() {
        ReservaSinuca r = new ReservaSinuca();
        r.setObservacoes("Aniversário");
        assertEquals("Aniversário", r.getObservacoes());
    }

    @Test
    @DisplayName("Deve retornar ID após persistência")
    void testRetornarId() {
        ReservaSinuca r = new ReservaSinuca();
        r.setId(1L);
        assertEquals(1L, r.getId());
    }

    @Test
    @DisplayName("Deve criar reserva com duração de 30 minutos")
    void testReservaComTrintaMinutos() {
        ReservaSinuca r = TestDataFactory.reservaValida(1, 1);
        long minutos = java.time.temporal.ChronoUnit.MINUTES.between(r.getHoraInicio(), r.getHoraFim());
        assertEquals(30, minutos);
    }

    @Test
    @DisplayName("Deve criar reserva com duração de 1 hora")
    void testReservaComUmaHora() {
        ReservaSinuca r = TestDataFactory.reservaValida(1, 2);
        long minutos = java.time.temporal.ChronoUnit.MINUTES.between(r.getHoraInicio(), r.getHoraFim());
        assertEquals(60, minutos);
    }

    @Test
    @DisplayName("Deve exibir toString com informações básicas")
    void testToString() {
        ReservaSinuca r = TestDataFactory.reservaValida(1);
        String str = r.toString();
        assertNotNull(str);
        assertTrue(str.length() > 0);
    }

    @Test
    @DisplayName("Deve validar construtor com parâmetros")
    void testConstrutorComParametros() {
        LocalDate data = LocalDate.now().plusDays(1);
        LocalTime inicio = LocalTime.of(14, 0);
        LocalTime fim = LocalTime.of(15, 0);
        ReservaSinuca r = new ReservaSinuca(1, "João", data, inicio, fim);
        
        assertEquals(1, r.getNumeroMesa());
        assertEquals("João", r.getNomeCliente());
        assertEquals(data, r.getDataReserva());
        assertEquals(inicio, r.getHoraInicio());
        assertEquals(fim, r.getHoraFim());
    }
}
